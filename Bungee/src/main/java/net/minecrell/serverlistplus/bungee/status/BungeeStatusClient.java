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

package net.minecrell.serverlistplus.bungee.status;

import net.md_5.bungee.api.connection.PendingConnection;
import net.minecrell.serverlistplus.status.StatusClient;

import java.net.InetSocketAddress;

import javax.annotation.Nullable;

class BungeeStatusClient implements StatusClient {

    private final PendingConnection connection;

    BungeeStatusClient(PendingConnection connection) {
        this.connection = connection;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.connection.getAddress();
    }

    @Override
    public int getProtocolVersion() {
        return this.connection.getVersion();
    }

    @Nullable
    @Override
    public InetSocketAddress getVirtualHost() {
        return this.connection.getVirtualHost();
    }

}
