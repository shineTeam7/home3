package com.home.shine.tool;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;

import java.util.Arrays;

/** 分段数据工具(每一段是min<=value<max,最后一段为<=max) */
public class SectionTool<T>
{
	private SList<SectionObj> _list=new SList<>();
	
	private boolean _leftFree;
	
	private int[] _keys;
	private T[] _values;
	
	public void put(int min,int max,T obj)
	{
		SectionObj oo=new SectionObj();
		oo.min=min;
		oo.max=max;
		oo.obj=obj;
		
		_list.add(oo);
	}
	
	private int compare(SectionObj obj1,SectionObj obj2)
	{
		return Integer.compare(obj1.min,obj2.min);
	}
	
	/** 构造 */
	public void make()
	{
		_list.sort(this::compare);
		
		if(_list.isEmpty())
		{
			Ctrl.errorLog("SectionTool不能没有数据");
			return;
		}
		
		int len=_list.size();
		
		_keys=new int[len+1];
		_values=(T[])new Object[len+1];
		
		if(_list.get(0).min==-1)
		{
			_list.get(0).min=0;
			_leftFree=true;
		}
		
		if(_list.getLast().max==-1)
		{
			_list.getLast().max=Integer.MAX_VALUE;
			_values[len]=_list.getLast().obj;
		}
		else
		{
			_values[len]=null;
		}
		
		_keys[len]=_list.getLast().max;
		
		SectionObj obj;
		
		for(int i=0;i<len;i++)
		{
			obj=_list.get(i);
			
			_keys[i]=obj.min;
			_values[i]=obj.obj;
			
			if(i<len-1)
			{
				if(obj.max<_list.get(i+1).min)
				{
					Ctrl.throwError("配置表错误,max比min小"+_list.get(i+1).min+" "+obj.max);
				}
			}
		}
	}
	
	/** 获取value对应的key */
	public T get(int key)
	{
		int index=Arrays.binarySearch(_keys,key);
		
		if(index>=0)
		{
			int last=_keys.length-1;
			
			if(index==last)
			{
				//结尾的算<=
				if(key==_keys[last])
				{
					return _values[last-1];
				}
			}
			
			return _values[index];
		}
		else
		{
			if(index==-1)
			{
				return _leftFree ? _values[0] : null;
			}
			
			index=-index-2;
			
			if(index>=_values.length)
				index=_values.length-1;
			
			return _values[index];
		}
	}
	
	private class SectionObj
	{
		public int min;
		public int max;
		
		public T obj;
	}
}
