package com.home.commonSceneBase.scene.scene;

import com.home.commonBase.constlist.scene.SceneAOIType;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.dataEx.scene.SceneAOITowerData;
import com.home.commonBase.dataEx.scene.SocketAOICountData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.SceneAOILogic;
import com.home.commonBase.scene.unit.UnitAOITowerLogic;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.AOITowerRefreshRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.AddUnitRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.RemoveUnitRequest;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.inter.IObjectConsumer;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.pool.ObjectPool;
import com.home.shine.utils.MathUtils;

/** 25宫灯塔AOILogic */
public class SceneAOITowerLogic extends SceneAOILogic
{
	/** 25宫 */
	private static final int ExpandSize=2;
	
	private ObjectPool<SceneAOITowerData> _pool;
	
	private float _originX;
	
	private float _originZ;
	/** aoi宽(减1的) */
	private int _aoiWidth;
	/** aoi长(减1的) */
	private int _aoiLength;
	
	/** 灯塔字典 */
	private SceneAOITowerData[][] _towerDic;
	
	//temp
	
	/** 临时单位组(处理其他单位) */
	private SList<Unit> _tempUnitList=new SList<>(Unit[]::new);
	/** 临时单位组2(处理自己) */
	private SList<Unit> _tempUnitList2=new SList<>(Unit[]::new);
	
	private AOITowerRefreshRequest _refreshMsg;
	
	private IntSet _tempRegions=new IntSet();
	
	//check
	private LongObjectMap<SocketAOICountData> checkDic;
	
	@Override
	public void construct()
	{
		super.construct();
		_aoiType=SceneAOIType.Tower;
		
		if(CommonSetting.openAOICheck)
		{
			checkDic=new LongObjectMap<>(SocketAOICountData[]::new);
		}
	}
	
	@Override
	public void init()
	{
		super.init();
		
		_pool=_scene.getExecutor().towerDataPool;
		
		_originX=_scene.originPos.x;
		_originZ=_scene.originPos.z;
		
		_aoiWidth=(int)Math.ceil(_scene.sizePos.x/Global.aoiTowerSize);
		_aoiLength=(int)Math.ceil(_scene.sizePos.z/Global.aoiTowerSize);
		
		//需要一个新的
		if(_towerDic==null)
		{
			_towerDic=new SceneAOITowerData[_aoiWidth][_aoiLength];
		}
		else
		{
			if(_towerDic.length!=_aoiWidth || _towerDic[0].length!=_aoiLength)
			{
				for(SceneAOITowerData[] arr:_towerDic)
				{
					for(SceneAOITowerData tower:arr)
					{
						if(tower!=null)
						{
							_pool.back(tower);
						}
					}
				}
				
				_towerDic=new SceneAOITowerData[_aoiWidth][_aoiLength];
			}
		}
		
		//减少1，好判断
		--_aoiWidth;
		--_aoiLength;
		
		if(_refreshMsg==null)
		{
			_refreshMsg=AOITowerRefreshRequest.create(new IntList(),new SList<>(UnitData[]::new));
		}
	}
	
	@Override
	public void dispose()
	{
		_pool=null;
	}
	
	/** 计算灯塔x */
	public int countTowerX(float x)
	{
		int re=(int)((x - _originX) / Global.aoiTowerSize);
		
		if(re>_aoiWidth)
			return _aoiWidth;
		
		if(re<0)
			return 0;
		
		return re;
	}
	
	/** 计算灯塔z */
	public int countTowerZ(float z)
	{
		int re=(int)((z-_originZ)/Global.aoiTowerSize);
		
		if(re>_aoiLength)
			return _aoiLength;
		
		if(re<0)
			return 0;
		
		return re;
	}
	
