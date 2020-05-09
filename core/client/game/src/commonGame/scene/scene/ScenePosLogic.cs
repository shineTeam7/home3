using System;
using ShineEngine;
using UnityEngine;
using UnityEngine.AI;

/// <summary>
/// 场景位置逻辑
/// </summary>
public class ScenePosLogic:SceneLogicBase
{
	private PosData _originPos;

	private PosData _endPos;

	private FindCircleNearestTemp _circleNearestTemp=new FindCircleNearestTemp();

	public override void construct()
	{

	}

	public override void init()
	{
		_originPos=_scene.originPos;
		_endPos=_scene.endPos;
	}

	public override void dispose()
	{

	}

	public override void onFrame(int delay)
	{

	}

	/** 矫正点(将坐标归到可用范围) */
	public void clampPos(PosData pos)
	{
		PosData originPos=_scene.originPos;
		PosData sizePos=_scene.sizePos;

		pos.x=MathUtils.clamp(pos.x,originPos.x,originPos.x+sizePos.x);
		pos.y=MathUtils.clamp(pos.y,originPos.y,originPos.y+sizePos.y);
		pos.z=MathUtils.clamp(pos.z,originPos.z,originPos.z+sizePos.z);
	}

	public class FindCircleNearestTemp
	{
		public Unit unit;

		public float dis;
	}

