package com.home.commonTest.app;

import com.home.commonBase.global.AppSetting;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.path.JPSPathFinding;
import com.home.commonGame.app.GameApp;
import com.home.commonTest.control.TestControl;
import com.home.commonTest.jmh.TestItemContainerTool;
import com.home.commonTest.jmh.TestPathFinding;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;

public class TestApp extends GameApp
{
	public static void main(String[] args)
	{
		//ShineSetting.isAllInOne=true;
		//ShineSetting.isRelease=false;
		//AppSetting.init();
		
		//new TestApp().start();
		
		JPSPathFinding jps=TestPathFinding.getJPS();
		
		TestPathFinding testPathFinding=new TestPathFinding();
		
		for(int i=0;i<100;i++)
		{
			testPathFinding.testJPS();
		}
		
		long t=Ctrl.getNanoTimer();
		
		testPathFinding.testJPS();
		
		long t2=Ctrl.getNanoTimer()-t;
		
		Ctrl.print("A1",t2,jps.posEnableTime);
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
