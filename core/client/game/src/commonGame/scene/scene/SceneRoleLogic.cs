using System;
using ShineEngine;

/// <summary>
/// 场景角色逻辑
/// </summary>
public class SceneRoleLogic:SceneLogicBase
{
	/** 角色字典 */
	private LongObjectMap<Role> _roleDic=new LongObjectMap<Role>();
	/** 自身绑定物品包数据 */
	private IntObjectMap<FieldItemBagBindData> _selfFieldItemBagDic=new IntObjectMap<FieldItemBagBindData>();

	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		Role[] values;
		Role v;

		for(int i=(values=_roleDic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				try
				{
					v.onFrame(delay);
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			}
		}
	}

	public override void onSecond()
	{
		if(!_selfFieldItemBagDic.isEmpty())
		{
			long now=_scene.getTimeMillis();

			foreach(FieldItemBagBindData v in _selfFieldItemBagDic)
			{
				if(now>v.removeTime)
				{
					_selfFieldItemBagDic.remove(v.instanceID);
					onRemoveFieldItemBagBind(v);
				}
			}
		}
	}

	public override void dispose()
	{
		foreach(Role v in _roleDic)
		{
			toRemoveRole(v);
		}
	}

	/** 是否需要角色 */
	public bool needRole()
	{
		return SceneTypeConfig.get(_scene.getType()).needRole;
	}

	/** 获取角色对象 */
	public Role getRole(long playerID)
	{
		return _roleDic.get(playerID);
	}

	/** 获取场景角色字典 */
	public LongObjectMap<Role> getRoleDic()
	{
		return _roleDic;
	}

	/** 添加场景角色 */
	public Role addRole(SceneRoleData data)
	{
		if(ShineSetting.openCheck)
		{
			if(_roleDic.contains(data.playerID))
			{
				Ctrl.throwError("场景角色重复添加",data.playerID);
				return null;
			}
		}

		Role role=GameC.pool.rolePool.getOne();
		role.setData(data);
		role.setScene(_scene);

		role.enabled=true;

		_roleDic.put(role.playerID,role);

		if(role.playerID==GameC.player.role.playerID)
		{
			//设置
			_scene.setSelfRole(role);
		}

		role.init();

		onAddRole(role);

		return role;
	}

	/** 移除角色 */
	public void removeRole(long playerID)
	{
		Role role=getRole(playerID);

		if(role==null)
		{
			return;
		}

		toRemoveRole(role);
	}

	/** 执行移除角色 */
	public void toRemoveRole(Role role)
	{
		long playerID=role.playerID;
		// SceneRoleData data=role.getData();

		onRemoveRole(role);

		role.enabled=false;

		role.dispose();
		_roleDic.remove(playerID);

		//回收
		GameC.pool.rolePool.back(role);
	}

	/** 角色加入接口 */
	protected virtual void onAddRole(Role role)
	{

	}

	protected virtual void onRemoveRole(Role role)
	{

	}

	//--掉落部分--//

	/** 添加掉落物品包绑定 */
	protected virtual void onAddFieldItemBagBind(FieldItemBagBindData data)
	{

	}

	/** 移除掉落物品包绑定 */
	protected virtual void onRemoveFieldItemBagBind(FieldItemBagBindData data)
	{

	}

	/** 初始化绑定物品掉落包 */
	public void initFieldItemBagBindDic(IntObjectMap<FieldItemBagBindData> dic)
	{
		_selfFieldItemBagDic=dic;
	}

	public void onAddFieldItemBagBindByServer(FieldItemBagBindData data)
	{
		_selfFieldItemBagDic.put(data.instanceID,data);
		onAddFieldItemBagBind(data);
	}

	public void onRemoveFieldItemBagBindByServer(int instanceID)
	{
		FieldItemBagBindData data=_selfFieldItemBagDic.remove(instanceID);

		if(data!=null)
		{
			onRemoveFieldItemBagBind(data);
		}
	}

	/** 获取绑定物品掉落包 */
	public FieldItemBagBindData getFieldItemBagBind(int instanceID)
	{
		return _selfFieldItemBagDic.get(instanceID);
	}


}