package net.minecrell.serverlistplus.server.network.protocol.packet;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import net.minecrell.serverlistplus.server.network.protocol.MinecraftProtocol;
import net.minecrell.serverlistplus.server.status.StatusPingResponse;

public class PacketStatusResponse implements ServerPacket {

    private static final Gson gson = new Gson();

    private final StatusPingResponse response;

    public PacketStatusResponse(StatusPingResponse response) {
        this.response = response;
    }

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public void write(ByteBuf buf) {
        MinecraftProtocol.writeString(buf, gson.toJson(this.response));
    }

}
