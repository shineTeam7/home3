package com.home.shine.tool;

public class MessageBindTool extends ArrayDic<int[]>
{
	public MessageBindTool()
	{
		super(int[][]::new);
	}
	
	/** 是否需要回执 */
	public boolean needReceipt(int mid)
	{
		int[] arr=get(mid);
		
		return arr!=null && arr.length==0;
	}
}
