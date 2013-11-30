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

package net.minecrell.serverlistplus.bungee;

import net.md_5.bungee.api.ChatColor;
import net.minecrell.serverlistplus.api.metrics.configuration.MetricsConfigurationProvider;
import net.minecrell.serverlistplus.api.plugin.ServerListServer;
import net.minecrell.serverlistplus.bungee.metrics.BungeeMetricsConfigurationProvider;

import java.io.File;

public class BungeeServer implements ServerListServer {
    private final BungeeServerListPlus plugin;

    public BungeeServer(BungeeServerListPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public String colorizeString(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUNGEE;
    }

    @Override
    public String getServerVersion() {
        return plugin.getProxy().getVersion() + " (MC: " + plugin.getProxy().getGameVersion() + ")";
    }

    @Override
    public int getOnlinePlayers() {
        return plugin.getProxy().getOnlineCount();
    }

    @Override
    public boolean getOnlineMode() {
        return plugin.getProxy().getConfigurationAdapter().getBoolean("online_mode", true);
    }

    @Override
    public MetricsConfigurationProvider getMetricsConfigurationProvider() {
        return new BungeeMetricsConfigurationProvider(plugin);
    }
}
