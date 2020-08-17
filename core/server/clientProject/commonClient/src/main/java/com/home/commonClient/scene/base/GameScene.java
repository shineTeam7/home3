package com.home.commonClient.scene.base;

import com.home.commonBase.constlist.generate.RobotTestModeType;
import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.constlist.generate.UnitAIModeType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.scene.role.SceneRoleData;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonBase.data.scene.scene.ScenePreInfoData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.data.scene.unit.identity.MUnitIdentityData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.logic.unit.UnitFightDataLogic;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.SceneAOILogic;
import com.home.commonBase.scene.scene.SceneBattleLogic;
import com.home.commonClient.global.ClientGlobal;
import com.home.commonClient.logic.unit.CharacterUseLogic;
import com.home.commonClient.logic.unit.MUnitUseLogic;
import com.home.commonClient.part.player.Player;
import com.home.commonClient.scene.scene.GameSceneBattleLogic;
import com.home.commonClient.scene.scene.SceneClientAOILogic;
import com.home.commonClient.scene.unit.GameUnitMoveLogic;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;

public class GameScene extends Scene
{
	/** 玩家 */
	public Player me;
	
	/** 主角 */
	private Unit _hero;
	
	private ScenePreInfoData _preInfo;
	
	@Override
	protected SceneAOILogic createAOILogic()
	{
		return new SceneClientAOILogic();
	}
	
	@Override
	protected SceneBattleLogic createBattleLogic()
	{
		return new GameSceneBattleLogic();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_hero=null;
		me=null;
	}
	
	/** 获取战斗单位 */
	public Unit getFightUnitAbs(int instanceID)
	{
		Unit re=getFightUnit(instanceID);
		
		if(re==null)
			Ctrl.throwError("找不到单位");
		
		return re;
	}
	
	/** 预备信息 */
	public void setPreInfo(ScenePreInfoData preInfo)
	{
		_preInfo=preInfo;
	}
	
	/** 获取预备信息 */
	public ScenePreInfoData getPreInfo()
	{
		return _preInfo;
	}
	
	/** 初始化进入数据 */
	public void initEnterData(SceneEnterData enterData)
	{
		//有玩家角色
		if(enterData.roles!=null)
		{
			for(SceneRoleData roleData : enterData.roles)
			{
				role.addRole(roleData);
			}
		}
		
		if(enterData.bindVisionUnits!=null)
		{
			//_bindVisionUnits=enterData.bindVisionUnits;
		}
		
		addHero(enterData.hero);
		
		//SList<SceneLogicBase> logics=_logics;
		//SceneLogicBase logic;
		//
		//for(int i=0,len=logics.length();i<len;++i)
		//{
		//	if((logic=logics.get(i)).enabled)
		//	{
		//		logic.afterHero();
		//	}
		//}
		
		//先处理掉落包数据
		if(enterData.selfBindFieldItemBags!=null)
		{
			//role.initFieldItemBagBindDic(enterData.selfBindFieldItemBags);
		}
		
		//添加单位
		for(UnitData v : enterData.units)
		{
			addUnit(v);
		}
		
		//if(unitFactory!=null)
		//{
		//	//初始化起始单位
		//	unitFactory.initFirst();
		//}
		
		//后调用play的
		((GameSceneBattleLogic)battle).initEnterData(enterData);
		//method.initEnterData(enterData);
		
		onStart();
	}
	
	/** 主角 */
	public Unit getHero()
	{
		return _hero;
	}
	
	/** 添加主角 */
	private void addHero(UnitData data)
	{
		if(ShineSetting.openCheck)
		{
			if(getUnitDic().contains(data.instanceID))
			{
				Ctrl.throwError("单位已存在");
				return;
			}
		}
		
		//直接调用
		addUnit(data);
	}
	
	@Override
	public Unit addUnit(UnitData data)
	{
		//预处理
		//服务器驱动场景启用
		if(getConfig().instanceType!=SceneInstanceType.ClientDriveSinglePlayerBattle)
		{
			//是自己的M单位
			if(data.identity instanceof MUnitIdentityData && data.identity.playerID==me.role.playerID)
			{
				MUnitUseLogic useLogic=me.character.getMUnitUseLogic(data.getMUnitIdentity().mIndex);
				
				if(useLogic==null)
				{
					Ctrl.throwError("不能找不到主单位的使用逻辑",data.getMUnitIdentity().mIndex);
				}
				
				//取主角的数据逻辑
				UnitFightDataLogic dataLogic=useLogic.getFightLogic();
				
				//先清空
				dataLogic.clear();
				//再重设数据
				dataLogic.setData(data.fight,data.avatar);
				
				data.fightDataLogic=dataLogic;
				
				Unit unit=super.addUnit(data);
				
				unit.ai.setAIMode(UnitAIModeType.Base);
				
				return unit;
			}
		}
		
		return super.addUnit(data);
	}
	
	@Override
	protected Unit toCreateUnitByData(UnitData data)
	{
		//自己的单位
		if(data.identity instanceof MUnitIdentityData && data.identity.playerID==me.role.playerID)
		{
			//不走池
			GameUnit unit=(GameUnit) BaseC.factory.createUnit();
			unit.setType(data.identity.type);
			
			//自己的单位
			unit.setIsMine(true);
			unit.construct();
			
			if(data.identity.type==UnitType.Character)
			{
				if(ShineSetting.openCheck)
				{
					if(_hero!=null)
					{
						Ctrl.throwError("已存在主角了");
					}
				}
				
				_hero=unit;
			}
			
			return unit;
		}
		else
		{
			return _executor.createUnit(data.identity.type);
		}
	}
	
	@Override
	protected boolean isUnitDataNeedRelease(Unit unit)
	{
		return !((GameUnit)unit).isMine();
	}
	
	@Override
	protected boolean isUnitNeedRelease(Unit unit)
	{
		return !((GameUnit)unit).isMine();
	}
	
	/** 通过身份数据查找角色使用逻辑 */
	protected CharacterUseLogic getCharacterUseLogic(CharacterIdentityData idData)
	{
		return me.character.getCurrentCharacterUseLogic();
	}
	
	/** 场景开始 */
	protected void onStart()
	{
		switch(ClientGlobal.mode)
		{
			case RobotTestModeType.RandomMove:
			{
				getHero().ai.setAIMode(UnitAIModeType.Base);
				((GameUnitMoveLogic)getHero().move).randomMove();
			}
				break;
		}
	}
}
