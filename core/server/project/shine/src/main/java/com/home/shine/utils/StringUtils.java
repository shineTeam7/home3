package com.home.shine.utils;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.VBoolean;
import com.home.shine.support.MD5;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.pool.StringBuilderPool;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 字符串方法 */
public class StringUtils
{
	/** UTF Charset */
	public static final Charset UTFCharset=Charset.forName("UTF-8");
	
	private static final int[] _intSizeTable=new int[]{9,99,999,9999,99999,999999,9999999,99999999,999999999,Integer.MAX_VALUE};
	
	private static final DecimalFormat _formatFor2=new DecimalFormat("##0.00");
	private static final DecimalFormat _floatFormatFor2=new DecimalFormat("0.00");
	
	private static Pattern _noneContentStrReg=Pattern.compile("[^ ^\\n^\\r^\\t]+?");
	
	/** 字符串是否是空的 */
	public static boolean isNullOrEmpty(String str)
	{
		return str==null || str.isEmpty();
	}
	
	/** 字符串是否是空的 */
	public static boolean isNoneContent(String str)
	{
		return !_noneContentStrReg.matcher(str).find();
	}
	
	/** 字符串转boolean */
	public static boolean strToBoolean(String str)
	{
		if(str==null || str.isEmpty())
			return false;
		
		return str.equals("true") || str.equals("1");
	}
	
	/** 字符串转int */
	public static int strToInt(String str)
	{
		if(str==null || str.isEmpty())
			return 0;
		
		return Integer.valueOf(str);
	}
	
	/** 字符串转boolean */
	public static String booleanToStr(boolean value)
	{
		return value ? "1" : "0";
	}
	
