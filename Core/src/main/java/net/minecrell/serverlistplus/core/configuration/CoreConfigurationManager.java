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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;

import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.api.configuration.Configuration;
import net.minecrell.serverlistplus.api.configuration.PluginConfiguration;
import net.minecrell.serverlistplus.api.configuration.ServerListConfiguration;
import net.minecrell.serverlistplus.core.configuration.util.IOUtil;
import net.minecrell.serverlistplus.core.util.CoreServerListPlusManager;
import net.minecrell.serverlistplus.core.util.Helper;

import com.google.common.collect.ClassToInstanceMap;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

public class CoreConfigurationManager extends CoreServerListPlusManager {
    private static final ClassToInstanceMap<Configuration> DefaultConfigurations = Helper.createLinkedClassMap();

    static {
        DefaultConfigurations.putInstance(ServerListConfiguration.class, new ServerListConfiguration());
        DefaultConfigurations.putInstance(PluginConfiguration.class, new PluginConfiguration());
        DefaultConfigurations.putInstance(CoreConfiguration.class, new CoreConfiguration());
    }

    public static final String CONFIG_FILENAME = "ServerListPlus.yml";

    public static final String HEADER_FILENAME = "HEADER";
    private final @Getter String[] header;

    private final Yaml yaml = new Yaml(); // TODO: Set yaml settings
    private ClassToInstanceMap<Configuration> storage = DefaultConfigurations;

    public CoreConfigurationManager(ServerListPlusCore core) {
        super(core);
        this.header = loadHeader(core); // Try loading the configuration header
    }

    public Path getConfigPath() {
        return this.getCore().getPlugin().getDataFolder().toPath().resolve(CONFIG_FILENAME).toAbsolutePath();
    }

    public Configuration[] reload() throws ServerListPlusException {
        this.getCore().getLogger().info("Reloading configuration...");
        Path configPath = this.getConfigPath();

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
            } else {
                Files.createDirectories(configPath.getParent());
                created = true;
            }

            this.getLogger().infoF("Loaded %d configurations.", newStorage.size());
            Configuration[] loaded = newStorage.values().toArray(new Configuration[newStorage.size()]);

            int generated = Helper.mergeMaps(newStorage, DefaultConfigurations);
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

            return loaded;
        } catch (YAMLException e) {
            throw this.getLogger().process(e, "Unable to parse configuration file. Make sure it contains only valid " +
                    "YAML syntax and check if you haven't got an error somewhere.");
        } catch (IOException e) {
            throw this.getLogger().processF(e, "Unable to access configuration file. " +
                    "Make sure that it is saved using the correct charset (%s) and accessible by the server.\n%s",
                    IOUtil.CHARSET.displayName(), configPath);
        } catch (Exception e) {
            throw this.getLogger().process(e, "An internal error occurred while reloading the configuration from: " +
                    configPath);
        }
    }

    public void save() throws ServerListPlusException {
        // TODO: Save configuration to file
    }

    public static String[] loadHeader(ServerListPlusCore core) throws ServerListPlusException {
        try (InputStream in = core.getClass().getClassLoader().getResourceAsStream(HEADER_FILENAME)) {
            return (in != null) ? Helper.nullWhenEmpty(IOUtil.readLineArray(in)) : null;
        } catch (Exception e) {
            core.getLogger().log(Level.WARNING, e, "Unable to read file header!"); return null;
        }
    }
}
