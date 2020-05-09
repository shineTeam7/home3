package com.home.commonGame.server;

import com.home.commonGame.net.request.scene.scene.AddUnitRequest;
import com.home.commonGame.net.request.system.SendInfoCodeRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.ReceiveSocket;

/** 逻辑服接收链接 */
public class GameReceiveSocket extends ReceiveSocket
{
	/** 绑定的player(不必volatile) */
	public Player player;
	
	public void setPlayer(Player value)
	{
		this.player=value;
	}
	
	/** 接绑player(主线程) */
	public void unbindPlayer()
	{
		setPlayer(null);
	}
	
	/** 推送信息码 */
	public void sendInfoCode(int code)
	{
		send(SendInfoCodeRequest.create(code));
	}
	
	@Override
	public void writeInfo(StringBuilder sb)
	{
		super.writeInfo(sb);
		
		if(player!=null)
		{
			sb.append(" ");
			player.writeInfo(sb);
		}
	}
}
