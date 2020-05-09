package com.home.shine.dataEx;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.pool.StringBuilderPool;

/** log信息(过程) */
public class LogInfo
{
	/** 分隔符 */
	public static char splitChar=' ';
	
	private StringBuilder _sb;
	
	private String _head;
	private int _num=0;
	
	public LogInfo()
	{
		_sb=StringBuilderPool.create();
	}
	
	public LogInfo(String head)
	{
		_sb=StringBuilderPool.create();
		
		putHead(head);
	}
	
	/** 写头 */
	public void putHead(String head)
	{
		_head=head;
		
		_sb.setLength(0);
		_num=0;
		_sb.append(head);
		_sb.append(' ');
	}
	
	/** 写时间戳 */
	public void putTimestamp()
	{
		put("timestamp",Ctrl.getFixedTimer());
	}
	
	private void putKey(String key)
	{
		if(_num>0)
		{
			_sb.append(splitChar);
		}
		
		_sb.append(key);
		_sb.append('=');
	}
	
	/** 添加 */
	public void put(String key,String value)
	{
		putKey(key);
		_sb.append(value);
		++_num;
	}
	
	/** 添加 */
	public void put(String key,long value)
	{
		putKey(key);
		_sb.append(value);
		++_num;
	}
	
	/** 添加 */
	public void put(String key,int value)
	{
		putKey(key);
		_sb.append(value);
		++_num;
	}
	
	/** 添加 */
	public void put(String key,boolean value)
	{
		putKey(key);
		_sb.append(value ? "true" : "false");
		++_num;
	}
	
	/** 返回字符串并析构 */
	public String releaseString()
	{
		StringBuilder sb=_sb;
		_sb=null;
		return StringBuilderPool.releaseStr(sb);
	}
	
	public String getString()
	{
		return _sb.toString();
	}
	
	public void clear()
	{
		_sb.setLength(0);
		_num=0;
	}
	
	public void clearToHead()
	{
		_sb.setLength(0);
		_num=0;
		_sb.append(_head);
		_sb.append(' ');
	}
	
	/** 返回字符串并析构 */
	public String getStringAndClear()
	{
		String re=getString();
		clear();
		return re;
	}
	
	/** 返回字符串并析构 */
	public String getStringAndClearToHead()
	{
		String re=getString();
		clearToHead();
		return re;
	}
}
