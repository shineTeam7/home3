package com.home.commonSceneBase.server;

import com.home.commonBase.server.BaseGameServer;
import com.home.commonSceneBase.constlist.generate.SceneBaseRequestType;
import com.home.commonSceneBase.constlist.generate.SceneBaseResponseType;
import com.home.commonSceneBase.tool.generate.SceneBaseRequestMaker;
import com.home.commonSceneBase.tool.generate.SceneBaseResponseBindTool;
import com.home.commonSceneBase.tool.generate.SceneBaseResponseMaker;
import com.home.shine.control.BytesControl;

public class SceneBaseServer extends BaseGameServer
{
	@Override
	protected void initMessage()
	{
		super.initMessage();
		
		BytesControl.addMessageConst(SceneBaseRequestType.class,true,false);
		BytesControl.addMessageConst(SceneBaseResponseType.class,false,false);
		addRequestMaker(new SceneBaseRequestMaker());
		addClientResponseMaker(new SceneBaseResponseMaker());
		addClientResponseBind(new SceneBaseResponseBindTool());
	}
}
