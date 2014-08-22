/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.core.plugin;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.status.StatusManager;
import net.minecrell.serverlistplus.core.util.InstanceStorage;

import java.nio.file.Path;
import java.util.logging.Logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.LoadingCache;

/**
 * Represents a plugin container running the ServerListPlus core.
 */
public interface ServerListPlusPlugin {
    Logger getLogger();
    ServerType getServerType();
    String getServerImplementation();
    Path getPluginFolder();

    String getRandomPlayer();
    Integer getOnlinePlayersAt(String location);

    Cache<?, ?> getRequestCache();
    LoadingCache<FaviconSource, ?> getFaviconCache();

    String colorize(String s);

    void initialize(ServerListPlusCore core);
    void reloadCaches(ServerListPlusCore core);
    void reloadFaviconCache(CacheBuilderSpec spec);
    void configChanged(InstanceStorage<Object> confs);
    void statusChanged(StatusManager status);
}
