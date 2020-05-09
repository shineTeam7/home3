using System;

namespace ShineEngine
{
	/// <summary>
	/// 数组字典
	/// </summary>
	public class ArrayDic<T>
	{
		/** 偏移 */
		public int offSet;

		/** 构造组 */
		public T[] list;

		/** 通过id取得数据 */
		public T get(int key)
		{
			T[] temp;
			if((temp=list)==null)
				return default(T);

			int index=key - offSet;

			if(index<0 || index>=temp.Length)
				return default(T);

			return temp[index];
		}

		/** 添加一组 */
		public void addDic(ArrayDic<T> dic)
		{
			if(list==null)
			{
				list=dic.list;
				offSet=dic.offSet;
			}
			else
			{
				int off=Math.Min(dic.offSet,offSet);
				int nowLen=offSet + list.Length;
				int targetLen=dic.offSet + dic.list.Length;
				int max=Math.Max(targetLen,nowLen);

				//有变化
				if(!(off==offSet && max==nowLen))
				{
					T[] temp=new T[max - off];

					Array.Copy(list,0,temp,offSet-off,list.Length);
					offSet=off;
					list=temp;
				}

				T[] arr=dic.list;
				T[] selfList=this.list;
				int arrOff=dic.offSet - offSet;

				for(int i=arr.Length-1;i>=0;i--)
				{
					if(arr[i]!=null)
					{
						selfList[i+arrOff]=arr[i];
					}
				}
			}
		}
	}
}