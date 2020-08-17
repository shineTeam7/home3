package com.home.commonBase.scene.path;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.IPoolObject;
import com.home.shine.support.pool.ObjectPool;

import java.util.Comparator;
import java.util.PriorityQueue;

/** jps寻路 */
public abstract class JPSPathFinding implements IPathFinding
{
	public long posEnableTime=0;
	
	/** 最长步数 */
	private static final int _maxTryTimes=10000;
	/** 启用斜角 */
	private static boolean _allowDiagonal=true;
	/** 朝向数组 */
	private static Dir[] _dirArr;
	/** 朝向字典 */
	private static Dir[] _dirDic;
	
	private ObjectPool<Node> _nodePool=new ObjectPool<>(Node::new,256);
	
	/** 优先队列 */
	private SList<Node> _queue;
	/** 字典 */
	private IntObjectMap<Node> _dic=new IntObjectMap<>(Node[]::new);
	/** 邻居组 */
	private SList<Node> _neighbourList=new SList<>(Node[]::new);
	
	private int _ex;
	private int _ey;
	/** 当前移动类型 */
	private int _moveType;
	
	private boolean _needCrowed;
	
	/** 跳点参数列表(为不递归) */
	private IntList _jumpArgList=new IntList();
	
	private int _jx;
	private int _jy;
	
	private static Comparator<Node> _nodeComparator;
	
	static
	{
		_dirArr=new Dir[8];
		_dirDic=new Dir[16];
		
		int index=0;
		Dir dir;
		
		for(int i=-1;i<=1;i++)
		{
			for(int j=-1;j<=1;j++)
			{
				if(i==0 && j==0)
					continue;
				
				dir=new Dir();
				dir.x=i;
				dir.y=j;
				_dirArr[index++]=dir;
				_dirDic[getDirIndex(i,j)]=dir;
			}
		}
		
		_nodeComparator=JPSPathFinding::nodeCompare;
	}
	
	/** 序号计算 */
	private static int getIndex(int x,int y)
	{
		return x<< 16 | y;
	}
	
	/** 获取朝向序号 */
	private static int getDirIndex(int i,int j)
	{
		return (i+1)<<2 | (j+1);
	}
	
	/** 获取两点朝向 */
	private static Dir getDirByPos(Node from,Node to)
	{
		return _dirDic[getDirIndex(Integer.compare(to.x,from.x),Integer.compare(to.y,from.y))];
	}
	
	/** 获取两点朝向 */
	private static Dir getDirByPos(int fx,int fy,int tx,int ty)
	{
		return _dirDic[getDirIndex(Integer.compare(tx,fx),Integer.compare(ty,fy))];
	}
	
	public JPSPathFinding()
	{
		//_queue=new PriorityQueue<>(JPSPathFinding::nodeCompare);
		_queue=new SList<>(Node[]::new);
	}
	
	@Override
	public void findPath(IntList result,int moveType,boolean needCrowed,int sx,int sy,int ex,int ey)
	{
		result.clear();
		
		if(!isEnable(moveType,needCrowed,ex,ey))
			return;
		
		SList<Node> queue=_queue;
		int t=_maxTryTimes;
		
		queue.clear();
		
		_moveType=moveType;
		_needCrowed=needCrowed;
		
		_ex=ex;
		_ey=ey;
		Node p=_nodePool.getOne();
		p.init(null,sx,sy,0,Math.abs(ex-sx)+Math.abs(ey-sy));
		
		//加入起始点
		queue.add(p);
		_dic.put(p.index,p);
		
		while((--t)>=0)
		{
			if(queue.isEmpty())
			{
				clear();
				
				//未找到
				Ctrl.print(String.format("无法到达 sx:%d sy:%d ex:%d ey:%d", sx, sy, ex, ey));
				return;
			}
			
			//queue.sort(JPSPathFinding::nodeCompare);
			
			p=queue.shift();
			
			if(p.x==ex && p.y==ey)
			{
				makePath(result,p);
				clear();
				return;
			}
			
			p.isClose=true;
			
			extendRound(p);
		}
		
		clear();
		Ctrl.warnLog("寻路时超出最大循环次数限制");
	}
	
	private Node createNode(int x,int y,int fx,int fy)
	{
		Node node=_nodePool.getOne();
		node.init(null,x,y,getG(x,y,fx,fy),getH(x,y,_ex,_ey));
		return node;
	}
	
