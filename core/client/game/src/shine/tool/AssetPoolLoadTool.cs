using System;
using UnityEngine;

namespace ShineEngine
{
	/** 池化资源加载工具(Destroy的调用，由池代劳，业务层无需调用) */
	public class AssetPoolLoadTool
	{
		/** 析构回调 */
		private Action<GameObject> _releaseCall;
		/** 完成回调 */
		private Action<GameObject> _completeCall;
		/** 池类型 */
		private int _type;
		/** loadControl的版本 */
		private int _loadVersion;
		/** 加载序号 */
		private int _index;

		/** 资源ID */
		private int _resourceID=-1;

		private bool _isLoading=false;

		private int _cacheResourceID=-1;
		private GameObject _cacheObject;

		public AssetPoolLoadTool(int type)
		{
			_type=type;
		}

		public AssetPoolLoadTool(int type,Action<GameObject> completeCall,Action<GameObject> releaseCall)
		{
			_type=type;
			_releaseCall=releaseCall;
			_completeCall=completeCall;
		}

		/** 清除占用(会析构GameObject) */
		public void clear()
		{
			toClear(true);
		}

		private void toClear(bool needCache)
		{
			_isLoading=false;

			if(_resourceID!=-1)
			{
				++_index;

				_resourceID=-1;

				//版本还对
				if(LoadControl.getVersion()==_loadVersion)
				{
					if(needCache)
					{
						releaseCache();
					}
				}
				else
				{
					_cacheResourceID=-1;
					_cacheObject=null;
				}
			}
		}

		private void releaseCache()
		{
			if(_cacheResourceID!=-1)
			{
				if(_cacheObject!=null && _releaseCall!=null)
					_releaseCall(_cacheObject);

				AssetPoolControl.unloadOne(_type,_cacheResourceID,_cacheObject);
				_cacheObject=null;
				_cacheResourceID=-1;
			}
		}

		/** 获取此时资源ID */
		public int getResourceID()
		{
			return _resourceID;
		}

		public bool isLoading()
		{
			return _isLoading;
		}

		/** 加载一个 */
		public void loadOne(int id)
		{
			//-1不加载
			if(id==-1)
			{
				clear();

				_isLoading=false;
				//不返回
				return;
			}

			//相同资源,加载中跳过
			if(_isLoading && _resourceID==id)
				return;

			toClear(false);

			_loadVersion=LoadControl.getVersion();
			int index=++_index;
			_isLoading=true;

			AssetPoolControl.loadOne(_type,_resourceID=id,()=>
			{
				if(_index==index && LoadControl.getVersion()==_loadVersion)
				{
					_isLoading=false;

					releaseCache();

					_cacheResourceID=id;
					_cacheObject=AssetPoolControl.getAsset(_type,id);

					if(_cacheObject==null)
					{
						Ctrl.throwError("获取Asset为空,可能是业务层调用了Destroy",_type,id);
					}
					else
					{
						if(_completeCall!=null)
							_completeCall(_cacheObject);
					}
				}
			});
		}

		/** 加载一个(带回调) */
		public void loadOne(int id,Action<GameObject> func)
		{
			_completeCall=func;

			loadOne(id);
		}
	}
}