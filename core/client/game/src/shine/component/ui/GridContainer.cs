using System;
using UnityEditor;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;
using Object = UnityEngine.Object;

namespace ShineEngine
{

	public class GridContainer:MonoBehaviour
	{
		[SElementAttrubute]
		public string gridElement;

		[SerializeField]
		protected SScrollType _scrollType=SScrollType.Vertical;

		[SerializeField]
		protected int _row;

		[SerializeField]
		protected int _column;

		[Tooltip("横向格子间隔")]
		[SerializeField]
		protected float _horizontalSpace;

		[Tooltip("纵向格子间隔")]
		[SerializeField]
		protected float _verticalSpace;

		/** 格子摆放锚点，固定左上角 */
		private static readonly Vector2 gridPivot=new Vector2(0f,1f);

		/** 格子适配方案锚点最大值 */
		private static readonly Vector2 gridAnchorMin=new Vector2(0f,1f);

		/** 格子适配方案锚点最小值 */
		private static readonly Vector2 gridAnchorMax=new Vector2(0f,1f);

		/** 容器宽 */
		protected float _width;

		/** 容器高 */
		protected float _height;

		/** 格子覆盖面积宽（包括本身宽以及横向间隔） */
		protected float _gridCoverWidth;

		/** 格子覆盖面积高（包括本身高以及纵向间隔） */
		protected float _gridCoverHeight;

		/** 当前屏幕适配缩放比例 */
		protected float _scaleRadio=1f;

		/** 格子数据集合 */
		protected SList<object> _dataList;

		/** 格子显示对象列表 */
		protected RectTransform[] _gridList;

		/** 格子数据id显示缓存 */
		protected int[] _gridDataCache;

		/** 是否可以触碰 */
		protected bool _touchAble=true;

		/** 空数据是否隐藏格子 */
		private bool _nullDontShow;

		/** 设置数据回调 */
		protected Action<int,int> _setDataAction;

		/** 触碰格子按下回调 */
		private Action<int> _touchGridDownAction;

		/** 触碰格子抬起回调 */
		private Action<int> _touchGridUpAction;

		/** 触碰格子取消回调 */
		private Action<int> _touchGridCancelAction;

		/** 触碰容器内部按下回调 */
		private Action<PointerEventData> _touchDownAction;

		/** 触碰容器内部抬起回调 */
		private Action<PointerEventData> _touchUpAction;

		/** 触碰容器内部取消回调 */
		private Action<PointerEventData> _touchCancelAction;

		/** 自定义检测格子是否被触碰方法 */
		private Func<int,PointerEventData,bool> _touchCheckAction;

		/// <summary>
		/// 格子列表
		/// </summary>
		public RectTransform[] gridList
		{
			get {return _gridList;}
		}

		/// <summary>
		/// 是否开启触碰
		/// </summary>
		public bool touchEnable
		{
			get {return _touchAble;}
			set {_touchAble=value;}
		}

		/// <summary>
		/// 数据为空时，是否隐藏格子显示
		/// </summary>
		public bool nullDontShow
		{
			get {return _nullDontShow;}
			set {_nullDontShow=value;}
		}

		/// <summary>
		/// 设置数据改变的回调函数
		/// </summary>
		/// <param name="action"></param>
		public void setDataAction(Action<int,int> action)
		{
			_setDataAction=action;
		}

		/// <summary>
		/// 设置自定义检测格子被触碰方法
		/// </summary>
		/// <param name="action"></param>
		public void setCheckGridTouchAction(Func<int,PointerEventData,bool> action)
		{
			_touchCheckAction=action;
		}

		/// <summary>
		/// 设置格子按下回调
		/// </summary>
		/// <param name="action"></param>
		public void setGridTouchDownAction(Action<int> action)
		{
			_touchGridDownAction=action;
		}

		/// <summary>
		/// 设置格子点击回调
		/// </summary>
		/// <param name="action"></param>
		public void setGridTouchUpAction(Action<int> action)
		{
			_touchGridUpAction=action;
		}

		/// <summary>
		/// 设置格子取消触碰回调
		/// </summary>
		/// <param name="action"></param>
		public void setGridTouchCancelAction(Action<int> action)
		{
			_touchGridCancelAction=action;
		}

		/// <summary>
		/// 设置触容器按下回调
		/// </summary>
		public void setTouchDownAction(Action<PointerEventData> action)
		{
			_touchDownAction=action;
		}

		/// <summary>
		/// 设置容器抬起回调
		/// </summary>
		public void setTouchUpAction(Action<PointerEventData> action)
		{
			_touchUpAction=action;
		}

		/// <summary>
		/// 设置容器取消触碰回调
		/// </summary>
		public void setTouchCancelAction(Action<PointerEventData> action)
		{
			_touchCancelAction=action;
		}

