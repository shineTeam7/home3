package com.home.commonGame.constlist.system;

/** 角色跨服状态类型 */
public class PlayerSwitchStateType
{
	/** 未跨服 */
	public static final int None=0;
	/** 发回源服阶段 */
	public static final int NoticeToSource=1;
	/** 切换当前服阶段 */
	public static final int SwitchCurrent=2;
	/** 发送到目标服 */
	public static final int SendToTarget=3;
	/** 等待客户端连接到目标服 */
	public static final int WaitClient=4;
	
	
	
	/** 源服等待切换结果 */
	public static final int SourceWait=9;
	/** 源服记录为切换完毕 */
	public static final int SourceReady=10;
}
