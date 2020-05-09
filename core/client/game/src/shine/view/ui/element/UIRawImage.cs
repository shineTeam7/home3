using System;
using UnityEngine;
using UnityEngine.UI;
namespace ShineEngine
{
	/// <summary>
	/// 
	/// </summary>
	public class UIRawImage:UIObject
	{
        private RawImage _rawImage;

		public UIRawImage()
		{
			_type=UIElementType.RawImage;
		}

        public RawImage rawImage
        {
            get {
                return _rawImage;
            }
        }

        public override void init(GameObject obj)
        {
            base.init(obj);
            this._rawImage = gameObject.GetComponent<RawImage>();
        }

        protected override void dispose()
        {
            base.dispose();
            this._rawImage = null;
        }
    }
}