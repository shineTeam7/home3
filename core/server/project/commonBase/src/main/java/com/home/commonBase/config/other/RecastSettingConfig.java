package com.home.commonBase.config.other;

//import org.recast4j.recast.AreaModification;
//import org.recast4j.recast.RecastConfig;
//import org.recast4j.recast.RecastConstants;

/** recast设置配置 */
public class RecastSettingConfig
{
	public float cellSize = 0.3f;
	public float cellHeight = 0.2f;
	public float agentHeight = 2.0f;
	public float agentRadius = 0.6f;
	public float agentMaxClimb = 0.9f;
	public float agentMaxSlope = 45.0f;
	public int regionMinSize = 8;
	public int regionMergeSize = 20;
	public float edgeMaxLen = 12.0f;
	public float edgeMaxError = 1.3f;
	public int vertsPerPoly = 6;
	public float detailSampleDist = 6.0f;
	public float detailSampleMaxError = 1.0f;
	
	///** 默认配置 */
	//public static RecastConfig defaultConfig;
	//
	//public RecastConfig createRecastConfig()
	//{
	//	//RecastConfig re=new RecastConfig(RecastConstants.PartitionType.WATERSHED,cellSize,cellHeight,
	//	//		agentHeight,agentRadius,agentMaxClimb,agentMaxSlope,regionMinSize,regionMergeSize,
	//	//		edgeMaxLen,edgeMaxError,vertsPerPoly,detailSampleDist,detailSampleMaxError,0,
	//	//		new AreaModification(SAMPLE_POLYAREA_TYPE_GROUND,SAMPLE_POLYAREA_TYPE_MASK));
	//
	//	//return re;
	//	return null;
	//}
}