	/** 将周围的点加入open组 */
	private void extendRound(Node p)
	{
		pruneNeighbours(p);
		
		Node[] values=_neighbourList.getValues();
		int len=_neighbourList.size();
		Node nn;
		
		//for(int i=0;i<len;i++)
		//{
		//	nn=values[i];
		//
		//	Ctrl.print("neighbour",nn.x,nn.y);
		//}
		
		int px=p.x;
		int py=p.y;
		int jx;
		int jy;
		int jIndex;
		
		int g;
		
		Node node;
		
		for(int i=0;i<len;i++)
		{
			nn=values[i];
			
			if(jumpNode(nn.x,nn.y,px,py))
			{
				jIndex=getIndex(jx=_jx,jy=_jy);
				
				//不斜角的跳过
				if(!_allowDiagonal && jx!=px && jy!=py)
				{
					continue;
				}
				
				node=_dic.get(jIndex);
				
				g=getG(jx,jy,px,py);
				
				if(node==null)
				{
					node=_nodePool.getOne();
					node.init(p,jx,jy,g,getH(jx,jy,px,py));
					_queue.add(node);
					_dic.put(node.index,node);
				}
				else
				{
					if(node.isClose)
						continue;
					
					//开销更低
					if(g<node.g)
					{
						//更新队列
						_queue.removeObj(node);
						
						node.parent=p;
						node.g=g;
						node.f=g+node.h;
						
						_queue.add(node);
					}
				}
			}
		}
		
		_neighbourList.forEachAndClear(v->
		{
			_nodePool.back(v);
		});
	}
	
	private int getG(int px,int py,int lx,int ly)
	{
		if(px==lx)
			return Math.abs(py-ly)*10;
		
		if(py==ly)
			return Math.abs(px-lx)*10;
		
		return Math.abs(px-lx)*14;
	}
	
	private int getH(int px,int py,int lx,int ly)
	{
		return (Math.abs(py-ly)+Math.abs(px-lx))*10;
	}
	
	/** 剪枝邻居组 */
	private void pruneNeighbours(Node p)
	{
		SList<Node> neighbourList=_neighbourList;
		neighbourList.clear();
		int x;
		int y;
		int px=p.x;
		int py=p.y;
		
		int moveType=_moveType;
		boolean needCrowed=_needCrowed;
		
		//不是起始点
		if(p.parent!=null)
		{
			Dir d=p.getDir();
			
			int dx=d.x;
			int dy=d.y;
			
			//对角线行走; eg:右下(1, 1)
			if(dx!=0 && dy!=0)
			{
				//普通邻居
				boolean walkX=false;
				boolean walkY=false;
				
				//下（0， 1）
				if(isEnable(moveType,needCrowed,x=(px),y=(py+dy)))
				{
					neighbourList.add(createNode(x,y,px,py));
					walkY=true;
				}
				
				//右（1， 0）
				if(isEnable(moveType,needCrowed,x=(px+dx),y=(py)))
				{
					neighbourList.add(createNode(x,y,px,py));
					walkX=true;
				}
				
				if(walkX || walkY)
				{
					neighbourList.add(createNode(px+dx,py+dy,px,py));
				}
				
				//强制邻居
				
				//左不能走且下可走,添加左下（-1， 1）
				if(!isEnable(moveType,needCrowed,x=(px-dx),py) && walkY)
					neighbourList.add(createNode(x,py+dy,px,py));
				
				//上不能走且右可走,添加右上（1， -1）
				if(!isEnable(moveType,needCrowed,px,y=(py-dy)) && walkX)
					neighbourList.add(createNode(px+dx,y,px,py));
			}
			//直行
			else
			{
				//垂直
				if(dx==0)
				{
					//下能走
					if(isEnable(moveType,needCrowed,px,y=(py+dy)))
					{
						neighbourList.add(createNode(px,y,px,py));
						
						//右不能走,加 右下
						if(!isEnable(moveType,needCrowed,x=(px+1),py))
							neighbourList.add(createNode(x,py+dy,px,py));
						
						//左不能走,加 左下
						if(!isEnable(moveType,needCrowed,x=(px-1),py))
							neighbourList.add(createNode(x,py+dy,px,py));
					}
					
					if(!_allowDiagonal)
					{
						if(isEnable(moveType,needCrowed,x=(px+1),py))
							neighbourList.add(createNode(x,py,px,py));
						
						if(isEnable(moveType,needCrowed,x=(px-1),py))
							neighbourList.add(createNode(x,py,px,py));
					}
				}
				//水平
				else
				{
					//右能走
					if(isEnable(moveType,needCrowed,x=(px+dx),py))
					{
						neighbourList.add(createNode(x,py,px,py));
						
						//下不能走,加 右下
						if(!isEnable(moveType,needCrowed,px,y=(py+1)))
							neighbourList.add(createNode(px+dx,y,px,py));
						
						//上不能走,加 右下
						if(!isEnable(moveType,needCrowed,px,y=(py-1)))
							neighbourList.add(createNode(px+dx,y,px,py));
					}
					
					if(!_allowDiagonal)
					{
						if(isEnable(moveType,needCrowed,px,y=(py+1)))
							neighbourList.add(createNode(px,y,px,py));
						
						if(isEnable(moveType,needCrowed,px,y=(py-1)))
							neighbourList.add(createNode(px,y,px,py));
					}
				}
			}
		}
		else
		{
			for(Dir v:_dirArr)
			{
				if(isEnable(moveType,needCrowed,x=(p.x+v.x),y=(p.y+v.y)))
				{
					neighbourList.add(createNode(x,y,px,py));
				}
			}
		}
		
		if(!neighbourList.isEmpty())
		{
			neighbourList.sort(_nodeComparator);
		}
	}
	
