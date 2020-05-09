using System;
using System.Text;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 碰撞矩形(以x,y为中心)
/// </summary>
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

	public String ToString()
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.Append("{x=");
		sb.Append(StringUtils.roundStrF2(x));
		sb.Append(",y=");
		sb.Append(StringUtils.roundStrF2(y));
		sb.Append(",width=");
		sb.Append(StringUtils.roundStrF2(width));
		sb.Append(",height=");
		sb.Append(StringUtils.roundStrF2(height));
		sb.Append("}");
		return StringBuilderPool.releaseStr(sb);
	}

	public void copy(SRect rect)
	{
		x=rect.x;
		y=rect.y;
		width=rect.width;
		height=rect.height;
	}

	public bool overlap(SRect rect)
	{
		if(rect==null)
			return false;

		if(Math.Abs(x - rect.x)<Math.Abs(width / 2) + Math.Abs(rect.width / 2) && Math.Abs(y - rect.y)<Math.Abs(height / 2) + Math.Abs(rect.height / 2))
			return true;

		return false;
	}

	/** 通过配置创建 */
	public static SRect createByConfig(float[] arr)
	{
		SRect re=new SRect();

		if(arr.Length>=4)
		{
			re.x=arr[0];
			re.y=arr[1];
			re.width=arr[2];
			re.height=arr[3];
		}

		return re;
	}
}