using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// 
	/// </summary>
	[RequireComponent(typeof(Image))]
	public class BloodBar:MonoBehaviour
	{

		[SerializeField]
		Sprite green;

		[SerializeField]
		Sprite yellow;

		[SerializeField]
		Sprite red;

		[Range(0f,1f),SerializeField]
		private float _progress=1f;

		private Image _image;

		public Image image
		{
			get {return _image;}
		}

		public float progress
		{
			set {setProgress(value);}
			get {return _progress;}
		}

		private void OnEnable()
		{
			_image=gameObject.GetComponent<Image>();
		}

#if UNITY_EDITOR
		private void OnValidate()
		{
			_image=gameObject.GetComponent<Image>();
		}
#endif

		public void setProgress(float progress)
		{
			if(!green || !yellow || !red)
				return;

			if(progress<0)
				progress=0;
			if(progress>1)
				progress=1;

			_progress=progress;

			if(_progress>=0.6)
			{
				_image.sprite=green;
			}
			else if(_progress>=0.2)
			{
				_image.sprite=yellow;
			}
			else
			{
				_image.sprite=red;
			}
			_image.fillAmount=_progress;
		}
	}
}