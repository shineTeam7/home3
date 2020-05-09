package com.home.commonGame.tool.func;

import com.home.commonBase.config.game.enumT.EquipSlotTypeConfig;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.ItemType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.item.EquipContainerData;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.global.BaseC;
import com.home.commonGame.logic.unit.MUnitUseLogic;
import com.home.commonGame.net.request.func.item.FuncSendMoveEquipRequest;
import com.home.commonGame.net.request.func.item.FuncSendPutOffEquipRequest;
import com.home.commonGame.net.request.func.item.FuncSendPutOnEquipRequest;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;

/** 角色装备容器工具 */
public class PlayerEquipContainerTool extends PlayerFuncTool
{
	/** 数据 */
	protected EquipContainerData _data;
	
	/** 装备数目字典 */
	protected IntIntMap _equipNumDic=new IntIntMap();
	
	/** 主单位逻辑 */
	protected MUnitUseLogic _mLogic;
	
	/** 添加buff实例ID记录 */
	protected IntIntMap _buffInstanceIDDic=new IntIntMap();
	
	//TODO:套装计算
	
	public PlayerEquipContainerTool(int funcID)
	{
		super(FuncToolType.EquipContainer,funcID);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_equipNumDic.clear();
		_mLogic=null;
	}
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		
		_data=(EquipContainerData)data;
	}
	
	@Override
	public void afterReadData()
	{
		super.afterReadData();
		
		_data.equips.forEachValue(v->
		{
			_equipNumDic.addValue(v.id,1);
		});
	}
	
	@Override
	public void afterReadDataSecond()
	{
		if(_mLogic==null)
		{
			Ctrl.throwError("此时不该没有mLogic");
			return;
		}
		_data.equips.forEach((k,v)->
		{
			v.makeConfig();
			addEquipInfluence(k,v);
		});
	}
	
	public EquipContainerData getData()
	{
		return _data;
	}
	
	@Override
	protected FuncToolData createToolData()
	{
		return BaseC.factory.createEquipContainerData();
	}
	
	@Override
	public void onNewCreate()
	{
		super.onNewCreate();
	}
	
	public void setMUnitLogic(MUnitUseLogic logic)
	{
		_mLogic=logic;
	}
	
	/** 获取主单位逻辑 */
	public MUnitUseLogic getMLogic()
	{
		return _mLogic;
	}
	
	/** 槽位是否可用 */
	public boolean isSlotEnabled(int slot)
	{
		return _data.openSlots.contains(slot);
	}
	
	/** 获取指定槽位的装备 */
	public ItemData getEquip(int slot)
	{
		return _data.equips.get(slot);
	}
	
	/** 开启槽位 */
	public void openSlot(int slot)
	{
		if(_data.openSlots.contains(slot))
			return;
		
		_data.openSlots.add(slot);
	}
	
	/** 添加装备影响(单个装备) */
	protected void addEquipInfluence(int slot,ItemData data)
	{
		me.character.addEquipInfluence(_mLogic,this,slot,data);
	}
	
	/** 移除装备影响(单个装备) */
	protected void removeEquipInfluence(int slot,ItemData data)
	{
		me.character.removeEquipInfluence(_mLogic,this,slot,data);
	}
	
	/** 是否可穿戴装备 */
	public boolean canPutOnEquip(int slot,ItemData data,boolean needNotice)
	{
		if(!isSlotEnabled(slot))
		{
			if(needNotice)
				me.warningInfoCode(InfoCodeType.Equip_slotNotOpen,slot);
			
			return false;
		}
		
		if(data.config.type!=ItemType.Equip)
		{
			if(needNotice)
				me.warningInfoCode(InfoCodeType.Equip_notEquip,data.id);
			
			return false;
		}
		
		if(getEquip(slot)!=null)
		{
			if(needNotice)
				me.warningInfoCode(InfoCodeType.Equip_slotAlreadyExist,slot);
			
			return false;
		}
		
		if(data.config.isUnique && _equipNumDic.get(data.id)>0)
		{
			if(needNotice)
				me.warningInfoCode(InfoCodeType.Equip_equipAlreadyExist,data.id);
			
			return false;
		}
		
		//类型不匹配
		if(EquipSlotTypeConfig.get(slot).equipType!=data.config.secondType)
		{
			if(needNotice)
				me.warningInfoCode(InfoCodeType.Equip_wrongType,data.id);
			
			return false;
		}
		
		//不满足使用条件
		if(!me.role.checkRoleConditions(data.config.useConditions,needNotice))
			return false;
		
		return true;
	}
	
	protected void toPutOnEquip(int slot,ItemData data)
	{
		data.makeConfig();
		_data.equips.put(slot,data);
		_equipNumDic.addValue(data.id,1);
		
		me.send(FuncSendPutOnEquipRequest.create(_funcID,slot,data));
		
		if(_mLogic!=null)
		{
			addEquipInfluence(slot,data);
		}
		else
		{
			Ctrl.errorLog("没有mLogic");
		}
	}
	
	protected void toPutOffEquip(int slot,ItemData data)
	{
		_data.equips.remove(slot);
		_equipNumDic.addValue(data.id,-1);
		
		me.send(FuncSendPutOffEquipRequest.create(_funcID,slot));
		
		if(_mLogic!=null)
		{
			removeEquipInfluence(slot,data);
		}
		else
		{
			Ctrl.errorLog("没有mLogic");
		}
		
		//TODO:套装
	}
	
	/** 移动装备 */
	public void moveEquip(int fromSlot,int toSlot)
	{
		ItemData equip=getEquip(fromSlot);
		
		if(equip==null)
		{
			warningInfoCode(InfoCodeType.MoveEquip_equipNotExist,fromSlot);
			return;
		}
		
		if(!isSlotEnabled(toSlot))
		{
			warningInfoCode(InfoCodeType.MoveEquip_targetSlotDisabled,fromSlot);
			return;
		}
		
		if(EquipSlotTypeConfig.get(toSlot).equipType!=equip.config.secondType)
		{
			warningInfoCode(InfoCodeType.MoveEquip_equipTypeNotMatch,equip.id);
			return;
		}
		
		ItemData target=getEquip(toSlot);
		
		removeEquipInfluence(fromSlot,equip);
		
		if(target!=null)
		{
			removeEquipInfluence(toSlot,target);
			_data.equips.put(fromSlot,target);
		}
		else
		{
			_data.equips.remove(fromSlot);
		}
		
		_data.equips.put(toSlot,equip);
		
		addEquipInfluence(toSlot,equip);
		
		if(target!=null)
		{
			addEquipInfluence(fromSlot,target);
		}
		
		me.send(FuncSendMoveEquipRequest.create(_funcID,fromSlot,toSlot));
	}
	
	/** 从背包穿戴装备 */
	public void putOnEquipFromBag(int slot,int bagIndex)
	{
		ItemData item=me.bag.getItem(bagIndex);
		
		if(item==null)
		{
			me.warningInfoCode(InfoCodeType.Equip_itemNotExist,bagIndex);
			return;
		}
		
		//不可穿戴
		if(!canPutOnEquip(slot,item,true))
			return;
		
		me.bag.removeItemByIndex(bagIndex,CallWayType.PutOnEquip);
		toPutOnEquip(slot,item);
	}
	
	/** 卸下装备到背包 */
	public void putOffEquipToBag(int slot,int bagIndex)
	{
		ItemData equip=getEquip(slot);
		
		if(equip==null)
		{
			me.warningInfoCode(InfoCodeType.Equip_equipNotExist,slot);
			return;
		}
		
		if(!canPutOffEquip(slot,bagIndex))
		{
			return;
		}
		
		if(bagIndex!=-1)
		{
			if(me.bag.getItem(bagIndex)!=null)
			{
				me.warningInfoCode(InfoCodeType.Equip_slotNotEmpty,bagIndex);
				return;
			}
		}
		else
		{
			if(!me.bag.hasFreeGrid(1))
			{
				me.warningInfoCode(InfoCodeType.Equip_slotNotEnough);
				return;
			}
		}
		
		toPutOffEquip(slot,equip);
		
		if(bagIndex!=-1)
		{
			me.bag.addNewItemToIndex(bagIndex,equip,CallWayType.PutOffEquip);
		}
		else
		{
			me.bag.addItem(equip,CallWayType.PutOffEquip);
		}
	}
	
	/** 是否能脱下装备 */
	protected boolean canPutOffEquip(int slot,int bagIndex)
	{
		return true;
	}
	
	/** buff实例ID记录 */
	public IntIntMap getBuffInstanceIDDic()
	{
		return _buffInstanceIDDic;
	}
	
	/** 卸下装备(删除) */
	public void putOffEquipRemove(int slot)
	{
		ItemData equip=getEquip(slot);
		
		if(equip==null)
		{
			me.warnLog("卸下装备时,装备为空",slot);
			return;
		}
		
		toPutOffEquip(slot,equip);
	}
	
}
