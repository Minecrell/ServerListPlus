package net.minecrell.serverlistplus.server.network;

import static net.minecrell.serverlistplus.server.network.protocol.MinecraftProtocol.PROTOCOL_STATE;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecrell.serverlistplus.server.ServerListPlusServer;
import net.minecrell.serverlistplus.server.network.protocol.MinecraftDecoder;
import net.minecrell.serverlistplus.server.network.protocol.MinecraftProtocol;
import net.minecrell.serverlistplus.server.network.protocol.ProtocolState;
import net.minecrell.serverlistplus.server.network.protocol.Varint21FrameDecoder;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public final  class NetworkManager extends ChannelInitializer<Channel> {

    private static final Logger logger = Logger.getLogger(NetworkManager.class.getName());

    private final ServerListPlusServer server;
    private final InetSocketAddress address;
    private Channel channel;
    private EventLoopGroup bossGroup, workerGroup;

    public NetworkManager(ServerListPlusServer server, InetSocketAddress address) {
        this.server = server;
        this.address = address;
    }

    public ServerListPlusServer getServer() {
        return this.server;
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = Netty.createEventLoopGroup();
        EventLoopGroup workerGroup = Netty.createEventLoopGroup();

        this.channel = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(Netty.getServerChannel())
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 1024 * 1024 * 10)
                .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 1024 * 1024)
                .childAttr(PROTOCOL_STATE, ProtocolState.HANDSHAKE)
                .childHandler(this)
                .bind(this.address)
                .sync().channel();

        logger.log(Level.INFO, "Listening on {0}", this.channel);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast("logger", MinecraftProtocol.LOGGER_HANDLER)
                .addLast("timeout", new ReadTimeoutHandler(30))
                .addLast("legacy", new LegacyClientHandler())
                .addLast("frame_decoder", new Varint21FrameDecoder())
                .addLast("packet_decoder", new MinecraftDecoder())
                .addLast("length_prepender", MinecraftProtocol.LENGTH_PREPENDER)
                .addLast("packet_encoder", MinecraftProtocol.PACKET_ENCODER)
                .addLast("packet_handler", new ClientHandler());
    }

    public void join() throws InterruptedException {
        this.channel.closeFuture().sync();
    }

    public void stop() throws Exception {
        try {
            if (this.channel != null) {
                this.channel.close().sync();
            }
        } finally {
            if (this.bossGroup != null) {
                this.bossGroup.shutdownGracefully();
                this.bossGroup = null;
            }
            if (this.workerGroup != null) {
                this.workerGroup.shutdownGracefully();
                this.workerGroup = null;
            }
        }
    }

}
