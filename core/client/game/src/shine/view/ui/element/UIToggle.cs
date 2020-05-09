using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// 选择按钮
	/// </summary>
	public class UIToggle:UIObject
	{
		private Toggle _toggle;

		private Text _text;

		public UIToggle()
		{
			_type=UIElementType.Toggle;
		}

		public Toggle toggle
		{
			get {return _toggle;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_toggle=gameObject.GetComponent<Toggle>();

			Transform label;
			if((label=gameObject.transform.Find("Label"))!=null)
			{
				_text=label.GetComponent<Text>();
			}
		}

		public bool isOn
		{
			get {return _toggle.isOn;}
			set {_toggle.isOn=value;}
		}

		public Toggle.ToggleEvent onValueChanged
		{
			get {return _toggle.onValueChanged;}
		}

		/// <summary>
		/// 设置显示文字
		/// </summary>
		public string text
		{
			get
			{
				if(_text!=null)
					return _text.text;

				return "";
			}
			set
			{
				if(_text!=null)
					_text.text=value;
			}
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