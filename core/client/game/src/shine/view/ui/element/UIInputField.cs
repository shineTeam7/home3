using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// 输入文本框
	/// </summary>
	public class UIInputField:UIObject
	{
		private InputField _inputField;

		public UIInputField()
		{
			_type=UIElementType.InputField;
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_inputField=gameObject.GetComponent<InputField>();
		}

		public InputField inputField
		{
			get {return _inputField;}
		}

		public void setString(string text)
		{
			_inputField.text=text;
		}

		public string getString()
		{
			return _inputField.text;
		}
	}
}