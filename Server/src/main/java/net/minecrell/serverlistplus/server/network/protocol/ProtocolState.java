package net.minecrell.serverlistplus.server.network.protocol;

import net.minecrell.serverlistplus.server.network.protocol.packet.ClientPacket;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketHandshake;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketLoginStart;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketPing;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketStatusRequest;

import java.util.function.Supplier;

public enum ProtocolState {
    HANDSHAKE(PacketHandshake::new),
    STATUS(PacketStatusRequest::new, PacketPing::new),
    LOGIN(PacketLoginStart::new);

    private final Supplier<? extends ClientPacket>[] client;

    @SuppressWarnings("unchecked")
    ProtocolState(Supplier<? extends ClientPacket>... packets) {
        this.client = packets;
    }

    public ClientPacket getPacket(int id) {
        return id < this.client.length && this.client[id] != null ? this.client[id].get() : null;
    }

}
