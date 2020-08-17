package com.home.commonSceneBase.scene.base;

import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.UnitAOILogic;
import com.home.commonBase.scene.unit.UnitAvatarLogic;
import com.home.commonBase.scene.unit.UnitFightLogic;
import com.home.commonBase.scene.unit.UnitIdentityLogic;
import com.home.commonBase.scene.unit.UnitMoveLogic;
import com.home.commonBase.scene.unit.UnitPosLogic;
import com.home.commonSceneBase.scene.unit.BBuildingIdentityLogic;
import com.home.commonSceneBase.scene.unit.BMonsterIdentityLogic;
import com.home.commonSceneBase.scene.unit.BOperationIdentityLogic;
import com.home.commonSceneBase.scene.unit.BUnitAOITowerLogic;
import com.home.commonSceneBase.scene.unit.BUnitAvatarLogic;
import com.home.commonSceneBase.scene.unit.BUnitFightLogic;
import com.home.commonSceneBase.scene.unit.BUnitMoveLogic;
import com.home.commonSceneBase.scene.unit.BUnitPosLogic;
import com.home.commonSceneBase.scene.unit.CharacterIdentityLogic;

public class BUnit extends Unit
{
	@Override
	protected UnitMoveLogic createMoveLogic()
	{
		return new BUnitMoveLogic();
	}
	
	@Override
	protected UnitAvatarLogic createAvatarLogic()
	{
		return new BUnitAvatarLogic();
	}
	
	@Override
	protected UnitPosLogic createPosLogic()
	{
		return new BUnitPosLogic();
	}
	
	@Override
	protected UnitAOILogic createAOILogic()
	{
		//TODO:根据场景类型，注册AOI
		//未来如有需求可以在preInit和afterDispose里做
		//目前这版本先做成绑定aoiTower
		
		return new BUnitAOITowerLogic();
	}
	
	@Override
	protected UnitFightLogic createFightLogic()
	{
		return new BUnitFightLogic();
	}
	
	/** 创建身份逻辑 */
	@Override
	protected UnitIdentityLogic createIdentityLogic()
	{
		switch(_type)
		{
			case UnitType.Character:
			{
				return new CharacterIdentityLogic();
			}
			case UnitType.Monster:
			{
				return new BMonsterIdentityLogic();
			}
			case UnitType.Operation:
			{
				return new BOperationIdentityLogic();
			}
			case UnitType.Building:
			{
				return new BBuildingIdentityLogic();
			}
		}
		
		return super.createIdentityLogic();
	}
}
