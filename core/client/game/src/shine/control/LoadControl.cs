using System;
#if UNITY_EDITOR
	using UnityEditor;
#endif
using UnityEngine;
using Object = UnityEngine.Object;

namespace ShineEngine
{
	/** 加载控制 */
	public class LoadControl
	{
		/** loader数目 */
		private const int _loaderNum=8;

		private const int LoadOver_Complete=1;
		private const int LoadOver_IOError=2;
		private const int LoadOver_Stop=3;

		/** 版本 */
		private static int _version;
		/** 是否就绪 */
		private static bool _ready=false;
		//映射组
		/** 资源信息字典 */
		private static IntObjectMap<ResourceInfoData> _resourceInfoDic=new IntObjectMap<ResourceInfoData>(ShineSetting.resourceDefaultSize);
		/** 资源名对id字典 */
		private static SMap<string,int> _resourceNameToIDDic=new SMap<string,int>(ShineSetting.resourceDefaultSize);
		/** 资源自定义id自增 */
		private static int _resourceCustomID=ShineSetting.resourceAutoIndex;

		//资源组
		/** 资源id对包id字典 */
		private static IntIntMap _resourceElementToPackDic=new IntIntMap(ShineSetting.resourceDefaultSize);
		/** 资源包定义组(key:packID)(只有包资源才有,独立资源没有) */
		private static IntObjectMap<BundleInfoData> _resourcePackDefineDic=new IntObjectMap<BundleInfoData>(ShineSetting.resourceDefaultSize);

		//实际加载
		/** 资源组(实际加载的url组,包括包资源和非包资源) */
		private static IntObjectMap<ResourceNodeData> _resourceDic=new IntObjectMap<ResourceNodeData>();


		//加载部分

		/** info池组 */
		private static ObjectPool<LoadInfoObj> _infoPool=new ObjectPool<LoadInfoObj>(()=>new LoadInfoObj());
		/** pack池组 */
		private static ObjectPool<LoadPackObj> _packPool=new ObjectPool<LoadPackObj>(()=>new LoadPackObj());
		/** obj池组 */
		private static ObjectPool<LoadOneObj> _objPool=new ObjectPool<LoadOneObj>(()=>new LoadOneObj());
		/** node池组 */
		private static ObjectPool<ResourceNodeData> _nodePool=new ObjectPool<ResourceNodeData>(()=>new ResourceNodeData());
		//TODO:做下资源删除的数目和时间驱动

		//pack层
		/** loader组 */
		private static ShineLoader[] _loaders=new ShineLoader[_loaderNum];
		/** 加载中标记 */
		private static bool[] _loadings=new bool[_loaderNum];
		/** 加载数目记录 */
		private static int _loadingFulls=0;
		/** 加载中对象 */
		private static LoadPackObj[] _thisOnes=new LoadPackObj[_loaderNum];
		/** 待加载组查询字典 */
		private static IntObjectMap<LoadPackObj> _waitLoadPackDic=new IntObjectMap<LoadPackObj>();
		/** 加载队列组 */
		private static SQueue<LoadPackObj>[] _waitLoadPackLists=new SQueue<LoadPackObj>[LoadPriorityType.Bottom + 1];
		/** 待加载计数 */
		private static int _waitLoadNum=0;
		/** 加载优先级起始(遍历加速) */
		private static int _loadPriorityStart=LoadPriorityType.Top;
		/** 加载中标记组(等待中不算)(key:id,value:loaderID)（包） */
		private static IntObjectMap<LoadPackObj> _loadingPackDic=new IntObjectMap<LoadPackObj>();
		/** 加载过的资源组记录(包) */
		private static IntSet _loadedMarkDic=new IntSet();

		//obj层

		/** 加载+解析中标记组(key:id,value:loaderID)（单+包） */
		private static IntObjectMap<LoadOneObj> _loadingObjDic=new IntObjectMap<LoadOneObj>();
		/** 加载完成回调（单+包） */
		private static IntObjectMap<SList<Action>> _loadCompleteCallDic=new IntObjectMap<SList<Action>>();

		/** 加载序号自增 */
		private static int _infoIndex=0;

		//解析部分

		/** 解析中字典 */
		private static IntObjectMap<LoadOneObj> _analysisDic=new IntObjectMap<LoadOneObj>();
		/** 临时删除组 */
		private static SList<ResourceNodeData> _tempDeleteList=new SList<ResourceNodeData>();

		/** 初始化 */
		public static void init()
		{
			TimeDriver.instance.setFrame(onFrame);
			TimeDriver.instance.setInterval(onCheckResourceKeep,ShineSetting.resourceKeepCheckDelay*1000);

			for(int i=0;i<_loaderNum;++i)
			{
				initLoader(i);
			}
		}

		private static void initLoader(int index)
		{
			ShineLoader loader;
			_loaders[index]=loader=new ShineLoader(index);

			loader.setCompleteCall(()=>toLoadOver(index,LoadOver_Complete));
			loader.setIoErrorCall(()=>toLoadOver(index,LoadOver_IOError));
		}

		private static void onFrame(int delay)
		{
			//loader们
			foreach(ShineLoader loader in _loaders)
			{
				loader.onFrame(delay);
			}

			runLoadOnce();
			checkAnalysis();
		}

		/** 获取当前版本 */
		public static int getVersion()
		{
			return _version;
		}

