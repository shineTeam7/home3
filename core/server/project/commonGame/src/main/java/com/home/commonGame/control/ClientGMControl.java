package com.home.commonGame.control;

import com.home.commonBase.constlist.generate.AttributeType;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.logic.unit.AttributeDataLogic;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.serverRequest.center.system.ClientGMToCenterServerRequest;
import com.home.commonGame.net.serverRequest.scene.system.ClientGMToSceneServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.pool.StringBuilderPool;

/** 客户端GM消息 */
public class ClientGMControl
{
	/** 指令全组 */
	private SSet<String> _cmdSet=new SSet<>();
	
	private SMap<String,Obj> _cmdDic=new SMap<>();
	private SList<DescribeObj> _describeList=new SList<>();
	
	public void init()
	{
		registOne(this::help,"帮助","help");
		
		registOne(this::addCurrency,"添加货币 [type] [value]","addCurrency","addC","ac");
		registOne(this::costCurrency,"消耗货币 [type] [value]","costCurrency","costC","cc");
		registOne(this::addRoleExp,"添加经验 [value]","addExp","addE","ae");
		registOne(this::roleLevelUp,"升级 [level](不带参为升一级)","levelUp","level","lv");
		registOne(this::charge,"充值 [value]","charge","cg");
		registOne(this::socketTest,"闪断测试","socketTest");
		registOne(this::triggerGMCommand,"triggerGM指令","triggerCMD","tg");
		
		//角色相关
		registOne(this::addCharacterAttribute,"增加主角属性 [type] [value]","addAttribute","addA","attr","aa");
		registOne(this::addCharacterStatus,"增加主角状态 [type]","addStatus","addS");
		registOne(this::subCharacterStatus,"减少主角状态 [type]","subStatus","subS");
		registOne(this::addCharacterBuff,"增加主角buff [id] [level]","addBuff","addB","ab");
		registOne(this::removeCharacterBuff,"移除主角buff [id]","removeBuff","remB","rb");
		registOne(this::addCharacterSkill,"增加主角技能 [id] [level]","addCharacterSkill","addSK","ask");
		registOne(this::removeCharacterSkill,"移除主角技能 [id]","removeCharacterSkill","remSK","rsk");
		registOne(this::addPuppet,"添加傀儡 [id] [level]","addPuppet","addPPT");
		registOne(this::speed,"增加移速","speed");
		
		//场景
		registOne(this::enterScene,"申请进入场景 [id]","enterScene","scene");
		
		//物品
		registOne(this::addItem,"添加物品 [id] [num]","addItem","ditem","at");
		registOne(this::removeItem,"消耗物品 [id] [num]","removeItem","ritem","rt");
		registOne(this::printBag,"查看背包","printBag","bag","pb");
		
		//任务
		registOne(this::acceptOneQuest,"接取单个任务 [id]","acceptOneQuest","aaq");
		registOne(this::acceptQuest,"接取任务及前置 [id]","acceptQuest","aq");
		registOne(this::commitQuest,"提交任务及前置 [id]","commitQuest","cq");
		registOne(this::clearAllQuest,"清空任务记录","clearAllQuest","clq");
		registOne(this::taskEvent,"触发任务[type] [args]，参数可省略","taskEvent");
		
		//邮件
		registOne(this::printMails,"查看邮件列表","printMails","printMail","pm");
		registOne(this::addMail,"添加邮件 [id] [title] [content] [itemID,num,itemID,num]","addMail","mail");
		registOne(this::addServerMail,"添加服务器邮件 [id] [title] [content] [itemID] [num]","addServerMail","addSMail");
		
		//好友
		registOne(this::addFriend,"添加好友 [playerID]","addFriend");
		registOne(this::removeFriend,"删除好友 [playerID]","removeFriend");
		
		//引导
		registOne(this::setGuideStep,"修改引导步 [step]","setGuideStep","sgs");
	}
	
