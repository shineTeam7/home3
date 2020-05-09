package com.home.commonBase.sdk.xinge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Vector;

public class MessageIOS
{
	public static final int TYPE_APNS_NOTIFICATION=11;
	public static final int TYPE_REMOTE_NOTIFICATION=12;
	private int m_expireTime;
	private String m_sendTime;
	private Vector<TimeInterval> m_acceptTimes;
	private int m_type;
	private Map<String,Object> m_custom;
	private String m_raw;
	private String m_alertStr;
	private JSONObject m_alertJo;
	private int m_badge;
	private String m_sound;
	private String m_category;
	private int m_loopInterval;
	private int m_loopTimes;
	
	public MessageIOS()
	{
		this.m_sendTime="2014-03-13 16:13:00";
		this.m_acceptTimes=new Vector();
		this.m_raw="";
		this.m_alertStr="";
		this.m_alertJo=new JSONObject();
		this.m_badge=0;
		this.m_sound="";
		this.m_category="";
		this.m_loopInterval=-1;
		this.m_loopTimes=-1;
		this.m_type=11;
	}
	
	public void setType(int type)
	{
		this.m_type=type;
	}
	
	public int getType()
	{
		return this.m_type;
	}
	
	public void setExpireTime(int expireTime)
	{
		this.m_expireTime=expireTime;
	}
	
	public int getExpireTime()
	{
		return this.m_expireTime;
	}
	
	public void setSendTime(String sendTime)
	{
		this.m_sendTime=sendTime;
	}
	
	public String getSendTime()
	{
		return this.m_sendTime;
	}
	
	public void addAcceptTime(TimeInterval acceptTime)
	{
		this.m_acceptTimes.add(acceptTime);
	}
	
	public String acceptTimeToJson()
	{
		JSONArray json_arr=new JSONArray();
		for(TimeInterval ti : this.m_acceptTimes)
		{
			JSONObject jtmp=ti.toJsonObject();
			json_arr.put(jtmp);
		}
		return json_arr.toString();
	}
	
	public JSONArray acceptTimeToJsonArray()
	{
		JSONArray json_arr=new JSONArray();
		for(TimeInterval ti : this.m_acceptTimes)
		{
			JSONObject jtmp=ti.toJsonObject();
			json_arr.put(jtmp);
		}
		return json_arr;
	}
	
	public void setCustom(Map<String,Object> custom)
	{
		this.m_custom=custom;
	}
	
	public void setRaw(String raw)
	{
		this.m_raw=raw;
	}
	
	public void setAlert(String alert)
	{
		this.m_alertStr=alert;
	}
	
	public void setAlert(JSONObject alert)
	{
		this.m_alertJo=alert;
	}
	
	public void setBadge(int badge)
	{
		this.m_badge=badge;
	}
	
	public void setSound(String sound)
	{
		this.m_sound=sound;
	}
	
	public void setCategory(String category)
	{
		this.m_category=category;
	}
	
	public int getLoopInterval()
	{
		return this.m_loopInterval;
	}
	
	public void setLoopInterval(int loopInterval)
	{
		this.m_loopInterval=loopInterval;
	}
	
	public int getLoopTimes()
	{
		return this.m_loopTimes;
	}
	
	public void setLoopTimes(int loopTimes)
	{
		this.m_loopTimes=loopTimes;
	}
	
	public boolean isValid()
	{
		if(!this.m_raw.isEmpty())
		{
			return true;
		}
		if((this.m_type<11) || (this.m_type>12))
		{
			return false;
		}
		if((this.m_expireTime<0) || (this.m_expireTime>259200))
		{
			return false;
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			sdf.parse(this.m_sendTime);
		}
		catch(ParseException e)
		{
			return false;
		}
		for(TimeInterval ti : this.m_acceptTimes)
		{
			if(!ti.isValid())
			{
				return false;
			}
		}
		if(this.m_type==12)
		{
			return true;
		}
		return (!this.m_alertStr.isEmpty()) || (this.m_alertJo.length()!=0);
	}
	
	public String toJson()
	{
		if(!this.m_raw.isEmpty())
		{
			return this.m_raw;
		}
		JSONObject json=new JSONObject(this.m_custom);
		json.put("accept_time",acceptTimeToJsonArray());
		
		JSONObject aps=new JSONObject();
		if(this.m_type==12)
		{
			aps.put("content-available",1);
		}
		else if(this.m_type==11)
		{
			if(this.m_alertJo.length()!=0)
			{
				aps.put("alert",this.m_alertJo);
			}
			else
			{
				aps.put("alert",this.m_alertStr);
			}
			if(this.m_badge!=0)
			{
				aps.put("badge",this.m_badge);
			}
			if(!this.m_sound.isEmpty())
			{
				aps.put("sound",this.m_sound);
			}
			if(!this.m_category.isEmpty())
			{
				aps.put("category",this.m_category);
			}
		}
		json.put("aps",aps);
		
		return json.toString();
	}
}