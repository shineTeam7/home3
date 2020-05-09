namespace Shine
{
	/** 字符串方法 */
	export class StringUtils
	{
		private static _numberSizeTable: number[] = [9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Number.MAX_VALUE];

		/** 字符串转boolean */
		public static strToboolean(str: string): boolean
		{
			if (str == null || str == "")
				return false;

			return str == "true" || str == "1";
		}

		/** 字符串转boolean */
		public static booleanToStr(value: boolean): string
		{
				return value ? "1" : "0";
		}

		/** 返回机器字数目 */
		public static getCharMachineNum(str: string): number
		{
			// var re: number = 0;
			// var len: number = str.length;
			// var code: number;
			// for (var i: number = 0; i < len; ++i)
			// {
			// 	code=Char.codePonumberAt(str,i);
			// 		//**********不明结构注释**********
			// 	code = str.charAt(i);

			// 	if (code >= 0 && code <= 255)
			// 	{
			// 			++re;
			// 	}
			// 	else
			// 	{
			// 			re += 2;
			// 	}
			// }

			// return re;

			return 0;
		}

		/** 对象组转字符串 */
		public static objectsToString(objs: any[]): string 
		{
			if (objs.length == 0)
				return "";

			if (objs.length == 1) 
			{
				if (objs[0] == null)
					return "null";
				else
					return objs[0].toString();
			}

			var sb: StringBuilder = StringBuilderPool.create();

			this.writeObjectsToStringBuilder(sb, objs);

			return StringBuilderPool.releaseStr(sb);
		}

		/** 对象组转字符串 */
		public static writeObjectsToStringBuilder(sb: StringBuilder, objs: any[]): void 
		{
			for (var i: number = 0; i < objs.length; ++i)
			{
				if (i > 0) {
					sb.append(',');
				}

				if (objs[i] == null) {
					sb.append("null");
				}
				else
				{
					if (objs[i] instanceof Array)
					{
						this.writeObjectsToStringBuilder(sb, objs[i] as any[]);
					}
					else 
					{
						sb.append(objs[i].toString());
					}
				}
			}
		}

		/** 字符是否为0-9 */
		public static isCharIsNumber(ch:string):boolean
		{
			return ch.charCodeAt(0)>=48 && ch.charCodeAt(0)<=57;
		}

		// private static numberStrSize(arg: number): number 
		// {
		// 	var arg0: number;

		// 	for (arg0 = 0; arg > this._numberSizeTable[arg0]; ++arg0) 
		// 	{
		// 		;
		// 	}

		// 	return arg0 + 1;
		// }

		// /** 以固定位数写入数字 */
		// public static writenumberWei(sb: StringBuilder, num: number, wei: number): void 
		// {
		// 	var size: number = this.numberStrSize(num);

		// 	var last: number = wei - size;

		// 	for (var i: number = 0; i < last; ++i) 
		// 	{
		// 		sb.Append('0');
		// 	}

		// 	sb.Append(num);
		// }


		// /** md5 hash */
		// public static md5Str(str: string): string 
		// {
		// 	return this.md5Byte(Encoding.UTF8.GetBytes(str));
		// }

		// /** md5 hash byteArray */
		// public static md5Byte(bytes: ArrayBuffer): string 
		// {
		// 	var md5: MD5 = new MD5CryptoServiceProvider();
		// 	var result: ArrayBuffer = md5.ComputeHash(bytes);

		// 	var sb: StringBuilder = new StringBuilder();

		// 	for (var i: number = 0; i < result.length; i++) 
		// 	{
		// 		sb.Append(Convert.ToString(result[i], 16));
		// 	}

		// 	return sb.ToString();
		// }

		/** 转成2位小数(四舍五入) */
		public static roundStrF2(value: number): string 
		{
			return value.toFixed(2);
		}

		/** 转成2位小数(四舍五入) */
		public static roundStrD2(value: number): string 
		{
			return value.toFixed(2);
		}

		/** 转成2位小数(向下取整) */
		public static floor(value: number): string 
		{
			return (Math.floor(value)).toString();
		}

		/** 转成2位小数(向下取整) */
		public static floorStrF2(value: number): string 
		{
			return (Math.floor(value*100)/100).toString();
		}

		/** 转成2位小数(向下取整) */
		public static floorStrD2(value: number): string 
		{
			return (Math.floor(value*100)/100).toString();
		}

		// /** 转化字节到显示 */
		// public static toMBString(num: number): string 
		// {
		// 	if (num < 1024)
		// 		return num + "B";

		// 	if (num < 1048576)
		// 		return this.roundStrF2(num / 1024) + "KB";

		// 	if (num < 1073741824)
		// 		return this.roundStrF2(num / 1048576) + "MB";

		// 	return this.roundStrF2(num / 1073741824) + "GB";
		// }

		/** 根据标记进行替换 */
		public static replaceWithSign(str: string, args: string[]): string 
		{
			if (args == null)
				return str;

			if (str.indexOf('$') == -1)
				return str;

			for (var i: number = 0; i < args.length; ++i) 
			{
				str = str.replace("$" + (i + 1), args[i]);
			}

			return str;
		}

		// /** 查找一组字符串中,任意一个出现的位置 */
		// public static indexOf(str: string, arr: string[], fromIndex: number): number
		//  {
		// 	return this.indexOfEx(str, arr, fromIndex).index;
		// }

		// /** 查找一组字符串中,任意一个出现的位置(详细) */
		// private static indexOfEx(str: string, arr: string[], fromIndex: number): IndexOfResult 
		// {
		// 	var result: IndexOfResult = new IndexOfResult();

		// 	var temp: number;

		// 	for (var i: number = 0; i < arr.length; ++i) 
		// 	{
		// 		temp = str.indexOf(arr[i], fromIndex);

		// 		if (temp != -1) 
		// 		{
		// 			if (result.index == -1) 
		// 			{
		// 				result.index = temp;
		// 				//					result.contentInex=i;
		// 				result.content = arr[i];
		// 			}
		// 			else {
		// 				if (temp < result.index) 
		// 				{
		// 					result.index = temp;
		// 					//						result.contentInex=i;
		// 					result.content = arr[i];
		// 				}
		// 			}
		// 		}
		// 	}

		// 	return result;
		// }

		//**********不明结构注释**********
		// /** IndexOf的结果 */
		// private class IndexOfResult
		// {
		// 	/** 找到的序号 */
		// 	public var index:number=-1;
		// 	//		/** 第几个查询单元 */
		// 	//		public number contentInex=-1;
		// 	/** 查询单元内容 */
		// 	public var content:string ="";
		// }

		// /** 找到另一个对应字符的位置(次数计数的) */
		// public static getAnotherIndex(str: string, left: string, right: string, fromIndex: number): number 
		// {
		// 	var re: number = -1;

		// 	var start: number = str.indexOf(left, fromIndex);

		// 	var lastIndex: number = start + left.length;

		// 	var num: number = 1;

		// 	var arr: string[] = [left, right];

		// 	while (true) 
		// 	{
		// 		var temp: IndexOfResult = this.indexOfEx(str, arr, lastIndex);

		// 		if (temp.index != -1) 
		// 		{
		// 			if (temp.content == left) 
		// 			{
		// 				++num;
		// 			}
		// 			else if (temp.content == right) 
		// 			{
		// 				--num;
		// 			}

		// 			lastIndex = temp.index + temp.content.length;

		// 			if (num == 0) 
		// 			{
		// 				re = temp.index;
		// 				break;
		// 			}
		// 		}
		// 		else 
		// 		{
		// 			break;
		// 		}
		// 	}

		// 	return re;
		// }

				/** 以固定位数写入数字 */
		public static  writeIntWei( sb:StringBuilder,num:number,wei:number)
		{
		
		}

	}
}