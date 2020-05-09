package com.home.commonGame.control;

import com.home.commonBase.config.game.CallWayConfig;
import com.home.commonBase.data.item.ItemData;
import com.home.commonGame.global.GameC;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.part.player.part.RolePart;
import com.home.commonGame.server.GameReceiveSocket;
import com.home.shine.constlist.SLogType;
import com.home.shine.control.DateControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.LogInfo;

/** 日志控制 */
public class GameLogControl
{
	/** 创建ActionLog信息 */
	protected LogInfo createActionLogInfo(String type)
	{
		LogInfo info=new LogInfo("action");
		//当前gameID
		info.put("gameID",GameC.app.id);
		//unix时间戳
		info.put("time",DateControl.getTimeMillis());
		//类型
		info.put("action",type);
		
		return info;
	}
	
	/** 添加行为日志 */
	protected void addActionLog(LogInfo info)
	{
		Ctrl.toLog(info.releaseString(),SLogType.Action,1);
	}
	
	/** 添加流程日志 */
	public void addPathLog(String uid,String name,int playerID,int step)
	{
		
	}
	
	/** 写入角色 */
	public void putPlayer(LogInfo info,Player player)
	{
		RolePart role=player.role;
		info.put("uid",role.uid);
		info.put("playerID",role.playerID);
		info.put("name",role.name);
		info.put("sourceGameID",role.sourceGameID);
		
		GameReceiveSocket socket=player.system.socket;
		info.put("ip",socket!=null ? socket.remoteIP() : "");
	}
	
	/** 写入物品 */
	public void putItemData(LogInfo info,ItemData data)
	{
	
	}
	
	//详细部分
	
	/** 游戏进入(第一条) */
	public void gameEnter(String uid,String platform,String ip)
	{
		LogInfo info=createActionLogInfo("gameEnter");
		info.put("uid",uid);
		info.put("platform",platform);
		info.put("ip",ip);
		
		addActionLog(info);
	}
	
	/** 账号登录(第二条) */
	public void userLogin(String uid,String platform,String ip)
	{
		LogInfo info=createActionLogInfo("userLogin");
		info.put("uid",uid);
		info.put("platform",platform);
		info.put("ip",ip);
		
		addActionLog(info);
	}
	
	/** 创建角色 */
	public void createPlayer(Player player)
	{
		LogInfo info=createActionLogInfo("createPlayer");
		putPlayer(info,player);
		
		addActionLog(info);
	}
	
	/** 角色获得货币 */
	public void playerAddCurrency(Player player,int type,long value,int way)
	{
		LogInfo info=createActionLogInfo("addCurrency");
		putPlayer(info,player);
		info.put("type",type);
		info.put("value",value);
		info.put("way",way);
		info.put("wayDescribe",CallWayConfig.get(way).describe);
		
		addActionLog(info);
	}
	
	/** 角色消费货币 */
	public void playerCostCurrency(Player player,int type,long value,int way)
	{
		LogInfo info=createActionLogInfo("costCurrency");
		putPlayer(info,player);
		info.put("type",type);
		info.put("value",value);
		info.put("way",way);
		info.put("wayDescribe",CallWayConfig.get(way).describe);
		
		addActionLog(info);
	}
	
	/** 角色获得物品 */
	public void playerAddItem(Player player,int itemID,int num,int way)
	{
		LogInfo info=createActionLogInfo("addItem");
		putPlayer(info,player);
		info.put("itemID",itemID);
		info.put("num",num);
		info.put("way",way);
		info.put("wayDescribe",CallWayConfig.get(way).describe);
		
		addActionLog(info);
	}
	
	/** 角色移除物品 */
	public void playerRemoveItem(Player player,int itemID,int num,int way)
	{
		LogInfo info=createActionLogInfo("removeItem");
		putPlayer(info,player);
		info.put("itemID",itemID);
		info.put("num",num);
		info.put("way",way);
		info.put("wayDescribe",CallWayConfig.get(way).describe);
		
		addActionLog(info);
	}
	
	/** 角色获得物品时添加到邮件 */
	public void playerAddItemToMail(Player player,int itemID,int num,int way)
	{
		LogInfo info=createActionLogInfo("addItemToMail");
		putPlayer(info,player);
		info.put("itemID",itemID);
		info.put("num",num);
		info.put("way",way);
		info.put("wayDescribe",CallWayConfig.get(way).describe);
		
		addActionLog(info);
	}
}
