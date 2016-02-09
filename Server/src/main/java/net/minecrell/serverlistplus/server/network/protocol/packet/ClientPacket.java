package net.minecrell.serverlistplus.server.network.protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public interface ClientPacket {

    void read(ByteBuf buf);

    void handle(ChannelHandlerContext ctx, PacketHandler handler) throws IOException;

}
