using System;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

namespace ShineEngine
{

	/// <summary>
	/// 格子翻页显示控件
	/// </summary>
	[RequireComponent(typeof(Image),typeof(Mask))]
	public class SPageView:GridContainer,IPointerDownHandler,IPointerUpHandler,IDragHandler,IBeginDragHandler
	{
		[Tooltip("翻页间隔,既两页之间的间隔")]
		[SerializeField]
		private float _pageSpace;

		[Tooltip("是否支持循环")]
		[SerializeField]
		private bool _loop=true;

		[Tooltip("回位时间(毫秒)")]
		[SerializeField]
		int BackTime=400;

		[Tooltip("翻页时间(毫秒)")]
		[SerializeField]
		int ChangePageTime=100;

		[Tooltip("滑动翻页距离比例（以控件宽高为基准，滑动距离超过比例才可以翻页，否则回归当前页）")]
		[SerializeField]
		float ChangePageDistancePer=0.2f;

		[Tooltip("滑动自动翻页距离比例（以控件宽高为基准，滑动超过比例则不等待TouchUp直接翻页）")]
		[SerializeField]
		float AutoChangePageDistancePer=0.5f;

		[Tooltip("滚动逻辑判定像素数(超出此长度则判定为滑动，否则可以走点击事件)")]
		[SerializeField]
		float ScrollLogicDistance=10f;

		/** 整体页数 */
		private int _totalPage=0;

		/** 当前页数(从0开始) */
		private int _curPage;

		/** 滚动位置 */
		private float _scrollPos;

		/** 翻页距离相对于普通间隔的附加距离 */
		private float _pageAddSpace;

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
		 * 最大真实滑动位置
		 */
		private float _maxRealPos;

		/**
		 * 最小真实滑动位置
		 */
		private float _minRealPos;

		/**
		 * 最大超出屏幕距离
		 */
		private float _maxOutDistance;

		/**
		 * 翻页滑动判定距离（即滑动多长距离则判定为翻页）
		 */
		private float _changePageDistance;

		/**
		 * 滑动自动翻页判定距离（代表即使手指不离开，一旦滑动超过此距离，则自动翻页）
		 */
		private float _autoChangePageDistance;

		/**
		 * 上次touch差值
		 */
		private float _touchD;

		/**
		 * 触碰移动偏移量
		 */
		private float _touchOffset;

		/**
		 * 是否已经滚动(判断子格子的点击事件)
		 */
		private bool _hasScroll=false;

		/**
		 * 是否已经触碰(标记是否已经有触碰，解决多点触碰问题)
		 */
		private bool _isTouching=false;

		/**
		 * 触碰id
		 */
		private int _touchId;

		/**
		 * 是否已初始化
		 */
		private bool _init;

		/**
		 * 缓动索引
		 */
		private int _tweenIndex=-1;

		/**
		 * 翻页缓动索引
		 */
		private int _changePageTweenIndex=-1;

		/**
		 * 新页回调
		 */
		private Action<int> _newPageAction;

		private void OnEnable()
		{
			init();
		}

		public void init()
		{
			if(_init)
				return;

			initContainer();
			initGrid();

			setChangePageDistancePer(ChangePageDistancePer);
			setAutoChangePageDistancePer(AutoChangePageDistancePer);

			clearDataCache();
			resetScrollLimit();
			setScrollPos(0f);

			_init=true;
		}

		/** this function only used in editor. */
		public void changeGridElement()
		{
			//强制初始化
			_init=false;
			init();
		}

		protected override void doPreInitGrid(bool isVirticle)
		{
			if(isVirticle)
			{
				_pageAddSpace=_pageSpace - _verticalSpace;
			}
			else
			{
				_pageAddSpace=_pageSpace - _horizontalSpace;
			}
		}

		protected override int getGridNum(bool isVirticle)
		{
			return isVirticle ? _column * (_row + 1) : _row * (_column + 1);
		}

		public int totalPage
		{
			get {return _totalPage;}
		}

		public void setChangePageCallBack(Action<int> action)
		{
			_newPageAction=action;
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
			setPage(0);
		}

