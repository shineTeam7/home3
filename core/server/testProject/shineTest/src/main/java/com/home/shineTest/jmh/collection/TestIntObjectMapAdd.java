package com.home.shineTest.jmh.collection;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.home.shineTest.control.TestSetting;
import com.home.shineTest.kolo.TestMapForIntObj;
import com.home.shine.data.DIntData;
import com.home.shine.support.collection.IntObjectMap;

import java.util.HashMap;
import java.util.Map;

@State(Scope.Thread)
public class TestIntObjectMapAdd
{
	private int _dv;
	
	@Benchmark
	public Object testNature()
	{
		Map<Integer,Object> map=new HashMap<>();

		int len=TestSetting.Len;
		Object[] objs=TestSetting.objs;

		for(int i=0;i<len;++i)
		{
			map.put(i,objs[i]);
		}

		//for(int i=0;i<len;++i)
		//{
		//	_dv=map.get(i);
		//}

		return 1;
	}
//	
//	@Benchmark
//	public Object testFastUtil()
//	{
//		Int2ObjectOpenHashMap<DIntData> map=new Int2ObjectOpenHashMap<>();
//
//		int len=TestSetting.Len;
//
//		DIntData[] datas=TestSetting.datas;
//
//		for(int i=0;i<len;++i)
//		{
//			map.put(i,datas[i]);
//		}
//
//		for(int i=0;i<len;++i)
//		{
//			_dv+=map.get(i).value;
//		}
//
//		return _dv;
//	}
	
	@Benchmark
	public Object testCustomKolo()
	{
		TestMapForIntObj<DIntData> map=TestMapForIntObj.withExpectedSize(0);
		
		
		int len=TestSetting.Len;

		DIntData[] datas=TestSetting.datas;

		for(int i=0;i<len;++i)
		{
			map.justPut(i,datas[i]);
		}

		for(int i=0;i<len;++i)
		{
			_dv+=map.get(i).value;
		}

		return _dv;
	}
	
	@Benchmark
	public int testMyKolo()
	{
		int len=TestSetting.Len;
		
		IntObjectMap<DIntData> map=new IntObjectMap<>();
		
		DIntData[] datas=TestSetting.datas;
		
		for(int i=0;i<len;++i)
		{
			map.put(i,datas[i]);
		}
		
		//for(int i=0;i<len;++i)
		//{
		//	_dv+=map.get(i).value;
		//}
		
//		Object[] values=map.getValues();
//		
//		for(int i=values.length-1;i>=0;--i)
//		{
//			if(values[i]!=null)
//			{
//				_dv+=((DIntData)values[i]).value;
//			}
//		}
		
		return _dv;
	}
}
