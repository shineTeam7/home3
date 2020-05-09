package com.home.commonBase.scene.unit;

import com.home.commonBase.config.game.MonsterConfig;
import com.home.commonBase.config.game.MonsterLevelConfig;
import com.home.commonBase.config.game.RewardConfig;
import com.home.commonBase.constlist.generate.DropDecideType;
import com.home.commonBase.constlist.generate.DropScopeType;
import com.home.commonBase.constlist.generate.MonsterDropType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.scene.unit.identity.MonsterIdentityData;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;

/** 怪物身份逻辑 */
public class MonsterIdentityLogic extends UnitIdentityLogic
{
	protected MonsterIdentityData _iData;
	/** 怪物配置 */
	protected MonsterConfig _config;
	/** 怪物等级配置 */
	protected MonsterLevelConfig _levelConfig;
	
	protected SList<Unit> _tempUnits=new SList<>(Unit[]::new);
	
	@Override
	public void init()
	{
		super.init();
		
		_iData=(MonsterIdentityData)_data.identity;
		_config=MonsterConfig.get(_iData.id);
		_levelConfig=MonsterLevelConfig.get(_iData.id,_iData.level);
		
		_unitName=_config.name;
	}
	
	@Override
	public void onReloadConfig()
	{
		_config=MonsterConfig.get(_iData.id);
		_levelConfig=MonsterLevelConfig.get(_iData.id,_iData.level);
		_unitName=_config.name;
	}
	
	/** 获取怪物配置 */
	public MonsterConfig getMonsterConfig()
	{
		return _config;
	}
	
	/** 被击杀 */
	public void beKill(Unit source)
	{
		Unit dropSource=getDropSource(source);
		
		if(dropSource!=null)
		{
			makeDropList(_tempUnits,dropSource);
			
			if(!_tempUnits.isEmpty())
			{
				int num=_tempUnits.size();
				
				Unit[] values=_tempUnits.getValues();
				Unit v;
				
				for(int i=0,len=_tempUnits.size();i<len;++i)
				{
					v=values[i];
					
					killRecord(v);
				}
				
				//有掉落
				if(_config.dropType!=MonsterDropType.None && _levelConfig.rewardID>0)
				{
					RewardConfig config=RewardConfig.get(_levelConfig.rewardID);
					
					for(int i=0,len=_tempUnits.size();i<len;++i)
					{
						v=values[i];
						
						dropOneWithoutItem(config,v,num);
					}
					
					switch(_config.dropScopeType)
					{
						case DropScopeType.OnlySelf:
						{
							dropOneForItem(config,dropSource);
						}
						break;
						case DropScopeType.Every:
						{
							for(int i=0,len=_tempUnits.size();i<len;++i)
							{
								v=values[i];
								
								dropOneForItem(config,v);
							}
						}
						break;
						case DropScopeType.RandomOne:
						{
							dropOneForItem(config,values[MathUtils.randomInt(_tempUnits.size())]);
						}
						break;
					}
				}
				
				_tempUnits.clear();
			}
		}
		
		
	}
	
	/** 获取掉落源 */
	protected Unit getDropSource(Unit source)
	{
		Unit re=null;
		
		//判定方式
		switch(_config.dropDecideType)
		{
			case DropDecideType.Killer:
			{
				if(source!=null)
				{
					re=source;
				}
			}
				break;
			case DropDecideType.MostDamage:
			{
				Unit target;
				
				int instanceID=_unit.fight.getMostDamageUnitInstanceID();
				
				if(instanceID!=-1)
				{
					if((target=_scene.getUnit(instanceID))!=null)
						return target;
				}
				
				if(source!=null)
				{
					re=source;
				}
			}
				break;
		}
		
		return re;
	}
	
	/** 构造初步掉落列表 */
	protected void makeDropList(SList<Unit> list,Unit source)
	{
	
	}
	
	/** 击杀记录部分 */
	protected void killRecord(Unit target)
	{
	
	}
	
	/** 掉落一个(非物品部分) */
	protected void dropOneWithoutItem(RewardConfig config,Unit target,int memberNum)
	{
	
	}
	
	/** 掉落一个(物品部分) */
	protected void dropOneForItem(RewardConfig config,Unit target)
	{
	
	}
	
	/** 回归idle状态 */
	public void reIdle()
	{
		if(_config.needRecoverAtBack)
		{
			//补满血蓝
			_unit.fight.getAttributeLogic().fillHpMp();
		}
		
		if(_config.dropDecideType==DropDecideType.MostDamage)
		{
			_unit.fight.clearDamageRecord();
		}
	}
	
}
