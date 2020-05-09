using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// 文本框
	/// </summary>
	public class UIText:UIObject
	{
		private Text _text;

		public UIText()
		{
			_type=UIElementType.Text;
		}

		public Text text
		{
			get {return _text;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_text=gameObject.GetComponent<Text>();
		}

		public void setString(string text)
		{
			_text.text=text;
		}

		public void setText(string text)
		{
			_text.text=text;
		}
	}
}