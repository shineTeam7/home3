package com.home.shineTest.jmh.collection;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.LinkedList;

@State(Scope.Thread)
public class TestArrayAndLinked
{
	private static int _length=100;
	
	@Benchmark
	public void arrayAdd()
	{
		ArrayList<String> list=new ArrayList<>();
		
		for(int i=0;i<_length;i++)
		{
			list.add("abc");
		}
	}
	
	@Benchmark
	public void linkedAdd()
	{
		LinkedList<String> list=new LinkedList<>();
		
		for(int i=0;i<_length;i++)
		{
			list.add("abc");
		}
	}
	
	//@Benchmark
	//public void arrayInsert()
	//{
	//	ArrayList<String> list=new ArrayList<>();
	//	list.add("abc");
	//
	//	for(int i=0;i<_length;i++)
	//	{
	//		list.add(MathUtils.randomInt(list.size()),"abc");
	//	}
	//
	//}
	//
	//@Benchmark
	//public void linkedInsert()
	//{
	//	LinkedList<String> list=new LinkedList<>();
	//	list.add("abc");
	//	for(int i=0;i<_length;i++)
	//	{
	//		list.add(MathUtils.randomInt(list.size()),"abc");
	//	}
	//}
}