	@Override
	public void unitAdd(Unit unit,boolean needSelf)
	{
		PosData pos=unit.pos.getPos();
		
		UnitAOITowerLogic unitT=unit.aoiTower;
		
		int tx=unitT.tx=countTowerX(pos.x);
		int tz=unitT.tz=countTowerZ(pos.z);
		
		//先添加自己
		unitT.addAround(unit);
		
		long belongPlayerID;
		if((belongPlayerID=unit.aoi.belongPlayerID)>0 && !unit.canFight())
		{
			Unit v=_scene.getCharacterByPlayerID(belongPlayerID);
			if(v!=null && isTowerAround(tx,tz,v.aoiTower.tx,v.aoiTower.tz))
			{
				v.aoiTower.addAround(unit);
				unitT.addAround(v);
			}
		}
		else
		{
			foreachTowerUnit(tx,tz,v->
			{
				if(v.aoi.isAvailable(unit))
				{
					v.aoiTower.addAround(unit);
					unitT.addAround(v);
				}
			});
		}
		
		//加入灯塔
		getTower(tx,tz).addUnit(unit);
		
		radioUnitAdd(unit,needSelf);
	}
	
	@Override
	public void unitRemove(Unit unit,boolean needSelf)
	{
		radioUnitRemove(unit,needSelf);
		
		UnitAOITowerLogic unitT=unit.aoiTower;
		
		getTower(unitT.tx,unitT.tz).removeUnit(unit);
		
		long belongPlayerID;
		if((belongPlayerID=unit.aoi.belongPlayerID)>0 && !unit.canFight())
		{
			Unit v=_scene.getCharacterByPlayerID(belongPlayerID);
			if(v!=null && isTowerAround(unitT.tx,unitT.tz,v.aoiTower.tx,v.aoiTower.tz))
			{
				v.aoiTower.removeAround(unit);
				unitT.removeAround(v);
			}
		}
		else
		{
			foreachTowerUnit(unitT.tx,unitT.tz,v->
			{
				if(v.aoi.isAvailable(unit))
				{
					v.aoiTower.removeAround(unit);
					unitT.removeAround(v);
				}
			});
			
			//unitT.getAroundDic().forEachValueS(v->
			//{
			//	if(v.aoi.isAvailable(unit))
			//	{
			//		v.aoiTower.removeAround(unit);
			//		unitT.removeAround(v);
			//	}
			//});
		}
		
		//移除自己了
		unitT.removeAround(unit);
		
		if(CommonSetting.openAOICheck && unit.isCharacter())
		{
			unit.aoi.clearRecord();
		}
	}
	
	@Override
	public void getAroundUnitsBase(Unit unit,IObjectConsumer<Unit> func)
	{
		PosData pos=unit.pos.getPos();
		
		foreachTowerUnit(countTowerX(pos.x),countTowerZ(pos.z),v->
		{
			if(v.aoi.isAvailable(unit))
			{
				func.accept(v);
			}
		});
	}
	
	@Override
	public boolean unitNeedRatio(Unit unit)
	{
		return !unit.aoiTower.getBeSeeDic().isEmpty();
	}
	
	@Override
	public void radioMessage(Unit unit,BaseRequest request,boolean needSelf)
	{
		if(ShineSetting.openCheck)
		{
			if(!unit.enabled && !(request instanceof RemoveUnitRequest))
			{
				Ctrl.warnLog("不该给移除单位推送消息",unit.getInfo());
			}
		}
		
		IntObjectMap<Unit> dic=unit.aoiTower.getBeSeeDic();
		
		if(!dic.isEmpty())
		{
			long selfID=needSelf ? -1L : unit.identity.controlPlayerID;
			
			toRadioMessage(selfID,request,dic.getValues());
		}
	}
	
	/** 获取某格的字典 */
	public SceneAOITowerData getTower(int tx,int tz)
	{
		SceneAOITowerData[] arr;
		SceneAOITowerData tower;
		
		if((tower=(arr=_towerDic[tx])[tz])==null)
		{
			arr[tz]=tower=_pool.getOne();
		}
		
		return tower;
	}
	
