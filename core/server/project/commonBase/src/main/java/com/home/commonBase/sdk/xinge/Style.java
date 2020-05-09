package com.home.commonBase.sdk.xinge;

public class Style
{
	private int m_builderId;
	private int m_ring;
	private int m_vibrate;
	private int m_clearable;
	private int m_nId;
	private String m_ringRaw;
	private int m_lights;
	private int m_iconType;
	private String m_iconRes;
	private int m_styleId;
	private String m_smallIcon;
	
	public Style(int builderId)
	{
		this(builderId,0,0,1,0,1,0,1);
	}
	
	public Style(int builderId,int ring,int vibrate,int clearable,int nId)
	{
		this.m_builderId=builderId;
		this.m_ring=ring;
		this.m_vibrate=vibrate;
		this.m_clearable=clearable;
		this.m_nId=nId;
	}
	
	public Style(int builderId,int ring,int vibrate,int clearable,int nId,int lights,int iconType,int styleId)
	{
		this.m_builderId=builderId;
		this.m_ring=ring;
		this.m_vibrate=vibrate;
		this.m_clearable=clearable;
		this.m_nId=nId;
		this.m_lights=lights;
		this.m_iconType=iconType;
		this.m_styleId=styleId;
	}
	
	public int getBuilderId()
	{
		return this.m_builderId;
	}
	
	public int getRing()
	{
		return this.m_ring;
	}
	
	public int getVibrate()
	{
		return this.m_vibrate;
	}
	
	public int getClearable()
	{
		return this.m_clearable;
	}
	
	public int getNId()
	{
		return this.m_nId;
	}
	
	public int getLights()
	{
		return this.m_lights;
	}
	
	public int getIconType()
	{
		return this.m_iconType;
	}
	
	public int getStyleId()
	{
		return this.m_styleId;
	}
	
	public void setRingRaw(String ringRaw)
	{
		this.m_ringRaw=ringRaw;
	}
	
	public String getRingRaw()
	{
		return this.m_ringRaw;
	}
	
	public void setIconRes(String iconRes)
	{
		this.m_iconRes=iconRes;
	}
	
	public String getIconRes()
	{
		return this.m_iconRes;
	}
	
	public void setSmallIcon(String smallIcon)
	{
		this.m_smallIcon=smallIcon;
	}
	
	public String getSmallIcon()
	{
		return this.m_smallIcon;
	}
	
	public boolean isValid()
	{
		if((this.m_ring<0) || (this.m_ring>1))
		{
			return false;
		}
		if((this.m_vibrate<0) || (this.m_vibrate>1))
		{
			return false;
		}
		if((this.m_clearable<0) || (this.m_clearable>1))
		{
			return false;
		}
		if((this.m_lights<0) || (this.m_lights>1))
		{
			return false;
		}
		if((this.m_iconType<0) || (this.m_iconType>1))
		{
			return false;
		}
		if((this.m_styleId<0) || (this.m_styleId>1))
		{
			return false;
		}
		
		return true;
	}
}