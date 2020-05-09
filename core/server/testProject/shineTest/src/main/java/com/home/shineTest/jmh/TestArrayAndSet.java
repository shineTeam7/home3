package com.home.shineTest.jmh;

import com.home.shine.support.collection.IntSet;
import com.home.shine.utils.MathUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.home.shine.support.collection.IntList;

@State(Scope.Thread)
public class TestArrayAndSet
{
	private static int _loop=100;
	private static int _len=4;
	
	private int[] _arr;
	
	private IntList _list;
	private IntSet _set;
	
	public TestArrayAndSet()
	{
		_arr=new int[_len];
		
		_list=new IntList(_len);
		_set=new IntSet(_len);
		
		for(int i=0;i<_len;++i)
		{
			_arr[i]=MathUtils.randomInt(1000);
			
			_list.add(_arr[i]);
			_set.add(_arr[i]);
		}
	}
	
	@Benchmark
	public void testArr()
	{
		int[] arr=_arr;
		
		boolean has;
		
		for(int i=_loop-1;i>=0;--i)
		{
			for(int j=_len-1;j>=0;--j)
			{
				has=_list.indexOf(arr[j])!=-1;
			}
		}
	}
	
	@Benchmark
	public void testList()
	{
		int[] arr=_arr;
		boolean has;
		
		for(int i=_loop-1;i>=0;--i)
		{
			for(int j=_len-1;j>=0;--j)
			{
				has=_set.contains(arr[j]);
			}
		}
	}
}
