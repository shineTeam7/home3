package com.home.commonTest.app;

import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.social.rank.RankToolData;
import com.home.commonBase.global.AppSetting;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.path.JPSPathFinding;
import com.home.commonGame.app.GameApp;
import com.home.commonTest.jmh.TestItemContainerTool;
import com.home.commonTest.jmh.TestPathFinding;
import com.home.commonTest.test.TRankTool;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;
import com.home.shineTest.control.TestControl;

public class TestApp extends GameApp
{
	private static TRankTool _tool=new TRankTool(1,30000,0);
	
	public static void main(String[] args)
	{
		//ShineSetting.isAllInOne=true;
		//ShineSetting.isRelease=false;
		//AppSetting.init();
		
		//new TestApp().start();
		
		RankToolData data=new RankToolData();
		data.version=1;
		data.list=new SList<>(k->new RankData[k],10000);
		_tool.setData(data);
		
		TestControl.testMethod(TestApp::testOne,10000);
	}
	
	private static void testOne()
	{
		int id=MathUtils.randomInt(10000000);
		int v=MathUtils.randomInt(10000000);
		
		_tool.commitRank(1,id,v,null);
		int rank=_tool.getRank(id);
	}
	
	public TestApp()
	{
		super(0);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		//测试容器
		//TestControl.testCls(TestItemContainerTool.class);
		
		
	}
}
