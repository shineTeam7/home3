using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 原生UI基类
/// </summary>
public class NatureUIBase
{
	private bool _inited=false;
	protected bool _isShow=false;

	protected GameObject _gameObj;

	public void init()
	{
		if(_inited)
			return;

		_inited=true;

		_gameObj=getGameObject();

		if(_gameObj==null)
		{
			Ctrl.throwError("缺少gameObject");
		}

		_gameObj.SetActive(true);

		onInit();

		_gameObj.SetActive(false);
	}

	protected virtual GameObject getGameObject()
	{
		return null;
	}

	protected virtual void onInit()
	{

	}

	public void initAndShow()
	{
		init();
		show();
	}

	public void showOrHide()
	{
		if(_isShow)
		{
			hide();
		}
		else
		{
			show();
		}
	}

	/** 当前是否显示 */
	public bool isShow()
	{
		return _isShow;
	}

	public void show()
	{
		if(_isShow)
			return;

		_isShow=true;

		_gameObj.SetActive(true);
		onShow();
	}

	public void hide()
	{
		if(!_isShow)
			return;

		_isShow=false;
		_gameObj.SetActive(false);

		onHide();
	}

	protected virtual void onShow()
	{

	}

	protected virtual void onHide()
	{

	}
}