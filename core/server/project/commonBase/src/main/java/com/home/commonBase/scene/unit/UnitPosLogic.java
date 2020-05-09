package com.home.commonBase.scene.unit;

import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.constlist.scene.PathFindingType;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.data.scene.unit.UnitPosData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.base.UnitLogicBase;
import com.home.commonBase.scene.path.GridSceneMap;
import com.home.commonBase.scene.scene.ScenePosLogic;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;

/** 单位位置逻辑(服务器x,y坐标系为标准笛卡尔系,x正方向右,y正方向上) */
public class UnitPosLogic extends UnitLogicBase
{
	/** 数据 */
	protected UnitPosData _d;
	/** 位置数据 */
	protected PosData _pos;
	/** 朝向数据 */
	protected DirData _dir;
	
	/** 位置朝向数据 */
	private PosDirData _posDir=new PosDirData();
	
	protected ScenePosLogic _scenePosLogic;
	
	/** 临时排序key(搜索单位用) */
	public float tempSortKey=0f;
	
	/** 所在区域组 */
	private IntObjectMap<Region> _inRegions=new IntObjectMap<>(Region[]::new);
	
	/** 移动绑定组 */
	private SList<Unit> _moveBindUnits=new SList<>(Unit[]::new);
	
	private Unit _beMoveBindUnit=null;
	
	/** 是否需要格子标记 */
	private boolean _needCrowedGrid=false;
	private int _collideGridRadius=0;
	
	private GridSceneMap _gMap;
	private boolean _addedCrowed=false;
	
	private int[] _tempArr=new int[2];
	/** 格子坐标 */
	private int _gx=-1;
	private int _gz=-1;
	
	@Override
	public void init()
	{
		super.init();
		
		_scenePosLogic=_scene.pos;
		
		_d=_data.pos;
		
		_posDir.pos=_pos=_d.pos;
		_posDir.dir=_dir=_d.dir;
		
		_needCrowedGrid=false;
		
		if(CommonSetting.monsterNeedCrowedGrid && CommonSetting.pathFindingType==PathFindingType.JPS && _data.identity.type==UnitType.Monster)
		{
			_needCrowedGrid=true;
		}
	}
	
