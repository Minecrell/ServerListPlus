/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.server.network;

import static net.minecrell.serverlistplus.server.network.protocol.MinecraftProtocol.PROTOCOL_STATE;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecrell.serverlistplus.core.status.StatusRequest;
import net.minecrell.serverlistplus.server.ServerListPlusServer;
import net.minecrell.serverlistplus.server.network.protocol.ProtocolState;
import net.minecrell.serverlistplus.server.network.protocol.packet.ClientPacket;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketHandler;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketHandshake;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketKick;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketLoginStart;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketPing;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketStatusRequest;
import net.minecrell.serverlistplus.server.network.protocol.packet.PacketStatusResponse;
import net.minecrell.serverlistplus.server.status.StatusClient;

import java.net.InetSocketAddress;

public class ClientHandler extends ChannelInboundHandlerAdapter implements PacketHandler {

    private final StatusClient client = new StatusClient();

    public static ProtocolState getState(ChannelHandlerContext ctx) {
        return ctx.channel().attr(PROTOCOL_STATE).get();
    }

    public static void setState(ChannelHandlerContext ctx, ProtocolState state) {
        ctx.channel().attr(PROTOCOL_STATE).set(state);
    }

    private void checkState(ChannelHandlerContext ctx, ProtocolState state) {
        Preconditions.checkState(getState(ctx) == state, "Not expecting state %s", state);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.client.setAddress((InetSocketAddress) ctx.channel().remoteAddress());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, PacketHandshake packet) {
        checkState(ctx, ProtocolState.HANDSHAKE);
        this.client.setProtocol(packet.getProtocolVersion());
        this.client.setVirtualHost(InetSocketAddress.createUnresolved(StatusRequest.cleanVirtualHost(packet.getHost()), packet.getPort()));
        setState(ctx, packet.getNextState());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, PacketStatusRequest packet) {
        checkState(ctx, ProtocolState.STATUS);
        ctx.writeAndFlush(new PacketStatusResponse(ServerListPlusServer.post(this.client)));
    }

    @Override
    public void handle(ChannelHandlerContext ctx, PacketPing packet) {
        checkState(ctx, ProtocolState.STATUS);
        ctx.writeAndFlush(packet).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void handle(ChannelHandlerContext ctx, PacketLoginStart packet) {
        checkState(ctx, ProtocolState.LOGIN);
        ctx.writeAndFlush(new PacketKick(ServerListPlusServer.postLogin(this.client, packet.getName()))).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClientPacket packet = (ClientPacket) msg;
        packet.handle(ctx, this);
    }

}