		/// <summary>
		/// 设置格子数据
		/// </summary>
		/// <param name="list">数据列表</param>
		public void setDataList<T>(SList<T> list)
		{
			SList<object> temp;
			(temp=_dataList).clear();

			T[] values=list.getValues();
			for(int i=0,len=list.size();i<len;i++)
			{
				temp.add(values[i]);
			}

			clearDataCache();

			doSetDataList();
		}

		/// <summary>
		/// 增加格子数据
		/// </summary>
		/// <param name="obj">要增加的格子数据</param>
		public void addData(object obj)
		{
			_dataList.add(obj);

			clearDataCache();

			doSetDataList();
		}

		/// <summary>
		/// 清空格子数据
		/// </summary>
		public void clearDataList()
		{
			_dataList.clear();

			clearDataCache();

			doSetDataList();
		}

		/// <summary>
		/// 清除所有格子数据缓存
		/// </summary>
		public void clearDataCache()
		{
			for(var i = _gridDataCache.Length - 1;i >= 0;--i)
			{
				_gridDataCache[i] = -1;
				_gridList[i].gameObject.SetActive(false);
			}
		}

		/// <summary>
		/// 根据当前格子索引获取格子数据索引
		/// </summary>
		/// <param name="index">格子索引</param>
		/// <returns></returns>
		public int getGridDataIndex(int index)
		{
			if(index < 0 || index >= _gridList.Length)
				return -1;

			return _gridDataCache[index];
		}

		/// <summary>
		/// 根据当前格子索引获取格子数据
		/// </summary>
		/// <param name="index">格子索引</param>
		/// <returns></returns>
		public object getGridData(int index)
		{
			int dataIndex = getGridDataIndex(index);
			if(dataIndex == -1)
				return null;

			return _dataList[dataIndex];
		}

		/** 初始化容器布局 */
		protected virtual void initContainer()
		{
			if(ShineSetting.isEditor)
			{
				_dataList=new SList<object>(100);
				_dataList.justSetSize(100);
			}
			else
			{
				_dataList=new SList<object>();
			}

			resize();
		}

		public virtual void resize()
		{
			RectTransform rectTransform=(RectTransform)gameObject.transform;

			// rectTransform.sizeDelta

			int realScreenWidth=Screen.width;
			int realScreenHeight=Screen.height;

			// _scaleRadio=cs.referenceResolution.y / realScreenHeight * cs.matchWidthOrHeight + cs.referenceResolution.x / realScreenWidth * (1 - cs.matchWidthOrHeight);
			// float screenWidth=realScreenWidth * _scaleRadio;
			// float screenHeight=realScreenHeight * _scaleRadio;

			Vector2 rSize=rectTransform.sizeDelta;

			_width=rSize.x<0 ? rSize.x + realScreenWidth : rSize.x;
			_height=rSize.y<0 ? rSize.y + realScreenHeight : rSize.y;
			
			doPreInitGrid(_scrollType==SScrollType.Vertical);
		}

		/** 初始化格子显示 */
		protected virtual void initGrid()
		{
			GameObject modelGrid=getModelGrid();
			RectTransform gridTransform=modelGrid.GetComponent<RectTransform>();

			_gridCoverWidth=gridTransform.sizeDelta.x + _horizontalSpace;
			_gridCoverHeight=gridTransform.sizeDelta.y + _verticalSpace;

			bool isVirticle=_scrollType==SScrollType.Vertical;
			doPreInitGrid(isVirticle);
			doInitGrid(modelGrid,isVirticle);

			//用完之后删除模板
			if(!ShineSetting.isEditor)
			{
				Destroy(modelGrid);
			}
			else
			{
				// DestroyImmediate(modelGrid);
			}
		}

		protected virtual void doPreInitGrid(bool isVirticle)
		{

		}

		protected virtual void doInitGrid(GameObject modelGrid,bool isVirticle)
		{
			int gridNum=getGridNum(isVirticle);
			Vector3 tmpPos;
			Transform rooTransform=gameObject.transform;

			_gridList = new RectTransform[gridNum];
			_gridDataCache = new int[gridNum];

			if(isVirticle)
			{
				for(int i=0;i<gridNum;i++)
				{
					tmpPos=Vector3.zero;
					tmpPos.x=i % _column * _gridCoverWidth;
					_gridList[i]=initOneGrid(modelGrid,rooTransform,"g" + i,tmpPos);
				}
			}
			else
			{
				for(int i=0;i<gridNum;i++)
				{
					tmpPos=Vector3.zero;
					tmpPos.y=i % _row * -_gridCoverHeight;
					_gridList[i]=initOneGrid(modelGrid,rooTransform,"g" + i,tmpPos);
				}
			}
		}

		/** 获取需要初始化的格子数 */
		protected virtual int getGridNum(bool isVirticle)
		{
			return _row * _column;
		}

