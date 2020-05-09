using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	public class UIRawImageLoader:UIObject
	{
		private RawImageLoader _imageLoader;

		public UIRawImageLoader()
		{
			_type=UIElementType.RawImageLoader;
		}

		public RawImageLoader imageLoader
		{
			get {return _imageLoader;}
		}

		public RawImage image
		{
			get {return _imageLoader.image;}
		}

		public Texture sprite
		{
			get {return _imageLoader.texture;}
			set {_imageLoader.texture=value;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_imageLoader=gameObject.GetComponent<RawImageLoader>();
			_imageLoader.init();
		}

		public void load(int id)
		{
			_imageLoader.load(id);
		}

		public void load(string path)
		{
			_imageLoader.load(path);
		}

		public void setOverFunc(Action overFunc)
		{
			_imageLoader.setOverFunc(overFunc);
		}

		/** 置空 */
		public void setEmpty()
		{
			_imageLoader.setEmpty();
		}
	}
}