		/// <summary>
		/// 直接滚动格子到初始位置
		/// </summary>
		public void resetToInitPos()
		{
			setPage(0);
		}

		protected override void doSetDataList()
		{
			resetScrollLimit();
			clearDataCache();

			setScrollPos(_scrollPos);

			if(!_isTouching)
				checkAndBack();
		}

		public int page
		{
			get {return _curPage;}
		}

		public void setNextPage(bool immediately=false)
		{
			if(_curPage < _totalPage - 1){
				setPage(_curPage + 1, immediately);
			}
		}

		public void setPrevPage(bool immediately=false)
		{
			if(_curPage > 0){
				setPage(_curPage - 1, immediately);
			}
		}

		/** 刷新滑动限制位置 */
		private void resetScrollLimit()
		{
			_totalPage=(int)Math.Ceiling((double)_dataList.size() / (_row * _column));
			if(_totalPage==0)
				_totalPage=1;

			if(_scrollType==SScrollType.Vertical)
			{
				_maxRealPos=0;
				if(_loop)
					_minRealPos=_totalPage * (-_gridCoverHeight * _row - _pageAddSpace);
				else
					_minRealPos=(_totalPage - 1) * (-_gridCoverHeight * _row - _pageAddSpace);

				_maxOutDistance=_height;
			}
			else
			{
				if(_loop)
					_maxRealPos=_totalPage * (_gridCoverWidth * _column + _pageAddSpace);
				else
					_maxRealPos=(_totalPage - 1) * (_gridCoverWidth * _column + _pageAddSpace);
				_minRealPos=0;

				_maxOutDistance=_width;
			}
		}

		/// <summary>
		/// 设置页数
		/// </summary>
		/// <param name="page">页数</param>
		/// <param name="immediately">是否瞬间跳转</param>
		private void setPage(int page,bool immediately=false)
		{
			//编辑器模式强制为true
			if(ShineSetting.isEditor)
				immediately=true;

			int tmpPage=page;
			page=_loop ? MathUtils.absMod(page,_totalPage) : MathUtils.clamp(page,0,_totalPage - 1);

			if(_curPage!=page && _newPageAction!=null)
			{
				_newPageAction(page);
			}

			_curPage=page;

			float scrollPos;
			if(_scrollType==SScrollType.Vertical)
			{
				scrollPos=_loop ? tmpPage * (_row * -_gridCoverHeight - _pageAddSpace) : _curPage * (_row * -_gridCoverHeight - _pageAddSpace);
			}
			else
			{
				scrollPos=_loop ? tmpPage * (_column * _gridCoverWidth + _pageAddSpace) : _curPage * (_column * _gridCoverWidth + _pageAddSpace);
			}

			killChangePageTween();

			if(immediately)
			{
				setScrollPos(scrollPos);
			}
			else
			{
				//寻找最近位置
				if(_loop)
				{
					float distance=_maxRealPos - _minRealPos;
					scrollPos=getNearPos(_scrollPos,scrollPos,scrollPos + distance,scrollPos - distance);
				}

				_changePageTweenIndex=Tween.normal.create(_scrollPos,scrollPos,ChangePageTime,setScrollPos,killChangePageTween);
			}
		}

		private void setScrollPos(float pos)
		{
			_scrollPos=_loop ? _minRealPos + MathUtils.absMode(pos - _minRealPos,_maxRealPos - _minRealPos) : MathUtils.clamp(pos,_minRealPos - _maxOutDistance,_maxRealPos + _maxOutDistance);

			int len=_gridList.Length;
			for(var i=0;i<len;i++)
			{
				refreshGrid(i);
			}
		}

