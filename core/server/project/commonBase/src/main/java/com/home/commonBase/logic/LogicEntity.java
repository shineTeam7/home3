package com.home.commonBase.logic;

import com.home.commonBase.config.game.InfoCodeConfig;
import com.home.shine.constlist.SLogType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.timer.ITimeEntity;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.StringUtils;

public abstract class LogicEntity implements ITimeEntity
{
	//--random--//
	
	/** 随机一个整形 */
	public int randomInt(int range)
	{
		return MathUtils.randomInt(range);
	}
	
	/** 判定几率 */
	public boolean randomProb(int prob,int max)
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
	public abstract void writeInfo(StringBuilder sb);
	
	//--log--//
	
	private void toLog(String str,int type)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		Ctrl.toLog(sb,type,2);
	}
	
	private void toLog(Object[] args,int type)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,args);
		sb.append(' ');
		writeInfo(sb);
		Ctrl.toLog(sb,type,2);
	}
	
	/** 普通日志 */
	public void log(String str)
	{
		toLog(str,SLogType.Normal);
	}
	
	/** 普通日志 */
	public void log(Object...args)
	{
		toLog(args,SLogType.Normal);
	}
	
	/** 调试日志 */
	public void debugLog(String str)
	{
		if(!ShineSetting.needDebugLog)
			return;
		
		toLog(str,SLogType.Debug);
	}
	
	/** 调试日志 */
	public void debugLog(Object...args)
	{
		if(!ShineSetting.needDebugLog)
			return;
		
		toLog(args,SLogType.Debug);
	}
	
	protected abstract void sendWarnLog(String str);
	
	/** 警告日志 */
	public void warnLog(String str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		String re=sb.toString();
		Ctrl.toLog(sb,SLogType.Warning,1);
		sendWarnLog(re);
	}
	
	/** 警告日志 */
	public void warnLog(Object...args)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,args);
		sb.append(' ');
		writeInfo(sb);
		
		String re=sb.toString();
		Ctrl.toLog(sb,SLogType.Warning,1);
		sendWarnLog(re);
	}
	
	/** 错误日志 */
	public void errorLog(String str)
	{
		toLog(str,SLogType.Error);
	}
	
	/** 错误日志输出错误 */
	public void errorLog(Exception e)
	{
		StringBuilder sb=StringBuilderPool.create();
		Ctrl.writeExceptionToString(sb,"",e);
		sb.append(' ');
		writeInfo(sb);
		Ctrl.toLog(sb,SLogType.Error,1);
	}
	
	/** 错误日志 */
	public void errorLog(Object...args)
	{
		toLog(args,SLogType.Error);
	}
	
	/** 抛错 */
	public void throwError(String str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.toThrowError(sb,null,1);
	}
	
	/** 抛错 */
	public void throwError(Object...args)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,args);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.toThrowError(sb,null,1);
	}
	
	/** 抛错 */
	public void throwError(Throwable e)
	{
		StringBuilder sb=StringBuilderPool.create();
		writeInfo(sb);
		
		Ctrl.toThrowError(sb,e,1);
	}
	
	/** 抛错 */
	public void throwError(String str,Throwable e)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.toThrowError(sb,e,1);
	}
	
	/** 推送信息码 */
	public void sendInfoCode(int code)
	{
	
	}
	
	/** 推送信息码 */
	public void sendInfoCode(int code,String...args)
	{
	
	}
	
	/** 警告信息码 */
	public void warningInfoCode(int code)
	{
		warnLog(InfoCodeConfig.get(code).text);
		sendInfoCode(code);
	}
	
	/** 警告信息码 */
	public void warningInfoCode(int code,Object...args)
	{
		warnLog(InfoCodeConfig.get(code).text,args);
		sendInfoCode(code);
	}
}
