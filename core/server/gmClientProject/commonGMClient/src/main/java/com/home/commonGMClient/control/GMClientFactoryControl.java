package com.home.commonGMClient.control;

import com.home.commonBase.control.FactoryControl;

public class GMClientFactoryControl extends FactoryControl
{
	public GMClientMainControl createMainControl()
	{
		return new GMClientMainControl();
	}
}
