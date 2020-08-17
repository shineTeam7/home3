package com.home.commonGame.scene.scene;

import com.home.commonBase.config.game.OperationConfig;
import com.home.commonBase.config.game.VehicleConfig;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.scene.scene.FieldItemBagBindData;
import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.SceneRoleLogic;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.RemoveFieldItemBagBindRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameScene;
import com.home.commonGame.scene.base.GameUnit;
import com.home.shine.net.base.BaseRequest;

/** 逻辑服角色逻辑 */
public class GameSceneRoleLogic extends SceneRoleLogic
{
	protected GameScene _gameScene;
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_gameScene=(GameScene)scene;
	}
	
	//@Override
	//public GameRole getRole(long playerID)
	//{
	//	return (GameRole)super.getRole(playerID);
	//}
	
	/** 添加玩家,如果不存在的话 */
	public Role addRoleIfNotExist(Player player)
	{
		Role role=_scene.getExecutor().rolePool.getOne();
		
		return null;
	}
	
	/** 广播所有角色 */
	@Override
	public void radioAllRole(BaseRequest request)
	{
		request.write();
		
		GameSceneInOutLogic gameInOut=_gameScene.gameInOut;
		
		_roleDic.forEachValue(v->
		{
			Player player;
			if((player=gameInOut.getPlayer(v.playerID))!=null)
			{
				//TODO:看Role是否需要广播屏蔽
				player.send(request);
			}
		});
	}
	
	@Override
	protected boolean checkCanPickFieldItem(Unit unit,ItemData data)
	{
		Player player=((GameUnit)unit).getPlayer();
		
		return player.bag.hasItemPlace(data);
	}
	
	@Override
	protected void doPickFieldItem(Unit unit,ItemData data)
	{
		Player player=((GameUnit)unit).getPlayer();
		
		player.bag.addItem(data,CallWayType.PickUpItem);
	}
	
	@Override
	protected boolean doPickFieldItemBagAll(Unit unit,FieldItemBagBindData data)
	{
		Player player=((GameUnit)unit).getPlayer();
		
		boolean re = player.bag.hasItemPlace(data.items);
		
		if(re)
		{
			player.bag.addItems(data.items,CallWayType.PickUpItem);
			//推送消息
			player.send(RemoveFieldItemBagBindRequest.create(data.instanceID));
		}
		else
		{
			player.sendInfoCode(InfoCodeType.BagNotEnough);
		}
		
		return re;
	}
	
	@Override
	protected boolean checkCanOperate(Unit unit,Unit targetUnit,OperationConfig config)
	{
		Player player=((GameUnit)unit).getPlayer();
		
		return player.role.checkRoleConditions(config.operateConditions,true);
	}
	
	@Override
	protected void doOperate(Unit unit,Unit targetUnit,OperationConfig config)
	{
		Player player=((GameUnit)unit).getPlayer();
		
		player.role.doRoleActions(config.operateActions,1,CallWayType.UseOperation);
	}
	
	@Override
	public boolean checkCanDrive(Unit unit,Unit targetUnit,VehicleConfig config)
	{
		Player player=((GameUnit)unit).getPlayer();
		
		return player.role.checkRoleConditions(config.driveConditions,true);
	}
}
