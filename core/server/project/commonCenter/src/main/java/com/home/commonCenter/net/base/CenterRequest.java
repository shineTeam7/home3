package com.home.commonCenter.net.base;

import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.serverRequest.game.system.CenterTransCenterToGameServerRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.global.ShineSetting;

/** 中心服消息 */
public class CenterRequest extends CenterServerRequest
{
	public CenterRequest()
	{
		setNeedFullWrite(ShineSetting.clientMessageUseFull);
	}
	
	@Override
	protected void doWriteToStream(BytesWriteStream stream)
	{
		//直接协议号和内容
		stream.natureWriteUnsignedShort(_dataID);
		doWriteBytesSimple(stream);
	}
	
	/** 发送给玩家(主线程用) */
	public void sendToPlayer(long playerID)
	{
		preSend();
		
		CenterTransCenterToGameServerRequest cr=CenterTransCenterToGameServerRequest.create();
		cr.setData(playerID,this);
		cr.sendToGame(CenterC.main.getNowGameIDByLogicID(playerID));
	}
}
