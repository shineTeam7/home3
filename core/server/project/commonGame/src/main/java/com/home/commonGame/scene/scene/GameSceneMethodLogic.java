package com.home.commonGame.scene.scene;

import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.scene.base.Scene;
import com.home.commonGame.logic.unit.CharacterUseLogic;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameScene;
import com.home.commonSceneBase.part.IScenePlayer;
import com.home.commonSceneBase.scene.scene.BSceneMethodLogic;

/** 游戏玩法逻辑体 */
public class GameSceneMethodLogic extends BSceneMethodLogic
{
	/** 游戏服场景 */
	protected GameScene _gameScene;
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_gameScene=(GameScene)scene;
	}
	
	/** 结束指定进入 */
	public void endSigned()
	{
	
	}
	
	@Override
	public void makeCharacterData(IScenePlayer player,UnitData data)
	{
		super.makeCharacterData(player,data);
		
		if(_scene.battle.enabled)
		{
			if(_scene.battle.getBattleConfig().isIndependent)
			{
				CharacterUseLogic logic=((Player)player).character.getCurrentCharacterUseLogic();

				logic.cacheForTown();
				//清空
				logic.getFightLogic().clearDataForIndependent();
			}
		}
	}
	
	///** 拾取物品 */
	//public void pickUpItem(Player player,Unit unit,int targetInstanceID)
	//{
	//	//TODO:补充拾取CD
	//
	//	if(!unit.identity.isCharacter())
	//	{
	//		unit.warnLog("拾取物品时，不是角色");
	//		return;
	//	}
	//
	//	Unit targetUnit=_scene.getUnit(targetInstanceID);
	//
	//	if(targetUnit==null)
	//	{
	//		unit.warnLog("拾取物品时，单位不存在");
	//		unit.sendInfoCode(InfoCodeType.PickUpFailed_unitMiss);
	//		return;
	//	}
	//
	//	if(targetUnit.getType()!=UnitType.FieldItem)
	//	{
	//		unit.warnLog("拾取物品时，不是掉落物品单位");
	//		unit.sendInfoCode(InfoCodeType.PickUpFailed_unitMiss);
	//		return;
	//	}
	//
	//	if(_scene.pos.calculatePosDistanceSq2D(unit.pos.getPos(),targetUnit.pos.getPos())>=Global.pickUpRadiusCheckSq)
	//	{
	//		unit.warnLog("拾取物品时，距离过远");
	//		unit.sendInfoCode(InfoCodeType.PickUpFailed_unitMiss);
	//		return;
	//	}
	//
	//	FieldItemIdentityData iData=(FieldItemIdentityData)targetUnit.getUnitData().identity;
	//
	//	ItemData itemData;
	//	if(!player.bag.hasItemPlace(itemData=iData.item))
	//	{
	//		unit.warnLog("背包已满");
	//		unit.sendInfoCode(InfoCodeType.PickUpFailed_bagNotEnough);
	//		return;
	//	}
	//
	//	//移除
	//	targetUnit.removeAbs();
	//
	//	player.bag.addItem(itemData,CallWayType.PickUpItem);
	//}
	
	
}