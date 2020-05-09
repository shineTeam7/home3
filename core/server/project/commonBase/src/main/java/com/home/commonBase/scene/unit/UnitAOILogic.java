package com.home.commonBase.scene.unit;

import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.dataEx.scene.SocketAOICountData;
import com.home.commonBase.dataEx.scene.UnitAOICountData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.base.UnitLogicBase;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.inter.IObjectConsumer;

/** 单位AOI逻辑体 */
public class UnitAOILogic extends UnitLogicBase
{
	/** 是否需要视野(isCharacter) */
	public boolean needVision=false;
	/** 是否需要周围单位统计 */
	public boolean needAround=false;
	
	/** 归属角色id */
	public long belongPlayerID=-1L;
	
	/** 绑定视野单位(同步位置血量) */
	protected IntObjectMap<Unit> _bindVisionUnits=new IntObjectMap<>(Unit[]::new);
	
	@Override
	public void construct()
	{
		super.construct();
		
	}
	
	@Override
	public void init()
	{
		super.init();
		
		needVision=_unit.identity.isCharacter();
		needAround=BaseC.constlist.unit_canFight(_unit.getType());
		
		belongPlayerID=_data.normal.belongPlayerID;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		belongPlayerID=-1L;
		_bindVisionUnits.clear();
	}
	
	@Override
	public void preRemove()
	{
		super.preRemove();
		
		//解绑
		_bindVisionUnits.forEachValueS(this::unBindEachOther);
	}
	
	/** 位置改变 */
	public void onPosChanged()
	{
	
	}
	
	/** 朝向改变 */
	public void onDirChanged()
	{
	
	}
	
	/** 立即更新AOI */
	public void refreshAOI()
	{
	
	}
	
	/** 遍历周围单位 */
	public void forEachAroundUnits(IObjectConsumer<Unit> func)
	{
		_scene.aoi.getAroundUnitsBase(_unit,func);
	}
	
	/** 遍历可视单位(进入场景用) */
	public void forEachCanSeeUnits(IObjectConsumer<Unit> func)
	{
		_scene.aoi.getAroundUnitsBase(_unit,func);
	}
	
	/** 是否归属于某单位 */
	public boolean isBelongTo(Unit unit)
	{
		return belongPlayerID>0 && belongPlayerID==unit.identity.playerID;
	}
	
	/** 是否互相可Around(用来处理，只被xxx可见) */
	public boolean isAvailable(Unit unit)
	{
		long belongPlayerID;
		if((belongPlayerID=this.belongPlayerID)>0 && unit.identity.playerID!=belongPlayerID)
		{
			return false;
		}
		
		if((belongPlayerID=unit.aoi.belongPlayerID)>0 && _unit.identity.playerID!=belongPlayerID)
		{
			return false;
		}
		
		return true;
	}
	
	/** 默认全可见 */
	public boolean isSee(Unit unit)
	{
		return isSee(unit.instanceID);
	}
	
	/** 默认全可见 */
	public boolean isSee(int instanceID)
	{
		return true;
	}
	
	/** 互相绑定 */
	public void bindEachOther(Unit unit)
	{
		bindEachOther(unit,true);
	}
	
	/** 互相绑定(abs:是否立即生效) */
	public void bindEachOther(Unit unit,boolean isAbs)
	{
		addBindUnit(unit,isAbs);
		unit.aoi.addBindUnit(_unit,isAbs);
	}
	
	/** 互相解绑定 */
	public void unBindEachOther(Unit unit)
	{
		removeBindUnit(unit);
		unit.aoi.removeBindUnit(_unit);
	}
	
	/** 添加视野绑定单位 */
	public void addBindUnit(Unit unit,boolean isAbs)
	{
		if(ShineSetting.openCheck)
		{
			if(_bindVisionUnits.contains(unit.instanceID))
			{
				Ctrl.errorLog("添加视野绑定单位时,重复添加");
			}
		}
		
		_bindVisionUnits.put(unit.instanceID,unit);
	}
	
	/** 移除视野绑定单位 */
	public void removeBindUnit(Unit unit)
	{
		Unit removed=_bindVisionUnits.remove(unit.instanceID);
		
		if(ShineSetting.openCheck)
		{
			if(removed==null)
			{
				Ctrl.errorLog("移除视野绑定单位时,重复移除");
			}
		}
	}
	
	/** 获取绑定单位组 */
	public IntObjectMap<Unit> getBindVisionUnits()
	{
		return _bindVisionUnits;
	}
	
	public SocketAOICountData getSelfAOICheck()
	{
		return null;
	}
	
	public void recordAddUnit(int instanceID)
	{
		if(!CommonSetting.openAOICheck)
			return;
		
		SocketAOICountData cData;
		if((cData=getSelfAOICheck())==null)
			return;
		
		cData.getCountData(instanceID).addMsg(true,new Exception());
	}
	
	public void recordRemoveUnit(int instanceID)
	{
		if(!CommonSetting.openAOICheck)
			return;
		
		SocketAOICountData cData;
		if((cData=getSelfAOICheck())==null)
			return;
		
		cData.getCountData(instanceID).addMsg(false,new Exception());
	}
	
	public void recordAddAround(int instanceID)
	{
		if(!CommonSetting.openAOICheck)
			return;
		
		SocketAOICountData cData;
		if((cData=getSelfAOICheck())==null)
			return;
		
		cData.getCountData(instanceID).addAround(true,new Exception());
	}
	
	public void recordRemoveAround(int instanceID)
	{
		if(!CommonSetting.openAOICheck)
			return;
		
		SocketAOICountData cData;
		if((cData=getSelfAOICheck())==null)
			return;
		
		cData.getCountData(instanceID).addAround(false,new Exception());
	}
	
	public void clearRecord()
	{
		if(!CommonSetting.openAOICheck)
			return;
		
		SocketAOICountData cData;
		if((cData=getSelfAOICheck())==null)
			return;
		
		cData.getCheckDic().clear();
	}
	
	public void clearMsg()
	{
		if(!CommonSetting.openAOICheck)
			return;
		
		SocketAOICountData cData;
		if((cData=getSelfAOICheck())==null)
			return;
		
		IntObjectMap<UnitAOICountData> checkDic=cData.getCheckDic();
		
		if(!checkDic.isEmpty())
		{
			checkDic.forEachValue(v->
			{
				v.clearMsg();
			});
		}
	}
	
	/** 周围是否没有角色 */
	public boolean isNoCharacterAround()
	{
		return false;
	}
}
