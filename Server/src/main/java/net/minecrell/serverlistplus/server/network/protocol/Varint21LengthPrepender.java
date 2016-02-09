package net.minecrell.serverlistplus.server.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class Varint21LengthPrepender extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int len = msg.readableBytes();
        int header = MinecraftProtocol.getVarIntSize(len);
        if (header > 3) {
            throw new EncoderException("Packet too big: " + len);
        }

        out.ensureWritable(header + len);
        MinecraftProtocol.writeVarInt(out, len);
        out.writeBytes(msg);
    }

}
