package com.home.shine.tool;

import com.home.shine.net.httpResponse.BaseHttpResponse;

/** http协议构造 */
public interface IHttpResponseMaker
{
	BaseHttpResponse getHttpResponseByID(String id);
}
