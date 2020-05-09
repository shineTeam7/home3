package com.home.commonData.data.system;

import com.home.shineData.support.MaybeNull;

/** 信息日志数据 */
public class InfoLogDO
{
	/** 日志id */
	int id;
	/** 参数组 */
	@MaybeNull
	String[] args;
	/** 日志时间 */
	long logTime;
}
