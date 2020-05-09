package com.home.shineTest.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/** 测试Array的各种forEach开销对比 */
@State(Scope.Thread)
public class TestArrayForEach
{
	private static int[] _arr;
	
	static
	{
		_arr=new int[256];
		
		for(int i=0;i<_arr.length;++i)
		{
			_arr[i]=i;
		}
	}
	
	private int _re;
	
	@Benchmark
	public void testFor()
	{
		for(int v:_arr)
		{
			_re=v;
		}
	}
	
	@Benchmark
	public void testForI_1()
	{
		for(int i=0;i<_arr.length;i++)
		{
			_re=_arr[i];
		}
	}
	
	@Benchmark
	public void testForI_2()
	{
		for(int i=0,len=_arr.length;i<len;i++)
		{
			_re=_arr[i];
		}
	}
	
	@Benchmark
	public void testForI_3()
	{
		int[] arr=_arr;
		
		for(int i=0,len=arr.length;i<len;++i)
		{
			_re=arr[i];
		}
	}
	
	@Benchmark
	public void testForI_4()
	{
		for(int i=_arr.length-1;i>=0;--i)
		{
			_re=_arr[i];
		}
	}
	
	@Benchmark
	public void testForI_5()
	{
		for(int i=_arr.length;--i>=0;)
		{
			_re=_arr[i];
		}
	}
}
