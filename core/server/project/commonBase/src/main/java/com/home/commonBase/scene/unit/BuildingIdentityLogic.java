package com.home.commonBase.scene.unit;

import com.home.commonBase.config.game.BuildingConfig;
import com.home.commonBase.config.game.BuildingLevelConfig;
import com.home.commonBase.constlist.generate.BuildingStateType;
import com.home.commonBase.data.scene.unit.identity.BuildingIdentityData;

/** 建筑身份逻辑 */
public class BuildingIdentityLogic extends UnitIdentityLogic
{
	protected BuildingIdentityData _iData;
	/** 配置 */
	protected BuildingConfig _config;
	/** 等级配置 */
	protected BuildingLevelConfig _levelConfig;
	
	@Override
	public void init()
	{
		super.init();
		
		_iData=(BuildingIdentityData)_data.identity;
		_config=BuildingConfig.get(_iData.id);
		_levelConfig=BuildingLevelConfig.get(_iData.id,_iData.level);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_iData=null;
		_config=null;
		_levelConfig=null;
	}
	
	@Override
	public void onSecond(int delay)
	{
		super.onSecond(delay);
		
		switch(_iData.state)
		{
			case BuildingStateType.Building:
			{
				if(_config.canAutoBuild)
				{
					if((_iData.passTime+=delay)>=_levelConfig.buildTime)
					{
						_iData.passTime=0;
						_iData.state=BuildingStateType.Ready;
						
						sendBuildComplete();
						onBuildComplete();
					}
				}
			}
				break;
			case BuildingStateType.LevelUping:
			{
				if((_iData.passTime+=delay)>=_levelConfig.levelUpTime)
				{
					_iData.passTime=0;
					_iData.state=BuildingStateType.Ready;
					_iData.level++;
					_levelConfig=BuildingLevelConfig.get(_iData.id,_iData.level);
					
					sendLevelUpComplete();
					onLevelUpComplete();
				}
			}
				break;
		}
	}
	
	/** 发送建造完成 */
	protected void sendBuildComplete()
	{
	
	}
	
	/** 发送升级完成 */
	protected void sendLevelUpComplete()
	{
	
	}
	
	/** 发送取消建筑升级 */
	protected void sendCancelLevelUp()
	{
	
	}
	
	/** 发送开始建筑升级 */
	protected void sendStartLevelUp()
	{
	
	}
	
	/** 开始建筑升级 */
	protected void onStartLevelUp()
	{
	
	}
	
	/** 建造完毕 */
	protected void onBuildComplete()
	{
	
	}
	
	/** 升级完毕 */
	protected void onLevelUpComplete()
	{
	
	}
	
	/** 取消升级 */
	public void cancelLevelUp()
	{
		_iData.state = BuildingStateType.Ready;
		_iData.passTime = 0;
		sendCancelLevelUp();
	}
	
	/** 开始升级 */
	public void startLevelUp()
	{
		//已达到等级上限
		if(_iData.level>=_config.levelMax)
		{
			return;
		}
		
		BuildingLevelConfig nextLevelConfig=BuildingLevelConfig.get(_levelConfig.id,_levelConfig.level+1);
		
		if(!checkCanLevelUp(nextLevelConfig))
			return;
		
		_iData.state = BuildingStateType.LevelUping;
		_iData.passTime = 0;
		
		doLevelUpCost(nextLevelConfig);
		sendStartLevelUp();
		onStartLevelUp();
	}
	
	protected boolean checkCanLevelUp(BuildingLevelConfig nextLevelConfig)
	{
		return true;
	}
	
	/** 执行升级开销 */
	protected void doLevelUpCost(BuildingLevelConfig nextLevelConfig)
	{
	
	}
}
