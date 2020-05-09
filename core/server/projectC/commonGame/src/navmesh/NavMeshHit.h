#pragma once
#include "SInclude.h"
#include "dataEx/ExternBuf.h"

class NavMeshHit
{
public:
	float position[3];
	float normal[3];
	float distance=0;
	uint16 mask=0;
	bool hit=false;

	void setPosition(float* value)
	{
		position[0]=value[0];
		position[1]=value[1];
		position[2]=value[2];
	}

	void setNormal(float* value)
	{
		normal[0]=value[0];
		normal[1]=value[1];
		normal[2]=value[2];
	}

	void clear()
	{
		position[0]=0;
		position[1]=0;
		position[2]=0;
		normal[0]=0;
		normal[1]=0;
		normal[2]=0;
		distance=0;
		mask=0;
		hit=false;
	}

	void writeBuf(ExternBuf* buf)
	{
		buf->writeVector(position);
		buf->writeVector(normal);
		buf->writeFloat(distance);
		buf->writeInt(mask);
		buf->writeBoolean(hit);
	}
};
