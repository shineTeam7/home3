package com.home.commonClient.tool.func;

import com.home.commonBase.config.game.enumT.EquipSlotTypeConfig;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.GameEventType;
import com.home.commonBase.constlist.generate.ItemType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.item.EquipContainerData;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonClient.logic.unit.MUnitUseLogic;
import com.home.commonClient.net.request.func.item.FuncPutOffEquipRequest;
import com.home.commonClient.net.request.func.item.FuncPutOnEquipRequest;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;

public class PlayerEquipContainerTool extends PlayerFuncTool
{
	/** 数据 */
	protected EquipContainerData _data;
	
	/** 装备数目字典 */
	private IntIntMap _equipNumDic=new IntIntMap();
	
	/** 主单位逻辑 */
	protected MUnitUseLogic _mLogic;
	
	/** 添加buff实例ID记录 */
	private IntIntMap _buffInstanceIDDic=new IntIntMap();
	
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
		
		ItemData[] values;
		ItemData v;
		
		for(int i=(values=_data.equips.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				_equipNumDic.addValue(v.id,1);
			}
		}
	}
	
	@Override
	public void afterReadDataSecond()
	{
		if(_mLogic==null)
		{
			Ctrl.throwError("此时不该没有mLogic");
			return;
		}
		
		IntObjectMap<ItemData> fDic;
		if(!(fDic=_data.equips).isEmpty())
		{
			int[] keys=fDic.getKeys();
			ItemData[] values=fDic.getValues();
			int fv=fDic.getFreeValue();
			int k;
			ItemData v;
			
			for(int i=keys.length-1;i>=0;--i)
			{
			    if((k=keys[i])!=fv)
			    {
			        v=values[i];
			        
			        v.makeConfig();
					addEquipInfluence(k,v);
			    }
			}
		}
	}
	
	public EquipContainerData getData()
	{
		return _data;
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
	
	private void addEquipInfluence(int slot,ItemData data)
	{
		me.character.addEquipInfluence(_mLogic,this,slot,data);
	}
	
	private void removeEquipInfluence(int slot,ItemData data)
	{
		me.character.removeEquipInfluence(_mLogic,this,slot,data);
	}
	
	/** 是否可穿戴装备 */
	public boolean canPutOnEquip(int slot,ItemData data,boolean needNotice)
	{
		if(!isSlotEnabled(slot))
		{
			if(needNotice)
				me.warnLog("装备位置未开放",slot);
			
			return false;
		}
		
		if(data.config.type!=ItemType.Equip)
		{
			if(needNotice)
				me.warnLog("不是装备",data.id);
			
			return false;
		}
		
		if(getEquip(slot)!=null)
		{
			if(needNotice)
				me.warnLog("装备位置已存在",slot);
			
			return false;
		}
		
		if(data.config.isUnique && _equipNumDic.get(data.id)>0)
		{
			if(needNotice)
				me.warnLog("已存在唯一装备",data.id);
			
			return false;
		}
		
		//类型不匹配
		if(EquipSlotTypeConfig.get(slot).equipType!=data.config.secondType)
		{
			if(needNotice)
				me.warnLog("装备类型不匹配",data.id);
			
			return false;
		}
		
		//不满足使用条件
		if(!me.role.checkRoleConditions(data.config.useConditions,needNotice))
			return false;
		
		return true;
	}
	
	private void toPutOnEquip(int slot,ItemData data)
	{
		data.makeConfig();
		_data.equips.put(slot,data);
		_equipNumDic.addValue(data.id,1);
		
		me.dispatch(GameEventType.FuncEquipContainerRefreshGrid,new int[]{_funcID,slot});
		
		if(_mLogic!=null)
		{
			addEquipInfluence(slot,data);
		}
		else
		{
			Ctrl.errorLog("没有mLogic");
		}
	}
	
	private void toPutOffEquip(int slot,ItemData data)
	{
		_data.equips.remove(slot);
		_equipNumDic.addValue(data.id,-1);
		
		me.dispatch(GameEventType.FuncEquipContainerRefreshGrid,new int[]{_funcID,slot});
		
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
			me.warnLog("移动装备时,物品为空",fromSlot);
			return;
		}
		
		if(!isSlotEnabled(toSlot))
		{
			me.warnLog("移动装备时,目标槽位不可用",fromSlot);
			return;
		}
		
		if(EquipSlotTypeConfig.get(toSlot).equipType!=equip.config.secondType)
		{
			me.warnLog("移动装备时,装备类型不匹配",equip.id);
			return;
		}
		
		if(CommonSetting.isClientDriveLogic)
		{
			onMoveEquipByServer(fromSlot,toSlot);
		}
	}
	
	/** 从背包穿戴装备 */
	public void putOnEquipFromBag(int slot,int bagIndex)
	{
		ItemData item=me.bag.getItem(bagIndex);
		
		if(item==null)
		{
			me.warnLog("穿戴装备时,物品为空",bagIndex);
			return;
		}
		
		//不可穿戴
		if(!canPutOnEquip(slot,item,true))
			return;
		
		if(CommonSetting.isClientDriveLogic)
		{
			me.bag.removeItemByIndex(bagIndex,CallWayType.PutOnEquip);
			toPutOnEquip(slot,item);
		}
		else
		{
			me.send(FuncPutOnEquipRequest.create(_funcID,slot,bagIndex));
		}
	}
	
	/** 卸下装备到背包 */
	public void putOffEquipToBag(int slot,int bagIndex)
	{
		ItemData equip=getEquip(slot);
		
		if(equip==null)
		{
			me.warnLog("卸下装备时,装备为空",slot);
			return;
		}
		
		if(!me.bag.hasFreeGrid(1))
		{
			me.warnLog("卸下装备时,没有空余格子");
			return;
		}
		
		if(bagIndex!=-1)
		{
			if(me.bag.getItem(bagIndex)!=null)
			{
				me.warnLog("卸下装备时,目标格子不为空",bagIndex);
				return;
			}
		}
		
		if(CommonSetting.isClientDriveLogic)
		{
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
		else
		{
			me.send(FuncPutOffEquipRequest.create(_funcID,slot,bagIndex));
		}
	}
	
	/** 穿装备 */
	public void onPutOnEquipByServer(int slot,ItemData data)
	{
		toPutOnEquip(slot,data);
	}
	
	/** 脱装备 */
	public void onPutOffEquipByServer(int slot)
	{
		ItemData equip=getEquip(slot);
		
		if(equip==null)
		{
			me.warnLog("收到服务器脱装备时,装备为空",slot);
			return;
		}
		
		toPutOffEquip(slot,equip);
	}
	
	public void onMoveEquipByServer(int fromSlot,int toSlot)
	{
		ItemData equip=getEquip(fromSlot);
		ItemData target=getEquip(toSlot);
		
		removeEquipInfluence(fromSlot,equip);
		
		if(target!=null)
		{
			removeEquipInfluence(toSlot,target);
		}
		
		_data.equips.put(toSlot,equip);
		_data.equips.put(fromSlot,target);
		
		addEquipInfluence(toSlot,equip);
		
		if(target!=null)
		{
			addEquipInfluence(fromSlot,target);
		}
		
		me.dispatch(GameEventType.FuncEquipContainerRefreshGrid,new int[]{_funcID,fromSlot});
		me.dispatch(GameEventType.FuncEquipContainerRefreshGrid,new int[]{_funcID,toSlot});
	}
	
	/** buff实例ID记录 */
	public IntIntMap getBuffInstanceIDDic()
	{
		return _buffInstanceIDDic;
	}
}
