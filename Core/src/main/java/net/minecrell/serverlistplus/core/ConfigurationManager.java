/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core;

import lombok.Getter;

import net.minecrell.serverlistplus.core.config.UnknownConf;
import net.minecrell.serverlistplus.core.config.io.IOUtil;
import net.minecrell.serverlistplus.core.config.yaml.ServerListPlusYAML;
import net.minecrell.serverlistplus.core.config.yaml.YAMLWriter;
import net.minecrell.serverlistplus.core.util.ClassToInstanceStorage;
import net.minecrell.serverlistplus.core.util.CoreManager;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.InstanceStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.yaml.snakeyaml.error.YAMLException;

import static net.minecrell.serverlistplus.core.logging.Logger.*;

public class ConfigurationManager extends CoreManager {
    public static final String CONFIG_FILENAME = "ServerListPlus.yml";
    protected static final String BACKUP_FILENAME = "ServerListPlus.bak.yml";

    protected final YAMLWriter yaml;

    protected @Getter InstanceStorage<Object> storage = ClassToInstanceStorage.createLinked();
    protected final @Getter InstanceStorage<Object> defaults = ClassToInstanceStorage.createLinked();

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
            // Create new storage, will replace the other one when finished loading
            ClassToInstanceStorage<Object> newStorage = ClassToInstanceStorage.createLinked();
            final boolean confExists = Files.exists(configPath);

            if (confExists) {
                try (BufferedReader reader = IOUtil.newBufferedReader(configPath)) {
                    Iterator<Object> itr = yaml.snakeYAML().getYaml().loadAll(reader).iterator();
                    while (itr.hasNext()) {
                        try {
                            // Read one configuration from the file
                            Object obj = itr.next();
                            if (obj.getClass() == UnknownConf.class) continue;

                            // Add it to the storage
                            newStorage.setUnsafe(obj.getClass(), obj);
                            getLogger().log(INFO, "Loaded configuration: " + obj.getClass().getSimpleName());
                        } catch (YAMLException e) {
                            getLogger().log(WARN, e, "Unable to parse a part of the configuration. Make " +
                                    "sure the YAML syntax is valid!");
                        }
                    }
                }
            }

            getLogger().log(REPORT, newStorage.count() + " configurations loaded.");

            // Add missing configurations from default values
            int generated = Helper.mergeMaps(newStorage.getMap(), defaults.getMap());
            this.storage = newStorage;

            if (generated > 0) {
                getLogger().log(DEBUG, "Using {} default configurations.", generated);
                if (confExists)
                    getLogger().log(WARN, generated + " configurations could not be found in the " +
                            "configuration file. Your configuration might be outdated, " +
                            "or some of them contain invalid YAML syntax. If you want to regenerate the missing " +
                            "configuration parts type '/ServerListPlus save'. Please note that this will delete " +
                            "all invalid configuration parts as well as any custom comments. A backup will be " +
                            "created automatically.");
            }

            if (!confExists) try {
                save(); // Save it if it doesn't exist
            } catch (ServerListPlusException ignored) {}

            core.getPlugin().configChanged(storage); // Call plugin handlers
            getLogger().log(DEBUG, "Configuration successfully reloaded!");
        } catch (YAMLException e) {
            throw getLogger().process(e, "Unable to parse the configuration. Make sure the YAML syntax is " +
                    "correct!");
        } catch (MalformedInputException e) {
            throw getLogger().process(e, "Your configuration contains invalid special characters. Please " +
                    "save your configuration using {} instead.", IOUtil.CHARSET.displayName());
        } catch (IOException e) {
            throw getLogger().process(e, "Unable to access the configuration file. Make sure that it is " +
                    "accessible by the server.");
        } catch (Exception e) {
            throw getLogger().process(e, "An internal error occurred while reloading the configuration!");
        }
    }

    public void save() throws ServerListPlusException {
        getLogger().log(INFO, "Saving configuration...");

        Path configPath = getConfigPath();
        getLogger().log(DEBUG, "Configuration location: " + configPath);

        try {
            if (Files.exists(configPath)) {
                // Create a backup if the configuration already exists!
                Path backupPath = getPluginFolder().resolve(BACKUP_FILENAME);
                getLogger().log(DEBUG, "Saving configuration backup to: " + backupPath);

                Files.copy(configPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // Create plugin folder if it doesn't exist already
                Files.createDirectories(configPath.getParent());
            }

            try (BufferedWriter writer = IOUtil.newBufferedWriter(configPath)) {
                yaml.writeHeader(writer);
                yaml.newLine(writer);
                yaml.newLine(writer);

                for (Object config : storage.get())
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
