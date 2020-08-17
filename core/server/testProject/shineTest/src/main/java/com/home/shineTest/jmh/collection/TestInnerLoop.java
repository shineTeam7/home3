package com.home.shineTest.jmh.collection;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestInnerLoop
{
	private int _k=0;
	
	private static int _byteInt=10;
	private static int _shortInt=10000;
	
	@Benchmark
	public void testBig()
	{
		_k=0;
		
		final int si=_shortInt;
		final int bi=_byteInt;
		
		for(int i=0;i<si;i++)
		{
			for(int j=0;j<bi;j++)
			{
				++_k;
			}
		}
	}
	
	@Benchmark
	public void testShort()
	{
		_k=0;
		
		final int si=_shortInt;
		final int bi=_byteInt;
		
		for(int i=0;i<bi;i++)
		{
			for(int j=0;j<si;j++)
			{
				++_k;
			}
		}
	}
	
	@Benchmark
	public void testBig2()
	{
		_k=0;
		
		for(int i=0;i<10000;i++)
		{
			for(int j=0;j<10;j++)
			{
				++_k;
			}
		}
	}
	
	@Benchmark
	public void testShort2()
	{
		_k=0;
		
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<10000;j++)
			{
				++_k;
			}
		}
	}
}
