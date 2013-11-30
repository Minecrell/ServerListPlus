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

import net.minecrell.serverlistplus.api.configuration.AdvancedConfiguration;
import net.minecrell.serverlistplus.api.configuration.ConfigurationManager;
import net.minecrell.serverlistplus.api.metrics.AbstractMetrics;
import net.minecrell.serverlistplus.api.metrics.Graph;
import net.minecrell.serverlistplus.api.metrics.MetricsPlugin;
import net.minecrell.serverlistplus.api.metrics.MetricsServer;
import net.minecrell.serverlistplus.api.metrics.SimpleCounterPlotter;
import net.minecrell.serverlistplus.api.util.SimplePrefixLogger;

import java.util.List;
import java.util.logging.Logger;

public final class ServerListMetrics extends AbstractMetrics<ServerListPlusAPI> {
    private final Logger logger;

    private SimpleCounterPlotter pingPlotter, playerPlotter, loginPlotter;
    private SimpleCounterPlotter configPlotter;

    private MetricsPlugin plugin;
    private MetricsServer server;

    public ServerListMetrics(ServerListPlusAPI api) throws Exception {
        super(api, api.getPlugin().getServerListServer().getMetricsConfigurationProvider());

        this.logger = new SimplePrefixLogger("ServerListMetrics", api.getLogger(), "Metrics");

        Graph pingGraph = this.createGraph("Server pings processed");
        pingGraph.addPlotter((pingPlotter = new SimpleCounterPlotter("Server pings")));
        pingGraph.addPlotter((playerPlotter = new SimpleCounterPlotter("Players identified")));
        pingGraph.addPlotter((loginPlotter = new SimpleCounterPlotter("Players logged in")));
        this.createGraph("Server list lines loaded").addPlotter((configPlotter = new SimpleCounterPlotter()));

        this.plugin = new MetricsPlugin() {
            @Override
            public String getName() {
                return "ServerListPlus";
            }

            @Override
            public String getVersion() {
                return getPlugin().getPlugin().getVersion();
            }
        };

        this.server = new MetricsServer() {

            @Override
            public String getVersion() {
                return getPlugin().getServer().getServerVersion();
            }

            @Override
            public boolean getOnlineMode() {
                return getPlugin().getServer().getOnlineMode();
            }

            @Override
            public int getOnlinePlayers() {
                return getPlugin().getServer().getOnlinePlayers();
            }
        };
    }

    protected void reloadConfiguration(ConfigurationManager config) {
        configPlotter.count(config.getLines().size());
        AdvancedConfiguration advancedConfig = config.getAdvanced();
        if (advancedConfig != null) {
            for (List<String> lines : advancedConfig.getForcedHosts().values())
                configPlotter.count(lines.size());
            if (advancedConfig.getPlayerTracking().getUnknownPlayer().getCustomLines().isEnabled())
                configPlotter.count(advancedConfig.getPlayerTracking().getUnknownPlayer().getCustomLines().getLines().size());
        }
    }

    protected void processRequest(boolean identified) {
        pingPlotter.count();
        if (identified) playerPlotter.count();
    }

    protected void processPlayerLogin() {
        loginPlotter.count();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected MetricsPlugin getMetricsPlugin() {
        return plugin;
    }

    @Override
    protected MetricsServer getMetricsServer() {
        return server;
    }
}