		/** 检查资源保持 */
		private static void onCheckResourceKeep(int delay)
		{
			int checkDelay=ShineSetting.resourceKeepCheckDelay;

			ResourceNodeData[] values;
			ResourceNodeData v;

			//Tips:此处可能连续删除
			for(int i=(values=_resourceDic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					if(v.isFree())
					{
						if(v.timeOut>0)
						{
							if((v.timeOut-=checkDelay)<=0)
							{
								v.timeOut=0;

								_tempDeleteList.add(v);
							}
						}
						else
						{
							//非补丁状态
							if(!ShineSetting.bundleAnalysisAll)
							{
								Ctrl.errorLog("存在没删掉的",v.id,getResourceNameByID(v.id));
							}
						}
					}
				}
			}

			if(!_tempDeleteList.isEmpty())
			{
				values=_tempDeleteList.getValues();

				for(int i=0,len=_tempDeleteList.size();i<len;++i)
				{
					if((v=values[i]).isEnable())
					{
						deleteOneNode(v);
					}

					values[i]=null;
				}

				_tempDeleteList.justClearSize();

				if(!ShineSetting.localLoadWithOutBundle)
				{
					//TODO:优化下调用时机
					//移除资源
					Resources.UnloadUnusedAssets();
				}
			}
		}

		/** 移除一个节点 */
		private static void deleteOneNode(ResourceNodeData node)
		{
			if(!ShineSetting.localLoadWithOutBundle)
			{
				if(node.isPack())
				{
					ResourceNodeData child;

					foreach(int v in node.packInfo.assets)
					{
						if((child=_resourceDic.get(v))!=null)
						{
							if(!child.isFree())
							{
								Ctrl.errorLog("节点不该还存活",child.id);
							}

							toDeleteOneNode(child);
						}
					}
				}
			}

			toDeleteOneNode(node);
		}

		//--信息相关--//

		/** 清空资源信息 */
		public static void clearResourceInfo()
		{
			_resourceInfoDic.clear();
			_resourceNameToIDDic.clear();
			_resourceElementToPackDic.clear();
			_resourcePackDefineDic.clear();
			_resourceCustomID=ShineSetting.resourceAutoIndex;
		}

		/** 注册资源信息 */
		public static void registResource(string name,int id)
		{
			registResource(name,id,ResourceType.getFileResourceType(name));
		}

		/** 注册资源信息 */
		public static void registResource(string name,int id,int type)
		{
			if(ShineSetting.openCheck)
			{
				if(_resourceInfoDic.contains(id))
				{
					Ctrl.throwError("registResource已存在的id",name,id);
				}

				if(_resourceNameToIDDic.contains(name))
				{
					Ctrl.throwError("registResource已存在的name",name,id,_resourceNameToIDDic.get(name));
				}

                if (name.StartsWith("source/"))
                {
                    Ctrl.throwError("还存在source开头的资源");
                }
            }

            ResourceInfoData infoData = new ResourceInfoData();
            infoData.id = id;
            infoData.type = type;
            infoData.name = name;

            _resourceInfoDic.put(id, infoData);
            _resourceNameToIDDic.put(name, id);
        }


		/** 注册bundle信息 */
		public static void registBundleInfo(BundleInfoData data)
		{
			_resourcePackDefineDic.put(data.id,data);

			IntIntMap resourceElementToPackDic=_resourceElementToPackDic;
			int[] dataAssets=data.assets;
			int dataID=data.id;

			for(int i=dataAssets.Length - 1;i>=0;--i)
			{
				resourceElementToPackDic.put(dataAssets[i],dataID);
			}
		}

		/** 注册资源结束 */
		public static void registBundleOver()
		{
			//版本号加1
			++_version;
			_ready=true;

			//			Ctrl.print("看bundle结构");
			//
			//			_resourcePackDefineDic.forEachValue(v=>
			//			{
			//				Ctrl.print("单包",getResourceInfoByID(v.id).name);
			//
			//				foreach(int v2 in v.depends)
			//				{
			//					BundleInfoUseData bb=_resourcePackDefineDic.get(v2);
			//
			//					if(bb.depends.Length>0)
			//					{
			//						Ctrl.print("多层",bb.id,getResourceInfoByID(bb.id).name);
			//					}
			//					else
			//					{
			//						Ctrl.print("单层",getResourceInfoByID(v.id).name,bb.id,getResourceInfoByID(bb.id).name);
			//					}
			//				}
			//			});
		}

		/** 获取bundle信息字典 */
		public static IntObjectMap<BundleInfoData> getBundleInfoDic()
		{
			return _resourcePackDefineDic;
		}

		/** 获取包信息(判定是包资源) */
		public static BundleInfoData getBundleInfo(int id)
		{
			return _resourcePackDefineDic.get(id);
		}

		/** 通过序号获取获取资源信息(没有返回null) */
		private static ResourceInfoData getResourceInfoByID(int id)
		{
			return _resourceInfoDic.get(id);
		}

		/** 通过序号获取获取资源名(没有返回null) */
		public static string getResourceNameByID(int id)
		{
			return _resourceInfoDic.get(id).name;
		}

		/** 获取资源名序号(没有会报错) */
		public static int getResourceIDByName(string name,bool needCheck=true)
		{
			if(name==null || name.isEmpty())
				return -1;

			//没有扩展名
			if(name.LastIndexOf('.')==-1)
				name=name + ".prefab";//默认prefab

			if(ShineSetting.localLoadWithOutBundle)
			{
				return getResourceIDByNameAbs(name);
			}

			int id;

			if((id=_resourceNameToIDDic.getOrDefault(name,-1))==-1)
			{
				if(needCheck && ShineSetting.openCheck)
				{
					Ctrl.errorLog("找不到该名字的资源编号:",name);
				}
			}

			return id;
		}

		/** 获取资源名序号(没有就注册) */
		public static int getResourceIDByNameAbs(string name)
		{
			if(!_ready)
			{
				if(!ShineSetting.isEditor)
				{
					Ctrl.warnLog("LoadControl未就绪");
				}
				return -1;
			}

			int id;

			if((id=_resourceNameToIDDic.getOrDefault(name,-1))==-1)
			{
				id=_resourceCustomID++;

				registResource(name,id);
			}

			return id;
		}

