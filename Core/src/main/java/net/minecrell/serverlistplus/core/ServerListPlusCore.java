/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your server list ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core;

import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;

import com.google.common.base.Preconditions;

/**
 * Represents the core part of the ServerListPlus plugin.
 */
public class ServerListPlusCore {
    private final ServerListPlusPlugin plugin;
    private final ServerListPlusLogger logger;

    private final ConfigurationManager configManager;

    public ServerListPlusCore(ServerListPlusPlugin plugin) throws ServerListPlusException {
        this.plugin = Preconditions.checkNotNull(plugin, "plugin");
        this.logger = new ServerListPlusLogger(this);
        this.configManager = new ConfigurationManager(this);
        this.reload();
    }

    public void reload() throws ServerListPlusException {
        configManager.reload();
    }

    public ServerListPlusLogger getLogger() {
        return logger;
    }

    public ServerListPlusPlugin getPlugin() {
        return plugin;
    }

    public ConfigurationManager getConf() {
        return configManager;
    }
}