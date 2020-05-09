using System;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 血条
	/// </summary>
	public class UIBloodBar:UIObject
	{
		private BloodBar _bloodBar;

		public UIBloodBar()
		{
			_type=UIElementType.BloodBar;
		}

		public BloodBar bloodBar
		{
			get {return _bloodBar;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_bloodBar=obj.GetComponent<BloodBar>();
		}

		/// <summary>
		/// 设置进度
		/// </summary>
		public float progress
		{
			get {return _bloodBar.progress;}
			set {_bloodBar.progress=value;}
		}
	}
}