	@Override
	public void afterInit()
	{
		super.afterInit();
		
		if(_needCrowedGrid)
		{
			_gMap=(GridSceneMap)_scenePosLogic.getBaseSceneMap();
			_gMap.getGridPos(_tempArr,_pos);
			_gx=_tempArr[0];
			_gz=_tempArr[1];
			_collideGridRadius=calculateCollideGridRadius(_unit.avatar.getCollideRadius());
			_addedCrowed=true;
			_gMap.addCrowedGrid(_gx,_gz,_collideGridRadius);
		}
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_scenePosLogic=null;
		_d=null;
		_pos=null;
		_dir=null;
		_posDir.pos=null;
		_posDir.dir=null;
		
		tempSortKey=0f;
		
		if(_needCrowedGrid)
		{
			if(_addedCrowed)
			{
				_addedCrowed=false;
				_gMap.removeCrowedGrid(_gx,_gz,_collideGridRadius);
			}
			
			_needCrowedGrid=false;
			_collideGridRadius=0;
			_gMap=null;
			_gx=-1;
			_gz=-1;
		}
	}
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
		
	}
	
	public boolean needCrowedGrid()
	{
		return _needCrowedGrid;
	}
	
	/** 获取位置 */
	public PosData getPos()
	{
		return _pos;
	}
	
	/** 获取朝向 */
	public DirData getDir()
	{
		return _dir;
	}
	
	/** 获取位置朝向数据 */
	public PosDirData getPosDir()
	{
		return _posDir;
	}
	
	/** 设置坐标和朝向 */
	public void setByPosDir(PosDirData posDir)
	{
		_pos.copyPos(posDir.pos);
		_dir.copyDir(posDir.dir);
		
		onSetPos();
		onSetDir();
	}
	
	/** 通过配置设置位置朝向 */
	public void setPosDirByFArr(float[] arr)
	{
		_posDir.setByFArr(arr);
		
		onSetPos();
		onSetDir();
	}
	
	//元方法
	
	/** 更新格子位置 */
	public void setGridPos(PosData pos)
	{
		if(!_needCrowedGrid)
			return;
		
		int[] arr;
		_gMap.getGridPos(arr=_tempArr,pos);
		
		if(arr[0]==_gx && arr[1]==_gz)
			return;
		
		_gMap.removeCrowedGrid(_gx,_gz,_collideGridRadius);
		
		_gx=arr[0];
		_gz=arr[1];
		
		_gMap.addCrowedGrid(_gx,_gz,_collideGridRadius);
	}
	
	/** 当前格子是否独占 */
	public boolean isGridSingle()
	{
		return _gMap.getCrowedGrid(_gx,_gz)<=1;
	}
	
	public int getGridX()
	{
		return _gx;
	}
	
	public int getGridZ()
	{
		return _gz;
	}
	
	/** 设置朝向(不推送) */
	public void setDir(DirData dir)
	{
		_dir.copyDir(dir);
		
		onSetDir();
	}
	
	/** 设置坐标(不推送) */
	public void setPos(PosData pos)
	{
		_pos.copyPos(pos);
		
		onSetPos();
	}
	
	public void onSetPos()
	{
		_unit.aoi.onPosChanged();
		
		if(!_moveBindUnits.isEmpty())
		{
			Unit[] values=_moveBindUnits.getValues();
			Unit v;
			
			for(int i=0,len=_moveBindUnits.size();i<len;++i)
			{
				v=values[i];
				v.pos.setPos(_pos);
			}
		}
	}
	
	public void onSetDir()
	{
		_unit.aoi.onDirChanged();
		
		if(!_moveBindUnits.isEmpty())
		{
			Unit[] values=_moveBindUnits.getValues();
			Unit v;
			
			for(int i=0,len=_moveBindUnits.size();i<len;++i)
			{
				v=values[i];
				v.pos.setDir(_dir);
			}
		}
	}
	
	/** 设置位置立即生效(触发send) */
	public void setPosDirByFArrAbs(float[] arr)
	{
		setPosDirByFArr(arr);
		sendSetPosDir(getPosDir());
	}
	
	/** 设置位置立即生效(触发send) */
	public void setPosAbs(PosData pos)
	{
		setPos(pos);
		sendSetPosDir(getPosDir());
	}
	
	/** 设置位置立即生效(触发send) */
	public void setDirAbs(DirData dir)
	{
		setDir(dir);
		sendSetPosDir(getPosDir());
	}
	
	/** 推送直接设置位置朝向 */
	protected void sendSetPosDir(PosDirData posDir)
	{
	
	}
	
	/** 计算与目标位置距离平方 */
	public float calculateDistanceSq(PosData pos)
	{
		return _scenePosLogic.calculatePosDistanceSq(_pos,pos);
	}
	
	/** 进入区域 */
	public void onEnterRegion(Region region)
	{
		_inRegions.put(region.instanceID,region);
	}
	
	/** 离开区域 */
	public void onLeaveRegion(Region region)
	{
		_inRegions.remove(region.instanceID);
	}
	
	/** 获取所在区域组 */
	public IntObjectMap<Region> getInRegions()
	{
		return _inRegions;
	}
	
	/** 添加移动绑定单位 */
	public void addMoveBindUnit(Unit unit)
	{
		_moveBindUnits.add(unit);
		unit.pos._beMoveBindUnit=_unit;
	}
	
	/** 移除移动绑定单位 */
	public void removeMoveBinUnit(Unit unit)
	{
		_moveBindUnits.removeObj(unit);
		unit.pos._beMoveBindUnit=null;
	}
	
	@Override
	public void preRemove()
	{
		super.preRemove();
		
		if(_beMoveBindUnit!=null)
		{
			_beMoveBindUnit.pos.removeMoveBinUnit(_unit);
		}
		
		if(!_moveBindUnits.isEmpty())
		{
			for(int i=_moveBindUnits.size()-1;i>=0;--i)
			{
				Unit unit=_moveBindUnits.remove(i);
				unit.pos._beMoveBindUnit=null;
			}
		}
	}
	
	private int calculateCollideGridRadius(float collideRadius)
	{
		int v=Math.round(collideRadius/ Global.mapBlockSizeHalf)-1;
		
		return Math.max(v,0);
	}
	
	/** 更新触碰半径 */
	public void refreshCollideRadius()
	{
		if(_needCrowedGrid && _gMap!=null)
		{
			int newC=calculateCollideGridRadius(_unit.avatar.getCollideRadius());
			
			if(newC!=_collideGridRadius)
			{
				_gMap.removeCrowedGrid(_gx,_gz,_collideGridRadius);
				_collideGridRadius=newC;
				_gMap.addCrowedGrid(_gx,_gz,_collideGridRadius);
			}
		}
	}
}
