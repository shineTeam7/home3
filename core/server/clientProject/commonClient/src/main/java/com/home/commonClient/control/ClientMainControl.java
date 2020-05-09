package com.home.commonClient.control;

import com.home.commonBase.config.game.RobotFlowStepConfig;
import com.home.commonBase.constlist.generate.RobotFlowStepType;
import com.home.commonClient.global.ClientC;
import com.home.commonClient.global.ClientGlobal;
import com.home.commonClient.part.player.Player;
import com.home.commonClient.tool.generate.CenterRequestMaker;
import com.home.commonClient.tool.generate.CenterResponseMaker;
import com.home.commonClient.tool.generate.GameRequestMaker;
import com.home.commonClient.tool.generate.GameResponseMaker;
import com.home.shine.control.BytesControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.tool.DataMaker;
import com.home.shine.tool.generate.ShineRequestMaker;
import com.home.shine.tool.generate.ShineResponseMaker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** 客户端主控制 */
public class ClientMainControl
{
	private int _eIndex=0;
	
	/** 角色字典 */
	private ConcurrentHashMap<String,Player> _playerDicByUID=new ConcurrentHashMap<>();
	private Player _uniquePlayer;
	
	/** 角色数目 */
	private int _playerNum=0;
	
	/** 客户端协议构造器(唯一) */
	protected DataMaker _clientResponseMaker=new DataMaker();
	
	/** 执行器组(分线程) */
	private LogicExecutor[] _executors;
	
	private AtomicInteger[] _flowSteps;
	
	public void init()
	{
		_flowSteps=new AtomicInteger[RobotFlowStepType.size];
		for(int i=_flowSteps.length-1;i>=0;--i)
		{
		    _flowSteps[i]=new AtomicInteger(0);
		
		}
		
		makeResponse();
		
		//注册消息
		BytesControl.addDataMaker(_clientResponseMaker);
		
		_executors=new LogicExecutor[ShineSetting.poolThreadNum];
		
		for(int i=0;i<ShineSetting.poolThreadNum;++i)
		{
			LogicExecutor executor=createExecutor(i);
			_executors[i]=executor;
			
			ThreadControl.addPoolFunc(i,()->
			{
				executor.init();
			});
		}
	}
	
	protected void makeResponse()
	{
		BytesControl.addRequestMaker(new ShineRequestMaker());
		_clientResponseMaker.addDic(new ShineResponseMaker());
		BytesControl.addRequestMaker(new GameRequestMaker());
		_clientResponseMaker.addDic(new GameResponseMaker());
		BytesControl.addRequestMaker(new CenterRequestMaker());
		_clientResponseMaker.addDic(new CenterResponseMaker());
	}

	public void dispose()
	{
		
	}
	
	/** 获取客户端协议构造器 */
	public DataMaker getClientResponseMaker()
	{
		return _clientResponseMaker;
	}
	
	/** 创建执行器 */
	protected LogicExecutor createExecutor(int index)
	{
		return new LogicExecutor(index);
	}
	
	/** 获取对应执行器 */
	public LogicExecutor getExecutor(int index)
	{
		if(index==-1)
		{
			return null;
		}
		
		return _executors[index];
	}
	
	/** 创建一个client(主线程) */
	public void createClient(String uid)
	{
		Player player=ClientC.factory.createPlayer();
		player.construct();
		player.role.uid=uid;
		
		Ctrl.log("客户端登录",uid);
		
		int index=_eIndex++ & ThreadControl.poolThreadNumMark;
		
		_playerDicByUID.put(uid,player);
		
		if(_uniquePlayer==null)
			_uniquePlayer=player;
		
		++_playerNum;
		
		player.recordStep(RobotFlowStepType.CreateClient);
		
		LogicExecutor executor=getExecutor(index);
		
		executor.addFunc(()->
		{
			executor.playerLogin(player);
		});
	}
	
	/** 获取唯一角色 */
	public Player getUniquePlayer()
	{
		return _uniquePlayer;
	}
	
	/** 获取客户端 */
	public Player getPlayerByUID(String uid)
	{
		return _playerDicByUID.get(uid);
	}
	
	/** 移除一个客户端(移除)(主线程) */
	public void onRemoveClient(String uid)
	{
		Player player=getPlayerByUID(uid);
		
		if(player==null)
		{
			return;
		}
		
		Ctrl.print("客户端移除",uid);
		
		_playerDicByUID.remove(uid);
		--_playerNum;
		
		if(player==_uniquePlayer)
			_uniquePlayer=null;
	}
	
	public int getPlayerNum()
	{
		return _playerNum;
	}

	/** 记录流程步(系统调用) */
	public void recordStep(Player player,int step)
	{
		int num=_flowSteps[step].incrementAndGet();
		int max=ClientGlobal.num;
		
		player.log("step:"+step+" "+ RobotFlowStepConfig.get(step).describe+" ("+num+"/"+max+")");
	}
}
