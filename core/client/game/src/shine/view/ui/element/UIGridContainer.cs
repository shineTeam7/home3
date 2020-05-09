using System;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

namespace ShineEngine
{
	/** 格子容器 */
	public class UIGridContainer<T>:UIGenericityContainer<T>,IGridContainer where T:UIObject,new()
	{
		/** 格子列表 */
		protected T[] _gridList;

		/** 格子对象索引存储 */
		protected SMap<UIObject,int> _gridIndexMap;

		private SMap<UIObject,int> _clickObjectMap=new SMap<UIObject,int>();

		/** 反查位置 */
		private SMap<UIObject,long> _clickObjectIndexMap=new SMap<UIObject,long>();

		/// <summary>
		/// 增加格子容器按钮点击回调
		/// </summary>
		/// <param name="obj">按钮对象</param>
		/// <param name="gridIndex">所属格子索引</param>
		public void setClickObject(UIObject obj,int gridIndex)
		{
			Transform trans=obj.transform;

			long re=0;
			int wei=0;

			while(trans!=null && trans!=this.transform)
			{
				int index=trans.GetSiblingIndex();
				re= (re<<8) | index;
				wei+=8;

				trans=trans.parent;
			}

			if(wei>=63)
			{
				Ctrl.errorLog("grid层级过于多了",wei,obj.name);
				wei=63;
			}

			re<<=(63 - wei);

			_clickObjectIndexMap.put(obj,re);
			_clickObjectMap.put(obj,gridIndex);
		}

		/// <summary>
		/// 移除格子容器按钮点击回调
		/// </summary>
		/// <param name="obj"></param>
		public void removeClickObject(UIObject obj)
		{
			_clickObjectMap.remove(obj);
			_clickObjectIndexMap.remove(obj);
		}

		/// <summary>
		/// 清除所有格子容器按钮点击回调
		/// </summary>
		public void clearClickObjects()
		{
			_clickObjectMap.clear();
			_clickObjectIndexMap.clear();
		}

		/// <summary>
		/// 根据格子索引获取格子对象
		/// </summary>
		public T getGrid(int index)
		{
			if(_gridList.Length>index)
				return _gridList[index];

			return default(T);
		}

		/// <summary>
		/// 获取格子对象列表长度
		/// </summary>
		public int gridSize
		{
			get {return _gridList.Length;}
		}

		/// <summary>
		/// 遍历格子操作
		/// </summary>
		/// <param name="action"></param>
		public void gridForEach(Action<T> action)
		{
			for(int i=0;i<_gridList.Length;i++)
			{
				action(_gridList[i]);
			}
		}

		/// <summary>
		/// 根据格子对象获取格子索引
		/// </summary>
		public int getGridIndex(UIObject grid)
		{
			if(_gridIndexMap.contains(grid))
				return _gridIndexMap.get(grid);

			return -1;
		}

		protected void onTouchDown(PointerEventData eventData)
		{
			UIObject tObj=null;
			long tKey=0L;
			int tIndex=-1;

			_clickObjectMap.forEach((obj,index)=>
			{
				if(checkTouchObject(obj,eventData))
				{
					long t=_clickObjectIndexMap.get(obj);

					if(tObj==null || t>tKey)
					{
						tObj=obj;
						tKey=t;
						tIndex=index;
					}
				}
			});

			if (tObj!=null)
			{
				setObjectTouchDown(tObj,eventData,tIndex);
			}
		}

		protected void onTouchUp(PointerEventData eventData)
		{
			UIObject tObj=null;
			long tKey=0L;
			int tIndex=-1;

			_clickObjectMap.forEach((obj,index)=>
			{
				if(checkTouchObject(obj,eventData))
				{
					long t=_clickObjectIndexMap.get(obj);

					if(tObj==null || t>tKey)
					{
						tObj=obj;
						tKey=t;
						tIndex=index;
					}
				}
			});
			
			if (tObj!=null)
			{
				setObjectTouchUp(tObj,eventData,tIndex);
			}
		}

		protected void onTouchCanceled(PointerEventData eventData)
		{
			_clickObjectMap.forEach((obj,index)=>{ setObjectTouchCancel(obj,eventData); });
		}

		protected bool checkTouchObject(UIObject obj,PointerEventData eventData)
		{
			if(obj.gameObject.activeInHierarchy && RectTransformUtility.RectangleContainsScreenPoint(obj.transform,eventData.position,eventData.pressEventCamera))
			{
				return true;
			}

			return false;
		}

		protected void setObjectTouchDown(UIObject obj,PointerEventData eventData,int gridIndex)
		{
			if(obj.type==UIElementType.Button)
			{
				Button bt=((UIButton)obj).button;

				bt.OnPointerEnter(eventData);
				bt.OnPointerDown(eventData);
			}

			obj.getPointer().OnPointerDown(eventData);
			obj.getPointer().OnTouchGridDown(gridIndex);
		}

		protected void setObjectTouchUp(UIObject obj,PointerEventData eventData,int gridIndex)
		{
			if(obj.type==UIElementType.Button)
			{
				Button bt=((UIButton)obj).button;

				bt.OnPointerUp(eventData);
				bt.OnPointerExit(eventData);
			}

			obj.getPointer().OnPointerClick(eventData);
			obj.getPointer().OnTouchGridClick(gridIndex);
			obj.getPointer().OnPointerUp(eventData);
		}

		protected void setObjectTouchCancel(UIObject obj,PointerEventData eventData)
		{
			if(obj.type==UIElementType.Button)
			{
				Button bt=((UIButton)obj).button;

				bt.OnPointerUp(eventData);
				bt.OnPointerExit(eventData);
			}

			obj.getPointer().OnPointerExit(eventData);
		}
	}
}