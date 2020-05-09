package com.home.shine.utils;

import com.home.shine.data.BaseData;
import com.home.shine.dataEx.VInt;
import com.home.shine.dataEx.VObj;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

/** 其他方法 */
public class OtherUtils
{
	/** 数组相等比较 */
	public static boolean dataArrEquals(BaseData[] arr1,BaseData[] arr2)
	{
		if(arr1==null || arr2==null)
			return false;
		
		int len;
		if((len=arr1.length)!=arr2.length)
			return false;
		
		for(int i=0;i<len;i++)
		{
			if(!arr1[i].dataEquals(arr2[i]))
				return false;
		}
		
		return true;
	}
	
	/** 移除不存在的项，从字典1,参考字典2 */
	public static <T1,T2> void removeNotExistFromDic1WithDic2(IntObjectMap<T1> dic1,IntObjectMap<T2> dic2)
	{
		int[] keys=dic1.getKeys();
		int fv=dic1.getFreeValue();
		int k;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=fv)
			{
				if(!dic2.contains(k))
				{
					dic1.remove(k);
					++i;
				}
			}
		}
	}
	
	/** 在字典中找寻最小的Key */
	public static <T> int findLeastOfDic(IntObjectMap<T> dic,Comparator<T> compare)
	{
		VObj<T> temp=new VObj<>();
		VInt re=new VInt(-1);
		
		dic.forEach((k,v)->
		{
			if(temp.value==null)
			{
				temp.value=v;
				re.value=k;
			}
			else
			{
				if(compare.compare(temp.value,v)<0)
				{
					temp.value=v;
					re.value=k;
				}
			}
		});
		
		return re.value;
	}
	
	/** 在字典中找寻最小的 */
	public static <T> long findLeastOfDic(LongObjectMap<T> dic,Comparator<T> compare)
	{
		long[] keys=dic.getKeys();
		T[] values=dic.getValues();
		long fv=dic.getFreeValue();
		long k;
		T v;
		
		T temp=null;
		long re=-1;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=fv)
			{
				v=values[i];
				
				if(temp==null)
				{
					temp=v;
					re=k;
				}
				else
				{
					if(compare.compare(temp,v)<0)
					{
						temp=v;
						re=k;
					}
				}
			}
		}
		
		return re;
	}
	
	/** 将元素放入有尺寸限制的集合中 */
	public static <T> void putObjInDicWithMax(int key,T obj,IntObjectMap<T> dic,int max,Comparator<T> compare)
	{
		if(max<=0)
		{
			dic.put(key,obj);
		}
		else
		{
			if(dic.contains(key))
			{
				dic.put(key,obj);
			}
			else
			{
				if(dic.size()>=max)
				{
					int leastKey=findLeastOfDic(dic,compare);
					
					dic.remove(leastKey);
				}
				
				dic.put(key,obj);
			}
		}
	}
	
	/** 将元素放入有尺寸限制的集合中 */
	public static <T> void putObjInDicWithMax(long key,T obj,LongObjectMap<T> dic,int max,Comparator<T> compare)
	{
		if(max<=0)
		{
			dic.put(key,obj);
		}
		else
		{
			if(dic.contains(key))
			{
				dic.put(key,obj);
			}
			else
			{
				if(dic.size()>=max)
				{
					long leastKey=findLeastOfDic(dic,compare);
					
					dic.remove(leastKey);
				}
				
				dic.put(key,obj);
			}
		}
	}
	
	/** 获得第一个非回环的ip */
	public static String getIp() throws SocketException
	{
		Enumeration en = NetworkInterface.getNetworkInterfaces();
		List<String> ips = new ArrayList<>();
		while (en.hasMoreElements()) {
			NetworkInterface i = (NetworkInterface) en.nextElement();
			for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
				InetAddress addr = (InetAddress) en2.nextElement();
				if (!addr.isLoopbackAddress()) {
					if (addr instanceof Inet4Address) {
						ips.add(addr.getHostAddress());
					}
				}
			}
		}
		ips.sort(Comparator.naturalOrder());
		return ips.get(0);
	}
	
	public static <T> T getLastFromDic(IntObjectMap<T> intObjectMap)
	{
		if(intObjectMap.length()<1)
		{
			return null;
		}
		IntList sortedKeyList=intObjectMap.getSortedKeyList();
		int lastId=sortedKeyList.get(sortedKeyList.length() - 1);
		return intObjectMap.get(lastId);
	}
	
	
	public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
		double ew1, ns1, ew2, ns2;
		double distance;
		
		// 角度转换为弧度
		// PI/180.0
		double DEF_PI180=Math.PI / 180;
		ew1 = lng1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lng2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		
		// 求大圆劣弧与球心所夹的角(弧度)
		distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);
		// 调整到[-1..1]范围内，避免溢出
		if (distance > 1.0){
			distance = 1.0;
		} else if (distance < -1.0){
			distance = -1.0;
		}
		
		// 求大圆劣弧长度
		// 地球半径
		double DEF_R=6370693.5;
		distance = DEF_R * Math.acos(distance);
		return distance;
	}
	
	
	
	/**
	 * 通过经纬度获取距离 小于 (单位：公里)
	 *
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @param kilometre 距离  公里
	 * @return
	 */
	public static boolean distanceLessThan(double lat1,double lng1,double lat2,double lng2,int kilometre ){
		return OtherUtils.getDistance(lat1,lng1,lat2,lng2)<=kilometre*1000;
	}
	
	
}
