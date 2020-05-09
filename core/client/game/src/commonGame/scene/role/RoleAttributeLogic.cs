using System;
using ShineEngine;

/// <summary>
/// 角色属性逻辑
/// </summary>
public class RoleAttributeLogic:RoleLogicBase
{
	/** 属性模块 */
	protected RoleAttributeDataLogic _aTool;

	private MUnitAttributesChangeEventObj _attributeEventObj=new MUnitAttributesChangeEventObj();

	public override void construct()
	{
		base.construct();

		_aTool=new RoleAttributeDataLogic(this);
	}

	public override void init()
	{
		base.init();

		_aTool.setData(_data.attribute.attributes);
	}

	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		_aTool.onPiece(delay);
	}

	/** 获取属性逻辑 */
	public RoleAttributeDataLogic getAttribute()
	{
		return _aTool;
	}

	/** 属性改变 */
	public virtual void onAttributeChange(int[] changeList,int num,bool[] changeSet,int[] lastAttributes)
	{
		Ctrl.print("BB",_role.playerID,GameC.player.role.playerID);

		if(_role.isSelf())
		{
			Ctrl.print("CC");

			_attributeEventObj.index=0;
			_attributeEventObj.changeSet=changeSet;

			GameC.player.dispatch(GameEventType.MRoleAttributeChange,_attributeEventObj);
		}
	}
}