		/** 获取一组名字对编号 */
		public static int[] getResourceIDsByNames(string[] names)
		{
			int len=names.Length;
			int[] re=new int[len];

			for(int i=0;i<len;++i)
			{
				re[i]=getResourceIDByName(names[i]);
			}

			return re;
		}

		/** 获取资源的加载序号(对应包的序号) */
		private static int getResourceLoadID(int id)
		{
			int re=_resourceElementToPackDic.get(id);

			if(re!=0)
			{
				return re;
			}

			return id;
		}

		//--加载相关--//

		/** 获取节点 */
		private static ResourceNodeData getNode(int id)
		{
			ResourceNodeData node;

			if((node=_resourceDic.get(id))!=null)
			{
				return node;
			}

			if(getResourceInfoByID(id)==null)
			{
				Ctrl.throwError("不存在的资源id",id);
				return null;
			}

			node=_nodePool.getOne();
			node.id=id;
			//包资源才有
			node.packInfo=_resourcePackDefineDic.get(id);

			//非包资源才有
			int packID=_resourceElementToPackDic.get(id);
			node.packID=packID>0 ? packID : -1;

			node.refreshTimeOut();

			_resourceDic.put(id,node);

			return node;
		}

		/** 节点依赖计数加1 */
		private static void addNodeDepend(ResourceNodeData node)
		{
			//第一次
			if((++node.beDependCount)==1)
			{
				if(node.packInfo!=null)
				{
					foreach(int v in node.packInfo.depends)
					{
						addNodeDepend(getNode(v));
					}
				}
			}
		}

		/** 节点依赖计数加1 */
		private static void subNodeDepend(ResourceNodeData node)
		{
			if(node.beDependCount==0)
			{
				Ctrl.errorLog("依赖计数出错",node.id,getResourceNameByID(node.id));
				return;
			}

			//归零
			if((--node.beDependCount)==0)
			{
				if(node.packInfo!=null)
				{
					foreach(int v in node.packInfo.depends)
					{
						subNodeDepend(getNode(v));
					}
				}

				checkNodeFree(node);
			}
		}

		/** 节点引用计数加1 */
		private static void addNodeRef(ResourceNodeData node)
		{
			//刚加
			if((++node.refCount)==1)
			{
				//只记录当前节点
				if(ShineSetting.localLoadWithOutBundle)
					return;

				if(node.packID>0)
				{
					addNodeDepend(getNode(node.packID));
				}
				else
				{
					if(node.packInfo!=null)
					{
						foreach(int v in node.packInfo.depends)
						{
							addNodeDepend(getNode(v));
						}
					}
					else
					{
						Ctrl.throwError("不该没有包信息",getResourceNameByID(node.id));
					}
				}
			}
		}

		/** 节点引用计数减1 */
		private static void subNodeRef(ResourceNodeData node)
		{
			if(node.refCount==0)
			{
				Ctrl.errorLog("引用计数出错",node.id,getResourceNameByID(node.id));
				return;
			}

			//归零
			if((--node.refCount)==0)
			{
				//只记录当前节点
				if(ShineSetting.localLoadWithOutBundle)
					return;

				//非包
				if(node.packID>0)
				{
					subNodeDepend(getNode(node.packID));
				}
				//包资源
				else
				{
					if(node.packInfo!=null)
					{
						foreach(int v in node.packInfo.depends)
						{
							subNodeDepend(getNode(v));
						}
					}
					else
					{
						Ctrl.throwError("不该没有包信息2",getResourceNameByID(node.id));
					}
				}

				checkNodeFree(node);
			}
		}

		private static void checkNodeFree(ResourceNodeData node)
		{
			if(node.isFree())
			{
				node.refreshTimeOut();

				int id=node.id;

				int packID=node.getSingleOrPackID();

				LoadPackObj pack;

				//加载中
				if((pack=_loadingPackDic.get(packID))!=null)
				{
					toLoadOver(pack.index,LoadOver_Stop);
				}
				//等待队列
				else if((pack=_waitLoadPackDic.get(packID))!=null)
				{
					//标记删除
					pack.removed=true;

					unBindDepend(pack);

					if(!pack.beDepends.isEmpty())
					{
						Ctrl.errorLog("不应该还有被依赖组:",getResourceNameByID(pack.id));
						unBindBeDepend(pack);
					}

					completeOnePack(pack,false);

					//删掉
					--_waitLoadNum;
					_waitLoadPackDic.remove(pack.id);
				}

				LoadOneObj obj;

				//加载/等待中的单数据(此时如果还在，就处理,此时为包内资源的情况)
				if((obj=_loadingObjDic.get(id))!=null)
				{
					//失效完成
					completeOneObj(obj,false,false);
				}
			}
		}

		/** 标记加载中 */
		private static void setLoading(int index,bool value)
		{
			if(_loadings[index]==value)
				return;

			_loadings[index]=value;

			if(value)
			{
				++_loadingFulls;
			}
			else
			{
				--_loadingFulls;
			}
		}

		/** 检查解析 */
		private static void checkAnalysis()
		{
			IntObjectMap<LoadOneObj> analysisDic;

			if((analysisDic=_analysisDic).isEmpty())
				return;

			foreach(LoadOneObj v in analysisDic)
			{
				//完成了
				if(v.assetBundleRequest.isDone)
				{
					analysisOneOver(v);
				}
			}
		}

		/** 解析完一个 */
		private static void analysisOneOver(LoadOneObj v)
		{
			//赋值资源
			getNode(v.id).asset=v.assetBundleRequest.asset;

			completeOneObj(v,true);
		}

