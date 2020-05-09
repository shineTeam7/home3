using System;
using System.Text;
using ShineEngine;

/// <summary>
/// 游戏服日志控制
/// </summary>
public class GameLogControl
{
	public void init()
	{
		GameC.mainLogin.setGameSendLogFunc(toSendLog);
	}

	private static bool toSendLog(int type,string str)
	{
		if(CommonSetting.isSingleGame)
			return true;

		//连着的时候
		if(GameC.player.system.isOnline() && SystemControl.isNetOpen() && GameC.server.getSocket().isConnect())
		{
			try
			{
				Ctrl.setLogPause(true);
				SendClientLogRequest.create(type,str).send(false);
			}
			catch(Exception e)
			{

			}

			Ctrl.setLogPause(false);
			return true;
		}

		return false;
	}

	/** 添加流程日志 */
	public void addFlowLog(int step)
	{
		int saveStep=GameC.save.getInt(LocalSaveType.FLowStep);

		if(step>saveStep)
		{
			string uid=GameC.main!=null ? GameC.main.uid : "";

			if(SystemControl.isNetOpen() && !uid.isEmpty())
			{
				SendHttpClientFlowStepHttpRequest.create(uid,step).send();
				GameC.save.setInt(LocalSaveType.FLowStep,step);
			}
		}
	}

	/** 创建ActionLog信息 */
	protected LogInfo createActionLogInfo(String type)
	{
		LogInfo info=new LogInfo("action");
		//当前区ID
		info.put("areaID",GameC.main.areaID);
		//unix时间戳
		info.put("time",DateControl.getTimeMillis());
		//类型
		info.put("action","type");

		return info;
	}

	/** 添加行为日志 */
	private void addLog(string str)
	{
		//TODO:ZZ日志输出
	}

	/** 添加行为日志 */
	private void addActionLog(LogInfo info)
	{
		addLog(info.releaseString());
	}

	/** 游戏服日志 */
	public void gameActionLog(params string[] args)
	{
		StringBuilder sb=StringBuilderPool.create();

		sb.Append("action");
		sb.Append(' ');

		for(int i=0,len=args.Length;i<len;i+=2)
		{
			if(i>0)
			{
				sb.Append('&');
			}

			sb.Append(args[i]);
			sb.Append('=');
			sb.Append(args[i+1]);
		}

		addLog(StringBuilderPool.releaseStr(sb));
	}

	/** 写入角色 */
	public void putPlayer(LogInfo info,Player player)
	{
		RolePart role=player.role;
		info.put("uid",role.uid);
		info.put("playerID",role.playerID);
		info.put("name",role.name);
		info.put("sourceGameID",role.getPartData().createAreaID);

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
		if(!CommonSetting.isClientDriveLogic)
			return;

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
		if(!CommonSetting.isClientDriveLogic)
			return;

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
		if(!CommonSetting.isClientDriveLogic)
			return;

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
		if(!CommonSetting.isClientDriveLogic)
			return;

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
		if(!CommonSetting.isClientDriveLogic)
			return;

		LogInfo info=createActionLogInfo("addItemToMail");
		putPlayer(info,player);
		info.put("itemID",itemID);
		info.put("num",num);
		info.put("way",way);
		info.put("wayDescribe",CallWayConfig.get(way).describe);

		addActionLog(info);
	}
}