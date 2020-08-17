package com.home.commonSceneBase.scene.scene;

import com.home.commonBase.config.game.ScenePlaceElementConfig;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.SceneMethodLogic;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.AddUnitRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.RemoveUnitRequest;
import com.home.commonSceneBase.part.IScenePlayer;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.LongSet;

public class BSceneMethodLogic extends SceneMethodLogic
{
	protected LongSet _sendSet=new LongSet();
	
	/** 检查某玩家是否可进入 */
	public boolean checkCanEnter(IScenePlayer player)
	{
		if(_scene.battle.enabled && !_scene.battle.checkCanEnter(player.getPlayerID()))
			return false;
		
		return true;
	}
	
	/** 玩家角色进入 */
	public void onPlayerEnter(IScenePlayer player,Unit unit)
	{
		if(_scene.battle.enabled)
			_scene.battle.onPlayerEnter(player.getPlayerID(),unit);
	}
	
	/** 玩家角色离开(移除单位前) */
	public void onPlayerLeave(IScenePlayer player,Unit unit)
	{
		if(_scene.battle.enabled)
			_scene.battle.onPlayerLeave(player.getPlayerID(),unit);
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
	public void makeSceneEnterData(IScenePlayer player,SceneEnterData data)
	{
	
	}
	
	/** 构造角色数据 */
	public void makeCharacterData(IScenePlayer player,UnitData data)
	{
		if(_scene.battle.enabled)
			_scene.battle.makeCharacterData(player.getPlayerID(),data);
	}
	
	/** 结束指定进入 */
	public void endSigned()
	{
	
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
	
	
	@Override
	public void doRadioMessage(long selfID,BaseRequest request,Unit[] values,int length)
	{
		boolean written=false;
		
		LongSet sendSet;
		(sendSet=_sendSet).clear();
		
		Unit v;
		BaseSocket socket;
		long targetID;
		
		for(int i=length - 1;i >= 0;--i)
		{
			if((v=values[i])!=null)
			{
				if((targetID=v.identity.controlPlayerID)!=-1 && (targetID!=selfID) && !sendSet.contains(targetID))
				{
					if(v.identity.socketReady && (socket=v.getSocket())!=null)
					{
						if(!written)
						{
							written=true;
							request.write();
						}
						
						socket.send(request);
						sendSet.add(targetID);
						
						if(CommonSetting.openAOICheck)
						{
							if(request instanceof AddUnitRequest)
							{
								AddUnitRequest aRequest=(AddUnitRequest)request;
								
								v.aoi.recordAddUnit(aRequest.data.instanceID);
							}
							
							if(request instanceof RemoveUnitRequest)
							{
								RemoveUnitRequest aRequest=(RemoveUnitRequest)request;
								
								v.aoi.recordRemoveUnit(aRequest.instanceID);
							}
						}
					}
				}
			}
		}
		
		sendSet.clear();
	}
}
