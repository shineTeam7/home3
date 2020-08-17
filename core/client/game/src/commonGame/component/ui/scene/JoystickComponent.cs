using UnityEngine;
using UnityEngine.EventSystems;

namespace ShineEngine
{
	public class JoystickComponent:MonoBehaviour,IPointerDownHandler, IPointerUpHandler, IBeginDragHandler, IDragHandler
	{
		/** 摇杆逻辑 */
		public JoystickLogic logic;

		public void OnPointerDown(PointerEventData eventData)
		{
			if(logic!=null)
				logic.OnPointerDown(eventData);
		}

		public void OnPointerUp(PointerEventData eventData)
		{
			if(logic!=null)
				logic.OnPointerUp(eventData);
		}

		public void OnBeginDrag(PointerEventData eventData)
		{
			if(logic!=null)
				logic.OnBeginDrag(eventData);
		}

		public void OnDrag(PointerEventData eventData)
		{

		}
	}
}