package com.home.commonTest.control;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class TestControl
{
	/** 测试jmh类 */
	public static void testCls(Class<?> cls)
	{
		Options opt=new OptionsBuilder().include(".*"+cls.getSimpleName()+".*").forks(1).build();
		
		try
		{
			new Runner(opt).run();
		}
		catch(RunnerException e)
		{
			e.printStackTrace();
		}
	}
}
