#include "JavaExportMethod.h"
#include "Recast.h"
#include "DetourTileCache.h"
#include "global/ShineGlobal.h"
#include "global/ShineToolGlobal.h"
#include "../navmesh/NavMesh.h"
#include "../global/GameC.h"
#include "dataEx/ExternBuf.h"
#include "../navmesh/NavMeshHit.h"
#include "../navmesh/NavMeshPath.h"

jboolean _bFalse = false;

//--tool--//

JNIEXPORT void JNICALL Java_com_home_commonTool_extern_ToolExternMethodNative_exportNav(JNIEnv* env, jclass cls, jstring configPath, jstring dataPath, jstring savePath)
{
	const char* configPathT = env->GetStringUTFChars(configPath, &_bFalse);
	const char* dataPathT = env->GetStringUTFChars(dataPath, &_bFalse);
	const char* savePathT = env->GetStringUTFChars(savePath, &_bFalse);

	NavMesh nav;
	nav.exportNav(configPathT, dataPathT, savePathT);
}

//--game--//

JNIEXPORT void JNICALL Java_com_home_commonBase_extern_ExternMethodNative_init(JNIEnv* env, jclass cls, jstring binPath)
{
	const char* str = env->GetStringUTFChars(binPath, &_bFalse);
	ShineGlobal::init(str);
}


JNIEXPORT void JNICALL Java_com_home_commonBase_extern_ExternMethodNative_registMap(JNIEnv* env, jclass cls, jint mapID, jbyteArray bytes)
{
	int size = env->GetArrayLength(bytes);
	jbyte* bytesJ = env->GetByteArrayElements(bytes, &_bFalse);

	GameC::scene->registMap(mapID, (char*)bytesJ, size);
}

JNIEXPORT jlong JNICALL Java_com_home_commonBase_extern_ExternMethodNative_createScene(JNIEnv* env, jclass cls, jint mapID)
{
	Scene* scene = GameC::scene->createScene(mapID);

	return (jlong)scene;
}

JNIEXPORT void JNICALL Java_com_home_commonBase_extern_ExternMethodNative_removeScene(JNIEnv* env, jclass cls, jlong scenePtr)
{
	Scene* scene = (Scene*)((uint64)scenePtr);

	delete scene;
}

JNIEXPORT jboolean JNICALL Java_com_home_commonBase_extern_ExternMethodNative_samplePosition(JNIEnv* env, jclass cls, jlong scenePtr, jfloatArray fArr, jintArray iArr, jfloat maxDistance, jint areaMask)
{
	jfloat* fA = env->GetFloatArrayElements(fArr, &_bFalse);
	jint* iA = env->GetIntArrayElements(iArr, &_bFalse);

	bool re = JavaCritical_com_home_commonBase_extern_ExternMethodNative_samplePosition(scenePtr, 0, fA, 0, iA, maxDistance, areaMask);

	env->ReleaseFloatArrayElements(fArr, fA, 0);
	env->ReleaseIntArrayElements(iArr, iA, 0);

	return re;
}

JNIEXPORT jboolean JNICALL JavaCritical_com_home_commonBase_extern_ExternMethodNative_samplePosition(jlong scenePtr, jint, jfloat* fA, jint, jint* iA, jfloat maxDistance, jint areaMask)
{
	Scene* scene = (Scene*)((uint64)scenePtr);

	ExternBuf buf(fA, iA, NULL);

	float position[3];
	buf.readVector(position);

	dtQueryFilter filter;
	filter.setIncludeFlags(areaMask);

	buf.clear();

	NavMeshHit hit;
	bool re = scene->getNavMesh()->samplePosition(&hit, position, filter, maxDistance);

	if (re)
	{
		hit.writeBuf(&buf);
	}

	return re;
}

JNIEXPORT jboolean JNICALL Java_com_home_commonBase_extern_ExternMethodNative_raycast(JNIEnv* env, jclass cls, jlong scenePtr, jfloatArray fArr, jintArray iArr, jint areaMask)
{
	float* fA = env->GetFloatArrayElements(fArr, &_bFalse);
	jint* iA = env->GetIntArrayElements(iArr, &_bFalse);

	bool re = JavaCritical_com_home_commonBase_extern_ExternMethodNative_raycast(scenePtr, 0, fA, 0, iA, areaMask);

	env->ReleaseFloatArrayElements(fArr, fA, 0);
	env->ReleaseIntArrayElements(iArr, iA, 0);

	return re;
}

JNIEXPORT jboolean JNICALL JavaCritical_com_home_commonBase_extern_ExternMethodNative_raycast(jlong scenePtr, jint, jfloat* fA, jint, jint* iA, jint areaMask)
{
	Scene* scene = (Scene*)((uint64)scenePtr);

	ExternBuf buf(fA, iA, NULL);

	float fromPosition[3];
	buf.readVector(fromPosition);

	float targetPosition[3];
	buf.readVector(targetPosition);

	dtQueryFilter filter;
	filter.setIncludeFlags(areaMask);

	buf.clear();

	NavMeshHit hit;

	bool re = scene->getNavMesh()->raycast(&hit, fromPosition, targetPosition, filter);

	if (re)
	{
		hit.writeBuf(&buf);
	}

	return re;
}

JNIEXPORT jboolean JNICALL Java_com_home_commonBase_extern_ExternMethodNative_calculatePath(JNIEnv* env, jclass cls, jlong scenePtr, jfloatArray fArr, jintArray iArr, jint areaMask)
{
	float* fA = env->GetFloatArrayElements(fArr, &_bFalse);
	jint* iA = env->GetIntArrayElements(iArr, &_bFalse);

	bool re = JavaCritical_com_home_commonBase_extern_ExternMethodNative_calculatePath(scenePtr, 0, fA, 0, iA, areaMask);

	env->ReleaseFloatArrayElements(fArr, fA, 0);
	env->ReleaseIntArrayElements(iArr, iA, 0);

	return re;
}

JNIEXPORT jboolean JNICALL JavaCritical_com_home_commonBase_extern_ExternMethodNative_calculatePath(jlong scenePtr, jint, jfloat* fA, jint, jint* iA, jint areaMask)
{
	Scene* scene = (Scene*)((uint64)scenePtr);

	ExternBuf buf(fA, iA, NULL);

	float fromPosition[3];
	buf.readVector(fromPosition);

	float targetPosition[3];
	buf.readVector(targetPosition);

	dtQueryFilter filter;
	filter.setIncludeFlags(areaMask);

	buf.clear();

	NavMeshPath path;

	bool re = scene->getNavMesh()->findPath(&path, fromPosition, targetPosition, filter);

	if (re)
	{
		path.writeBuf(&buf);
	}

	return re;
}
