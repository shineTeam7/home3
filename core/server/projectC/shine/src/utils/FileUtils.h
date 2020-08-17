#pragma once

#include "../SInclude.h"
#include "../bytes/BytesReadStream.h"
#include "../bytes/BytesWriteStream.h"

/** 文件方法类 */
class FileUtils
{
public:
	/** 修复路径(把所有的\变成/) */
	static string fixPath(string path);
	/** 修复路径(把所有的\变成/)(根据文件系统格式选择分隔符) */
	static string fixPath2(string path);
	/** 返回路径的父路径(已fix过的) */
	static string getParentPath(string path);

	/** 从文件中读取字节 */
	static char* readFileForBytes(const char* path, int& size);
	/** 从文件中读取字节流 */
	static BytesReadStream* readFileForBytesReadStream(const char* path);

	/** 从文件中读取字节 */
	static char* readFileForBytes(string& path, int& size)
	{
		return readFileForBytes(path.c_str(), size);
	}

	/** 从文件中读取字节流 */
	static BytesReadStream* readFileForBytesReadStream(string path)
	{
		return readFileForBytesReadStream(path.c_str());
	}

	/** 读取UTF文件(没有返回空地址) */
	static string readFileForUTF(string path)
	{
		int size;
		return readFileForBytes(path, size);
	}

	static bool writeFileForBytes(const char* path, const void* ptr, int len);

	static bool writeFileForBytes(string& path, const void* ptr, int len)
	{
		return writeFileForBytes(path.c_str(), ptr, len);
	}

	static bool writeFileForBytesWriteStream(const char* path, BytesWriteStream& stream)
	{
		return writeFileForBytes(path, stream.getBuf(), stream.length());
	}

	static bool writeFileForBytesWriteStream(string& path, BytesWriteStream& stream)
	{
		return writeFileForBytes(path.c_str(), stream.getBuf(), stream.length());
	}
};
