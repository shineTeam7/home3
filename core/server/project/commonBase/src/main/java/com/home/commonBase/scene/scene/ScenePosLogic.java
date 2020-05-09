package com.home.commonBase.scene.scene;

import com.home.commonBase.constlist.scene.PathFindingType;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.SceneLogicBase;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.path.BaseSceneMap;
import com.home.commonBase.scene.path.RecastSceneMap;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;

/** 场景位置逻辑 */
public class ScenePosLogic extends SceneLogicBase
{
	private PosData _originPos;
	
	private PosData _endPos;
	
	private FindCircleNearestTemp _circleNearestTemp=new FindCircleNearestTemp();
	
	private BaseSceneMap _map;
	
	@Override
	public void construct()
	{
		switch(CommonSetting.pathFindingType)
		{
			case PathFindingType.JPS:
			{
				_map=BaseC.factory.createGridSceneMap();
			}
				break;
			case PathFindingType.Recast:
			{
				_map=new RecastSceneMap();
			}
				break;
		}
		
		if(_map!=null)
		{
			_map.setScene(_scene);
		}
	}

	@Override
	public void init()
	{
		_originPos=_scene.originPos;
		_endPos=_scene.endPos;
		
		if(_map!=null)
		{
			_map.init();
		}
	}

	@Override
	public void dispose()
	{
		_originPos=null;
		_endPos=null;
		
		if(_map!=null)
		{
			_map.dispose();
		}
	}

	@Override
	public void onFrame(int delay)
	{

	}
	
	public FindCircleNearestTemp getCircleNearestTemp()
	{
		return _circleNearestTemp;
	}
	
	public static class FindCircleNearestTemp
	{
		public Unit unit;
		
		public float dis;
	}
	
	/** 矫正点(将坐标归到可用范围) */
	public void clampPos(PosData pos)
	{
		PosData originPos=_originPos;
		PosData endPos=_endPos;
		
		pos.x=MathUtils.clamp(pos.x,originPos.x,endPos.x);
		pos.y=MathUtils.clamp(pos.y,originPos.y,endPos.y);
		pos.z=MathUtils.clamp(pos.z,originPos.z,endPos.z);
	}
	
	/** 计算坐标和值(判定用) */
	public float calculatePosSum(PosData p0,PosData p1)
	{
		if(CommonSetting.is3D)
		{
			if(CommonSetting.sceneCalculateUse2D)
				return Math.abs(p1.x-p0.x) + Math.abs(p1.z-p0.z);
			else
				return Math.abs(p1.x-p0.x) + Math.abs(p1.y-p0.y) + Math.abs(p1.z-p0.z);
		}
		else
		{
			if(CommonSetting.isZHeight)
				return Math.abs(p1.x-p0.x) + Math.abs(p1.y-p0.y);
			else
				return Math.abs(p1.x-p0.x) + Math.abs(p1.z-p0.z);
		}
	}
	
	/** 返回点1对于点0的朝向(from:p0,to:p1) */
	public void calculateDirByPos(DirData re,PosData p0,PosData p1)
	{
		if(CommonSetting.is3D)
		{
			float d1;
			float d2;
			
			if(CommonSetting.isZHeight)
			{
				re.direction=(float)Math.atan2(d1=(p1.y-p0.y),d2=(p1.x-p0.x));
				
				if(CommonSetting.sceneCalculateUse2D || MathUtils.floatEquals(p0.z,p1.z))
				{
					re.directionX=0f;
				}
				else
				{
					re.directionX=(float)Math.atan2(p1.z - p0.z,Math.sqrt(d1 * d1 + d2 * d2));
				}
			}
			else
			{
				re.direction=(float)Math.atan2(d1=(p1.z-p0.z),d2=(p1.x-p0.x));
				
				if(CommonSetting.sceneCalculateUse2D || MathUtils.floatEquals(p0.y,p1.y))
				{
					re.directionX=0f;
				}
				else
				{
					re.directionX=(float)Math.atan2(p1.y - p0.y,Math.sqrt(d1 * d1 + d2 * d2));
				}
			}
		}
		else
		{
			re.direction=(float)Math.atan2(p1.y-p0.y,p1.x-p0.x);
			re.directionX=0f;
		}
	}
	
