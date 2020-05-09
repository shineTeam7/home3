#pragma once


struct Vector3
{
public:
	float x;
	float y;
	float z;

	Vector3() :Vector3(0.0f, 0.0f, 0.0f) {};

	Vector3(float x, float y, float z)
	{
		this->x = x;
		this->y = y;
		this->z = z;
	};
};
