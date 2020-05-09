using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// UI显示对象
	/// </summary>
	public class UIObject
	{
		/** 对象名(所在父级的访问名) */
		public string name;

		/** UI元素类型(见UIElementType) */
		protected int _type;

		private GameObject _gameObject;

		private RectTransform _transform;

		private PointerBehaviour _pointer;

		private DragBehaviour _drag;

		private UIObject _parent;

		/** 是否是容器内部Grid */
		private bool _isGrid;

		public UIObject()
		{
			_type=UIElementType.None;
		}

		public virtual void init(GameObject obj)
		{
			_transform=(_gameObject=obj).GetComponent<RectTransform>();
		}

		/** 析构 */
		public void doDispose()
		{
			dispose();

			_gameObject=null;
			_transform=null;
		}

		protected virtual void dispose()
		{

		}

		public int type
		{
			get {return _type;}
		}

		public GameObject gameObject
		{
			get {return _gameObject;}
		}

		public RectTransform transform
		{
			get{return _transform;}
		}

		public UIObject parent
		{
			get {return _parent;}
		}

		/// <summary>
		/// 设置是否为容器内部Grid
		/// </summary>
		public void setIsGrid(bool isGrid)
		{
			_isGrid=isGrid;
		}

		public bool isGrid
		{
			get {return _isGrid;}
		}

		/// <summary>
		/// 设置父控件，逻辑层禁用！！！
		/// </summary>
		public void setParent(UIObject theParent)
		{
			_parent=theParent;
		}

		/// <summary>
		/// 绑定格子
		/// </summary>
		public void bindGrid()
		{
			UIObject grid = ViewUtils.getRootGrid(this);

			if(grid!=null)
			{
				IGridContainer gridContainer=(IGridContainer)grid.parent;

				gridContainer.setClickObject(this, gridContainer.getGridIndex(grid));
			}
		}

		/// <summary>
		/// 设置是否显示
		/// </summary>
		public void setActive(bool isActive)
		{
			if (_gameObject!=null)
				_gameObject.SetActive(isActive);
		}



		/// <summary>
		/// 指针行为
		/// </summary>
		public PointerBehaviour getPointer()
		{
			if(_pointer==null)
			{
                _pointer= _gameObject.GetComponent<PointerBehaviour>();
                
                if(_pointer==null)
                   _pointer =_gameObject.AddComponent<PointerBehaviour>();
			}

			return _pointer;
		}

		/// <summary>
		/// 拖拽行为
		/// </summary>
		public DragBehaviour getDrag()
		{
			if(_drag==null)
			{
				_drag=_gameObject.GetComponent<DragBehaviour>();
				
				if(_drag==null)
					_drag=_gameObject.AddComponent<DragBehaviour>();
			}

			return _drag;
		}

		/// <summary>
		/// 格子内部点击相应，参数为格子索引（只适用于格子容器中格子内部的按钮，参数是格子索引而非数据索引）
		/// </summary>
		public Action<int> gridClick
		{
			set
			{
				getPointer().onGridClick = value;

				bindGrid();
			}
		}
	}
}