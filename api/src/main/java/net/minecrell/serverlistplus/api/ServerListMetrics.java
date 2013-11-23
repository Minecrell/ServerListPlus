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

import net.minecrell.serverlistplus.api.metrics.Metrics;
import net.minecrell.serverlistplus.api.metrics.SimpleCounterPlotter;

import java.io.File;
import java.io.IOException;

public final class ServerListMetrics extends Metrics<ServerListPlusAPI> {
    private SimpleCounterPlotter pingPlotter, playerPlotter, loginPlotter;
    private SimpleCounterPlotter configPlotter;

    public ServerListMetrics(ServerListPlusAPI api) throws IOException {
        super(api, "ServerListPlus", api.getPlugin().getVersion());

        Graph pingGraph = this.createGraph("Server pings processed");
        pingGraph.addPlotter((pingPlotter = new SimpleCounterPlotter("Server pings")));
        pingGraph.addPlotter((playerPlotter = new SimpleCounterPlotter("Players identified")));
        pingGraph.addPlotter((loginPlotter = new SimpleCounterPlotter("Players logged in")));
        this.createGraph("Configuration lines loaded").addPlotter((configPlotter = new SimpleCounterPlotter()));
    }

    protected void reloadConfiguration(ServerListConfiguration config) {
        configPlotter.count(config.getLines().size());
    }

    protected void processRequest(boolean identified) {
        pingPlotter.count();
        if (identified) playerPlotter.count();
    }

    protected void processPlayerLogin() {
        loginPlotter.count();
    }

    @Override
    public String getFullServerVersion() {
        return this.getPlugin().getServer().getServerVersion();
    }

    @Override
    public boolean getAuthMode() {
        return this.getPlugin().getServer().getOnlineMode();
    }

    @Override
    public int getPlayersOnline() {
        return this.getPlugin().getServer().getOnlinePlayers();
    }

    @Override
    public File getConfigFile() {
        return this.getPlugin().getServer().getMetricsConfiguration();
    }
}
