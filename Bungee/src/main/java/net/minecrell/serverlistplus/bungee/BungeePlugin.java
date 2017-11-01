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

package net.minecrell.serverlistplus.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.minecrell.serverlistplus.ServerListPlus;
import net.minecrell.serverlistplus.platform.PlatformType;
import net.minecrell.serverlistplus.platform.Platform;
import org.slf4j.LoggerFactory;

public final class BungeePlugin extends Plugin implements Platform {

    private ServerListPlus core;

    @Override
    public void onLoad() {
        this.core = new ServerListPlus(this, LoggerFactory.getLogger(getLogger().getName()));
    }

    @Override
    public void onEnable() {
        core.initialize();
    }

    @Override
    public PlatformType getType() {
        return PlatformType.BUNGEE;
    }

    @Override
    public ServerListPlus getCore() {
        return core;
    }

}