	/** 首字母变大写 */
	public static String ucWord(String str)
	{
		if(str.isEmpty())
		{
			return str;
		}
		
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.setCharAt(0,Character.toUpperCase(str.charAt(0)));
		
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 首字母变小写 */
	public static String lcWord(String str)
	{
		if(str.isEmpty())
		{
			return str;
		}
		
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.setCharAt(0,Character.toLowerCase(str.charAt(0)));
		
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 返回机器字数目 */
	public static int getCharMachineNum(String str)
	{
		if(str.isEmpty())
			return 0;
		
		int re=0;
		int len=str.length();
		int code;
		for(int i=0;i<len;++i)
		{
			code=str.charAt(i);
			//code=Character.codePointAt(str,i);
			
			if(code >= 0 && code<=255)
			{
				++re;
			}
			else
			{
				re+=2;
			}
		}
		
		return re;
	}
	
	/** 字符是否为0-9 */
	public static boolean isCharIsNumber(char ch)
	{
		return ch>=48 && ch<=57;
	}
	
	///** float转String,保留几位小数 */
	//public static String floatToString(float value,int wei)
	//{
	//	return String.format("%."+wei+"f",value);
	//}
	
	/** 对象组转字符串 */
	public static String objectsToString(Object[] objs)
	{
		if(objs.length==0)
		{
			return "";
		}
		
		if(objs.length==1)
		{
			if(objs[0]==null)
			{
				return "null";
			}
			else
			{
				return objs[0].toString();
			}
		}
		
		StringBuilder sb=StringBuilderPool.create();
		writeObjectsToStringBuilder(sb,objs);
		
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 对象组写到StringBuilder中 */
	public static void writeObjectsToStringBuilder(StringBuilder sb,Object[] objs)
	{
		for(int i=0;i<objs.length;++i)
		{
			if(i!=0)
			{
				sb.append(' ');
			}
			
			if(objs[i]==null)
			{
				sb.append("null");
			}
			else
			{
				if(objs[i] instanceof Object[])
				{
					writeObjectsToStringBuilder(sb,(Object[])objs[i]);
				}
				else
				{
					sb.append(objs[i].toString());
				}
			}
		}
	}
	
	private static int intStrSize(int arg)
	{
		int arg0;
		
		for(arg0=0;arg>_intSizeTable[arg0];++arg0)
		{
			;
		}

		return arg0 + 1;
	}
	
	/** 以固定位数写入数字 */
	public static void writeIntWei(StringBuilder sb,int num,int wei)
	{
		int size=intStrSize(num);
		
		int last=wei - size;
		
		for(int i=0;i<last;++i)
		{
			sb.append('0');
		}
		
		sb.append(num);
	}
	
	/** 获取jsonObject */
	public static JSONObject getJsonArgs(String content)
	{
		JSONObject args=null;
		
		try
		{
			args = new JSONObject(content);
		}
		catch(Exception e)
		{
		
		}
		
		return args;
	}
	
	/** 获取网页参数(用"&"和"="分割) */
	public static SMap<String,String> getWebArgs(String content)
	{
		SMap<String,String> re=new SMap<String,String>();
		
		makeWebArgs(content,re);
		
		return re;
	}
	
	/** 获取网页参数(用"&"和"="分割) */
	public static void makeWebArgs(String content,SMap<String,String> dic)
	{
		if(content!=null && !content.isEmpty())
		{
			String[] list=content.split("\\&");
			
			String[] arr;
			
			for(String ss : list)
			{
				arr=ss.split("=");
				
				if(arr.length==2)
				{
					dic.put(arr[0],arr[1]);
				}
			}
		}
	}
	
	/** 通过字典设置网页参数 */
	public static String setWebArgs(SMap<String,String> dic,boolean needUrlEncode)
	{
		StringBuilder sb=StringBuilderPool.create();
		
		VBoolean is2=new VBoolean();
		
		dic.forEach((k,v)->
		{
			if(is2.value)
			{
				sb.append('&');
			}
			
			sb.append(k);
			sb.append('=');
			
			if(needUrlEncode)
			{
				sb.append(urlEncode(v));
			}
			else
			{
				sb.append(v);
			}
			
			is2.value=true;
		});
		
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** urlEncode */
	public static String urlEncode(String value)
	{
		String re=value;
		
		try
		{
			re=URLEncoder.encode(value,"utf-8");
		}
		catch(Exception e)
		{
			Ctrl.throwError("urlEncode出错" + value,e);
		}
		
		return re;
	}
	
	/** urlDncode */
	public static String urlDecode(String value)
	{
		String re=value;
		
		try
		{
			re=URLDecoder.decode(value,"utf-8");
		}
		catch(Exception e)
		{
			Ctrl.throwError("urlDncode出错" + value,e);
		}
		
		return re;
	}
	
	//正则
	
	/** 正则替换一次 */
	public static String replaceOnce(String src,String reg,String replacement)
	{
		return replaceOnce(src,Pattern.compile(reg),replacement);
	}
	
	/** 正则替换一次 */
	public static String replaceOnce(String src,Pattern reg,String replacement)
	{
		return reg.matcher(src).replaceFirst(replacement);
	}
	
	/** 正则替换全部 */
	public static String replaceAll(String src,String reg,String replacement)
	{
		return replaceAll(src,Pattern.compile(reg),replacement);
	}
	
	/** 正则替换全部 */
	public static String replaceAll(String src,Pattern reg,String replacement)
	{
		return reg.matcher(src).replaceAll(replacement);
	}
	
	/** 去掉两边空格 */
	public static String cutOutsideSpace(String str)
	{
		return str.trim();
	}
	
	/** 去掉首尾各一个字符 */
	public static String cutOutsideOne(String str)
	{
		return str.substring(1,str.length()-1);
	}
	
	/** 去除全部空格 */
	public static String cutAllSpace(String str)
	{
		return str.replace(" ","");
	}
	
	///** 查找一组字符串中,任意一个出现的位置 */
	//public static int indexOf(String str,String[] arr,int fromIndex)
	//{
	//	return indexOfEx(str,arr,fromIndex).index;
	//}
	
	/** 查找一组字符串中,任意一个出现的位置 */
	public static int indexOf(String str,char[] arr,int fromIndex)
	{
		for(int i=fromIndex,len=str.length();i<len;i++)
		{
			char c=str.charAt(i);
			
			for(int j=arr.length-1;j>=0;--j)
			{
				if(arr[j]==c)
					return i;
			}
		}
		
		return -1;
	}
	
	/** 查找一组字符串中,任意一个出现的位置(详细) */
	private static IndexOfResult indexOfEx(String str,String[] arr,int fromIndex)
	{
		IndexOfResult result=new IndexOfResult();
		
		int temp;
		
		for(int i=0;i<arr.length;i++)
		{
			temp=str.indexOf(arr[i],fromIndex);
			
			if(temp!=-1)
			{
				if(result.index==-1)
				{
					result.index=temp;
					//					result.contentInex=i;
					result.content=arr[i];
				}
				else
				{
					if(temp<result.index)
					{
						result.index=temp;
						//						result.contentInex=i;
						result.content=arr[i];
					}
				}
			}
		}
		
		return result;
	}
	
	/** IndexOf的结果 */
	private static class IndexOfResult
	{
		/** 找到的序号 */
		public int index=-1;
		//		/** 第几个查询单元 */
		//		public int contentInex=-1;
		/** 查询单元内容 */
		public String content="";
	}
	
	/** 找到另一个对应字符的位置(次数计数的) */
	public static int getAnotherIndex(String str,String left,String right,int fromIndex)
	{
		int re=-1;
		
		int start=str.indexOf(left,fromIndex);
		
		int lastIndex=start + left.length();
		
		int num=1;
		
		String[] arr=new String[]{left,right};
		
		while(true)
		{
			IndexOfResult temp=indexOfEx(str,arr,lastIndex);
			
			if(temp.index!=-1)
			{
				if(temp.content.equals(left))
				{
					++num;
				}
				else if(temp.content.equals(right))
				{
					--num;
				}
				
				lastIndex=temp.index + temp.content.length();
				
				if(num==0)
				{
					re=temp.index;
					break;
				}
			}
			else
			{
				break;
			}
		}
		
		return re;
	}
	
	/** 找到另一个对应字符的位置(次数计数的) */
	public static int getAnotherIndex(String str,char left,char right,int fromIndex)
	{
		int re=-1;
		
		int start=str.indexOf(left,fromIndex);
		
		int lastIndex=start + 1;
		
		int num=1;
		
		char[] arr=new char[]{left,right};
		
		while(true)
		{
			int dd=indexOf(str,arr,lastIndex);
			
			if(dd!=-1)
			{
				char cc=str.charAt(dd);
				
				if(cc==left)
				{
					++num;
				}
				else if(cc==right)
				{
					--num;
				}
				
				lastIndex=dd + 1;
				
				if(num==0)
				{
					re=dd;
					break;
				}
			}
			else
			{
				break;
			}
		}
		
		return re;
	}
	
	/** 切割字符串(过滤掉括号) */
	public static String[] splitEx(String str,char leftEx,char rightEx,char parten)
	{
		int open=0;
		IntList dds=new IntList();
		char cc;
		for(int i=0,len=str.length();i<len;i++)
		{
			cc=str.charAt(i);
			
			if(cc==parten)
			{
				if(open==0)
				{
					dds.add(i);
				}
			}
			else if(cc==leftEx)
			{
				open++;
			}
			else if(cc==rightEx)
			{
				open--;
			}
		}

		if(open!=0)
		{
			Ctrl.errorLog("字符串出错");
			return null;
		}
		
		if(dds.isEmpty())
		{
			return new String[]{str};
		}

		String[] reArgs=new String[dds.size()+1];

		int s=0;
		int index;
		for(int i=0;i<dds.size();i++)
		{
			reArgs[i]=str.substring(s,index=dds.get(i));
			s=index+1;
		}
		
		reArgs[dds.size()]=str.substring(s);
		
		return reArgs;
	}
	
	/** 获取下个可用字符(对代码解析) */
	public static int getNextExceptChar(String str,char[] arr,int fromIndex)
	{
		for(int i=fromIndex,len=str.length();i<len;i++)
		{
			char c=str.charAt(i);
			boolean has=false;
			
			for(int j=0;j<arr.length;j++)
			{
				if(arr[j]==c)
				{
					has=true;
					break;
				}
			}
			
			if(!has)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/** 获取下个可用字符(对代码解析) */
	public static int getNextEnableChar(String str,int fromIndex)
	{
		for(int i=fromIndex,len=str.length();i<len;i++)
		{
			char c=str.charAt(i);
			
			switch(c)
			{
				case ' ':
				case '\n':
				case '\r':
				case '\t':
					break;
				default:
				{
					return i;
				}
			}
		}
		
		return -1;
	}
	
	/** 从完全限定名获取类名 */
	public static String getClassNameForQName(String qName)
	{
		if(qName.isEmpty())
			return "";
		
		return qName.substring(qName.lastIndexOf('.') + 1,qName.length());
	}
	
	/** 从完全限定名获取包名 */
	public static String getPackageNameForQName(String qName)
	{
		return qName.substring(0,qName.lastIndexOf('.'));
	}
	
	/** list转String(逗号隔开) */
	@SuppressWarnings("rawtypes")
	public static String getListStr(List list)
	{
		StringBuilder sb=StringBuilderPool.create();
		
		int i=0;
		
		for(Object obj : list)
		{
			if(i>0)
			{
				sb.append(',');
			}
			
			sb.append(obj.toString());
			
			++i;
		}
		
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** md5 hash */
	public static String md5(String str)
	{
		if(str==null)
			return "";
		
		return MD5.hash(str);
	}
	
	/** md5 hash byteArray */
	public static String md5(byte[] bytes)
	{
		if(bytes==null)
			return "";
		
		return MD5.hashBytes(bytes);
	}
	
	/** 转成两位小数(四舍五入) */
	public static String roundStrF2(float value)
	{
		return _formatFor2.format(value);
	}
	
	/** 转成两位小数(四舍五入) */
	public static String roundStrD2(double value)
	{
		return _formatFor2.format(value);
	}
	
	/** 转成两位小数(向下取整) */
	public static String floorStrF2(float value)
	{
		return String.valueOf(Math.floor(value*100)/100);
	}
	
	/** 转成两位小数(向下取整) */
	public static String floorStrD2(double value)
	{
		return _floatFormatFor2.format(Math.floor(value*100)/100);
	}
	
	/** 转化字节到显示 */
	public static String toMBString(long num)
	{
		if(num<1024)
			return num+"B";
		
		if(num<1048576)
			return roundStrF2(num/1024f) + "KB";
		
		if(num<1073741824)
			return roundStrF2(num/1048576f) + "MB";
		
		return roundStrF2(num/1073741824f) + "GB";
	}
	
	/** string的标准replace(\n,\r,\t这些) */
	public static String stringNormalReplace(String str)
	{
		str=str.replace("\\n","\n")
				.replace("\\t","\t")
				.replace("\\r","\r")
				.replace("\\\\","\\");
		
		return str;
	}
	
	/** 数据拼接字符串 */
	public static String join(Object[] array,String separator)
	{
		return array==null ? null : join((Object[])array,separator,0,array.length);
	}
	
	/** 数据拼接字符串 */
	public static String join(Object[] array,String separator,int startIndex,int endIndex)
	{
		if(array==null)
		{
			return null;
		}
		else
		{
			if(separator==null)
			{
				separator="";
			}
			
			int noOfItems=endIndex - startIndex;
			if(noOfItems<=0)
			{
				return "";
			}
			else
			{
				StringBuilder buf=new StringBuilder(noOfItems);
				
				for(int i=startIndex;i<endIndex;++i)
				{
					if(i>startIndex)
					{
						buf.append(separator);
					}
					
					if(array[i]!=null)
					{
						buf.append(array[i]);
					}
				}
				
				return buf.toString();
			}
		}
	}
	
	/** 拆分str到set */
	public static SSet<String> splitStrToSet(String str,String parten)
	{
		String[] arr=str.split(parten);
		
		SSet<String> re=new SSet<>(arr.length);
		re.addAll(arr);
		return re;
	}
	
	/** 将set写出String */
	public static String writeSetToStr(SSet<String> strs,String parten)
	{
		StringBuilder sb=StringBuilderPool.create();
		
		boolean isNext=false;
		
		String[] keys=strs.getKeys();
		String k;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=null)
			{
				if(isNext)
				{
					sb.append(parten);
				}
				
				sb.append(k);
				isNext=true;
			}
		}
		
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 是否文本字符(a-z,A-Z,0-9) */
	public static boolean isTextChar(char c)
	{
		if('a'<=c && c<='z')
			return true;
		
		if('A'<=c && c<='Z')
			return true;
		
		if('0'<=c && c<='9')
			return true;
		
		return false;
	}
	
	/** 使用正则表达式将Unicode编码字符串转换为string字符串(包括中文、英文、数字、emoji表情) */
	public static String unicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{2,4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str;
	}

	/** 替换使用正则表达式将U+编码字符串转换为string字符串(包括中文、英文、数字、emoji表情) */
	public static String replaceUnicodePlusToString(String str) {
		Pattern pattern = Pattern.compile("(U\\+(\\p{XDigit}{4,5}))");
		Matcher matcher = pattern.matcher(str);
		char[] ch;

		while (matcher.find())
		{
			ch = Character.toChars(Integer.parseInt(matcher.group(2), 16));
			str=str.replace(matcher.group(1),String.valueOf(ch));
		}
		return str;
	}
	
	/** string字符串(包括中文、英文、数字、emoji表情) 转换为Unicode编码字符串*/
	public static String stringToUnicode(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			if (ch > 255)
				str += "\\u" + Integer.toHexString(ch);
			else
				str += String.valueOf(ch);
		}
		return str;
	}
}