	/** 遍历塔(可能为空) */
	private void foreachTower(int tx,int tz,ObjectCall<SceneAOITowerData> func)
	{
		//25宫
		int startX;
		if((startX=tx-ExpandSize)<0)
			startX=0;
		
		int endX;
		if((endX=tx+ExpandSize)>_aoiWidth)
			endX=_aoiWidth;
		
		int startZ;
		if((startZ=tz-ExpandSize)<0)
			startZ=0;
		
		int endZ;
		if((endZ=tz+ExpandSize)>_aoiLength)
			endZ=_aoiLength;
		
		SceneAOITowerData[] arr;
		
		for(int i=startX;i<=endX;i++)
		{
			arr=_towerDic[i];
			
			for(int j=startZ;j<=endZ;j++)
			{
				func.apply(arr[j]);
			}
		}
	}
	
	/** 遍历塔中单位 */
	private void foreachTowerUnit(int tx,int tz,IObjectConsumer<Unit> func)
	{
		foreachTower(tx,tz,tower->
		{
			if(tower!=null && !tower.dic.isEmpty())
			{
				Unit[] values;
				Unit v;
				
				for(int k=(values=tower.dic.getValues()).length-1;k>=0;--k)
				{
					if((v=values[k])!=null)
					{
						func.accept(v);
					}
				}
			}
		});
	}
	
	/** 遍历塔中单位 */
	private void foreachTowerCharacter(int tx,int tz,ObjectCall<Unit> func)
	{
		foreachTower(tx,tz,tower->
		{
			if(tower!=null && !tower.characterDic.isEmpty())
			{
				Unit[] values;
				Unit v;
				
				for(int k=(values=tower.characterDic.getValues()).length-1;k>=0;--k)
				{
					if((v=values[k])!=null)
					{
						func.apply(v);
					}
				}
			}
		});
	}
	
