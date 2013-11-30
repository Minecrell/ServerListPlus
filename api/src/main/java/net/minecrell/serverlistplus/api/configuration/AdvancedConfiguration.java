package net.minecrell.serverlistplus.api.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class AdvancedConfiguration {
    public static final String[] COMMENTS = new String[] {
            "ADVANCED CONFIGURATION!", "Second line"
    };

    private boolean enableMetrics;

    private PlayerTracking playerTracking;

    private Map<String, List<String>> forcedHosts;

    public static AdvancedConfiguration getDefaultConfiguration() {
        return new AdvancedConfiguration();
    }

    public AdvancedConfiguration() {
        // Default configuration
        this.enableMetrics = true;

        this.playerTracking = new PlayerTracking();
        this.forcedHosts = new HashMap<>();

        this.forcedHosts.put(
                "ExampleServer",
                Arrays.asList(
                        "&eExample lines for an example server",
                        "&eusing forced hosts. Have fun!"
                )
        );

        this.forcedHosts.put(
                "hostname.example.com",
                Arrays.asList(
                        "&eThis is an example for a custom forced host",
                        "&eusing its hostname instead of the server name."
                )
        );
    }

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
}
