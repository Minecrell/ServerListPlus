package net.minecrell.serverlistplus.server.network.protocol.packet;

import io.netty.channel.ChannelHandlerContext;

public interface PacketHandler {

    void handle(ChannelHandlerContext ctx, PacketHandshake packet);
    void handle(ChannelHandlerContext ctx, PacketStatusRequest packet);
    void handle(ChannelHandlerContext ctx, PacketPing packet);
    void handle(ChannelHandlerContext ctx, PacketLoginStart packet);

}
