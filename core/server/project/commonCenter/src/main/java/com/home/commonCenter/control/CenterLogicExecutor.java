package com.home.commonCenter.control;

import com.home.commonBase.config.game.ActivationCodeConfig;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.control.AbstractLogicExecutor;
import com.home.commonBase.data.activity.UseActivationCodeSuccessOWData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.table.table.ActivationCodeTable;
import com.home.commonCenter.global.CenterC;
import com.home.shine.control.DateControl;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.tool.TableOperateTool;

/** 中心服逻辑执行器 */
public class CenterLogicExecutor extends AbstractLogicExecutor
{
	/** 激活码工具 */
	private TableOperateTool<String,ActivationCodeTable> _activationTableTool;
	
	public CenterLogicExecutor(int index)
	{
		super(index);
	}
	
	/** 初始化(池线程) */
	public void init()
	{
		super.init();
		
		initTick();
		
		_activationTableTool=new TableOperateTool<String,ActivationCodeTable>(CenterC.db.getConnect())
		{
			@Override
			protected ActivationCodeTable makeTable(String key)
			{
				ActivationCodeTable table=BaseC.factory.createActivationCodeTable();
				table.code=key;
				return table;
			}
		};
	}
	
	/** 添加自身带参回调 */
	public void addSelfFunc(ObjectCall<CenterLogicExecutor> func)
	{
		addFunc(()->
		{
			func.apply(this);
		});
	}
	
	/** 使用激活码 */
	public void useActivationCode(long playerID,String code)
	{
		_activationTableTool.load(code,table->
		{
			//没有表
			if(table==null)
			{
				CenterC.main.sendInfoCodeToPlayer(playerID,InfoCodeType.UseActivationCodeFailed_notExist);
				return;
			}
			
			ActivationCodeConfig config=ActivationCodeConfig.get(table.id);
			
			if(config==null)
			{
				CenterC.main.sendInfoCodeToPlayer(playerID,InfoCodeType.UseActivationCodeFailed_notExist);
				return;
			}
			
			if(table.disableTime>0 && DateControl.getTimeMillis()>table.disableTime)
			{
				//失效时间已过
				CenterC.main.sendInfoCodeToPlayer(playerID,InfoCodeType.UseActivationCodeFailed_timeOut);
				return;
			}
			
			//达到上限次数
			if(table.lastNum!=-1 && table.lastNum==0)
			{
				CenterC.main.sendInfoCodeToPlayer(playerID,InfoCodeType.UseActivationCodeFailed_numIsMax);
				return;
			}
			
			if(table.lastNum!=-1)
			{
				--table.lastNum;
			}
			
			UseActivationCodeSuccessOWData wData=new UseActivationCodeSuccessOWData();
			wData.id=table.id;
			wData.code=code;
			
			CenterC.main.addPlayerOfflineWork(playerID,wData);
		});
	}
}
