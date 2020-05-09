package com.home.shineTest.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestCalculate
{
	private static int _num=100;
	
	private int _a1=0;
	private float _a2=0f;
	private double _a3=0.0;
	
	private int _b1=10000;
	private float _b2=10000f;
	private double _b3=10000.0;
	
	//@Benchmark
	//public int testIntAdd()
	//{
	//	return _a1+=1;
	//}
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
	
	@Benchmark
	public void testIntMul()
	{
		for(int i=_num-1;i>=0;--i)
		{
			_a1+=3;
		}
	}
	
	@Benchmark
	public void testFloatMul()
	{
		for(int i=_num-1;i>=0;--i)
		{
			_a2+=3f;
		}
	}
	
	@Benchmark
	public void testDoubleMul()
	{
		for(int i=_num-1;i>=0;--i)
		{
			_a3+=3f;
		}
	}
	
	//@Benchmark
	//public void testFloatDiv()
	//{
	//	for(int i=_num-1;i>=0;--i)
	//	{
	//		_a2/=1.01f;
	//	}
	//}
}