	/** 根据朝向计算矢量 */
	public void calculateVectorByDir(PosData re,DirData dir,float value)
	{
		if(CommonSetting.is3D)
		{
			if(CommonSetting.isZHeight)
			{
				if(CommonSetting.sceneCalculateUse2D)
				{
					re.z=0f;
					re.x=value * (float)Math.cos(dir.direction);
					re.y=value * (float)Math.sin(dir.direction);
				}
				else
				{
					re.z=(float)Math.sin(dir.directionX)*value;
					float last=(float)Math.cos(dir.directionX) * value;
					re.x=last * (float)Math.cos(dir.direction);
					re.y=last * (float)Math.sin(dir.direction);
				}
			}
			else
			{
				if(CommonSetting.sceneCalculateUse2D)
				{
					re.y=0f;
					re.x=value * (float)Math.cos(dir.direction);
					re.z=value * (float)Math.sin(dir.direction);
				}
				else
				{
					re.y=(float)Math.sin(dir.directionX)*value;
					float last=(float)Math.cos(dir.directionX) * value;
					re.x=last * (float)Math.cos(dir.direction);
					re.z=last * (float)Math.sin(dir.direction);
				}
			}
		}
		else
		{
			float d=dir.direction;
			re.x=(float)(value * Math.cos(d));
			re.y=(float)(value * Math.sin(d));
			re.z=0f;
		}
	}
	
	/** 计算矢量距离 */
	public float calculatePosDistance(PosData p0,PosData p1)
	{
		if(CommonSetting.sceneCalculateUse2D)
			return calculatePosDistance2D(p0,p1);
		
		return (float)Math.sqrt(calculatePosDistanceSq(p0,p1));
	}
	
	/** 计算矢量距离平方(3D) */
	public float calculatePosDistanceSq(PosData p0,PosData p1)
	{
		if(CommonSetting.sceneCalculateUse2D)
			return calculatePosDistanceSq2D(p0,p1);
		
		float t;
		return (t=(p1.x-p0.x))*t+(t=(p1.y-p0.y))*t+(t=(p1.z-p0.z))*t;
	}
	
	/** 矢量相加(3D) */
	public void addPosByVector(PosData pos,PosData a,float value)
	{
		pos.x+=a.x*value;
		pos.y+=a.y*value;
		pos.z+=a.z*value;
	}
	
	/** 坐标相加 */
	public void addPos(PosData re,PosData pos)
	{
		re.x+=pos.x;
		re.y+=pos.y;
		re.z+=pos.z;
	}
	
	//--2D--//
	
	/** 返回量点高度差绝对值 */
	public float getDHeight(PosData p0,PosData p1)
	{
		if(CommonSetting.isZHeight)
			return Math.abs(p0.z-p1.z);
		else
			return Math.abs(p0.y-p1.y);
	}
	
	/** 计算矢量距离平方(2D) */
	public float calculatePosDistanceSq2D(PosData p0,PosData p1)
	{
		float t;
		if(CommonSetting.isZHeight)
			return (t=(p1.x-p0.x))*t+(t=(p1.y-p0.y))*t;
		else
			return (t=(p1.x-p0.x))*t+(t=(p1.z-p0.z))*t;
	}
	
	/** 计算矢量距离 */
	public float calculatePosDistance2D(PosData p0,PosData p1)
	{
		return (float)Math.sqrt(calculatePosDistanceSq2D(p0,p1));
	}
	
	/** 返回点1对于点0的朝向 */
	public void calculateDirByPos2D(DirData re,PosData p0,PosData p1)
	{
		if(CommonSetting.is3D)
		{
			if(CommonSetting.isZHeight)
				re.direction=(float)Math.atan2(p1.y-p0.y,p1.x-p0.x);
			else
				re.direction=(float)Math.atan2(p1.z-p0.z,p1.x-p0.x);
		}
		else
		{
			re.direction=(float)Math.atan2(p1.y-p0.y,p1.x-p0.x);
		}
	}
	