	private void addCmdObj(String cmd,Obj obj)
	{
		if(_cmdSet.contains(cmd))
		{
			Ctrl.throwError("重复的gm指令:"+cmd);
			return;
		}
		
		_cmdDic.put(cmd,obj);
		_cmdSet.add(cmd);
	}
	
	private void toRegistOne(Obj obj,String describe,String... cmds)
	{
		for(String v:cmds)
		{
			addCmdObj(v,obj);
		}
		
		_describeList.add(new DescribeObj(describe,cmds));
	}
	
	/** 注册一个 */
	protected void registOne(FuncCall func,String describe,String... cmds)
	{
		Obj obj=new Obj();
		obj.func=func;
		
		toRegistOne(obj,describe,cmds);
	}
	
	/** 注册一个 */
	protected void registOne(FuncCall2 func,String describe,String... cmds)
	{
		Obj obj=new Obj();
		obj.func2=func;
		
		toRegistOne(obj,describe,cmds);
	}
	
	/** 执行gm */
	public void execute(Player player,String cmd)
	{
		if(CommonSetting.isOfficial)
		{
			player.warnLog("正式服,依然有gm访问");
			return;
		}
		
		toExecute(player,cmd);
	}
	
	/** 执行gm */
	protected void toExecute(Player player,String cmd)
	{
		if(cmd.isEmpty())
		{
			return;
		}
		
		player.log("clientGM指令",cmd);
		
		if(cmd.startsWith("c "))
		{
			ClientGMToCenterServerRequest.create(player.role.playerID,cmd.substring(2)).send();
			return;
		}
		
		if(cmd.startsWith("s "))
		{
			SceneLocationData sceneLocation=player.scene.getSceneLocation();
			
			if(sceneLocation!=null)
			{
				ClientGMToSceneServerRequest.create(player.role.playerID,cmd.substring(2)).send(sceneLocation.serverID);
			}
			
			return;
		}
		
		String[] args=cmd.split(" ");
		
		Obj obj=_cmdDic.get(args[0]);
		
		if(obj==null)
		{
			//错误的指令
			player.sendInfoCode(InfoCodeType.ClientGMFailed);
			return;
		}
		
		int len=args.length - 1;
		int[] ints=new int[len];
		String[] strs=new String[len];
		
		for(int i=0;i<len;++i)
		{
			strs[i]=args[i + 1];
			
			try
			{
				ints[i]=Integer.parseInt(args[i + 1]);
			}
			catch(Exception e)
			{
			
			}
		}
		
		if(obj.call(player,ints,strs))
		{
			player.sendInfoCode(InfoCodeType.ClientGMSuccess);
		}
	}
	
	protected class DescribeObj
	{
		/** 描述 */
		public String describe;
		/** 指令名 */
		public String[] cmds;
		
		public DescribeObj(String describe,String[] cmds)
		{
			this.describe=describe;
			this.cmds=cmds;
		}
	}
	
	protected class Obj
	{
		public FuncCall func;
		public FuncCall2 func2;
		
		public boolean call(Player me,int[] ints,String[] strs)
		{
			try
			{
				if(func!=null)
				{
					func.call(me,ints,strs);
					return true;
				}
				else if(func2!=null)
				{
					func2.call(me,ints);
					return true;
				}
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
				
				//错误的指令
				me.sendInfoCode(InfoCodeType.ClientGMFailed);
			}
			
			return false;
		}
	}
	
	protected interface FuncCall
	{
		void call(Player me,int[] ints,String[] strs);
	}
	
	protected interface FuncCall2
	{
		void call(Player me,int[] ints);
	}
	
	//--方法组--//
	
