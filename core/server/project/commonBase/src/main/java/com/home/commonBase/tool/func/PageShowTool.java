package com.home.commonBase.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.system.KeyData;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;

/** 翻页显示插件 */
public class PageShowTool extends FuncTool
{
	protected IRankTool _rankTool;
	
	protected int _roleGroupFuncID=-1;
	
	private SList<? extends KeyData> _dataList;
	
	protected int _showMaxNum;
	protected int _eachPageShowNum;
	protected int _pageNum;
	
	public PageShowTool(int funcID,int showMaxNum,int eachPageShowNum)
	{
		super(FuncToolType.PageShow,funcID);
		
		_showMaxNum=showMaxNum;
		_eachPageShowNum=eachPageShowNum;
		_pageNum=MathUtils.divCeil(showMaxNum,eachPageShowNum);
	}
	
	/** 绑定数据列表 */
	public void setDataList(SList<? extends KeyData> list)
	{
		_dataList=list;
	}
	
	/** 绑定排行插件 */
	public void setRankTool(IRankTool rankTool)
	{
		_rankTool=rankTool;
	}
	
	/** 绑定玩家群功能ID */
	public void setRoleGroupFuncID(int funcID)
	{
		_roleGroupFuncID=funcID;
	}
	
	/** 获取数据list源 */
	protected SList<? extends KeyData> getDataList(int arg)
	{
		if(_rankTool!=null)
		{
			return _rankTool.getList();
		}
		else if(_dataList!=null)
		{
			return _dataList;
		}
		
		return null;
	}
	
	/** 获取某页显示(数据为clone的) */
	public final SList<KeyData> getPageShowList(int page,int arg)
	{
		SList<KeyData> reList=new SList<>(KeyData[]::new);
		
		SList<? extends KeyData> dataList=getDataList(arg);
		
		if(dataList==null)
		{
			Ctrl.errorLog("不该找不到数据列表",_funcID,page);
			return reList;
		}
		
		int len=MathUtils.min(dataList.size(),(page+1)*_eachPageShowNum,_showMaxNum);
		
		KeyData[] values=dataList.getValues();
		
		for(int i=page*_eachPageShowNum;i<len;++i)
		{
			reList.add((KeyData)values[i].clone());
		}
		
		return reList;
	}
	
	/** 清空数据 */
	public void clear()
	{
		if(_dataList!=null)
			_dataList.clear();
	}
}
