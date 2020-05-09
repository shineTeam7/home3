package com.home.commonCenter.control;

import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonCenter.global.CenterC;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;

/** 中心服客户端GM指令组 */
public class CenterClientGMControl
{
	private SMap<String,Obj> _cmdDic=new SMap<>();
	
	private SList<DescribeObj> _describeList=new SList<>();
	
	private SSet<String> _cmdSet=new SSet<>();
	
	public void init()
	{
		regist();
		
		_cmdDic.forEach((k,v)->
		{
			_cmdSet.add(k);
		});
	}
	
	public SSet<String> getCMDSet()
	{
		return _cmdSet;
	}
	
	/** 注册指令组 */
	protected void regist()
	{
		registOne(this::changeServerTimeOff,"更改服务器时间偏差 [value]","changeServerTime","changeST","cst");
		registOne(this::addServerTimeByMinute,"增加服务器分钟 [num]","addServerMinute","addSM");
		registOne(this::addServerTimeByHour,"增加服务器小时 [num]","addServerHour","addSH");
		registOne(this::addServerTimeByDay,"增加服务器天 [num]","addServerDay","addSD");
		
		registOne(this::forceCloseActivity,"强制关闭某活动 [id]","forceCloseActivity","fca");
		registOne(this::cancelForceCloseActivity,"取消强制关闭某活动 [id]","cancelForceCloseActivity","cfca");
		
		registOne(this::addCenterMail,"添加中心服全服邮件 [id] [title] [content] [itemID] [num]","addCenterMail","addCM");
		//registOne(this::testCenter,"中心服测试","testC");
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
	
	protected void toRegistOne(Obj obj,String describe,String... cmds)
	{
		for(String v:cmds)
		{
			addCmdObj(v,obj);
		}
		
		_describeList.add(new DescribeObj(describe,cmds));
	}
	
	private void addCmdObj(String cmd,Obj obj)
	{
		if(_cmdDic.contains(cmd))
		{
			Ctrl.throwError("重复的gm指令");
			return;
		}
		
		_cmdDic.put(cmd,obj);
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
		
		public boolean call(long playerID,int[] ints,String[] strs)
		{
			try
			{
				if(func!=null)
				{
					func.call(playerID,ints,strs);
					return true;
				}
				else if(func2!=null)
				{
					func2.call(playerID,ints);
					return true;
				}
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
				
				//错误的指令
				CenterC.main.sendInfoCodeToPlayer(playerID,InfoCodeType.ClientGMFailed);
			}
			
			return false;
		}
	}
	
	protected interface FuncCall
	{
		void call(long playerID,int[] ints,String[] strs);
	}
	
	protected interface FuncCall2
	{
		void call(long playerID,int[] ints);
	}
	
	/** 执行gm */
	public void execute(long playerID,String cmd)
	{
		if(cmd.isEmpty())
		{
			return;
		}
		
		String[] args=cmd.split(" ");
		
		Obj obj=_cmdDic.get(args[0]);
		
		if(obj==null)
		{
			//错误的指令
			CenterC.main.sendInfoCodeToPlayer(playerID,InfoCodeType.ClientGMFailed);
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
		
		if(obj.call(playerID,ints,strs))
		{
			CenterC.main.sendInfoCodeToPlayer(playerID,InfoCodeType.ClientGMSuccess);
		}
	}
	
	
	/** 更改服务器时间偏移 */
	private void changeServerTimeOff(long playerID,int[] ints)
	{
		long offTime=CenterC.global.system.getOffTime();
		CenterC.global.system.setOffTime(offTime + ints[0]);
	}
	
	/** 增加服务器时间->分钟 */
	private void addServerTimeByMinute(long playerID,int[] ints)
	{
		long offTime=CenterC.global.system.getOffTime();
		CenterC.global.system.setOffTime(offTime + ints[0]*1000*60);
	}
	
	/** 增加服务器时间->小时 */
	private void addServerTimeByHour(long playerID,int[] ints)
	{
		long offTime=CenterC.global.system.getOffTime();
		CenterC.global.system.setOffTime(offTime + ints[0]*1000*60*60);
	}
	
	/** 增加服务器时间->天 */
	private void addServerTimeByDay(long playerID,int[] ints)
	{
		long offTime=CenterC.global.system.getOffTime();
		CenterC.global.system.setOffTime(offTime + ints[0]*1000*60*60*24);
	}
	
	/** 强制关闭活动 */
	private void forceCloseActivity(long playerID,int[] ints)
	{
		CenterC.global.activity.forceCloseActivityByGM(ints[0]);
	}
	
	/** 取消强制关闭活动 */
	private void cancelForceCloseActivity(long playerID,int[] ints)
	{
		CenterC.global.activity.cancelForceCloseActivityByGM(ints[0]);
	}
	
	/** 增加中心服全服邮件 */
	private void addCenterMail(long playerID,int[] ints,String[] strs)
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
		
		CenterC.global.mail.addMail(ints[0],strArgs,intArgs[0],intArgs[1]);
	}
	
	//private void testCenter(long playerID,int[] ints,String[] strs)
	//{
	//	switch(ints[0])
	//	{
	//		case 1:
	//		{
	//			GMAddCurrencyOWData data=new GMAddCurrencyOWData();
	//			data.type=CurrencyType.Coin;
	//			data.value=100;
	//
	//			CenterC.main.addPlayerOfflineWork(playerID,data);
	//		}
	//			break;
	//	}
	//
	//
	//}
}
