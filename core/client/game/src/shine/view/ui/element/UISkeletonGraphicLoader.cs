using System;
using Spine.Unity;
using UnityEngine;

namespace ShineEngine
{
	public class UISkeletonGraphicLoader:UIObject
	{
		private SkeletonGraphicLoader _skeletonGraphicLoader;

		public UISkeletonGraphicLoader()
		{
			_type=UIElementType.SkeletonGraphicLoader;
		}

		public SkeletonGraphicLoader skeletonGraphicLoader
		{
			get {return _skeletonGraphicLoader;}
		}

		public SkeletonGraphic skeletonGraphic
		{
			get {return _skeletonGraphicLoader.skeletonGraphic;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_skeletonGraphicLoader=gameObject.GetComponent<SkeletonGraphicLoader>();
			_skeletonGraphicLoader.init();
		}

		public void load(int id)
		{
			_skeletonGraphicLoader.load(id);
		}

		public void load(string path)
		{
			_skeletonGraphicLoader.load(path);
		}

		public void setOverFunc(Action overFunc)
		{
			_skeletonGraphicLoader.setOverFunc(overFunc);
		}
	}
}