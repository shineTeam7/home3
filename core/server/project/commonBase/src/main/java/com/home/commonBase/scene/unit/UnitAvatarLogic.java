package com.home.commonBase.scene.unit;

import com.home.commonBase.config.game.ModelConfig;
import com.home.commonBase.logic.unit.AvatarDataLogic;
import com.home.commonBase.scene.base.UnitLogicBase;
import com.home.shine.support.collection.IntIntMap;

/** 单位显示逻辑 */
public class UnitAvatarLogic extends UnitLogicBase
{
	private AvatarDataLogic _dataLogic;
	
	/** 模型配置 */
	private ModelConfig _modelConfig;
	
	private float _collideRadius=0f;
	
	@Override
	public void init()
	{
		super.init();
		
		if(_unit.canFight())
		{
			_dataLogic=_data.fightDataLogic.avatar;
			
			onRefreshModel(_dataLogic.getModelID());
		}
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		onRefreshModel(-1);
		_dataLogic=null;
	}
	
	@Override
	public void onReloadConfig()
	{
		if(_unit.canFight())
		{
			onRefreshModel(_dataLogic.getModelID());
		}
	}
	
	/** 更新模型 */
	protected void onRefreshModel(int modelID)
	{
		if(modelID>0)
		{
			_modelConfig=ModelConfig.get(modelID);
			_collideRadius=_modelConfig.collideRadius;
		}
		else
		{
			_modelConfig=null;
			_collideRadius=0f;
		}
		
		_unit.pos.refreshCollideRadius();
	}
	
	/** 造型改变 */
	public void onAvatarChange(int modelID,IntIntMap dic)
	{
		onRefreshModel(modelID);
	}
	
	/** 造型部件改变 */
	public void onAvatarPartChange(IntIntMap dic)
	{
	
	}
	
	public int getModelID()
	{
		return _dataLogic.getModelID();
	}
	
	/** 获取模型配置  */
	public ModelConfig getModelConfig()
	{
		return _modelConfig;
	}
	
	/** 获取触碰半径 */
	public float getCollideRadius()
	{
		return _collideRadius;
	}
}
