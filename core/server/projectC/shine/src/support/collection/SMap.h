#pragma once
#include "../../SInclude.h"
#include <unordered_map>

template <class _Kty, class _Ty>
class SMap :public unordered_map<_Kty, _Ty>
{
private:
	_Ty _defaultV;

public:

	/** 设置默认值 */
	SMap<_Kty, _Ty>* setDefaultValue(const _Ty& v)
	{
		_defaultV = v;
		return this;
	}

	/** 获取 */
	const _Ty& get(const _Kty& _Keyval)
	{
		auto it = this->find(_Keyval);

		if (it == this->end())
		{
			return _defaultV;
		}

		return it->second;
	}

	/** 获取或默认 */
	const _Ty& getOrDefault(const _Kty& _Keyval, const _Ty& defaultValue)
	{
		auto it = this->find(_Keyval);

		if (it == this->end())
		{
			return defaultValue;
		}

		return it->second;
	}

	/** 添加 */
	void put(const _Kty& _Keyval, const _Ty& _Val)
	{
		this->insert({ _Keyval,_Val });
	}

	/** 是否包含 */
	bool contains(const _Kty& _Keyval)
	{
		return this->find(_Keyval) != this->end();
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
