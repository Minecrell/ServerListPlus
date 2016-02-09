package net.minecrell.serverlistplus.server.network.protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketStatusRequest implements ClientPacket {

    @Override
    public void read(ByteBuf buf) {

    }

    @Override
    public void handle(ChannelHandlerContext ctx, PacketHandler handler) {
        handler.handle(ctx, this);
    }

}
