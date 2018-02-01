package com.my.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.apache.log4j.Logger;

import com.my.util.Common;

public class NettySocketServer {
	private static Logger logger = Logger.getLogger(NettySocketServer.class);
    static final int PORT = Common.getPort();
    static final int POOL_SIZE = Common.getPoolSize();

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, POOL_SIZE)
             .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
             .handler(new LoggingHandler(LogLevel.ERROR))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     ByteBuf delimiter = Unpooled.copiedBuffer(",END".getBytes());
//                     p.addLast(new IdleStateHandler(1,0,1));
                     p.addLast(new DelimiterBasedFrameDecoder(300, delimiter));
                     p.addLast(new StringDecoder());
                     p.addLast(new DataServerHandler());
                 }
             });
            ChannelFuture f = b.bind(PORT).sync();
            logger.info("服务已经启动，端口号 :"+PORT);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
