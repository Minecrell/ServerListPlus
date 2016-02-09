package net.minecrell.serverlistplus.server.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecrell.serverlistplus.server.network.ClientHandler;
import net.minecrell.serverlistplus.server.network.protocol.packet.ClientPacket;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MinecraftDecoder extends ByteToMessageDecoder {

    private static final Logger logger = Logger.getLogger(MinecraftDecoder.class.getName());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) {
            return;
        }

        int id = MinecraftProtocol.readVarInt(in);
        ClientPacket packet = ClientHandler.getState(ctx).getPacket(id);
        if (packet == null) {
            logger.log(Level.WARNING, "Unknown packet: " + Integer.toHexString(id));
            return;
        }

        packet.read(in);
        if (in.isReadable()) {
            logger.log(Level.WARNING, "Packet " + id + " was not fully read: " + in.readableBytes() + " bytes left");
        }

        out.add(packet);
    }

}
