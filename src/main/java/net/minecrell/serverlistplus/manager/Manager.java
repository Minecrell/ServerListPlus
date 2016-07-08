/*
 * ServerListPlus
 * Copyright (C) 2016, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.manager;

import net.minecrell.serverlistplus.ServerListPlus;
import net.minecrell.serverlistplus.logger.Logger;

import java.util.Objects;

public abstract class Manager {

    protected final ServerListPlus core;

    public Manager(ServerListPlus core) {
        this.core = Objects.requireNonNull(core, "core");
    }

    public final ServerListPlus getCore() {
        return this.core;
    }

    protected final Logger getLogger() {
        return this.core.getLogger();
    }

}
