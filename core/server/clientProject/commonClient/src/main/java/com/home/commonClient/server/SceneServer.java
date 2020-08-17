package com.home.commonClient.server;

import com.home.commonClient.global.ClientC;
import com.home.commonClient.tool.generate.SceneRequestBindTool;
import com.home.commonClient.tool.generate.SceneRequestMaker;
import com.home.commonClient.tool.generate.SceneResponseMaker;

public class SceneServer extends SceneBaseServer
{
	@Override
	public void init()
	{
		setClientResponseMaker(ClientC.main.getSceneResponseMaker());
		
		super.init();
	}
}
