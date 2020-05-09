using System;

namespace ShineEngine
{
	/** 杂项方法 */
	public class OtherUtils
	{
		/** 数组相等比较 */
		public static bool dataArrEquals(BaseData[] arr1,BaseData[] arr2)
		{
			if(arr1==null || arr2==null)
				return false;

			int len;
			if((len=arr1.Length)!=arr2.Length)
				return false;

			for(int i=0;i<len;i++)
			{
				if(!arr1[i].dataEquals(arr2[i]))
					return false;
			}

			return true;
		}

		/** 移除不存在的项，从字典1,参考字典2 */
		public static void removeNotExistFromDic1WithDic2<T1,T2>(IntObjectMap<T1> dic1,IntObjectMap<T2> dic2)
		{
			int[] keys=dic1.getKeys();
			int fv=dic1.getFreeValue();
			int k;

			for(int i=keys.Length-1;i>=0;--i)
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
		public static int findLeastOfDic<T>(IntObjectMap<T> dic,Comparison<T> compare)
		{
			int[] keys=dic.getKeys();
			T[] values=dic.getValues();
			int fv=dic.getFreeValue();
			int k;
			T v;

			T temp=default(T);
			bool has=false;
			int re=-1;

			for(int i=keys.Length-1;i>=0;--i)
			{
				if((k=keys[i])!=fv)
				{
					v=values[i];

					if(!has)
					{
						has=true;
						temp=v;
						re=k;
					}
					else
					{
						if(compare(temp,v)<0)
						{
							temp=v;
							re=k;
						}
					}
				}
			}

			return re;
		}

		/** 在字典中找寻最小的 */
		public static long findLeastOfDic<T>(LongObjectMap<T> dic,Comparison<T> compare)
		{
			long[] keys=dic.getKeys();
			T[] values=dic.getValues();
			long fv=dic.getFreeValue();
			long k;
			T v;

			T temp=default(T);
			bool has=false;
			long re=-1;

			for(int i=keys.Length-1;i>=0;--i)
			{
				if((k=keys[i])!=fv)
				{
					v=values[i];

					if(!has)
					{
						has=true;
						temp=v;
						re=k;
					}
					else
					{
						if(compare(temp,v)<0)
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
		public static void putObjInDicWithMax<T>(int key,T obj,IntObjectMap<T> dic,int max,Comparison<T> compare)
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
		public static void putObjInDicWithMax<T>(long key,T obj,LongObjectMap<T> dic,int max,Comparison<T> compare)
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
	}
}