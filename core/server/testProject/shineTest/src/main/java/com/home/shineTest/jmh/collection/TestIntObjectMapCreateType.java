package com.home.shineTest.jmh.collection;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.home.shineTest.control.TestSetting;
import com.home.shine.support.collection.IntObjectMap;

@State(Scope.Thread)
public class TestIntObjectMapCreateType
{
	private static IntObjectMap<TestSetting> _map1=new IntObjectMap<>();
	
	private static IntObjectMap<TestSetting> _map2=new IntObjectMap<>(k->new TestSetting[k]);
	
	private static int _len=256;
	
	private static TestSetting _re=new TestSetting();
	
	static
	{
		for(int i=0;i<_len;++i)
		{
			_map1.put(i,new TestSetting());
			_map2.put(i,new TestSetting());
		}
	}
	
//	@Benchmark
	public void test1()
	{
		IntObjectMap<TestSetting> dic=new IntObjectMap<>();
		
		for(int i=0;i<_len;++i)
		{
			dic.put(i,new TestSetting());
		}
		
		for(int i=0;i<_len;++i)
		{
			_re=dic.get(i);
		}
	}
	
//	@Benchmark
	public void test2()
	{
		IntObjectMap<TestSetting> dic=new IntObjectMap<>(k->new TestSetting[k]);
		
		for(int i=0;i<_len;++i)
		{
			dic.put(i,new TestSetting());
		}
		
		for(int i=0;i<_len;++i)
		{
			_re=dic.get(i);
		}
	}
	
	@Benchmark
	public void testForeach1()
	{
		Object[] values=_map1.getValues();
		TestSetting v;
		
		for(int i=values.length-1;i>=0;--i)
		{
			if(values[i]!=null)
			{
				v=(TestSetting)values[i];
				_re=v;
			}
		}
	}
	
	@Benchmark
	public void testForeach2()
	{
		TestSetting[] values=_map2.getValues();
		TestSetting v;
		
		for(int i=values.length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				_re=v;
			}
		}
	}
	
	@Benchmark
	public void testForeach3()
	{
		TestSetting[] values=_map2.getValues();
		TestSetting v;
		
		for(int i=values.length-1;i>=0;--i)
		{
			if(values[i]!=null)
			{
				v=values[i];
				_re=v;
			}
		}
	}
}
