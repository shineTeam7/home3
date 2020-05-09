package com.home.shine.constlist;

/** 逻辑线程类型(for response) */
public class ThreadType
{
	/** 非shine线程 */
	public static final int None=0;
	/** 主线程 */
	public static final int Main=1;
	/** 杂项线程 */
	public static final int Mixed=2;
	/** 日志线程 */
	public static final int Log=3;
	/** IO线程 */
	public static final int IO=4;
	/** 池线程 */
	public static final int Pool=5;
	/** db池线程 */
	public static final int DBPool=6;
	/** db写线程 */
	public static final int DBWrite=7;
	/** 观测线程 */
	public static final int Watcher=8;
	
	
}
