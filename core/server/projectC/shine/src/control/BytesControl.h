#pragma once
#include "../SInclude.h"

#include "../data/BaseData.h"

/** 字节控制(包括Data序列化/反序列化) */
class BytesControl
{
public:
	/** 通过id取得Data类型 */
	static BaseData* getDataByID(int dataID);

private:

};
