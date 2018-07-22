/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.LoadingCache;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.storage.InstanceStorage;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.logging.ServerListPlusLogger;
import net.minecrell.serverlistplus.core.status.StatusManager;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Represents a plugin container running the ServerListPlus core.
 */
public interface ServerListPlusPlugin {
    ServerListPlusCore getCore();

    ServerType getServerType();
    String getServerImplementation();
    Path getPluginFolder();

    Integer getOnlinePlayers(String location);

    Iterator<String> getRandomPlayers();
    Iterator<String> getRandomPlayers(String location);

    Cache<?, ?> getRequestCache();
    LoadingCache<FaviconSource, ?> getFaviconCache();

    void runAsync(Runnable task);
    ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit);

    String colorize(String s);

    ServerListPlusLogger createLogger(ServerListPlusCore core);
    void initialize(ServerListPlusCore core);
    void reloadCaches(ServerListPlusCore core);
    void reloadFaviconCache(CacheBuilderSpec spec);
    void configChanged(ServerListPlusCore core, InstanceStorage<Object> confs);
    void statusChanged(StatusManager status, boolean hasChanges);
}
