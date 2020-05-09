package com.home.commonCenter.net.serverHttpResponse;

import com.home.commonCenter.net.base.CenterServerHttpResponse;
import com.home.shine.constlist.HttpContentType;

public class CenterNormalServerHttpResponse extends CenterServerHttpResponse
{
	@Override
	protected void execute()
	{
		result(true,HttpContentType.Text);
	}
}
