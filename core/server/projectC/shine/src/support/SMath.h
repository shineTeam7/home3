#pragma once
#include "../SInclude.h"
#include "../utils/MathUtils.h"

struct Vector3
{
public:
	float x;
	float y;
	float z;


	static const float        epsilon;

	Vector3() :Vector3(0.0f, 0.0f, 0.0f) {};

	Vector3(const Vector3& v) : x(v.x), y(v.y), z(v.z) {}   // Necessary for correct optimized GCC codegen
	Vector3(float inX, float inY, float inZ) { x = inX; y = inY; z = inZ; }
	explicit Vector3(const float* array) { x = array[0]; y = array[1]; z = array[2]; }
	void Set(float inX, float inY, float inZ) { x = inX; y = inY; z = inZ; }
	void Set(const float* array) { x = array[0]; y = array[1]; z = array[2]; }
	void SetZero() { x = 0.0f; y = 0.0f; z = 0.0f; }

	//float* getPtr() { return &x; }
	const float* getPtr() const { return &x; }

	void toArray(float* arr) const
	{
		arr[0] = x;
		arr[1] = y;
		arr[2] = z;
	}

	float& operator[](int i) { assert(i >= 0 && i <= 2); return (&x)[i]; }
	const float& operator[](int i) const { assert(i >= 0 && i <= 2); return (&x)[i]; }

	bool operator==(const Vector3& v) const { return x == v.x && y == v.y && z == v.z; }
	bool operator!=(const Vector3& v) const { return x != v.x || y != v.y || z != v.z; }

	Vector3& operator+=(const Vector3& inV) { x += inV.x; y += inV.y; z += inV.z; return *this; }
	Vector3& operator-=(const Vector3& inV) { x -= inV.x; y -= inV.y; z -= inV.z; return *this; }
	Vector3& operator*=(float s) { x *= s; y *= s; z *= s; return *this; }
	Vector3& operator/=(float s);

	Vector3 operator-() const { return Vector3(-x, -y, -z); }

	Vector3& Scale(const Vector3& inV) { x *= inV.x; y *= inV.y; z *= inV.z; return *this; }

	//bool CompareApproximately(const Vector3& inV0, const Vector3& inV1, const float inMaxDist = Vector3::epsilon);
};

inline Vector3 Scale(const Vector3& lhs, const Vector3& rhs) { return Vector3(lhs.x * rhs.x, lhs.y * rhs.y, lhs.z * rhs.z); }

inline Vector3 operator+(const Vector3& lhs, const Vector3& rhs) { return Vector3(lhs.x + rhs.x, lhs.y + rhs.y, lhs.z + rhs.z); }
inline Vector3 operator-(const Vector3& lhs, const Vector3& rhs) { return Vector3(lhs.x - rhs.x, lhs.y - rhs.y, lhs.z - rhs.z); }
inline Vector3 Cross(const Vector3& lhs, const Vector3& rhs);
inline float Dot(const Vector3& lhs, const Vector3& rhs) { return lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z; }
inline float Volume(const Vector3& inV) { return inV.x * inV.y * inV.z; }

inline Vector3 operator*(const Vector3& inV, const float s) { return Vector3(inV.x * s, inV.y * s, inV.z * s); }
inline Vector3 operator*(const float s, const Vector3& inV) { return Vector3(inV.x * s, inV.y * s, inV.z * s); }
inline Vector3 operator*(const Vector3& lhs, const Vector3& rhs) { return Vector3(lhs.x * rhs.x, lhs.y * rhs.y, lhs.z * rhs.z); }
inline Vector3 operator/(const Vector3& inV, const float s) { Vector3 temp(inV); temp /= s; return temp; }
inline Vector3 Inverse(const Vector3& inVec) { return Vector3(1.0F / inVec.x, 1.0F / inVec.y, 1.0F / inVec.z); }

inline float SqrMagnitude(const Vector3& inV) { return Dot(inV, inV); }
inline float Magnitude(const Vector3& inV) { return sqrt(Dot(inV, inV)); }

// Normalizes a vector, asserts if the vector can be normalized
inline Vector3 Normalize(const Vector3& inV) { return inV / Magnitude(inV); }
//// Normalizes a vector, returns default vector if it can't be normalized
//inline Vector3 NormalizeSafe(const Vector3& inV, const Vector3& defaultV = Vector3::zero);

inline float Angle(const Vector3& lhs, const Vector3& rhs) { return ::acos(fmin(1.0f, fmax(-1.0f, Dot(lhs, rhs) / (Magnitude(lhs) * Magnitude(rhs))))); }

inline Vector3 ReflectVector(const Vector3& inDirection, const Vector3& inNormal) { return -2.0F * Dot(inNormal, inDirection) * inNormal + inDirection; }

inline Vector3 Lerp(const Vector3& from, const Vector3& to, float t) { return to * t + from * (1.0F - t); }
//Vector3 Slerp(const Vector3& from, const Vector3& to, float t);


inline float Dot2D(const Vector3& a, const Vector3& b)
{
	return a.x * b.x + a.z * b.z;
}

inline Vector3& Vector3::operator/=(float s)
{
	assert(!MathUtils::floatEquals(s, 0.0f));
	x /= s;
	y /= s;
	z /= s;
	return *this;
}
