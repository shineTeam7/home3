using System;
using ShineEngine;

/// <summary>
/// 单位造型逻辑
/// </summary>
public class UnitAvatarLogic:UnitLogicBase
{
	/** 数据逻辑 */
	private AvatarDataLogic _dataLogic;
	/** 模型配置 */
	private ModelConfig _modelConfig;

	private float _collideRadius=0f;

	public override void init()
	{
		base.init();

		if(_unit.canFight())
		{
			_dataLogic=_unit.getUnitData().fightDataLogic.avatar;

			onRefreshModel(_dataLogic.getModelID());
		}
	}
	
	public override void dispose()
	{
		base.dispose();

		onRefreshModel(-1);
		_dataLogic=null;
	}

	public override void onReloadConfig()
	{
		if(_unit.canFight())
		{
			onRefreshModel(_dataLogic.getModelID());
		}
	}

	/** 获取模型ID */
	public int getModelID()
	{
		return _dataLogic.getModelID();
	}

	/** 获取模型配置 */
	public ModelConfig getModelConfig()
	{
		return _modelConfig;
	}

	/** 获取触碰半径 */
	public float getCollideRadius()
	{
		return _collideRadius;
	}

	public int getShowPart(int type)
	{
		if(_dataLogic==null)
			return -1;

		return _dataLogic.getShowPart(type);
	}

	/** 更新模型 */
	protected virtual void onRefreshModel(int modelID)
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
	}

	/** 造型改变 */
	public virtual void onAvatarChange(int modelID)
	{
		onRefreshModel(modelID);
	}
	
	/** 造型部件改变 */
	public virtual void onAvatarPartChange(IntIntMap dic)
	{

	}
}