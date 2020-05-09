using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{

	/// <summary>
	/// 多帧图片容器
	/// </summary>
	[RequireComponent(typeof(Image))]
	public class ImageFrameContainer:MonoBehaviour
	{
		public Sprite[] sprites=new Sprite[0];

		private Image _image;

		[SerializeField]
		private int _curFrame;

		public Image image
		{
			get {return _image;}
		}

		public int frame
		{
			set {frameTo(value);}
			get {return _curFrame;}
		}

		public int getFrameTotal()
		{
			return sprites.Length;
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

		public void frameTo(int index)
		{
			int len=sprites.Length;
			if(len==0)
				return;

			if(index>len - 1)
				index=len - 1;
			if(index<0)
				index=0;

			_curFrame=index;

			if(!gameObject.activeInHierarchy)
				return;

			_image.sprite=sprites[index];
		}
	}
}