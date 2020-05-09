package com.home.commonData.data.scene.unit;

import com.home.commonData.data.scene.base.DirDO;
import com.home.commonData.data.scene.base.DriveDO;
import com.home.commonData.data.scene.base.PosDO;
import com.home.shineData.support.MaybeNull;

import java.util.List;

/** 单位移动数据 */
public class UnitMoveDO
{
	/** 基元移动状态(见UnitBaseMoveState) */
	int baseMoveState;
	/** 当前移动类型(见MoveType) */
	int moveType;
	/** 基元移动点位置 */
	@MaybeNull
	PosDO baseMovePos;
	/** 剩余移动点组 */
	@MaybeNull
	List<PosDO> moveList;
	/** 当前特殊移动ID(如没有为-1) */
	int specialMoveID;
	/** 特殊移动参数组 */
	@MaybeNull
	int[] specialMoveArgs;
	/** 特殊移动剩余时间 */
	int specialMoveLastTime;
	/** 当前骑乘载具id */
	int vehicleInstanceID;
	/** 驾驶数据 */
	@MaybeNull
	DriveDO driveData;
	/** 实际移动方向(moveDir类型有效) */
	@MaybeNull
	DirDO realMoveDir;
	/** 实际移动方向速度比率(x100值)(默认-1)(moveDir类型有效) */
	int realMoveSpeedRatio=-1;
}
