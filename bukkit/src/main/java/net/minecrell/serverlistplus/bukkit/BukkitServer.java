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

package net.minecrell.serverlistplus.bukkit;

import net.md_5.bungee.api.ChatColor;
import net.minecrell.serverlistplus.api.plugin.ServerListServer;

import java.io.File;

public class BukkitServer implements ServerListServer {
    private final BukkitServerListPlus plugin;

    public BukkitServer(BukkitServerListPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public String colorizeString(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUKKIT;
    }

    @Override
    public String getServerVersion() {
        return plugin.getServer().getVersion();
    }

    @Override
    public int getOnlinePlayers() {
        return plugin.getServer().getOnlinePlayers().length;
    }

    @Override
    public boolean getOnlineMode() {
        return plugin.getServer().getOnlineMode();
    }

    @Override
    public File getMetricsConfiguration() {
        return new File(new File(plugin.getDataFolder().getParentFile(), "PluginMetrics"), "config.properties");
    }
}
