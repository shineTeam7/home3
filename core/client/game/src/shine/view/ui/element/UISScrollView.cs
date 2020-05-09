using System;
using UnityEngine;
using UnityEngine.EventSystems;

namespace ShineEngine
{
	/// <summary>
	/// 滑动容器
	/// </summary>
	public class UISScrollView<T>:UIGridContainer<T>where T:UIObject,new()
	{
		private Action<int,int> _setDataIndexAction;

		private Action<T,object> _setDataAction;
		private Action<T,int> _setDataActionWithIndex;

		private SScrollView _scrollView;

		public UISScrollView()
		{
			_type=UIElementType.SScrollView;
		}

		public SScrollView scrollView
		{
			get {return _scrollView;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_scrollView=obj.GetComponent<SScrollView>();
			_scrollView.init();

			_gridList=new T[_scrollView.gridList.Length];
			_gridIndexMap=new SMap<UIObject,int>();
			for(int i=0;i<_gridList.Length;++i)
			{
				UIObject uObj=_gridList[i]=createOne();
				uObj.init(_scrollView.gridList[i].gameObject);
				uObj.name="g" + i;
				uObj.setParent(this);
				uObj.setIsGrid(true);

				_gridIndexMap[uObj]=i;
			}

			_scrollView.setTouchDownAction(onTouchDown);
			_scrollView.setTouchUpAction(onTouchUp);
			_scrollView.setTouchCancelAction(onTouchCanceled);
			_scrollView.setDataAction(onSetDataAction);
		}

		/// <summary>
		/// 根据当前格子索引获取格子数据索引
		/// </summary>
		public int getGridDataIndex(int index)
		{
			return _scrollView.getGridDataIndex(index);
		}

		/// <summary>
		/// 根据当前格子对象获取格子数据索引
		/// </summary>
		public int getGridDataIndexByGrid(UIObject grid)
		{
			if(_gridIndexMap.contains(grid))
				return getGridDataIndex(_gridIndexMap.get(grid));

			return -1;
		}

		/// <summary>
		/// 根据当前格子索引获取格子数据
		/// </summary>
		public object getGridData(int index)
		{
			return _scrollView.getGridData(index);
		}

		/// <summary>
		/// 根据当前格子对象获取格子数据
		/// </summary>
		public object getGridDataByGrid(UIObject grid)
		{
			if(_gridIndexMap.contains(grid))
				return getGridData(_gridIndexMap.get(grid));

			return null;
		}

		public void setDataList<T>(SList<T> dataList)
		{
			_scrollView.setDataList(dataList);
		}

		public void setDataAction(Action<int,int> action)
		{
			_setDataIndexAction=action;
		}

		public void setDataAction(Action<T,object> action)
		{
			_setDataAction=action;
		}

		public void setDataActionWithIndex(Action<T,int> action)
		{
			_setDataActionWithIndex=action;
		}

		public void setProgressChangeAction(Action<float,float> action)
		{
			_scrollView.setProgressChangeAction(action);
		}

		public void setScrollProgress(float progress,bool needCallback=false)
		{
			_scrollView.setScrollProgress(progress,needCallback);
		}

		public void refreshGridDatas()
		{
			_scrollView.refreshGridData();
		}

		public void resetGridDatas()
		{
			_scrollView.resetGridData();
		}

		public void resetToInitPos()
		{
			_scrollView.resetToInitPos();
		}

		public void setPageNum(int pageNum, Action<int> callBack)
		{
			_scrollView.setPageNum(pageNum, callBack);
		}

		public void setCheckGridTouchAction(Func<int,PointerEventData,bool> action)
		{
			_scrollView.setCheckGridTouchAction(action);
		}

		public void setGridTouchDownAction(Action<int> action)
		{
			_scrollView.setGridTouchDownAction(action);
		}

		public void setGridTouchUpAction(Action<int> action)
		{
			_scrollView.setGridTouchUpAction(action);
		}

		public void setGridTouchCancleAction(Action<int> action)
		{
			_scrollView.setGridTouchCancelAction(action);
		}

		private void onSetDataAction(int index,int dataIndex)
		{
			if(_setDataAction!=null)
				_setDataAction(getGrid(index),getGridData(index));
			else if(_setDataActionWithIndex!=null)
				_setDataActionWithIndex(getGrid(index),dataIndex);
			else if(_setDataIndexAction!=null)
				_setDataIndexAction(index,dataIndex);
		}
	}
}