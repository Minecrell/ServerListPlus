package net.minecrell.serverlistplus.server.network.protocol.packet;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface ServerPacket {

    int getId();

    void write(ByteBuf buf) throws IOException;

}
