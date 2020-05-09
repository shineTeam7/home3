package com.home.commonTest.app;

import com.home.commonTest.jmh.TestPathFinding;
import com.home.shineTest.control.TestControl;

public class TestCJMHApp
{
	public static void main( String[] args )
	{
		TestControl.testCls(TestPathFinding.class);
	}
}
