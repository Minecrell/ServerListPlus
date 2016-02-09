package net.minecrell.serverlistplus.server.network.protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecrell.serverlistplus.server.network.protocol.MinecraftProtocol;

import java.io.IOException;

public class PacketLoginStart implements ClientPacket {

    private String name;

    public String getName() {
        return this.name;
    }

    @Override
    public void read(ByteBuf buf) {
        this.name = MinecraftProtocol.readString(buf, 16);
    }

    @Override
    public void handle(ChannelHandlerContext ctx, PacketHandler handler) throws IOException {
        handler.handle(ctx, this);
    }

}
