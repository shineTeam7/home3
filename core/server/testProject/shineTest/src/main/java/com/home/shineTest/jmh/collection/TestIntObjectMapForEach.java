package com.home.shineTest.jmh.collection;

import com.home.shine.data.DIntData;
import com.home.shine.support.collection.IntLinkedObjectMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shineTest.control.TestSetting;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/** 测试集合遍历 */
@State(Scope.Thread)
public class TestIntObjectMapForEach
{
	private IntObjectMap<DIntData> _dic=new IntObjectMap<>(DIntData[]::new);
	
	private IntLinkedObjectMap<DIntData> _linkedDic=new IntLinkedObjectMap<>();
	
	private DIntData _re=null;
	
	public TestIntObjectMapForEach()
	{
		int len=TestSetting.Len;
		
		for(int i=0;i<len;i++)
		{
			_dic.put(i,TestSetting.datas[i]);
			_linkedDic.put(i,TestSetting.datas[i]);
		}
	}
	
	//@Benchmark
	//public void testLambda()
	//{
	//	_dic.forEachValue(v->
	//	{
	//		_re=v;
	//	});
	//}
	//
	//@Benchmark
	//public void testLambdaS()
	//{
	//	_dic.forEachValueS(v->
	//	{
	//		_re=v;
	//	});
	//}
	
	@Benchmark
	public void testFor1()
	{
		DIntData[] values;
		DIntData v;

		for(int i=(values=_dic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				_re=v;
			}
		}
	}
	
	//@Benchmark
	//public void testForSafe1()
	//{
	//	int free=_dic.getFreeValue();
	//	int[] keys=_dic.getKeys();
	//	DIntData[] vals=_dic.getValues();
	//	int key;
	//
	//	int capacityMask=keys.length-1;
	//	int safeIndex=_dic.getLastFreeIndex();
	//
	//	for(int i=(safeIndex-1) & capacityMask;i != safeIndex;i=(i-1)&capacityMask)
	//	{
	//		if((key=keys[i])!=free)
	//		{
	//			_re=vals[i];
	//
	//			if(key!=keys[i])
	//			{
	//				++i;
	//			}
	//		}
	//	}
	//}
	
	//@Benchmark
	//public void testForSafe2()
	//{
	//	int free=_dic.getFreeValue();
	//	int[] keys=_dic.getKeys();
	//	DIntData[] vals=_dic.getValues();
	//	int key;
	//
	//	int safeIndex=_dic.getLastFreeIndex();
	//
	//	for(int i=safeIndex-1;i>=0;--i)
	//	{
	//		if((key=keys[i])!=free)
	//		{
	//			_re=vals[i];
	//
	//			if(key!=keys[i])
	//			{
	//				++i;
	//			}
	//		}
	//	}
	//
	//	for(int i=keys.length-1;i>safeIndex;--i)
	//	{
	//		if((key=keys[i])!=free)
	//		{
	//			_re=vals[i];
	//
	//			if(key!=keys[i])
	//			{
	//				++i;
	//			}
	//		}
	//	}
	//}
	//
	//@Benchmark
	//public void testForSafe3()
	//{
	//	int free=_dic.getFreeValue();
	//	int[] keys=_dic.getKeys();
	//	DIntData[] vals=_dic.getValues();
	//	int key;
	//	int safeIndex=_dic.getLastFreeIndex();
	//
	//	for(int i=safeIndex-1;i != safeIndex;--i)
	//	{
	//		if(i<0)
	//		{
	//			i=keys.length;
	//		}
	//		else if((key=keys[i])!=free)
	//		{
	//			_re=vals[i];
	//
	//			if(key!=keys[i])
	//			{
	//				++i;
	//			}
	//		}
	//	}
	//}
	
	//@Benchmark
	//public void testForSafeStrong()
	//{
	//	for(DIntData v:_dic)
	//	{
	//		_re=v;
	//	}
	//}
	//
	//@Benchmark
	//public void testLinkedLambda()
	//{
	//	_linkedDic.forEachValueS(v->
	//	{
	//		_re=v;
	//	});
	//}
	//
	@Benchmark
	public void testLinked()
	{
		IntLinkedObjectMap.Node<DIntData> node=_linkedDic.getHead();

		while(node!=null)
		{
			_re=node.value;
			node=node.next;
		}
	}
	//
	//@Benchmark
	//public void testLinkedS()
	//{
	//	IntLinkedObjectMap.Node<DIntData> node=_linkedDic.getHead();
	//	IntLinkedObjectMap.Node<DIntData> next;
	//
	//	while(node!=null)
	//	{
	//		next=node.next;
	//		_re=node.value;
	//		node=next;
	//	}
	//}
}
