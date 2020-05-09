package com.home.commonBase.scene.unit;

import com.home.commonBase.constlist.generate.AOIPriorityType;
import com.home.commonBase.constlist.scene.SceneAOIType;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.inter.IObjectConsumer;

public class UnitAOITowerLogic extends UnitAOILogic
{
	/** 灯塔x */
	public int tx=-1;
	/** 灯塔z */
	public int tz=-1;
	
	/** 当前是否激活(该AOILogic) */
	private boolean _active=false;
	//可见部分
	
	/** 可见组(包括自己) */
	private IntObjectMap<Unit> _seeDic=new IntObjectMap<>();
	/** 当前视野优先级 */
	private int _seePriority=AOIPriorityType.Self;
	/** 周围组(包括自己) */
	private IntObjectMap<Unit> _aroundDic=new IntObjectMap<>();
	/** 周围玩家单位数 */
	private int _aroundCharacterNum=0;
	/** 可视单位优先级组 */
	private IntObjectMap<Unit>[] _seePriorityDic=new IntObjectMap[AOIPriorityType.size];
	/** 剩余单位优先级组 */
	private IntObjectMap<Unit>[] _lastPriorityDic=new IntObjectMap[AOIPriorityType.size];
	/** 优先级记录组 */
	private IntIntMap _recordPriorityDic=new IntIntMap();
	/** 绑定单位记录组 */
	private IntSet _bindVisionRecord=new IntSet();
	//被可见部分
	/** 被可见组(包括自己) */
	private IntObjectMap<Unit> _beSeeDic=new IntObjectMap<>(Unit[]::new);
	
	//dirty
	private boolean _posDirty=false;
	private boolean _dirDirty=false;
	
	@Override
	public void init()
	{
		super.init();
		
		_active=_scene.aoi.getAOIType()==SceneAOIType.Tower;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		if(ShineSetting.openCheck)
		{
			if(!_aroundDic.isEmpty())
			{
				Ctrl.errorLog("UnitAOITowerLogic dispose时,_aroundDic组不为空");
				_aroundDic.clear();
			}
			
			if(!_seeDic.isEmpty())
			{
				Ctrl.errorLog("UnitAOITowerLogic dispose时,_seeDic组不为空");
				_seeDic.clear();
			}
			
			if(!_beSeeDic.isEmpty())
			{
				Ctrl.errorLog("UnitAOITowerLogic dispose时,_beSeeDic组不为空");
				_beSeeDic.clear();
			}
		}
		
		_active=true;
		_aroundCharacterNum=0;
		_posDirty=false;
		tx=-1;
		tz=-1;
	}
	
	@Override
	public void onPiece(int delay)
	{
		if(!_active)
			return;
		
		checkRefresh();
	}
	
	private void checkRefresh()
	{
		boolean needSimple=_posDirty || _dirDirty;
		_dirDirty=false;
		
		refreshAOI();
		
		if(needSimple)
		{
			doSimplePosChanged();
		}
	}
	
	@Override
	public void refreshAOI()
	{
		if(_posDirty)
		{
			_posDirty=false;
			_scene.aoi.unitRefreshPos(_unit);
		}
	}
	
	private void findNextSeePriority()
	{
		int p=_seePriority;
		IntObjectMap<Unit> dic=_seePriorityDic[p];
		
		while(p>AOIPriorityType.Self && (dic==null || dic.isEmpty()))
		{
			dic=_seePriorityDic[p];
			--p;
		}
		
		_seePriority=p;
	}
	
	public boolean isAround(Unit unit)
	{
		return _aroundDic.contains(unit.instanceID);
	}
	
