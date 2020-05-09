package com.home.commonGame.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonGame.net.request.func.rank.FuncReGetSelfPageShowRequest;
import com.home.shine.data.BaseData;
import com.home.shine.support.collection.SList;

/** 角色自身翻页显示插件 */
public class PlayerSelfPageShowTool extends PlayerFuncTool
{
	/** 每页显示数 */
	private int _eachPageShowNum;
	/** 数据 */
	private SList<? extends BaseData> _list;
	/** 是否获取了所有 */
	private boolean _isGetAll=false;
	
	public PlayerSelfPageShowTool(int funcID,int eachPageShowNum)
	{
		super(FuncToolType.SelfPageShow,funcID);
		
		_eachPageShowNum=eachPageShowNum;
	}
	
	/** 设置数据列表(引用赋值) */
	public void setDataList(SList<? extends BaseData> list)
	{
		_list=list;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_list=null;
		_isGetAll=false;
	}
	
	/** 获取翻页显示 */
	public void getPageShow(int page)
	{
		SList<BaseData> list=null;
		
		int size=_list.size();
		
		int len=(page+1)*_eachPageShowNum;
		
		for(int i=page*_eachPageShowNum;i<len;++i)
		{
			if(i>=size)
			{
				_isGetAll=true;
				break;
			}
			
			if(list==null)
			{
				list=new SList<>();
			}
			
			list.add(_list.get(i));
		}
		
		me.send(FuncReGetSelfPageShowRequest.create(_funcID,page,list));
	}
	
	/** 是否获取了全部 */
	public boolean isGetAll()
	{
		return _isGetAll;
	}
}
