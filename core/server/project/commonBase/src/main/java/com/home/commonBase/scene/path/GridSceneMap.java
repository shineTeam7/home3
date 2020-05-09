package com.home.commonBase.scene.path;

import com.home.commonBase.config.game.enumT.MapBlockTypeConfig;
import com.home.commonBase.config.other.GridMapInfoConfig;
import com.home.commonBase.config.other.MapInfoConfig;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;

/** 场景格子地图 */
public class GridSceneMap extends BaseSceneMap
{
	/** 最长步数 */
	private static final int _maxTryTimes=10000;
	
	private byte[] _mainGrids;
	private byte[] _secondGrids;
	
	/** 单位阻挡 */
	private IntIntMap _crowedGrids=new IntIntMap();
	
	private int _xGridNum;
	private int _zGridNum;
	private int _heightW;
	
	private IPathFinding _pathFinding;
	
	private IntList _findResult=new IntList();
	
	//temp
	private int[] _tempArr=new int[2];
	private int[] _tempArr2=new int[2];
	
	private PosData _tempPos=new PosData();
	
	private GridMapInfoConfig _infoConfig;
	
	public GridSceneMap()
	{
	
	}
	
	@Override
	public void init()
	{
		super.init();
		
		MapInfoConfig mapInfoConfig=MapInfoConfig.get(_scene.getConfig().mapID);
		
		if(mapInfoConfig.grid==null || mapInfoConfig.grid.isEmpty)
		{
			Ctrl.errorLog("未找到MapInfo配置",_scene.getConfig().mapID);
			return;
		}
		
		_infoConfig=mapInfoConfig.grid;
		
		_mainGrids=_infoConfig.mainGrids;
		_secondGrids=_infoConfig.secondGrids;
		
		if(_mainGrids!=null)
		{
			_xGridNum=_infoConfig.width;
			_zGridNum=_infoConfig.height;
			_heightW=_infoConfig.heightW;
		}
		else
		{
			_xGridNum=0;
			_zGridNum=0;
		}
		
		_pathFinding=new JPSPathFinding()
		{
			@Override
			public boolean isEnable(int moveType,boolean needCrowed,int x,int y)
			{
				return isGridEnabled(moveType,needCrowed,x,y);
			}
		};
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_infoConfig=null;
		
		_mainGrids=null;
		_xGridNum=0;
		_zGridNum=0;
		_findResult.clear();
		_crowedGrids.clear();
	}
	
	public GridMapInfoConfig getInfoConfig()
	{
		return _infoConfig;
	}
	
	@Override
	public boolean isPosEnabled(int moveType,PosData pos,boolean isClient)
	{
		int[] arr=_tempArr;
		getGridPos(arr,pos);
		
		return isGridEnabled(moveType,arr[0],arr[1],isClient);
	}
	
	@Override
	public void findRayPos(int moveType,PosData re,PosData from,float direction,float length)
	{
		_scene.pos.getPolarPos(re,from,direction,length);
		
		//不能直达
		if(!isStraightArrive(moveType,from,re))
		{
			setPosByGrid(re,_tempArr[0],_tempArr[1]);
		}
	}
	
	@Override
	public void findPath(SList<PosData> list,int moveType,boolean needCrowed,PosData from,PosData target)
	{
		if(_mainGrids==null)
		{
			PosData data=new PosData();
			data.copyPos(target);
			list.add(data);
			return;
		}
		
		int[] arr=_tempArr;
		
		getGridPos(arr,target);
		int tx=arr[0];
		int ty=arr[1];
		
		getGridPos(arr,from);
		int fx=arr[0];
		int fy=arr[1];
		
		boolean reseted=false;
		
		//目标点不可走
		if(!isGridEnabled(moveType,needCrowed,tx,ty))
		{
			boolean tr=resetTarget(arr,moveType,needCrowed,fx,fy,tx,ty);
			
			if(!tr)
				return;
			
			tx=arr[0];
			ty=arr[1];
			
			reseted=true;
		}
		
		//不是同一连通区
		if(!_infoConfig.isSameArea(moveType,fx,fy,tx,ty))
			return;
		
		//可直达
		if(isStraightArriveForGrid(moveType,needCrowed,fx,fy,tx,ty))
		{
			PosData data=new PosData();
			
			if(reseted)
			{
				data.y=target.y;
				setPosByGrid(data,tx,ty);
			}
			else
			{
				data.copyPos(target);
			}
			
			list.add(data);
		}
		else
		{
			IntList findResult=_findResult;
			_pathFinding.findPath(findResult,moveType,needCrowed,fx,fy,tx,ty);
			
			//有结果
			if(!findResult.isEmpty())
			{
				int[] values=findResult.getValues();
				
				for(int i=0,len=findResult.size();i<len;i+=2)
				{
					PosData data=new PosData();
					data.y=from.y;
					setPosByGrid(data,values[i],values[i+1]);
					list.add(data);
				}
			}
		}
	}
	
