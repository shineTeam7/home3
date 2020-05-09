package com.home.commonBase.dataEx.role;

import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.SList;

/** 状态统计信息 */
public class StatusCalculateInfo
{
	public int size;
	
	public int[] allList;
	
	/** 全部可能推送组 */
	public int[] allMaybeSendList;
	/** 全部可能推送组 */
	public boolean[] allMaybeSendSet;
	
	/** 开关推送自己组 */
	public int[] sendSelfList;
	/** 开关推送自己组 */
	public boolean[] sendSelfSet;
	
	/** 推送其他角色组 */
	public int[] sendOtherList;
	/** 推送其他角色组 */
	public boolean[] sendOtherSet;
	
	/** 初始化 */
	public void init(SList<StatusOneInfo> list,int size)
	{
		this.size=size;
		
		IntList tAllList=new IntList();
		//推送组
		IntList tAllMaybeSendList=new IntList();
		allMaybeSendSet=new boolean[size];
		
		IntList tSendSelfList=new IntList();
		sendSelfSet=new boolean[size];
		
		IntList tSendOtherList=new IntList();
		sendOtherSet=new boolean[size];
		
		StatusOneInfo[] values=list.getValues();
		StatusOneInfo v;
		
		for(int i=0,len=list.size();i<len;++i)
		{
			v=values[i];
			
			int type=v.id;
			
			tAllList.add(type);
			
			if(v.needSendSelf)
			{
				tSendSelfList.add(type);
				sendSelfSet[type]=true;
			}
			
			if(v.needSendOther)
			{
				tSendOtherList.add(type);
				sendOtherSet[type]=true;
			}
			
			if(v.needSendSelf || v.needSendOther)
			{
				tAllMaybeSendList.add(type);
				allMaybeSendSet[type]=true;
			}
		}
		
		allList=tAllList.toArray();
		allMaybeSendList=tAllMaybeSendList.toArray();
		sendSelfList=tSendSelfList.toArray();
		sendOtherList=tSendOtherList.toArray();
	}
}
