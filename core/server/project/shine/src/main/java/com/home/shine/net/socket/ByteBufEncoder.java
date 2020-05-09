package com.home.shine.net.socket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/** 自定义Encoder(TCP用) */
public class ByteBufEncoder extends MessageToByteEncoder<ByteBuf>
{
	@Override
	protected void encode(ChannelHandlerContext ctx,ByteBuf msg,ByteBuf out) throws Exception
	{
		out.writeBytes(msg);
	}
}