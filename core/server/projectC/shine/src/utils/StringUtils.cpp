#include "StringUtils.h"

string StringUtils::replaceAll(const string& str, char oldChar, char newChar)
{
	string re = str;
	string::size_type pos = 0;

	while ((pos = re.find(oldChar)) != string::npos)
	{
		re.replace(pos, 1, 1, newChar);
	}

	return re;
}
