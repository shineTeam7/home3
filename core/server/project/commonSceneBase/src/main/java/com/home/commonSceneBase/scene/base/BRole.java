package com.home.commonSceneBase.scene.base;

import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.role.RoleAttributeLogic;
import com.home.commonSceneBase.scene.role.BRoleAttributeLogic;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;

public class BRole extends Role
{
	protected BScene _bScene;
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_bScene=(BScene)scene;
	}
	
	@Override
	protected RoleAttributeLogic createRoleAttributeLogic()
	{
		return new BRoleAttributeLogic();
	}
	
	@Override
	public void radioMessage(BaseRequest request,boolean needSelf)
	{
		request.write();
		
		_scene.role.getRoleDic().forEachValue(v->
		{
			if(v.playerID!=playerID || needSelf)
			{
				BaseSocket socket=v.getSocket();
				
				if(socket!=null)
				{
					socket.send(request);
				}
			}
		});
	}
}