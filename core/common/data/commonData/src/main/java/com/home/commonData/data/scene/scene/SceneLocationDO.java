package com.home.commonData.data.scene.scene;

/** 场景位置数据 */
public class SceneLocationDO
{
	/** 场景ID(必选参,剩下的都是可选参) */
	int sceneID;
	/** 线ID */
	int lineID=-1;
	/** 所在服(游戏服或场景服)ID */
	int serverID=-1;
	/** 执行器号 */
	int executorIndex=-1;
	/** 实例ID */
	int instanceID=-1;
}
