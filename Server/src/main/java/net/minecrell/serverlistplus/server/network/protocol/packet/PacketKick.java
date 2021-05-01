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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecrell.serverlistplus.server.network.protocol.MinecraftProtocol;

public class PacketKick implements ServerPacket {

    private final Component reason;

    public PacketKick(Component reason) {
        this.reason = reason;
    }

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public void write(ByteBuf buf) {
        MinecraftProtocol.writeString(buf, GsonComponentSerializer.gson().serialize(this.reason));
    }

}
