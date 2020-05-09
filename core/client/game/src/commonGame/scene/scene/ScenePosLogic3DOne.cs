using System;
using ShineEngine;
using UnityEngine;
using UnityEngine.AI;

/// <summary>
/// 场景位置逻辑3D(方案一,mmo用)
/// </summary>
public class ScenePosLogic3DOne:ScenePosLogic
{
	private NavMeshPath _navMeshPath=new NavMeshPath();

	public override void findRayPos(int moveType,PosData re,PosData from,float direction,float length)
	{
		re.y=from.y;
		polar2D(re,length,direction);
		addPos2D(re,from);
		clampPos(re);


		BaseGameUtils.makeTerrainPos(re);
		if(NavMesh.Raycast(from.getVector(),re.getVector(),out NavMeshHit hit,BaseC.constlist.mapMoveType_getMask(moveType)))
		{
			//赋值为碰撞点
			re.setByVector(hit.position);
		}
	}

	public override void findPath(int moveType,SList<PosData> list,PosData from,PosData target)
	{
		list.clear();

		//有路径
		if(NavMesh.CalculatePath(from.getVector(),target.getVector(),BaseC.constlist.mapMoveType_getMask(moveType),_navMeshPath))
		{
			Vector3[] corners=_navMeshPath.corners;
			PosData p;

			if(corners.Length>0)
			{
				//跳过起始点
				for(int i=1;i<corners.Length;i++)
				{
					p=new PosData();
					p.setByVector(corners[i]);
					list.add(p);
				}
			}
		}
	}
}