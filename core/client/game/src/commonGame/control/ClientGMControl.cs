using System;
using System.Text;
using ShineEngine;

/// <summary>
/// 客户端gm指令
/// </summary>
public class ClientGMControl
{
	private SMap<string,Obj> _cmdDic=new SMap<string,Obj>();
	private SList<DescribeObj> _describeList=new SList<DescribeObj>();

	public virtual void init()
	{
		registOne(help,"帮助","help");
	}

	// /** 设置服务器指令组 */
	// public void setServerCmdSet(SSet<string> value)
	// {
	// 	if(value==null)
	// 		return;
	//
	// 	value.forEach(k=>
	// 	{
	// 		if(_cmdDic.contains(k) && !k.Equals("help"))
	// 		{
	// 			Ctrl.throwError("重复的gm指令(与服务器冲突):",k);
	// 		}
	// 	});
	// }

	private void addCmdObj(string cmd,Obj obj)
	{
		if(_cmdDic.contains(cmd))
		{
			Ctrl.throwError("重复的gm指令:"+cmd);
			return;
		}

		_cmdDic.put(cmd,obj);
	}

	private void toRegistOne(Obj obj,string describe,params string[] cmds)
	{
		foreach(String v in cmds)
		{
			addCmdObj(v,obj);
		}

		_describeList.add(new DescribeObj(describe,cmds));
	}

	/** 注册一个 */
	protected void registOne(Action<int[],string[]> func,string describe,params string[] cmds)
	{
		Obj obj=new Obj();
		obj.func=func;

		toRegistOne(obj,describe,cmds);
	}

	/** 注册一个 */
	protected void registOne(Action<int[]> func,string describe,params string[] cmds)
	{
		Obj obj=new Obj();
		obj.func2=func;

		toRegistOne(obj,describe,cmds);
	}

	public bool hasCmd(string cmd)
	{
		if(cmd.isEmpty())
		{
			return false;
		}

		string[] args=cmd.Split(' ');
		
		return _cmdDic.contains(args[0]);
	}

	/** 执行gm */
	public void execute(string cmd)
	{
		if(ShineSetting.isOfficial)
		{
			GameC.player.warnLog("正式服,依然有gm访问");
			return;
		}

		if(cmd.isEmpty())
		{
			return;
		}

		string[] args=cmd.Split(' ');

		Obj obj=_cmdDic.get(args[0]);

		if(obj==null)
		{
			//错误的指令
			GameC.info.showInfoCode(InfoCodeType.ClientGMFailed);
			return;
		}

		int len=args.Length - 1;
		int[] ints=new int[len];
		string[] strs=new string[len];

		for(int i=0;i<len;++i)
		{
			strs[i]=args[i + 1];

			try
			{
				ints[i]=int.Parse(args[i + 1]);
			}
			catch(Exception e)
			{

			}
		}

		if(obj.call(ints,strs))
		{
			GameC.info.showInfoCode(InfoCodeType.ClientGMSuccess);
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
		public Action<int[],string[]> func;
		public Action<int[]> func2;

		public bool call(int[] ints,String[] strs)
		{
			try
			{
				if(func!=null)
				{
					func(ints,strs);
					return true;
				}
				else if(func2!=null)
				{
					func2(ints);
					return true;
				}
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);

				//错误的指令
				GameC.info.showInfoCode(InfoCodeType.ClientGMFailed);
			}

			return false;
		}
	}

	//--方法组--//

	/** 帮助 */
	public void help(int[] ints)
	{
		StringBuilder sb=StringBuilderPool.create();
		DescribeObj dObj;
		for(int i=1,len=_describeList.length();i<len;++i)
		{
			if(i>1)
				sb.Append("\n");

			dObj=_describeList.get(i);

			for(int j=0;j<dObj.cmds.Length;++j)
			{
				if(j>0)
				{
					sb.Append(" / ");
				}

				sb.Append("<color=orange>");
				sb.Append(dObj.cmds[j]);
				sb.Append("</color>");
			}

			sb.Append(" : ");
			sb.Append(dObj.describe);
		}

		GameC.info.showInfoCode(InfoCodeType.ClientGMHelp,StringBuilderPool.releaseStr(sb));
	}
}