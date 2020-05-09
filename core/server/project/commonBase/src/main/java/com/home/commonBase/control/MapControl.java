package com.home.commonBase.control;

import com.home.commonBase.config.game.enumT.MapBlockTypeConfig;
import com.home.commonBase.config.other.GridMapInfoConfig;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;

/** 地图控制相关 */
public class MapControl
{
	/** 计算连通区时间 */
	public static long loadTime=0;
	
	/** 计算连通区 */
	public static int[] calculateGridArea(GridMapInfoConfig config,int moveType)
	{
		long t=Ctrl.getTimer();
		
		int areaIndex=0;
		byte[] arr=config.mainGrids;
		int[] re=new int[arr.length];
		IntObjectMap<IntList> areaDic=new IntObjectMap<>(IntList[]::new);
		
		
		IntList ss;
		int index;
		int index2;
		int aIndex2;
		
		int width=config.width;
		int height=config.height;
		int heightW=config.heightW;
		
		boolean[] allows=MapBlockTypeConfig.allowDicT[moveType];
		
		for(int i=0;i<width;++i)
		{
			for(int j=0;j<height;++j)
			{
				index=i<<heightW | j;
				
				if(allows[arr[index]])
				{
					int belong=-1;
					
					if(j==0 || !allows[arr[index-1]])
					{
						//新建
						belong=++areaIndex;
						ss=new IntList();
						
						areaDic.put(belong,ss);
						ss.add(index);
						re[index]=belong;
					}
					else
					{
						//添加到上个归属
						belong=re[index-1];
						areaDic.get(belong).add(index);
						re[index]=belong;
					}
					
					//不是起始行
					if(i>0)
					{
						for(int k=-1;k<=1;k++)
						{
							int m=j+k;
							
							if(m>=0 && m<height)
							{
								index2=(i-1)<<heightW | m;
								
								if(allows[arr[index2]])
								{
									aIndex2=re[index2];
									
									//添加归属
									if(belong<0)
									{
										belong=aIndex2;
										re[index]=belong;
										areaDic.get(belong).add(index);
									}
									//合并连通区
									else if(belong!=aIndex2)
									{
										IntList intList=areaDic.remove(belong);
										IntList intList2=areaDic.get(aIndex2);
										
										int[] values2=intList.getValues();
										int v2;
										
										for(int i2=0,len3=intList.size();i2<len3;++i2)
										{
											v2=values2[i2];
											
											re[v2]=aIndex2;
											intList2.add(v2);
										}
										
										belong=aIndex2;
									}
								}
							}
						}
					}
				}
			}
		}
		
		loadTime+=(Ctrl.getTimer()-t);
		
		return re;
	}
}
