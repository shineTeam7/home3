package com.home.commonBase.global;

import com.home.commonBase.control.BaseClassControl;
import com.home.commonBase.control.BaseConstControl;
import com.home.commonBase.control.BaseFactoryControl;
import com.home.commonBase.control.BaseLogicControl;
import com.home.commonBase.control.ConfigControl;
import com.home.commonBase.control.PushNotifyControl;
import com.home.commonBase.control.TriggerControl;
import com.home.commonBase.control.DBUpdateControl;

/** 基础工程单例管理 */
public class BaseC
{
	/** 基础配置 */
	public static ConfigControl config;
	/** 基础工厂 */
	public static BaseFactoryControl factory;
	/** 基础逻辑 */
	public static BaseLogicControl logic;
	/** 常量方法 */
	public static BaseConstControl constlist;
	/** 触发器控制 */
	public static TriggerControl trigger;
	/** 推送控制器 */
	public static PushNotifyControl push;
	/** 数据库升级控制 */
	public static DBUpdateControl dbUpdate;
	/** 类信息 */
	public static BaseClassControl cls;
}
