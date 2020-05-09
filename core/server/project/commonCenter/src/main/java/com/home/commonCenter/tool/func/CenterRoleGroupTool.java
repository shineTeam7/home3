package com.home.commonCenter.tool.func;

import com.home.commonBase.config.game.RoleGroupConfig;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.social.roleGroup.CenterRoleGroupToolData;
import com.home.commonBase.data.social.roleGroup.RoleGroupChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.tool.func.FuncTool;
import com.home.commonCenter.global.CenterC;
import com.home.shine.support.collection.LongObjectMap;

/** 中心服玩家群工具(主要为了做排行榜) */
public class CenterRoleGroupTool extends FuncTool
{
	/** 数据 */
	protected CenterRoleGroupToolData _d;
	/** 配置 */
	protected RoleGroupConfig _config;
	
	protected LongObjectMap<RoleGroupSimpleData> _dic;
	
	public CenterRoleGroupTool(int funcID,int groupID)
	{
		super(FuncToolType.RoleGroup,funcID);
		
		_config=RoleGroupConfig.get(groupID);
	}
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		
		_d=(CenterRoleGroupToolData)data;
	}
	
	@Override
	public void afterReadData()
	{
		super.afterReadData();
		
		_dic=_config.needSave ? _d.roleGroupSimpleDataDic : new LongObjectMap<>(RoleGroupSimpleData[]::new);
	}
	
	public CenterRoleGroupToolData getToolData()
	{
		return _d;
	}
	
	/** 创建新数据 */
	protected FuncToolData createToolData()
	{
		return new CenterRoleGroupToolData();
	}
	
	/** 新创建时 */
	@Override
	public void onNewCreate()
	{
		super.onNewCreate();
		
		_d.roleGroupSimpleDataDic=new LongObjectMap<>(RoleGroupSimpleData[]::new);
	}
	
	public RoleGroupSimpleData getRoleGroup(long groupID)
	{
		return _dic.get(groupID);
	}
	
	/** 添加玩家群简版数据 */
	public void addRoleGroupSimpleData(RoleGroupSimpleData simpleData)
	{
		_dic.put(simpleData.groupID,simpleData);
	}
	
	/** 删除玩家群简版数据 */
	public void removeRoleGroup(long groupID)
	{
		_dic.remove(groupID);
		
		CenterC.global.onRoleGroupDelete(_funcID,groupID);
	}
	
	/** 更新玩家群简版数据 */
	public void refreshRoleGroupSimpleData(RoleGroupSimpleData simpleData)
	{
		RoleGroupSimpleData rData=_dic.get(simpleData.groupID);
		
		if(rData!=null)
		{
			rData.copy(simpleData);
		}
		else
		{
			_dic.put(simpleData.groupID,simpleData);
		}
	}
	
	/** 玩家群局部数据变更 */
	public void doRoleGroupSimpleChange(long groupID,RoleGroupChangeData cData)
	{
		RoleGroupSimpleData rData=_dic.get(groupID);
		
		if(rData!=null)
		{
			rData.onRoleGroupChange(cData);
			
			onRoleGroupSimpleChange(rData,cData);
		}
	}
	
	/** 玩家群数据变更 */
	protected void onRoleGroupSimpleChange(RoleGroupSimpleData rData,RoleGroupChangeData cData)
	{
		CenterC.global.func.onRefreshPartRoleGroup(_funcID,rData.groupID,cData);
	}
}
