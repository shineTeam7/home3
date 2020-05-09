package com.home.shine.support;

import org.apache.commons.codec.digest.DigestUtils;

/** MD5 */
public class MD5
{
	/** 哈希字符串 */
	public static String hash(String str)
	{
		return DigestUtils.md5Hex(str);
	}
	
	/** 哈希二进制 */
	public static String hashBytes(byte[] bytes)
	{
		return DigestUtils.md5Hex(bytes);
	}
	
	/** 哈希二进制 */
	public static byte[] hashForBytes(byte[] bytes)
	{
		return DigestUtils.md5(bytes);
	}
}
