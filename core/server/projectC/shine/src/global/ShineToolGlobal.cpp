#include "ShineToolGlobal.h"
#include "ShineGlobal.h"

#ifdef _isWindows
#include <direct.h>
#endif

#ifdef _isMac
#include <unistd.h>
#endif
#include "../utils/StringUtils.h"
#include "../utils/FileUtils.h"

string ShineToolGlobal::getCwdPath()
{
	string re;

#ifdef _isMac
	const int MAXPATH = 256;
	char buffer[MAXPATH];
//	__getcwd(buffer, MAXPATH);
	getcwd(buffer, MAXPATH);

	re = string(buffer);
#endif

#ifdef _isWindows
	const int MAXPATH = 256;
	char buffer[MAXPATH];
	_getcwd(buffer, MAXPATH);

	re = string(buffer);
#endif


	return re;
}

bool ShineToolGlobal::_inited = false;
string ShineToolGlobal::codePath = "";
string ShineToolGlobal::clientPath = "";
string ShineToolGlobal::serverPath = "";
string ShineToolGlobal::recordPath = "";

string ShineToolGlobal::dataPath = "";
string ShineToolGlobal::configPath = "";
string ShineToolGlobal::configSavePath = "";

string ShineToolGlobal::mapInfoPath = "";

void ShineToolGlobal::init()
{
	if (_inited)
		return;

	_inited = true;

	string cwdPath = ShineToolGlobal::getCwdPath();

	cwdPath = FileUtils::fixPath(cwdPath);

//#ifdef _isMac
//	codePath = FileUtils::getParentPath(FileUtils::getParentPath(FileUtils::getParentPath(cwdPath)));
//#endif
//
//#ifdef _isWindows
//	codePath = FileUtils::getParentPath(FileUtils::getParentPath(FileUtils::getParentPath(cwdPath)));
//#endif

	codePath = FileUtils::getParentPath(FileUtils::getParentPath(FileUtils::getParentPath(cwdPath)));

	setPathByCode();
}


void ShineToolGlobal::setPathByCode()
{
	clientPath = codePath + "/client";
	serverPath = codePath + "/server";
	recordPath = codePath + "/record";

	setPathByMain();
}

void ShineToolGlobal::setPathByMain()
{
	dataPath = codePath + "/common/data";
	configPath = codePath + "/common/config";
	configSavePath = codePath + "/common/save";

	mapInfoPath = configSavePath + "/mapInfo";
}
