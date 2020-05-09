package com.home.commonBase.scene.role;

import com.home.commonBase.logic.role.RoleAttributeDataLogic;
import com.home.commonBase.scene.base.RoleLogicBase;
import com.home.shine.support.collection.IntIntMap;

/** 玩家属性逻辑 */
public class RoleAttributeLogic extends RoleLogicBase
{
	/** 属性模块 */
	protected RoleAttributeDataLogic _aTool;
	
	@Override
	public void construct()
	{
		super.construct();
		
		_aTool=new RoleAttributeDataLogic(this);
	}
	
	@Override
	public void init()
	{
		super.init();
		
		_aTool.setData(_data.attribute.attributes);
	}
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
		
		_aTool.onPiece(delay);
	}
	
	/** 获取属性逻辑 */
	public RoleAttributeDataLogic getAttribute()
	{
		return _aTool;
	}
	
	/** 推送自己属性 */
	public void sendSelfAttribute(IntIntMap dic)
	{
	
	}
	
	/** 推送别人属性 */
	public void sendOtherAttribute(IntIntMap dic)
	{
	
	}
	
	/** 属性改变 */
	public void onAttributeChange(int[] changeList,int num,boolean[] changeSet,int[] lastAttributes)
	{
	
	}
}