	public void findPathByGrid(SList<PosData> list,int moveType,boolean needCrowed,float y,int fx,int fy,int tx,int ty)
	{
		//不是同一连通区
		if(!_infoConfig.isSameArea(moveType,fx,fy,tx,ty))
			return;
		
		if(!isGridEnabled(moveType,needCrowed,tx,ty))
		{
			boolean tr=resetTarget(_tempArr,moveType,needCrowed,fx,fy,tx,ty);
			
			if(!tr)
				return;
			
			tx=_tempArr[0];
			ty=_tempArr[1];
		}
		
		//可直达
		if(isStraightArriveForGrid(moveType,needCrowed,fx,fy,tx,ty))
		{
			PosData data=new PosData();
			setPosByGrid(data,tx,ty);
			list.add(data);
		}
		else
		{
			IntList findResult=_findResult;
			_pathFinding.findPath(findResult,moveType,needCrowed,fx,fy,tx,ty);
			
			//有结果
			if(!findResult.isEmpty())
			{
				int[] values=findResult.getValues();
				
				for(int i=0,len=findResult.size();i<len;i+=2)
				{
					PosData data=new PosData();
					data.y=y;
					setPosByGrid(data,values[i],values[i+1]);
					list.add(data);
				}
			}
		}
	}
	
	@Override
	public int getBlockType(PosData pos)
	{
		int[] arr=_tempArr;
		
		getGridPos(arr,pos);
		
		return _mainGrids[arr[0]<< _heightW | arr[1]];
	}
	
	/** 格子是否可走 */
	protected boolean isGridEnabled(int moveType,boolean needCrowed,int x,int z)
	{
		boolean re=isGridEnabled(moveType,x,z,false);
		
		if(re && needCrowed)
		{
			return _crowedGrids.get(x<< _heightW | z)==0;
		}
		
		return re;
	}
	
	/** 格子是否可走 */
	protected boolean isGridEnabled(int moveType,int x,int z,boolean isClient)
	{
		if(x<0 || x>=_xGridNum || z<0 || z>=_zGridNum)
			return false;
		
		int index=x<< _heightW | z;
		int v=_mainGrids[index];
		
		if(isClient)
		{
			//主格可通行
			if(MapBlockTypeConfig.allowDic[v][moveType])
				return true;
			
			if(!CommonSetting.serverMapNeedSecondGrid)
				return false;
			
			return _secondGrids[index]>0;
			
			////有第二类型
			//if((v=_secondGrids[index])>0)
			//{
			//	if(MapBlockTypeConfig.allowDic[v][moveType])
			//		return true;
			//}
			//
			//return false;
		}
		else
		{
			return MapBlockTypeConfig.allowDic[v][moveType];
		}
	}
	
	/** 获取格子位置 */
	public void getGridPos(int[] arr,PosData pos)
	{
		PosData originPos=_originPos;
		float gridSizeN=Global.mapBlockSizeN;
		
		arr[0]=(int)((pos.x - originPos.x) * gridSizeN);
		arr[1]=(int)((pos.z - originPos.z) * gridSizeN);
		
		//float gridSizeHalf=Global.mapBlockSizeHalf;
		//
		//float v;
		//v=arr[0]=Math.round((pos.x - originPos.x -gridSizeHalf) * gridSizeN);
		//if(v<0)
		//	arr[0]=0;
		//else if(v>=_xGridNum)
		//	arr[0]=_xGridNum-1;
		//
		//v=arr[1]=Math.round((pos.z - originPos.z -gridSizeHalf) * gridSizeN);
		//if(v<0)
		//	arr[1]=0;
		//else if(v>=_zGridNum)
		//	arr[1]=_zGridNum-1;
	}
	