		/** 执行一次加载 */
		private static void runLoadOnce()
		{
			//满了
			if(_loadingFulls==_loaderNum)
				return;

			LoadPackObj pack=getOneLoadPack();

			if(pack==null)
				return;

			bool[] loadings=_loadings;

			for(int i=0;i<_loaderNum;++i)
			{
				if(!loadings[i])
				{
					if(pack==null)
					{
						pack=getOneLoadPack();

						//没了
						if(pack==null)
							return;
					}

					toLoadOne(pack,i);
					pack=null;
				}
			}

			if(pack!=null)
			{
				Ctrl.throwError("出错,加载对象剩下了");
			}
		}

		/** 取一个加载任务 */
		private static LoadPackObj getOneLoadPack()
		{
			if(_waitLoadNum<=0)
				return null;

			SQueue<LoadPackObj>[] queues=_waitLoadPackLists;
			SQueue<LoadPackObj> queue;

			LoadPackObj pack;

			for(int i=_loadPriorityStart;i<=LoadPriorityType.Bottom;_loadPriorityStart=++i) //标记
			{
				if((queue=queues[i])!=null)
				{
					while(true)
					{
						if(queue.isEmpty())
							break;

						pack=queue.peek();

						if(pack.removed)
						{
							queue.poll();

							//已经移除过了
							_packPool.back(pack);

							continue;
						}

						//依赖没完
						if(pack.depends!=null && !pack.depends.isEmpty())
						{
							return null;
						}

						queue.poll();

						_waitLoadPackDic.remove(pack.id);
						--_waitLoadNum;

						return pack;
					}
				}
			}

			return null;
		}

		private static void toLoadOne(LoadPackObj pack,int index)
		{
			_thisOnes[index]=pack;

			//资源已存在
			if(hasResource(pack.id))
			{
				Ctrl.throwError("不应该资源已存在");
//				resourceComplete(index,false);
			}
			else
			{
				setLoading(index,true);

				//赋值line
				pack.index=index;

				_loadingPackDic.put(pack.id,pack);

				ResourceInfoData infoData=_resourceInfoDic.get(pack.id);

				if(infoData==null)
				{
					Ctrl.throwError("不能没有资源信息:" + pack.id);
					return;
				}

				//加载
				_loaders[index].loadResource(infoData.name);
			}
		}

		/** 一次加载结束(只对加载中的进行) */
		private static void toLoadOver(int index,int type)
		{
			LoadPackObj pack=_thisOnes[index];

			if(pack==null)
			{
				Ctrl.errorLog("不该为空");
				return;
			}

			//清空
			_thisOnes[index]=null;
			_loadingPackDic.remove(pack.id);

			if(!pack.depends.isEmpty())
			{
				Ctrl.throwError("不应该还有没加载的依赖组");
			}

			unBindBeDepend(pack);

			setLoading(index,false);

			switch(type)
			{
				case LoadOver_Complete:
				{
					oneComplete(pack);
				}
					break;
				case LoadOver_IOError:
				{
					completeOnePack(pack,false);
				}
					break;
				case LoadOver_Stop:
				{
					//卸载
					_loaders[index].unload();

					completeOnePack(pack,false);
				}
					break;
			}

			if(ShineSetting.openCheck)
			{
				if(!pack.isDependAndBeDependEmpty())
				{
					Ctrl.throwError("入池时,不干净");
				}
			}

			_packPool.back(pack);
		}

		// private void releasePackObj(LoadPackObj pack)
		// {
		// 	if(ShineSetting.openCheck)
		// 	{
		// 		if(!pack.isDependAndBeDependEmpty())
		// 		{
		// 			Ctrl.throwError("出错,release时,不是全空的",getResourceNameByID(pack.id));
		// 		}
		// 	}
		//
		// 	_packPool.back(pack);
		// }

		/** 解绑被依赖组 */
		private static void unBindBeDepend(LoadPackObj pack)
		{
			SSet<LoadPackObj> beDepends;
			//解绑被依赖组
			if(!(beDepends=pack.beDepends).isEmpty())
			{
				LoadPackObj[] keys=beDepends.getKeys();
				LoadPackObj k;

				for(int i=keys.Length-1;i>=0;--i)
				{
					if((k=keys[i])!=null)
					{
						k.depends.remove(pack);
						keys[i]=null;
					}
				}

				beDepends.justClearSize();
			}
		}

		/** 解绑依赖组 */
		private static void unBindDepend(LoadPackObj pack)
		{
			SSet<LoadPackObj> depends;
			//解绑被依赖组
			if(!(depends=pack.depends).isEmpty())
			{
				LoadPackObj[] keys=depends.getKeys();
				LoadPackObj k;

				for(int i=keys.Length-1;i>=0;--i)
				{
					if((k=keys[i])!=null)
					{
						k.beDepends.remove(pack);
						keys[i]=null;
					}
				}

				depends.justClearSize();
			}
		}

		/** 执行包资源的完成 */
		private static void completeOnePack(LoadPackObj pack,bool needCallBack)
		{
			SList<LoadOneObj> list=pack.objs;
			LoadOneObj obj;

			for(int i=0,len=list.Count;i<len;++i)
			{
				if((obj=list[i]).removed)
				{
					_objPool.back(obj);
				}
				else
				{
					completeOneObj(list[i],needCallBack);
				}
			}
		}

		/** 完成包并回收 */
		private static void deletePack(LoadPackObj pack)
		{
			_loadingPackDic.remove(pack.id);
			completeOnePack(pack,false);
			_packPool.back(pack);
		}

