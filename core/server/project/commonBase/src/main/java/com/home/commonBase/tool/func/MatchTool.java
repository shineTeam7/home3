package com.home.commonBase.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.scene.match.PlayerMatchData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.LongSet;
import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.ObjectPool;
import com.home.shine.support.pool.PoolObject;

import java.util.Comparator;

/** 基础匹配插件(目前只支持单人匹配) */
public abstract class MatchTool extends FuncTool
{
	/** 匹配数目 */
	private int _batchNum;
	/** 高低差 */
	private int _dValue;
	/** 最长匹配时间(s) */
	private int _waitTimeMax=0;
	/** 是否需要等待匹配 */
	private boolean _needWaitAccept;
	
	
	/** 参与匹配角色字典(总) */
	private LongObjectMap<PlayerMatchData> _playerDic=new LongObjectMap<>(PlayerMatchData[]::new);
	
	/** 匹配中组 */
	private LongObjectMap<PlayerMatchData> _matchDic=new LongObjectMap<>(PlayerMatchData[]::new);
	/** 匹配临时组 */
	private SList<PlayerMatchData> _tempList=new SList<>(PlayerMatchData[]::new);
	
	private Comparator<PlayerMatchData> _comparator;
	
	//waitAccepts
	
	private ObjectPool<MatchWaitAcceptData> _waitDataPool;
	
	private int _waitAcceptIndex=0;
	
	private IntObjectMap<MatchWaitAcceptData> _waitAcceptDic;
	private LongObjectMap<MatchWaitAcceptData> _waitAcceptPlayerDic;
	
	//public MatchTool(int funcID,int batchNum)
	//{
	//	this(funcID,batchNum,-1,0,false);
	//}
	
	/** batchNum:每轮人数,dValue:最高和最低允许差值(-1为不看差距),waitTimeMax:最长等待时间(0:为无限等待),needWaitAccept:是否需要接受匹配阶段 */
	public MatchTool(int funcID,int batchNum,int dValue,int waitTimeMax,boolean needWaitAccept)
	{
		super(FuncToolType.Match,funcID);
		
		_batchNum=batchNum;
		_dValue=dValue;
		_waitTimeMax=waitTimeMax;
		_needWaitAccept=needWaitAccept;
		
		if(batchNum<=0)
		{
			Ctrl.throwError("匹配人数非法",batchNum);
		}
		
		if(needWaitAccept)
		{
			_waitAcceptDic=new IntObjectMap<>(MatchWaitAcceptData[]::new);
			_waitAcceptPlayerDic=new LongObjectMap<>(MatchWaitAcceptData[]::new);
			_waitDataPool=new ObjectPool<>(MatchWaitAcceptData::new);
			_waitDataPool.setEnable(CommonSetting.logicUsePool);
		}
		
		_comparator=this::compare;
	}
	
	/** 匹配 */
	private int compare(PlayerMatchData data0,PlayerMatchData data1)
	{
		if(data0.value>data1.value)
			return 1;
		
		if(data0.value<data1.value)
			return -1;
		
		return 0;
	}
	
	/** 添加匹配数据 */
	public boolean add(PlayerMatchData data)
	{
		if(_playerDic.contains(data.showData.playerID))
		{
			return false;
		}
		
		//新的匹配时间
		data.time=_waitTimeMax;
		
		_playerDic.put(data.showData.playerID,data);
		
		_matchDic.put(data.showData.playerID,data);
		
		return true;
	}
	
	/** 取消匹配(包括等待) */
	public boolean cancelMatch(long playerID)
	{
		if(!_playerDic.contains(playerID))
		{
			return false;
		}
		
		_playerDic.remove(playerID);
		
		//在匹配中
		if(_matchDic.contains(playerID))
		{
			_matchDic.remove(playerID);
			
			return true;
		}
		
		if(_needWaitAccept)
		{
			MatchWaitAcceptData waitAcceptData=_waitAcceptPlayerDic.get(playerID);
			
			if(waitAcceptData!=null)
			{
				//标记失效
				waitAcceptData.disabledDic.add(playerID);
				return true;
			}
		}
		
		return false;
	}
	
	/** 玩家是否匹配中 */
	public boolean isMatching(long playerID)
	{
		return _playerDic.contains(playerID);
	}
	
	@Override
	public void onSecond(int delay)
	{
		if(_needWaitAccept)
		{
			waitAcceptSecond();
		}
		
		matchSecond();
	}
	
	/** 等待接受每秒 */
	private void waitAcceptSecond()
	{
		//FIXME:SMap
		IntObjectMap<MatchWaitAcceptData> waitAcceptDic;
		
		if((waitAcceptDic=_waitAcceptDic).isEmpty())
			return;
		
		waitAcceptDic.forEachValueS(v->
		{
			if(--v.waitTime<=0)
			{
				removeWaitMatch(v);
				reAddWaitData(v);
			}
			
		});
	}
	
