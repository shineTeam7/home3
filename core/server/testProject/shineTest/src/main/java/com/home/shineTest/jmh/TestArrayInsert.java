package com.home.shineTest.jmh;

import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.SList;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.LinkedList;

@State(Scope.Thread)
public class TestArrayInsert
{
	private static int _times=1000;
	
	@Benchmark
	public void testList1()
	{
		SList<Integer> list=new SList<>();
		list.ensureCapacity(_times);
		
		for(int i=_times-1;i>=0;--i)
		{
			list.insert(list.size()>>1,i);
		}
		
		while(!list.isEmpty())
		{
			list.pop();
		}
	}
	
	@Benchmark
	public void testList2()
	{
		IntList list=new IntList();
		list.ensureCapacity(_times);
		
		for(int i=_times-1;i>=0;--i)
		{
			list.insert(list.size()>>1,i);
		}
		
		while(!list.isEmpty())
		{
			list.pop();
		}
	}
	
	@Benchmark
	public void testList3()
	{
		LinkedList<Integer> list=new LinkedList();
		
		for(int i=_times-1;i>=0;--i)
		{
			list.add(list.size()>>1,i);
		}
		
		while(!list.isEmpty())
		{
			list.pop();
		}
	}
}
