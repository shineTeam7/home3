using System;
using System.Text;
using ShineEngine;

/// <summary>
///
/// </summary>
public abstract class LogicEntity:ILogicEntity
{
	public long getTimeMillis()
	{
		return DateControl.getTimeMillis();
	}

	public long getTimeSeconds()
	{
		return getTimeMillis()/1000;
	}

	/** 随机一个整形 */
	public int randomInt(int range)
	{
		return MathUtils.randomInt(range);
	}

	/** 判定几率 */
	public bool randomProb(int prob,int max)
	{
		return MathUtils.randomProb(prob,max);
	}

	/** 随一整形(start<=value<end) */
	public int randomRange(int start,int end)
	{
		if(end<=start)
			return -1;

		return start+randomInt(end-start);
	}

	/** 随一整形(start<=value<=end)(包括结尾) */
	public int randomRange2(int start,int end)
	{
		if(start==end)
			return start;

		return randomRange(start,end+1);
	}

	/** 写描述信息 */
	public virtual void writeInfo(StringBuilder sb)
	{

	}

	//--log--//

	private void toLog(string str,int type)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.Append(str);
		sb.Append(' ');
		writeInfo(sb);
		Ctrl.toLog(sb,type);
	}

	private void toLog(object[] args,int type)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,args);
		sb.Append(' ');
		writeInfo(sb);
		Ctrl.toLog(sb,type);
	}

	/** 普通日志 */
	public void log(string str)
	{
		toLog(str,SLogType.Normal);
	}

	/** 普通日志 */
	public void log(params object[] args)
	{
		toLog(args,SLogType.Normal);
	}

	/** 调试日志 */
	public void debugLog(string str)
	{
		if(!ShineSetting.needDebugLog)
			return;

		toLog(str,SLogType.Debug);
	}

	/** 调试日志 */
	public void debugLog(params object[] args)
	{
		if(!ShineSetting.needDebugLog)
			return;

		toLog(args,SLogType.Debug);
	}

	/** 警告日志 */
	public void warnLog(string str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.Append(str);
		sb.Append(' ');
		writeInfo(sb);

		string re=sb.ToString();
		Ctrl.toLog(sb,SLogType.Warning);
	}

	/** 警告日志 */
	public void warnLog(params object[] args)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,args);
		sb.Append(' ');
		writeInfo(sb);

		string re=sb.ToString();
		Ctrl.toLog(sb,SLogType.Warning);
	}

	/** 错误日志 */
	public void errorLog(string str)
	{
		toLog(str,SLogType.Error);
	}

	/** 错误日志输出错误 */
	public void errorLog(Exception e)
	{
		StringBuilder sb=StringBuilderPool.create();
		Ctrl.writeExceptionToString(sb,"",e);
		sb.Append(' ');
		writeInfo(sb);
		Ctrl.toLog(sb,SLogType.Error);
	}

	/** 错误日志 */
	public void errorLog(params object[] args)
	{
		toLog(args,SLogType.Error);
	}

	/** 抛错 */
	public void throwError(string str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.Append(str);
		sb.Append(' ');
		writeInfo(sb);

		Ctrl.toThrowError(sb,null);
	}

	/** 抛错 */
	public void throwError(params object[]args)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,args);
		sb.Append(' ');
		writeInfo(sb);

		Ctrl.toThrowError(sb,null);
	}

	/** 抛错 */
	public void throwError(Exception e)
	{
		StringBuilder sb=StringBuilderPool.create();
		writeInfo(sb);

		Ctrl.toThrowError(sb,e);
	}

	/** 抛错 */
	public void throwError(String str,Exception e)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.Append(str);
		sb.Append(' ');
		writeInfo(sb);

		Ctrl.toThrowError(sb,e);
	}
}