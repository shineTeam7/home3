package com.home.commonBase.utils;

import com.home.commonBase.data.scene.base.RectData;

/** 地图工具类 */
public class MapUtils
{
	/** 判断两个矩形结构是否部分重叠 */
	public static boolean isTwoRectOverlapped(RectData rect1,RectData rect2)
	{
		float centerX1=rect1.x + rect1.width / 2;
		float centerX2=rect2.x + rect2.width / 2;
		float centerY1=rect1.y + rect1.height / 2;
		float centerY2=rect2.y + rect2.height / 2;
		float verticalDistance=Math.abs(centerX1 - centerX2);//垂直距离
		float horizontalDistance=Math.abs(centerY1 - centerY2);//水平距离
		float verticalThreshold=(rect1.height + rect2.height) / 2;//两矩形分离的垂直临界值
		float horizontalThreshold=(rect1.width + rect2.width) / 2;//两矩形分离的水平临界值
		
		return verticalDistance<=verticalThreshold && horizontalDistance<=horizontalThreshold;
	}
	
	/** 是否包含矩形结构(rect1包含rect2) */
	public static boolean isContainRect(RectData rect1,RectData rect2)
	{
		return rect1.x<=rect2.x && rect1.width>=rect2.width && rect1.y>=rect2.y && rect1.height>=rect2.height;
	}
}
