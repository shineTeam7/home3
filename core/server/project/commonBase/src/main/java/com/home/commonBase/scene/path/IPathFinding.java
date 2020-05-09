package com.home.commonBase.scene.path;

import com.home.shine.support.collection.IntList;

/** 寻路接口 */
public interface IPathFinding
{
	/** 寻路(做过预处理的) */
	void findPath(IntList result,int moveType,boolean needCrowed,int sx,int sy,int ex,int ey);
	/** 是否可走 */
	boolean isEnable(int moveType,boolean needCrowed,int x,int y);
}
