package com.home.shineTest.control;

import com.home.shine.ctrl.Ctrl;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class TestControl
{
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
	
	public static void testMethod(Runnable func,int times)
	{
		int warmTimes=times*3;
		
		for(int i=0;i<warmTimes;++i)
		{
			func.run();
		}
		
		Ctrl.print("startTestMethod");
		
		long t=Ctrl.getTimer();
		
		for(int i=0;i<times;++i)
		{
			func.run();
		}
		
		Ctrl.print("endTestMethod",Ctrl.getTimer()-t);
	}
}
