package com.home.shineTest.jmh;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/** 测试方法调用 */
@State(Scope.Benchmark)
public class TestMethodCall
{
	private static Method _method;
	
	private static MethodHandle _methodHandle;
	
	static
	{
		try
		{
			_method=TestMethodCall.class.getMethod("addOne");
			
			_methodHandle=MethodHandles.lookup().findVirtual(TestMethodCall.class,"addOne",MethodType.methodType(Void.TYPE));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private int _index=0;
	
	public void addOne()
	{
		++_index;
	}
	
	public int getIndex()
	{
		return _index;
	}
	
	@Benchmark
	public void testDefault()
	{
		addOne();
	}
	
	@Benchmark
	public void testMethodInvoke()
	{
		try
		{
			_method.invoke(this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Benchmark
	public void testMethodHandle()
	{
		try
		{
			_methodHandle.invoke(this);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	@Benchmark
	public void testMethodHandle2()
	{
		try
		{
			_methodHandle.invokeExact(this);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	@Benchmark
	public void testMethodHandle3()
	{
		try
		{
			_methodHandle.invokeWithArguments(this);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
