using System;
using UnityEngine;
using UnityEngine.EventSystems;

namespace ShineEngine
{
	/// <summary>
	/// 翻页容器
	/// </summary>
	public class UISPageView<T>:UIGridContainer<T>where T:UIObject,new()
	{
		private SPageView _pageView;

		public UISPageView()
		{
			_type=UIElementType.SPageView;
		}

		public SPageView pageView
		{
			get {return _pageView;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_pageView=obj.GetComponent<SPageView>();
			_pageView.init();

			_gridList=new T[_pageView.gridList.Length];
			_gridIndexMap=new SMap<UIObject,int>();
			for(int i=0;i<_gridList.Length;++i)
			{
				_gridList[i]=createOne();
				_gridList[i].init(_pageView.gridList[i].gameObject);
				_gridList[i].setParent(this);
				_gridList[i].setIsGrid(true);

				_gridIndexMap[_gridList[i]]=i;
			}

			_pageView.setTouchDownAction(onTouchDown);
			_pageView.setTouchUpAction(onTouchUp);
			_pageView.setTouchCancelAction(onTouchCanceled);
		}

		public int page
		{
			get {return _pageView.page;}
		}

		public int getGridDataIndex(int index)
		{
			return _pageView.getGridDataIndex(index);
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

		public object getGridData(int index)
		{
			return _pageView.getGridData(index);
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
			_pageView.setDataList(dataList);
		}

		public void setDataAction(Action<int,int> action)
		{
			_pageView.setDataAction(action);
		}

		public void refreshGridDatas()
		{
			_pageView.refreshGridData();
		}

		public void resetGridDatas()
		{
			_pageView.resetGridData();
		}

		public void resetToInitPos()
		{
			_pageView.resetToInitPos();
		}

		public void setNextPage(bool immediately=false)
		{
			_pageView.setNextPage(immediately);
		}

		public void setPrevPage(bool immediately=false)
		{
			_pageView.setPrevPage(immediately);
		}

		public void setChangePageCallBack(Action<int> action)
		{
			_pageView.setChangePageCallBack(action);
		}

		public void setCheckTouchAction(Func<int,PointerEventData,bool> action)
		{
			_pageView.setCheckGridTouchAction(action);
		}

		public void setGridTouchDownAction(Action<int> action)
		{
			_pageView.setGridTouchDownAction(action);
		}

		public void setGridTouchUpAction(Action<int> action)
		{
			_pageView.setGridTouchUpAction(action);
		}

		public void setGridTouchCancleAction(Action<int> action)
		{
			_pageView.setGridTouchCancelAction(action);
		}
	}
}