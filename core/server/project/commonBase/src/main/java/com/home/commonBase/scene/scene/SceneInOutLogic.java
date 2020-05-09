package com.home.commonBase.scene.scene;

import com.home.commonBase.config.game.ScenePlaceElementConfig;
import com.home.commonBase.constlist.generate.CustomPlayerType;
import com.home.commonBase.constlist.generate.SceneForceType;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.role.RoleAttributeData;
import com.home.commonBase.data.scene.role.RoleForceData;
import com.home.commonBase.data.scene.role.SceneRoleData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.SceneLogicBase;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.shine.support.collection.IntLongMap;
import com.home.shine.support.collection.LongObjectMap;

/** 场景进出逻辑 */
public class SceneInOutLogic extends SceneLogicBase
{
	/** 机器人角色ID */
	private int _robotPlayerIndex=0;
	
	/** 场景是否就绪(默认就绪) */
	public boolean ready=true;
	
	/** 组队字典 */
	protected LongObjectMap<LongObjectMap<Unit>> _playerTeamUnitDic=new LongObjectMap<>(LongObjectMap[]::new);
	
	/** 单位复活字典(key:placeInstanceID,value:time) */
	protected IntLongMap _unitReviveDic=new IntLongMap();
	
	@Override
	public void construct()
	{
	
	}
	
	@Override
	public void init()
	{
	
	}
	
	@Override
	public void dispose()
	{
		_robotPlayerIndex=0;
		ready=true;
		_unitReviveDic.clear();
	}
	
	@Override
	public void onFrame(int delay)
	{
	
	}
	
	@Override
	public void onSecond(int delay)
	{
		if(!_unitReviveDic.isEmpty())
		{
			long now=_scene.getTimeMillis();
			
			_unitReviveDic.forEachS((k,v)->
			{
				if(now>v)
				{
					_unitReviveDic.remove(k);
					
					//添加单位
					doReviveUnit(k);
				}
			});
		}
	}
	
	/** 获取机器人角色ID */
	protected long getRobotPlayerID()
	{
		return BaseGameUtils.makeCustomLogicID(CustomPlayerType.SceneRobot,++_robotPlayerIndex);
	}
	
	public int getCurrentRobotPlayerIndex()
	{
		return _robotPlayerIndex;
	}
	
	//role部分
	
	/** 创建初始化角色数据 */
	protected void initRoleData(SceneRoleData data)
	{
		(data.attribute=new RoleAttributeData()).initDefault();
		(data.force=new RoleForceData()).initDefault();
	}
	
	/** 创建新的角色数据 */
	public SceneRoleData createNewRoleData()
	{
		SceneRoleData rData=_scene.getExecutor().roleDataPool.getOne();
		
		if(rData.force==null)
		{
			initRoleData(rData);
		}
		
		return rData;
	}
	
	/** 通过角色显示数据构造场景角色数据 */
	public SceneRoleData createRoleDataByShowData(RoleShowData data)
	{
		SceneRoleData rData=createNewRoleData();
		rData.initDefault();
		
		rData.playerID=data.playerID;
		rData.showData=data;
		
		rData.force.force=SceneForceType.Neutral;
		
		//TODO:其他部分
		
		return rData;
	}
	
	/** 获取队伍成员组 */
	public LongObjectMap<Unit> getTeamUnits(long teamID)
	{
		return _playerTeamUnitDic.get(teamID);
	}
	
	/** 添加要复活的单位记录 */
	public void addUnitRevive(int placeInstanceID,long time)
	{
		_unitReviveDic.put(placeInstanceID,time);
	}
	
	protected void doReviveUnit(int placeInstanceID)
	{
		if(CommonSetting.monsterReviveUseNewInstanceID)
		{
			_scene.unitFactory.createAddUnitByPlace(placeInstanceID,true);
		}
		else
		{
			ScenePlaceElementConfig config=_scene.getPlaceConfig().getElement(placeInstanceID);
			
			Unit unit=_scene.getUnit(config.instanceID);
			
			if(unit!=null)
			{
				//先改位置
				unit.pos.setPosDirByFArrAbs(config.pos);
				//再复活
				unit.fight.doRevive();
			}
			else
			{
				//创建
				_scene.unitFactory.createAddUnitByPlace(placeInstanceID);
			}
		}
	}
}
