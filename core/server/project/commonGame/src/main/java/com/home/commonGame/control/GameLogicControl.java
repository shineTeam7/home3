package com.home.commonGame.control;

import com.home.commonBase.data.mail.AddMailOWData;
import com.home.commonBase.data.mail.MailData;
import com.home.commonBase.data.social.chat.RoleChatData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.table.table.ActivationCodeTable;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.request.social.chat.SendPlayerChatRequest;
import com.home.shine.control.ThreadControl;

/** game逻辑控制 */
public class GameLogicControl
{
	public void init()
	{
	
	}
	
	/** 发送邮件(给其他玩家)(主线程) */
	public void sendMailToPlayer(long playerID,MailData data)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		AddMailOWData oData=new AddMailOWData();
		oData.data=data;
		GameC.main.addPlayerOfflineWork(playerID,oData);
	}
	
	/** 使用激活码 */
	public void useActivationCode(String code)
	{
		ActivationCodeTable table=BaseC.factory.createActivationCodeTable();
		table.code=code;
		
		//table.load(b->
		//{
		//
		//});
	}
	
	/** 推送聊天到所有在线玩家(主线程) */
	public void sendChatToAllPlayer(RoleChatData cData,int channel)
	{
		SendPlayerChatRequest request=SendPlayerChatRequest.create(cData,channel,0);
		request.write();
		GameC.main.radioAllPlayer(request);
	}
}
