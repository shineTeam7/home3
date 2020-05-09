package com.home.shineTest.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestLocalVar
{
	private int _i1=0;
	private int _i2=1;
	private int _i3=2;
//	private int _i4=3;
//	private int _i5=4;
	
	private int[] _arr=new int[64];
	
	private int _re;
	
	public TestLocalVar()
	{
		for(int i=0;i<64;++i)
		{
			_arr[i]=i;
		}
	}
	
//	@Benchmark
//	public int test1()
//	{
//		int re=0;
//		
//		for(int i=0;i<3;++i)
//		{
//			re+=_i1;
//			re+=_i2;
//			re+=_i3;
//		}
//		
//		return re;
//	}
//	
//	@Benchmark
//	public int test2()
//	{
//		int re=0;
//		
//		int i1=_i1;
//		int i2=_i2;
//		int i3=_i3;
//		
//		for(int i=0;i<3;++i)
//		{
//			re+=i1;
//			re+=i2;
//			re+=i3;
//		}
//		
//		return re;
//	}
	
//	@Benchmark
//	public int forEach1()
//	{
//		int len=_arr.length;
//		
//		for(int i=0;i<len;++i)
//		{
//			_re=_arr[i];
//		}
//		
//		return _re;
//		
//	}
	
	
//	@Benchmark
//	public int forEach2()
//	{
//		for(int i=0;i<_arr.length;++i)
//		{
//			_re+=_arr[i];
//		}
//		
//		return _re;
//	}
	
//	@Benchmark
//	public int forEach3()
//	{
//		for(int i=0,len=_arr.length;i<len;++i)
//		{
//			_re+=_arr[i];
//		}
//		
//		return _re;
//	}
	
	@Benchmark
	public int forEach4()
	{
		int len=_arr.length;
		
		for(int i=0;i<len;++i)
		{
			_re+=_arr[i];
		}
		
		return _re;
	}
	
	@Benchmark
	public int forEach5()
	{
		int[] arr=_arr;
		int len=arr.length;
		
		for(int i=0;i<len;++i)
		{
			_re+=arr[i];
		}
		
		return _re;
	}
}
