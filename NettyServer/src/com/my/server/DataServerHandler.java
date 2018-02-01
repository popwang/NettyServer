package com.my.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.my.database.DataSource;
import com.my.vo.EquipmentData;
import com.my.vo.OrderBufferVo;

public class DataServerHandler extends ChannelInboundHandlerAdapter {
	private static Logger logger = Logger.getLogger(DataServerHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		logger.info(ctx.channel().remoteAddress().toString() + " 客户端连接成功!");
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		logger.info(ctx.channel().remoteAddress().toString() + " 客户端断开连接！");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// ByteBuf buf = (ByteBuf)msg;
		// String str = this.getMessage(buf);
		String str = ((String) msg).trim();
		str += ",END";
		String v_equipment_name = "";
		if (str != null && !"".equals(str)) {
			logger.info(ctx.channel().remoteAddress().toString() + ":" + str);
			/**
			 * 以SEND开头为有效数据
			 */
			if (str.startsWith("SEND") && str.endsWith("END")) {
				String[] arr = str.split(",");
				EquipmentData ed = new EquipmentData();
				ed.setV_equipment_name(arr[2]);
				v_equipment_name = arr[2];
				ed.setParam(1, Double.parseDouble(arr[3]));
				ed.setParam(2, Double.parseDouble(arr[4]));
				ed.setParam(3, Double.parseDouble(arr[5]));
				ed.setParam(4, Double.parseDouble(arr[6]));
				ed.setParam(5, Double.parseDouble(arr[7]));
				ed.setParam(6, Double.parseDouble(arr[8]));
				ed.setParam(7, Double.parseDouble(arr[9]));
				ed.setParam(8, Double.parseDouble(arr[10]));
				ed.setParam(9, Double.parseDouble(arr[11]));
				ed.setParam(10, Double.parseDouble(arr[12]));
				ed.setParam(11, Double.parseDouble(arr[13]));
				ed.setParam(12, Double.parseDouble(arr[14]));
				ed.setParam(13, Double.parseDouble(arr[15]));
				ed.setParam(14, Double.parseDouble(arr[16]));
				ed.setParam(15, Double.parseDouble(arr[17]));
				// 新增业务字段
				if (arr.length == 34) {
					logger.info("新设备接入：");
					ed.setParam(16, Double.parseDouble(arr[18]));
					ed.setParam(17, Double.parseDouble(arr[19]));
					ed.setParam(18, Double.parseDouble(arr[20]));
					ed.setParam(19, Double.parseDouble(arr[21]));
					ed.setParam(20, Double.parseDouble(arr[22]));
					ed.setParam(21, Double.parseDouble(arr[23]));
					ed.setParam(22, Double.parseDouble(arr[24]));
					ed.setParam(23, Double.parseDouble(arr[25]));
					ed.setParam(24, Double.parseDouble(arr[26]));
					ed.setParam(25, Double.parseDouble(arr[27]));
					ed.setParam(26, Double.parseDouble(arr[28]));
					ed.setParam(27, Double.parseDouble(arr[29]));
					ed.setParam(28, Double.parseDouble(arr[30]));
					ed.setParam(29, Double.parseDouble(arr[31]));
					ed.setParam(30, Double.parseDouble(arr[32]));
				}
				DataSource.insertEquipmentData(ed);
				DataSource.deleteAndInsertData2(ed);
			}
		} else {
			logger.info("没有数据...");
		}
		
		OrderBufferVo order = DataSource.selectSendBufferTable(v_equipment_name);
		if (order != null) {
			// 如果需要回写客户端，需要转成字节写入buff
			ByteBuf buf = Unpooled
					.copiedBuffer(new String(order.getV_order_content().getBytes("gb2312"), "GB2312").getBytes());
			ctx.write(buf);
			DataSource.updateSendBufferById(order.getI_id().intValue());
			logger.info("设备编号为：" + order.getV_equipment_name() + "的第" + order.getI_id() + "号指令已发送成功！");
		}
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
		logger.info(ctx.channel().remoteAddress().toString() + "连接已移除！");
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
		logger.info(ctx.channel().remoteAddress().toString() + "连接已关闭！");
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
