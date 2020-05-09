#include "ShineGlobal.h"

string ShineGlobal::binPath = "";
bool ShineGlobal::_inited = false;

void ShineGlobal::init(string binPathV)
{
	if (_inited)
		return;

	_inited = true;
	binPath = binPathV;

	//TODO:后续设置
}
