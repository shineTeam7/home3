package com.home.shineTool.constlist;

/** 访问类型 */
public class VisitType
{
	/** 没写 */
	public static final int None=0;
	/** 公有 */
	public static final int Public=1;
	/** 私有 */
	public static final int Private=2;
	/** 保护 */
	public static final int Protected=3;
	
	public static String getString(int type)
	{
		String re="";
		
		switch(type)
		{
			case None:
			{
				re="";
			}
			break;
			case Public:
			{
				re="public";
			}
			break;
			case Private:
			{
				re="private";
			}
			break;
			case Protected:
			{
				re="protected";
			}
			break;
		}
		
		return re;
	}
}
