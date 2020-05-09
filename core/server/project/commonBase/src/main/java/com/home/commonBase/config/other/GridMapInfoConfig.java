package com.home.commonBase.config.other;

import com.home.commonBase.config.base.BaseConfig;
import com.home.commonBase.config.game.enumT.MapBlockTypeConfig;
import com.home.commonBase.config.game.enumT.MapMoveTypeConfig;
import com.home.commonBase.constlist.generate.MapMoveType;
import com.home.commonBase.control.MapControl;
import com.home.commonBase.global.CommonSetting;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.utils.MathUtils;

/** 格子地图信息配置 */
public class GridMapInfoConfig extends BaseConfig
{
	public boolean isEmpty=false;
	
	public int width;
	
	public int height;
	/** 高 数位(左移几位) */
	public int heightW;
	/** 主格子组 */
	public byte[] mainGrids;
	/** 辅助格子组 */
	public byte[] secondGrids;
	/** 连通区 */
	public int[][] areaDic;
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		int len=stream.readLen();
		
		if(len==0)
		{
			width=0;
			height=0;
			isEmpty=true;
			return;
		}
		
		width=stream.readLen();
		height=stream.readLen();
		
		int wq=MathUtils.getPowerOf2(width);
		int hq=MathUtils.getPowerOf2(height);
		
		heightW=MathUtils.getIntBitNum(hq)-1;
		
		int length=wq*hq;
		
		mainGrids=new byte[length];
		
		if(CommonSetting.serverMapNeedSecondGrid)
			secondGrids=new byte[length];
		
		for(int i=0;i<width;i++)
		{
			for(int j=0;j<height;j++)
			{
				int index=i<<heightW | j;
				mainGrids[index]=stream.readByteB();
				
				if(CommonSetting.serverMapNeedSecondGrid)
					secondGrids[index]=stream.readByteB();
			}
		}
	}
	
	@Override
	protected void afterReadConfig()
	{
		super.afterReadConfig();
		
		if(mainGrids!=null && mainGrids.length==0)
		{
			//用空来代替
			mainGrids=null;
			secondGrids=null;
		}
		else
		{
		
		}
	}
	
	public int getGridIndex(int x,int y)
	{
		return x<<heightW | y;
	}
	
	public int getGrid(int x,int y)
	{
		return mainGrids[x<<heightW | y];
	}
	
	public int getSecondGrid(int x,int y)
	{
		return secondGrids[x<<heightW | y];
	}
	
	/** 读完所有表后处理 */
	public static void afterReadConfigAll()
	{
		MapBlockTypeConfig.afterReadConfigAll();
		
		IntObjectMap<MapInfoConfig> dic=MapInfoConfig.getDic();
		
		for(MapInfoConfig v:dic)
		{
			GridMapInfoConfig config=v.grid;
			
			if(config.mainGrids!=null)
			{
				config.areaDic=new int[MapMoveType.size][];
				
				for(int i=0;i<MapMoveType.size;i++)
				{
					MapMoveTypeConfig mapMoveTypeConfig=MapMoveTypeConfig.get(i);
					
					if(mapMoveTypeConfig!=null && mapMoveTypeConfig.needLinkArea)
					{
						config.areaDic[i]=MapControl.calculateGridArea(config,i);
					}
				}
			}
		}
		
		if(CommonSetting.usePathAreaDic && !dic.isEmpty())
		{
			Ctrl.log("计算连通区时间:",MapControl.loadTime);
		}
	}
	
	/** 两点是否在同一个连通区 */
	public boolean isSameArea(int moveType,int fx,int fy,int tx,int ty)
	{
		int[] dic=areaDic[moveType];
		
		//没有的时候视为在
		if(dic==null)
			return true;
		
		int fIndex=dic[fx<< heightW | fy];
		int tIndex=dic[tx<< heightW | ty];

		return fIndex!=0 && fIndex==tIndex;
	}
}
