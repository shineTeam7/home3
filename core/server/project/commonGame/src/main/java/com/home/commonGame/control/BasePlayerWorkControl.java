package com.home.commonGame.control;

import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.data.system.PlayerToPlayerTCCWData;
import com.home.commonBase.data.social.roleGroup.work.PlayerToRoleGroupTCCWData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonGame.part.player.Player;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.func.ObjectCall2;
import com.home.shine.support.func.ObjectCall3;
import com.home.shine.support.func.ObjectFunc3;

public class BasePlayerWorkControl
{
	protected IntObjectMap<ObjectCall2<Player,PlayerWorkData>> _dic=new IntObjectMap<>();
	
	protected IntObjectMap<ObjectCall3<Player,PlayerToRoleGroupTCCWData,Integer>> _roleGroupTCCResult=new IntObjectMap<>();
	
	protected IntObjectMap<ObjectFunc3<Integer,Player,PlayerToPlayerTCCWData>> _playerTCCDic=new IntObjectMap<>();
	
	protected IntObjectMap<ObjectCall3<Player,PlayerToPlayerTCCWData,Integer>> _playerTCCResult=new IntObjectMap<>();
	
	public void init()
	{
		regist();
		registRoleGroupTCCResult();
		registPlayerTCC();
		registPlayerTCCResult();
	}
	
	/** 注册事务 */
	protected void regist()
	{
	
	}
	
	/** 注册玩家群事务返回结果 */
	protected void registRoleGroupTCCResult()
	{
	
	}
	
	/** 注册玩家群事务返回结果 */
	protected void registPlayerTCC()
	{
	
	}
	
	/** 注册玩家群事务返回结果 */
	protected void registPlayerTCCResult()
	{
	
	}
	
	/** 注册一个 */
	protected void registOne(int dataID,ObjectCall2<Player,PlayerWorkData> func)
	{
		_dic.put(dataID,func);
	}
	
	/** 注册一个 */
	protected void registOneRoleGroupTCCResult(int dataID,ObjectCall3<Player,PlayerToRoleGroupTCCWData,Integer> func)
	{
		_roleGroupTCCResult.put(dataID,func);
	}
	
	/** 注册一个 */
	protected void registOnePlayerTCC(int dataID,ObjectFunc3<Integer,Player,PlayerToPlayerTCCWData> func)
	{
		_playerTCCDic.put(dataID,func);
	}
	
	/** 注册一个 */
	protected void registOnePlayerTCCResult(int dataID,ObjectCall3<Player,PlayerToPlayerTCCWData,Integer> func)
	{
		_playerTCCResult.put(dataID,func);
	}
	
	/** 执行一个离线事务数据 */
	public void execute(Player player,PlayerWorkData data)
	{
		ObjectCall2<Player,PlayerWorkData> func=_dic.get(data.getDataID());
		
		if(func==null)
		{
			Ctrl.throwError("未找到类型为：" + data.getDataID() + "的离线事务处理");
		}
		else
		{
			try
			{
				func.apply(player,data);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
	}
	
	/** 执行玩家群tcc事务结果 */
	public void executeRoleGroupTCCResult(Player player,PlayerToRoleGroupTCCWData data,Integer result)
	{
		ObjectCall3<Player,PlayerToRoleGroupTCCWData,Integer> func=_roleGroupTCCResult.get(data.getDataID());
		
		if(func==null)
		{
			Ctrl.throwError("未找到类型为：" + data.getDataID() + "的TCC事务结果处理");
		}
		else
		{
			try
			{
				func.apply(player,data,result);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
	}
	
	/** 执行玩家cc事务结果 */
	public int executePlayerTCC(Player player,PlayerToPlayerTCCWData data)
	{
		ObjectFunc3<Integer,Player,PlayerToPlayerTCCWData> func=_playerTCCDic.get(data.getDataID());
		
		if(func==null)
		{
			Ctrl.throwError("未找到类型为：" + data.getDataID() + "的TCC事务结果处理");
			return InfoCodeType.WorkError;
		}
		else
		{
			int re=InfoCodeType.WorkError;
			
			try
			{
				re=func.apply(player,data);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			return re;
		}
	}
	
	/** 执行玩家群tcc事务结果 */
	public void executePlayerTCCResult(Player player,PlayerToPlayerTCCWData data,int result)
	{
		ObjectCall3<Player,PlayerToPlayerTCCWData,Integer> func=_playerTCCResult.get(data.getDataID());
		
		if(func==null)
		{
			Ctrl.throwError("未找到类型为：" + data.getDataID() + "的TCC事务结果处理");
		}
		else
		{
			try
			{
				func.apply(player,data,result);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
	}
}
