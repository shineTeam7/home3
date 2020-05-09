package com.home.shineTest.control;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class TestControl
{
	public static int aa=0;
	
	public static void testCls(Class<?> cls)
	{
		Options opt=new OptionsBuilder().include(".*"+cls.getSimpleName()+".*")
				.measurementIterations(5)
				.measurementTime(TimeValue.seconds(2))
				.warmupTime(TimeValue.seconds(2))
				.forks(1).build();
		
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
