package com.home.shineTest.jmh;

import com.home.shine.ctrl.Ctrl;
import com.home.shineTest.control.TestControl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestCount
{
	public static int _num=0;
	
	private int _aa;
	@Benchmark
	public void testCount()
	{
		++_num;
		TestControl.aa=_num;
		
		//try
		//{
		//	Thread.sleep(50);
		//}
		//catch(InterruptedException e)
		//{
		//	e.printStackTrace();
		//}
		
		Ctrl.print(_num);
		
		//for(int i=0;i<1000000;++i)
		//{
		//	_aa++;
		//}
	}
}
