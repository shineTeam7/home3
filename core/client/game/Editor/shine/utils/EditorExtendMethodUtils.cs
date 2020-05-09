using System;

/** 扩展方法组 */
static class EditorExtendMethodUtils
{
	/// <summary>
	/// 为空判定
	/// </summary>
	public static bool isEmpty(this string str)
	{
		return str.Length==0;
	}

	/// <summary>
	/// 字符串拆分
	/// </summary>
	public static string slice(this string str,int startIndex)
	{
		return str.Substring(startIndex);
	}

	/// <summary>
	/// 字符串拆分
	/// </summary>
	public static string slice(this string str,int startIndex,int endIndex)
	{
		return str.Substring(startIndex,endIndex - startIndex);
	}
}