using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	public class FrameAnimation:MonoBehaviour
	{
		public Sprite[] sprites=new Sprite[0];

		/** 参与渲染的子对象 */
		[SerializeField]
		GameObject targetGraphic;

		/** 渲染对象的图片 */
		private Image _image;

		/** 渲染对象的位移 */
		private RectTransform _imageTransform;

		public bool refreshTargetGraphic()
		{
			if(!targetGraphic)
				return false;

			Image image=targetGraphic.GetComponent<Image>();
			RectTransform rectTransform=targetGraphic.GetComponent<RectTransform>();
			if(image==null || rectTransform==null)
				return false;

			return false;
		}
	}
}