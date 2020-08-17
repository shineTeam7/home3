package com.home.commonBase.scene.base;

import com.home.commonBase.data.scene.role.SceneRoleData;
import com.home.commonBase.scene.role.RoleAttributeLogic;
import com.home.commonBase.scene.role.RoleBuildLogic;
import com.home.commonBase.scene.role.RoleForceLogic;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.StringUtils;

/** 场景玩家 */
public class Role extends SceneObject
{
	/** 角色ID */
	public long playerID;
	
	/** 场景角色数据 */
	protected SceneRoleData _data;
	
	/** 属性逻辑 */
	public RoleAttributeLogic attribute;
	
	/** 势力逻辑 */
	public RoleForceLogic force;
	
	/** 建造逻辑 */
	public RoleBuildLogic build;
	
	/** 设置数据 */
	public void setData(SceneRoleData data)
	{
		_data=data;
		playerID=data!=null ? data.playerID : -1;
	}
	
	/** 获取数据 */
	public SceneRoleData getData()
	{
		return _data;
	}
	
	@Override
	protected void registLogics()
	{
		addLogic(attribute=createRoleAttributeLogic());
		addLogic(force=createRoleForceLogic());
		addLogic(build=createRoleBuildLogic());
	}
	
	protected RoleAttributeLogic createRoleAttributeLogic()
	{
		return new RoleAttributeLogic();
	}
	
	protected RoleForceLogic createRoleForceLogic()
	{
		return new RoleForceLogic();
	}
	
	protected RoleBuildLogic createRoleBuildLogic()
	{
		return new RoleBuildLogic();
	}
	
	/** 警告日志 */
	public void warnLog(String str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.warnLog(StringBuilderPool.releaseStr(sb));
	}
	
	/** 警告日志 */
	public void warnLog(Object... args)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,args);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.warnLog(StringBuilderPool.releaseStr(sb));
	}
	
	/** 写描述信息 */
	private void writeInfo(StringBuilder sb)
	{
		sb.append("instanceID:");
		sb.append(playerID);
	}
	
	public BaseSocket getSocket()
	{
		return null;
	}
	
	/** 发送消息 */
	public void send(BaseRequest request)
	{
		BaseSocket socket=getSocket();
		if(socket!=null)
			socket.send(request);
	}
	
	/** 广播消息 */
	public void radioMessage(BaseRequest request)
	{
		radioMessage(request,true);
	}
	
	/** 广播消息 */
	public void radioMessage(BaseRequest request,boolean needSelf)
	{
	
	}
}
