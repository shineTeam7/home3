using System;
using ShineEngine;

/// <summary>
/// 地图配置
/// </summary>
public class MapInfoConfig:BaseConfig
{
	/** 数据组 */
	protected static IntObjectMap<MapInfoConfig> _dic=new IntObjectMap<MapInfoConfig>();

	/** 地图ID */
	public int id;
	/** 格子地图 */
	public GridMapInfoConfig grid;

	public static void load(int id,Action<MapInfoConfig> func)
	{
		BaseConfig.toLoadSplit(ConfigType.MapInfo,CommonSetting.mapInfo,id,_dic,func);
	}

	public static MapInfoConfig getSync(int id)
	{
		return toGetSync(ConfigType.MapInfo,CommonSetting.mapInfo,id,_dic);
	}

	public static void unload(int id)
	{
		_dic.remove(id);
		BaseConfig.toUnloadSplit(ConfigType.MapInfo,CommonSetting.mapInfo,id);
	}

	/** 读取字节流(简版) */
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);

		id=stream.readInt();

		if(CommonSetting.clientMapNeedGrid)
		{
			grid=new GridMapInfoConfig();
			grid.readBytesSimple(stream);
		}
	}
}