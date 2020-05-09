using System;
using System.Collections.Generic;
using ShineEngine;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// 选择服务器UI
/// </summary>
public class SelectServerUI:NatureUIBase
{
	private Dropdown _dropdown;

	private SList<string> _nameList;
	private SList<string> _urlList;

	protected override GameObject getGameObject()
	{
		Transform transform=UIControl.getUIContainer().transform.Find("selectServerUI");

		if(transform==null)
		{
			Ctrl.throwError("找不到:selectServerUI");
			return null;
		}

		return transform.gameObject;
	}

	protected override void onInit()
	{
		base.onInit();

		_urlList=new SList<string>();
		_nameList=new SList<string>();

		List<Dropdown.OptionData> dataList=new List<Dropdown.OptionData>();

		for(int i=0,len=LocalSetting.serverList.size();i<len;i+=2)
		{
			dataList.Add(new Dropdown.OptionData(LocalSetting.serverList.get(i)));
			_nameList.add(LocalSetting.serverList.get(i));
			_urlList.add(LocalSetting.serverList.get(i+1));
		}

		Transform transform=_gameObj.transform;
		(_dropdown=transform.Find("dropList").GetComponent<Dropdown>()).options=dataList;
		transform.Find("sureBt").gameObject.AddComponent<PointerBehaviour>().onClick=onClick;

		string str=GameC.save.getString("lastSelectServer");

		if(!str.isEmpty())
		{
			int index;

			if((index=_nameList.indexOf(str))!=-1)
			{
				_dropdown.value=index;
			}
		}
	}

	protected void onClick()
	{
		string url=_urlList.get(_dropdown.value);
		string name=_nameList[_dropdown.value];

		Ctrl.print("选择了服务器:",_nameList[_dropdown.value],url);
		LocalSetting.loginHttpURL=url;

		GameC.save.setString("lastSelectServer",name);
		hide();

		GameC.mainLogin.selectServerOver();
	}
}