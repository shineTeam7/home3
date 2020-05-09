package com.home.commonGame.logic.union;

import com.home.commonBase.constlist.generate.FunctionType;
import com.home.commonBase.data.social.roleGroup.CreateRoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupData;
import com.home.commonBase.data.social.union.CreateUnionData;
import com.home.commonBase.data.social.union.UnionData;
import com.home.commonBase.global.Global;
import com.home.commonBase.table.table.UnionNameTable;
import com.home.commonGame.global.GameC;
import com.home.commonGame.logic.func.RoleGroup;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.tool.func.GameRoleGroupTool;
import com.home.shine.support.func.ObjectCall;

/** 工会工具 */
public class UnionTool extends GameRoleGroupTool
{
	public UnionTool()
	{
		super(FunctionType.Union,Global.unionRoleGroupID);
	}
	
	/** 创建玩家群(只创建) */
	@Override
	protected Union toCreateRoleGroup()
	{
		return new Union();
	}
	
	/** 创建玩家群数据(只创建) */
	@Override
	protected UnionData toCreateRoleGroupData()
	{
		return new UnionData();
	}
	
	/** 创建群创建数据 */
	@Override
	protected CreateUnionData toCreateCreateRoleGroupData()
	{
		return new CreateUnionData();
	}
	
	@Override
	protected void toCheckNameRepeatDeep(String name,ObjectCall<Boolean> func)
	{
		UnionNameTable ut=new UnionNameTable();
		ut.name=name;
		ut.load(GameC.db.getCenterConnect(),b->
		{
			func.apply(!b);
		});
	}
}
