package com.home.shineTool.reflect;

import com.home.shine.utils.ObjectUtils;

/** 注解信息 */
public class AnnotationInfo
{
	/** 名字 */
	public String name;
	/** 参数组 */
	public String[] args=ObjectUtils.EmptyStringArr;
	
	public static AnnotationInfo create(String name,String... args)
	{
		AnnotationInfo re=new AnnotationInfo();
		re.name=name;
		re.args=args;
		return re;
	}
}
