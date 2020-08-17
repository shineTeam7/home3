package com.home.shineTest.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shineTest.control.TestControl;
import com.home.shineTest.jmh.TestBytesRawInt;
import com.home.shineTest.jmh.TestCheckCast;
import com.home.shineTest.jmh.TestCount;
import com.home.shineTest.jmh.TestFloatAndInt;
import com.home.shineTest.jmh.collection.TestArrayAndLinked;
import com.home.shineTest.jmh.collection.TestArrayAndMap;
import com.home.shineTest.jmh.collection.TestInnerLoop;
import com.home.shineTest.jmh.collection.TestIntObjectMapAdd;
import com.home.shineTest.jmh.collection.TestIntObjectMapForEach;

@SuppressWarnings(value={"unused"})
public class TestJMHApp
{
	public static void main( String[] args )
    {
//		testCls(TestIntIntMap.class);
//		TestControl.testCls(TestIntObjectMapAdd.class);
//		TestControl.testCls(TestIntObjectMapForEach.class);
//		testCls(TestObjectObjectMap.class);
//		testCls(TestIDObjectMap.class);
//		testCls(TestNative.class);
//		testCls(TestGetter.class);
//		testCls(TestArrayForEach.class);
//		testCls(TestArrayAndSet.class);
//		TestControl.testCls(TestCheckCast.class);
//		testCls(TestArrayCreate.class);
//		testCls(TestIntObjectMapCreateType.class);
//		testCls(TestMethodCall.class);
//		testCls(TestMoveMethod.class);
//		testCls(TestLocalVar.class);
//		testCls(TestRankTool.class);
//		TestControl.testCls(TestIntObjectMapForEach.class);
		TestControl.testCls(TestInnerLoop.class);
//		TestControl.testCls(TestBytesRawInt.class);
		
		//TestControl.testCls(TestArrayAndMap.class);
		
//		TestControl.testCls(TestArrayAndSet.class);
//		  new MTestLambda().test();
	
      //TestControl.testCls(TestPathFinding.class);
	
		//TestControl.aa=0;
		//TestControl.testCls(TestCount.class);
		//Ctrl.print(TestCount._num);
		//Ctrl.print(TestControl.aa);
	}
	
}