	private void addListSome(IntList list,int... args)
	{
		int len=list.size();
		list.setLength(len+args.length);
		int[] values=list.getValues();
		System.arraycopy(args,0,values,len,args.length);
	}
	
	/** 获取跳点(返回是否有结果) */
	private boolean jumpNode(int px,int py,int lx,int ly)
	{
		IntList list=_jumpArgList;
		addListSome(list,0,px,py,lx,ly,0,0);
		
		//是否栈退回
		boolean isBack=false;
		
		int mark;
		int dx;
		int dy;
		
		//还有
		while(!list.isEmpty())
		{
			int[] values=list.getValues();
			int start=list.size()-7;//0:标记位,1-4:方法参数位,5-6:朝向值
			
			if(isBack)
			{
				list.justSetSize(start);//用掉7个参数
				
				mark=values[start];
				
				//调用点
				switch(mark)
				{
					case 1:
					{
						px=values[start+3];
						py=values[start+4];
						
						//有值
						if(_jx!=-1)
						{
							_jx=px;
							_jy=py;
						}
						//返回false
						else
						{
							//直接执行doJumpNodePart2
							
							dx=values[start+5];
							dy=values[start+6];
							
							addListSome(list,2,px,py+dy,px,py,dx,dy);
							//继续调用
							isBack=false;
						}
					}
					break;
					case 2:
					{
						px=values[start+3];
						py=values[start+4];
						
						//有值
						if(_jx!=-1)
						{
							_jx=px;
							_jy=py;
						}
						else
						{
							dx=values[start+5];
							dy=values[start+6];
							
							isBack=doJumpNodePart3(px,py,dx,dy);
						}
					}
					break;
					case 3:
					{
						//不用做什么
					}
					break;
				}
			}
			else
			{
				isBack=doJumpNode(values[start+1],values[start+2],values[start+3],values[start+4]);
			}
		}
		
		return _jx!=-1;
	}
	
