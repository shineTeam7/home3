package com.home.commonBase.sdk.xinge;

import org.json.JSONObject;

public class TimeInterval
{
	private int m_startHour;
	private int m_startMin;
	private int m_endHour;
	private int m_endMin;
	
	public TimeInterval(int startHour,int startMin,int endHour,int endMin)
	{
		this.m_startHour=startHour;
		this.m_startMin=startMin;
		this.m_endHour=endHour;
		this.m_endMin=endMin;
	}
	
	public boolean isValid()
	{
		if((this.m_startHour >= 0) && (this.m_startHour<=23) && (this.m_startMin >= 0) && (this.m_startMin<=59) && (this.m_endHour >= 0) && (this.m_endHour<=23) && (this.m_endMin >= 0) && (this.m_endMin<=59))
		{
			return true;
		}
		return false;
	}
	
	public JSONObject toJsonObject()
	{
		JSONObject json=new JSONObject();
		JSONObject js=new JSONObject();
		JSONObject je=new JSONObject();
		js.put("hour",String.valueOf(this.m_startHour));
		js.put("min",String.valueOf(this.m_startMin));
		je.put("hour",String.valueOf(this.m_endHour));
		je.put("min",String.valueOf(this.m_endMin));
		json.put("start",js);
		json.put("end",je);
		return json;
	}
}