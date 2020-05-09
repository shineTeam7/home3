package com.home.commonGame.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.tool.func.FuncTool;
import com.home.commonGame.net.serverRequest.center.func.pageShow.FuncSendGetPageShowToCenterServerRequest;
import com.home.commonGame.part.player.Player;

/** 逻辑服转中心服翻页显示插件(除了中心服实时数据以外，其他场合是不需要的) */
public class GameToCenterPageShowTool extends FuncTool implements IGamePageShowTool
{
	public GameToCenterPageShowTool(int funcID)
	{
		super(FuncToolType.PageShow,funcID);
	}
	
	@Override
	public void getPageShow(Player player,int page,int arg)
	{
		FuncSendGetPageShowToCenterServerRequest.create(player.role.playerID,_funcID,page,arg).send();
	}
}