	/** 通过grid设置格子的xz坐标 */
	public void setPosByGrid(PosData pos,int x,int z)
	{
		PosData originPos=_originPos;
		float gridSize=Global.mapBlockSize;
		float gridSizeHalf=Global.mapBlockSizeHalf;
		
		pos.x=originPos.x+(x*gridSize)+gridSizeHalf;
		pos.z=originPos.z+(z*gridSize)+gridSizeHalf;
	}
	
	/** 判断两点是否可直达 */
	private boolean isStraightArrive(int moveType,PosData from,PosData to)
	{
		if(_mainGrids!=null)
		{
			int[] arr=_tempArr;
			getGridPos(arr,from);
			int fx=arr[0];
			int fy=arr[1];
			
			getGridPos(arr,to);
			int tx=arr[0];
			int ty=arr[1];
			
			return isStraightArriveForGrid(moveType,false,fx,fy,tx,ty);
		}
		else
		{
			return true;
		}
	}
	
	/** 判断两点是否可直达(格子用) */
	private boolean isStraightArriveForGrid(int moveType,boolean needCrowed,int fx,int fy,int tx,int ty)
	{
		int sx=Integer.compare(tx,fx);
		int sy=Integer.compare(ty,fy);
		
		int dx=Math.abs(tx-fx);
		int dy=Math.abs(ty-fy);
		
		int rx;
		int ry;
		int lastRX=fx;
		int lastRY=fy;
		
		int j;
		
		if(dx>dy)
		{
			for(int i=1;i<=dx;i++)
			{
				j=i*dy/dx;
				
				if(!isGridEnabled(moveType,needCrowed,rx=(fx+i*sx),ry=(fy+j*sy)))
				{
					_tempArr[0]=lastRX;
					_tempArr[1]=lastRY;
					return false;
				}
				
				lastRX=rx;
				lastRY=ry;
			}
		}
		else
		{
			for(int i=1;i<=dy;i++)
			{
				j=i*dx/dy;
				
				if(!isGridEnabled(moveType,needCrowed,rx=(fx+j*sx),ry=(fy+i*sy)))
				{
					_tempArr[0]=lastRX;
					_tempArr[1]=lastRY;
					return false;
				}
				
				lastRX=rx;
				lastRY=ry;
			}
		}
		
		return true;
	}
	
	/** 重新找目标点(从tx,ty向fx,fy查询) */
	public boolean resetTarget(int[] re,int moveType,boolean needCrowed,int fx,int fy,int tx,int ty)
	{
		int sx=Integer.compare(fx,tx);
		int sy=Integer.compare(fy,ty);
		
		int dx=Math.abs(tx-fx);
		int dy=Math.abs(ty-fy);
		
		int rx;
		int ry;
		
		int j;
		
		if(dx>dy)
		{
			for(int i=1;i<=dx;i++)
			{
				j=i*dy/dx;
				
				if(isGridEnabled(moveType,needCrowed,rx=(tx+i*sx),ry=(ty+j*sy)))
				{
					re[0]=rx;
					re[1]=ry;
					return true;
				}
			}
		}
		else
		{
			for(int i=1;i<=dy;i++)
			{
				j=i*dx/dy;
				
				if(isGridEnabled(moveType,needCrowed,rx=(tx+j*sx),ry=(ty+i*sy)))
				{
					re[0]=rx;
					re[1]=ry;
					return true;
				}
			}
		}
		
		return false;
	}
	
	/** 重新找目标点(随机螺旋查询)(pos,disSq，是否需要限制在某圆内) */
	public boolean resetPos(int[] re,int moveType,boolean needCrowed,int px,int py,PosData pos,float dis)
	{
		int fLen;
		int tLen=0;
		int tx=0;
		int ty=0;
		
		boolean needSecond=false;
		
		if(pos!=null)
		{
			getGridPos(_tempArr,pos);
			tx=_tempArr[0];
			ty=_tempArr[1];
			
			tLen=(int)Math.round(dis * Global.mapBlockSizeN *1.4);
			fLen=(int)Math.round((Math.abs(tx-px)+Math.abs(ty-py))*1.4);
			
			if(needCrowed)
				needSecond=true;
		}
		else
		{
			fLen=CommonSetting.gridResetPosLen;
		}
		
		float disSq=dis*dis;
		float min=Math.max(dis-CommonSetting.gridResetPosCircleLen,0);
		float minDisSq=min*min;
		
		//自身
		if(toResetPos(re,moveType,needCrowed,px,py,fLen,pos,disSq,minDisSq,tx,ty,tLen,false,_tempArr2))
			return true;
		
		if(pos==null)
			return false;
		
		_tempArr2[0]=-1;
		_tempArr2[1]=-1;
		
		//目标
		if(toResetPos(re,moveType,needCrowed,tx,ty,tLen,pos,disSq,minDisSq,tx,ty,tLen,true,_tempArr2))
			return true;
		
		//补充
		if(needSecond && _tempArr2[0]!=-1)
		{
			re[0]=_tempArr2[0];
			re[1]=_tempArr2[1];
			return true;
		}
		
		return false;
	}
	
