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

package net.minecrell.serverlistplus.core.player;

import net.minecrell.serverlistplus.core.ServerListPlusException;

import java.net.InetAddress;

public interface IdentificationStorage {
    boolean has(InetAddress client);
    PlayerIdentity resolve(InetAddress client);

    void update(InetAddress client, PlayerIdentity identity);

    void reload() throws ServerListPlusException;
    void enable() throws ServerListPlusException;
    boolean isEnabled();
    void disable() throws ServerListPlusException;
}
