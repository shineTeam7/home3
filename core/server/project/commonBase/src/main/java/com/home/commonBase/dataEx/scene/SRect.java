package com.home.commonBase.dataEx.scene;

import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.StringUtils;

/** 碰撞矩形 */
public class SRect
{
	/** x */
	public float x;
	/** y */
	public float y;
	/** 宽 */
	public float width;
	/** 高 */
	public float height;
	
	public SRect()
	{
	
	}
	
	public SRect(float x,float y,float width,float height)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}
	
	public SRect clone()
	{
		return new SRect(x,y,width,height);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append("{x=");
		sb.append(StringUtils.roundStrF2(x));
		sb.append(",y=");
		sb.append(StringUtils.roundStrF2(y));
		sb.append(",width=");
		sb.append(StringUtils.roundStrF2(width));
		sb.append(",height=");
		sb.append(StringUtils.roundStrF2(height));
		sb.append("}");
		return StringBuilderPool.releaseStr(sb);
	}
	
	public void copy(SRect rect)
	{
		x=rect.x;
		y=rect.y;
		width=rect.width;
		height=rect.height;
	}
	
	public boolean overlap(SRect rect)
	{
		if(rect==null)
			return false;
		
		if(Math.abs(x - rect.x)<Math.abs(width / 2) + Math.abs(rect.width / 2) && Math.abs(y - rect.y)<Math.abs(height / 2) + Math.abs(rect.height / 2))
			return true;
		
		return false;
	}
	
	/** 通过配置创建 */
	public static SRect createByConfig(float[] arr)
	{
		SRect re=new SRect();
		
		if(arr.length>3)
		{
			re.x=arr[0];
			re.y=arr[1];
			re.width=arr[2];
			re.height=arr[3];
		}
		
		return re;
	}
}
