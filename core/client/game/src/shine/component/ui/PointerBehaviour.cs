using System;
using UnityEngine;
using UnityEngine.EventSystems;

namespace ShineEngine
{

	/// <summary>
	/// 点击(mouse/touch)行为
	/// </summary>
	public class PointerBehaviour:MonoBehaviour,IPointerClickHandler,IPointerDownHandler,IPointerEnterHandler,IPointerExitHandler,IPointerUpHandler
	{
		public Action onClick;

		public Action<Vector2> onClickPos;

		public Action<PointerEventData> onTouchClick;

		public Action onDown;

		public Action<PointerEventData> onTouchDown;

		public Action onUp;

		public Action<PointerEventData> onTouchUp;

		public Action onEnter;

		public Action<PointerEventData> onTouchEnter;

		public Action onExit;

		public Action<PointerEventData> onTouchExit;

		public Action onSelect;

		public Action onUpdateSelect;

		/** 按下格子容器中某格子的按钮回调（只对格子容器有效） */
		public Action<int> onGridDown;

		/** 点击格子容器中某格子的按钮（只对格子容器有效） */
		public Action<int> onGridClick;

		/** 是否是容器内控件 */
		private bool isViewGrid;

		public PointerBehaviour()
		{
		}

		public void OnPointerClick(PointerEventData eventData)
		{
			if(onClick!=null)
				onClick();

			if(onClickPos!=null)
				onClickPos(eventData.position);

			if(onTouchClick!=null)
				onTouchClick(eventData);
		}

		public void OnPointerDown(PointerEventData eventData)
		{
			if(onDown!=null)
				onDown();

			if(onTouchDown!=null)
				onTouchDown(eventData);
		}

		public void OnPointerUp(PointerEventData eventData)
		{
			if(onUp!=null)
				onUp();

			if(onTouchUp!=null)
				onTouchUp(eventData);
		}

		public void OnPointerEnter(PointerEventData eventData)
		{
			if(onEnter!=null)
				onEnter();

			if(onTouchEnter!=null)
				onTouchEnter(eventData);
		}

		public void OnPointerExit(PointerEventData eventData)
		{
			if(onExit!=null)
				onExit();

			if(onTouchExit!=null)
				onTouchExit(eventData);
		}

		public void OnSelect(BaseEventData eventData)
		{
			if(onSelect!=null)
				onSelect();
		}

		public void OnUpdateSelected(BaseEventData eventData)
		{
			if(onUpdateSelect!=null)
				onUpdateSelect();
		}

		public void OnTouchGridDown(int index)
		{
			if(onGridDown!=null)
				onGridDown(index);
		}

		public void OnTouchGridClick(int index)
		{
			if(onGridClick!=null)
				onGridClick(index);
		}
	}
}