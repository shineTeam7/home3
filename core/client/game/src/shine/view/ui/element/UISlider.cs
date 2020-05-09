using System.Runtime.InteropServices;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// 滑块条
	/// </summary>
	public class UISlider:UIObject
	{
		private Slider _slider;

		public UISlider()
		{
			_type=UIElementType.Slider;
		}

		public Slider slider
		{
			get {return _slider;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_slider = gameObject.GetComponent<Slider>();
		}
		
		public Slider.SliderEvent onValueChanged
		{
			get
			{
				return _slider.onValueChanged;
			}
			set
			{
				_slider.onValueChanged = value;
			}
		}
		
		/// <summary>
		///   <para>The current value of the slider.</para>
		/// </summary>
		public virtual float value
		{
			get {return _slider.value;}
			set {_slider.value=value;}
		}
	}
}