		/** 初始化单个格子 */
		protected virtual RectTransform initOneGrid(GameObject modelGrid,Transform rooTransform,string name,Vector3 pos)
		{
			GameObject grid=null;

			if(ShineSetting.isEditor)
			{
	#if UNITY_EDITOR
					grid=(GameObject)PrefabUtility.InstantiatePrefab(modelGrid);
	#endif
			}
			else
			{
				grid=Instantiate(modelGrid);
			}

			if(grid)
			{
				RectTransform objTransform=(RectTransform)grid.transform;
				objTransform.SetParent(rooTransform,false);
				objTransform.pivot=gridPivot;
				objTransform.anchorMin=gridAnchorMin;
				objTransform.anchorMax=gridAnchorMax;
				objTransform.anchoredPosition=pos;
				grid.name=name;
				grid.SetActive(true);

				//去除子节点消息阻挡
				Graphic[] graphics=objTransform.GetComponentsInChildren<Graphic>(true);
				for(int i=0;i<graphics.Length;i++)
				{
					graphics[i].raycastTarget=false;
				}

				return objTransform;
			}

			return null;
		}

		/** 获取一个模板grid */
		protected virtual GameObject getModelGrid()
		{
			GameObject retGrid;

			if(ShineSetting.isEditor)
			{
				retGrid=getModelGridByEditor();
			}
			else
			{
				retGrid=getModelGridByGame();
			}

			return retGrid;
		}


		protected virtual GameObject getModelGridByEditor()
		{
			string path = "Assets/source/ui/elements/"+gridElement+".prefab";

			GameObject prefab=null;

#if UNITY_EDITOR
			prefab = AssetDatabase.LoadAssetAtPath<GameObject>(path);
#endif
			//清除无用gameObject
			while(transform.childCount>0)
			{
				DestroyImmediate(transform.GetChild(0).gameObject);
			}

			return prefab;
		}


		protected virtual GameObject getModelGridByGame()
		{
			GameObject retGrid=null;

			for(int i=0;i<transform.childCount;i++)
			{
				GameObject tmpGo=transform.GetChild(i).gameObject;

				if(tmpGo.name=="g0")
					retGrid=tmpGo;
				else
					Destroy(tmpGo);
			}

			if(retGrid==null)
			{
				Ctrl.errorLog("不应该找不到g0");
			}

			return retGrid;
		}

		protected virtual void doSetDataList()
		{

		}

		/** 设置格子数据 */
		protected virtual void setData(int index, int dataIndex)
		{
			if(_gridDataCache[index] == dataIndex)
				return;

			GameObject obj = _gridList[index].gameObject;
			if(dataIndex<-1)
			{
				obj.SetActive(false);
				return;
			}

			_gridDataCache[index] = dataIndex;

			//如果不显示，则不触发回调
			if(dataIndex==-1 || (nullDontShow && _dataList[dataIndex]==null))
			{
				obj.SetActive(false);
			}
			else
			{
				obj.SetActive(true);
				if(_setDataAction!=null)
					_setDataAction(index,dataIndex);
			}
		}

		/** 执行按下回调 */
		protected void doDownAction(PointerEventData eventData)
		{
			if(_touchDownAction!=null && gameObject.activeInHierarchy)
				_touchDownAction(eventData);

			if(_touchGridDownAction!=null)
			{
				for(var i = 0;i < _gridList.Length;i++)
				{
					if(!_gridList[i].gameObject.activeSelf)
						continue;

					if(checkTouchGrid(i,eventData))
					{
						_touchGridDownAction(i);
					}
				}
			}
		}

		/** 执行抬起回调 */
		protected void doUpAction(PointerEventData eventData)
		{
			if(_touchUpAction!=null && gameObject.activeInHierarchy)
				_touchUpAction(eventData);

			if(_touchGridUpAction!=null)
			{
				for(var i = 0;i < _gridList.Length;i++)
				{
					if(!_gridList[i].gameObject.activeSelf)
						continue;

					if(checkTouchGrid(i,eventData))
					{
						_touchGridUpAction(i);
					}
				}
			}
		}

		/** 执行取消触摸回调 */
		protected void doCancelAction(PointerEventData eventData)
		{
			if(_touchCancelAction!=null && gameObject.activeInHierarchy)
				_touchCancelAction(eventData);

			if(_touchGridCancelAction!=null)
			{
				for(var i = 0;i < _gridList.Length;i++)
				{
					if(!_gridList[i].gameObject.activeSelf)
						continue;

					if(checkTouchGrid(i,eventData))
					{
						_touchGridCancelAction(i);
					}
				}
			}
		}

		private bool checkTouchGrid(int index,PointerEventData eventData)
		{
			if(!gameObject.activeInHierarchy)
				return false;

			if(_touchCheckAction!=null)
				return _touchCheckAction(index,eventData);

			if(_gridList[index].gameObject.activeSelf && RectTransformUtility.RectangleContainsScreenPoint(_gridList[index],eventData.position,UIControl.getUICameraComponent()))
			{
				return true;
			}

			return false;
		}
	}
}