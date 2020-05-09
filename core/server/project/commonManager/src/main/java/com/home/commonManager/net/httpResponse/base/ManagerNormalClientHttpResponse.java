package com.home.commonManager.net.httpResponse.base;

import com.home.commonBase.config.game.ActivationCodeConfig;
import com.home.commonBase.constlist.system.ManagerCommandType;
import com.home.commonBase.global.BaseC;
import com.home.commonManager.global.ManagerC;
import com.home.commonManager.net.serverRequest.center.ManagerToCenterCommandServerRequest;
import com.home.commonManager.net.serverRequest.game.ManagerToGameCommandServerRequest;
import com.home.commonManager.net.serverRequest.login.login.HotfixToLoginServerRequest;
import com.home.commonManager.net.serverRequest.login.system.ManagerToLoginCommandServerRequest;
import com.home.shine.ShineSetup;
import com.home.shine.agent.AgentControl;
import com.home.shine.constlist.HttpContentType;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.httpResponse.BaseHttpResponse;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.utils.StringUtils;

/** 通用manager服客户端gm请求 */
public class ManagerNormalClientHttpResponse extends BaseHttpResponse
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
				Ctrl.log("收到管理服关闭消息","IP:",_http.remoteIP());
				
				ThreadControl.getMainTimeDriver().callLater(ShineSetup::exit);
			}
				break;
			case "exitServer":
			{
				Ctrl.log("收到管理服关闭服务器消息","IP:",_http.remoteIP());
				
				ManagerC.main.exitServer();
			}
				break;
			case "agent":
			{
				//id
				String node=_http.args.get("node");
				int id=_http.getIntArg("id");
				
				Ctrl.log("收到管理服agent消息","IP:",_http.remoteIP(),node,id);
				
				switch(node)
				{
					case "all":
					{
						ManagerToCenterCommandServerRequest.create(ManagerCommandType.Agent).send();
						
						ManagerC.server.radioGames(ManagerToGameCommandServerRequest.create(ManagerCommandType.Agent));
						ManagerC.server.radioLogins(ManagerToLoginCommandServerRequest.create(ManagerCommandType.Agent));
						
						result(AgentControl.agentClass());
						
						return;
					}
					case "manager":
					{
						result(AgentControl.agentClass());
						return;
					}
					case "center":
					{
						ManagerToCenterCommandServerRequest.create(ManagerCommandType.Agent).send();
					}
						break;
					case "game":
					{
						ManagerC.server.getGameSocket(id).send(ManagerToGameCommandServerRequest.create(ManagerCommandType.Agent));
					}
						break;
					case "login":
					{
						ManagerC.server.getLoginSocket(id).send(ManagerToLoginCommandServerRequest.create(ManagerCommandType.Agent));
					}
						break;
				}
			}
				break;
			case "hotfix":
			{
				//热更新
				ManagerC.main.clientHotfix(_http.getBooleanArg("hasConfig"));
			}
				break;
			case "reload":
			case "reloadConfig":
			{
				ManagerC.main.reloadConfig(null);
			}
				break;
			//重新加载server配置
			case "reloadServerConfig":
			{
				ServerConfig.load();
			}
				break;
			//更新重定向
			case "redirect":
			{
				ManagerC.setting.load();
				ManagerC.server.radioLogins(HotfixToLoginServerRequest.create(ManagerC.setting.clientVersionDic,ManagerC.setting.redirectURLDic));
			}
				break;
			case "open":
			{
				ManagerC.main.changeOpen(true);
			}
				break;
			case "close":
			{
				ManagerC.main.changeOpen(false);
			}
				break;
			case "makeActivationCode":
			{
				//id
				int id=_http.getIntArg("id");
				//产生数目
				int num=_http.hasArg("num") ? _http.getIntArg("num") : 1;
				//可使用次数
				int useNum=_http.getIntArg("useNum");
				//失效时间
				long disableTime=_http.getLongArg("disableTime");
				//批次数目
				int batchNum=_http.hasArg("batchNum") ? _http.getIntArg("batchNum") : 1;
				
				//没有
				if(ActivationCodeConfig.get(id)==null)
				{
					result("不存在的id:"+id,HttpContentType.Text);
					return;
				}
				
				ManagerC.logic.makeActivationCode(id,num,disableTime,useNum,batchNum,arr->
				{
					if(arr==null)
					{
						result(false,HttpContentType.Text);
					}
					else
					{
						result(StringUtils.join(arr,"\n"),HttpContentType.Text);
					}
				});
				
				return;
			}
			default:
			{
				result(false,HttpContentType.Text);
				return;
			}
		}
		
		result(true,HttpContentType.Text);
	}
}
