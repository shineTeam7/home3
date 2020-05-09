package com.home.shineTest.jmh;

import com.home.shine.utils.MathUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestTemp
{
	private static float[] _arr;
	
	static
	{
		_arr=new float[10000];
		
		for(int i=0;i<10000;i++)
		{
			_arr[i]=MathUtils.randomFloat()*MathUtils.fPI*4-MathUtils.fPI*2;
		}
	}
	
	private float _re;
	
	@Benchmark
	public void test1()
	{
		for(int i=0;i<_arr.length;i++)
		{
			_re=MathUtils.directionCut(_arr[i]);
		}
	}
	
	@Benchmark
	public void test2()
	{
		//for(int i=0;i<_arr.length;i++)
		//{
		//	_re=MathUtils.directionCut2(_arr[i]);
		//}
	}
}