		private void refreshGrid(int index)
		{
			/** 滚动的格子数 */
			int scrollNum;
			/** 滚动的页数 */
			int scrollPageNum;
			/** 滚动的偏移量 */
			float offset;
			/** 翻页影响的滚动偏移量 */
			float offsetPage;
			/** 实际滚动的像素数（即不包括拉到头之后再继续拉的距离） */
			float realScrollPos=_loop ? _scrollPos : MathUtils.clamp(_scrollPos,_minRealPos,_maxRealPos);
			int dataRow;
			int dataIndex;
			int curRow;
			int curColumn;
			int tmpColumnOrRowNum;
			int tmpChangePageNum;
			Vector3 tmpPos;

			if(_scrollType==SScrollType.Vertical)
			{
				scrollPageNum=(int)Math.Floor(realScrollPos / (-_gridCoverHeight * _row - _pageAddSpace));
				tmpColumnOrRowNum=(int)Math.Floor((realScrollPos - scrollPageNum * (-_gridCoverHeight * _row - _pageAddSpace)) / -_gridCoverHeight);
				if(tmpColumnOrRowNum>_row)
					tmpColumnOrRowNum=_row;
				scrollNum=scrollPageNum * _row + tmpColumnOrRowNum;
				tmpChangePageNum=_row - scrollNum + scrollPageNum * _row;
				offset=scrollNum * -_gridCoverHeight - scrollPageNum * _pageAddSpace - _scrollPos;

				int startRow=index / _column;
				curRow=MathUtils.absMod(startRow - scrollNum,_row + 1);
				curColumn=index % _column;
				offsetPage=curRow<tmpChangePageNum ? 0 : -_pageAddSpace;

				RectTransform objTransform=_gridList[index];
				tmpPos=objTransform.anchoredPosition;
				tmpPos.y=curRow * -_gridCoverHeight + offset + offsetPage;
				objTransform.anchoredPosition=tmpPos;
				dataRow=startRow + (int)Math.Floor((float)(_row - startRow + scrollNum) / (_row + 1)) * (_row + 1);
				dataIndex=dataRow * _column + curColumn;
				if(_loop)
					dataIndex=MathUtils.absMod(dataIndex,(int)Math.Ceiling((float)_dataList.size() / (_row * _column)) * _row * _column);

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
				scrollPageNum=(int)Math.Floor(realScrollPos / (_gridCoverWidth * _column + _pageAddSpace));
				tmpColumnOrRowNum=(int)Math.Floor((realScrollPos - scrollPageNum * (_gridCoverWidth * _column + _pageAddSpace)) / _gridCoverWidth);
				if(tmpColumnOrRowNum>_column)
					tmpColumnOrRowNum=_column;
				scrollNum=scrollPageNum * _column + tmpColumnOrRowNum;
				tmpChangePageNum=_column - scrollNum + scrollPageNum * _column;
				offset=scrollNum * _gridCoverWidth + scrollPageNum * _pageAddSpace - _scrollPos;

				int startColumn=index / _row;
				curColumn=MathUtils.absMod(startColumn - scrollNum,_column + 1);
				curRow=index % _row;
				offsetPage=curColumn<tmpChangePageNum ? 0 : _pageAddSpace;

				RectTransform objTransform=_gridList[index];
				tmpPos=objTransform.anchoredPosition;
				tmpPos.x=curColumn * _gridCoverWidth + offset + offsetPage;
				objTransform.anchoredPosition=tmpPos;
				dataRow=startColumn + (int)Math.Floor((float)(_column + 1 - 1 - startColumn + scrollNum) / (_column + 1)) * (_column + 1);
				dataIndex=dataRow * _row + curRow;
				if(_loop)
					dataIndex=MathUtils.absMod(dataIndex,(int)Math.Ceiling((float)_dataList.size() / (_row * _column)) * _row * _column);
				dataIndex=_column * (dataIndex % _row) + dataIndex / _row + dataIndex / (_row * _column) * (_row - 1) * _column; //横向滚动的每页数据也要采用竖向排布

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

		public void OnPointerDown(PointerEventData eventData)
		{
			if(!_touchAble)
				return;

			if(_isTouching)
				return;

			if(_changePageTweenIndex!=-1)
				return;

			killScrollTween();

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
			_touchOffset=0;

			_isTouching=true;
			_touchId=eventData.pointerId;

			doDownAction(eventData);
		}

		public void OnPointerUp(PointerEventData eventData)
		{
			if(!_touchAble)
				return;

			if(eventData.pointerId!=_touchId)
				return;

			if(_changePageTweenIndex!=-1)
				return;

			if(!_hasScroll)
			{
				doUpAction(eventData);

				setPage(_curPage,true);
			}
			else
			{
				if(Math.Abs(_touchOffset)>_changePageDistance)
				{
					if(_touchOffset<0)
					{
						if(_loop || _curPage<_totalPage - 1)
						{
							setPage(_curPage + 1);
							_touchOffset=0;
						}
					}
					else if(_touchOffset>0)
					{
						if(_loop || _curPage>0)
						{
							setPage(_curPage - 1);
							_touchOffset=0;
						}
					}
					else
					{
						checkAndBack();
					}
				}
				else
				{
					checkAndBack();
				}
			}

			_isTouching=false;
			_hasScroll=false;
		}

		public void OnDrag(PointerEventData eventData)
		{
			onTouchMove(eventData);
		}

		public void OnBeginDrag(PointerEventData eventData)
		{
			onTouchMove(eventData);
		}

		private void onTouchMove(PointerEventData eventData)
		{
			if(!_touchAble)
				return;

			if(eventData.pointerId!=_touchId)
				return;

			var pt=eventData.position;

			float offset;
			if(_scrollType==SScrollType.Vertical)
			{
				offset=pt.y - _lastTouchPos;
				_lastTouchPos=pt.y;
				_touchOffset-=offset;
			}
			else
			{
				offset=pt.x - _lastTouchPos;
				_lastTouchPos=pt.x;
				_touchOffset+=offset;
			}

			_touchD=-offset;

			if(_changePageTweenIndex!=-1)
				return;

			if(!_hasScroll)
			{
				_hasScroll=true;

				doCancelAction(eventData);
			}

			setScrollPos(_scrollPos + getAddPos(_touchD * _scaleRadio));

			//检测翻页
			if(Math.Abs(_touchOffset) > _autoChangePageDistance)
			{
				if(_touchOffset < 0)
				{
					if(_loop || _curPage<_totalPage - 1)
					{
						setPage(_curPage + 1);
						_touchOffset = 0;
					}
				}

				if(_touchOffset > 0)
				{
					if(_loop || _curPage>0)
					{
						setPage(_curPage - 1);
						_touchOffset = 0;
					}
				}
			}
		}

		/**
		 * 检测当前位置并执行回位操作
		 */
		private void checkAndBack()
		{
			killScrollTween();

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
						_tweenIndex=Tween.normal.create(_scrollPos,targetPos,tweenTime,setScrollPos,killScrollTween);
					}
					else
					{
						setScrollPos(targetPos);
					}
				}
			}
			else
			{
				setPage(_curPage);
			}

