package net.minecrell.serverlistplus.server.network.protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketPing implements ClientPacket, ServerPacket {

    private long payload;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public void read(ByteBuf buf) {
        this.payload = buf.readLong();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong(this.payload);
    }

    @Override
    public void handle(ChannelHandlerContext ctx, PacketHandler handler) {
        handler.handle(ctx, this);
    }

}
