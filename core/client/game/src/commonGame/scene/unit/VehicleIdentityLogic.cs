using System;
using ShineEngine;

/// <summary>
/// 
/// </summary>
public class VehicleIdentityLogic:UnitIdentityLogic
{
	protected VehicleIdentityData _iData;
	/** 载具配置 */
	protected VehicleConfig _config;
	/** 乘客组 */
	private IntSet _driverSet=new IntSet();

	private int[] _drivers;

	public override void init()
	{
		base.init();

		_iData=(VehicleIdentityData)_data.identity;
		_config=VehicleConfig.get(_iData.id);

		_unitName=_config.name;

		_drivers=new int[_config.driverNum];

	}

	public override void afterInit()
	{
		base.afterInit();

		IntList iDataDrivers=_iData.drivers;
		for(int i=0,len=iDataDrivers.size();i<len;++i)
		{
			if(iDataDrivers[i]>0)
			{
				addDrive(i,iDataDrivers[i]);
			}
		}
	}

	public override void onReloadConfig()
	{
		_config=VehicleConfig.get(_iData.id);
		_unitName=_config.name;
	}

	public override void dispose()
	{
		base.dispose();

		_driverSet.clear();
		_drivers=null;
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
	public bool isFull()
	{
		return _driverSet.size()>=_config.driverNum;
	}

	public bool hasDriver(int instanceID)
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
	public bool canDriveIndex(int index)
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

		_iData.drivers.set(index,-1);
		_drivers[index]=0;
		_driverSet.remove(instanceID);

		Unit unit=_scene.getUnit(instanceID);
		if(unit!=null)
		{
			_unit.pos.removeMoveBinUnit(unit);
		}
	}
}