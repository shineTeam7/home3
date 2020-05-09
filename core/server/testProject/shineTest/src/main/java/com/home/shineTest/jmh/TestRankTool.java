package com.home.shineTest.jmh;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestRankTool
{
	//private TRankTool _tool=new TRankTool(1,30000,0);
	//
	//public TestRankTool()
	//{
	//	RankToolData data=new RankToolData();
	//	data.version=1;
	//	data.list=new SList<>(k->new RankData[k],20000);
	//
	//	for(int i=0;i<20000;++i)
	//	{
	//		RankData v=new RankData();
	//		v.key=i+1;
	//		v.value=20000-i;
	//		v.rank=i+1;
	//
	//		data.list.add(v);
	//	}
	//
	//	_tool.setData(data);
	//}
	//
	//@Benchmark
	//public void testRank()
	//{
	//	long key=MathUtils.randomInt(20000)+1;
	//
	//	RankData rankData=_tool.getRankData(key);
	//
	//	long value=rankData.value;
	//
	//	value+=(MathUtils.randomInt(500)-250);
	//
	//	if(value<0)
	//		value=0;
	//
	//	if(value>10000000)
	//		value=10000000;
	//
	//	_tool.commitRank(1,key,value);
	//}
}
