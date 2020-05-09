package com.home.commonBase.scene.unit;

import com.home.commonBase.config.game.PuppetConfig;
import com.home.commonBase.config.game.PuppetLevelConfig;
import com.home.commonBase.constlist.generate.PuppetAIType;
import com.home.commonBase.constlist.generate.PuppetTimeUpActionType;
import com.home.commonBase.data.scene.unit.identity.PuppetIdentityData;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.BaseSocket;

/** 傀儡身份逻辑 */
public class PuppetIdentityLogic extends UnitIdentityLogic
{
	protected PuppetIdentityData _iData;
	/** 配置 */
	protected PuppetConfig _config;
	
	@Override
	public void init()
	{
		super.init();
		
		_iData=(PuppetIdentityData)_data.identity;
		_config=PuppetConfig.get(_iData.id);
	}
	
	@Override
	public void afterInit()
	{
		super.afterInit();
		
		if(_config.isClientDrive || _scene.isDriveAll())
		{
			initAI();
		}
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_iData=null;
		_config=null;
	}
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
		
		if(_iData.lastTime>0)
		{
			if((_iData.lastTime-=delay)<=0)
			{
				_iData.lastTime=0;
				
				timeUp();
			}
		}
	}
	
	/** 获取主 */
	public Unit getMaster()
	{
		return _scene.getFightUnit(_iData.masterInstanceID);
	}
	
	/** 获取攻击者 */
	public Unit getAttacker()
	{
		if(_config.isIndependentAttacker)
			return _unit;
		
		return getMaster();
	}
	
	protected void initAI()
	{
		switch(_config.aiType)
		{
			case PuppetAIType.MoveStraight:
			{
			
			}
				break;
		}
	}

	/** 获取socket */
	@Override
	protected BaseSocket toGetSocket()
	{
		Ctrl.warnLog("这里应该走不进来了才对");
		
		Unit master=getMaster();
		
		if(master==null)
			return null;
		
		return master.getSocket();
	}
	
	/** 时间到 */
	private void timeUp()
	{
		if(!_scene.isDriveAll())
			return;
		
		PuppetLevelConfig levelConfig=PuppetLevelConfig.get(_iData.id,_iData.level);
		
		for(int[] v:levelConfig.timeUpActions)
		{
			doOneTimeUpAction(v);
		}
		
		_unit.removeLater();
	}
	
	protected void doOneTimeUpAction(int[] args)
	{
		switch(args[0])
		{
			case PuppetTimeUpActionType.UseSkill:
			{
				_unit.fight.useSkill(args[1],args[2]);
			}
				break;
		}
	}
}
