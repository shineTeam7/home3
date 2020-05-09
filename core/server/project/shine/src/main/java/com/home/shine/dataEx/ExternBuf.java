package com.home.shine.dataEx;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;

/** 外接buf */
public class ExternBuf
{
	public float[] fArr=new float[ShineSetting.externBufSize];
	public int[] iArr=new int[ShineSetting.externBufSize];
	public long[] lArr=new long[ShineSetting.externBufSize];
	
	private int _fIndex=0;
	private int _iIndex=0;
	private int _lIndex=0;
	
	public void clear()
	{
		_fIndex=0;
		_iIndex=0;
		_lIndex=0;
	}
	
	public void writeFloat(float v)
	{
		fArr[_fIndex++]=v;
	}
	
	public void writeInt(int v)
	{
		iArr[_iIndex++]=v;
	}
	
	public void writeLong(long v)
	{
		lArr[_lIndex++]=v;
	}
	
	public float readFloat()
	{
		return fArr[_fIndex++];
	}
	
	public int readInt()
	{
		return iArr[_iIndex++];
	}
	
	public long readLong()
	{
		return lArr[_lIndex++];
	}
}
