package com.home.commonManager.tool;

import com.home.commonManager.net.httpResponse.base.ManagerNormalClientHttpResponse;
import com.home.shine.net.httpResponse.BaseHttpResponse;
import com.home.shine.tool.IHttpResponseMaker;

public class ManagerClientHttpResponseMaker implements IHttpResponseMaker
{
	@Override
	public BaseHttpResponse getHttpResponseByID(String id)
	{
		return new ManagerNormalClientHttpResponse();
	}
	
}