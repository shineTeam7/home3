package com.home.commonBase.dataEx.scene;

import com.home.commonBase.global.CommonSetting;
import com.home.shine.support.collection.IntObjectMap;

public class SocketAOICountData
{
	private IntObjectMap<UnitAOICountData> _checkDic=new IntObjectMap<>(UnitAOICountData[]::new);
	
	public IntObjectMap<UnitAOICountData> getCheckDic()
	{
		return _checkDic;
	}
	
	public UnitAOICountData getCountData(int instanceID)
	{
		UnitAOICountData data=_checkDic.get(instanceID);
		
		if(data==null)
		{
			_checkDic.put(instanceID,data=new UnitAOICountData());
		}
		
		return data;
	}
	
	//public void recordAddUnit(int instanceID)
	//{
	//	if(!CommonSetting.openAOICheck)
	//		return;
	//
	//
	//	getCountData(instanceID).addMsg(true,new Exception());
	//}
	//
	//public void recordRemoveUnit(int instanceID)
	//{
	//	if(!CommonSetting.openAOICheck)
	//		return;
	//
	//	getCountData(instanceID).addMsg(false,new Exception());
	//}
	//
	//public void recordAddAround(int instanceID)
	//{
	//	if(!CommonSetting.openAOICheck)
	//		return;
	//
	//
	//	getCountData(instanceID).addAround(true,new Exception());
	//}
	//
	//public void recordRemoveAround(int instanceID)
	//{
	//	if(!CommonSetting.openAOICheck)
	//		return;
	//
	//	getCountData(instanceID).addAround(false,new Exception());
	//}
	//
	//public void clearRecord()
	//{
	//	if(!CommonSetting.openAOICheck)
	//		return;
	//
	//	_checkDic.clear();
	//}
	//
	//public void clearMsg()
	//{
	//	if(!CommonSetting.openAOICheck)
	//		return;
	//
	//	if(!_checkDic.isEmpty())
	//	{
	//		UnitAOICountData[] values;
	//		UnitAOICountData v;
	//
	//		for(int i=(values=_checkDic.getValues()).length-1;i>=0;--i)
	//		{
	//			if((v=values[i])!=null)
	//			{
	//				v.clearMsg();
	//			}
	//		}
	//	}
	//}
}
