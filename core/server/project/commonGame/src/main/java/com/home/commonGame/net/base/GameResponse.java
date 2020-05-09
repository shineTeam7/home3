package com.home.commonGame.net.base;

import com.home.commonBase.global.CommonSetting;
import com.home.commonGame.net.request.system.SendGameReceiptToClientRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.server.GameReceiveSocket;
import com.home.shine.constlist.ThreadType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseResponse;

/** 客户端消息推送 */
public abstract class GameResponse extends BaseResponse
{
	/** 主角 */
	protected Player me;
	
	/** 需要的功能ID */
	protected int _needFunctionID=-1;
	
	public GameResponse()
	{
		//标记池线程
		_threadType=ThreadType.Pool;
		setNeedFullRead(ShineSetting.clientMessageUseFull);
	}
	
	/** 设置所需功能ID */
	protected void setNeedFunctionID(int funcID)
	{
		_needFunctionID=funcID;
	}
	
	@Override
	public void dispatch()
	{
		Player player;
		
		if((player=((GameReceiveSocket)socket).player)==null)
		{
			doPlayerNull();
		}
		else
		{
			setPlayer(player);
			
			//主线程的
			if(_threadType==ThreadType.Main)
			{
				//主线程
				player.addMainFunc(this);
			}
			else
			{
				//派发
				player.addFunc(this);
			}
		}
	}
	
	protected void setPlayer(Player player)
	{
		me=player;
	}
	
	/** 执行player为空的情况 */
	protected void doPlayerNull()
	{
		//默认不执行
	}
	
	@Override
	protected void doExecute()
	{
		if(CommonSetting.needClientResponseFunctionLimit && _needFunctionID>0 && !me.func.isFunctionOpen(_needFunctionID))
		{
			me.warnLog("收到Response:"+_dataID+"时,所需功能functionID:"+_needFunctionID+"未开启");
			return;
		}
		
		super.doExecute();
	}
	
	@Override
	protected void sendReceipt()
	{
		socket.send(SendGameReceiptToClientRequest.create(getDataID()));
	}
}
