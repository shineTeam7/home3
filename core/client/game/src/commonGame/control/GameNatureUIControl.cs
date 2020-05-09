using ShineEngine;
using UnityEngine;

/// <summary>
/// 游戏原始UI控制
/// </summary>
public class GameNatureUIControl
{
	protected Reporter _reporter;

	private GMCommandUI _gmCommandUI;
	/** 选择服务器 */
	private SelectServerUI _selectServerUI;

	public void init()
	{
		if(CommonSetting.useDebug)
		{
			_gmCommandUI=new GMCommandUI();
			_gmCommandUI.init();

			// Ctrl.setPrintFunc(_gmCommandUI.onPrint);
		}

		_selectServerUI=new SelectServerUI();
	}
	
	public virtual void refreshReporter()
	{
		_reporter=ShineSetup.getRoot().GetComponent<Reporter>();

		if(CommonSetting.useReporter)
		{
			if(_reporter==null)
				Ctrl.throwError("不该找不到Reporter");
		}
		else
		{
			//为了恢复
			if(_reporter!=null)
			{
				GameObject.DestroyImmediate(_reporter);
				_reporter=null;
			}
		}
	}

	/** 获取Reporter */
	public Reporter getReporter()
	{
		return _reporter;
	}

	public GMCommandUI getGMCommandUI()
	{
		return _gmCommandUI;
	}

	public SelectServerUI getSelectServerUI()
	{
		return _selectServerUI;
	}
}