/*
 *       __                           __ _     _     ___ _
 *      / _\ ___ _ ____   _____ _ __ / /(_)___| |_  / _ \ |_   _ ___
 *      \ \ / _ \ '__\ \ / / _ \ '__/ / | / __| __|/ /_)/ | | | / __|
 *      _\ \  __/ |   \ V /  __/ | / /__| \__ \ |_/ ___/| | |_| \__ \
 *      \__/\___|_|    \_/ \___|_| \____/_|___/\__\/    |_|\__,_|___/
 *                       Customize your server ping!
 *
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.configuration;

import lombok.Getter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.api.configuration.Configuration;
import net.minecrell.serverlistplus.api.configuration.ConfigurationManager;
import net.minecrell.serverlistplus.core.configuration.util.IOUtil;
import net.minecrell.serverlistplus.core.util.CoreServerListPlusManager;
import net.minecrell.serverlistplus.core.util.Helper;

import com.google.common.collect.ClassToInstanceMap;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class CoreConfigurationManager extends CoreServerListPlusManager implements ConfigurationManager {
    public static String COMMENT_PREFIX = "# ";

    public static final String CONFIG_FILENAME = "ServerListPlus.yml";
    private static final String BACKUP_FILENAME = "ServerListPlus.bak.yml";

    public static final String HEADER_FILENAME = "HEADER";
    private final @Getter String[] header;

    private final Map<String, Class<? extends Configuration>> aliases = new HashMap<>();
    private final ClassToInstanceMap<Configuration> defaultConfigs = Helper.createLinkedClassMap();
    private ClassToInstanceMap<Configuration> storage = Helper.createLinkedClassMap();

    private final Yaml yaml;
    private final Representer yamlRepresenter;
    private final Constructor yamlConstructor;

    public CoreConfigurationManager(ServerListPlusCore core) {
        super(core);
        this.header = loadHeader(core); // Try loading the configuration header

        // YAML settings
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(
                this.yamlConstructor = new CustomClassLoaderConstructor(core.getClass().getClassLoader()),
                this.yamlRepresenter = new Representer(),
                dumperOptions);
    }

    @Override
    public Configuration[] getDefault() {
        return Helper.toConfigArray(defaultConfigs.values());
    }

    @Override
    public <T extends Configuration> T getDefault(Class<T> configClass) {
        return defaultConfigs.getInstance(configClass);
    }

    @Override
    public <T extends Configuration> void registerDefault(Class<T> configClass, T defaultConfig) {
        defaultConfigs.put(configClass, defaultConfig);
        String name = Configuration.getUniqueName(configClass);
        if (name != null && !aliases.containsKey(name)) {
            aliases.put(name, configClass);
            Tag tag = new Tag("!" + name);
            yamlRepresenter.addClassTag(configClass, tag);
            yamlConstructor.addTypeDescription(new TypeDescription(configClass, tag));
        }
        if (!this.has(configClass)) this.set(configClass, defaultConfig);
    }

    @Override
    public boolean unregisterDefault(Class<? extends Configuration> configClass) {
        // TODO: Unregister alias
        return (defaultConfigs.remove(configClass) != null);
    }

    @Override
    public Configuration[] reset() {
        Configuration[] loaded = this.get();
        this.storage = Helper.copyLinkedClassMap(defaultConfigs);
        return loaded;
    }

    @Override
    public Configuration[] get() {
        return Helper.toConfigArray(storage.values());
    }

    @Override
    public <T extends Configuration> T get(Class<T> configClass) {
        return storage.getInstance(configClass);
    }

    @Override
    public boolean has(Class<? extends Configuration> configClass) {
        return storage.containsKey(configClass);
    }

    @Override
    public <T extends Configuration> T set(Class<T> configClass, T config) {
        return storage.putInstance(configClass, config);
    }

    private Path getDataFolder() {
        return this.getCore().getPlugin().getDataFolder().toPath();
    }

    public Path getConfigPath() {
        return this.getDataFolder().resolve(CONFIG_FILENAME).toAbsolutePath();
    }

    @Override
    public Configuration[] reload() throws ServerListPlusException {
        Path configPath = this.getConfigPath();
        this.getLogger().info("Reloading configuration from: " + configPath);

        try {
            boolean created = false;
            ClassToInstanceMap<Configuration> newStorage = Helper.createLinkedClassMap();
            if (Files.exists(configPath)) {
                // TODO: Load configuration from file.
                try (InputStreamReader reader = IOUtil.newReader(configPath)) {
                    int counter = 0;
                    Iterator<Object> itr = yaml.loadAll(reader).iterator();

                    while (itr.hasNext()) {
                        counter++;
                        try {
                            Object obj = itr.next();
                            if (obj instanceof Configuration) {
                                Configuration config = (Configuration) obj;
                                newStorage.put(config.getClass(), config);
                                this.getLogger().info("Loaded configuration: " + config.getClass().getSimpleName());
                            } else
                                this.getLogger().warningF("Unknown configuration type, skipping %s configuration: %s",
                                        Helper.ordinalNumber(counter), obj.getClass());
                        } catch (YAMLException e) {
                            this.getLogger().logF(Level.WARNING, e, "Unable to parse the %s configuration. Make sure" +
                                    " the YAML syntax is correct!", Helper.ordinalNumber(counter));
                        }
                    }
                }
            } else created = true;

            this.getLogger().infoF("Loaded %d configurations.", newStorage.size());
            Configuration[] loaded = Helper.toConfigArray(newStorage.values());

            int generated = Helper.mergeMaps(newStorage, defaultConfigs);
            this.storage = newStorage;

            if (generated > 0) {
                this.getLogger().infoF("Generated %d configurations.", generated);
                if (!created)
                    this.getLogger().warning((newStorage.size() - generated) + " configurations could not be read " +
                            "from the configuration file. Your configuration might be outdated," +
                            "or a part of is not valid YAML syntax. To prevent data loss, the configuration is not " +
                            "saved automatically to add the new generated configurations. Type '/ServerListPlus save'" +
                            " to save the configuration and add the missing ones. All invalid configuration parts " +
                            "will be deleted, as well as any custom made comments. Make a backup before saving!");
            }
            if (created)
                try { this.save(); } catch (ServerListPlusException ignored) {}

            this.getLogger().info("Configuration reload completed successfully.");
            return loaded;
        } catch (YAMLException e) {
            throw this.getLogger().process(e, "Unable to parse configuration file. Make sure it contains only valid " +
                    "YAML syntax and check if you haven't got an error somewhere.");
        } catch (IOException e) {
            throw this.getLogger().processF(e, "Unable to access configuration file. " +
                    "Make sure that it is saved using the correct charset (%s) and accessible by the server.",
                    IOUtil.CHARSET.displayName());
        } catch (Exception e) {
            throw this.getLogger().process(e, "An internal error occurred while reloading the configuration file!");
        }
    }

    @Override
    public void save() throws ServerListPlusException {
        Path configPath = this.getConfigPath();
        this.getLogger().info("Saving configuration to: " + configPath);

        try {
            if (Files.exists(configPath)) {
                Path backupPath = this.getDataFolder().resolve(BACKUP_FILENAME).toAbsolutePath();
                this.getLogger().info("Backing up configuration file to: " + backupPath);

                Files.copy(configPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // Create plugin folder if it doesn't already
                Files.createDirectories(configPath.getParent());
            }

            try (BufferedWriter writer = IOUtil.newBufferedWriter(configPath)) {
                IOUtil.writePrefixed(writer, COMMENT_PREFIX, header);

                // TODO: Split into multiple logical methods
                for (Configuration config : storage.values()) {
                    // Two empty lines before the next configuration
                    writer.newLine(); writer.newLine();

                    IOUtil.writePrefixed(writer, COMMENT_PREFIX, Configuration.getDescription(config));
                    writer.write("--- ");
                    yaml.dump(config, writer);
                }
            }

            this.getLogger().info("Configuration saving completed successfully.");
        } catch (YAMLException e) {
            throw this.getLogger().process(e, "An error occurred while generating the YAML configuration!");
        } catch (IOException e) {
            throw this.getLogger().process(e, "Unable to access configuration file. Make sure that the server has " +
                    "permission to write to the file.");
        } catch (Exception e) {
            throw this.getLogger().process(e, "An internal error occurred while saving the configuration file!");
        }
    }

    public static String[] loadHeader(ServerListPlusCore core) throws ServerListPlusException {
        try (InputStream in = core.getResource(HEADER_FILENAME)) {
            return (in != null) ? Helper.nullWhenEmpty(IOUtil.readLineArray(in)) : null;
        } catch (Exception e) {
            core.getLogger().log(Level.WARNING, e, "Unable to read file header!"); return null;
        }
    }
}