	@Override
	public void unitRefreshPos(Unit unit)
	{
		PosData pos=unit.pos.getPos();
		
		UnitAOITowerLogic unitT=unit.aoiTower;
		
		countTowerRegion(unit,true);
		
		float mx=_originX+(unitT.tx*Global.aoiTowerSize);
		float mz=_originZ+(unitT.tz*Global.aoiTowerSize);
		
		float lx=mx-Global.aoiTowerExpandSize;
		float rx=mx+Global.aoiTowerSize+Global.aoiTowerExpandSize;
		
		float lz=mz-Global.aoiTowerExpandSize;
		float rz=mz+Global.aoiTowerSize+Global.aoiTowerExpandSize;
		
		float px=pos.x;
		float pz=pos.z;
		
		//还在本格
		if(lx<=px && px<=rx && lz<=pz && pz<=rz)
		{
			return;
		}
		
		int tx=countTowerX(px);
		int tz=countTowerZ(pz);
		
		//相同(已经不需要判断了)
		if(tx==unitT.tx && tz==unitT.tz)
			return;
		
		//25宫
		int startNewX;
		if((startNewX=tx-ExpandSize)<0)
			startNewX=0;
		
		int endNewX;
		if((endNewX=tx+ExpandSize)>_aoiWidth)
			endNewX=_aoiWidth;
		
		int startNewZ;
		if((startNewZ=tz-ExpandSize)<0)
			startNewZ=0;
		
		int endNewZ;
		if((endNewZ=tz+ExpandSize)>_aoiLength)
			endNewZ=_aoiLength;
		
		//25宫
		int startOldX;
		if((startOldX=unitT.tx-ExpandSize)<0)
			startOldX=0;
		
		int endOldX;
		if((endOldX=unitT.tx+ExpandSize)>_aoiWidth)
			endOldX=_aoiWidth;
		
		int startOldZ;
		if((startOldZ=unitT.tz-ExpandSize)<0)
			startOldZ=0;
		
		int endOldZ;
		if((endOldZ=unitT.tz+ExpandSize)>_aoiLength)
			endOldZ=_aoiLength;
		
		SceneAOITowerData[][] towerDic=_towerDic;
		SceneAOITowerData[] arr;
		SceneAOITowerData tower;
		
		//先遍历new
		for(int i=startNewX;i<=endNewX;i++)
		{
			arr=towerDic[i];
			
			for(int j=startNewZ;j<=endNewZ;j++)
			{
				if((tower=arr[j])!=null && !tower.dic.isEmpty())
				{
					if(ShineSetting.openCheck)
					{
						if(tower.foreachFlag)
						{
							Ctrl.throwError("aoi标记出错");
						}
					}
					
					tower.foreachFlag=true;
				}
			}
		}
		
		SList<Unit> tempUnitList;
		(tempUnitList=_tempUnitList).clear();
		
		IntList removeUnits;
		(removeUnits=_refreshMsg.removeUnits).clear();
		
		//再遍历old
		for(int i=startOldX;i<=endOldX;i++)
		{
			arr=towerDic[i];
			
			for(int j=startOldZ;j<=endOldZ;j++)
			{
				if((tower=arr[j])!=null && !tower.dic.isEmpty())
				{
					if(tower.foreachFlag)
					{
						tower.foreachFlag=false;
					}
					else
					{
						Unit[] values;
						Unit v;
						
						for(int k=(values=tower.dic.getValues()).length-1;k>=0;--k)
						{
							if((v=values[k])!=null)
							{
								//不是自己
								if(v.instanceID!=unit.instanceID && v.aoi.isAvailable(unit))
								{
									if(v.aoiTower.tryRemoveAround(unit))
									{
										//从视野中移除
										tempUnitList.add(v);
									}
									
									if(unitT.tryRemoveAround(v))
									{
										//视野变更消息
										removeUnits.add(v.instanceID);
									}
								}
							}
						}
					}
				}
			}
		}
		
		if(!tempUnitList.isEmpty())
		{
			toRadioMessage(unit.identity.controlPlayerID,RemoveUnitRequest.create(unit.instanceID),tempUnitList.getValues(),tempUnitList.size());
			tempUnitList.clear();
		}
		
		SList<Unit> tempUnitList2;
		(tempUnitList2=_tempUnitList2).clear();
		
		//再遍历new
		for(int i=startNewX;i<=endNewX;i++)
		{
			arr=towerDic[i];
			
			for(int j=startNewZ;j<=endNewZ;j++)
			{
				if((tower=arr[j])!=null && !tower.dic.isEmpty())
				{
					//还在
					if(tower.foreachFlag)
					{
						tower.foreachFlag=false;
						
						Unit[] values;
						Unit v;
						
						for(int k=(values=tower.dic.getValues()).length-1;k>=0;--k)
						{
							if((v=values[k])!=null)
							{
								//不是自己
								if(v.instanceID!=unit.instanceID && v.aoi.isAvailable(unit))
								{
									if(v.aoiTower.tryAddAround(unit))
									{
										tempUnitList.add(v);
									}
									
									if(unitT.tryAddAround(v))
									{
										//视野变更消息
										tempUnitList2.add(v);
									}
								}
							}
						}
					}
				}
			}
		}
		
		if(!tempUnitList.isEmpty())
		{
			toRadioMessage(unit.identity.controlPlayerID,createUnitAddRequestForOther(unit),tempUnitList.getValues(),tempUnitList.size());
			tempUnitList.clear();
		}
		
		boolean tempB;
		if((tempB=!tempUnitList2.isEmpty()) || !removeUnits.isEmpty())
		{
			//有添加
			if(tempB)
			{
				SList<UnitData> addUnits;
				(addUnits=_refreshMsg.addUnits).clear();
				
				Unit[] values=tempUnitList2.getValues();
				Unit v;
				
				for(int i=tempUnitList2.size()-1;i>=0;--i)
				{
					(v=values[i]).beforeWrite();
					
					//是否可战斗
					if(v.canFight())
					{
						v.fight.switchSendOther();
					}
					
					addUnits.add(v.getUnitData());
				}
			}
			
			unit.send(_refreshMsg);
			
			if(CommonSetting.openAOICheck)
			{
				UnitData[] values=_refreshMsg.addUnits.getValues();
				UnitData v;
				
				for(int i=0,len=_refreshMsg.addUnits.size();i<len;++i)
				{
					v=values[i];
					unit.aoi.recordAddUnit(v.instanceID);
				}
				
				int[] values1=_refreshMsg.removeUnits.getValues();
				int v1;
				
				for(int i1=0,len1=_refreshMsg.removeUnits.size();i1<len1;++i1)
				{
					v1=values1[i1];
					unit.aoi.recordRemoveUnit(v1);
				}
			}
			
			//给个新的
			_refreshMsg=AOITowerRefreshRequest.create(new IntList(),new SList<>(UnitData[]::new));
			
			//有添加
			if(tempB)
			{
				tempUnitList2.forEachAndClear(v->
				{
					if(v.canFight())
					{
						v.fight.endSwitchSend();
					}
				});
			}
		}
		
		getTower(unitT.tx,unitT.tz).removeUnit(unit);
		unitT.tx=tx;
		unitT.tz=tz;
		getTower(tx,tz).addUnit(unit);
		
		countTowerRegion(unit,false);
	}
	
