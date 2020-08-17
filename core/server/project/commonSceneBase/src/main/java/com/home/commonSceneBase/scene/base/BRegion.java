package com.home.commonSceneBase.scene.base;

import com.home.commonBase.constlist.scene.SceneAOIType;
import com.home.commonBase.dataEx.scene.SceneAOITowerData;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.Unit;
import com.home.commonSceneBase.scene.scene.SceneAOITowerLogic;

public class BRegion extends Region
{
	protected BScene _bScene;
	
	protected SceneAOITowerLogic _sceneTowerAOI;
	
	@Override
	public void init()
	{
		_bScene=(BScene)_scene;
		
		super.init();
		
		//灯塔
		if(_scene.aoi.getAOIType()==SceneAOIType.Tower)
		{
			_sceneTowerAOI=(SceneAOITowerLogic)_scene.aoi;
			
			if(!_boundRect.isEmpty())
			{
				int tx=_sceneTowerAOI.countTowerX(_boundRect.x);
				int tz=_sceneTowerAOI.countTowerZ(_boundRect.y);
				
				int tr=_sceneTowerAOI.countTowerX(_boundRect.getRight());
				int tb=_sceneTowerAOI.countTowerZ(_boundRect.getBottom());
				
				SceneAOITowerData tower;
				
				for(int i=tx;i<=tr;i++)
				{
					for(int j=tz;j<=tb;j++)
					{
						(tower=_sceneTowerAOI.getTower(i,j)).regionDic.put(_data.instanceID,this);
						
						if(_config.onlyCharacter)
						{
							if(!tower.characterDic.isEmpty())
							{
								tower.characterDic.forEachValue(v->
								{
									if(isInRegion(v))
									{
										doEnterRegion(v);
									}
								});
							}
						}
						else
						{
							if(!tower.dic.isEmpty())
							{
								tower.dic.forEachValue(v->
								{
									if(isInRegion(v))
									{
										doEnterRegion(v);
									}
								});
							}
						}
						
						
					}
				}
			}
		}
		else if(_scene.aoi.getAOIType()==SceneAOIType.All)
		{
			_scene.getCharacterDic().forEachValue(v->
			{
				if(isInRegion(v))
				{
					doEnterRegion(v);
				}
			});
		}
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		//灯塔
		if(_scene.aoi.getAOIType()==SceneAOIType.Tower)
		{
			if(!_boundRect.isEmpty())
			{
				int tx=_sceneTowerAOI.countTowerX(_boundRect.x);
				int tz=_sceneTowerAOI.countTowerZ(_boundRect.y);
				
				int tr=_sceneTowerAOI.countTowerX(_boundRect.getRight());
				int tb=_sceneTowerAOI.countTowerZ(_boundRect.getBottom());
				
				for(int i=tx;i<=tr;i++)
				{
					for(int j=tz;j<=tb;j++)
					{
						_sceneTowerAOI.getTower(i,j).regionDic.remove(_data.instanceID);
					}
				}
			}
		}
		
		_containUnits.forEachValueS(v->
		{
			doLeaveRegion(v);
		});
	}
	
	@Override
	protected void doRegionAction(int[] args,Unit unit,boolean isEnter)
	{
		super.doRegionAction(args,unit,isEnter);
		
		
	}
}
