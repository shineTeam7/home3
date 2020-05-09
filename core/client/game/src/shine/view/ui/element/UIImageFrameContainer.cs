using System;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 多帧图片容器
	/// </summary>
	public class UIImageFrameContainer:UIObject
	{
		private ImageFrameContainer _imageFrameContainer;

		public UIImageFrameContainer()
		{
			_type=UIElementType.ImageFrameContainer;
		}

		public ImageFrameContainer imageFrameContainer
		{
			get {return _imageFrameContainer;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_imageFrameContainer=obj.GetComponent<ImageFrameContainer>();

			//临时初始化内容
			_frameTotal=_imageFrameContainer.getFrameTotal();
		}

		public void setFrame(int index)
		{
			_imageFrameContainer.frameTo(index);
		}

		//-------------------------------临时动画代码---------------------------------
		/** 时间控制索引 */
		private int _timeIndex=-1;

		/** 当前动画帧数（从0开始，最大值为frameTotal） */
		protected int _frame=-1;

		/** 动画总帧数(也就是最后一帧的帧号，如果一个动画时间刚好1秒，则frameTotal=fps) */
		protected int _frameTotal;

		/** 动画帧率 */
		protected float _fps=30f;

		/** 帧间隔 */
		protected float _frameDelay;

		/** 帧累积时间 */
		protected float _frameTime;

		/** 是否停到最后一帧 */
		protected bool _isStopAtLast;

		/** 每帧进入回调 */
		private Action<UIImageFrameContainer,int> _frameEnterCallBack;

		/** 每帧离开回调 */
		private Action<UIImageFrameContainer,int> _frameEndCallBack;

		/** 动画完成回调 */
		private Action<UIImageFrameContainer> _completeCallBack;

		public void setFrameEnterCallBack(Action<UIImageFrameContainer,int> action)
		{
			_frameEnterCallBack=action;
		}

		public void setFrameEndCallBack(Action<UIImageFrameContainer,int> action)
		{
			_frameEndCallBack=action;
		}

		public void setCompleteCallBack(Action<UIImageFrameContainer> action)
		{
			_completeCallBack=action;
		}

		private void startFrame()
		{
			if(_timeIndex!=-1)
				return;

			_timeIndex=TimeDriver.instance.setFrame(onFrame);
		}

		private void clearFrame()
		{
			if(_timeIndex==-1)
				return;

			TimeDriver.instance.clearFrame(_timeIndex);
			_timeIndex=-1;
		}

		public bool isStop
		{
			get {return _timeIndex==-1;}
		}

		/// <summary>
		/// 播放动画
		/// </summary>
		/// <param name="isStopAtLast">是否停在最后一帧</param>
		public void play(bool isStopAtLast=false)
		{
			_isStopAtLast=isStopAtLast;
			startFrame();

			refreshShow();
		}

		/// <summary>
		/// 播放动画
		/// </summary>
		/// <param name="fps">帧率</param>
		/// <param name="isStopAtLast">是否停在最后一帧</param>
		public void play(float fps,bool isStopAtLast=false)
		{
			_isStopAtLast=isStopAtLast;
			startFrame();
			setFPS(fps);

			refreshShow();
		}

		/// <summary>
		/// 停止动画
		/// </summary>
		public void stop()
		{
			clearFrame();
		}

		/// <summary>
		/// 设置帧率
		/// </summary>
		/// <param name="fps">帧率</param>
		public void setFPS(float fps)
		{
			_fps=fps;
			resetFrameDelay();
		}

		/// <summary>
		/// 从某一帧开始播放
		/// </summary>
		/// <param name="index">帧数</param>
		/// <param name="isStopAtLast">是否停到最后一帧</param>
		public void gotoAndPlay(int index,bool isStopAtLast=false)
		{
			if(index<0)
				index=0;
			if(index>_frameTotal)
				index=_frameTotal;

			_frame=index;

			play(isStopAtLast);
		}

		/// <summary>
		/// 从某一帧开始播放
		/// </summary>
		/// <param name="index">帧数</param>
		/// <param name="fps">帧率</param>
		/// <param name="isStopAtLast">是否停到最后一帧</param>
		public void gotoAndPlay(int index,float fps=30f,bool isStopAtLast=false)
		{
			if(index<0)
				index=0;
			if(index>_frameTotal)
				index=_frameTotal;

			_frame=index;

			play(fps,isStopAtLast);
		}

		/// <summary>
		/// 停到某一帧
		/// </summary>
		/// <param name="index">帧数</param>
		public void gotoAndStop(int index)
		{
			if(index<0)
				index=0;
			if(index>_frameTotal)
				index=_frameTotal;

			_frame=index;

			clearFrame();
			refreshShow();
		}

		protected void onFrame(int interval)
		{
			if(isStop)
				return;

			if((_frameTime+=interval)>=_frameDelay)
			{
				_frameTime-=_frameDelay;

				if(_frame>=0)
					onFrameEnd(_frame);

				if(++_frame==_frameTotal)
				{
					onComplete();
					if(_isStopAtLast)
						stop();
					else
						_frame=0;
				}

				onFrameEnter(_frame);
			}

			refreshShow();
		}

		protected virtual void onFrameEnter(int frame)
		{
			if(_frameEnterCallBack!=null)
				_frameEnterCallBack(this,frame);
		}

		protected virtual void onFrameEnd(int frame)
		{
			if(_frameEndCallBack!=null)
				_frameEndCallBack(this,frame);
		}

		protected virtual void onComplete()
		{
			if(_completeCallBack!=null)
				_completeCallBack(this);
		}

		/** 根据当前动画时间刷新显示 */
		protected virtual void refreshShow()
		{
			_imageFrameContainer.frameTo(_frame);
		}

		private void resetFrameDelay()
		{
			_frameDelay=1000f / _fps;
		}

		//------------------------------临时动画代码结束-------------------------------
	}
}