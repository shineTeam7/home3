#include "ShineToolSetup.h"
#include "ShineToolGlobal.h"
#include "ShineSetting.h"

void ShineToolSetup::init()
{
	ShineToolGlobal::init();

	ShineSetting::needError = true;
}
