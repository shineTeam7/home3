package com.home.commonGame.scene.scene;

import com.home.commonBase.config.game.ScenePlaceElementConfig;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.data.scene.unit.identity.FieldItemIdentityData;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.ScenePlayLogic;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameScene;
import com.home.shine.ctrl.Ctrl;

/** 游戏玩法逻辑体 */
public class GameScenePlayLogic extends ScenePlayLogic
{
	/** 游戏服场景 */
	protected GameScene _gameScene;
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_gameScene=(GameScene)scene;
	}
	
	/** 检查某玩家是否可进入 */
	public boolean checkCanEnter(Player player)
	{
		return true;
	}
	
	/** 玩家角色进入 */
	public void onPlayerEnter(Player player,Unit unit)
	{
	
	}
	
	/** 玩家角色离开(移除单位前) */
	public void onPlayerLeave(Player player,Unit unit)
	{
	
	}
	
	/** 创建场景进入数据(只创建) */
	public SceneEnterData createSceneEnterData()
	{
		return new SceneEnterData();
	}
	
	/** 构造预进入位置 */
	public void makeScenePosData(UnitData data,int posID)
	{
		//设置初始位置
		if(posID==-1)
		{
			posID=_scene.getConfig().defaultEnterPos;
		}
		
		ScenePlaceElementConfig element;
		
		if((element=_scene.getPlaceConfig().getElement(posID))==null)
		{
			Ctrl.throwError("未找到posID为"+posID+"的场景进入位置,sceneID:",_scene.getSceneID());
			
			element=_scene.getPlaceConfig().getElement(_scene.getConfig().defaultEnterPos);
		}
		
		data.pos.setByFArr(element.pos);
	}
	
	/** 构造场景进入数据(激活单位后) */
	public void makeSceneEnterData(Player player,SceneEnterData data)
	{
	
	}
	
	/** 构造角色数据 */
	public void makeCharacterData(Player player,UnitData data)
	{
	
	}
	
	/** 结束指定进入 */
	public void endSigned()
	{
	
	}
	
	/** 拾取物品 */
	public void pickUpItem(Player player,Unit unit,int targetInstanceID)
	{
		//TODO:补充拾取CD
		
		if(!unit.identity.isCharacter())
		{
			unit.warnLog("拾取物品时，不是角色");
			return;
		}
		
		Unit targetUnit=_scene.getUnit(targetInstanceID);
		
		if(targetUnit==null)
		{
			unit.warnLog("拾取物品时，单位不存在");
			unit.sendInfoCode(InfoCodeType.PickUpFailed_unitMiss);
			return;
		}
		
		if(targetUnit.getType()!=UnitType.FieldItem)
		{
			unit.warnLog("拾取物品时，不是掉落物品单位");
			unit.sendInfoCode(InfoCodeType.PickUpFailed_unitMiss);
			return;
		}
		
		if(_scene.pos.calculatePosDistanceSq2D(unit.pos.getPos(),targetUnit.pos.getPos())>=Global.pickUpRadiusCheckSq)
		{
			unit.warnLog("拾取物品时，距离过远");
			unit.sendInfoCode(InfoCodeType.PickUpFailed_unitMiss);
			return;
		}
		
		FieldItemIdentityData iData=(FieldItemIdentityData)targetUnit.getUnitData().identity;
		
		ItemData itemData;
		if(!player.bag.hasItemPlace(itemData=iData.item))
		{
			unit.warnLog("背包已满");
			unit.sendInfoCode(InfoCodeType.PickUpFailed_bagNotEnough);
			return;
		}
		
		//移除
		targetUnit.removeAbs();
		
		player.bag.addItem(itemData,CallWayType.PickUpItem);
	}
	
	
}