	/** 计算坐标和值(判定用) */
	public float calculatePosSum(PosData p0,PosData p1)
	{
		if(CommonSetting.is3D)
		{
			if(CommonSetting.sceneCalculateUse2D)
				return Math.Abs(p1.x-p0.x) + Math.Abs(p1.z-p0.z);
			else
				return Math.Abs(p1.x-p0.x) + Math.Abs(p1.y-p0.y) + Math.Abs(p1.z-p0.z);
		}
		else
		{
			if(CommonSetting.isZHeight)
				return Math.Abs(p1.x-p0.x) + Math.Abs(p1.y-p0.y);
			else
				return Math.Abs(p1.x-p0.x) + Math.Abs(p1.z-p0.z);
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
				re.direction=(float)Math.Atan2(d1=(p1.y-p0.y),d2=(p1.x-p0.x));

				if(CommonSetting.sceneCalculateUse2D || MathUtils.floatEquals(p0.z,p1.z))
				{
					re.directionX=0f;
				}
				else
				{
					re.directionX=(float)Math.Atan2(p1.z - p0.z,Math.Sqrt(d1 * d1 + d2 * d2));
				}
			}
			else
			{
				re.direction=(float)Math.Atan2(d1=(p1.z-p0.z),d2=(p1.x-p0.x));

				if(CommonSetting.sceneCalculateUse2D || MathUtils.floatEquals(p0.y,p1.y))
				{
					re.directionX=0f;
				}
				else
				{
					re.directionX=(float)Math.Atan2(p1.y - p0.y,Math.Sqrt(d1 * d1 + d2 * d2));
				}
			}
		}
		else
		{
			re.direction=(float)Math.Atan2(p1.y-p0.y,p1.x-p0.x);
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
					re.x=value * (float)Math.Cos(dir.direction);
					re.y=value * (float)Math.Sin(dir.direction);
				}
				else
				{
					re.z=(float)Math.Sin(dir.directionX)*value;
					float last=(float)Math.Cos(dir.directionX) * value;
					re.x=last * (float)Math.Cos(dir.direction);
					re.y=last * (float)Math.Sin(dir.direction);
				}
			}
			else
			{
				if(CommonSetting.sceneCalculateUse2D)
				{
					re.y=0f;
					re.x=value * (float)Math.Cos(dir.direction);
					re.z=value * (float)Math.Sin(dir.direction);
				}
				else
				{
					re.y=(float)Math.Sin(dir.directionX)*value;
					float last=(float)Math.Cos(dir.directionX) * value;
					re.x=last * (float)Math.Cos(dir.direction);
					re.z=last * (float)Math.Sin(dir.direction);
				}
			}
		}
		else
		{
			float d=dir.direction;
			re.x=(float)(value * Math.Cos(d));
			re.y=(float)(value * Math.Sin(d));
			re.z=0f;
		}
	}
	
	/** 计算矢量距离 */
	public float calculatePosDistance(PosData p0,PosData p1)
	{
		if(CommonSetting.sceneCalculateUse2D)
			return calculatePosDistance2D(p0,p1);

		return Mathf.Sqrt(calculatePosDistanceSq(p0,p1));
	}
	
	/** 计算矢量距离平方(2D) */
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
			return Mathf.Abs(p0.z-p1.z);
		else
			return Mathf.Abs(p0.y-p1.y);
	}

	/** 计算矢量距离 */
	public float calculatePosDistance2D(PosData p0,PosData p1)
	{
		return (float)Math.Sqrt(calculatePosDistanceSq2D(p0,p1));
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
	
	/** 返回点1对于点0的朝向 */
	public void calculateDirByPos2D(DirData re,PosData p0,PosData p1)
	{
		if(CommonSetting.is3D)
		{
			if(CommonSetting.isZHeight)
				re.direction=Mathf.Atan2(p1.y-p0.y,p1.x-p0.x);
			else
				re.direction=Mathf.Atan2(p1.z-p0.z,p1.x-p0.x);
		}
		else
		{
			re.direction=Mathf.Atan2(p1.y-p0.y,p1.x-p0.x);
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
		re.x=(float)(Math.Cos(dir)*length);

		if(CommonSetting.isZHeight)
			re.y=(float)(Math.Sin(dir)*length);
		else
			re.z=(float)(Math.Sin(dir)*length);
	}

	/** 将点，向朝向移动距离 */
	public void addPolar2D(PosData pos,float length,DirData dir)
	{
		addPolar2D(pos,length,dir.direction);
	}

	/** 将点，向朝向移动距离 */
	public void addPolar2D(PosData pos,float length,float dir)
	{
		pos.x+=(float)(Math.Cos(dir)*length);

		if(CommonSetting.isZHeight)
			pos.y+=(float)(Math.Sin(dir)*length);
		else
			pos.z+=(float)(Math.Sin(dir)*length);
	}

	/** 反向(只xz平面) */
	public void inverseDir(DirData dir)
	{
		dir.direction=MathUtils.directionInverse(dir.direction);
	}

	/** 根据朝向旋转向量*/
	public void rotatePosByDir2D(PosData re,DirData dir)
	{
		if(!MathUtils.floatEquals(dir.direction,0f))
		{
			float nx=(float)(re.x*Math.Cos(dir.direction)-re.z*Math.Sin(dir.direction));
			float nz=(float)(re.x*Math.Sin(dir.direction)+re.z*Math.Cos(dir.direction));

			re.x=nx;
			re.z=nz;
		}
	}

	//--findPath--//

	/** 某点是否可用(地图范围内并且不是阻挡) */
	public bool isPosEnabled(int moveType,PosData pos)
	{
		PosData originPos=_originPos;
		PosData endPos=_endPos;

		//超出地图边界
		if(pos.x<originPos.x || pos.z<originPos.z || pos.x>endPos.x || pos.z>endPos.z)
		{
			return false;
		}

		return BaseGameUtils.isPosEnabled(moveType,pos);
	}

	/** 从某点开始，向某朝向找寻第一个不是阻挡的点 */
	public virtual void findRayPos(int moveType,PosData re,PosData from,float direction,float length)
	{
		re.y=from.y;
		polar2D(re,length,direction);
		addPos2D(re,from);
		clampPos(re);
	}

	/** 寻路(不包含当前点) */
	public virtual void findPath(int moveType,SList<PosData> list,PosData from,PosData target)
	{
		list.clear();

		//TODO:补真实寻路

		PosData data=new PosData();
		data.copyPos(target);
		list.add(data);
	}

	/** 获取半径内的可走点 */
	public virtual void getRandomWalkablePos(int moveType,PosData re,PosData from,float radius)
	{
		findRayPos(moveType,re,from,MathUtils.randomDirection(),radius*(MathUtils.randomFloat()*0.7f+0.3f));//至少30%
	}

	/** 取圆形范围内最近的一个可拾取单位 */
	public Unit getNearestFieldItem(float radius)
	{
		ScenePosLogic posLogic=_scene.pos;
		float sq=radius*radius;

		FindCircleNearestTemp cTemp=_circleNearestTemp;
		cTemp.dis=float.MaxValue;
		cTemp.unit=null;

		Unit hero=_scene.hero;

		PosData pos=hero.pos.getPos();

		Unit[] values;
		Unit k;

		for(int i=(values=_scene.getUnitDic().getValues()).Length-1;i>=0;--i)
		{
			if((k=values[i])!=null)
			{
				//是掉落物品，并且可拾取
				if(k.getType()==UnitType.FieldItem && hero.aiCommand.checkCanPickUpFieldItem(k))
				{
					float dq;
					//在范围内
					if((dq=posLogic.calculatePosDistanceSq2D(k.pos.getPos(),pos))<=sq)
					{
						if(dq<cTemp.dis)
						{
							cTemp.dis=dq;
							cTemp.unit=k;
						}
					}
				}
			}
		}

		Unit re=cTemp.unit;
		cTemp.unit=null;

		return re;
	}
}