package com.home.commonBase.scene.unit;

import com.home.commonBase.config.game.BuffConfig;
import com.home.commonBase.config.game.VehicleConfig;
import com.home.commonBase.constlist.generate.BuffKeepType;
import com.home.commonBase.constlist.generate.ItemEquipActionType;
import com.home.commonBase.constlist.generate.VehicleActionType;
import com.home.commonBase.data.scene.base.BuffData;
import com.home.commonBase.data.scene.unit.identity.VehicleIdentityData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.logic.unit.BuffDataLogic;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntSet;

/** 载具身份逻辑 */
public class VehicleIdentityLogic extends UnitIdentityLogic
{
	protected VehicleIdentityData _iData;
	/** 载具配置 */
	protected VehicleConfig _config;
	/** 乘客组 */
	private IntSet _driverSet=new IntSet();
	
	private int[] _drivers;
	
	@Override
	public void init()
	{
		super.init();
		
		_iData=(VehicleIdentityData)_data.identity;
		_config=VehicleConfig.get(_iData.id);
		
		_unitName=_config.name;
		
		_drivers=new int[_config.driverNum];
		
		if(CommonSetting.isClient)
		{
			IntList drivers=_iData.drivers;
			
			for(int i=0,len=drivers.size();i<len;++i)
			{
				if(drivers.get(i)>0)
				{
					addDrive(i,drivers.get(i));
				}
			}
		}
	}
	
	@Override
	public void onReloadConfig()
	{
		_config=VehicleConfig.get(_iData.id);
		_unitName=_config.name;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_driverSet.clear();
		_drivers=null;
		_iData.drivers.clear();
	}
	
	@Override
	public void preRemove()
	{
		super.preRemove();
		
		if(_drivers!=null)
		{
			Unit unit;
			
			for(int i=0,len=_drivers.length;i<len;i++)
			{
				if(_drivers[i]>0)
				{
					unit=_scene.getUnit(_drivers[i]);
					
					if(unit!=null)
					{
						//载具移除
						unit.move.cancelVehicle();
					}
				}
			}
		}
	}
	
	/** 获取身份数据 */
	public VehicleIdentityData getData()
	{
		return _iData;
	}
	
	/** 获取载具配置 */
	public VehicleConfig getVehicleConfig()
	{
		return _config;
	}
	
	/** 已满员 */
	public boolean isFull()
	{
		return _driverSet.size()>=_config.driverNum;
	}
	
	public boolean hasDriver(int instanceID)
	{
		return _driverSet.contains(instanceID);
	}
	
	/** 获取乘客 */
	public int getDriverByIndex(int index)
	{
		if(index>=_iData.drivers.size())
			return -1;
		
		int re=_iData.drivers.get(index);
		
		if(re<=0)
			re=-1;
		
		return re;
	}
	
	/** 某序号是否可驾驶 */
	public boolean canDriveIndex(int index)
	{
		return index==0;
	}
	
	/** 添加乘客 */
	public void addDrive(int index,int instanceID)
	{
		_iData.drivers.setLength(index+1);
		_iData.drivers.set(index,instanceID);
		_driverSet.add(instanceID);
		_drivers[index]=instanceID;
		
		Unit unit=_scene.getUnit(instanceID);
		
		if(unit!=null)
		{
			_unit.pos.addMoveBindUnit(unit);
		}
	}
	
	/** 移除乘客 */
	public void removeDrive(int index)
	{
		if(_drivers[index]<=0)
			return;
		
		int instanceID=_drivers[index];
		Unit unit=_scene.getUnit(instanceID);
		
		_iData.drivers.set(index,-1);
		_drivers[index]=0;
		_driverSet.remove(instanceID);
		
		if(unit!=null)
		{
			_unit.pos.removeMoveBinUnit(unit);
		}
	}
	
	/** 添加骑乘影响 */
	public void addDriveInfluence(Unit unit)
	{
		for(int[] v:_config.driveActions)
		{
			doOneAction(unit,v,true);
		}
	}
	
	/** 移除骑乘影响 */
	public void removeDriveInfluence(Unit unit)
	{
		for(int[] v:_config.driveActions)
		{
			doOneAction(unit,v,false);
		}
	}
	
	protected void doOneAction(Unit unit,int[] args,boolean isOn)
	{
		switch(args[0])
		{
			case VehicleActionType.AddBuff:
			{
				if(isOn)
				{
					if(ShineSetting.openCheck)
					{
						BuffConfig buffConfig=BuffConfig.get(args[1]);
						
						if(buffConfig.keepType!=BuffKeepType.Online)
						{
							Ctrl.throwError("骑乘用buff的保存类型必须为Online");
						}
					}
					
					unit.fight.getBuffLogic().addBuff(args[1],args[2]);
				}
				else
				{
					unit.fight.getBuffLogic().removeBuffByID(args[1]);
				}
			}
				break;
			case VehicleActionType.AddSkill:
			{
				if(isOn)
				{
					unit.fight.getDataLogic().addSkill(args[1],args[2]);
				}
				else
				{
					unit.fight.getDataLogic().removeSkill(args[1]);
				}
			}
				break;
		}
	}
}
