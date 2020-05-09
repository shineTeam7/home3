namespace Shine
{
	/** 杂项方法 */
	export class OtherUtils
	{
		/** 数组相等比较 */
		public static dataArrEquals(arr1: BaseData[], arr2: BaseData[]): boolean 
		{
			if (arr1 == null || arr2 == null)
				return false;

			var len: number;
			if ((len = arr1.length) != arr2.length)
				return false;

			for (var i: number = 0; i < len; ++i) 
			{
				if (!arr1[i].dataEquals(arr2[i]))
					return false;
			}

			return true;
		}

		// /** 移除不存在的项，从字典1,参考字典2 */
		// public static removeNotExistFromDic1WithDic2<T1, T2>(dic1: SMap<number, T1>, dic2: SMap<number, T2>): void 
		// {
		// 	var keys: number[] = dic1.getKeys();
		// 	var fv: number = dic1.getFreeValue();
		// 	var k: number;

		// 	for (var i: number = keys.length - 1; i >= 0; --i) 
		// 	{
		// 		if ((k = keys[i]) != fv) 
		// 		{
		// 			if (!dic2.contains(k)) 
		// 			{
		// 				dic1.remove(k);
		// 				++i;
		// 			}
		// 		}
		// 	}
		// }

		// /** 在字典中找寻最小的Key */
		// public static findLeastOfDic<T>(dic: SMap<number, T>, compare: Comparison<T>): number 
		// {
		// 	var keys: number[] = dic.getKeys();
		// 	var values: T[] = dic.getValues();
		// 	var fv: number = dic.getFreeValue();
		// 	var k: number;
		// 	var v: T;

		// 	var temp: T = null;
		// 	var has: boolean = false;
		// 	var re: number = -1;

		// 	for (var i: number = keys.length - 1; i >= 0; --i) 
		// 	{
		// 		if ((k = keys[i]) != fv) 
		// 		{
		// 			v = values[i];

		// 			if (!has) 
		// 			{
		// 				has = true;
		// 				temp = v;
		// 				re = k;
		// 			}
		// 			else 
		// 			{
		// 				if (compare(temp, v) < 0) 
		// 				{
		// 					temp = v;
		// 					re = k;
		// 				}
		// 			}
		// 		}
		// 	}

		// 	return re;
		// }

		//**********重载**********
		// /** 在字典中找寻最小的 */
		// public static findLeastOfDic<T>(dic:numberObjectMap<T> ,compare:Comparison<T> ):number
		// {
		// 	var keys:number[] =dic.getKeys();
		// 	var values:T[] =dic.getValues();
		// 	var fv:number =dic.getFreeValue();
		// 	var k:number ;
		// 	var v:T ;

		// 	T temp=default(T);
		// 	boolean has=false;
		// 	number re=-1;

		// 	for(number i=keys.length-1;i>=0;--i)
		// 	{
		// 		if((k=keys[i])!=fv)
		// 		{
		// 			v=values[i];

		// 			if(!has)
		// 			{
		// 				has=true;
		// 				temp=v;
		// 				re=k;
		// 			}
		// 			else
		// 			{
		// 				if(compare(temp,v)<0)
		// 				{
		// 					temp=v;
		// 					re=k;
		// 				}
		// 			}
		// 		}
		// 	}

		// 	return re;
		// }

		// /** 将元素放入有尺寸限制的集合中 */
		// public static putObjInDicWithMax<T>(key:number,obj:T,dic:SMap<number,T>, max:number,compare:Comparison<T>): void 
		// {
		// 	if (max <= 0) 
		// 	{
		// 		dic.put(key, obj);
		// 	}
		// 	else {
		// 		if (dic.contains(key)) {
		// 			dic.put(key, obj);
		// 		}
		// 		else {
		// 			if (dic.size() >= max) 
		// 			{
		// 				var leastKey: number = this.findLeastOfDic(dic, compare);

		// 				dic.remove(leastKey);
		// 			}

		// 			dic.put(key, obj);
		// 		}
		// 	}
		// }

		//**********重载**********
		// /** 将元素放入有尺寸限制的集合中 */
		// public static void putObjInDicWithMax<T>(number key,T obj,numberObjectMap<T> dic,number max,Comparison<T> compare)
		// {
		// 	if(max<=0)
		// 	{
		// 		dic.put(key,obj);
		// 	}
		// 	else
		// 	{
		// 		if(dic.contains(key))
		// 		{
		// 			dic.put(key,obj);
		// 		}
		// 		else
		// 		{
		// 			if(dic.size()>=max)
		// 			{
		// 				number leastKey=findLeastOfDic(dic,compare);

		// 				dic.remove(leastKey);
		// 			}

		// 			dic.put(key,obj);
		// 		}
		// 	}
		// }
	}
}