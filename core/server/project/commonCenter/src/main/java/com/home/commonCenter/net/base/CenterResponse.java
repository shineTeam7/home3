package com.home.commonCenter.net.base;

import com.home.commonCenter.net.centerRequest.system.SendCenterReceiptToClientRequest;
import com.home.shine.global.ShineSetting;

/** 中心服消息 */
public abstract class CenterResponse extends CenterServerResponse
{
	/** 角色ID */
	public long playerID;
	
	public CenterResponse()
	{
		setNeedFullRead(ShineSetting.clientMessageUseFull);
	}
	
	@Override
	protected void preExecute()
	{
		super.preExecute();
		////不在线
		//if((me=CenterC.main.getPlayerByID(playerID))==null)
		//{
		//	return;
		//}
		//
		////切换中
		//if(me.system.isSwitching())
		//{
		//	me.system.addCacheResponse(this);
		//}
		//else
		//{
		//	doExecute();
		//}
	}
	
	@Override
	protected void sendReceipt()
	{
		socket.send(SendCenterReceiptToClientRequest.create(getDataID()));
	}
}
