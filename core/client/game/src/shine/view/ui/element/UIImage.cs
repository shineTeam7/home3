using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// 
	/// </summary>
	public class UIImage:UIObject
	{
		private Image _image;

		public UIImage()
		{
			_type=UIElementType.Image;
		}

		public Image image
		{
			get {return _image;}
		}

		/** 设置透明度 */
		public void setAlpha(float v)
		{
			Color color=_image.color;
			color.a=v;
			_image.color=color;
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_image=gameObject.GetComponent<Image>();
		}
		
		protected override void dispose()
		{
			base.dispose();
			
			_image = null;
		}

		public void setSprite(Sprite sprite)
		{
			_image.sprite = sprite;
		}

		/// <summary>
		/// 点击响应
		/// </summary>
		public Action click
		{
			set {getPointer().onClick=value;}
		}
	}
}