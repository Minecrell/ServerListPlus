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

package net.minecrell.serverlistplus.server.network.protocol;

import net.minecrell.serverlistplus.server.network.protocol.packet.ClientPacket;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketHandshake;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketLoginStart;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketPing;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketStatusRequest;

import java.util.function.Supplier;

public enum ProtocolState {
    HANDSHAKE(PacketHandshake::new),
    STATUS(PacketStatusRequest::new, PacketPing::new),
    LOGIN(PacketLoginStart::new);

    private final Supplier<? extends ClientPacket>[] client;

    @SafeVarargs
    ProtocolState(Supplier<? extends ClientPacket>... packets) {
        this.client = packets;
    }

    public ClientPacket getPacket(int id) {
        return id < this.client.length && this.client[id] != null ? this.client[id].get() : null;
    }

}
