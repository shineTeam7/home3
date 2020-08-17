package com.home.commonClient.net.base;

import com.home.commonClient.part.player.Player;
import com.home.shine.constlist.ThreadType;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseResponse;

public abstract class SceneResponse extends BaseResponse
{
	/** 主角 */
	public Player me;
	
	public SceneResponse()
	{
		setNeedFullRead(ShineSetting.clientMessageUseFull);
	}
	
	@Override
	public void dispatch()
	{
		_threadType=ThreadType.Pool;
		//取角色的线程序号
		_poolIndex=me.system.executorIndex;
		
		super.dispatch();
	}
	
	/** 设置主角 */
	public void setPlayer(Player player)
	{
		me=player;
	}
	
	@Override
	protected void preExecute()
	{
		if(me!=null)
		{
			me.system.recordResponse(this);
		}
		
		super.preExecute();
	}
}
