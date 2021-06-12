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

package net.minecrell.serverlistplus.server.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecrell.serverlistplus.server.ServerListPlusServer;
import net.minecrell.serverlistplus.server.status.StatusPingResponse;

import java.net.InetSocketAddress;

public class LegacyClientHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf buf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.buf = ctx.alloc().buffer();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (this.buf != null) {
            this.buf.release();
            this.buf = null;
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg;
        this.buf.writeBytes(m);
        m.release();

        this.buf.markReaderIndex();
        boolean result = false;

        try {
            result = readLegacy(ctx, this.buf);
        } finally {
            this.buf.resetReaderIndex();
            if (!result) {
                ByteBuf buf = this.buf;
                this.buf = null;

                ctx.pipeline().remove("legacy");
                ctx.fireChannelRead(buf);
            }
        }
    }

    private boolean readLegacy(ChannelHandlerContext ctx, ByteBuf buf) {
        if (buf.readUnsignedByte() != 0xFE) {
            return false;
        }

        InetSocketAddress client = (InetSocketAddress) ctx.channel().remoteAddress();
        StatusPingResponse response;

        int i = buf.readableBytes();
        switch (i) {
            case 0:
                response = ServerListPlusServer.postLegacy(client, null);
                sendResponse(ctx, String.format("%s§%d§%d",
                        getFirstLine(PlainTextComponentSerializer.plainText().serialize(response.getDescription())),
                        response.getPlayers().getOnline(),
                        response.getPlayers().getMax()));
                break;
            case 1:
                if (buf.readUnsignedByte() != 0x01) {
                    return false;
                }

                response = ServerListPlusServer.postLegacy(client, null);
                sendResponse(ctx, String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                        response.getVersion().getProtocol(),
                        response.getVersion().getName(),
                        getFirstLine(LegacyComponentSerializer.legacySection().serialize(response.getDescription())),
                        response.getPlayers().getOnline(),
                        response.getPlayers().getMax()));

                break;
            default:
                if (buf.readUnsignedByte() != 0x01 || buf.readUnsignedByte() != 0xFA) {
                    return false;
                }
                if (!buf.isReadable(2)) {
                    break;
                }
                short length = buf.readShort();
                if (!buf.isReadable(length * 2)) {
                    break;
                }
                if (!buf.readBytes(length * 2).toString(Charsets.UTF_16BE).equals("MC|PingHost")) {
                    return false;
                }
                if (!buf.isReadable(2)) {
                    break;
                }
                length = buf.readShort();
                if (!buf.isReadable(length)) {
                    break;
                }

                buf.readUnsignedByte();
                length = buf.readShort();
                String host = buf.readBytes(length * 2).toString(Charsets.UTF_16BE);
                int port = buf.readInt();

                response = ServerListPlusServer.postLegacy(client, InetSocketAddress.createUnresolved(host, port));
                sendResponse(ctx, String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                        response.getVersion().getProtocol(),
                        response.getVersion().getName(),
                        getFirstLine(LegacyComponentSerializer.legacySection().serialize(response.getDescription())),
                        response.getPlayers().getOnline(),
                        response.getPlayers().getMax()));
                break;
        }

        return true;
    }

    private static String getFirstLine(String s) {
        int i = s.indexOf('\n');
        return i == -1 ? s : s.substring(0, i);
    }

    private static void sendResponse(ChannelHandlerContext ctx, String response) {
        ctx.writeAndFlush(encode(response)).addListener(ChannelFutureListener.CLOSE);
    }

    private static ByteBuf encode(String response) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0xFF);

        char[] chars = response.toCharArray();
        buf.writeShort(chars.length);

        for (char c : chars) {
            buf.writeChar(c);
        }

        return buf;
    }

}
