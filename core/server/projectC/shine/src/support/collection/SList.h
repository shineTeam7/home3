#pragma once
#include "../../SInclude.h"
#include <vector>

template <class _Ty>
class SList :public vector<_Ty>
{
public:
	/** 添加元素 */
	void add(const _Ty& _Val)
	{
		this->push_back(_Val);
	}

	/** 添加元素 */
	void add2(const _Ty& v0, const _Ty& v1)
	{
		this->push_back(v0);
		this->push_back(v1);
	}

	/** 添加元素 */
	void add3(const _Ty& v0, const _Ty& v1, const _Ty& v2)
	{
		this->push_back(v0);
		this->push_back(v1);
		this->push_back(v2);
	}

	/** 删除执行位置元素 */
	void remove(int index)
	{
		this->erase(this->begin() + index);
	}

	/** 获取 */
	const _Ty& get(int index)
	{
		return this->at(index);
	}

	/** 长度 */
	int length()
	{
		return this->size();
	}
};
