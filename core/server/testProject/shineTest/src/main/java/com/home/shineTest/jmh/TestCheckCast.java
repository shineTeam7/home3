package com.home.shineTest.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.home.shineTest.control.TestSetting;

/** 测试checkCast的开销,跟数组取值的区别(结果差别不大,大约10%) */
@State(Scope.Thread)
public class TestCheckCast
{
	private static Object[] _objs=new Object[10000];
	
	private static TestSetting _t1=new TestSetting();
	
	private static Object _t2=new TestSetting();
	
	static
	{
		int len=_objs.length;
		
		for(int i=0;i<len;i+=4)
		{
			_objs[i]=new TestSetting();
		}
	}
	
	@SuppressWarnings("unused")
	private static TestSetting _tt;
	
	@Benchmark
	public void test1()
	{
		//Object[] values=_objs;
		//
		//TestSetting tt;
		//
		//for(int i=values.length-1;i>=0;--i)
		//{
		//	tt=(TestSetting)values[i];
		//}
		
		_tt=_t1;
	}
	
	@Benchmark
	public void test2()
	{
		//Object[] values=_objs;
		//
		//TestSetting tt;
		//
		//for(int i=values.length-1;i>=0;--i)
		//{
		//	_tt=values[i];
		//}
		
		_tt=(TestSetting)_t2;
	}
}
