package com.home.commonGMClient.control;

import com.home.commonBase.constlist.system.WorkType;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonGMClient.net.httpRequest.role.MAddPlayerWorkHttpRequest;
import com.home.commonGMClient.net.httpRequest.role.MPlayerQueryWorkHttpRequest;
import com.home.commonGMClient.net.httpRequest.role.MQueryPlayerHttpRequest;
import com.home.commonGMClient.net.httpRequest.system.GMClientLoginHttpRequest;
import com.home.commonGMClient.net.httpResponseResult.role.MAddPlayerWorkResult;
import com.home.commonGMClient.net.httpResponseResult.role.MPlayerQueryWorkResult;
import com.home.shine.control.DateControl;
import com.home.shine.data.BaseData;

public class GMClientMainControl
{
	/** 令牌 */
	private int _token;
	
	/** 初始化 */
	public void init()
	{
		GMClientLoginHttpRequest.create("root","").sendToManager();
	}
	
	/** 获取令牌 */
	public int getToken()
	{
		return _token;
	}
	
	/** 登陆成功 */
	public void loginSuccess(int token)
	{
		_token=token;
		
		onLogin();
	}
	
	protected void onLogin()
	{
	
	}
	
	/** 添加角色离线事务(同步) */
	public boolean addPlayerOfflineWorkSync(PlayerWorkData data)
	{
		data.workType=WorkType.PlayerOffline;
		
		MAddPlayerWorkResult result=MAddPlayerWorkHttpRequest.create(_token,data).sendMSync();
		
		if(result==null)
			return false;
		
		return result.success;
	}
	
	/** 角色查询事务 */
	public BaseData queryPlayerWork(PlayerWorkData data)
	{
		data.workType=WorkType.PlayerAbs;
		
		MPlayerQueryWorkResult result=MPlayerQueryWorkHttpRequest.create(_token,data).sendMSync();
		
		if(result==null)
			return null;
		
		return result.data;
	}
}
