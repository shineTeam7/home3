using System;
using UnityEngine;

namespace ShineEngine
{
	/** 资源池控制 */
	public class AssetPoolControl
	{
		private static GameObject _poolRoot;
		private static Transform _poolRootTrans;

		private static AssetPool[] _poolArr;

		/** 自定义 */
		private static IntObjectMap<Func<GameObject>> _custemCreateFuncDic=new IntObjectMap<Func<GameObject>>();

		public static void init()
		{
			_poolRoot=GameObject.Find(ShineSetting.poolRootName);

			if(_poolRoot==null)
			{
				Ctrl.throwError("未找到池对象根");
				return;
			}

			_poolRootTrans=_poolRoot.transform;
			_poolRoot.SetActive(false);

			//最多50种
			_poolArr=new AssetPool[50];

			TimeDriver.instance.setInterval(onSecond,1000);

			registPool(AssetPoolType.Default,200,120 * 1000);
		}

		private static void onSecond(int delay)
		{
			AssetPool pool;

			for(int i=0,len=_poolArr.Length;i<len;i++)
			{
				if((pool=_poolArr[i])!=null)
				{
					pool.onSecond();
				}
			}
		}

		public static void registCustomCreate(int id,Func<GameObject> func)
		{
			_custemCreateFuncDic.put(id,func);
		}

		/** 默认注册 */
		public static void registPool(int type)
		{
			registPool(type,100,120 * 1000);
		}

		/** 注册一池 */
		public static void registPool(int type,int num,int time)
		{
			AssetPool pool=_poolArr[type];

			if(pool==null)
			{
				_poolArr[type]=pool=new AssetPool();
			}

			pool.keepNum=num;
			pool.keepTime=time;
		}

		/** 加载一个(计数+1) */
		public static void loadOne(int type,int id,Action overFunc)
		{
			AssetPool pool=_poolArr[type];

			if(pool==null)
			{
				Ctrl.warnLog("未注册AssetPoolType",type);
				return;
			}

			pool.loadOne(id,overFunc);
		}

		/** 获取asset(不改变计数) */
		public static GameObject getAsset(int type,int id)
		{
			AssetPool pool=_poolArr[type];

			if(pool==null)
			{
				Ctrl.warnLog("未注册AssetPoolType",type);
				return null;
			}


			return pool.getAsset(id,false);
		}

		/** 获取asset(计数+1)(用于确定资源存在，不走加载的场合) */
		public static GameObject getAssetAndIncrease(int type,int id)
		{
			AssetPool pool=_poolArr[type];

			if(pool==null)
			{
				Ctrl.warnLog("未注册AssetPoolType",type);
				return null;
			}

			return pool.getAsset(id,true);
		}

		/** 获取自定义asset(计数+1) */
		public static GameObject getCustomAsset(int type,int id)
		{
			AssetPool pool=_poolArr[type];

			if(pool==null)
			{
				Ctrl.warnLog("未注册AssetPoolType",type);
				return null;
			}

			return pool.getCustomAsset(id);
		}

		/** 卸载一个(obj可为空)(计数-1) */
		public static void unloadOne(int type,int id,GameObject obj)
		{
			AssetPool pool=_poolArr[type];

			if(pool==null)
			{
				Ctrl.warnLog("未注册AssetPoolType",type);
				return;
			}

			pool.unloadOne(id,obj);
		}

		private struct ResourceNode
		{
			/** 资源 */
			public GameObject obj;

			/** 入池时间 */
			public long inTime;
		}

		private class AssetQueue
		{
			/** 加载状态-空闲中 */
			private const int LoadState_Free=0;
			/** 加载状态-加载中 */
			private const int LoadState_Loading=1;
			/** 加载状态-已完成 */
			private const int LoadState_Complete=2;

			public int id;

			private AssetPool _parent;

			/** 加载标记(0:未开始,1:加载中,2:已完成) */
			private int _loadState=LoadState_Free;

			private int _refCount=0;

			/** 资源队列 */
			private SQueue<ResourceNode> _queue=new SQueue<ResourceNode>();

			private SList<Action> _callFuncs=new SList<Action>();

			public AssetQueue(AssetPool pool)
			{
				_parent=pool;
			}

			public long getHeadTime()
			{
				if(_queue.isEmpty())
					return 0L;

				return _queue.peek().inTime;
			}

			public bool releaseHead()
			{
				if(_queue.isEmpty())
					return false;

				ResourceNode node=_queue.poll();

				GameObject obj=node.obj;
				node.obj = null;

				GameObject.Destroy(obj);

				--_parent.num;

				//直接-1
				if(_refCount==0 && _queue.isEmpty())
				{
					LoadControl.unloadOne(id);
				}

				return true;
			}