		private static void laterCompleteOneObj(LoadOneObj obj)
		{
			TimeDriver.instance.callLater(()=>
			{
				//还是
				if(_loadingObjDic.get(obj.id)==obj)
				{
					completeOneObj(obj,true);
				}
			});
		}

		private static void completeOneObj(LoadOneObj obj,bool needCallBack)
		{
			completeOneObj(obj,needCallBack,true);
		}

		/** 完成一个数据 */
		private static void completeOneObj(LoadOneObj obj,bool needCallBack,bool needRemove)
		{
			//移除加载标记
			_loadingObjDic.remove(obj.id);
			//以及解析部分
			_analysisDic.remove(obj.id);

			SList<Action> overFuncs=_loadCompleteCallDic.remove(obj.id);

			if(needCallBack && overFuncs!=null)
			{
				for(int i=0,len=overFuncs.Count;i<len;++i)
				{
					TimeDriver.instance.callLater(overFuncs[i]);
				}
			}

			SList<LoadInfoObj> infos;

			if(!(infos=obj.infos).isEmpty())
			{
				LoadInfoObj infoObj;

				for(int i=0,len=infos.Count;i<len;++i)
				{
					if((++(infoObj=infos[i]).num)==infoObj.max)
					{
						if(infoObj.complete!=null)
						{
							if(needCallBack)
							{
								TimeDriver.instance.callLater(infoObj.complete);
							}

							_infoPool.back(infoObj);
						}
					}
				}
			}

			if(needRemove)
			{
				_objPool.back(obj);
			}
			else
			{
				obj.removed=true;
			}
		}

		//--删除部分--//

		/** 清空全部资源 */
		public static void clearAllResource()
		{
			LoadPackObj packObj;

			for(int i=0;i<_loaderNum;i++)
			{
				_loaders[i].unload();
				_loadings[i]=false;

				//加载中的
				if((packObj=_thisOnes[i])!=null)
				{
					_thisOnes[i]=null;
					deletePack(packObj);
				}
			}

			//等待加载的
			_waitLoadPackDic.forEachValueS(v=>
			{
				deletePack(v);
			});

			_waitLoadPackDic.clear();

			foreach(SQueue<LoadPackObj> v in _waitLoadPackLists)
			{
				if(v!=null)
				{
					v.clear();
				}
			}

			//版本号增
			++_version;
			_ready=false;
			_loadingFulls=0;
			_infoIndex=0;
			_loadPriorityStart=LoadPriorityType.Top;
			_waitLoadNum=0;

			_loadedMarkDic.clear();

			//清空加载过的所有资源节点
			_resourceDic.forEachValueS(v=>
			{
				toDeleteOneNode(v);
			});

			//加载中的也全部移除
			_loadingObjDic.forEachValueS(v=>
			{
				completeOneObj(v,false);
			});

			if(!_loadingObjDic.isEmpty()
			   || !_analysisDic.isEmpty()
			   || !_loadCompleteCallDic.isEmpty()
			   || !_loadingPackDic.isEmpty()
			   )
			{
				Ctrl.throwError("未清理干净");
			}
		}

		/** 检查是不是包错误 */
		private static void checkPackError(int id)
		{
			ResourceInfoData infoData=_resourceInfoDic.get(id);

			if(infoData.type==ResourceType.Bundle)
			{
				Ctrl.throwError("不该有包资源",infoData.id,infoData.name);
			}
		}

		private static LoadInfoObj createLoadInfo(Action callback)
		{
			LoadInfoObj info=_infoPool.getOne();
			info.num=0;
			info.max=0;
			info.complete=callback;
			info.index=++_infoIndex;

			return info;
		}

		private static void callbackFunc(Action func)
		{
			if(func!=null)
			{
				if(ShineSetting.loaderCallbackAsync)
				{
					TimeDriver.instance.callLater(func);
				}
				else
				{
					func();
				}
			}
		}

		/** 解析包(初始资源使用) */
		public static void analysisBundle(int packID)
		{
			BundleInfoData info=getBundleInfo(packID);

			if(info==null)
			{
				Ctrl.throwError("不是包资源",getResourceNameByID(packID));

				return;
			}

			AssetBundle bundle=(AssetBundle)getNode(packID).asset;

			if(bundle==null)
			{
				Ctrl.throwError("未加载bundle",getResourceNameByID(packID));
				return;
			}

			ResourceNodeData node;

			foreach(int v in info.assets)
			{
				ResourceInfoData resourceInfoData=getResourceInfoByID(v);

				(node=getNode(v)).asset=bundle.LoadAsset(resourceInfoData.getUName());
				//加一个计数
				addNodeRef(node);
			}
		}

		//--load部分--//

//		/** 加载一组 */
//		public static void loadSet(SSet<string> sets,Action callback,int priority=-1)
//		{
//			IntList list2=new IntList(sets.Count);
//
//			foreach(string v in sets)
//			{
//				list2.add(getResourceIDByNameAbs(v));
//			}
//
//			loadList(list2,callback,priority);
//		}
//
//		/** 加载一组 */
//		public static void loadList(SList<string> list,Action callback,int priority=-1)
//		{
//			IntList list2=new IntList(list.Count);
//
//			foreach(string v in list)
//			{
//				list2.add(getResourceIDByNameAbs(v));
//			}
//
//			loadList(list2,callback,priority);
//		}

		/** 加载一个 */
		public static void loadOne(string name,Action callback,int priority=-1)
		{
			if(!_ready)
			{
				Ctrl.warnLog("LoadControl未就绪");
				return;
			}

			if(string.IsNullOrEmpty(name))
			{
				callbackFunc(callback);

				return;
			}

			loadOne(getResourceIDByNameAbs(name),callback,priority);
		}

