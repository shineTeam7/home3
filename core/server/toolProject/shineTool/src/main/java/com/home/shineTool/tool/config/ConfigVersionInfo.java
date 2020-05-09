package com.home.shineTool.tool.config;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;

/** 配置版本信息 */
public class ConfigVersionInfo
{
	/** 名字(useName) */
	public String name;
	
	public String clientValueMD5="";
	
	public String serverValueMD5="";
	
	/** 写字节流 */
	public void writeBytes(BytesWriteStream stream)
	{
		stream.writeUTF(name);
		stream.writeUTF(clientValueMD5);
		stream.writeUTF(serverValueMD5);
	}
	
	public void readBytes(BytesReadStream stream)
	{
		name=stream.readUTF();
		clientValueMD5=stream.readUTF();
		serverValueMD5=stream.readUTF();
	}
}
