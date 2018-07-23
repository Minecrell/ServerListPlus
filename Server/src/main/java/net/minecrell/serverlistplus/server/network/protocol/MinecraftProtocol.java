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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.AttributeKey;

import java.nio.charset.StandardCharsets;

public final class MinecraftProtocol {

    public static final AttributeKey<ProtocolState> PROTOCOL_STATE = AttributeKey.valueOf("state");

    public static final ChannelHandler LOGGER_HANDLER = new LoggerHandler();
    public static final ChannelHandler LENGTH_PREPENDER = new Varint21LengthPrepender();
    public static final ChannelHandler PACKET_ENCODER = new MinecraftEncoder();

    private MinecraftProtocol() {
    }

    public static int readVarInt(ByteBuf buf) {
        int result = 0;
        int bytes = 0;
        byte b;

        while (true) {
            b = buf.readByte();
            result |= (b & 127) << bytes++ * 7;
            if (bytes > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if ((b & 128) != 128) {
                break;
            }
        }

        return result;
    }

    public static int getVarIntSize(int input) {
        for (int i = 1; i <= 3; ++i) {
            if ((input & -1 << i * 7) == 0) {
                return i;
            }
        }

        return 4;
    }

    public static void writeVarInt(ByteBuf buf, int i) {
        while ((i & -128) != 0) {
            buf.writeByte(i & 127 | 128);
            i >>>= 7;
        }

        buf.writeByte(i);
    }

    public static String readString(ByteBuf buf, int max) {
        int length = readVarInt(buf);
        if (length > max * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + length + " > " + max * 4 + ")");
        }

        if (length < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        }

        String s = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);

        if (s.length() > max) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + s.length() + " > " + max + ")");
        }

        buf.skipBytes(length);
        return s;
    }

    public static void writeString(ByteBuf buf, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

        if (bytes.length > Short.MAX_VALUE) {
            throw new EncoderException("String too big (was " + bytes.length + " bytes encoded, max " + Short.MAX_VALUE + ")");
        }

        MinecraftProtocol.writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

}
