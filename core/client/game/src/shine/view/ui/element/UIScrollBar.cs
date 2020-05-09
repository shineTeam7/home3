using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	public class UIScrollBar:UIObject
	{
		/** 是否禁止进度回调 */
		private bool _noProgressCallBack;

		/** 进度条数值改变回调 */
		private Action<float> _changeProgressAction;

		private Scrollbar _scrollbar;

		public UIScrollBar()
		{
			_type=UIElementType.ScrollBar;
		}

		public Scrollbar scrollbar
		{
			get {return _scrollbar;}
		}

		public override void init(GameObject obj)
		{
			base.init(obj);

			_scrollbar=gameObject.GetComponent<Scrollbar>();

			_scrollbar.onValueChanged.AddListener(onProgressChanged);
		}

		public float value
		{
			get {return _scrollbar.value;}
			set {_scrollbar.value=value;}
		}

		public void setValueWithoutCallback(float value)
		{
			_noProgressCallBack=true;
			_scrollbar.value=value;
			_noProgressCallBack=false;
		}

		public float size
		{
			get {return _scrollbar.size;}
			set {_scrollbar.size=value;}
		}

		public int numberOfSteps
		{
			get {return _scrollbar.numberOfSteps;}
			set {_scrollbar.numberOfSteps=value;}
		}

		public void setChangeProgressAction(Action<float> action)
		{
			_changeProgressAction=action;
		}

		private void onProgressChanged(float value)
		{
			if(_changeProgressAction!=null && !_noProgressCallBack)
				_changeProgressAction(value);
		}
	}
}