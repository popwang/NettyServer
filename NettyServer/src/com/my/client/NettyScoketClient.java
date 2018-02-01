package com.my.client;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 用于与远程服务器建立连接并定时发送数据
 * @author Administrator
 *
 */
public class NettyScoketClient {
	private static Logger logger = Logger.getLogger(NettyScoketClient.class);
	static final String REMOTE_HOST = "123.15.58.210";
	static final int REMOTE_PORT = 9123;//Common.getPort();
	
	public static void main(String[] args) throws InterruptedException {
	    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
	    try {
	      Bootstrap bootstrap = new Bootstrap();
	      bootstrap.channel(NioSocketChannel.class);
	      bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
	      bootstrap.group(eventLoopGroup);
	      bootstrap.remoteAddress(REMOTE_HOST, REMOTE_PORT);
	      bootstrap.handler(new ChannelInitializer<SocketChannel>() {
	        @Override
	        protected void initChannel(SocketChannel socketChannel)
	            throws Exception {			
	          socketChannel.pipeline().addLast(new DataSendHandler());
	        }
	      });
	      
	      ChannelFuture future = bootstrap.connect(REMOTE_HOST, REMOTE_PORT).sync();
	      if (future.isSuccess()) {
    		future.channel();
    		logger.info("connect remote server success!");
	      }
	      future.channel().closeFuture().sync();
	    } finally {
	      eventLoopGroup.shutdownGracefully();
	    }
	  }
}
