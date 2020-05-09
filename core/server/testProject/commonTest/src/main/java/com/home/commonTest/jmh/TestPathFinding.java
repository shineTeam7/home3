package com.home.commonTest.jmh;

import com.home.commonBase.constlist.generate.MapMoveType;
import com.home.commonBase.scene.path.JPSPathFinding;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntList;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestPathFinding
{
	private static int _len=30;
	private static boolean[][] _grids;
	
	private static JPSPathFinding _jps;
	
	static
	{
		_grids=new boolean[_len][_len];
		
		int g=_len/10;
		int k=_len*7/10;
		boolean b=false;
		
		for(int i=(g/2);i<_len;i+=g)
		{
			if(b)
			{
				for(int j=_len-k;j<_len;j++)
				{
					_grids[i][j]=true;
				}
			}
			else
			{
				for(int j=0;j<k;j++)
				{
					_grids[i][j]=true;
				}
			}
			
			b=!b;
		}
		
		for(int i=0;i<_len;i++)
		{
			StringBuilder sb=new StringBuilder();
			
			for(int j=0;j<_len;j++)
			{
				if(j>0)
					sb.append(',');
				
				sb.append(_grids[j][i] ? "1" : "0");
			}
			
			Ctrl.print(sb);
		}
		
		_jps=new JPSPathFinding()
		{
			@Override
			public boolean isEnable(int moveType,boolean needCrowed,int x,int y)
			{
				long t=Ctrl.getNanoTimer();
				
				boolean re=x>=0 && x<_len && y>=0 && y<_len && !_grids[x][y];
				
				posEnableTime+=(Ctrl.getNanoTimer()-t);
				
				return re;
			}
		};
	}
	
	private IntList _result=new IntList();
	
	@Benchmark
	public void testJPS()
	{
		_jps.posEnableTime=0L;
		
		_jps.findPath(_result,MapMoveType.Land,false,0,0,_len-1,_len-1);
	}
	
	public static JPSPathFinding getJPS()
	{
		return _jps;
	}
}
