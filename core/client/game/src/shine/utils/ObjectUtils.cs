using System;
using System.Collections.Generic;
using System.Text;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 对象方法
	/// </summary>
	public class ObjectUtils
	{
		/** 空字节数组 */
		public static byte[] EmptyByteArr=new byte[0];
		/** 空char数组 */
		public static char[] EmptyCharArr=new char[0];
		/** 空int数组 */
		public static int[] EmptyIntArr=new int[0];
		/** 空float数组 */
		public static float[] EmptyFloatArr=new float[0];
		/** 空long数组 */
		public static long[] EmptyLongArr=new long[0];
		/** 空obj数组 */
		public static object[] EmptyObjectArr=new object[0];

		//方法添加

		/** 修复未使用警告 */
		public static void fixUnusedWarn(object obj)
		{
		}

		/** 修复未使用警告 */
		public static void fixUnusedWarn(params object[] obj)
		{
		}

		public static int hashCode(object obj)
		{
			return obj==null ? 0 : obj.GetHashCode();
		}

		public static bool equals(object a,object b)
		{
			return (a==b) || (a!=null && a.Equals(b));
		}

		//public static List<K> getSortedMapKeys<K>(SMap<K,object> map)
		//{
		//	List<K> list=map.getKeyList();

		//	list.Sort();

		//	return list;
		//}

		/** 整形数组indexOf */
		public static int arrayIndexOf<T>(T[] arr,T v)
		{
			T temp;

			for(int i=0,len=arr.Length;i<len;++i)
			{
				if((temp=arr[i])!=null && temp.Equals(v))
					return i;
			}

			return -1;
		}

		/** 整形数组包含 */
		public static bool arrayContains<T>(T[] arr,T v)
		{
			return arrayIndexOf(arr,v)!=-1;
		}

		public static void arrayFill<T>(T[] arr,T v)
		{
			for(int i=arr.Length - 1;i>=0;--i)
			{
				arr[i]=v;
			}
		}

		public static void arrayReplace<T>(T[] arr,T oldValue,T newValue)
		{
			for(int i=arr.Length - 1;i>=0;--i)
			{
				if(oldValue.Equals(arr[i]))
				{
					arr[i]=newValue;
				}
			}
		}

		/** 整形组转数组set */
		public static bool[] intArrayToBooleanSet(int[] arr,int length)
		{
			bool[] re=new bool[length];

			for(int i=arr.Length - 1;i >= 0;--i)
			{
				re[arr[i]]=true;
			}

			return re;
		}

		/** 整形组转int数组set */
		public static IntSet intArrayToIntSet(int[] arr)
		{
			IntSet re=new IntSet();

			for(int i=arr.Length - 1;i >= 0;--i)
			{
				re.add(arr[i]);
			}

			return re;
		}

		/** 转换intint字典 */
		public static IntIntMap parseIntIntMapByDIntArr(DIntData[] arr)
		{
			IntIntMap map=new IntIntMap(arr.Length);

			foreach(DIntData v in arr)
			{
				map.put(v.key,v.value);
			}

			return map;
		}

		/** 输出数据列表 */
		public static void printDataList<T>(SList<T> list)where T:BaseData
		{
			StringBuilder sb=StringBuilderPool.create();

			T data;

			for(int i=0,len=list.size();i<len;i++)
			{
				data=list.get(i);

				sb.Append(i);
				sb.Append(":");

				if(data==null)
				{
					sb.Append("null");
				}
				else
				{
					sb.Append(data.toDataString());
				}

				sb.Append("\n");
			}

			Ctrl.print(StringBuilderPool.releaseStr(sb));
		}

		/** 输出数据字典 */
		public static void printDataDic<T>(IntObjectMap<T> dic)where T:BaseData
		{
			StringBuilder sb=StringBuilderPool.create();

			IntObjectMap<T> fDic;
			if(!(fDic=dic).isEmpty())
			{
				int[] keys=fDic.getKeys();
				T[] values=fDic.getValues();
				int fv=fDic.getFreeValue();
				int k;
				T v;

				for(int i=keys.Length-1;i>=0;--i)
				{
					if((k=keys[i])!=fv)
					{
						v=values[i];

						sb.Append(k);
						sb.Append(":");

						if(v==null)
						{
							sb.Append("null");
						}
						else
						{
							sb.Append(v.toDataString());
						}

						sb.Append("\n");
					}
				}
			}

			Ctrl.log(StringBuilderPool.releaseStr(sb));
		}
		
		/** intMap转SList */
		public static SList<T> intObjectMapToSList<T>(IntObjectMap<T> map)
		{
			SList<T> sList=new SList<T>(map.length());

			map.forEach((k,v) =>
			{
				sList.add(v);
			});

			return sList;
		}
		
		/** longMap转SList */
		public static SList<T> longObjectMapToSList<T>(LongObjectMap<T> map)
		{
			SList<T> sList=new SList<T>(map.length());

			map.forEach((k,v) =>
			{
				sList.add(v);
			});

			return sList;
		}

		/** SList拷贝SList */
		public static SList<T> sListCopyToSLst<T>(SList<T> sList)
		{
			SList<T> list=new SList<T>(sList.length());

			sList.forEach((v) =>
			{
				list.add(v);
			});
			
			return list;
		}

		/** []数组转对象数组 */
		public static SList<T> arrToSList<T>(T[] arr)
		{
			SList<T> list = new SList<T>(arr.Length);
			for (int i = 0; i < arr.Length; i++)
			{
				list.add(arr[i]);
			}
			return list;
		}
		
		/** intList转slist */
		public static SList<int> intListToSList(IntList arr)
		{
			SList<int> list = new SList<int>(arr.length());
			for (int i = 0; i < arr.length(); i++)
			{
				list.add(arr[i]);
			}
			return list;
		}

		/** 数组排序(为了ILRuntime) */
		public static void arraySort<T>(T[] arr,Comparison<T> compare)
		{
			Array.Sort(arr,new TempCompare<T>(compare));
		}

		private class TempCompare<T>:IComparer<T>
		{
			private Comparison<T> _compare;

			public TempCompare(Comparison<T> compare)
			{
				_compare=compare;
			}

			public int Compare(T x,T y)
			{
				return _compare(x,y);
			}
		}

		/** 确保数组长度 */
		public static bool[] ensureArrayLength(bool[] arr,int length)
		{
			if(arr.Length >= length)
			{
				return arr;
			}

			bool[] re=new bool[length];

			Array.Copy(arr,0,re,0,arr.Length);

			return re;
		}

		/** 确保数组长度 */
		public static int[] ensureArrayLength(int[] arr,int length)
		{
			if(arr.Length >= length)
			{
				return arr;
			}

			int[] re=new int[length];

			Array.Copy(arr,0,re,0,arr.Length);

			return re;
		}

		/** 确保数组长度 */
		public static long[] ensureArrayLength(long[] arr,int length)
		{
			if(arr.Length >= length)
			{
				return arr;
			}

			long[] re=new long[length];

			Array.Copy(arr,0,re,0,arr.Length);

			return re;
		}

		/** 数组putAll */
		public static void arrayPutAll<T>(T[] source,T[] target)
		{
			int len=Math.Max(source.Length,target.Length);

			T v;

			for(int i=0;i<len;i++)
			{
				if((v=target[i])!=null && !v.Equals(default(T)))
				{
					source[i]=v;
				}
			}
		}
	}
}