	/** 帮助 */
	protected void help(Player me,int[] ints)
	{
		StringBuilder sb=StringBuilderPool.create();
		DescribeObj dObj;
		for(int i=1,len=_describeList.length();i<len;++i)
		{
			if(i>1)
				sb.append("\n");
			
			dObj=_describeList.get(i);
			
			for(int j=0;j<dObj.cmds.length;++j)
			{
				if(j>0)
				{
					sb.append(" / ");
				}
				
				sb.append("<color=orange>");
				sb.append(dObj.cmds[j]);
				sb.append("</color>");
			}
			
			sb.append(" : ");
			sb.append(dObj.describe);
		}
		
		me.sendInfoCode(InfoCodeType.ClientGMHelp,StringBuilderPool.releaseStr(sb));
	}
	
	/** 添加货币 */
	private void addCurrency(Player me,int[] ints)
	{
		me.role.addCurrency(ints[0],ints[1],CallWayType.AddCurrencyByClientGM);
	}
	
	/** 消耗货币 */
	private void costCurrency(Player me,int[] ints)
	{
		me.role.costCurrency(ints[0],ints[1],CallWayType.CostCurrencyByClientGM);
	}
	
	/** 添加角色经验 */
	private void addRoleExp(Player me,int[] ints)
	{
		me.role.addExp(ints[0],CallWayType.AddRoleExpByClientGM);
	}
	
	/** 角色升级 */
	private void roleLevelUp(Player me,int[] ints)
	{
		int level=ints.length>0 ? ints[0] : me.role.getLevel() + 1;
		
		me.role.levelUpByGM(level);
	}
	
	/** 充值 */
	private void charge(Player me,int[] ints)
	{
		me.role.chargeCash(ints[0],CallWayType.ChargeByClientGM);
	}
	
	/** 闪断测试 */
	private void socketTest(Player me,int[] ints)
	{
		BaseSocket socket;
		if((socket=me.system.socket)!=null)
		{
			socket.closeTest();
		}
	}
	
	/** 闪断测试 */
	private void triggerGMCommand(Player me,int[] ints)
	{
		me.scene.triggerGMCommand(ints);
	}
	
	//--主角相关--//
	
	/** 添加主角属性 */
	protected void addCharacterAttribute(Player me,int[] ints)
	{
		me.character.getCurrentAttributeDataLogic().addOneAttribute(ints[0],ints[1]);
		
		AttributeDataLogic currentAttributeDataLogic=me.character.getCurrentAttributeDataLogic();
	}
	
	/** 添加主角状态 */
	protected void addCharacterStatus(Player me,int[] ints)
	{
		me.character.getCurrentFightDataLogic().status.addStatus(ints[0]);
	}
	
	/** 减少主角状态 */
	protected void subCharacterStatus(Player me,int[] ints)
	{
		me.character.getCurrentFightDataLogic().status.subStatus(ints[0]);
	}
	
	/** 添加主角buff */
	protected void addCharacterBuff(Player me,int[] ints)
	{
		me.character.getCurrentBuffDataLogic().addBuff(ints[0],ints[1]);
	}
	
	/** 移除指定id的buff */
	protected void removeCharacterBuff(Player me,int[] ints)
	{
		me.character.getCurrentBuffDataLogic().removeBuffByID(ints[0]);
	}
	
	/** 添加主角skill */
	protected void addCharacterSkill(Player me,int[] ints)
	{
		me.character.getCurrentFightDataLogic().addSkill(ints[0],ints[1]);
	}
	
	/** 移除指定id的skill */
	protected void removeCharacterSkill(Player me,int[] ints)
	{
		me.character.getCurrentFightDataLogic().removeSkill(ints[0]);
	}
	
	/** 添加傀儡 */
	protected void addPuppet(Player me,int[] ints)
	{
		me.scene.getScene().unitFactory.createAddPuppet(ints[0],ints[1],me.scene.getUnit().pos.getPos(),me.scene.getUnit(),0);
	}
	
	//场景
	/** 进入某id的场景 */
	protected void enterScene(Player me,int[] ints)
	{
		me.scene.applyEnterScene(ints[0]);
	}
	
