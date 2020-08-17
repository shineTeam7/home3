#include "FileUtils.h"
#include "StringUtils.h"

string FileUtils::fixPath(string path)
{
	return StringUtils::replaceAll(path, '\\', '/');
}


string FileUtils::fixPath2(string path)
{
	return StringUtils::replaceAll(path, '/', '\\');
}


string FileUtils::getParentPath(string path)
{
	string::size_type index = path.rfind('/', path.size() - 1);

	if (index == string::npos)
		return path;

	return path.substr(0, index);
}

char* FileUtils::readFileForBytes(const char* path, int& size)
{
	size = 0;

	char* buf = 0;
	FILE* fp = fopen(path, "rb");

	if (!fp)
		return nullptr;

	if (fseek(fp, 0, SEEK_END) != 0)
	{
		fclose(fp);
		return nullptr;
	}

	int bufSize = ftell(fp);
	if (bufSize < 0)
	{
		fclose(fp);
		return nullptr;
	}

	if (fseek(fp, 0, SEEK_SET) != 0)
	{
		fclose(fp);
		return nullptr;
	}

	buf = new char[bufSize];
	if (!buf)
	{
		fclose(fp);
		return nullptr;
	}

	size_t readLen = fread(buf, bufSize, 1, fp);
	fclose(fp);

	if (readLen != 1)
	{
		delete[] buf;
		return nullptr;
	}

	size = bufSize;
	return buf;
}

BytesReadStream* FileUtils::readFileForBytesReadStream(const char* path)
{
	int size = 0;
	char* buf = readFileForBytes(path, size);

	if (!buf)
		return nullptr;

	return new BytesReadStream(buf, size);
}

bool FileUtils::writeFileForBytes(const char* path, const void* ptr, int len)
{
	FILE* fp = fopen(path, "wb");

	if (!fp)
		return false;

	fwrite(ptr, len, 1, fp);
	fclose(fp);
}
