using System;
using UnityEngine;
using ShineEngine;
using UnityEngine.UI;
#if UNITY_EDITOR

#endif

/// <summary>
/// UI基类
/// </summary>
public class UIBase:UILogicBase
{
	/** ui配置 */
	protected UIConfig _config;

	/** 析构保持计时索引 */
	private int _disposeTimeOutIndex=-1;

	/** 遮罩(有可能为空，以后改实现方式) */
	protected GameObject _modelBack;
	
	/** 隐藏时是否需要dispose */
	private bool _hideOverNeedDispose=true;

	public UIBase()
	{

	}

	public override void construct()
	{
		base.construct();

		_config=UIConfig.get(id);
	}

	public UIConfig getUIConfig()
	{
		return _config;
	}
	
	/** 获取模型(基类) */
	public UIModel getSModel()
	{
		return _smodel;
	}
	
	protected void hideOverNeedDispose(bool value)
	{
		_hideOverNeedDispose = value;
	}
	
	protected override void beforeExecuteShow()
	{
		load(_config.resourceNameT);
	}

	protected override void initModelObject()
	{
		_modelObject.GetComponent<RectTransform>().SetParent(UIControl.getUIRoot().transform);
		_modelObject.layer=UIControl.getUILayer();
		_modelObject.transform.localPosition=Vector3.zero;//归零
		_modelObject.transform.localScale = Vector3.one;
	}

	protected override void preShow()
	{
		base.preShow();

		if(_disposeTimeOutIndex!=-1)
		{
			TimeDriver.instance.clearTimeOut(_disposeTimeOutIndex);
			_disposeTimeOutIndex=-1;
		}
	}

	protected override void addShow()
	{
		if(_modelObject!=null)
		{
			_modelObject.SetActive(true);
			GameC.ui.addRealShow(this);
		}
	}

	protected override void removeShow()
	{
		if(_modelObject!=null)
		{
			_modelObject.SetActive(false);
			GameC.ui.removeRealShow(this);
		}
	}

	protected override void hideOver()
	{
		base.hideOver();

		//不需要return
		if (!_hideOverNeedDispose)
		{
			return;
		}

		if(ShineSetting.uiDisposeKeepTime==0)
		{
			doDispose();
		}
		else if(ShineSetting.uiDisposeKeepTime>0)
		{
			_disposeTimeOutIndex=TimeDriver.instance.setTimeOut(doDispose,ShineSetting.uiDisposeKeepTime*1000);
		}
	}

	protected override void disposeOver()
	{
		base.disposeOver();

		_disposeTimeOutIndex=-1;


	}
}