	/** 添加一个单位进入周围(返回是否添加进视野) */
	public boolean addAround(Unit unit)
	{
		if(!needAround)
			return false;
		
		int instanceID=unit.instanceID;
		
		if(ShineSetting.openCheck)
		{
			if(_aroundDic.contains(instanceID))
			{
				Ctrl.errorLog("around组添加时,单位已存在",instanceID);
				return false;
			}
		}
		
		_aroundDic.put(instanceID,unit);
		
		if(unit.isCharacter())
		{
			++_aroundCharacterNum;
			//激活
			_unit.ai.activeAI();
		}
		
		if(!needVision)
			return false;
		
		if(CommonSetting.openAOICheck)
			recordAddAround(instanceID);
		
		if(Global.aoiSeeMax<=0)
		{
			addSee(unit);
			return true;
		}
		else
		{
			int unitPriority=getUnitPriority(unit);
			_recordPriorityDic.put(instanceID,unitPriority);
			
			//还有可用数
			if(_seeDic.size()<Global.aoiSeeMax)
			{
				addSee(unit);
				addToSeePriority(unit,unitPriority);
				return true;
			}
			else
			{
				//优先级更高
				if(unitPriority<_seePriority)
				{
					IntObjectMap<Unit> dic=_seePriorityDic[_seePriority];
					
					if(dic==null)
					{
						Ctrl.throwError("不应该找不到");
						return false;
					}
					
					Unit one=dic.getEver();
					
					dic.remove(one.instanceID);
					removeSee(one);
					sendRemoveOne(one);
					addToLastPriority(one,_recordPriorityDic.get(one.instanceID));
					
					if(dic.isEmpty())
					{
						findNextSeePriority();
					}
					
					addToSeePriority(unit,unitPriority);
					addSee(unit);
					return true;
				}
				else
				{
					addToLastPriority(unit,unitPriority);
					return false;
				}
			}
		}
	}
	
	/** 移除周围(返回是否从视野中移除) */
	public boolean removeAround(Unit unit)
	{
		if(!needAround)
			return false;
		
		int instanceID=unit.instanceID;
		
		if(ShineSetting.openCheck)
		{
			if(!_aroundDic.contains(instanceID))
			{
				Ctrl.errorLog("around组移除时,单位不存在",instanceID);
				return false;
			}
		}
		
		if(_aroundDic.remove(instanceID)!=null)
		{
			if(unit.isCharacter())
				--_aroundCharacterNum;
		}
		
		if(!needVision)
			return false;
		
		if(CommonSetting.openAOICheck)
			recordRemoveAround(instanceID);
		
		if(Global.aoiSeeMax<=0)
		{
			removeSee(unit);
			return true;
		}
		
		int unitPriority=_recordPriorityDic.remove(instanceID);
		
		//在当前组里
		if(_seeDic.remove(instanceID)!=null)
		{
			unit.aoiTower._beSeeDic.remove(_unit.instanceID);
			
			IntObjectMap<Unit> dic=_seePriorityDic[unitPriority];
			
			if(dic==null)
			{
				Ctrl.throwError("不应该找不到");
				return false;
			}
			
			dic.remove(instanceID);
			
			//还有富余
			if(_aroundDic.size()>_seeDic.size())
			{
				int p=_seePriority;
				IntObjectMap<Unit> lastDic=_lastPriorityDic[p];
				
				//有备用
				while(p<AOIPriorityType.size && (lastDic==null || lastDic.isEmpty()))
				{
					++p;
					lastDic=_lastPriorityDic[p];
				}
				
				if(lastDic==null || lastDic.isEmpty())
				{
					Ctrl.throwError("出错，不应该为空");
				}
				
				Unit one=lastDic.getEver();
				lastDic.remove(one.instanceID);
				addSee(one);
				sendAddOne(one);
				addToSeePriority(one,p);
			}
			else
			{
				//是当前优先级，并且空了
				if(unitPriority==_seePriority && dic.isEmpty())
				{
					findNextSeePriority();
				}
			}
			
			return true;
		}
		else
		{
			removeFromLastPriority(unit,unitPriority);
			
			return false;
		}
	}
	
	/** 场景添加Around(位置变更用) */
	public boolean tryAddAround(Unit unit)
	{
		if(!needAround)
			return false;
		
		//绑定单位
		if(_bindVisionUnits.contains(unit.instanceID))
		{
			_bindVisionRecord.add(unit.instanceID);
			
			if(!isAround(unit))
			{
				return addAround(unit);
			}
			
			return false;
		}
		
		return addAround(unit);
	}
	
