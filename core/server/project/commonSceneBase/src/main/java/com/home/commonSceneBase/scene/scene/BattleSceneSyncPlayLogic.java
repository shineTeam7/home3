package com.home.commonSceneBase.scene.scene;

import com.home.commonSceneBase.scene.scene.BSceneMethodLogic;

/** 帧同步副本逻辑 */
public class BattleSceneSyncPlayLogic extends BSceneMethodLogic
{
	///** 帧计时 */
	//private int _frameTime=0;
	///** 帧序号 */
	//private int _frameIndex=0;
	///** 帧数据 */
	//private SList<FrameSyncData> _frames=new SList<>(FrameSyncData[]::new);
	///** 接收指令组 */
	//private SList<FrameSyncCommandData> _receiveCommands=new SList<>(FrameSyncCommandData[]::new);
	//
	//@Override
	//protected void onStart()
	//{
	//	super.onStart();
	//
	//	for(int i=Global.frameSyncCacheFrames=-1;i>=0;--i)
	//	{
	//		pushFrame(false);
	//	}
	//
	//	_scene.setFrameSync(true);
	//
	//	_scene.aoi.radioMessageAll(FrameSyncStartRequest.create(_frames));
	//}
	//
	//@Override
	//public void onFrame(int delay)
	//{
	//	super.onFrame(delay);
	//
	//	//还未开始
	//	if(!_scene.isFrameSync())
	//		return;
	//
	//	int t=_frameTime+delay;
	//
	//	while(t>= ShineSetting.systemFrameDelay)
	//	{
	//		t-=ShineSetting.systemFrameDelay;
	//		pushFrame(true);
	//	}
	//
	//	_frameTime=t;
	//}
	//
	//private void pushFrame(boolean needSend)
	//{
	//	FrameSyncData data=new FrameSyncData();
	//	data.index=_frameIndex++;
	//
	//	SList<FrameSyncCommandData> receiveCommands;
	//
	//	if(!(receiveCommands=_receiveCommands).isEmpty())
	//	{
	//		data.commands=new SList<>(FrameSyncCommandData[]::new,receiveCommands.size());
	//
	//		receiveCommands.forEachAndClear(v->
	//		{
	//			data.commands.add(v);
	//		});
	//	}
	//
	//	_frames.add(data);
	//
	//	if(needSend)
	//	{
	//		_scene.aoi.radioMessageAll(FrameSyncFrameRequest.create(data));
	//	}
	//}
	//
	//public void receiveCommand(long playerID,FrameSyncCommandData data)
	//{
	//	//还未开始
	//	if(!_scene.isFrameSync())
	//		return;
	//
	//	//写序号
	//	data.index=_scene.getCharacterByPlayerID(playerID).getUnitData().getCharacterIdentity().syncIndex;
	//
	//	_receiveCommands.add(data);
	//}
}
