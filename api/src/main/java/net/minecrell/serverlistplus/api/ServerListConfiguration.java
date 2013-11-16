/*
 * ServerListPlus - Customize your server's ping information!
 * Copyright (C) 2013, Minecrell
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

package net.minecrell.serverlistplus.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

public class ServerListConfiguration {
    public final static String CONFIG_FILENAME = "serverlistplus.cfg";
    public final static String LINES_FILENAME = "lines.txt";

    public final static Charset FILE_CHARSET = StandardCharsets.UTF_8;

    public static enum ConfigurationEntry {
        TRACK_PLAYERS ("track-players", "true"),
        DEFAULT_PLAYER_NAME ("default-player-name", "player");

        private final String key;
        private final String defaultValue;

        private ConfigurationEntry(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getKey() {
            return key;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public static String getProperty(Properties properties, ConfigurationEntry entry) {
            return properties.getProperty(entry.getKey(), entry.getDefaultValue());
        }

        public static void validateProperty(Properties properties, ConfigurationEntry entry) {
            if (!properties.containsKey(entry.getKey()))
                properties.setProperty(entry.getKey(), entry.getDefaultValue());
        }
    }

    private final ServerListPlusAPI api;

    private final Path configPath;
    private final Path linesPath;

    private List<String> lines = new ArrayList<>();

    private boolean trackPlayers = true;
    private String defaultPlayerName = "player";

    public ServerListConfiguration(ServerListPlusAPI api) throws IOException {
        this.api = api;

        Path pluginFolder = api.getPlugin().getDataFolder().toPath();
        this.configPath = pluginFolder.resolve(CONFIG_FILENAME);
        this.linesPath = pluginFolder.resolve(LINES_FILENAME);

        this.reload();
    }

    public Path getConfigPath() {
        return configPath;
    }

    public Path getLinesPath() {
        return linesPath;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public boolean trackPlayers() {
        return trackPlayers;
    }

    public String getDefaultPlayerName() {
        return defaultPlayerName;
    }

    public void reload() throws IOException {
        try {
            api.getLogger().info("Reloading plugin configuration...");
            Properties properties = this.loadConfiguration();

            this.trackPlayers = Boolean.parseBoolean(ConfigurationEntry.getProperty(properties, ConfigurationEntry.TRACK_PLAYERS));
            this.defaultPlayerName = ConfigurationEntry.getProperty(properties, ConfigurationEntry.DEFAULT_PLAYER_NAME);
            api.getLogger().info("Successful!");

            api.getLogger().info("Reloading server list lines...");
            this.lines = this.loadServerListLines();
            api.getLogger().info("Successful!");
        } catch (IOException e) {
            api.getLogger().log(Level.SEVERE, "Unable to access server list configuration file: " + configPath.toAbsolutePath().toString(), e); throw e;
        } catch (Exception e) {
            api.getLogger().log(Level.SEVERE, "An internal error occurred while reloading the plugin configuration!", e); throw e;
        }

    }

    private Properties loadConfiguration() throws IOException {
        if (Files.notExists(configPath)) {
            Files.createDirectories(configPath.getParent());

            InputStream defaultConfig = api.getPlugin().getClass().getClassLoader().getResourceAsStream(CONFIG_FILENAME);
            if (defaultConfig != null)
                Files.copy(defaultConfig, configPath);
        }

        Properties defaultProperties = new Properties();

        if (Files.exists(configPath)) {
            defaultProperties.load(Files.newBufferedReader(configPath, FILE_CHARSET));
        } else Files.createFile(configPath);

        for (ConfigurationEntry entry : ConfigurationEntry.values())
            ConfigurationEntry.validateProperty(defaultProperties, entry);

        defaultProperties.store(Files.newBufferedWriter(configPath, FILE_CHARSET), "ServerListPlus - Default configuration");

        return defaultProperties;
    }

    private List<String> loadServerListLines() throws IOException {
        if (Files.notExists(linesPath)) {
            Files.createDirectories(linesPath.getParent());

            InputStream defaultConfig = api.getPlugin().getClass().getClassLoader().getResourceAsStream(LINES_FILENAME);
            if (defaultConfig != null)
                Files.copy(defaultConfig, linesPath);
        }

        if (Files.exists(linesPath)) {
            try (BufferedReader reader = Files.newBufferedReader(linesPath, FILE_CHARSET)) {
                List<String> lines = new ArrayList<>();

                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;

                    // Process configuration line
                    if (line.startsWith("#")) continue; // Skip comment lines
                    lines.add(api.getPlugin().colorizeString((line.length() > 0) ? line : "&r"));
                }

                return lines;
            }
        } else {
            Files.createFile(linesPath);
            return new ArrayList<>();
        }
    }
}
