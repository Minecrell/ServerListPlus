package net.minecrell.serverlistplus.server.network.protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import net.minecrell.serverlistplus.server.network.protocol.MinecraftProtocol;
import net.minecrell.serverlistplus.server.network.protocol.ProtocolState;

@Getter
public class PacketHandshake implements ClientPacket {

    private int protocolVersion;
    private String host;
    private int port;
    private ProtocolState nextState;

    @Override
    public void read(ByteBuf buf) {
        this.protocolVersion = MinecraftProtocol.readVarInt(buf);
        this.host = MinecraftProtocol.readString(buf, 255);
        this.port = buf.readUnsignedShort();
        this.nextState = MinecraftProtocol.readVarInt(buf) == 1 ? ProtocolState.STATUS : ProtocolState.LOGIN;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, PacketHandler handler) {
        handler.handle(ctx, this);
    }

}
