package com.home.commonBase.scene.path;

import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.dataEx.scene.NavMeshHit;
import com.home.commonBase.extern.ExternMethod;
import com.home.commonBase.extern.ExternMethodNative;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;

public class RecastSceneMap extends BaseSceneMap
{
	/** c场景指针 */
	private long _scenePtr=0L;
	
	private NavMeshHit _tempHit=new NavMeshHit();
	
	@Override
	public void init()
	{
		super.init();
		
		_scenePtr=ExternMethodNative.createScene(_scene.getMapConfig().id);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		if(_scenePtr!=0L)
		{
			ExternMethodNative.removeScene(_scenePtr);
			_scenePtr=0L;
		}
	}
	
	public boolean getHeight(PosData pos, int mask)
	{
		//TODO:物理实现
		return true;
	}
	
	private void makeTerrainPos(PosData pos)
	{
		//TODO:物理实现
	}
	
	@Override
	public boolean isPosEnabled(int moveType,PosData pos,boolean isClient)
	{
		PosData originPos=_originPos;
		PosData endPos=_endPos;
		
		//超出地图边界
		if(pos.x<originPos.x || pos.z<originPos.z || pos.x>endPos.x || pos.z>endPos.z)
		{
			return false;
		}
		
		if(_scenePtr==0L)
			return true;
		
		boolean re=ExternMethod.samplePosition(_scenePtr,_scene.getExecutor().externBuf,pos,_tempHit,Global.mapSamplePositionRadius,BaseC.constlist.mapMoveType_getMask(moveType));
		
		return re;
	}
	
	@Override
	public void findRayPos(int moveType,PosData re,PosData from,float direction,float length)
	{
		re.y=from.y;
		_scene.pos.polar2D(re,length,direction);
		_scene.pos.addPos2D(re,from);
		_scene.pos.clampPos(re);
		
		makeTerrainPos(re);
		
		if(_scenePtr==0L)
			return;
		
		if(ExternMethod.raycast(_scenePtr,_scene.getExecutor().externBuf,from,re,_tempHit,BaseC.constlist.mapMoveType_getMask(moveType)))
		{
			//赋值为碰撞点
			re.copyPos(_tempHit.position);
		}
	}
	
	@Override
	public void findPath(SList<PosData> list,int moveType,boolean needCrowed,PosData from,PosData target)
	{
		list.clear();
		
		if(_scenePtr==0L)
		{
			list.add((PosData)target.clone());
			return;
		}
		
		//如失败则为空路径
		ExternMethod.calculatePath(_scenePtr,_scene.getExecutor().externBuf,from,target,list,BaseC.constlist.mapMoveType_getMask(moveType));
	}
	
	@Override
	public int getBlockType(PosData pos)
	{
		return 0;
	}
}
