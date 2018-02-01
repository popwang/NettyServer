package com.my.client;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DataSendHandler extends ChannelInboundHandlerAdapter{
	private static Logger logger = Logger.getLogger(DataSendHandler.class);
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("连接 "+ctx.channel().remoteAddress().toString()+" 成功!");
		String str = "STX9901|AZ|V1.0|AZ00000001|-1.0|23.0|234.01|123.02|0|45|3.0|ES|AZ00000001ETX";
		
		byte[] data = str.getBytes();
		ByteBuf firstMessage=Unpooled.buffer();
	    firstMessage.writeBytes(data);
	    ctx.writeAndFlush(firstMessage);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		logger.info("从 "+ctx.channel().remoteAddress().toString()+" 断开连接！");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		super.channelRead(ctx, msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	public String getMessage(ByteBuf buf) {
	    byte[] con = new byte[buf.readableBytes()];
	    buf.readBytes(con);
	    try {
	      return new String(con, "UTF-8");
	    } catch (UnsupportedEncodingException e) {
	      e.printStackTrace();
	      return null;
	    }
    }
}
