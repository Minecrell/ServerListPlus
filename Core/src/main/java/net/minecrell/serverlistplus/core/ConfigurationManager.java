/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your complete server status ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
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

import net.minecrell.serverlistplus.core.config.ServerStatusConf;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.yaml.snakeyaml.error.YAMLException;

public class ConfigurationManager extends CoreManager {
    public static final String CONFIG_FILENAME = "ServerListPlus.yml";
    protected static final String BACKUP_FILENAME = "ServerListPlus.bak.yml";

    protected final YAMLWriter yaml;

    protected InstanceStorage<Object> storage = ClassToInstanceStorage.createLinked();
    protected final InstanceStorage<Object> defaults = ClassToInstanceStorage.createLinked();

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

    public InstanceStorage<Object> getStorage() {
        return storage;
    }

    public InstanceStorage<Object> getDefaults() {
        return defaults;
    }

    public void reload() throws ServerListPlusException {
        Path configPath = this.getConfigPath();
        this.getLogger().info("Reloading configuration from: " + configPath);

        try {
            ClassToInstanceStorage<Object> newStorage = ClassToInstanceStorage.createLinked();
            final boolean confExists = Files.exists(configPath);

            if (confExists) {
                try (BufferedReader reader = IOUtil.newBufferedReader(configPath)) {
                    Iterator<Object> itr = yaml.snakeYAML().getYaml().loadAll(reader).iterator();
                    while (itr.hasNext()) {
                        try {
                            Object obj = itr.next();
                            newStorage.setUnsafe(obj.getClass(), obj);
                            this.getLogger().info("Loaded configuration: " + obj.getClass().getSimpleName());
                        } catch (YAMLException e) {
                            this.getLogger().warning(e, "Unable to parse configuration part from the " +
                                    "configuration file. Make sure the YAML syntax is valid!");
                        }
                    }
                }
            }

            this.getLogger().infoF("Loaded %d configurations.", newStorage.count());

            int generated = Helper.mergeMaps(newStorage.getMap(), defaults.getMap());
            this.storage = newStorage;

            if (generated > 0) {
                this.getLogger().infoF("Generated %d configurations.", generated);
                if (confExists)
                    this.getLogger().warning(generated + " configurations could not be found in the " +
                            "configuration file. Your configuration might be outdated, " +
                            "or it contains invalid YAML syntax. If you want to regenerate the missing " +
                            "configuration parts type '/ServerListPlus save'. Please not that this will delete " +
                            "all invalid configuration parts as well as any custom comments. A backup will be " +
                            "created automatically.");
            }

            if (!confExists) try {
                this.save();
            } catch (ServerListPlusException ignored) {
            }

            core.getPlugin().configChanged(storage);
            this.getLogger().info("Configuration reload complete.");
        } catch (YAMLException e) {
            throw this.getLogger().process(e, "Unable to parse the configuration. Make sure it is valid YAML " +
                    "syntax.");
        } catch (IOException e) {
            throw this.getLogger().processF(e, "Unable to access the configuration file. Make sure that it is " +
                    "saved using the correct charset (%s) and accessible by the server.",
                    IOUtil.CHARSET.displayName());
        } catch (Exception e) {
            throw this.getLogger().process(e, "An internal error occurred while reloading the configuration!");
        }
    }

    public void save() throws ServerListPlusException {
        Path configPath = this.getConfigPath();
        this.getLogger().info("Saving configuration to: " + configPath);

        try {
            if (Files.exists(configPath)) {
                Path backupPath = this.getPluginFolder().resolve(BACKUP_FILENAME);
                this.getLogger().info("Saving configuration backup to: " + backupPath);

                Files.copy(configPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // Create plugin folder if it doesn't exist already
                Files.createDirectories(configPath.getParent());
            }

            try (BufferedWriter writer = IOUtil.newBufferedWriter(configPath)) {
                yaml.writeHeader(writer);
                yaml.newLine(writer);

                for (Object config : storage.get()) {
                    boolean stupidFix = yaml.snakeYAML().isOutdated() && (config instanceof ServerStatusConf);
                    if (stupidFix) yaml.applyStupidFix();
                    yaml.writeDocumented(writer, config);
                    if (stupidFix) yaml.revertStupidFix();
                }
            }

            this.getLogger().info("Configuration saving complete.");
        } catch (YAMLException e) {
            throw this.getLogger().process(e, "An error occurred while generating the YAML configuration!");
        } catch (IOException e) {
            throw this.getLogger().processF(e, "Unable to access the configuration file. Make sure that it is " +
                    "accessible by the server.", IOUtil.CHARSET.displayName());
        } catch (Exception e) {
            throw this.getLogger().process(e, "An internal error occurred while saving the configuration!");
        }
    }
}
