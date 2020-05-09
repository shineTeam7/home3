package com.home.shineTest.jmh;

import java.lang.reflect.Array;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.home.shineTest.control.TestSetting;

@State(Scope.Thread)
public class TestArrayCreate
{
	@Benchmark
	public TestSetting[] testNature()
	{
		return new TestSetting[64];
	}
	
	@Benchmark
	public TestSetting[] testReflect()
	{
		return (TestSetting[])Array.newInstance(TestSetting.class,64);
	}
}
