package net.minecrell.serverlistplus.server.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class Varint21FrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();

        final byte[] buf = new byte[3];
        for (int i = 0; i< buf.length; i++) {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }

            buf[i] = in.readByte();
            if (buf[i] >= 0) {
                int length = MinecraftProtocol.readVarInt(Unpooled.wrappedBuffer(buf));
                if (in.readableBytes() >= length) {
                    if (in.hasMemoryAddress()) {
                        out.add(in.slice(in.readerIndex(), length).retain());
                        in.skipBytes(length);
                    } else {
                        out.add(in.readBytes(length));
                    }
                } else {
                    in.resetReaderIndex();
                }

                return;
            }
        }

        throw new CorruptedFrameException("Length wider than 21-bit");
    }

}
