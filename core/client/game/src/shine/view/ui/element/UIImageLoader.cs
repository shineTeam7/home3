using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	public class UIImageLoader:UIObject
	{
		private ImageLoader _imageLoader;

		public UIImageLoader()
		{
			_type=UIElementType.ImageLoader;
		}

		public ImageLoader imageLoader
		{
			get {return _imageLoader;}
		}

		public Image image
		{
			get {return _imageLoader.image;}
		}

		public Sprite sprite
		{
			get {return _imageLoader.sprite;}
			set {_imageLoader.sprite=value;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_imageLoader=gameObject.GetComponent<ImageLoader>();
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