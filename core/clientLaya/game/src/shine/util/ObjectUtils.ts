namespace Shine
{
	export class ObjectUtils
    {
        /** 空字节数组 */
        public static EmptyByteArr: ArrayBuffer = new ArrayBuffer(0);
        /** 空number数组 */
        public static EmptyNumberArr: Array<number> = new Array<number>();
        /** 空obj数组 */
        public static EmptyObjectArr: Array<Object> = new Array<Object>();

        //方法添加

        /** 修复未使用警告 */
        public static fixUnusedWarn(...obj: Object[]): void
        {
        }

        public static hashCode(obj:Object): number
        {
            // return obj == null ? 0 : obj.GetHashCode();
            return 0;
        }

        public static equals(a: Object, b: Object): boolean
        {
            return (a == b) || (a != null && a == b);
        }

        //public static List<K> getSortedMapKeys<K>(SMap<K,object> map)
        //{
        //	List<K> list=map.getKeyList();

        //	list.Sort();

        //	return list;
        //}

        /** 整形数组indexOf */
        public static arrayIndexOf<T>(arr: T[], v: T): number
        {
            var temp: T;

            for (var i: number = 0, len = arr.length; i < len; ++i)
            {
                if ((temp = arr[i]) != null && temp == v)
                    return i;
            }

            return -1;
        }

        public static arrayFill<T>(arr: T[], v: T): void
        {
            for (var i: number = arr.length - 1; i >= 0; --i)
            {
                arr[i] = v;
            }
        }

        public static numberArrayReplaceAll(a: number[], oldValue: number, newValue: number): void
        {
            for (var i: number = a.length - 1; i >= 0; --i)
            {
                if (a[i] == oldValue)
                {
                    a[i] = newValue;
                }
            }
        }

        public static longArrayReplaceAll(a: number[], oldValue: number, newValue: number): void
        {
            for (var i: number = a.length - 1; i >= 0; --i)
            {
                if (a[i] == oldValue)
                {
                    a[i] = newValue;
                }
            }
        }

        /** 整形组转数组set */
        public static intArrayToBooleanSet(arr: number[], length: number): boolean[]
        {
            var re: Array<boolean> = new Array<boolean>();

            for (var i: number = arr.length - 1; i >= 0; --i)
            {
                re[arr[i]] = true;
            }

            return re;
        }

        /** 整形组转number数组set */
        public static intArrayToIntSet(arr: number[]):SSet<number> 
        {
            var re: SSet<number> = new SSet<number> ();

            for (var i: number = arr.length - 1; i >= 0; --i)
            {
                re.add(arr[i]);
            }

            return re;
        }

        /** 转换numbernumber字典 */
        public static parseIntIntMapByDIntArr(arr: DIntData[]): SMap<number,number>
        {
            var map:SMap<number,number> = new SMap<number,number>(arr.length);

            for (var v of arr)
            {
                map.put(v.key, v.value);
            }

            return map;
        }

        //**********不明结构注释**********
        /** 输出数据列表 */
        // public static void prnumberDataList<T>(list:SList<T>)where T:BaseData
        // {
        // 	var sb:StringBuilder =StringBuilderPool.create();

        // 	var data:T;

        // 	for(var i:number=0,len=list.size();i<len;i++)
        // 	{
        // 		data=list.get(i);

        // 		sb.Append(i);
        // 		sb.Append(":");

        // 		if(data==null)
        // 		{
        // 			sb.Append("null");
        // 		}
        // 		else
        // 		{
        // 			sb.Append(data.toDataString());
        // 		}

        // 		sb.Append("\n");
        // 	}

        // 	Ctrl.prnumber(StringBuilderPool.releaseStr(sb));
        // }

        /** numberMap转SList */
        public static numberObjectMapToSList<T>(map: SMap<number,T>):SList<T>
        {
            var sList: SList<T> = new SList<T>(map.length);

            map.forEach((k, v) =>
            {
                sList.add(v);
            });

            return sList;
        }

        /** longMap转SList */
        public static longObjectMapToSList<T>(map:SMap<number,T>): SList<T>
        {
            var sList: SList<T> = new SList<T>(map.length);

            map.forEach((k, v) =>
            {
                sList.add(v);
            });

            return sList;
        }

        /** SList拷贝SList */
        public static sListCopyToSLst<T>(sList: SList<T>): SList<T>
        {
            var list: SList<T> = new SList<T>(sList.length);

            sList.forEach((v) =>
            {
                list.add(v);
            });

            return list;
        }

        /** []数组转对象数组 */
        public static arrToSList<T>(arr: T[]): SList<T>
        {
            var list: SList<T> = new SList<T>(arr.length);
            for (var i: number = 0; i < arr.length; ++i)
            {
                list.add(arr[i]);
            }
            return list;
        }

        /** numberList转slist */
        public static numberListToSList(arr: SList<number>): SList<number>
        {
            var list: SList<number> = new SList<number>(arr.length);
            for (var i: number = 0; i < arr.length; ++i)
            {
                list.add(arr[i]);
            }
            return list;
        }

        // /** 数组排序(为了ILRuntime) */
        // public static arraySort<T>(arr: T[], compare: Comparison<T>): void
        // {
        //     Array.Sort(arr, new TempCompare<T>(compare));
        // }

        //**********不明结构注释**********
        // private class TempCompare<T>:IComparer<T>
        // {
        // 	private Comparison<T> _compare;

        // 	public TempCompare(Comparison<T> compare)
        // 	{
        // 		_compare=compare;
        // 	}

        // 	public number Compare(T x,T y)
        // 	{
        // 		return _compare(x,y);
        // 	}
        // }

        // /** 确保数组长度 */
        // public static ensureArrayLength(arr: Array<Object>, length: number): Array<Object>
        // {
        //     if (arr.length >= length)
        //     {
        //         return arr;
        //     }

        //     var re: Array<Object> = new Array<Object>(length);

        //     Array.Copy(arr, 0, re, 0, arr.length);

        //     return re;
        // }

        //**********重载**********
        // /** 确保数组长度 */
        public static ensureArrayLength(arr:number[] ,length:number):number[]
        {
            if(arr.length >= length)
            {
                return arr;
            }

            var re:Array<number> =new Array(length);

            for(var i=0; i<arr.length; i++)
            {
                re[i]=arr[i]
            }

            return re;
        }

        //**********重载**********
        // /** 确保数组长度 */
        // public static long[] ensureArrayLength(long[] arr,number length)
        // {
        // 	if(arr.Length >= length)
        // 	{
        // 		return arr;
        // 	}

        // 	long[] re=new long[length];

        // 	Array.Copy(arr,0,re,0,arr.Length);

        // 	return re;
        // }

        public static arrayPutAll<T>(source: T[], target: T[]): void
        {
            // for (var i: number = arr.length - 1; i >= 0; --i)
            // {
            //     arr[i] = v;
            // }

            var len:number=Math.max(source.length,target.length);

			var v:T;

			for(var i:number=0;i<len;i++)
			{
				if((v=target[i])!=null)
				{
					source[i]=v;
				}
			}
        }
    }
}