		/** 卸载一个 */
		public static void unloadOne(string name)
		{
			if(!_ready)
			{
				Ctrl.warnLog("LoadControl未就绪");
				return;
			}

			if(string.IsNullOrEmpty(name))
				return;

			unloadOne(getResourceIDByNameAbs(name));
		}

		/** 加载一组 */
		public static void loadList(IntList list,Action callback,int priority=-1,bool needCheckPack=true)
		{
			if(!_ready)
			{
				Ctrl.warnLog("LoadControl未就绪");
				return;
			}

			LoadInfoObj info=null;

			bool has=false;

			ResourceNodeData node;

			int[] values=list.getValues();
			int id;

			for(int i=0,len=list.size();i<len;++i)
			{
				if((id=values[i])>0)
				{
					if(needCheckPack)
					{
						checkPackError(id);
					}

					//计数加
					addNodeRef(node=getNode(id));

					//需要加载
					if(!node.hasResource())
					{
						if(info==null)
						{
							info=createLoadInfo(callback);
						}

						addOne(id,info,priority,true);

						has=true;
					}
				}

			}

			if(!has)
			{
				callbackFunc(callback);
				return;
			}
		}

		/** 卸载一组 */
		public static void unloadList(IntList list)
		{
			if(!_ready)
			{
				Ctrl.warnLog("LoadControl未就绪");
				return;
			}

			int[] values=list.getValues();
			int id;

			for(int i=0,len=list.size();i<len;++i)
			{
				if((id=values[i])>0)
				{
					unloadOne(id);
				}
			}
		}

		/** 加载一组 */
		public static void loadList(int[] list,Action callback,int priority=-1)
		{
			if(!_ready)
			{
				Ctrl.warnLog("LoadControl未就绪");
				return;
			}

			int id;

			LoadInfoObj info=null;

			bool has=false;

			ResourceNodeData node;

			for(int i=list.Length - 1;i>=0;--i)
			{
				if((id=list[i])>0)
				{
					//跳过
					if(!ShineSetting.bundleAnalysisAll)
					{
						checkPackError(id);
					}

					//计数加
					addNodeRef(node=getNode(id));

					//需要加载
					if(!node.hasResource())
					{
						if(info==null)
						{
							info=createLoadInfo(callback);
						}

						addOne(id,info,priority,true);

						has=true;
					}
				}
			}

			if(!has)
			{
				callbackFunc(callback);
				return;
			}
		}

		/** 卸载一组 */
		public static void unloadList(int[] list)
		{
			if(!_ready)
			{
				Ctrl.warnLog("LoadControl未就绪");
				return;
			}

			foreach(int v in list)
			{
				unloadOne(v);
			}
		}

		/** 加载一组 */
		public static void loadSet(IntSet sets,Action callback,int priority=-1)
		{
			if(!_ready)
			{
				Ctrl.warnLog("LoadControl未就绪");
				return;
			}

			LoadInfoObj info=null;

			bool has=false;

			ResourceNodeData node;

			int[] keys=sets.getKeys();
			int fv=sets.getFreeValue();
			int id;

			for(int i=keys.Length-1;i>=0;--i)
			{
				if((id=keys[i])!=fv && id>0)//大于0
				{
					checkPackError(id);

					//计数加
					addNodeRef(node=getNode(id));

					//需要加载
					if(!node.hasResource())
					{
						if(info==null)
						{
							info=createLoadInfo(callback);
						}

						addOne(id,info,priority,true);

						has=true;
					}
				}
			}

			if(!has)
			{
				callbackFunc(callback);
				return;
			}
		}

		/** 卸载一组 */
		public static void unloadSet(IntSet sets)
		{
			if(!_ready)
			{
				Ctrl.warnLog("LoadControl未就绪");
				return;
			}

			int[] keys=sets.getKeys();
			int fv=sets.getFreeValue();
			int id;

			for(int i=keys.Length - 1;i>=0;--i)
			{
				if((id=keys[i])!=fv && id>0)
				{
					unloadOne(id);
				}
			}
		}

		/** 加载一个资源() */
		public static void loadOne(int id,Action callback,int priority=-1)
		{
			if(!_ready)
			{
				Ctrl.warnLog("LoadControl未就绪");
				return;
			}

			if(id<=0)
			{
				callbackFunc(callback);
				return;
			}

			checkPackError(id);

			ResourceNodeData node;

			//计数加
			addNodeRef(node=getNode(id));

			//有资源
			if(node.hasResource())
			{
				callbackFunc(callback);
				return;
			}

			if(callback!=null)
			{
				_loadCompleteCallDic.computeIfAbsent(id,k=>new SList<Action>()).add(callback);
			}

			addOne(id,null,priority,true);
		}

		/** 卸载一个 */
		public static void unloadOne(int id)
		{
			if(!_ready)
			{
				Ctrl.warnLog("LoadControl未就绪");
				return;
			}

			if(id<=0)
				return;

			ResourceNodeData node=getNode(id);

			if(node!=null)
			{
				subNodeRef(node);
			}
			else
			{
				Ctrl.warnLog("出现unload时,找不到资源node",id);
			}
		}

