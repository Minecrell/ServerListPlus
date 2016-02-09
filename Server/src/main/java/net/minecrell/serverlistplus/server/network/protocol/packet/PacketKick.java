package net.minecrell.serverlistplus.server.network.protocol.packet;

import com.google.gson.stream.JsonWriter;
import io.netty.buffer.ByteBuf;
import net.minecrell.serverlistplus.server.network.protocol.MinecraftProtocol;

import java.io.IOException;
import java.io.StringWriter;

public class PacketKick implements ServerPacket {

    private final String reason;

    public PacketKick(String reason) {
        this.reason = reason;
    }

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        StringWriter writer = new StringWriter();
        try (JsonWriter json = new JsonWriter(writer)) {
            json.beginObject();
            json.name("text").value(this.reason);
            json.endObject();
        }

        MinecraftProtocol.writeString(buf, writer.toString());
    }

}
