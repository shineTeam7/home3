package com.home.shine.utils;

import com.home.shine.agent.AgentControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.data.DIntData;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.collection.inter.ICreateArray;
import com.home.shine.support.pool.StringBuilderPool;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/** 对象方法 */
public class ObjectUtils
{
	/** 空byte数组 */
	public static final byte[] EmptyByteArr=new byte[0];
	/** 空char数组 */
	public static final char[] EmptyCharArr=new char[0];
	/** 空int数组 */
	public static final int[] EmptyIntArr=new int[0];
	/** 空数组 */
	public static final float[] EmptyFloatArr=new float[0];
	/** 空long数组 */
	public static final long[] EmptyLongArr=new long[0];
	/** 空boolean数组 */
	public static final boolean[] EmptyBooleanArr=new boolean[0];
	/** 空string数组 */
	public static final String[] EmptyStringArr=new String[0];
	/** 空obj数组 */
	public static final Object[] EmptyObjectArr=new Object[0];
	
	/** 获取字典key的排序List */
	public static <K> List<K> getSortMapKeys(Map<K,?> map)
	{
		List<K> list=new ArrayList<>(map.size());
		
		map.forEach((k,v)->
		{
			list.add(k);
		});
		
		Collections.sort(list,null);
		
		return list;
	}
	
	/** 获取字典key的排序List */
	public static <K> List<K> getSortSMapKeys(SMap<K,?> map)
	{
		List<K> list=new ArrayList<>(map.size());
		
		map.forEach((k,v)->
		{
			list.add(k);
		});
		
		Collections.sort(list,null);
		
		return list;
	}
	
	/** 获取Set的排序List */
	public static <K> List<K> getSortSetKeys(Set<K> set)
	{
		List<K> list=new ArrayList<>(set);
		
		Collections.sort(list,null);
		
		return list;
	}
	
	/** 获取Set的排序List */
	public static <K> List<K> getSortSSetKeys(SSet<K> set)
	{
		List<K> list=new ArrayList<>(set.size());
		
		set.forEach(v->
		{
			list.add(v);
		});
		
		Collections.sort(list,null);
		
		return list;
	}
	
	/** 获取某类的final int枚举(反射) */
	public static IntList getConstIntList(Class<?> cls)
	{
		Field[] fields=cls.getFields();
		
		IntList list=new IntList();
		
		for(Field f : fields)
		{
			//static final int
			if(Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()) && f.getType().equals(int.class))
			{
				try
				{
					int v=(int)f.get(cls);
					list.add(v);
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			}
		}
		
		return list;
	}
	
	/** 添加元素到数组末尾 */
	public static <T> T[] addArray(T[] arr,T obj,ICreateArray<T> createArrFunc)
	{
		T[] re=createArrFunc.create(arr.length+1);
		System.arraycopy(arr,0,re,0,arr.length);
		re[arr.length]=obj;
		return re;
	}
	
	/** 添加元素到数组末尾 */
	public static int[] addIntArray(int[] arr,int obj)
	{
		int[] re=new int[arr.length+1];
		System.arraycopy(arr,0,re,0,arr.length);
		re[arr.length]=obj;
		return re;
	}
	
	/** 确保数组长度 */
	public static boolean[] ensureArrayLength(boolean[] arr,int length)
	{
		if(arr.length >= length)
		{
			return arr;
		}
		
		boolean[] re=new boolean[length];
		
		System.arraycopy(arr,0,re,0,arr.length);
		
		return re;
	}
	
	/** 确保数组长度 */
	public static int[] ensureArrayLength(int[] arr,int length)
	{
		if(arr.length >= length)
		{
			return arr;
		}
		
		int[] re=new int[length];
		
		System.arraycopy(arr,0,re,0,arr.length);
		
		return re;
	}
	
	/** 确保数组长度 */
	public static long[] ensureArrayLength(long[] arr,int length)
	{
		if(arr.length >= length)
		{
			return arr;
		}
		
		long[] re=new long[length];
		
		System.arraycopy(arr,0,re,0,arr.length);
		
		return re;
	}
	
	/** 确保数组长度 */
	public static <T> T[] ensureArrayLength(T[] arr,int length,ICreateArray<T> createArrayFunc)
	{
		if(arr.length >= length)
		{
			return arr;
		}
		
		if(createArrayFunc==null)
		{
			Ctrl.errorLog("必须传入数组构造方法");
			return arr;
		}
		
		T[] re=createArrayFunc.create(length);
		
		System.arraycopy(arr,0,re,0,arr.length);
		
		return re;
	}
	
	/** 整形组转boolean数组set */
	public static boolean[] intArrayToBooleanSet(int[] arr,int length)
	{
		boolean[] re=new boolean[length];
		
		for(int i=arr.length - 1;i >= 0;--i)
		{
			re[arr[i]]=true;
		}
		
		return re;
	}
	
