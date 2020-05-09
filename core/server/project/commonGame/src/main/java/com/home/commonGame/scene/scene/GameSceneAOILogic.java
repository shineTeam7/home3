package com.home.commonGame.scene.scene;

import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.SceneAOILogic;
import com.home.commonBase.scene.unit.UnitFightLogic;
import com.home.commonGame.net.request.scene.scene.AddUnitRequest;
import com.home.commonGame.net.request.scene.scene.RemoveUnitRequest;
import com.home.commonGame.scene.base.GameScene;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.LongSet;

public abstract class GameSceneAOILogic extends SceneAOILogic
{
	/** 游戏服场景 */
	protected GameScene _gameScene;
	
	protected LongSet _sendSet=new LongSet();
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_gameScene=(GameScene)scene;
	}
	
	@Override
	public void unitAdd(Unit unit,boolean needSelf)
	{
		radioUnitAdd(unit,needSelf);
	}
	
	protected void radioUnitAdd(Unit unit,boolean needSelf)
	{
		//是否可战斗
		if(unit.canFight())
		{
			boolean wrote=false;
			
			UnitFightLogic fight=unit.fight;
			
			//是c单位
			if(needSelf && unit.hasSocket())// && unit.isCUnitNotM()
			{
				unit.beforeWrite();
				wrote=true;
				
				fight.switchSendSelf();
				unit.send(AddUnitRequest.create(unit.getUnitData()));
				fight.endSwitchSend();
				
				if(CommonSetting.openAOICheck)
					unit.aoi.recordAddUnit(unit.instanceID);
			}
			
			if(unitNeedRatio(unit))
			{
				if(!wrote)
					unit.beforeWrite();
				
				fight.switchSendOther();
				radioMessage(unit,AddUnitRequest.create(unit.getUnitData()),false);
				fight.endSwitchSend();
			}
		}
		else
		{
			if(unitNeedRatio(unit))
			{
				unit.beforeWrite();
				radioMessage(unit,AddUnitRequest.create(unit.getUnitData()),needSelf);
			}
		}
	}
	
	/** 创建单位添加消息 */
	public AddUnitRequest createUnitAddRequestForOther(Unit unit)
	{
		unit.beforeWrite();
		
		//是否可战斗
		if(unit.canFight())
		{
			UnitFightLogic fight=unit.fight;
			
			fight.switchSendOther();
			AddUnitRequest request=AddUnitRequest.create(unit.getUnitData());
			fight.endSwitchSend();
			return request;
		}
		else
		{
			return AddUnitRequest.create(unit.getUnitData());
		}
	}
	
	@Override
	public void unitRemove(Unit unit,boolean needSelf)
	{
		radioUnitRemove(unit,needSelf);
	}
	
	protected void radioUnitRemove(Unit unit,boolean needSelf)
	{
		radioMessage(unit,RemoveUnitRequest.create(unit.instanceID),needSelf);
	}
	
	/** 实际广播消息 */
	protected void toRadioMessage(long selfID,BaseRequest request,Unit[] values)
	{
		toRadioMessage(selfID,request,values,values.length);
	}
	
	/** 实际广播消息(排除controlID为selfID的) */
	protected void toRadioMessage(long selfID,BaseRequest request,Unit[] values,int length)
	{
		boolean written=false;
		
		LongSet sendSet;
		(sendSet=_sendSet).clear();
		
		Unit v;
		BaseSocket socket;
		long targetID;
		
		for(int i=length - 1;i >= 0;--i)
		{
			if((v=values[i])!=null)
			{
				if((targetID=v.identity.controlPlayerID)!=-1 && (targetID!=selfID) && !sendSet.contains(targetID))
				{
					if(v.identity.socketReady && (socket=v.getSocket())!=null)
					{
						if(!written)
						{
							written=true;
							request.write();
						}
						
						socket.send(request);
						sendSet.add(targetID);
						
						if(CommonSetting.openAOICheck)
						{
							if(request instanceof AddUnitRequest)
							{
								AddUnitRequest aRequest=(AddUnitRequest)request;
								
								v.aoi.recordAddUnit(aRequest.data.instanceID);
							}
							
							if(request instanceof RemoveUnitRequest)
							{
								RemoveUnitRequest aRequest=(RemoveUnitRequest)request;
								
								v.aoi.recordRemoveUnit(aRequest.instanceID);
							}
						}
					}
				}
			}
		}
		
		sendSet.clear();
	}
	
	@Override
	public void radioMessageAll(BaseRequest request)
	{
		LongObjectMap<Unit> dic=_scene.getCharacterDic();
		
		if(!dic.isEmpty())
		{
			toRadioMessage(-1L,request,dic.getValues());
		}
	}
}
