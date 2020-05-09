package com.home.shine.agent;

import java.lang.instrument.Instrumentation;

/** 热更App */
public class AgentApp
{
	public static void premain(String agentArg,Instrumentation inst)
	{
		AgentControl.init(inst);
	}
}
