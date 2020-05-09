package com.home.commonCenter.tool;

import com.home.commonCenter.net.serverHttpResponse.CenterNormalServerHttpResponse;
import com.home.shine.net.httpResponse.BaseHttpResponse;
import com.home.shine.tool.IHttpResponseMaker;

public class CenterServerHttpResponseMaker implements IHttpResponseMaker
{
	@Override
	public BaseHttpResponse getHttpResponseByID(String id)
	{
		return new CenterNormalServerHttpResponse();
	}
}