			public void loadOne(Action overFunc)
			{
				++_refCount;

				//自定义
				if(id<-1)
				{
					overFunc();
				}
				else
				{
					if(_loadState==LoadState_Complete)
					{
						overFunc();
					}
					else
					{
						_callFuncs.add(overFunc);

						if(_loadState==LoadState_Free)
						{
							_loadState=LoadState_Loading;

							LoadControl.loadOne(id,onLoadComplete);
						}
					}
				}
			}

			public GameObject getAsset(bool needIncrease)
			{
				if(needIncrease)
				{
					++_refCount;
				}

				if(!_queue.isEmpty())
				{
					ResourceNode node=_queue.poll();

					_parent.pickOne(id);
					return node.obj;
				}
				else
				{
					//直接从LoadControl取
					return LoadControl.getAsset(id);
				}
			}

			public GameObject getCustomAsset()
			{
				++_refCount;

				if(!_queue.isEmpty())
				{
					ResourceNode node=_queue.poll();

					_parent.pickOne(id);

					return node.obj;
				}
				else
				{
					Func<GameObject> func=_custemCreateFuncDic.get(id);

					if(func==null)
					{
						Ctrl.throwError("未注册自定义创建方法",id);
						return null;
					}

					return func();
				}
			}

			private void onLoadComplete()
			{
				_loadState=LoadState_Complete;

				Action[] values=_callFuncs.getValues();

				for(int i=0,len=_callFuncs.size();i<len;++i)
				{
					try
					{
						values[i]();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}

					values[i]=null;
				}

				_callFuncs.justClearSize();
			}

			public void unloadOne(GameObject obj)
			{
				--_refCount;

				if(obj!=null)
				{
					obj.transform.SetParent(_poolRootTrans);

					ResourceNode node=new ResourceNode();
					node.obj=obj;
					node.inTime=Ctrl.getFixedTimer();
					_queue.add(node);

					++_parent.num;
				}

				//直接-1
				if(_refCount==0 && _queue.isEmpty())
				{
					LoadControl.unloadOne(id);
				}
			}
		}

		/** 资源池 */
		private class AssetPool
		{
			/** 保留个数 */
			public int keepNum;
			/** 保留时间 */
			public int keepTime;

			/** 当前缓存数目 */
			public int num=0;

			private int _headID=-1;
			private long _headTime=0L;

			public IntObjectMap<AssetQueue> _dic=new IntObjectMap<AssetQueue>();

			public void onSecond()
			{
				long now=Ctrl.getFixedTimer();

				while(_headID!=-1)
				{
					//超时
					if((now - _headTime)>keepTime)
					{
						releaseOne();
					}
					else
					{
						break;
					}
				}
			}

			public void loadOne(int id,Action overFunc)
			{
				AssetQueue queue=_dic.get(id);

				if(queue==null)
				{
					_dic.put(id,queue=new AssetQueue(this));
					queue.id=id;
				}

				queue.loadOne(overFunc);
			}

			public GameObject getAsset(int id,bool needIncrease)
			{
				AssetQueue queue=_dic.get(id);

				if(queue==null)
				{
					if(needIncrease)
					{
						_dic.put(id,queue=new AssetQueue(this));
						queue.id=id;
					}
					else
					{
						return null;
					}
				}

				return queue.getAsset(needIncrease);
			}

			public GameObject getCustomAsset(int id)
			{
				AssetQueue queue=_dic.get(id);

				if(queue==null)
				{
					_dic.put(id,queue=new AssetQueue(this));
					queue.id=id;
				}

				return queue.getCustomAsset();
			}

			public void unloadOne(int id,GameObject obj)
			{
				AssetQueue queue=_dic.get(id);

				if(queue==null)
					return;

				queue.unloadOne(obj);

				if(obj!=null)
				{
					if(_headID==-1)
					{
						_headID=id;
						_headTime=queue.getHeadTime();
					}

					if(num>keepNum)
					{
						releaseOne();
					}
				}
			}

			/** 拿走一个 */
			public void pickOne(int id)
			{
				//数目减少
				--num;

				if(_headID==id)
				{
					findNextHead();
				}
			}

			private void releaseOne()
			{
				if(_headID==-1)
					return;

				AssetQueue queue=_dic.get(_headID);

				if(!queue.releaseHead())
				{
					Ctrl.errorLog("不应该release不掉");
				}

				findNextHead();
			}

			private void findNextHead()
			{
				_headID=-1;
				_headTime=0L;

				long t;

				IntObjectMap<AssetQueue> fDic;
				if(!(fDic=_dic).isEmpty())
				{
					AssetQueue[] values;
					AssetQueue v;

					for(int i=(values=fDic.getValues()).Length - 1;i>=0;--i)
					{
						if((v=values[i])!=null)
						{
							if((t=v.getHeadTime())>0L)
							{
								if(_headID==-1 || t<_headTime)
								{
									_headID=v.id;
									_headTime=t;
								}
							}
						}
					}
				}
			}
		}
	}
}