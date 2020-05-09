package com.home.commonBase.config.other;

import com.home.commonBase.config.base.BaseConfig;
import com.home.shine.bytes.BytesReadStream;

/** 导航网格地图信息配置 */
public class RecastMapInfoConfig extends BaseConfig
{
	public byte[] bytes;
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		int len=stream.readLen();
		
		bytes=stream.readByteArr(len);
	}
}