	private boolean toResetPos(int[] re,int moveType,boolean needCrowed,int px,int py,int fLen,PosData pos,float disSq,float minDisSq,int tx,int ty,int tLen,boolean needSecond,int[] secondArr)
	{
		int x=0;
		int y=0;
		
		boolean outCircle;
		float sq;
		
		for(int n=1;n<=fLen;++n)
		{
			//边长
			int sLen=n*2;
			//总数
			int nLen=(sLen+1)*(sLen+1)-(n*n);
			
			int sv=MathUtils.randomInt(sLen);
			int d=MathUtils.randomInt(4);
			
			for(int i=0;i<nLen;i++)
			{
				//根据sv,d,获取位置
				
				switch(d)
				{
					case 0:
					{
						x=px-n+sv;
						y=py-n;
					}
					break;
					case 1:
					{
						x=px+n;
						y=py-n+sv;
					}
					break;
					case 2:
					{
						x=px+n-sv;
						y=py+n;
					}
					break;
					case 3:
					{
						x=px-n;
						y=py+n-sv;
					}
					break;
				}
				
				//在粗略半径内
				if(pos==null || (Math.abs(x - tx) + Math.abs(y - ty))<=tLen)
				{
					outCircle=false;
					
					if(isGridEnabled(moveType,needCrowed,x,y))
					{
						if(pos==null)
						{
							re[0]=x;
							re[1]=y;
							return true;
						}
						
						setPosByGrid(_tempPos,x,y);
						sq=_scene.pos.calculatePosDistanceSq2D(pos,_tempPos);
						if(sq<=disSq && sq>=minDisSq)
						{
							re[0]=x;
							re[1]=y;
							return true;
						}
						else
						{
							outCircle=true;
						}
					}
					
					if(needSecond && secondArr[0]==-1 && !outCircle)
					{
						//找到的第一个
						if(isGridEnabled(moveType,false,x,y))
						{
							setPosByGrid(_tempPos,x,y);
							sq=_scene.pos.calculatePosDistanceSq2D(pos,_tempPos);
							if(sq<=disSq && sq>=minDisSq)
							{
								secondArr[0]=x;
								secondArr[1]=y;
							}
						}
					}
				}
				
				if((++sv)==sLen)
				{
					sv=0;
					
					if((++d)==4)
					{
						d=0;
					}
				}
			}
		}
		
		return false;
	}
	
	/** 添加crowed格子 */
	public void addCrowedGrid(int x,int z,int radius)
	{
		if(radius<0)
			return;
		
		if(radius==0)
		{
			_crowedGrids.addValue(x<< _heightW | z,1);
			return;
		}
		
		int left=Math.max(0,x-radius);
		int right=Math.min(_xGridNum-1,x+radius);
		int up=Math.max(0,z-radius);
		int down=Math.min(_zGridNum-1,z+radius);
		
		for(int i=left;i<=right;i++)
		{
			for(int j=up;j<=down;j++)
			{
				_crowedGrids.addValue(i<< _heightW | j,1);
			}
		}
	}
	
	/** 减少怪物格子 */
	public void removeCrowedGrid(int x,int z,int radius)
	{
		if(radius<0)
			return;
		
		if(radius==0)
		{
			_crowedGrids.addValueR(x<< _heightW | z,-1);
			return;
		}
		
		int left=Math.max(0,x-radius);
		int right=Math.min(_xGridNum-1,x+radius);
		int up=Math.max(0,z-radius);
		int down=Math.min(_zGridNum-1,z+radius);
		
		for(int i=left;i<=right;i++)
		{
			for(int j=up;j<=down;j++)
			{
				_crowedGrids.addValueR(i<< _heightW | j,-1);
			}
		}
	}
	
	public int getCrowedGrid(int x,int z)
	{
		return _crowedGrids.get(x<< _heightW | z);
	}
}
