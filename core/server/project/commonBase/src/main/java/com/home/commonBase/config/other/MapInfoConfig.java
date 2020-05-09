package com.home.commonBase.config.other;

import com.home.commonBase.config.base.BaseConfig;
import com.home.commonBase.constlist.scene.PathFindingType;
import com.home.commonBase.global.CommonSetting;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.support.collection.IntObjectMap;

public class MapInfoConfig extends BaseConfig
{
	/** 数据组 */
	protected static IntObjectMap<MapInfoConfig> _dic=new IntObjectMap<>(MapInfoConfig[]::new);
	
	/** 地图ID */
	public int id;
	/** 格子地图 */
	public GridMapInfoConfig grid;
	/** recast地图 */
	public RecastMapInfoConfig recast;
	
	/** 设置editor部分 */
	public static void setDic(IntObjectMap<MapInfoConfig> dic)
	{
		_dic=dic;
	}
	
	public static IntObjectMap<MapInfoConfig> getDic()
	{
		return _dic;
	}
	
	public static MapInfoConfig get(int id)
	{
		MapInfoConfig config=_dic.get(id);
		
		if(config!=null)
			return config;
		
		
		return _dic.get(id);
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		id=stream.readInt();
		
		if(CommonSetting.serverMapNeedGrid)
		{
			grid=new GridMapInfoConfig();
			grid.readBytesSimple(stream);
		}
		
		if(CommonSetting.serverMapNeedRecast)
		{
			recast=new RecastMapInfoConfig();
			recast.readBytesSimple(stream);
		}
	}
	
	public static void afterReadConfigAll()
	{
		if(CommonSetting.serverMapNeedGrid)
		{
			GridMapInfoConfig.afterReadConfigAll();
		}
	}
}
