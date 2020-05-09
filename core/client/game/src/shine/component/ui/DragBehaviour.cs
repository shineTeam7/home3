using System;
using UnityEngine;
using UnityEngine.EventSystems;

namespace ShineEngine
{

	/// <summary>
	/// 拖拽行为
	/// </summary>
	public class DragBehaviour:MonoBehaviour, IDragHandler, IBeginDragHandler, IEndDragHandler
	{
		public Action<Vector2, Vector2> onDrag;

		public Action<PointerEventData> onTouchDrag;

		public Action<Vector2> onDragStart;

		public Action<PointerEventData> onTouchDragStart;

		public Action<Vector2> onDragEnd;

		public Action<PointerEventData> onTouchDragEnd;

		public DragBehaviour()
		{
		}

		public void OnDrag(PointerEventData eventData)
		{
			if(onDrag != null)
				onDrag(eventData.delta, eventData.position);
			if(onTouchDrag != null)
				onTouchDrag(eventData);
		}

		public void OnBeginDrag(PointerEventData eventData)
		{
			if(onDragStart != null)
				onDragStart(eventData.position);
			if(onTouchDragStart != null)
				onTouchDragStart(eventData);
		}

		public void OnEndDrag(PointerEventData eventData)
		{
			if(onDragEnd != null)
				onDragEnd(eventData.position);
			if(onTouchDragEnd != null)
				onTouchDragEnd(eventData);
		}
	}
}