package net.minecrell.serverlistplus.api;

import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;

public final class ServerListMetrics extends Metrics {
    private final ServerListPlusAPI api;

    private int pingCounter = 0;

    private class PingPlotter extends Plotter {
        private PingPlotter() {
            super("Server pings");
        }

        @Override
        public int getValue() {
            return pingCounter;
        }

        @Override
        public void reset() {
            pingCounter = 0;
        }
    }

    private int configCounter = 0;

    private class ConfigPlotter extends Plotter {
        private ConfigPlotter() {
            super("Configuration lines");
        }

        @Override
        public int getValue() {
            return configCounter;
        }

        @Override
        public void reset() {
            configCounter = 0;
        }
    }

    private int indentifiedPlayers = 0;

    private class PlayerPlotter extends Plotter {
        private PlayerPlotter() {
            super("Players");
        }

        @Override
        public int getValue() {
            return indentifiedPlayers;
        }

        @Override
        public void reset() {
            indentifiedPlayers = 0;
        }
    }

    public ServerListMetrics(ServerListPlusAPI api) throws IOException {
        super("ServerListPlusAPI", api.getPlugin().getVersion());
        this.api = api;

        this.createGraph("Server pings processed").addPlotter(new PingPlotter());
        this.createGraph("Configuration lines loaded").addPlotter(new ConfigPlotter());
        this.createGraph("Players identified").addPlotter(new PlayerPlotter());
    }

    protected void reloadConfiguration(ServerListConfiguration config) {
        configCounter += config.getLines().size();
    }

    protected void processRequest(boolean identified) {
        pingCounter++;
        if (identified) indentifiedPlayers++;
    }

    @Override
    public String getFullServerVersion() {
        return api.getPlugin().getServerVersion();
    }

    @Override
    public int getPlayersOnline() {
        return api.getPlugin().getOnlinePlayers();
    }

    @Override
    public File getConfigFile() {
        return new File(new File(api.getPlugin().getDataFolder().getParent(), "PluginMetrics"), "config.properties");
    }
}
