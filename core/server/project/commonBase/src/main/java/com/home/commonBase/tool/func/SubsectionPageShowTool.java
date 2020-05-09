package com.home.commonBase.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.system.KeyData;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;

/** 翻页显示插件 */
public class SubsectionPageShowTool extends FuncTool
{
	protected ISubsectionRankTool _rankTool;

	protected int _roleGroupFuncID=-1;

	private IntObjectMap<SList<SList<? extends KeyData>>> _dataListListMap;

	protected int _showMaxNum;
	protected int _eachPageShowNum;
	protected int _pageNum;

	public SubsectionPageShowTool(int funcID, int showMaxNum, int eachPageShowNum)
	{
		super(FuncToolType.SubsectionPageShow,funcID);
		
		_showMaxNum=showMaxNum;
		_eachPageShowNum=eachPageShowNum;
		_pageNum=MathUtils.divCeil(showMaxNum,eachPageShowNum);
	}
	
	/** 绑定数据列表 */
	public void setDataList(IntObjectMap<SList<SList<? extends KeyData>>> dataListListMap)
	{
		_dataListListMap=dataListListMap;
	}
	
	/** 绑定排行插件 */
	public void setRankTool(ISubsectionRankTool rankTool)
	{
		_rankTool=rankTool;
	}
	
	/** 绑定玩家群功能ID */
	public void setRoleGroupFuncID(int funcID)
	{
		_roleGroupFuncID=funcID;
	}
	
	/** 获取数据list源 */
	protected SList<? extends KeyData> getDataList(int subsectionIndex,int subsectionSubIndex,int arg)
	{
		if(_rankTool!=null)
		{
			return _rankTool.getList(subsectionIndex,subsectionSubIndex);
		}
		else if(_dataListListMap!=null)
		{
			return _dataListListMap.get(subsectionIndex).get(subsectionSubIndex);
		}
		
		return null;
	}
	
	/** 获取某页显示(数据为clone的) */
	public final SList<KeyData> getPageShowList(int subsectionIndex,int subsectionSubIndex,int page,int arg)
	{
		SList<KeyData> reList=new SList<>(KeyData[]::new);
		
		SList<? extends KeyData> dataList=getDataList(subsectionIndex,subsectionSubIndex,arg);
		
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
		if(_dataListListMap!=null)
			_dataListListMap.clear();
	}
}
