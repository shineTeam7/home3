package com.home.commonGame.net.base;

import com.home.commonGame.control.LogicExecutor;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.serverRequest.game.system.SendGameRequestToPlayerServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.thread.AbstractThread;

public class GameRequest extends BaseRequest
{
	/** 是否需要转发 */
	private boolean _needTrans;
	
	private byte[] _data;
	
	public GameRequest()
	{
		setNeedFullWrite(ShineSetting.clientMessageUseFull);
	}
	
	@Override
	protected void doWriteToStream(BytesWriteStream stream)
	{
		if(_needTrans)
		{
			stream.setPosition(0);
			//直接协议号和内容
			stream.natureWriteUnsignedShort(_dataID);
			doWriteBytesSimple(stream);
			
			_data=stream.getByteArray();
		}
		else
		{
			super.doWriteToStream(stream);
		}
	}
	
	/** 发送到角色(如switch中，阻塞) */
	public void sendToPlayer(long playerID)
	{
		AbstractThread currentShineThread=ThreadControl.getCurrentShineThread();
		
		if(currentShineThread!=null)
		{
			if(currentShineThread.type==ThreadType.Main)
			{
				toSendToPlayerOnMain(playerID);
			}
			else if(currentShineThread.type==ThreadType.Pool)
			{
				LogicExecutor executor=GameC.main.getExecutor(currentShineThread.index);
				
				Player player=executor.getPlayer(playerID);
				
				if(player!=null)
				{
					player.send(this);
					return;
				}
				
				ThreadControl.addMainFunc(()->
				{
					toSendToPlayerOnMain(playerID);
				});
			}
			else
			{
				Ctrl.throwError("不支持的线程类型");
			}
		}
		else
		{
			Ctrl.throwError("不支持的线程类型");
		}
	}
	
	private void toSendToPlayerOnMain(long playerID)
	{
		Player player=GameC.main.getPlayerByID(playerID);
		
		if(player!=null)
		{
			player.send(this);
			return;
		}
		
		_needTrans=true;
		
		write();
		
		SendGameRequestToPlayerServerRequest.create(_data).sendToPlayer(playerID);
	}
}
