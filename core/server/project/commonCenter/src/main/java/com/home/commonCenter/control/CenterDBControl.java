package com.home.commonCenter.control;

import com.home.commonBase.control.BaseDBControl;
import com.home.commonBase.control.BaseGameDBControl;
import com.home.commonBase.part.centerGlobal.list.CenterGlobalListData;
import com.home.commonBase.part.gameGlobal.list.GameGlobalListData;
import com.home.commonCenter.global.CenterC;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;

import java.sql.Connection;

public class CenterDBControl extends BaseGameDBControl
{
	private DBWriteTempData _tempData;
	
	public CenterDBControl()
	{
		
	}
	
	@Override
	protected void makeDBFilePath()
	{
		_dbFilePath=ShineGlobal.dbFilePath +"/center.bin";
	}
	
	@Override
	protected void toInit()
	{
		setURL(CenterC.server.getSelfInfo().mysql);
		_tempData=toCreateDBWriteTempData();
		super.toInit();
	}
	
	@Override
	protected void makeDBWrites(int index,boolean lastFinish)
	{
		if(!lastFinish)
		{
			//旧的clear
			_tempData.clear();
			//创建新的
			_tempData=toCreateDBWriteTempData();
		}
		else
		{
		
		}
		
		DBWriteTempData tempData=_tempData;
		tempData.index=index;
		
		//global
		makeDBWriteGlobal(tempData);
		//roleSocial
		makeDBWriteRoleSocials(tempData,CenterC.global.social.getRoleSocialDataDic());
		
		//直接下一段
		makeDBWriteOver(index,tempData);
	}
	
	/** 构造global的写入 */
	protected void makeDBWriteGlobal(DBWriteTempData tempData)
	{
		//全局表
		tempData.globalListData=CenterC.global.createListData();
		CenterC.global.writeListData((CenterGlobalListData)tempData.globalListData);
	}
	
	@Override
	protected void toWriteDB(BaseDBWriteTempData tData,Connection con)
	{
		super.toWriteDB(tData,con);
		
	}
	
	@Override
	protected void toWriteEndDB(BaseDBWriteTempData tData)
	{
		super.toWriteEndDB(tData);
		
	}
	
	@Override
	protected void toWriteFile(BytesWriteStream stream,Object obj)
	{
		super.toWriteFile(stream,obj);
	}
	
	@Override
	protected void toReadFile(BytesReadStream stream)
	{
		super.toReadFile(stream);
	}
	
	protected DBWriteTempData toCreateDBWriteTempData()
	{
		return new DBWriteTempData();
	}
	
	/** db写入临时数据 */
	protected class DBWriteTempData extends BaseDBWriteTempData
	{
		
		@Override
		public void clear()
		{
			super.clear();
			
			
		}
	}
}
