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

import com.google.common.base.Splitter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

public final class Netty {

    private static final Splitter HOST_SPLITTER = Splitter.on(':').limit(2).trimResults();

    private static final boolean DISABLE_EPOLL = Boolean.parseBoolean(System.getProperty("epoll.disable"));
    private static final boolean epoll;

    private Netty() {
    }

    static {
        if (!DISABLE_EPOLL && Epoll.isAvailable()) {
            epoll = true;
        } else {
            epoll = false;
        }
    }

    public static boolean isEpoll() {
        return epoll;
    }

    public static EventLoopGroup createEventLoopGroup() {
        return epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    public static Class<? extends ServerChannel> getServerChannel() {
        return epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static InetSocketAddress parseAddress(String address) throws UnknownHostException {
        List<String> parts = HOST_SPLITTER.splitToList(address);
        if (parts.size() != 2)
            throw new IllegalArgumentException(address);

        String host = parts.get(0);
        if (host.isEmpty() || host.equals("*")) host = null;
        int port = Integer.parseInt(parts.get(1));

        InetSocketAddress socket = host != null ? new InetSocketAddress(host, port) : new InetSocketAddress(port);
        if (socket.getAddress() == null)
            throw new UnknownHostException(host);

        return socket;
    }


}
