package com.home.shineTest.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestFloatAndInt
{
	private static int _num=100000;
	
	private static final int _len=100;
	
	private static int[] intArr=new int[_len];
	private static float[] floatArr=new float[_len];
	
	static
	{
		for(int i=0;i<_len;i++)
		{
			intArr[i]=i+1;
			floatArr[i]=i+1;
		}
	}
	
	@Benchmark
	public void testIntAdd()
	{
		for(int i=1;i<_len;i++)
		{
			intArr[i]=(int)((intArr[i-1]+1000f)/1000f*intArr[i]);
		}
	}
	
	@Benchmark
	public void testFloatAdd()
	{
		for(int i=1;i<_len;i++)
		{
			floatArr[i]=(floatArr[i-1]+1f)*floatArr[i];
		}
	}
	
	//
	//@Benchmark
	//public float testFloatAdd()
	//{
	//	return _a2+=0.01f;
	//}
	//
	//@Benchmark
	//public double testDoubleAdd()
	//{
	//	return _a3+=0.01;
	//}
	//
	//@Benchmark
	//public int testIntMul()
	//{
	//	return _b1*100;
	//}
	//
	//@Benchmark
	//public float testFloatMul()
	//{
	//	return _b2*100f;
	//}
	//
	//@Benchmark
	//public double testDoubleMul()
	//{
	//	return _b3*100.0;
	//}
	//
	//@Benchmark
	//public int testIntDiv()
	//{
	//	return _b1/100;
	//}
	//
	//@Benchmark
	//public float testFloatDiv()
	//{
	//	return _b2/100f;
	//}
	//
	//@Benchmark
	//public double testDoubleDiv()
	//{
	//	return _b3/100.0;
	//}
	//
	//@Benchmark
	//public float testFloatSqrt()
	//{
	//	return (float)Math.sqrt(_b2);
	//}
	//
	//@Benchmark
	//public double testDoubleSqrt()
	//{
	//	return Math.sqrt(_b3);
	//}
	
}
