package com.home.commonGame.utils;

import com.home.commonBase.global.CommonSetting;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;

/** common游戏逻辑方法 */
public class GameUtils
{
	/** 随机种子组 */
	public static void randomSeeds(int[] arr)
	{
		int len=arr.length;
		
		for(int i=len-1;i>=0;--i)
		{
		    arr[i]=i;
		}
		
		int index;
		int temp;
		
		for(int i=len-1;i>0;--i)
		{
			temp=arr[index=MathUtils.randomInt(i+1)];
			arr[index]=arr[i];
			arr[i]=temp;
		}
	}
	
	/** 从数组中随机指定数量数据 */
	public static <T> SList<T> randomDataList(SList<T> list,int num)
	{
		IntList indexList=new IntList(list.size());
		
		for(int i=0;i<list.length();++i)
		{
			indexList.add(i);
		}
		
		IntList resList=new IntList(num);
		
		for(int i=0;i<num;i++)
		{
			if(indexList.length()==0)
				break;
			
			int index = MathUtils.randomRange(0,indexList.length());
			resList.add(indexList.get(index));
			indexList.remove(index);
		}
		
		SList<T> resultList = new SList<>(indexList.length());
		
		for(int i=0;i<resList.length();++i)
		{
			resultList.add(list.get(resList.get(i)));
		}
		
		return resultList;
	}
}