	/** 执行一次调点(返回是否执行完毕) */
	private boolean doJumpNode(int px,int py,int lx,int ly)
	{
		//Ctrl.print("doJumpNode",px,py,lx,ly);
		
		if(px==_ex && py==_ey)
		{
			_jx=px;
			_jy=py;
			return true;
		}
		
		int moveType=_moveType;
		boolean needCrowed=_needCrowed;
		
		if(!isEnable(moveType,needCrowed,px,py))
		{
			_jx=-1;
			_jy=-1;
			return true;
		}
		
		IntList jumpArgList=_jumpArgList;
		
		Dir d=getDirByPos(lx,ly,px,py);
		
		int dx=d.x;
		int dy=d.y;
		
		//对角
		if(dx!=0 && dy!=0)
		{
			// 左下能走且左不能走
			if(isEnable(moveType,needCrowed,px-dx,py+dy) && !isEnable(moveType,needCrowed,px-dx,py))
			{
				_jx=px;
				_jy=py;
				return true;
			}
			
			//右上能走且上不能走
			if(isEnable(moveType,needCrowed,px+dx,py-dy) && !isEnable(moveType,needCrowed,px,py-dy))
			{
				_jx=px;
				_jy=py;
				return true;
			}
			
			addListSome(jumpArgList,1,px+dx,py,px,py,dx,dy);
			return false;
			
			//if(doJumpNode(px+dx,py,px,py))
			//{
			//	_jx=px;
			//	_jy=py;
			//	return true;
			//}
			//
			//if(doJumpNode(px,py+dy,px,py))
			//{
			//	_jx=px;
			//	_jy=py;
			//	return true;
			//}
		}
		else
		{
			//水平
			if(dx!=0)
			{
				if(_allowDiagonal)
				{
					// 右下能走且下不能走
					if(isEnable(moveType,needCrowed,px+dx,py+1) && !isEnable(moveType,needCrowed,px,py+1))
					{
						_jx=px;
						_jy=py;
						return true;
					}
					
					//右上能走且上不能走
					if(isEnable(moveType,needCrowed,px+dx,py-1) && !isEnable(moveType,needCrowed,px,py-1))
					{
						_jx=px;
						_jy=py;
						return true;
					}
				}
				else
				{
					//左右可走
					if(isEnable(moveType,needCrowed,px+1,py) || isEnable(moveType,needCrowed,px-1,py))
					{
						_jx=px;
						_jy=py;
						return true;
					}
				}
			}
			//垂直
			else
			{
				if(_allowDiagonal)
				{
					//右下能走且右不能走
					if(isEnable(moveType,needCrowed,px+1,py+dy) && !isEnable(moveType,needCrowed,px+1,py))
					{
						_jx=px;
						_jy=py;
						return true;
					}
					
					//左下能走且左不能走
					if(isEnable(moveType,needCrowed,px-1,py+dy) && !isEnable(moveType,needCrowed,px-1,py))
					{
						_jx=px;
						_jy=py;
						return true;
					}
				}
				else
				{
					//上下可走
					if(isEnable(moveType,needCrowed,px,py+1) || isEnable(moveType,needCrowed,px,py-1))
					{
						_jx=px;
						_jy=py;
						return true;
					}
				}
			}
		}
		
		if(_allowDiagonal)
		{
			if(isEnable(moveType,needCrowed,px+dx,py) || isEnable(moveType,needCrowed,px,py+dy))
			{
				addListSome(jumpArgList,3,px+dx,py+dy,px,py,dx,dy);
				return false;
				
				//if(doJumpNode(px+dx,py+dy,px,py))
				//	return true;
				//else
				//	return false;
			}
		}
		
		_jx=-1;
		_jy=-1;
		return true;
	}
	
	private boolean doJumpNodePart3(int px,int py,int dx,int dy)
	{
		if(_allowDiagonal)
		{
			if(isEnable(_moveType,_needCrowed,px+dx,py) || isEnable(_moveType,_needCrowed,px,py+dy))
			{
				addListSome(_jumpArgList,3,px+dx,py+dy,px,py,dx,dy);
				return false;
			}
		}
		
		_jx=-1;
		_jy=-1;
		return true;
	}
	
	/** 输出路径 */
	private void makePath(IntList result,Node p)
	{
		//不包含起点
		while(p.parent!=null)
		{
			//反向添加
			result.add2(p.y,p.x);
			p=p.parent;
		}
		
		result.reverse();
	}
	
	/** 清理数据 */
	private void clear()
	{
		_queue.clear();
		ObjectPool<Node> nodePool=_nodePool;
		
		IntObjectMap<Node> fDic;
		if(!(fDic=_dic).isEmpty())
		{
			fDic.forEachValueAndClear(v->
			{
				nodePool.back(v);
			});
		}
		
		SList<Node> neighbourList=_neighbourList;
		if(!neighbourList.isEmpty())
		{
			neighbourList.forEachAndClear(v->
			{
				nodePool.back(v);
			});
		}
		
		_jumpArgList.clear();
	}
	
	/** 节点比较 */
	private static int nodeCompare(Node n0,Node n1)
	{
		return Integer.compare(n0.f,n1.f);
	}
	
	/** 朝向 */
	private static class Dir
	{
		int x;
		int y;
	}
	
	private static class Node implements IPoolObject
	{
		/** 父节点 */
		public Node parent;
		
		public int x;
		public int y;
		
		public int index;
		
		/** 开销值 */
		public int g;
		/** 曼哈顿距离 */
		public int h;
		/** 开销(g+h) */
		public int f;
		
		/** 是否关闭 */
		public boolean isClose;
		
		/** 朝向 */
		private Dir _dir;
		
		public Node()
		{
		
		}
		
		public void init(Node parent,int x,int y,int g,int h)
		{
			this.parent=parent;
			this.x=x;
			this.y=y;
			this.index=getIndex(x,y);
			this.g=g;
			this.h=h;
			this.f=g+h;
		}
		
		public Dir getDir()
		{
			if(_dir==null)
			{
				if(parent!=null)
				{
					_dir=getDirByPos(parent,this);
				}
			}
			
			return _dir;
		}
		
		@Override
		public void clear()
		{
			parent=null;
			_dir=null;
			isClose=false;
		}
	}
}
