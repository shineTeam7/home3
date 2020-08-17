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
}
