/*
 * ServerListPlus
 * Copyright (C) 2017 Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkState;

import net.minecrell.serverlistplus.platform.ServerListPlusPlatform;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public final class ServerListPlus {

    @Nullable private static ServerListPlus instance;

    public static ServerListPlus getInstance() {
        checkState(instance != null, "ServerListPlus was not initialized yet");
        return instance;
    }

    private final ServerListPlusPlatform platform;
    private final Logger logger;
    private final String name;
    private final String version;

    private boolean initialized;

    public ServerListPlus(ServerListPlusPlatform platform, Logger logger) {
        checkState(instance == null, "ServerListPlus was already initialized");
        this.platform = platform;
        this.logger = logger;

        Package p = getClass().getPackage();
        this.name = firstNonNull(p.getSpecificationTitle(), "ServerListPlus");
        this.version = firstNonNull(p.getSpecificationVersion(), "Unknown");

        instance = this;
    }

    public ServerListPlusPlatform getPlatform() {
        return platform;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDisplayName() {
        return name + " (" + platform.getType() + ')';
    }

    public String getDisplayVersion() {
        return getDisplayName() + " v" + version;
    }

    public void initialize() {
        checkState(!initialized, "Already initialized");
        initialized = true;

        logger.info("Initializing {}", getDisplayVersion());
    }

}
