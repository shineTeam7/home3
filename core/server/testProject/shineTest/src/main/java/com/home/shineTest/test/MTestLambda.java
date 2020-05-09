package com.home.shineTest.test;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DIntData;
import com.home.shine.support.collection.IntObjectMap;

public class MTestLambda
{
	private int _a=0;
	
	public void test()
	{
		long t1;
		long t2;
		long t3;
		long t4;
		long t5;
		long t6;
		
		Ctrl.print(Ctrl.getTimer());
		
		Ctrl.print(Ctrl.getTimer());
		
		Runnable func=()->
		{
			_a++;
		};
		
		Ctrl.print(Ctrl.getTimer());
		
		func.run();
		Ctrl.print(Ctrl.getTimer());
		
		func.run();
		Ctrl.print(Ctrl.getTimer());
		
		func.run();
		Ctrl.print(Ctrl.getTimer());
	}
}