	/** 场景移除Around(位置变更用) */
	public boolean tryRemoveAround(Unit unit)
	{
		if(!needAround)
			return false;
		
		//绑定单位
		if(_bindVisionUnits.contains(unit.instanceID))
		{
			_bindVisionRecord.remove(unit.instanceID);
			return false;
		}
		
		return removeAround(unit);
	}
	
	public IntObjectMap<Unit> getAroundDic()
	{
		return _aroundDic;
	}
	
	/** 优先级变更 */
	protected void priorityChange(Unit unit)
	{
		if(!needVision)
			return;
		
		if(Global.aoiSeeMax<=0)
			return;
		
		int instanceID=unit.instanceID;
		
		int oldPriority=_recordPriorityDic.get(instanceID);
		
		int unitPriority=getUnitPriority(unit);
		
		//相同
		if(oldPriority==unitPriority)
			return;
		
		_recordPriorityDic.put(instanceID,unitPriority);
		
		//还有可用数
		if(_seeDic.size()<=Global.aoiSeeMax)
		{
			IntObjectMap<Unit> dic;
			//更换
			(dic=_seePriorityDic[oldPriority]).remove(instanceID);
			_seePriorityDic[unitPriority].put(instanceID,unit);

			if(dic.isEmpty())
				findNextSeePriority();

			return;
		}
		else
		{
			boolean lastIsSee=_seeDic.contains(instanceID);

			if(lastIsSee)
			{
				//先删
				_seePriorityDic[oldPriority].remove(instanceID);
				
				//需要掉
				if(unitPriority>_seePriority)
				{
					int p=_seePriority;
					IntObjectMap<Unit> lastDic=_lastPriorityDic[p];
					
					//有备用
					while(p<AOIPriorityType.size && (lastDic==null || lastDic.isEmpty()))
					{
						++p;
						lastDic=_lastPriorityDic[p];
					}
					
					if(lastDic==null || lastDic.isEmpty())
					{
						Ctrl.throwError("出错，不应该为空");
					}
					
					Unit one=lastDic.getEver();
					lastDic.remove(one.instanceID);
					addSee(one);
					sendAddOne(one);
					addToSeePriority(one,p);
					

					removeSee(unit);
					sendRemoveOne(unit);
					addToLastPriority(unit,unitPriority);
				}
				else
				{
					addToSeePriority(unit,unitPriority);
				}
			}
			else
			{
				//先删
				_lastPriorityDic[oldPriority].remove(instanceID);
				
				//优先级更高
				if(unitPriority<_seePriority)
				{
					IntObjectMap<Unit> dic=_seePriorityDic[_seePriority];
					
					if(dic==null)
					{
						Ctrl.throwError("不应该找不到");
					}
					
					Unit one=dic.getEver();
					
					dic.remove(one.instanceID);
					removeSee(one);
					sendRemoveOne(one);
					addToLastPriority(one,_recordPriorityDic.get(one.instanceID));
					
					if(dic.isEmpty())
					{
						findNextSeePriority();
					}
					
					addToSeePriority(unit,unitPriority);
					addSee(unit);
					
					sendAddOne(unit);
				}
				else
				{
					addToLastPriority(unit,unitPriority);
				}
			}
		}
	}
	
	/** 添加到可见 */
	private void addSee(Unit unit)
	{
		_seeDic.put(unit.instanceID,unit);
		unit.aoiTower._beSeeDic.put(_unit.instanceID,_unit);
	}
	
	private void removeSee(Unit unit)
	{
		_seeDic.remove(unit.instanceID);
		unit.aoiTower._beSeeDic.remove(_unit.instanceID);
	}
	
	/** 是否可见某单位 */
	@Override
	public boolean isSee(int instanceID)
	{
		return _seeDic.contains(instanceID);
	}
	
