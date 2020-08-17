package com.home.commonBase.scene.base;

import com.home.commonBase.config.game.RegionConfig;
import com.home.commonBase.constlist.generate.RegionActionType;
import com.home.commonBase.constlist.generate.RegionShapeType;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.RectData;
import com.home.commonBase.data.scene.base.RegionData;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;

/** 区域 */
public class Region
{
	public int instanceID=-1;
	
	protected RegionData _data;
	
	protected Scene _scene;
	
	protected RegionConfig _config;
	
	/** 包含单位组 */
	protected IntObjectMap<Unit> _containUnits=new IntObjectMap<>(Unit[]::new);
	
	/** 外框 */
	protected RectData _boundRect=new RectData();
	
	/** 原点 */
	protected PosData _originPos;
	
	protected float _disSq;
	
	public void setData(RegionData data)
	{
		_data=data;
		
		if(data!=null)
		{
			_config=RegionConfig.get(data.id);
			instanceID=data.instanceID;
		}
		else
		{
			_config=null;
			instanceID=-1;
		}
	}
	
	public void setScene(Scene scene)
	{
		_scene=scene;
	}
	
	public void init()
	{
		_boundRect.clear();
		
		float[] args=_data.args;
		
		switch(_config.shapeType)
		{
			case RegionShapeType.Circle:
			{
				_boundRect.x=args[0]-args[2];
				_boundRect.y=args[1]-args[2];
				_boundRect.width=_boundRect.height=args[2]*2;
				
				if(_originPos==null)
					_originPos=new PosData();
				
				_originPos.x=args[0];
				_originPos.z=args[1];
				_disSq=args[2]*args[2];
			}
				break;
			case RegionShapeType.Rect:
			{
				_boundRect.x=args[0];
				_boundRect.y=args[1];
				_boundRect.width=args[2];
				_boundRect.height=args[3];
				
			}
				break;
		}
	}
	
	public void dispose()
	{
	
	}
	
	public RegionConfig getConfig()
	{
		return _config;
	}
	
	/** 是否包含某单位(已存在) */
	public boolean containsUnit(Unit unit)
	{
		return _containUnits.contains(unit.instanceID);
	}
	
	/** 查询某单位是否在区域内(只判定) */
	public boolean isInRegion(Unit unit)
	{
		if(_config.isClientDrive)
			return false;
		
		if(!_boundRect.containPos(unit.pos.getPos()))
			return false;
		
		switch(_config.shapeType)
		{
			case RegionShapeType.Circle:
			{
				return _scene.pos.calculatePosDistanceSq2D(unit.pos.getPos(),_originPos)<=_disSq;
			}
			case RegionShapeType.Rect:
			{
				return true;
			}
		}
		
		return false;
	}
	
	/** 单位进入区域 */
	public void doEnterRegion(Unit unit)
	{
		Unit old=_containUnits.get(unit.instanceID);
		
		if(old!=null)
		{
			Ctrl.warnLog("单位进入区域时，已存在");
			return;
		}
		
		_containUnits.put(unit.instanceID,unit);
		
		unit.pos.onEnterRegion(this);
		onEnterRegion(unit);
	}
	
	/** 单位离开区域 */
	public void doLeaveRegion(Unit unit)
	{
		Unit old=_containUnits.remove(unit.instanceID);
		
		if(old==null || old!=unit)
		{
			Ctrl.warnLog("单位移出区域时，不存在");
			return;
		}
		
		unit.pos.onLeaveRegion(this);
		onLeaveRegion(unit);
	}
	
	protected void onEnterRegion(Unit unit)
	{
		for(int[] arr:_config.actions)
		{
			doRegionAction(arr,unit,true);
		}
	}
	
	protected void onLeaveRegion(Unit unit)
	{
		for(int[] arr:_config.actions)
		{
			doRegionAction(arr,unit,false);
		}
	}
	
	protected void doRegionAction(int[] args,Unit unit,boolean isEnter)
	{
		switch(args[0])
		{
			case RegionActionType.SelfAddBuff:
			{
				if(unit.canFight())
				{
					if(isEnter)
					{
						unit.fight.getBuffLogic().addBuff(args[1],args[2]);
					}
					else
					{
						unit.fight.getBuffLogic().removeBuffByID(args[1]);
					}
				}
			}
				break;
		}
	}
}