		/** 添加一个(不能直接加包资源)(返回所添加的way序号,如返回-1则为未添加) */
		private static LoadOneObj addOne(int id,LoadInfoObj info,int priority=-1,bool needForever=true)
		{
			//已存在
			if(hasResource(id))
			{
				Ctrl.throwError("不应该已存在");
				return null;
			}

			LoadOneObj obj=_loadingObjDic.get(id);

			//还在
			if(obj!=null)
			{
				if(info!=null)
				{
					++info.max;
					obj.infos.add(info);
				}

				//TODO:暂时不支持单个资源修改优先级和forever

				return obj;
			}

			//创建
			obj=_objPool.getOne();
			obj.id=id;

			if(info!=null)
			{
				++info.max;
				obj.infos.add(info);
			}

			_loadingObjDic.put(id,obj);

			if(ShineSetting.localLoadWithOutBundle)
			{
				ResourceInfoData infoData=_resourceInfoDic.get(id);

				if(infoData==null)
				{
					Ctrl.throwError("不能没有资源信息:" + id);
					return null;
				}

				//场景资源
				if(infoData.type==ResourceType.Scene)
				{
					//延时完成
					laterCompleteOneObj(obj);
					return null;
				}
				//资源类
				else if(ResourceType.isAsset(infoData.type))
				{
#if UNITY_EDITOR
					string path=ShineGlobal.sourceHeadU + infoData.name;

					Object asset=AssetDatabase.LoadAssetAtPath<Object>(path);

					if(asset==null)
					{
						Ctrl.throwError("加载资源为空:" + path);
						return null;
					}

					getNode(id).asset=asset;

					laterCompleteOneObj(obj);

					return obj;
#endif

				}
			}

			int packID=_resourceElementToPackDic.get(id);

			//有包资源
			if(packID>0)
			{
				object temp;

				//包已有资源
				if((temp=getNode(packID).asset)!=null)
				{
					ResourceInfoData packInfoData=_resourceInfoDic.get(packID);

					//场景资源不解析(unity规定)
					if(packInfoData.type==ResourceType.Scene)
					{
						//延时回调
						laterCompleteOneObj(obj);
						return obj;
						// completeOneObj(obj,true);
						// return null;
					}
					else
					{
						AssetBundle ab=(AssetBundle)temp;

						obj.assetBundleRequest=ab.LoadAssetAsync(getResourceInfoByID(obj.id).getUName());

						_analysisDic.put(obj.id,obj);

						return obj;
					}
				}
				else
				{
					//加入
					toAddPack(packID,priority,needForever).objs.add(obj);
				}
			}
			else
			{
				toAddPack(id,priority,needForever).objs.add(obj);
			}

			return obj;
		}

		/** 加入一个加载包 */
		private static LoadPackObj toAddPack(int id,int priority=-1,bool needForever=true)
		{
			//已存在
			if(hasResource(id))
			{
				Ctrl.throwError("此时不该已存在");
				return null;
			}

			LoadPackObj pack;

			if((pack=_loadingPackDic.get(id))!=null)
			{
				return pack;
			}

			if(priority==-1)
			{
				priority=LoadPriorityType.Default;
			}

			if(ShineSetting.useFastLoadedResource)
			{
				if(priority>LoadPriorityType.Loaded && _loadedMarkDic.contains(id))
				{
					priority=LoadPriorityType.Loaded;
				}
			}

			//优先级更高
			if(priority<_loadPriorityStart)
			{
				_loadPriorityStart=priority;
			}

			//已存在
			if((pack=_waitLoadPackDic.get(id))!=null)
			{
				//优先级更高
				if(priority<pack.priority)
				{
					//标记删除
					pack.removed=true;

					LoadPackObj pack2=_packPool.getOne();
					pack2.id=id;

					pack2.needForever=needForever;
					pack2.priority=priority;

					//更改主
					if(!pack.depends.isEmpty())
					{
						LoadPackObj[] keys=pack.depends.getKeys();
						LoadPackObj k;

						for(int i=keys.Length-1;i>=0;--i)
						{
							if((k=keys[i])!=null)
							{
								k.beDepends.remove(pack);
								k.beDepends.add(pack2);
							}
						}
					}

					if(!pack.beDepends.isEmpty())
					{
						LoadPackObj[] keys=pack.beDepends.getKeys();
						LoadPackObj k;

						for(int i=keys.Length-1;i>=0;--i)
						{
							if((k=keys[i])!=null)
							{
								k.depends.remove(pack);
								k.depends.add(pack2);
							}
						}
					}

					//交换
					pack.swap(pack2);


					//字典
					_waitLoadPackDic[id]=pack2;

					//组
					SQueue<LoadPackObj> queue=_waitLoadPackLists[priority];

					if(queue==null)
					{
						queue=new SQueue<LoadPackObj>();
						_waitLoadPackLists[priority]=queue;
					}

					queue.offer(pack2);

					pack=pack2;
				}

				pack.needForever=needForever;
			}
			else
			{
				pack=_packPool.getOne();

				if(ShineSetting.openCheck)
				{
					if(!pack.isDependAndBeDependEmpty())
					{
						Ctrl.throwError("出严重错误,析构未干净");
					}
				}

				pack.id=id;

				pack.needForever=needForever;
				pack.priority=priority;

				//包信息
				BundleInfoData data=_resourcePackDefineDic.get(id);

				if(data!=null)
				{
					LoadPackObj dependPack;

					int[] depends=data.depends;
					int dependID;

					//依赖组
					for(int i=0,len=depends.Length;i<len;++i)
					{
						//没有此依赖包
						if(!hasResource(dependID=depends[i]))
						{
							//互相绑定
							(dependPack=toAddPack(dependID,priority,needForever)).beDepends.add(pack);
							pack.depends.add(dependPack);
						}
					}
				}

				//字典
				_waitLoadPackDic.put(id,pack);

				//组
				SQueue<LoadPackObj> queue=_waitLoadPackLists[priority];

				if(queue==null)
				{
					queue=new SQueue<LoadPackObj>();
					_waitLoadPackLists[priority]=queue;
				}

				queue.offer(pack);

				++_waitLoadNum;
			}

			//加载中

			return pack;
		}

		//--解析--//