	private void addToSeePriority(Unit unit,int priority)
	{
		IntObjectMap<Unit> dic=_seePriorityDic[priority];
		
		if(dic==null)
		{
			_seePriorityDic[priority]=dic=new IntObjectMap<>(Unit[]::new);
		}
		
		dic.put(unit.instanceID,unit);
		
		if(_seePriority<priority)
			_seePriority=priority;
	}
	
	private void removeFromSeePriority(Unit unit,int priority)
	{
		IntObjectMap<Unit> dic=_seePriorityDic[priority];
		
		if(dic!=null)
		{
			dic.remove(unit.instanceID);
		}
	}
	
	private void addToLastPriority(Unit unit,int priority)
	{
		IntObjectMap<Unit> dic=_lastPriorityDic[priority];
		
		if(dic==null)
		{
			_lastPriorityDic[priority]=dic=new IntObjectMap<>(Unit[]::new);
		}
		
		dic.put(unit.instanceID,unit);
	}
	
	private void removeFromLastPriority(Unit unit,int priority)
	{
		IntObjectMap<Unit> dic=_lastPriorityDic[priority];
		
		if(dic!=null)
		{
			dic.remove(unit.instanceID);
		}
	}
	
	/** 获取单位的视野优先级(对于当前单位而言) */
	protected int getUnitPriority(Unit unit)
	{
		if(unit==_unit)
			return AOIPriorityType.Self;
		//归属自己
		if(unit.aoi.isBelongTo(_unit))
			return AOIPriorityType.Self;
		//归属目标
		if(_unit.aoi.isBelongTo(unit))
			return AOIPriorityType.Self;
		//绑定
		if(_bindVisionUnits.contains(unit.instanceID))
			return AOIPriorityType.Self;
		
		return AOIPriorityType.Normal;
	}
	
	protected void sendAddOne(Unit unit)
	{
	
	}
	
	protected void sendRemoveOne(Unit unit)
	{
	
	}
	
	public IntObjectMap<Unit> getBeSeeDic()
	{
		return _beSeeDic;
	}
	
	/** 添加视野绑定单位 */
	@Override
	public void addBindUnit(Unit unit,boolean isAbs)
	{
		super.addBindUnit(unit,isAbs);
		
		if(isAround(unit))
		{
			_bindVisionRecord.add(unit.instanceID);
			priorityChange(unit);
		}
		else
		{
			if(isAbs)
			{
				addAround(unit);
			}
		}
	}
	
	/** 移除视野绑定单位 */
	@Override
	public void removeBindUnit(Unit unit)
	{
		super.removeBindUnit(unit);
		
		//存在
		if(_bindVisionRecord.remove(unit.instanceID))
		{
			priorityChange(unit);
		}
		else
		{
			//在才remove
			if(isAround(unit))
			{
				removeAround(unit);
			}
		}
	}
	
	@Override
	public void forEachAroundUnits(IObjectConsumer<Unit> func)
	{
		if(!_active)
		{
			super.forEachAroundUnits(func);
			return;
		}
		
		if(!_aroundDic.isEmpty())
		{
			_aroundDic.forEachValueS(func);
		}
	}
	
	public void forEachCanSeeUnits(IObjectConsumer<Unit> func)
	{
		if(!_active)
		{
			super.forEachCanSeeUnits(func);
			return;
		}
		
		if(!_seeDic.isEmpty())
		{
			_seeDic.forEachValueS(func);
		}
	}
	
	@Override
	public void onPosChanged()
	{
		if(!_active)
			return;
		
		_posDirty=true;
		
		if(CommonSetting.refreshAOIAbs)
		{
			checkRefresh();
		}
	}
	
	@Override
	public void onDirChanged()
	{
		if(!_active)
			return;
		
		_dirDirty=true;
	}
	
	protected void doSimplePosChanged()
	{
	
	}
	
	/** 周围是否没有角色 */
	@Override
	public boolean isNoCharacterAround()
	{
		return _aroundCharacterNum==0;
	}
}
