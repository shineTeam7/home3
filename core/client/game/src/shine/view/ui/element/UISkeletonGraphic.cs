using Spine.Unity;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 骨骼动画
	/// </summary>
	public class UISkeletonGraphic:UIObject
	{
		private SkeletonGraphic _skeleton;

		public UISkeletonGraphic()
		{
			_type=UIElementType.SkeletonGraphic;
		}

		public SkeletonGraphic skeleton
		{
			get
			{
				return _skeleton;
			}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_skeleton = obj.GetComponent<SkeletonGraphic>();
		}
	}
}