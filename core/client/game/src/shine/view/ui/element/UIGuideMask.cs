using UnityEngine;

namespace ShineEngine
{
	public class UIGuideMask:UIObject
	{
		private GuideMask _guideMask;

		public UIGuideMask()
		{
			_type=UIElementType.GuideMask;
		}

		public GuideMask guideMask
		{
			get {return _guideMask;}
		}

		public RectTransform hollow
		{
			get {return _guideMask.hollow;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_guideMask=gameObject.GetComponent<GuideMask>();
		}
	}
}