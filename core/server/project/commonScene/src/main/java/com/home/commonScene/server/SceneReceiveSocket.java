package com.home.commonScene.server;

import com.home.commonScene.part.ScenePlayer;
import com.home.shine.net.socket.ReceiveSocket;

public class SceneReceiveSocket extends ReceiveSocket
{
	/** 绑定的player(不必volatile) */
	public ScenePlayer player;
	
	public void setPlayer(ScenePlayer value)
	{
		this.player=value;
	}
	
	/** 接绑player(主线程) */
	public void unbindPlayer()
	{
		setPlayer(null);
	}
}
