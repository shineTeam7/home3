using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	public class UIDropdown:UIObject
	{
		private Dropdown _dropdown;

		public UIDropdown()
		{
			_type=UIElementType.Dropdown;
		}

		public Dropdown dropdown
		{
			get {return _dropdown;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_dropdown=obj.GetComponent<Dropdown>();
		}

		public int value
		{
			get {return _dropdown.value;}
			set {_dropdown.value=value;}
		}

		public List<Dropdown.OptionData> options
		{
			get {return _dropdown.options;}
			set {_dropdown.options=value;}
		}
	}
}