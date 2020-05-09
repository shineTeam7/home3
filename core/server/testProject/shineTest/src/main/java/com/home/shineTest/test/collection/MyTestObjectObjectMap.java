package com.home.shineTest.test.collection;

import com.home.shineTest.jmh.collection.TestObjectObjectMap;
import com.home.shine.ctrl.Ctrl;

public class MyTestObjectObjectMap
{
	public static void test()
	{
		TestObjectObjectMap tt=new TestObjectObjectMap();
		
		for(int i=0;i<1000;++i)
		{
//			tt.testNature();
			tt.testMyKolo();
		}
		
		long t=Ctrl.getTimer();
		
		for(int i=0;i<1000;++i)
		{
//			tt.testNature();
			tt.testMyKolo();
		}
		
		Ctrl.print(Ctrl.getTimer()-t);
	}
}
