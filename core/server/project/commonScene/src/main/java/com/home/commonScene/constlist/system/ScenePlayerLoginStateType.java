package com.home.commonScene.constlist.system;

/** 场景角色登录状态 */
public class ScenePlayerLoginStateType
{
	/** 无 */
	public static final int None=0;
	/** 等待客户端连接 */
	public static final int WaitConnect=1;
	/** 等待game服推送信息 */
	public static final int WaitGameInfo=2;
	/** 运行中 */
	public static final int Running=3;
	/** 退出中 */
	public static final int Exiting=4;
}