	/** 将一对极坐标转换为笛卡尔点坐标(2D) */
	public void addPos2D(PosData re,PosData pos)
	{
		re.x+=pos.x;
		
		if(CommonSetting.isZHeight)
			re.y+=pos.y;
		else
			re.z+=pos.z;
	}
	
	/** 将一对极坐标转换为笛卡尔点坐标(2D) */
	public void polar2D(PosData re,float length,DirData dir)
	{
		polar2D(re,length,dir.direction);
	}
	
	/** 将一对极坐标转换为笛卡尔点坐标(2D) */
	public void polar2D(PosData re,float length,float dir)
	{
		re.x=(float)(Math.cos(dir)*length);
		
		if(CommonSetting.isZHeight)
			re.y=(float)(Math.sin(dir)*length);
		else
			re.z=(float)(Math.sin(dir)*length);
	}
	
	/** 将点，向朝向移动距离 */
	public void addPolar2D(PosData pos,float length,DirData dir)
	{
		addPolar2D(pos,length,dir.direction);
	}
	
	/** 将点，向朝向移动距离 */
	public void addPolar2D(PosData pos,float length,float dir)
	{
		pos.x+=(float)(Math.cos(dir)*length);
		
		if(CommonSetting.isZHeight)
			pos.y+=(float)(Math.sin(dir)*length);
		else
			pos.z+=(float)(Math.sin(dir)*length);
	}
	
	/** 反向(只xz平面) */
	public void inverseDir(DirData dir)
	{
		dir.direction=MathUtils.directionInverse(dir.direction);
	}
	
	/** 获取极坐标偏移点(在范围内的) */
	public void getPolarPos(PosData re,PosData from,float direction,float length)
	{
		re.y=from.y;
		polar2D(re,length,direction);
		addPos2D(re,from);
		clampPos(re);
	}
	
	/** 根据朝向旋转向量*/
	public void rotatePosByDir2D(PosData re,DirData dir)
	{
		if(!MathUtils.floatEquals(dir.direction,0f))
		{
			float nx=(float)(re.x*Math.cos(dir.direction)-re.z*Math.sin(dir.direction));
			float nz=(float)(re.x*Math.sin(dir.direction)+re.z*Math.cos(dir.direction));
			
			re.x=nx;
			re.z=nz;
		}
	}
	
	//--阻挡相关--//
	
	/** 某点是否可用(地图范围内并且不是阻挡) */
	public boolean isPosEnabled(int moveType,PosData pos)
	{
		return isPosEnabled(moveType,pos,false);
	}
	
	/** 某点是否可用(地图范围内并且不是阻挡) */
	public boolean isPosEnabled(int moveType,PosData pos,boolean isClient)
	{
		PosData originPos=_originPos;
		PosData endPos=_endPos;
		
		//超出地图边界
		if(pos.x<originPos.x || pos.z<originPos.z || pos.x>endPos.x || pos.z>endPos.z)
		{
			return false;
		}
		
		return _map.isPosEnabled(moveType,pos,isClient);
	}
	
	/** 从某点开始,寻找射线点 */
	public void findRayPos(int moveType,PosData re,PosData from,float direction,float length)
	{
		if(_map!=null)
		{
			_map.findRayPos(moveType,re,from,direction,length);
		}
		else
		{
			getPolarPos(re,from,direction,length);
		}
	}
	
	/** 寻路(不包含当前点)(needCrowed:是否需要避障) */
	public void findPath(SList<PosData> list,int moveType,boolean needCrowed,PosData from,PosData target)
	{
		list.clear();
		
		if(_map!=null)
		{
			_map.findPath(list,moveType,needCrowed,from,target);
		}
		else
		{
			PosData data=new PosData();
			data.copyPos(target);
			list.add(data);
		}
	}
	
	/** 获取半径内的可走点 */
	public void getRandomWalkablePos(int moveType,PosData re,PosData from,float radius)
	{
		findRayPos(moveType,re,from,MathUtils.randomDirection(),radius*(MathUtils.randomFloat()*0.7f+0.3f));//至少30%
	}

	public BaseSceneMap getBaseSceneMap()
	{
		return _map;
	}
	
	
}
