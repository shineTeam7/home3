using System;
using UnityEditor;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// 格子滚动显示控件
	/// </summary>
	public class SScrollView:GridContainer,IPointerDownHandler,IPointerUpHandler,IDragHandler,IBeginDragHandler,IEndDragHandler
	{
		[Tooltip("ScrollType为横向时，此值代表行数，否则代表列数")]
		[SerializeField]
		private int _rowOrColumn;

		[Tooltip("是否支持循环")]
		[SerializeField]
		private bool _loop;

		[Tooltip("最大超出屏幕距离百分比,具体数值由宽高决定此值(百分位)")]
		[SerializeField]
		private float _maxOutDistancePercent=15;

		[SerializeField]
		/** 裁剪类型 */
		private int maskType=0;

		[Tooltip("惯性起始速度缩放比率（相当于刚开始运动时速度的衰减率）")]
		[SerializeField]
		float SpeedRatio=1f;

		[Tooltip("启动惯性速度要求")]
		[SerializeField]
		float SpeedBegin=1f;

		[Tooltip("停止惯性速度要求")]
		[SerializeField]
		float SpeedEnd=0.5f;

		[Tooltip("速度上限")]
		[SerializeField]
		float SpeedMax=300;

		[Tooltip("速度衰减率")]
		[SerializeField]
		[Range(0.01f,0.5f)]
		float SpeedDis=0.1f;

		[Tooltip("超出时速度衰减增加率")]
		[SerializeField]
		float OutSpeedDis=1f;

		[Tooltip("回位最长时间(毫秒)")]
		[SerializeField]
		int BackTime=400;

		[Tooltip("对齐到网格")]
		[SerializeField]
		bool AlignGrid=false;

		[Tooltip("对齐到网格需要的最长时间(毫秒)")]
		[SerializeField]
		int AlignTime=100;

		[Tooltip("滚动逻辑判定像素数(超出此长度则判定为滑动，否则可以走点击事件)")]
		[SerializeField]
		int ScrollLogicDistance=10;

		[Tooltip("是否需要根据位置变化透明度")]
		[SerializeField]
		bool NeedChangeAlpha=false;
		
		[Tooltip("根据位置变化透明度时的，最大长度")]
		[SerializeField]
		int ChangeAlphaLen=20;
		
		/** 滚动位置 */
		protected float _scrollPos;

		/**
		 * 上一次触碰的位置
		 */
		private float _lastTouchPos;

		/**
		 * 上一次触碰的时间
		 * @type Number
		 */
		private int _lastTouchTime;

		/**
		 * 惯性速度
		 */
		private float _touchSpeed;

		/**
		 * 最大真实滑动位置
		 */
		private float _maxRealPos;

		/**
		 * 最小真实滑动位置
		 */
		private float _minRealPos;

		/**
		 * 最大超出距离
		 */
		private float _maxOutDistance;

		/**
		 * 可滑动区域总长度
		 */
		private float _dataViewDistance;

		/**
		 * 桢控制方法索引
		 */
		private int _frameControlIndex=-1;

		/**
		 * 上次touch差值
		 */
		private float _touchD=0;

		/**
		 * 是否已经滚动(判断子格子的点击事件)
		 */
		private bool _hasScroll=false;

		/**
		 * 是否已经触碰(标记是否已经有触碰，解决多点触碰问题)
		 */
		private bool _isTouching=false;

		/**
		 * 锁定滚动
		 */
		private bool _lockScroll=false;

		/**
		 * 触碰id
		 */
		private int _touchId;

		/**
		 * 是否已初始化
		 */
		private bool _init;

		/**
		 * 滚动条长度相对于数据总长度的比例
		 */
		private float _scrollBarDistancePer;

		/**
		 * 当前页
		 */
		private int _curPage;

		/**
		 * 每页滚动条目数(即多少行或者多少列为一页)
		 */
		private int _pageGridNum;

		/**
		 * 缓动索引
		 */
		protected int _tweenIndex=-1;

		/**
		 * 新页回调
		 */
		private Action<int> _newPageAction;

		/**
		 * 进度变化回调函数
		 */
		private Action<float,float> _progressChangeAction;

		/**
		 * 位置变化回调函数
		 */
		private Action<float> _scrollChangeAction;

		/** canvas缓存组，为了实现透明度改变 */
		private CanvasGroup[] _gridCanvasGroups;
		
		/** 停止回调 */
		private Action _endAction;
		
		void Awake()
		{
			// 向前兼容，>0.5  =0.1
			if(SpeedDis>0.5f)
			{
				SpeedDis=0.1f;
			}
		}

		private void OnEnable()
		{
			if(ShineSetting.isEditor)
			{
				init();
			}
		}

		public void init()
		{
			if(_init)
				return;

			_init=true;

			reMake();
		}

		public void reMake()
		{
			initContainer();
			initGrid();
			clearDataCache();
			resetScrollLimit();

			if (NeedChangeAlpha)
			{
				_gridCanvasGroups=new CanvasGroup[_gridList.Length];

				for (int i = 0; i < _gridList.Length; i++)
				{
					_gridCanvasGroups[i] = _gridList[i].GetComponent<CanvasGroup>();
				}
			}

			setScrollPos(0f);
		}

		public override void resize()
		{
			base.resize();
			
			resetScrollLimit();
		}

		/** this function only used in editor. */
		public void changeGridElement()
		{
			if(ShineSetting.isEditor)
			{
				reMake();
			}
		}

		protected override void doPreInitGrid(bool isVirticle)
		{
			if(isVirticle)
			{
				_column=_rowOrColumn;
				_row=(int)Math.Ceiling(_height / _gridCoverHeight) + 1;
			}
			else
			{
				_row=_rowOrColumn;
				_column=(int)Math.Ceiling(_width / _gridCoverWidth) + 1;
			}
		}

		/// <summary>
		/// 滑到头之后，是否可以继续滑动
		/// </summary>
		public float maxOutDistancePercent
		{
			get {return _maxOutDistancePercent;}
			set
			{
				_maxOutDistancePercent=value;
				resetScrollLimit();
			}
		}

		/// <summary>
		/// 设置滚进度
		/// </summary>
		/// <param name="progress">滚动进度（0-1）</param>
		/// <param name="needCallback">是否需要回调</param>
		public void setScrollProgress(float progress,bool needCallback=false)
		{
			setScrollPos(progress * _dataViewDistance,needCallback);
		}

		/// <summary>
		/// 设置滚进度
		/// </summary>
		public void setScrollPosion(float position)
		{
			setScrollPos(position);
		}

		/** 获取当前滚动进度 */
		public float getScrollPosion
		{
			get {return _scrollPos;}
		}

		/// <summary>
		/// 获得滚动长度
		/// </summary>
		public float getScrollDistance()
		{
			return _dataViewDistance;
		}

		/// <summary>
		/// 锁定滚动
		/// </summary>
		public bool lockScroll
		{
			get {return _lockScroll;}
			set
			{
				_lockScroll=value;
				if(_lockScroll)
					stopSpeed();
			}
		}

        public bool IsLoop
        {
            get { return _loop; }
            set { _loop = value; }
        }

		/// <summary>
		/// 设置进度改变回调函数
		/// </summary>
		/// <param name="action">进度改变函数，会传递两个参数，参数1：当前进度条位置相对于可滑动区域总长度的比例，参数2：当前进度条长度相对于数据总长度的比例</param>
		public void setProgressChangeAction(Action<float,float> action)
		{
			_progressChangeAction=action;
		}

		public void setScrollPosChangeAction(Action<float> action)
		{
			_scrollChangeAction=action;
		}

		public void setEndAction(Action endAction)
		{
			_endAction = endAction;
		}
		
		/// <summary>
		/// 设置滚动条页数及回调
		/// </summary>
		/// <param name="pageNum">每页滚动条目数(即多少行或者多少列为一页)</param>
		/// <param name="callBack">页数更新回调</param>
		public void setPageNum(int pageNum,Action<int> callBack)
		{
			if(_loop)
			{
				Ctrl.errorLog(new Exception("循环滚动条不支持页数"));
				return;
			}

			_pageGridNum=pageNum;
			_newPageAction=callBack;
			refreshPage(false);
		}

		/**
		 * 刷新所有格子数据
		 */
		public void refreshGridData()
		{
			clearDataCache();
			setScrollPos(_scrollPos);
		}

		/// <summary>
		/// 重置格子数据并恢复原位
		/// </summary>
		public void resetGridData()
		{
			clearDataCache();
			setScrollPos(0f);
		}

		/// <summary>
		/// 直接滚动格子到初始位置
		/// </summary>
		public void resetToInitPos()
		{
			setScrollPos(0f);
		}

		protected override void doSetDataList()
		{
			resetScrollLimit();
			clearDataCache();
			setScrollPos(_scrollPos);

			if(!_isTouching && Math.Abs(_touchSpeed)<0.1f)
				checkAndBack();
		}

		/** 刷新滑动限制位置 */
		private void resetScrollLimit()
		{
			if(_scrollType==SScrollType.Vertical)
			{
				int maxRows=(int)Math.Ceiling((double)_dataList.size() / _column);

				_maxRealPos=0;
				if(_loop)
					_minRealPos=maxRows==0 ? 0 : Math.Min(maxRows * -_gridCoverHeight + _verticalSpace,0f);
				else
					_minRealPos=Math.Min(maxRows * -_gridCoverHeight + _verticalSpace + _height,0f);

				_maxOutDistance=_maxOutDistancePercent / 100 * _height;

				_dataViewDistance=_maxRealPos - _minRealPos;
				_scrollBarDistancePer=_height / (_dataViewDistance + _height);
			}
			else
			{
				int maxColumns=(int)Math.Ceiling((double)_dataList.size() / _row);

				if(_loop)
					_maxRealPos=maxColumns==0 ? 0 : Math.Max(maxColumns * _gridCoverWidth - _horizontalSpace,0f);
				else
					_maxRealPos=Math.Max(maxColumns * _gridCoverWidth - _horizontalSpace - _width,0f);
				_minRealPos=0;

				_maxOutDistance=_maxOutDistancePercent / 100 * _width;

				_dataViewDistance=_maxRealPos - _minRealPos;
				_scrollBarDistancePer=_width / (_dataViewDistance + _width);
			}
		}

		private void setScrollPos(float pos,bool needCallback=true)
		{
			_scrollPos=_loop ? _minRealPos + MathUtils.absMode(pos - _minRealPos,_maxRealPos - _minRealPos) : MathUtils.clamp(pos,_minRealPos - _maxOutDistance,_maxRealPos + _maxOutDistance);
			if(!_loop)
				refreshPage(true);

			int len=_gridList.Length;
			for(var i=0;i<len;i++)
			{
				refreshGrid(i);
			}
            
			if(needCallback)
				refreshScrollProgress();
		}

		private void refreshGrid(int index)
		{
			/** 滚动的格子数 */
			int scrollNum;
			/** 滚动的偏移量 */
			float offset;
			/** 实际滚动的像素数（即不包括拉到头之后再继续拉的距离） */
			float realScrollPos=_loop ? _scrollPos : MathUtils.clamp(_scrollPos,_minRealPos,_maxRealPos);
			int dataRow;
			int dataIndex;
			int curRow;
			int curColumn;

			Vector3 tmpPos;
			if(_scrollType==SScrollType.Vertical)
			{
				scrollNum=(int)Math.Floor(realScrollPos / -_gridCoverHeight);
				offset=scrollNum * _gridCoverHeight + _scrollPos;

				int startRow=(int)((double)index / _column);
				curRow=MathUtils.absMod(startRow - scrollNum,_row);
				curColumn=index % _column;

				RectTransform objTransform=_gridList[index];
				tmpPos=objTransform.anchoredPosition;
				tmpPos.y=-curRow * _gridCoverHeight - offset;
				objTransform.anchoredPosition=tmpPos;
				dataRow=startRow + (int)Math.Floor((float)(_row - 1 - startRow + scrollNum) / _row) * _row;
				dataIndex=dataRow * _column + curColumn;
				if(_loop)
					dataIndex=MathUtils.absMod(dataIndex,_dataList.size());

				if(dataIndex>=_dataList.size())
				{
					setData(index,-1);
				}
				else
				{
					setData(index,dataIndex);
				}

            }
			else
			{
				scrollNum=(int)Math.Floor(realScrollPos / _gridCoverWidth);
				offset=scrollNum * _gridCoverWidth - _scrollPos;

				int startColumn=(int)((float)index / _row);
				curColumn=MathUtils.absMod(startColumn - scrollNum,_column);
				curRow=index % _row;

				RectTransform objTransform=_gridList[index];
				tmpPos=objTransform.anchoredPosition;
				tmpPos.x=curColumn * _gridCoverWidth + offset;
				objTransform.anchoredPosition=tmpPos;
				dataRow=startColumn + (int)Math.Floor((float)(_column - 1 - startColumn + scrollNum) / _column) * _column;
				dataIndex=dataRow * _row + curRow;
				if(_loop)
					dataIndex=MathUtils.absMod(dataIndex,_dataList.size());

				if(dataIndex>=_dataList.size())
				{
					setData(index,-1);
				}
				else
				{
					setData(index,dataIndex);
				}
                
            }
        }

		protected override void setData(int index, int dataIndex)
		{
			base.setData(index, dataIndex);
			
			if (NeedChangeAlpha)
			{
				float alphaFactor = 1;
				
				if (_scrollType == SScrollType.Horizontal)
				{
					alphaFactor = Mathf.Clamp01((_width - _gridList[index].anchoredPosition.x) / ChangeAlphaLen);
				}
				else
				{
					alphaFactor = Mathf.Clamp01((_height - _gridList[index].anchoredPosition.y) / ChangeAlphaLen);
				}

				_gridCanvasGroups[index].alpha = alphaFactor;
			}
		}

		/// <summary>
		/// 刷新页数
		/// </summary>
		/// <param name="isCallBack">是否调用回调</param>
		private void refreshPage(bool isCallBack)
		{
			if(_newPageAction==null)
				return;

			/** 实际滚动的像素数（即不包括拉到头之后再继续拉的距离） */
			float realScrollPos=MathUtils.clamp(_scrollPos,_minRealPos,_maxRealPos);
			/** 每页的长度 */
			float pageDistance;

			int curPage;

			if(_scrollType==SScrollType.Vertical)
			{
				pageDistance=-_gridCoverHeight * _pageGridNum;
				curPage=(int)((realScrollPos + _height) / pageDistance);
			}
			else
			{
				pageDistance=_gridCoverWidth * _pageGridNum;
				curPage=(int)((realScrollPos + _width) / pageDistance);
			}

			if(isCallBack)
			{
				if(curPage!=_curPage)
					_newPageAction(curPage);
			}

			_curPage=curPage;
		}

		public void OnPointerDown(PointerEventData eventData)
		{
			if(!_touchAble)
				return;

			if(_isTouching)
				return;

			_isTouching=true;
			_touchId=eventData.pointerId;

			if(!_hasScroll)
			{
				doDownAction(eventData);
			}
		}

		public void OnPointerUp(PointerEventData eventData)
		{
			if(!_touchAble)
				return;

			if(eventData.pointerId!=_touchId)
				return;

			if(!_hasScroll)
			{
				doUpAction(eventData);
			}

			_isTouching=false;
		}

		public void OnDrag(PointerEventData eventData)
		{
			if(!_touchAble)
				return;

			if(eventData.pointerId!=_touchId)
				return;

			onTouchMove(eventData);
		}

		public void OnBeginDrag(PointerEventData eventData)
		{
			if(!_touchAble)
				return;

			if(eventData.pointerId!=_touchId)
				return;

			stopSpeed();

			var pt=eventData.position;
			if(_scrollType==SScrollType.Vertical)
			{
				_lastTouchPos=pt.y;
			}
			else
			{
				_lastTouchPos=pt.x;
			}

			_touchD=0;

			onTouchMove(eventData);
		}

		public void OnEndDrag(PointerEventData eventData)
		{
			if(!_touchAble)
				return;

			if(eventData.pointerId!=_touchId)
				return;

			if (_lockScroll)
			{
				_hasScroll = false;
				return;
			}

			startSpeed(_touchD * SpeedRatio);
		}

		private void onTouchMove(PointerEventData eventData)
		{
			var pt=eventData.position;

			float offset;
			if(_scrollType==SScrollType.Vertical)
			{
				offset=pt.y - _lastTouchPos;
				_lastTouchPos=pt.y;
			}
			else
			{
				offset=pt.x - _lastTouchPos;
				_lastTouchPos=pt.x;
			}

			_touchD=-offset;

			if(!_hasScroll)
			{
				_hasScroll=true;

				doCancelAction(eventData);
			}

			if(_lockScroll)
				return;

			setScrollPos(_scrollPos + getAddPos(_touchD * _scaleRadio));
		}

		private void startSpeed(float speed)
		{
			stopSpeed();

			if(Math.Abs(speed)<=SpeedBegin)
			{
				checkAndBack();
				return;
			}

			_touchSpeed=MathUtils.clamp(speed,-SpeedMax,SpeedMax);

			_frameControlIndex=TimeDriver.instance.setFrame(onFrame);
		}

		private void stopSpeed()
		{
			if(_frameControlIndex!=-1)
			{
				TimeDriver.instance.clearFrame(_frameControlIndex);
				_frameControlIndex=-1;
			}

			_touchSpeed=0;
		}

		/**
		 * 检测当前位置并执行回位操作
		 */
		 private void checkAndBack()
		{
			if(_loop)
			{
				_hasScroll=false;
				return;
			}

			if(_tweenIndex!=-1)
				Tween.normal.kill(_tweenIndex);

			float targetPos=0;
			float distance=0;
			bool needBack=false;

			if(_scrollPos>_maxRealPos)
			{
				targetPos=_maxRealPos;
				distance=_scrollPos - _maxRealPos;
				needBack=true;
			}
			else if(_scrollPos<_minRealPos)
			{
				targetPos=_minRealPos;
				distance=_minRealPos - _scrollPos;
				needBack=true;
			}

			if(needBack)
			{
				if(Math.Abs(_maxOutDistance)<0.1f)
				{
					setScrollPos(targetPos);
				}
				else
				{
					int tweenTime=(int)(distance * BackTime / _maxOutDistance);
					if(tweenTime>BackTime)
						tweenTime=BackTime;

					if(tweenTime>0 && !ShineSetting.isEditor)
					{
						_tweenIndex=Tween.normal.create(_scrollPos,targetPos,tweenTime,onBackTween);
					}
					else
					{
						setScrollPos(targetPos);
					}
				}
			}
			else
			{
				if(AlignGrid)
				{
					int tweenTime=0;
					if(_scrollType==SScrollType.Vertical)
					{
						targetPos=MathUtils.clamp((int)Math.Round(_scrollPos / -_gridCoverHeight) * -_gridCoverHeight,_minRealPos,_maxRealPos);
						distance=Math.Abs(_scrollPos - targetPos);
						tweenTime=(int)(distance * AlignTime / _gridCoverHeight);
					}
					else
					{
						targetPos=MathUtils.clamp((int)Math.Round(_scrollPos / _gridCoverWidth) * _gridCoverWidth,_minRealPos,_maxRealPos);
						distance=Math.Abs(_scrollPos - targetPos);
						tweenTime=(int)(distance * AlignTime / _gridCoverWidth);
					}

					if(tweenTime>0 && !ShineSetting.isEditor)
					{
						_tweenIndex=Tween.normal.create(_scrollPos,targetPos,tweenTime,onBackTween);
					}
					else
					{
						setScrollPos(targetPos);
					}
				}
			}

			_hasScroll=false;
		}

		private void onBackTween(float value)
		{
			setScrollPos(value);
		}

		private void onFrame(int interval)
		{
            if (Math.Abs(_touchSpeed) <= SpeedEnd || (!_loop && (_scrollPos > _maxRealPos + _maxOutDistance || _scrollPos < _minRealPos - _maxOutDistance)) || getAddPos(_touchSpeed) == 0)
            { 
                if (_endAction != null)
	                _endAction();
                
                stopSpeed();
				checkAndBack();
				return;
			}

			setScrollPos(_scrollPos + getAddPos(_touchSpeed));

			_touchSpeed-=_touchSpeed * SpeedDis;
			if(_touchSpeed>0)
			{
				// _touchSpeed -= SpeedDis;
				if(!_loop && _scrollPos>_maxRealPos)
					_touchSpeed-=OutSpeedDis;
				if(_touchSpeed<0)
					_touchSpeed=0;
			}
			else
			{
				// _touchSpeed += SpeedDis;
				if(!_loop && _scrollPos<_minRealPos)
					_touchSpeed+=OutSpeedDis;
				if(_touchSpeed>0)
					_touchSpeed=0;
			}
		}

		private float getAddPos(float distance)
		{
			if(_loop)
				return distance;

			float percent=0;
			float testPos=_scrollPos + distance;
			if(_maxOutDistance>0)
			{
				if(testPos>_maxRealPos && distance>0)
				{
					percent=(testPos - _maxRealPos) / _maxOutDistance;
				}
				else if(testPos<_minRealPos && distance<0)
				{
					percent=(_minRealPos - testPos) / _maxOutDistance;
				}

				if(percent>=1)
				{
					distance=0;
				}
				else if(percent>0)
				{
					distance*=1 - percent;
				}
			}
			else
			{
				if(testPos>_maxRealPos && distance>0)
				{
					distance=_maxRealPos - _scrollPos;
				}
				else if(testPos<_minRealPos && distance<0)
				{
					distance=_minRealPos - _scrollPos;
				}
			}

			return distance;
		}

		private void refreshScrollProgress()
		{
			if(_progressChangeAction!=null)
			{
				float progress=MathUtils.clamp(_scrollPos / _dataViewDistance,0f,1f);
				_progressChangeAction(float.IsNaN(progress) ? 0 : progress,_scrollBarDistancePer);
			}

			if(_scrollChangeAction!=null)
			{
				_scrollChangeAction(_scrollPos);
			}
		}

        public int getRowOrColumn()
        {
            return _rowOrColumn;
        }
	}
}