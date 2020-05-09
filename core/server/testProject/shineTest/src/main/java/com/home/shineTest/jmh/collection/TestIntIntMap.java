package com.home.shineTest.jmh.collection;

import java.util.HashMap;
import java.util.Map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.home.shineTest.control.TestSetting;
import com.home.shineTest.kolo.TestMapForIntInt;
import com.home.shine.support.collection.IntIntMap;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/** 测IntIntMap的性能 */
@State(Scope.Thread)
public class TestIntIntMap
{
	@Benchmark
	public int testNature()
	{
		Map<Integer,Integer> map=new HashMap<>();
		
		int len=TestSetting.Len;
		
		for(int i=0;i<len;++i)
		{
			map.put(i,i);
		}
		
		int re=0;
		
		for(int i=0;i<len;++i)
		{
			re+=map.get(i);
		}
		
		return re;
	}
	
	@Benchmark
	public int testFastUtil()
	{
		Int2IntOpenHashMap map=new Int2IntOpenHashMap();
		
		int len=TestSetting.Len;
		
		for(int i=0;i<len;++i)
		{
			map.put(i,i);
		}
		
		int re=0;
		
		for(int i=0;i<len;++i)
		{
			re+=map.get(i);
		}
		
		return re;
	}
	
	@Benchmark
	public int testCustomKolo()
	{
		TestMapForIntInt map=TestMapForIntInt.withExpectedSize(0);
		
		int len=TestSetting.Len;
		
		for(int i=0;i<len;++i)
		{
			map.justPut(i,i);
		}
		
		int re=0;
		
		for(int i=0;i<len;++i)
		{
			re+=map.get(i);
		}
		
		return re;
	}
	
	@Benchmark
	public int testMyKolo()
	{
		IntIntMap map=new IntIntMap();
		
		int len=TestSetting.Len;
		
		for(int i=0;i<len;++i)
		{
			map.put(i,i);
		}
		
		int re=0;
		
		for(int i=0;i<len;++i)
		{
			re+=map.get(i);
		}
		
		return re;
	}
}