	/** 匹配每秒 */
	private void matchSecond()
	{
		//FIXME:SMap
		LongObjectMap<PlayerMatchData> matchDic=_matchDic;
		
		if(matchDic.isEmpty())
			return;
		
		SList<PlayerMatchData> tempList=_tempList;
		
		matchDic.forEachValueS(data->
		{
			//到时间
			if(_waitTimeMax>0 && (--data.time)<=0)
			{
				matchDic.remove(data.showData.playerID);
				
				matchTimeOutOne(data);
			}
			else
			{
				//放入匹配队列
				tempList.add(data);
			}
		});
		
		//排序一下
		tempList.sort(_comparator);
		
		PlayerMatchData[] values=tempList.getValues();
		
		int dd=_batchNum-1;
		
		int len=tempList.size();
		int index=0;
		int next;
		
		while(true)
		{
			//超了
			if((next=index+dd)>=len)
				break;
			
			//匹配值符合
			if(_dValue<0 || Math.abs(values[index].value-values[next].value)<=_dValue)
			{
				PlayerMatchData[] arr=new PlayerMatchData[_batchNum];
				System.arraycopy(values,index,arr,0,_batchNum);
				
				for(PlayerMatchData v:arr)
				{
					//从匹配组移除
					matchDic.remove(v.showData.playerID);
				}
				
				matchSuccessOne(arr);
				
				//位置
				index=next+1;
			}
			else
			{
				//下一个
				++index;
			}
		}
		
		tempList.clear();
	}
	
	private void reAddWaitData(MatchWaitAcceptData data)
	{
		LongObjectMap<PlayerMatchData> matchDic=_matchDic;
		
		for(PlayerMatchData v:data.matches)
		{
			//未失效
			if(!data.disabledDic.contains(v.showData.playerID))
			{
				//放回匹配组
				matchDic.put(v.showData.playerID,v);
				
				sendReAddMatch(v.showData.playerID);
			}
		}
	}
	
	/** 匹配超时一个 */
	private void matchTimeOutOne(PlayerMatchData data)
	{
		////当前组也移除
		_playerDic.remove(data.showData.playerID);
		
		onMatchTimeOut(data.showData.playerID);
	}
	
	/** 成功一组 */
	private void matchSuccessOne(PlayerMatchData[] matches)
	{
		if(_needWaitAccept)
		{
			MatchWaitAcceptData data=_waitDataPool.getOne();
			data.index=++_waitAcceptIndex;
			data.matches=matches;
			data.waitTime=Global.matchWaitTime;
			
			//添加等待组
			_waitAcceptDic.put(data.index,data);
			
			LongObjectMap<MatchWaitAcceptData> waitAcceptPlayerDic=_waitAcceptPlayerDic;
			
			for(PlayerMatchData v:matches)
			{
				waitAcceptPlayerDic.put(v.showData.playerID,data);
			}
			
			radioMatchSuccess(matches,data.index);
		}
		else
		{
			preMatchOverOne(matches);
		}
	}
	
	private void removeWaitMatch(MatchWaitAcceptData data)
	{
		_waitAcceptDic.remove(data.index);
		
		LongObjectMap<MatchWaitAcceptData> waitAcceptPlayerDic=_waitAcceptPlayerDic;
		
		for(PlayerMatchData v:data.matches)
		{
			waitAcceptPlayerDic.remove(v.showData.playerID);
		}
		
		_waitDataPool.back(data);
	}
	
	/** 接受匹配 */
	public void acceptMatch(int index,long playerID)
	{
		if(!_needWaitAccept)
		{
			Ctrl.warnLog("不支持waitMatch",_funcID);
			return;
		}
		
		MatchWaitAcceptData data=_waitAcceptDic.get(index);
		
		if(data==null)
		{
			Ctrl.warnLog("未找到waitMatch数据");
			return;
		}
		
		if(data.readies.add(playerID))
		{
			radioAcceptMatch(data.matches,playerID);
			
			//数够了
			if(data.readies.size()==_batchNum)
			{
				//移除
				removeWaitMatch(data);
				
				if(data.disabledDic.isEmpty())
				{
					preMatchOverOne(data.matches);
				}
				else
				{
					reAddWaitData(data);
				}
			}
		}
		else
		{
			Ctrl.warnLog("重复发送匹配accept",playerID);
		}
	}
	
	/** 预备结束一组 */
	private void preMatchOverOne(PlayerMatchData[] matches)
	{
		LongObjectMap<PlayerMatchData> playerDic=_playerDic;
		
		for(PlayerMatchData v:matches)
		{
			playerDic.remove(v.showData.playerID);
		}
		
		matchOverOne(matches);
	}
	
	/** 匹配结束一组 */
	protected abstract void matchOverOne(PlayerMatchData[] matches);
	
	/** 广播匹配成功 */
	protected abstract void radioMatchSuccess(PlayerMatchData[] matches,int matchIndex);
	
	/** 广播接收匹配 */
	protected abstract void radioAcceptMatch(PlayerMatchData[] matches,long playerID);
	
	/** 推送重新添加匹配 */
	protected abstract void sendReAddMatch(long playerID);
	
	/** 匹配超时 */
	protected abstract void onMatchTimeOut(long playerID);
	
	/** 获取匹配场景ID */
	public abstract int getMatchSceneID();
	
	private class MatchWaitAcceptData extends PoolObject
	{
		/** 序号 */
		public int index;
		/** 等待时间 */
		public int waitTime;
		/** ready组 */
		public LongSet readies=new LongSet();
		/** 匹配数据组 */
		public PlayerMatchData[] matches;
		/** 失效组 */
		public LongSet disabledDic=new LongSet();
		
		@Override
		public void clear()
		{
			readies.clear();
			matches=null;
			disabledDic.clear();
		}
	}
}
