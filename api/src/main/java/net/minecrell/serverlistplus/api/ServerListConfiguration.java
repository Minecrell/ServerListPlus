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

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecrell.serverlistplus.api.yaml.FieldOrderPropertyUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ServerListConfiguration {
    private static final String[] comments = new String[] {
            "ServerListPlus - http://www.spigotmc.org/resources/serverlistplus.241/",
            "Please go to the plugin website for more information about the configuration!"
    };

    public static ServerListConfiguration getDefaultConfiguration() {
        return new ServerListConfiguration();
    }

    public ServerListConfiguration() {
        // Default configuration
        this.enableMetrics = true;

        this.lines = Arrays.asList(
                "&aHey, %player%!",
                "&eThis is the default configuration",
                "&eof the ServerListPlus plugin preview.",
                "",
                "&6If you can see this, you should probably",
                "&6contact the server admin to customize",
                "&6this text for the server!"
        );

        this.playerTracking = new PlayerTracking();
        this.forcedHosts = new HashMap<>();

        this.forcedHosts.put(
                "ExampleServer",
                Arrays.asList(
                        "&eExample lines for an example server",
                        "&eusing forced hosts. Have fun!"
                )
        );
    }

    private boolean enableMetrics;

    private List<String> lines;
    private PlayerTracking playerTracking;

    private Map<String, List<String>> forcedHosts;

    @Data
    @AllArgsConstructor
    public static class PlayerTracking {
        private boolean enabled;
        private UnknownPlayer unknownPlayer;

        public PlayerTracking() {
            // Default configuration
            this.enabled = true;
            this.unknownPlayer = new UnknownPlayer();
        }

        @Data
        @AllArgsConstructor
        public static class UnknownPlayer {
            private String name;
            private CustomLines customLines;

            public UnknownPlayer() {
                // Default configuration
                this.name = "player";
                this.customLines = new CustomLines();
            }

            @Data
            @AllArgsConstructor
            public static class CustomLines {
                public CustomLines() {
                    this.enabled = false;
                    this.lines = Arrays.asList(
                            "&aHey unknown player!",
                            "&eThis message is only used",
                            "&eif you haven't logged in to",
                            "&ethe server yet.",
                            "",
                            "&6A custom text for unknown players",
                            "&6is enabled in the configuration!"
                    );
                }

                private boolean enabled;
                private List<String> lines;
            }
        }
    }

    // Configuration loading
    public final static Charset FILE_CHARSET = StandardCharsets.UTF_8;
    public final static String CONFIG_FILENAME = "serverlistplus.yml";

    public static Yaml createYAMLLoader(ClassLoader loader) {
        Representer representer = new Representer();
        representer.setPropertyUtils(new FieldOrderPropertyUtils());
        representer.addClassTag(ServerListConfiguration.class, new Tag("ServerListConfiguration"));

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        return new Yaml(new CustomClassLoaderConstructor(loader), representer, options);
    }

    public static ServerListConfiguration loadConfiguration(ServerListPlusAPI api, Yaml yaml) throws Exception {
        Path configPath = api.getPlugin().getDataFolder().toPath().resolve(CONFIG_FILENAME);

        api.getLogger().info("Loading configuration...");

        ServerListConfiguration config;

        if (Files.notExists(configPath)) {
            Files.createDirectories(configPath.getParent());

            InputStream defaultConfig = api.getPlugin().getClass().getClassLoader().getResourceAsStream(CONFIG_FILENAME);
            if (defaultConfig != null) {
                api.getLogger().info("Copying default configuration...");
                Files.copy(defaultConfig, configPath);
            }
        }

        if (Files.notExists(configPath)) {
            api.getLogger().info("Creating default configuration...");

            config = new ServerListConfiguration();
        } else {
            try (BufferedReader reader = Files.newBufferedReader(configPath, FILE_CHARSET)) {
                config = yaml.loadAs(reader, ServerListConfiguration.class);
            }
        }

        if (config != null) {
            api.getLogger().info("Configuration successfully loaded!");
            api.getLogger().info("Saving configuration...");

            try (BufferedWriter writer = Files.newBufferedWriter(configPath, FILE_CHARSET)) {
                for (String comment : comments) {
                    writer.write("# " + comment); writer.newLine();
                }

                yaml.dump(config, writer);
            }

            api.getLogger().info("Configuration successfully saved.");

            return config;
        } else throw new Exception("YAML configuration loader returned null!");
    }
}
