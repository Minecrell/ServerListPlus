/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core;

import static net.minecrell.serverlistplus.core.logging.Logger.Level.DEBUG;
import static net.minecrell.serverlistplus.core.logging.Logger.Level.INFO;
import static net.minecrell.serverlistplus.core.logging.Logger.Level.REPORT;
import static net.minecrell.serverlistplus.core.logging.Logger.Level.WARN;

import lombok.Getter;
import net.minecrell.serverlistplus.core.config.UnknownConf;
import net.minecrell.serverlistplus.core.config.io.IOHelper;
import net.minecrell.serverlistplus.core.config.storage.InstanceStorage;
import net.minecrell.serverlistplus.core.config.storage.InstanceStorages;
import net.minecrell.serverlistplus.core.config.yaml.ServerListPlusYAML;
import net.minecrell.serverlistplus.core.config.yaml.YAMLWriter;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

public class ConfigurationManager extends AbstractManager {
    public static final String CONFIG_FILENAME = "ServerListPlus.yml";
    protected static final String BACKUP_FILENAME = "ServerListPlus.bak.yml";

    protected final YAMLWriter yaml;

    protected final @Getter InstanceStorage<Object> defaults = InstanceStorages.create();
    protected @Getter InstanceStorage<Object> storage = InstanceStorages.createOrdered().withDefaults(defaults);

    protected final @Getter InstanceStorage<Object> examples = InstanceStorages.createOrdered();

    public ConfigurationManager(ServerListPlusCore core) {
        super(core);
        this.yaml = ServerListPlusYAML.createWriter(core);
    }

    protected Path getPluginFolder() {
        return core.getPlugin().getPluginFolder();
    }

    public Path getConfigPath() {
        return this.getPluginFolder().resolve(CONFIG_FILENAME);
    }

    public YAMLWriter getYAML() {
        return yaml;
    }

    public void reload() throws ServerListPlusException {
        getLogger().log(INFO, "Reloading configuration...");

        Path configPath = getConfigPath();
        getLogger().log(DEBUG, "Configuration location: " + configPath);

        try {
            final boolean confExists = Files.exists(configPath);

            InstanceStorage<Object> newStorage;
            if (confExists) {
                newStorage = InstanceStorages.createOrdered().withDefaults(defaults);

                // Open the configuration file
                try (BufferedReader reader = IOHelper.newBufferedReader(configPath)) {
                    Iterator<Object> itr = yaml.snakeYAML().getYaml().loadAll(reader).iterator();
                    while (itr.hasNext()) {
                        try {
                            // Read one configuration from the file
                            Object obj = itr.next();
                            if (obj.getClass() == UnknownConf.class) continue;

                            // Add it to the storage
                            newStorage.set(obj);
                            getLogger().log(INFO, "Loaded configuration: " + obj.getClass().getSimpleName());
                        } catch (YAMLException e) {
                            getLogger().log(WARN, e, "Unable to parse a part of the configuration. Make " +
                                    "sure the YAML syntax is valid!");
                        }
                    }
                }


                getLogger().log(REPORT, newStorage.size() + " configurations loaded.");
            } else {
                newStorage = InstanceStorages.createOrdered().withDefaults(examples.withDefaults(defaults));
            }

            this.storage = newStorage;

            if (examples.size() > newStorage.size()) {
                if (confExists)
                    getLogger().log(WARN, "Could not load all configurations from the configuration file. Please" +
                            " make sure the syntax is correct. Type '/slp save' if you want to add the missing " +
                            "parts.");
            }

            if (!confExists) try {
                save(examples); // Save it if it doesn't exist
            } catch (ServerListPlusException ignored) {}

            core.getPlugin().configChanged(core, storage); // Call plugin handlers
            getLogger().log(DEBUG, "Configuration successfully reloaded!");
        } catch (YAMLException e) {
            throw getLogger().process(e, "Unable to parse the configuration. Make sure the YAML syntax is " +
                    "correct!");
        } catch (MalformedInputException e) {
            throw getLogger().process(e, "Your configuration contains invalid special characters. Please " +
                    "save your configuration using {} instead.", IOHelper.CHARSET.displayName());
        } catch (IOException e) {
            throw getLogger().process(e, "Unable to access the configuration file. Make sure that it is " +
                    "accessible by the server.");
        } catch (Exception e) {
            throw getLogger().process(e, "An internal error occurred while reloading the configuration!");
        }
    }

    public void save() throws ServerListPlusException {
        InstanceStorage<Object> tmp = InstanceStorages.createOrdered();
        tmp.setAll(examples);
        tmp.setAll(storage);
        this.storage = tmp;
        save(storage);
    }

    private void save(InstanceStorage<Object> storage) throws ServerListPlusException {
        getLogger().log(INFO, "Saving configuration...");

        Path configPath = getConfigPath();
        getLogger().log(DEBUG, "Configuration location: " + configPath);

        try {
            if (Files.exists(configPath)) {
                // Create a backup if the configuration already exists!
                Path backupPath = getPluginFolder().resolve(BACKUP_FILENAME);
                getLogger().log(DEBUG, "Saving configuration backup to: " + backupPath);

                Files.copy(configPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            } else
                // Create plugin folder if it doesn't exist already
                Files.createDirectories(configPath.toAbsolutePath().getParent());

            try (BufferedWriter writer = IOHelper.newBufferedWriter(configPath)) {
                yaml.writeHeader(writer);
                yaml.newLine(writer);

                for (Object config : storage)
                    // Write the configuration to the file
                    yaml.writeDocumented(writer, config);
            }

            getLogger().log(DEBUG, "Configuration successfully saved!");
        } catch (YAMLException e) {
            throw getLogger().process(e, "An error occurred while generating the YAML configuration!");
        } catch (IOException e) {
            throw getLogger().process(e, "Unable to access the configuration file. Make sure that it is " +
                    "accessible by the server.");
        } catch (Exception e) {
            throw getLogger().process(e, "An internal error occurred while saving the configuration!");
        }
    }
}
