package com.home.shineTest.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestSwitch
{
	private int[] _arr;
	
	public TestSwitch()
	{
		_arr=new int[5];
		_arr[0]=0;
		_arr[1]=1;
		_arr[2]=2;
		_arr[3]=3;
		_arr[4]=4;
	}
	
	@Benchmark
	public void testArr()
	{
		int arr=getArr(3);
	}
	
	@Benchmark
	public void testSwitch()
	{
		int aSwitch=getSwitch(3);
	}
	
	private int getArr(int index)
	{
		return _arr[index];
	}
	
	private int getSwitch(int index)
	{
		switch(index)
		{
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			case 4:
				return 4;
		}
		
		return 0;
	}
}