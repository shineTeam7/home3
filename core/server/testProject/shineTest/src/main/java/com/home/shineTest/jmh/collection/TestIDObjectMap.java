package com.home.shineTest.jmh.collection;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.home.shineTest.control.TestControl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.home.shineTest.control.TestSetting;
import com.home.shine.support.collection.SMap;

@State(Scope.Thread)
public class TestIDObjectMap
{
	private Object _re=null;
	
	@Benchmark
	/** 原生Map */
	public Object testNature()
	{
		Map<Object,Object> map=new HashMap<>();
		
		int len=TestSetting.Len;
		
		Object obj;
		Object[] objs=TestSetting.objs;
		
		for(int i=0;i<len;++i)
		{
			obj=objs[i];
			map.put(obj,obj);
		}
		
		for(int i=0;i<len;++i)
		{
			_re=map.get(objs[i]);
		}
		
		map.forEach((k,v)->{
			_re=v;
		});
		
		return _re;
	}
	
	@Benchmark
	/** 原生Map */
	public Object testNature2()
	{
		Map<Object,Object> map=new IdentityHashMap<>();
		
		int len=TestSetting.Len;
		
		Object obj;
		Object[] objs=TestSetting.objs;
		
		for(int i=0;i<len;++i)
		{
			obj=objs[i];
			map.put(obj,obj);
		}
		
		for(int i=0;i<len;++i)
		{
			_re=map.get(objs[i]);
		}
		
		map.forEach((k,v)->{
			_re=v;
		});
		
		return _re;
	}
	
//	@Benchmark
//	/** fastUtil Map */
//	public Object testFastUtil()
//	{
//		Object2ObjectOpenHashMap<Object,Object> map=new Object2ObjectOpenHashMap<>();
//		
//		int len=TestSetting.Len;
//		
//		Object obj;
//		Object[] objs=TestSetting.objs;
//		
//		for(int i=0;i<len;++i)
//		{
//			obj=objs[i];
//			map.put(obj,obj);
//		}
//		
//		for(int i=0;i<len;++i)
//		{
//			_re=map.get(objs[i]);
//		}
//		
//		map.forEach((k,v)->{
//			_re=v;
//		});
//		
//		return _re;
//	}
	
//	@Benchmark
//	/** kolo生成Map */
//	public Object testCustomKolo()
//	{
//		TestMap<Object,Object> map=TestMap.withExpectedSize(0);
//		
//		int len=TestSetting.Len;
//		
//		Object obj;
//		Object[] objs=TestSetting.objs;
//		
//		for(int i=0;i<len;++i)
//		{
//			obj=objs[i];
//			map.justPut(obj,obj);
//		}
//		
//		for(int i=0;i<len;++i)
//		{
//			_re=map.get(objs[i]);
//		}
//		
//		map.forEach((k,v)->{
//			_re=v;
//		});
//		
//		return _re;
//	}
	
	@Benchmark
	/** kolo生成Map */
	public Object testMyKolo()
	{
		SMap<Object,Object> map=new SMap<>();
		
		int len=TestSetting.Len;
		
		Object obj;
		Object[] objs=TestSetting.objs;
		
		for(int i=0;i<len;++i)
		{
			obj=objs[i];
			map.put(obj,obj);
		}
		
		for(int i=0;i<len;++i)
		{
			_re=map.get(objs[i]);
		}
		
		Object[] table=map.getTable();
		
		for(int i=table.length-2;i>=0;i-=2)
		{
			if(table[i]!=null)
			{
				_re=table[i+1];
			}
		}
		
//		map.forEach((k,v)->{
//			_re=v;
//		});
		
		return _re;
	}
	
}