			_hasScroll=false;
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

		private void killScrollTween()
		{
			if(_tweenIndex!=-1)
			{
				Tween.normal.kill(_tweenIndex);
				_tweenIndex=-1;
			}
		}

		private void killChangePageTween()
		{
			if(_changePageTweenIndex!=-1)
			{
				Tween.normal.kill(_changePageTweenIndex);
				_changePageTweenIndex=-1;
			}
		}

		/** 获取距离基础点最近的点 */
		private float getNearPos(float basePos,params float[] args)
		{
			if(args.Length==0)
				return basePos;

			float offset=args[0] - basePos;
			float absOffset=Math.Abs(offset);
			for(int i=1;i<args.Length;i++)
			{
				float tmpOffset=args[i] - basePos;
				float tmpAbsOffset=Math.Abs(tmpOffset);
				if(tmpAbsOffset<absOffset)
				{
					offset=tmpOffset;
					absOffset=tmpAbsOffset;
				}
			}

			return offset + basePos;
		}

		private void setChangePageDistancePer(float value)
		{
			float pageDistance;
			if(_scrollType==SScrollType.Vertical)
			{
				pageDistance=_height;
			}
			else
			{
				pageDistance=_width;
			}

			_changePageDistance=pageDistance * value;
		}

		private void setAutoChangePageDistancePer(float value)
		{
			float pageDistance;
			if(_scrollType==SScrollType.Vertical)
			{
				pageDistance=_height;
			}
			else
			{
				pageDistance=_width;
			}

			_autoChangePageDistance=pageDistance * value;
		}
	}
}