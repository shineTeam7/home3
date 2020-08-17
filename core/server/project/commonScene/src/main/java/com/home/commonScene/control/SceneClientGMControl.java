package com.home.commonScene.control;

import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonScene.part.ScenePlayer;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.pool.StringBuilderPool;

/** 场景服gm指令控制 */
public class SceneClientGMControl
{
	/** 指令全组 */
	private SSet<String> _cmdSet=new SSet<>();
	
	private SMap<String,Obj> _cmdDic=new SMap<>();
	private SList<DescribeObj> _describeList=new SList<>();
	
	public void init()
	{
		registOne(this::help,"帮助","help");
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
	public void execute(ScenePlayer player,String cmd)
	{
		if(CommonSetting.isOfficial)
		{
			player.warnLog("正式服,依然有gm访问");
			return;
		}
		
		toExecute(player,cmd);
	}
	
	/** 执行gm */
	protected void toExecute(ScenePlayer player,String cmd)
	{
		if(cmd.isEmpty())
		{
			return;
		}
		
		player.log("clientGM指令",cmd);
		
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
		
		public boolean call(ScenePlayer me,int[] ints,String[] strs)
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
		void call(ScenePlayer me,int[] ints,String[] strs);
	}
	
	protected interface FuncCall2
	{
		void call(ScenePlayer me,int[] ints);
	}
	
	//--方法组--//
	
	/** 帮助 */
	protected void help(ScenePlayer me,int[] ints)
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
	
	
}
