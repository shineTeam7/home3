package com.home.shineTest.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/** 测试单个getter和public属性的开销对比 */
@State(Scope.Thread)
public class TestGetter
{
	private int _a=1;
	
	@Benchmark
	public int test1()
	{
		return _a;
	}
	
	@Benchmark
	public int test2()
	{
		return getA();
	}
	
	public int getA()
	{
		return _a;
	}
}
