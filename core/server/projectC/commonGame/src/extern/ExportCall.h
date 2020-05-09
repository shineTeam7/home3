#pragma once
#include "UnityExportMethod.h"
#include "JavaExportMethod.h"
#include "DetourNavMeshBuilder.h"

bool _exportCallFlag=false;

/** 对export接口做强引用 */
void c_exportCall()
{

	if(_exportCallFlag)
	{
#ifndef _isLinux
		//unity部分
		exportNavMeshTiles(NULL, 0, NULL, 0, NULL);

#endif
		//recast显示调用
		dtCreateNavMeshData(NULL,NULL,0);

		//java部分
		Java_com_home_commonTool_extern_ToolExternMethodNative_exportNav(NULL, NULL, NULL, NULL, NULL);
		Java_com_home_commonBase_extern_ExternMethodNative_init(NULL, NULL, NULL);
		Java_com_home_commonBase_extern_ExternMethodNative_registMap(NULL, NULL, NULL, NULL);
		Java_com_home_commonBase_extern_ExternMethodNative_createScene(NULL, NULL, NULL);
		Java_com_home_commonBase_extern_ExternMethodNative_removeScene(NULL, NULL, NULL);
		Java_com_home_commonBase_extern_ExternMethodNative_samplePosition(NULL, NULL, NULL,NULL,NULL,NULL,NULL);
		Java_com_home_commonBase_extern_ExternMethodNative_raycast(NULL, NULL, NULL,NULL,NULL,NULL);
		Java_com_home_commonBase_extern_ExternMethodNative_calculatePath(NULL, NULL, NULL,NULL,NULL,NULL);
	}
}
