package com.home.commonBase.scene.scene;

import com.home.commonBase.config.game.BuildingLevelConfig;
import com.home.commonBase.config.game.OperationConfig;
import com.home.commonBase.config.game.VehicleConfig;
import com.home.commonBase.config.game.enumT.SceneTypeConfig;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.OperationStateType;
import com.home.commonBase.constlist.generate.SceneForceType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.role.SceneRoleData;
import com.home.commonBase.data.scene.scene.FieldItemBagBindData;
import com.home.commonBase.data.scene.unit.identity.FieldItemIdentityData;
import com.home.commonBase.data.scene.unit.identity.OperationIdentityData;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.SceneLogicBase;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.OperationIdentityLogic;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;

/** 场景角色逻辑 */
public class SceneRoleLogic extends SceneLogicBase
{
	/** 场景角色组 */
	protected LongObjectMap<Role> _roleDic=new LongObjectMap<>(Role[]::new);
	
	/** 中立角色组 */
	private IntObjectMap<Role> _neutralRoleDic=new IntObjectMap<>(Role[]::new);
	
	/** 角色自身掉落包逻辑 */
	private LongObjectMap<IntObjectMap<FieldItemBagBindData>> _playerSelfFieldItemBagDic=new LongObjectMap<>(IntObjectMap[]::new);
	
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
		_roleDic.forEachValueS(v->
		{
			toRemoveRole(v);
		});
	}
	
	@Override
	public void onFrame(int delay)
	{
		Role[] values;
		Role v;
		
		for(int i=(values=_roleDic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				try
				{
					v.onFrame(delay);
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			}
		}
	}
	
	@Override
	public void onSecond(int delay)
	{
		if(!_playerSelfFieldItemBagDic.isEmpty())
		{
			long now=_scene.getTimeMillis();
			
			_playerSelfFieldItemBagDic.forEachS((k,v)->
			{
				v.forEachValueS(v2->
				{
					if(now>v2.removeTime)
					{
						v.remove(v2.instanceID);
						onSelfFieldItemBagRemove(k,v2);
					}
				});
				
				if(v.isEmpty())
				{
					_playerSelfFieldItemBagDic.remove(k);
				}
			});
		}
	}
	
	/** 是否需要角色 */
	public boolean needRole()
	{
		return SceneTypeConfig.get(_scene.getType()).needRole;
	}
	
	/** 获取角色对象 */
	public Role getRole(long playerID)
	{
		return _roleDic.get(playerID);
	}
	
	/** 获取场景角色字典 */
	public LongObjectMap<Role> getRoleDic()
	{
		return _roleDic;
	}
	
	///** 获取默认中立角色 */
	//public Role getDefaultNeutralRole()
	//{
	//
	//}
	
	/** 添加场景角色 */
	public Role addRole(SceneRoleData data)
	{
		if(ShineSetting.openCheck)
		{
			if(_roleDic.contains(data.playerID))
			{
				Ctrl.throwError("场景角色重复添加",data.playerID);
				return null;
			}
		}
		
		Role role=_scene.getExecutor().rolePool.getOne();
		role.setData(data);
		role.setScene(_scene);
		
		_roleDic.put(role.playerID,role);
		
		role.init();
		
		role.enabled=true;
		
		onAddRole(role);
		
		return role;
	}
	
	/** 通过角色显示数据，添加角色 */
	public Role addRoleByShowData(RoleShowData data)
	{
		Role role=getRole(data.playerID);
		
		if(role==null)
		{
			SceneRoleData roleData=_scene.inout.createRoleDataByShowData(data);
			roleData.force.force=SceneForceType.Player;
			role=_scene.role.addRole(roleData);
		}
		
		return role;
	}
	
	/** 移除角色 */
	public void removeRole(long playerID)
	{
		Role role=getRole(playerID);
		
		if(role==null)
		{
			return;
		}
		
		toRemoveRole(role);
	}
	
	/** 执行移除角色 */
	public void toRemoveRole(Role role)
	{
		long playerID=role.playerID;
		SceneRoleData data=role.getData();
		
		onRemoveRole(role);
		
		role.enabled=false;
		
		role.dispose();
		_roleDic.remove(playerID);
		
		//回收
		_scene.getExecutor().rolePool.back(role);
		_scene.getExecutor().roleDataPool.back(data);
	}
	
	/** 角色加入接口 */
	protected void onAddRole(Role role)
	{
	
	}
	
	protected void onRemoveRole(Role role)
	{
	
	}
	
	/** 广播所有角色 */
	public void radioAllRole(BaseRequest request)
	{
	
	}
	
	///** 获取某中立势力对应的中立角色 */
	//public Role getNeutralRoleByForce(int force)
	//{
	//	Role role=_neutralRoleDic.get(force);
	//
	//	if(role==null)
	//	{
	//		SceneRoleData data=_scene.inout.createNewRoleData();
	//		data.playerID=_scene.inout.getRobotPlayerID();
	//		data.force.force=force;//绑定势力
	//		role=addRole(data);
	//		_neutralRoleDic.put(force,role);
	//	}
	//
	//	return role;
	//}
	
	/** 拾取物品 */
	public void pickUpItem(Unit unit,int targetInstanceID)
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
		
		if(_scene.pos.calculatePosDistanceSq2D(unit.pos.getPos(),targetUnit.pos.getPos())>= Global.pickUpRadiusCheckSq)
		{
			unit.warnLog("拾取物品时，距离过远");
			unit.sendInfoCode(InfoCodeType.PickUpFailed_tooFar);
			return;
		}
		
		if(targetUnit.getType()!=UnitType.FieldItem)
		{
			unit.warnLog("拾取物品时，不是掉落物品单位");
			unit.sendInfoCode(InfoCodeType.PickUpFailed_unitMiss);
			return;
		}
		
		FieldItemIdentityData iData=(FieldItemIdentityData)targetUnit.getUnitData().identity;
		
		ItemData itemData=iData.item;
		
		if(!checkCanPickFieldItem(unit,itemData))
		{
			unit.warnLog("拾取物品时，背包已满");
			unit.sendInfoCode(InfoCodeType.PickUpFailed_bagNotEnough);
			return;
		}
		
		//移除
		targetUnit.removeAbs();
		
		doPickFieldItem(unit,itemData);
	}
	
	/** 检查是否可拾取物品 */
	protected boolean checkCanPickFieldItem(Unit unit,ItemData data)
	{
		return true;
	}
	
	/** 执行拾取物品 */
	protected void doPickFieldItem(Unit unit,ItemData data)
	{
	
	}
	
	/** 拾取物品 */
	public void pickUpItemBagAll(Unit unit,int targetInstanceID)
	{
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
		
		if(_scene.pos.calculatePosDistanceSq2D(unit.pos.getPos(),targetUnit.pos.getPos())>= Global.pickUpRadiusCheckSq)
		{
			unit.warnLog("拾取物品时，距离过远");
			unit.sendInfoCode(InfoCodeType.PickUpFailed_tooFar);
			return;
		}
		
		IntObjectMap<FieldItemBagBindData> dic=_playerSelfFieldItemBagDic.get(unit.identity.playerID);
		
		if(dic==null)
		{
			unit.warnLog("拾取物品时，找不到绑定掉落包数据");
			unit.sendInfoCode(InfoCodeType.PickUpFailed_unitMiss);
			return;
		}
		
		FieldItemBagBindData bData=dic.get(targetUnit.instanceID);
		
		if(bData==null)
		{
			unit.warnLog("拾取物品时，找不到绑定掉落包数据", targetUnit.instanceID);
			unit.sendInfoCode(InfoCodeType.PickUpFailed_unitMiss);
			return;
		}
		
		if(doPickFieldItemBagAll(unit,bData))
		{
			dic.remove(targetUnit.instanceID);
			
			if(dic.isEmpty())
			{
				_playerSelfFieldItemBagDic.remove(unit.identity.playerID);
			}
		}
	}
	
	/** 拾取掉落物品包 */
	protected boolean doPickFieldItemBagAll(Unit unit,FieldItemBagBindData data)
	{
		return true;
	}
	
	/** 使用操作体 */
	public void operate(Unit unit,int targetInstanceID)
	{
		if(!unit.identity.isCharacter())
		{
			unit.warnLog("使用操作体时，不是角色");
			return;
		}
		
		Unit targetUnit=_scene.getUnit(targetInstanceID);
		
		if(targetUnit==null)
		{
			unit.warnLog("使用操作体时，单位不存在");
			unit.sendInfoCode(InfoCodeType.OperateFailed_unitMiss);
			return;
		}
		
		if(targetUnit.getType()!=UnitType.Operation)
		{
			unit.warnLog("使用操作体时，不是操作体单位");
			unit.sendInfoCode(InfoCodeType.OperateFailed_unitMiss);
			return;
		}
		
		OperationIdentityLogic iLogic=targetUnit.getOperationIdentityLogic();
		
		OperationIdentityData iData=(OperationIdentityData)targetUnit.getUnitData().identity;
		
		if(iData.state!=OperationStateType.Ready)
		{
			unit.warnLog("使用操作体时，不在空闲中");
			unit.sendInfoCode(InfoCodeType.OperateFailed_notReady);
			return;
		}
		
		OperationConfig config=OperationConfig.get(iData.id);
		float disSq=_scene.pos.calculatePosDistanceSq2D(unit.pos.getPos(),targetUnit.pos.getPos());
		float operationDisSq=config.radius*config.radius;
		
		if(config.radius<=0 || disSq >= operationDisSq)
		{
			unit.warnLog("使用操作体时，距离过远 disSq:",disSq,"  operationDisSq:",operationDisSq);
			unit.sendInfoCode(InfoCodeType.OperateFailed_tooFar);
			return;
		}
		
		if(!checkCanOperate(unit,targetUnit,config))
		{
			unit.warnLog("使用操作体时，拾取条件不满足");
			return;
		}
		
		if(config.isOnce)
		{
			iLogic.changeState(OperationStateType.Disabled);
		}
		
		doOperate(unit,targetUnit,config);
	}
	
	/** 检查是否可使用操作体 */
	protected boolean checkCanOperate(Unit unit,Unit targetUnit,OperationConfig config)
	{
		return true;
	}
	
	/** 执行使用操作体 */
	protected void doOperate(Unit unit,Unit targetUnit,OperationConfig config)
	{
	
	}
	
	/** 检查是否可骑乘 */
	public boolean checkCanDrive(Unit unit,Unit targetUnit,VehicleConfig config)
	{
		return true;
	}
	
	/** 检查是否可建造(只在Role不存在下用) */
	public boolean checkCanBuild(Unit unit,int arg)
	{
		return true;
	}
	
	/** 执行建造消耗(只在Role不存在下用) */
	public void doBuildCost(Unit unit,int arg)
	{
	}
	
	/** 添加自身绑定数据 */
	public void addSelfFieldItemBag(long playerID,FieldItemBagBindData data)
	{
		_playerSelfFieldItemBagDic.computeIfAbsent(playerID,k->new IntObjectMap<>(FieldItemBagBindData[]::new)).put(data.instanceID,data);
	}
	
	/** 获取自身绑定物品掉落包数据组 */
	public IntObjectMap<FieldItemBagBindData> getSelfFieldItemBagDic(long playerID)
	{
		return _playerSelfFieldItemBagDic.get(playerID);
	}
	
	/** 一个绑定数据移除(处理重要物品) */
	protected void onSelfFieldItemBagRemove(long playerID,FieldItemBagBindData data)
	{
	
	}
}
