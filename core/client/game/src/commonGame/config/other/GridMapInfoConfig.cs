using System;
using ShineEngine;

/// <summary>
///
/// </summary>
public class GridMapInfoConfig:BaseConfig
{
	public bool isEmpty=false;

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
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);

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

	protected override void afterReadConfig()
	{
		base.afterReadConfig();

		if(mainGrids!=null && mainGrids.Length==0)
		{
			//用空来代替
			mainGrids=null;
			secondGrids=null;
		}

		if(mainGrids!=null)
		{
			// areaDic=new int[MapMoveType.size][];
			//
			// for(int i=0;i<MapMoveType.size;i++)
			// {
			// 	MapMoveTypeConfig mapMoveTypeConfig=MapMoveTypeConfig.get(i);
			//
			// 	if(mapMoveTypeConfig!=null && mapMoveTypeConfig.needLinkArea)
			// 	{
			// 		areaDic[i]=MapControl.calculateGridArea(config,i);
			// 	}
			// }
		}
	}

	/** 获取格子序号 */
	public int getGridIndex(int x,int y)
	{
		return x << heightW | y;
	}

	public int getGrid(int x,int y)
	{
		return mainGrids[x<<heightW | y];
	}

	public int getSecondGrid(int x,int y)
	{
		return secondGrids[x<<heightW | y];
	}

	/** 两点是否在同一个连通区 */
	public bool isSameArea(int moveType,int fx,int fy,int tx,int ty)
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