package com.home.commonGame.part.player.base;

import com.home.commonBase.baseData.BaseClientPartData;
import com.home.commonBase.baseData.BasePart;
import com.home.commonBase.data.login.PlayerLoginToEachGameData;
import com.home.commonBase.data.login.RePlayerLoginFromEachGameData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.data.login.PlayerSwitchGameData;
import com.home.commonGame.part.player.Player;

/** 角色部件基础 */
public abstract class PlayerBasePart extends BasePart
{
	/** 角色自己 */
	public Player me;
	
	/** 设置主 */
	public void setMe(Player player)
	{
		me=player;
	}
	
	/** 构建clientPartData */
	public BaseClientPartData makeClientPartData()
	{
		beforeMakeData();
		
		BaseClientPartData clientData=createClientData();
		
		if(CommonSetting.isClientPartDataInitDefault)
		{
			clientData.initDefault();
		}
		
		clientData.copyFromServer(getData());
		
		writeClientData(clientData);
		
		return clientData;
	}
	
	/** 构造客户端数据 */
	protected abstract BaseClientPartData createClientData();
	
	/** 写客户端数据(copyServer过后的) */
	protected abstract void writeClientData(BaseClientPartData data);
	
	
	/** 功能开启 */
	public abstract void onFunctionOpen(int id);
	
	/** 功能关闭 */
	public abstract void onFunctionClose(int id);
	
	/** 离线时间 */
	public void onOfflineTime(long delay)
	{
	
	}
	
	/** 登录前(主线程)(beforeLogin在beforeEnter前) */
	public void beforeLoginOnMain()
	{
	
	}
	
	/** 进入前(登录和切到game都算)(主线程)(beforeLogin在beforeEnter前) */
	public void beforeEnterOnMain()
	{
	
	}
	
	/** 登录前(beforeLogin在beforeEnter前) */
	public void beforeLogin()
	{
	
	}
	
	/** 进入前(登录和切到game都算)(beforeLogin在beforeEnter前) */
	public void beforeEnter()
	{
	
	}
	
	/** 初次登录(一个角色一生只调用一次) */
	public void onFirstLogin()
	{
	
	}
	
	/** 登录时 */
	public void onLogin()
	{
	
	}
	
	/** 进入时 */
	public void onEnter()
	{
	
	}
	
	/** 离开时 */
	public void onLeave()
	{
	
	}
	
	/** 登出时(与离开不同) */
	public void onLogout()
	{
	
	}
	
	/** 重连登录前 */
	public void beforeReconnectLogin()
	{
	
	}
	
	/** 重连登录 */
	public void onReconnectLogin()
	{
	
	}
	
	/** 登出时(主线程) */
	public void onLogoutOnMain()
	{
	
	}
	
	/** 等级提升 */
	public void onLevelUp(int oldLevel)
	{
	
	}
	
	/** 角色登陆逻辑服数据 */
	public void makeLoginEachGameData(PlayerLoginToEachGameData data)
	{
	
	}
	
	/** 登录前(收到各个逻辑服返回数据时)(主线程) */
	public void beforeLoginForEachGame(RePlayerLoginFromEachGameData data)
	{
	
	}
	
	/** 写切换数据 */
	public void writeSwitchData(PlayerSwitchGameData data)
	{
	
	}
	
	/** 读切换数据 */
	public void readSwitchData(PlayerSwitchGameData data)
	{
	
	}
	
	/** 删除该角色 */
	public void onDeletePlayer()
	{
	
	}
	
	/** 统计该模块的战斗力 */
	public long countFightForce()
	{
		return 0L;
	}
}
