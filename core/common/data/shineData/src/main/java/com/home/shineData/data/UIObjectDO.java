package com.home.shineData.data;

import com.home.shineData.support.MaybeNull;

import java.util.ArrayList;
import java.util.List;

public final class UIObjectDO
{
	/** 名字 */
	String name;
	/** 类型 */
	int type;
	/** 样式 */
	String style;
	/** 子项组 */
	@MaybeNull
	List<UIObjectDO> children=new ArrayList<>();
	/** 整型参数组 */
	@MaybeNull
	int[] intArgs;
	/** 字符参数组 */
	@MaybeNull
	String[] strArgs;
}