	/** 主角加移速 */
	protected void speed(Player me,int[] ints)
	{
		me.character.getCurrentAttributeDataLogic().addOneAttribute(AttributeType.MoveSpeed,1000);
	}
	
	//物品
	
	/** 添加物品 */
	private void addItem(Player me,int[] ints)
	{
		me.bag.addItem(ints[0],ints[1],CallWayType.AddItemByClientGM);
	}
	
	/** 消耗物品 */
	private void removeItem(Player me,int[] ints)
	{
		me.bag.removeItem(ints[0],ints[1],CallWayType.AddItemByClientGM);
	}
	
	/** 查看背包 */
	private void printBag(Player me,int[] ints)
	{
		me.bag.printBag();
	}
	
	//任务
	
	/** 接取单个任务 */
	private void acceptOneQuest(Player me,int[] ints)
	{
		me.quest.acceptQuestByGM(ints[0],false);
	}
	
	/** 接取任务 */
	private void acceptQuest(Player me,int[] ints)
	{
		me.quest.acceptQuestByGM(ints[0],true);
	}
	
	/** 提交任务 */
	private void commitQuest(Player me,int[] ints)
	{
		me.quest.commitQuestByGM(ints[0]);
	}
	
	/** 清空任务 */
	private void clearAllQuest(Player me,int[] ints)
	{
		me.quest.clearAllQuestByGM();
	}
	
	/** 触发任务 */
	protected void taskEvent(Player me,int[] ints)
	{
		int type=ints[0];
		int[] args=null;
		
		if(ints.length>1)
		{
			args=new int[ints.length-1];
			
			for(int i=1;i<ints.length;++i)
			{
				args[i - 1]=ints[i];
			}
		}
		
		me.quest.taskEvent(type,args);
	}
	
	//邮件
	
	/** 查看邮件列表 */
	private void printMails(Player me,int[] ints)
	{
		me.mail.printMails();
	}
	
	/** 添加邮件 */
	private void addMail(Player me,int[] ints,String[] strs)
	{
		int i;
		
		for(i=strs.length-1;i>=0;--i)
		{
			try
			{
				Integer.parseInt(strs[i]);
			}
			catch(Exception e)
			{
				break;
			}
		}
		
		String[] strArgs=new String[i-1];
		System.arraycopy(strs,1,strArgs,0,i-1);
		
		i++;
		int[] intArgs=new int[ints.length-i];
		System.arraycopy(ints,i,intArgs,0,ints.length-i);
		
		me.mail.addMailByGM(ints[0],strArgs,intArgs);
	}
	
	/** 添加服务器邮件 */
	private void addServerMail(Player me,int[] ints,String[] strs)
	{
		int i;
		
		for(i=strs.length-1;i>=0;--i)
		{
			try
			{
				Integer.parseInt(strs[i]);
			}
			catch(Exception e)
			{
				break;
			}
		}
		
		String[] strArgs=new String[i-1];
		System.arraycopy(strs,1,strArgs,0,i-1);
		
		i++;
		int[] intArgs=new int[ints.length-i];
		System.arraycopy(ints,i,intArgs,0,ints.length-i);
		
		me.addMainFunc(()->
		{
			GameC.global.mail.addMail(ints[0],strArgs,intArgs[0],intArgs[1]);
		});
	}
	
	//好友
	
	/** 添加好友 */
	protected void addFriend(Player me,int[] ints,String[] strs)
	{
		long playerID=Long.parseLong(strs[0]);
		
		me.friend.addFriend(playerID,ints[1]);
	}
	
	/** 移除好友 */
	protected void removeFriend(Player me,int[] ints,String[] strs)
	{
		long playerID=Long.parseLong(strs[0]);
		
		me.friend.removeFriend(playerID);
	}
	
	/** 修改引导步 */
	protected void setGuideStep(Player me,int[] ints,String[] strs)
	{
		int step=Integer.parseInt(strs[0]);
		
		me.guide.setMainStep(step,true);
	}
}
