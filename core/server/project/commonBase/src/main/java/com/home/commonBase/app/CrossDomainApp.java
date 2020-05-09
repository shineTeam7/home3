package com.home.commonBase.app;

import com.home.shine.ShineSetup;
import com.home.shine.server.CrossDomainServer;

public class CrossDomainApp extends App
{
	private CrossDomainServer _server;
	
	public CrossDomainApp()
	{
		super("cross",-1);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		_server=new CrossDomainServer()
		{
			@Override
			protected void onStartCrossFailed()
			{
				ShineSetup.exit();
			}
		};
		
		_server.init();
		
		startOver();
	}
	
	@Override
	protected void onExit()
	{
		_server.dispose();
		
		exitOver();
	}
}
