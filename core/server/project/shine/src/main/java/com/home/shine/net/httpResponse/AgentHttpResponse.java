package com.home.shine.net.httpResponse;

import com.home.shine.agent.AgentControl;

/** 代码热更指令 */
public class AgentHttpResponse extends BaseHttpResponse
{
	@Override
	protected void execute()
	{
		result(AgentControl.agentClass());
	}
}
