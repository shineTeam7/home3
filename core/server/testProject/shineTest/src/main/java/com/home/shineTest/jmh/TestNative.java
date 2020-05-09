package com.home.shineTest.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.home.shine.control.ThreadControl;
import com.home.shine.data.BaseData;
import com.home.shine.data.DIntData;
import com.home.shine.utils.MathUtils;

/** 测试一些native方法的开销对比(也包括一些自己写的小方法) */
@State(Scope.Thread)
public class TestNative
{
	private int _index;
	
	private BaseData _dd=new BaseData();
	
	@Benchmark
	public int testAutoIncrement()
	{
		return ++_index;
	}
	
	@Benchmark
	public boolean testInstanceof()
	{
		return _dd instanceof DIntData;
	}
	
	@Benchmark
	public int testRandom()
	{
		return MathUtils.randomInt();
	}
	
	@Benchmark
	public long testCurrentTimeMillis()
	{
		return System.currentTimeMillis();
	}
	
	@Benchmark
	public Thread testCurrentThread()
	{
		return Thread.currentThread();
	}
	
	@Benchmark
	public Thread testCurrentBaseThread()
	{
		return ThreadControl.getCurrentShineThread();
	}
}