		/** 一个包完成 */
		private static void oneComplete(LoadPackObj pack)
		{
			int id=pack.id;

			//标记加载过
			_loadedMarkDic.add(id);

			ShineLoader loader=_loaders[pack.index];

			ResourceInfoData infoData=_resourceInfoDic.get(id);

			try
			{
				switch(infoData.type)
				{
					case ResourceType.XML:
					{
						getNode(id).asset=loader.getXML();
						completeOnePack(pack,true);
					}
						break;
					case ResourceType.Bundle:
					{
						AssetBundle bundle;

						getNode(id).asset=bundle=loader.getAssetBundle();

						BundleInfoData data=_resourcePackDefineDic.get(id);

						if(data==null)
						{
							Ctrl.throwError("不应该没有包信息");
							return;
						}

						if(ShineSetting.bundleAnalysisAll)
						{
							ResourceNodeData node;

							foreach(int v in data.assets)
							{
								ResourceInfoData resourceInfoData=getResourceInfoByID(v);

								(node=getNode(v)).asset=bundle.LoadAsset(resourceInfoData.getUName());
								//加一个计数
								addNodeRef(node);
							}

							completeOnePack(pack,true);
						}
						else
						{
							SList<LoadOneObj> list=pack.objs;
							LoadOneObj obj;

							for(int i=0,len=list.Count;i<len;++i)
							{
								if((obj=list[i]).removed)
								{
									_objPool.back(obj);
								}
								else
								{
									if(obj.id!=id)
									{
										ResourceInfoData resourceInfoData=getResourceInfoByID(obj.id);

										obj.assetBundleRequest=bundle.LoadAssetAsync(resourceInfoData.getUName());

										//解析
										_analysisDic.put(obj.id,obj);
									}
									//兼容只加载包的模式
									else
									{
										completeOneObj(obj,true);
									}
								}
							}
						}

					}
						break;
					case ResourceType.Scene:
					{
						AssetBundle bundle;
						getNode(id).asset=bundle=loader.getAssetBundle();

						//加载场景必须调用代码
						bundle.GetAllScenePaths();

						completeOnePack(pack,true);
					}
						break;
					case ResourceType.Bin:
					{
						getNode(id).asset=loader.getBytes();
						completeOnePack(pack,true);
					}
						break;
				}
			}
			catch(Exception e)
			{
				Ctrl.errorLog("oneComplete出错",e);
			}
		}

		/** 执行删除一个 */
		private static void toDeleteOneNode(ResourceNodeData node)
		{
			_resourceDic.remove(node.id);

			if(!ShineSetting.localLoadWithOutBundle)
			{
				ResourceInfoData infoData=_resourceInfoDic.get(node.id);

				//加载过资源了
				if(node.asset!=null)
				{
					try
					{
						switch(infoData.type)
						{
							case ResourceType.Bundle:
							{
								if(!node.isPack())
								{
									Ctrl.throwError("不应该不是包类型");
								}

								AssetBundle bundle=(AssetBundle)node.asset;
								bundle.Unload(true);
							}
								break;
							case ResourceType.Scene:
							{
								AssetBundle bundle=(AssetBundle)node.asset;
								bundle.Unload(true);
							}
								break;
							default:
							{

							}
								break;
						}
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
				}
			}

			_nodePool.back(node);
		}

		//get组

		/** 是否有某资源(加载好的) */
		public static bool hasResource(int id)
		{
			ResourceNodeData node=_resourceDic.get(id);

			if(node==null)
				return false;

			return node.hasResource();
		}

		/** 获取资源(原始资源，没有实例化的) */
		public static object getResource(int id)
		{
			ResourceNodeData node=_resourceDic.get(id);

			if(node==null)
				return null;

			return node.asset;
		}

		/** 获取资源 */
		public static object getResource(string name)
		{
			return getResource(getResourceIDByName(name));
		}

		/** 获取sprite */
		public static Sprite getSprite(string name)
		{
			return getSprite(getResourceIDByName(name));
		}

		/** 获取sprite */
		public static Sprite getSprite(int id)
		{
			object asset=getResource(id);

			if(asset is Texture2D)
			{
				Texture2D tex=(Texture2D)asset;
				return Sprite.Create(tex,new Rect(0f,0f,tex.width,tex.height),new Vector2((float)tex.width / 2,(float)tex.height / 2));
			}
			else if(asset is Sprite)
			{
				return (Sprite)asset;
			}
			else
			{
				return null;
			}
		}

		/** 获取bundle内资源 */
		private static UnityEngine.Object getBundleAsset(int id)
		{
			return (UnityEngine.Object)getResource(id);
		}

		/** 获取xml */
		public static XML getXML(string url)
		{
			return getXML(getResourceIDByName(url));
		}

		/** 获取xml */
		public static XML getXML(int id)
		{
			return (XML)getResource(id);
		}

		/** 获取包内资源(未实例化的) */
		public static T getUnityObjectByType<T>(int id) where T:UnityEngine.Object
		{
			return (T)getResource(id);
		}

		/** 获取包内资源(未实例化的) */
		public static T getUnityObjectByType<T>(string name) where T:UnityEngine.Object
		{
			return (T)getResource(getResourceIDByName(name));
		}

		/** 获取实例实例化的GameObject */
		public static GameObject getAsset(int id)
		{
			UnityEngine.Object bundleAsset;

			if((bundleAsset=getBundleAsset(id))==null)
				return null;

			return (GameObject)UnityEngine.Object.Instantiate(bundleAsset);
		}

		/** 获取实例化的GameObject */
		public static GameObject getAsset(string name)
		{
			UnityEngine.Object bundleAsset;

			if((bundleAsset=getBundleAsset(getResourceIDByName(name)))==null)
				return null;

			return (GameObject)UnityEngine.Object.Instantiate(bundleAsset);
		}
	}
}