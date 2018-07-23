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

package net.minecrell.serverlistplus.server.network.protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import net.minecrell.serverlistplus.server.network.protocol.MinecraftProtocol;
import net.minecrell.serverlistplus.server.network.protocol.ProtocolState;

@Getter
public class PacketHandshake implements ClientPacket {

    private int protocolVersion;
    private String host;
    private int port;
    private ProtocolState nextState;

    @Override
    public void read(ByteBuf buf) {
        this.protocolVersion = MinecraftProtocol.readVarInt(buf);
        this.host = MinecraftProtocol.readString(buf, 255);
        this.port = buf.readUnsignedShort();
        this.nextState = MinecraftProtocol.readVarInt(buf) == 1 ? ProtocolState.STATUS : ProtocolState.LOGIN;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, PacketHandler handler) {
        handler.handle(ctx, this);
    }

}
