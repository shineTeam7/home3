package com.home.commonBase.app;


public class BaseGameApp extends App
{
	public BaseGameApp(String name,int id)
	{
		super(name,id);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		//server
		_server.init();
		_server.connectManager();
	}
	
	public void startNext()
	{
		onStartNext();
	}
	
	protected void onStartNext()
	{
	
	}
}
