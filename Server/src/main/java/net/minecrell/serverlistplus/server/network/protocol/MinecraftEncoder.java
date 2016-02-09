package net.minecrell.serverlistplus.server.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecrell.serverlistplus.server.network.protocol.packet.ServerPacket;

@ChannelHandler.Sharable
public final class MinecraftEncoder extends MessageToByteEncoder<ServerPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerPacket packet, ByteBuf out) throws Exception {
        MinecraftProtocol.writeVarInt(out, packet.getId());
        packet.write(out);
    }

}
