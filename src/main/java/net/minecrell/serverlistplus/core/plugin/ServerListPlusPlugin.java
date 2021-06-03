/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
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

package net.minecrell.serverlistplus.core.plugin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilderSpec;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.storage.InstanceStorage;
import net.minecrell.serverlistplus.core.favicon.FaviconCache;
import net.minecrell.serverlistplus.core.replacement.rgb.RGBFormat;
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
    FaviconCache<?> getFaviconCache();

    void runAsync(Runnable task);
    ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit);

    String colorize(String s);
    RGBFormat getRGBFormat();

    void initialize(ServerListPlusCore core);
    void reloadCaches(ServerListPlusCore core);
    void createFaviconCache(CacheBuilderSpec spec);
    void configChanged(ServerListPlusCore core, InstanceStorage<Object> confs);
    void statusChanged(StatusManager status, boolean hasChanges);
}
