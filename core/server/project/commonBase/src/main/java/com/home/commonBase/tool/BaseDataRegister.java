package com.home.commonBase.tool;

import com.home.commonBase.tool.generate.BaseDataMaker;
import com.home.commonBase.tool.generate.CenterGlobalListDataMaker;
import com.home.commonBase.tool.generate.CenterGlobalPartDataMaker;
import com.home.commonBase.tool.generate.GameGlobalListDataMaker;
import com.home.commonBase.tool.generate.GameGlobalPartDataMaker;
import com.home.commonBase.tool.generate.PlayerListClientDataMaker;
import com.home.commonBase.tool.generate.PlayerListDataMaker;
import com.home.commonBase.tool.generate.PlayerPartClientDataMaker;
import com.home.commonBase.tool.generate.PlayerPartDataMaker;
import com.home.shine.control.BytesControl;

public class BaseDataRegister extends DataRegister
{
	@Override
	public void regist()
	{
		super.regist();
		
		add(new BaseDataMaker());
		
		add(new CenterGlobalListDataMaker());
		add(new CenterGlobalPartDataMaker());
		
		add(new GameGlobalListDataMaker());
		add(new GameGlobalPartDataMaker());
		
		add(new PlayerListDataMaker());
		add(new PlayerPartDataMaker());
		
		add(new PlayerListClientDataMaker());
		add(new PlayerPartClientDataMaker());
	}
}
