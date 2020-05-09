package com.home.commonClient.net.httpResponse;

import com.home.commonClient.global.ClientC;
import com.home.commonClient.part.player.Player;
import com.home.shine.ShineSetup;
import com.home.shine.agent.AgentControl;
import com.home.shine.constlist.HttpContentType;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.httpResponse.BaseHttpResponse;

public class ClientMainHttpResponse extends BaseHttpResponse
{
	@Override
	public void dispatch()
	{
		_threadType=ThreadType.Main;
		
		super.dispatch();
	}
	
	@Override
	protected void execute()
	{
		switch(cmd)
		{
			case "exit":
			{
				Ctrl.log("收到客户端服关闭消息","IP:",_http.remoteIP());
				
				ThreadControl.getMainTimeDriver().callLater(ShineSetup::exit);
			}
				break;
			case "agent":
			{
				result(AgentControl.agentClass());
			}
				break;
			//更新重定向
			case "cmd":
			{
				doCMD();
				
				result(true,HttpContentType.Text);
			}
				break;
			default:
			{
				result(false,HttpContentType.Text);
				return;
			}
		}
		
		result(true,HttpContentType.Text);
	}
	
	private void doCMD()
	{
		Player player;
		
		if(args.contains("uid"))
		{
			player=ClientC.main.getPlayerByUID(getArgStr("uid"));
		}
		else
		{
			player=ClientC.main.getUniquePlayer();
		}
		
		if(player!=null)
		{
			String gm=getArgStr("gm");
			gm=gm.replace('_',' ');
			player.sendGM(gm);
		}
	}
}
