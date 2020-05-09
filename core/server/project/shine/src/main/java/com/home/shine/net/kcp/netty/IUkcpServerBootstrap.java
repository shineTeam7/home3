package com.home.shine.net.kcp.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;

public interface IUkcpServerBootstrap
{
	void nodelay(boolean nodelay, int interval, int fastResend,boolean nocwnd);
	
	IUkcpServerBootstrap childHandler(ChannelHandler childHandler);
	
	<T> IUkcpServerBootstrap childOption(ChannelOption<T> childOption,T value);
	
	ChannelFuture bind(int inetPort);
}
