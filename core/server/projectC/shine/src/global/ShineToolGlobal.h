#pragma once
#include "../SInclude.h"

/** shine配置 */
class ShineToolGlobal
{
public:
	/** 获取cwd目录,通常是projectC/game目录 */
	static string getCwdPath();

	/** 总目录(code) */
	static string codePath;

	/** 客户端根目录 */
	static string clientPath;
	/** 服务器根目录 */
	static string serverPath;
	/** 文件记录目录 */
	static string recordPath;
	/** 数据工程目录 */
	static string dataPath;
	/** 配置表目录 */
	static string configPath;
	/** 配置表保存目录 */
	static string configSavePath;
	/** 服务器地图信息目录 */
	static string mapInfoPath;

	static void init();
	/** 设置其他路径,通过code */
	static void setPathByCode();
	/** 设置其他路径,通过各类主路径 */
	static void setPathByMain();
private:
	static bool _inited;

};
