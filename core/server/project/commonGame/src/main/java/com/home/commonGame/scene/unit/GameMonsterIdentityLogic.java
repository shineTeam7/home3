package com.home.commonGame.scene.unit;

import com.home.commonBase.config.game.RewardConfig;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.constlist.generate.MonsterDropType;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.scene.FieldItemBagBindData;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.MonsterIdentityLogic;
import com.home.commonGame.logic.team.PlayerTeam;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.AddFieldItemBagBindRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameUnit;
import com.home.commonSceneBase.scene.unit.BMonsterIdentityLogic;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;

/** 逻辑服怪物逻辑 */
public class GameMonsterIdentityLogic extends BMonsterIdentityLogic
{
	@Override
	protected void makeDropList(SList<Unit> list,Unit source)
	{
		list.clear();
		list.add(source);
		
		Player player=((GameUnit)source).getPlayer();
		
		if(player!=null && player.team!=null)
		{
			PlayerTeam team=player.team.getTeam();
			
			if(team!=null)
			{
				LongObjectMap<Unit> teamUnits=_scene.inout.getTeamUnits(team.groupID);
				
				//有成员
				if(teamUnits!=null && !teamUnits.isEmpty())
				{
					PosData aPos=source.pos.getPos();
					
					teamUnits.forEachValue(v->
					{
						//不是攻击者并且在范围内
						if(v!=source && (Global.teamShareRadius<=0 || _scene.pos.calculatePosDistanceSq(v.pos.getPos(),aPos)<=Global.teamShareRadiusT))
						{
							list.add(v);
						}
					});
				}
			}
		}
	}
	
	@Override
	protected void killRecord(Unit target)
	{
		Player player=((GameUnit)target).getPlayer();
		
		if(player!=null)
		{
			player.quest.taskEventForKillMonster(_unit);
		}
	}
	
	@Override
	protected void dropOneWithoutItem(RewardConfig config,Unit target,int memberNum)
	{
		Player player=((GameUnit)target).getPlayer();
		
		//掉落时找不到角色
		if(player==null)
			return;
		
		player.bag.addRewardWithoutItem(config,CallWayType.MonsterDrop);
	}
	
	@Override
	protected void dropOneForItem(RewardConfig config,Unit target)
	{
		Player player=((GameUnit)target).getPlayer();
		
		//掉落时找不到角色
		if(player==null)
			return;
		
		switch(_config.dropType)
		{
			case MonsterDropType.Add:
			{
				player.bag.addRewardForItem(config,false,CallWayType.MonsterDrop);
			}
				break;
			case MonsterDropType.AddAbs:
			{
				player.bag.addRewardForItem(config,true,CallWayType.MonsterDrop);
			}
				break;
			case MonsterDropType.Drop:
			{
			
			}
				break;
			case MonsterDropType.DropBag:
			{
			
			}
				break;
			case MonsterDropType.SelfDrop:
			{
			
			}
				break;
			case MonsterDropType.SelfDropBag:
			{
			
			}
				break;
			case MonsterDropType.SelfDropBagBindUnit:
			{
				SList<ItemData> list=new SList<>(ItemData[]::new);
				player.bag.makeRewardItems(config,list);
				
				//添加掉落绑定数据
				FieldItemBagBindData data=new FieldItemBagBindData();
				data.instanceID=_unit.instanceID;
				data.items=list;
				data.removeTime=_scene.getTimeMillis()+_unit.fight.getFightUnitConfig().deathKeepTime;
				
				_scene.role.addSelfFieldItemBag(player.role.playerID,data);
				
				//推送
				player.send(AddFieldItemBagBindRequest.create(data));
			}
				break;
		}
	}
}
