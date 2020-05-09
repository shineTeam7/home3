package com.home.commonGame.dataEx;

import com.home.commonBase.data.login.ClientLoginData;
import com.home.commonBase.data.login.ClientLoginExData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonGame.server.GameReceiveSocket;
import com.home.shine.dataEx.AffairTimeLock;
import com.home.shine.support.collection.LongSet;
import com.home.shine.support.pool.PoolObject;

/** 角色登录记录数据 */
public class UserLoginRecordData extends PoolObject
{
	//--center登录部分--//
	
	/** 客户端登陆数据 */
	public ClientLoginData data;
	/** 客户端登陆Ex数据 */
	public ClientLoginExData exData;
	
	/** 登陆序号 */
	public int version;
	/** 令牌 */
	public int token;
	/** 登录时间(s) */
	public int loginTime=0;
	
	//--client登录部分--//
	/** socket */
	public GameReceiveSocket socket;
	/** 角色ID组 */
	public LongSet playerIDs=new LongSet();
	
	/** 用户登录计时锁 */
	public AffairTimeLock userLoginLock=new AffairTimeLock();
	/** 创建角色计时锁 */
	public AffairTimeLock creatingLock=new AffairTimeLock();
	/** 角色登录计时锁 */
	public AffairTimeLock playerLoginLock=new AffairTimeLock();
	
	/** 刷新登录时间 */
	public void refreshLoginTime()
	{
		loginTime=CommonSetting.playerLoginTimeMax;
	}
	
	/** 登录结束 */
	public void loginOver()
	{
		loginTime=0;//登录锁关
		userLoginLock.unlock();
		playerLoginLock.unlock();
	}
	
	@Override
	public void clear()
	{
		loginOver();
		playerIDs.clear();
		socket=null;
	}
}