	/** 灯塔是否相邻 */
	public boolean isTowerAround(int tx1,int tz1,int tx2,int tz2)
	{
		return Math.abs(tx2-tx1)<=ExpandSize && Math.abs(tz2-tz1)<=ExpandSize;
	}
	
	/** 灯塔是否相邻 */
	public boolean isTowerAround(Unit unit1,Unit unit2)
	{
		return isTowerAround(unit1.aoiTower.tx,unit1.aoiTower.tz,unit2.aoiTower.tx,unit2.aoiTower.tz);
	}
	
	@Override
	public void onAddUnit(Unit unit)
	{
		UnitAOITowerLogic unitT=unit.aoiTower;
		SceneAOITowerData tower=getTower(unitT.tx,unitT.tz);
		
		if(!tower.regionDic.isEmpty())
		{
			Region[] values;
			Region v;
			
			for(int i=(values=tower.regionDic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					if(v.isInRegion(unit))
					{
						v.doEnterRegion(unit);
					}
				}
			}
		}
	}
	
	private void countTowerRegion(Unit unit,boolean isFirst)
	{
		if(isFirst)
		{
			_tempRegions.clear();
		}
		
		UnitAOITowerLogic unitT=unit.aoiTower;
		SceneAOITowerData tower=getTower(unitT.tx,unitT.tz);
		
		if(!tower.regionDic.isEmpty())
		{
			Region[] values;
			Region v;
			
			for(int i=(values=tower.regionDic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					if(isFirst)
					{
						_tempRegions.add(v.instanceID);
					}
					else
					{
						if(_tempRegions.contains(v.instanceID))
						{
							continue;
						}
					}
					
					if(v.containsUnit(unit))
					{
						if(!v.isInRegion(unit))
						{
							v.doLeaveRegion(unit);
						}
					}
					else
					{
						if(v.isInRegion(unit))
						{
							v.doEnterRegion(unit);
						}
					}
				}
			}
		}
	}
	
	public SocketAOICountData getSocketAOICheck(long playerID)
	{
		return checkDic.computeIfAbsent(playerID,k->new SocketAOICountData());
	}
	
	@Override
	protected BaseRequest createAddUnitRequest(UnitData data)
	{
		return AddUnitRequest.create(data);
	}
	
	@Override
	protected BaseRequest createUnitRemoveRequest(int instanceID)
	{
		return RemoveUnitRequest.create(instanceID);
	}
	
}
