using System;
using Spine.Unity;
using UnityEngine;

namespace ShineEngine
{

	[RequireComponent(typeof(SkeletonGraphic))]
	public class SkeletonGraphicLoader:MonoBehaviour
	{
		private string _source;

		[SerializeField]
		private LoadingShowType _loadingType=LoadingShowType.Hide;

		private LoadTool _loadTool;

		private SkeletonGraphic _skeletonGraphic;

		private bool _init;

		private Action _overFunc;

		public SkeletonGraphic skeletonGraphic
		{
			get {return _skeletonGraphic;}
		}

		public string source
		{
			get {return _source;}
			set {_source=value;}
		}

		public void load(int id)
		{
			_loadTool.loadOne(id);
		}

		public void load(string path)
		{
			load(LoadControl.getResourceIDByName(path));
		}

		public void setOverFunc(Action overFunc)
		{
			_overFunc=overFunc;
		}

		public void setLoading(bool isLoading)
		{
			switch(_loadingType)
			{
				case LoadingShowType.Hide:
				{
					if(isLoading)
						_skeletonGraphic.enabled=false;
					else
						_skeletonGraphic.enabled=true;
				}
					break;
				case LoadingShowType.None:
				{
				}
					break;
			}
		}

		private void OnEnable()
		{
			init();
		}

		private void OnDisable()
		{
			_loadTool.clear();
		}

		private void OnDestroy()
		{
			_loadTool.clear();
		}

		public void init()
		{
			if(_init)
				return;

			_skeletonGraphic=gameObject.GetComponent<SkeletonGraphic>();
			setLoading(true);

			_loadTool=new LoadTool(onLoadOver);

			if(!string.IsNullOrEmpty(_source))
				load(_source);

			_init=true;
		}

		private void onLoadOver()
		{
			SkeletonDataAsset asset=LoadControl.getUnityObjectByType<SkeletonDataAsset>(_loadTool.getResourceID());
			if(asset!=null)
			{
				setLoading(false);

				_skeletonGraphic.skeletonDataAsset=asset;
				_skeletonGraphic.startingAnimation="";
				_skeletonGraphic.Initialize(true);
			}

			if(_overFunc!=null)
				_overFunc();
		}
	}
}