package net.minecrell.serverlistplus.server.network.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

@ChannelHandler.Sharable
public class LoggerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(LoggerHandler.class.getName());

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.log(Level.INFO, "Client connected {0}", ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.log(Level.INFO, "Client disconnected {0}", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(Level.SEVERE, "FAIL", cause);
    }

}
