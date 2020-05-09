package com.home.shine.tool;

import com.home.shine.support.collection.inter.ICreateArray;

public class ArrayDic<T>
{
	private ICreateArray<T> _createArrFunc;
	/** 偏移 */
	public int offSet;
	
	/** 构造组 */
	public T[] list;
	
	public ArrayDic(ICreateArray<T> createFunc)
	{
		_createArrFunc=createFunc;
	}
	
	/** 通过id取得数据 */
	public T get(int key)
	{
		T[] temp;
		if((temp=list)==null)
			return null;
		
		int index=key - offSet;
		
		if(index<0 || index >= temp.length)
		{
			return null;
		}
		
		return temp[index];
	}
	
	/** 添加构造器 */
	public void addDic(ArrayDic<T> dic)
	{
		if(list==null)
		{
			list=dic.list;
			offSet=dic.offSet;
		}
		else
		{
			int off=Math.min(dic.offSet,offSet);
			int nowLen=offSet + list.length;
			int targetLen=dic.offSet + dic.list.length;
			int max=Math.max(nowLen,targetLen);
			
			//有变化
			if(!(off==offSet && max==nowLen))
			{
				T[] temp;
				
				if(_createArrFunc!=null)
				{
					temp=_createArrFunc.create(max - off);
				}
				else
				{
					temp=(T[])new Object[max - off];
				}
				
				System.arraycopy(list,0,temp,offSet-off,list.length);
				offSet=off;
				list=temp;
			}
			
			T[] arr=dic.list;
			T[] selfList=this.list;
			int arrOff=dic.offSet - offSet;
			
			for(int i=arr.length-1;i>=0;i--)
			{
				if(arr[i]!=null)
				{
					selfList[i+arrOff]=arr[i];
				}
			}
		}
	}
}
