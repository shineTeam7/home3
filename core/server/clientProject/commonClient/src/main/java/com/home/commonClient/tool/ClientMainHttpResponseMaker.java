package com.home.commonClient.tool;

import com.home.commonClient.net.httpResponse.ClientMainHttpResponse;
import com.home.shine.net.httpResponse.BaseHttpResponse;
import com.home.shine.tool.IHttpResponseMaker;

public class ClientMainHttpResponseMaker implements IHttpResponseMaker
{
	@Override
	public BaseHttpResponse getHttpResponseByID(String id)
	{
		return new ClientMainHttpResponse();
	}
	
}