	/** 整形组转int数组set */
	public static IntSet intArrayToIntSet(int[] arr)
	{
		IntSet re=new IntSet();

		for(int i=arr.length - 1;i >= 0;--i)
		{
			re.add(arr[i]);
		}

		return re;
	}
	
	/** 转换intint字典 */
	public static IntIntMap parseIntIntMapByDIntArr(DIntData[] arr)
	{
		IntIntMap map=new IntIntMap(arr.length);
		
		for(DIntData v:arr)
		{
			map.put(v.key,v.value);
		}
		
		return map;
	}
	
	/** 数组indexOf */
	public static <T> int arrayIndexOf(T[] arr,T v)
	{
		T temp;
		
		for(int i=0,len=arr.length;i<len;++i)
		{
			if((temp=arr[i])!=null && temp.equals(v))
				return i;
		}
		
		return -1;
	}
	
	/** 整形数组indexOf */
	public static int intArrayIndexOf(int[] arr,int v)
	{
		for(int i=0,len=arr.length;i<len;++i)
		{
			if(arr[i]==v)
				return i;
		}
		
		return -1;
	}
	
	/** 整形数组包含 */
	public static boolean intArrayContains(int[] arr,int v)
	{
		return intArrayIndexOf(arr,v)!=-1;
	}
	
	/** 整形数组indexOf */
	public static long longArrayIndexOf(long[] arr,long v)
	{
		for(int i=0,len=arr.length;i<len;++i)
		{
			if(arr[i]==v)
				return i;
		}
		
		return -1;
	}
	
	/** 整形数组indexOf */
	public static boolean longArrayContains(long[] arr,long v)
	{
		return longArrayIndexOf(arr,v)!=-1;
	}
	
	private static AtomicInteger[] _steps=new AtomicInteger[100];
	
	static
	{
		for(int i=_steps.length-1;i>=0;--i)
		{
			_steps[i]=new AtomicInteger(0);
		}
	}
	
	/** 记步 */
	public static void recordStep(int step)
	{
		int num=_steps[step].incrementAndGet();
		
		Ctrl.log("step:"+step+" num:"+num);
	}
	
	/** 输出数据列表 */
	public static void printDataList(SList<? extends BaseData> list)
	{
		StringBuilder sb=StringBuilderPool.create();
		
		BaseData data;
		
		for(int i=0,len=list.size();i<len;i++)
		{
			data=list.get(i);
			
			sb.append(i);
			sb.append(":");
			
			if(data==null)
			{
				sb.append("null");
			}
			else
			{
				sb.append(data.toDataString());
			}
			
			sb.append("\n");
		}
		
		Ctrl.log(StringBuilderPool.releaseStr(sb));
	}
	
	/** 输出数据字典 */
	public static void printDataDic(IntObjectMap<? extends BaseData> dic)
	{
		StringBuilder sb=StringBuilderPool.create();
		
		dic.forEach((k,v)->
		{
			sb.append(k);
			sb.append(":");
			
			if(v==null)
			{
				sb.append("null");
			}
			else
			{
				sb.append(v.toDataString());
			}
			
			sb.append("\n");
		});
		
		Ctrl.log(StringBuilderPool.releaseStr(sb));
	}
	
	/** 数组putAll */
	public static <T> void arrayPutAll(T[] source,T[] target)
	{
		int len=Math.max(source.length,target.length);
		
		T v;
		
		for(int i=0;i<len;i++)
		{
			if((v=target[i])!=null)
			{
				source[i]=v;
			}
		}
	}
	
	/** 二分查找(范围的index的值<=value) */
	public static <T> int binarySearchS(T[] arr,int length,T value,Comparator<? super T> c)
	{
		int index=Arrays.binarySearch(arr,0,length,value,c);
		
		if(index>=0)
			return index;
		
		index=-index-2;
		
		if(index<0)
			index=0;
		
		return index;
	}
	
	/** 设置对象属性值(私有) */
	public static void setObjField(Object obj,String fieldName,Object value)
	{
		Class<?> cls = obj.getClass();
		try {
			Field ff = cls.getDeclaredField(fieldName);
			
			ff.setAccessible(true);
			
			ff.set(obj,value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 查看对象大小 */
	public static long sizeOf(Object obj)
	{
		Instrumentation inst=AgentControl.getInstrumentation();
		
		if(inst==null)
			return 0;
		
		return inst.getObjectSize(obj);
	}
	
	/** 查看对象递归尺寸 */
	public static long deepSizeOf(Object obj)
	{
		return 0;
	}
	
	public static int arrayGetOrDefault(int[] arr,int index,int defaultValue)
	{
		return index<arr.length ? arr[index] : defaultValue;
	}
}
