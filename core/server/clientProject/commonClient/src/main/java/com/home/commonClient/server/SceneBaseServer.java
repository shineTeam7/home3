package com.home.commonClient.server;

import com.home.commonClient.global.ClientC;
import com.home.commonClient.tool.generate.CenterRequestBindTool;
import com.home.commonClient.tool.generate.CenterRequestMaker;
import com.home.commonClient.tool.generate.CenterResponseMaker;
import com.home.commonClient.tool.generate.GameRequestBindTool;
import com.home.commonClient.tool.generate.GameRequestMaker;
import com.home.commonClient.tool.generate.GameResponseMaker;
import com.home.commonClient.tool.generate.SceneBaseRequestBindTool;
import com.home.commonClient.tool.generate.SceneBaseRequestMaker;
import com.home.commonClient.tool.generate.SceneBaseResponseMaker;
import com.home.shine.server.BaseServer;

public class SceneBaseServer extends BaseServer
{
	@Override
	public void init()
	{
		////只需要注册
		//initMessage();
		
		//开关
		setClientReady(true);
	}
}
