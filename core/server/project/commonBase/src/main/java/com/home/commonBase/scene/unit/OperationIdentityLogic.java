package com.home.commonBase.scene.unit;

import com.home.commonBase.config.game.OperationConfig;
import com.home.commonBase.data.scene.unit.identity.OperationIdentityData;

/** 操作体身份逻辑 */
public class OperationIdentityLogic extends UnitIdentityLogic
{
	protected OperationIdentityData _iData;
	/** 配置 */
	protected OperationConfig _config;
	
	@Override
	public void init()
	{
		super.init();
		
		_iData=(OperationIdentityData)_data.identity;
		_config=OperationConfig.get(_iData.id);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_iData=null;
		_config=null;
	}
	
	/** 改变状态 */
	public void changeState(int state)
	{
		if(_iData.state==state)
			return;
		
		_iData.state=state;
		
		sendRefreshState(state);
	}
	
	/** 发送刷新状态 */
	protected void sendRefreshState(int state)
	{
	
	}
}
