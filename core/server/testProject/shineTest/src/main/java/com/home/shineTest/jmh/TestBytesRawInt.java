package com.home.shineTest.jmh;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.support.collection.SList;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestBytesRawInt
{
	private int _len=10000;
	private BytesWriteStream _ws=new BytesWriteStream();
	private BytesReadStream _rs=new BytesReadStream();
	
	@Benchmark
	public void testLocalWrite()
	{
		_ws.clear();
		for(int i=0;i<_len;i++)
		{
			_ws.writeInt(i);
		}
		
		_rs.setBuf(_ws.getBuf());
		
		for(int i=0;i<_len;i++)
		{
			_rs.readInt();
		}
	}
	
	@Benchmark
	public void testRawWrite()
	{
		_ws.clear();
		for(int i=0;i<_len;i++)
		{
			_ws.writeSInt(i);
		}
		
		_rs.setBuf(_ws.getBuf());
		
		for(int i=0;i<_len;i++)
		{
			_rs.readSInt();
		}
	}
}
