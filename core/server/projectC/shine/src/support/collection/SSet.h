#pragma once
#include "../../SInclude.h"
#include <unordered_set>

template <class _Kty>
class SSet :public unordered_set<_Kty>
{
public:

	bool add(const _Kty& _Keyval)
	{
		return this->insert(_Keyval).second != NULL;
	}

	/** 是否包含 */
	bool contains(const _Kty& _Keyval)
	{
		this->find(_Keyval) != this->end();
	}

	/** 删除 */
	void remove(const _Kty& _Keyval)
	{
		this->erase(_Keyval);
	}

	/** 长度 */
	int length()
	{
		return this->size();
	}
};
