package com.home.shineTest.jmh.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntList;
import com.home.shine.utils.MathUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.HashMap;

@State(Scope.Thread)
public class TestArrayAndMap
{
	private static final int _len=10000;
	
	private static int[] _tt=new int[_len];
	
	static {
		
		IntList temp=new IntList();
		
		for(int i=0;i<_len;i++)
		{
			temp.add(i);
		}
		
		for(int i=0;i<_len;i++)
		{
			int k=MathUtils.randomInt(temp.size());
			
			_tt[i]=temp.remove(k);
		}
	}
	
	@Benchmark
	public void arrayAdd()
	{
		ArrayList<Integer> list=new ArrayList<Integer>();
		list.ensureCapacity(_len);
		
		for(int i=0;i<_len;i++)
		{
			list.add(i);
		}
		
		for(int i=0;i<_len;i++)
		{
			list.remove(list.indexOf(_tt[i]));
		}
	}
	
	@Benchmark
	public void mapAdd()
	{
		HashMap<Integer,Integer> map=new HashMap<>();
		
		for(int i=0;i<_len;i++)
		{
			map.put(i,i);
		}
		
		for(int i=0;i<_len;i++)
		{
			map.remove(_tt[i]);
		}
	}
	
	@Benchmark
	public void arrayAdd2()
	{
		IntList list=new IntList();
		list.ensureCapacity(_len);
		
		for(int i=0;i<_len;i++)
		{
			list.add(i);
		}
		
		for(int i=0;i<_len;i++)
		{
			list.remove(list.indexOf(_tt[i]));
		}
	}
	
	@Benchmark
	public void mapAdd2()
	{
		IntIntMap map=new IntIntMap();
		map.ensureCapacity(_len);
		
		for(int i=0;i<_len;i++)
		{
			map.put(i,i);
		}
		
		for(int i=0;i<_len;i++)
		{
			map.remove(_tt[i]);
		}
	}
}