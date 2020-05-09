package com.home.shineTool.dataEx;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shineTool.tool.base.RecordDataClassInfo;

/** 数据记录总数据 */
public class DataRecordTotalData
{
	/** 类字典 */
	public SMap<String,RecordDataClassInfo> clsDic=new SMap<>();
	
	
	public void readBytes(BytesReadStream stream)
	{
		int len=stream.readLen();
		
		for(int i=0;i<len;i++)
		{
			RecordDataClassInfo cls=new RecordDataClassInfo();
			cls.readBytes(stream);
			
			clsDic.put(cls.clsQName,cls);
		}
	}
	
	public void writeBytes(BytesWriteStream stream)
	{
		stream.writeLen(clsDic.size());
		
		SList<String> sortedKeyList=clsDic.getSortedKeyList();
		
		for(String v:sortedKeyList)
		{
			RecordDataClassInfo cls=clsDic.get(v);
			
			cls.writeBytes(stream);
		}
	}
}
