package com.home.commonManager.control;

import com.home.commonBase.config.game.ActivationCodeConfig;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.Global;
import com.home.commonBase.table.table.ActivationCodeTable;
import com.home.commonManager.global.ManagerC;
import com.home.shine.control.DateControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** 逻辑控制 */
public class ManagerLogicControl
{
	private static char[] _charArr;
	
	private StringBuilder _sb=new StringBuilder();
	
	/** 初始化 */
	public void init()
	{
		_charArr=new char[36];
		
		for(int i=0;i<10;i++)
		{
			_charArr[i]=(char)('0'+i);
		}
		
		for(int i=0;i<26;i++)
		{
			_charArr[i+10]=(char)('A'+i);//用大写
		}
	}
	
	/** 构造一条激活码 */
	protected String makeOneCode()
	{
		for(int i=0;i<Global.activationCodeLength;i++)
		{
			_sb.append(_charArr[MathUtils.randomInt(_charArr.length)]);
		}
		
		String str=_sb.toString();
		_sb.setLength(0);
		return str;
	}
	
	/** 生成激活码 */
	public void makeActivationCode(int id,int num,long disableTime,int useNum,int batchNum,ObjectCall<String[]> func)
	{
		if(num<=0)
		{
			Ctrl.errorLog("生成的激活码数目小于0");
			return;
		}
		
		ActivationCodeConfig config=ActivationCodeConfig.get(id);
		
		if(config==null)
		{
			Ctrl.errorLog("生成的激活码数目小于0");
			return;
		}
		
		ActivationCodeTable table=BaseC.factory.createActivationCodeTable();
		table.id=config.id;
		table.bornTime=DateControl.getTimeMillis();
		
		if(disableTime>0)
		{
			table.disableTime=disableTime;
		}
		else
		{
			table.disableTime=config.disableTimeT.getNextTime(table.bornTime);
		}
		
		if(useNum!=0)
		{
			table.lastNum=useNum;
		}
		else
		{
			table.lastNum=config.useNum;
		}
		
		ThreadControl.addMixedFunc(()->
		{
			//最多10次
			for(int i=0;i<10;i++)
			{
				String[] arr=makeActivationCodeOnce(table,num,batchNum);
				
				if(arr!=null)
				{
					StringBuilder sb=StringBuilderPool.create();
					
					sb.append("生成激活码 ");
					sb.append(id);
					sb.append(" ");
					sb.append(num);
					
					for(String a:arr)
					{
						sb.append(StringBuilderPool.Enter);
						sb.append(a);
					}
					
					Ctrl.log(StringBuilderPool.releaseStr(sb));
					
					func.apply(arr);
					return;
				}
			}
			
			func.apply(null);
		});
	}
	
	/** 生成激活码(辅助线程) */
	private String[] makeActivationCodeOnce(ActivationCodeTable table,int num,int batchNum)
	{
		if(batchNum<1)
			batchNum=1;
		
		if(num<1)
			num=1;
		
		if(!Global.activationCodeUseBatch)
		{
			batchNum=1;
		}
		
		String[] arr=new String[num*batchNum];
		
		Connection con=ManagerC.db.getConnect().getMainConnection();
		
		boolean hasError=false;
		
		try
		{
			PreparedStatement ps=con.prepareStatement(table.getInsertStr());
			
			String code;
			
			for(int i=0;i<num;i++)
			{
				code=makeOneCode();
				
				for(int j=0;j<batchNum;j++)
				{
					if(Global.activationCodeUseBatch)
					{
						arr[i*batchNum+j]=table.code=(getBatchHead(j)+code);
					}
					else
					{
						arr[i*batchNum+j]=table.code=code;
					}
					
					table.insertToPs(ps);
					ps.addBatch();
				}
			}
			
			ps.executeBatch();
		}
		catch(SQLException e)
		{
			hasError=true;
			Ctrl.errorLog(e);
		}
		
		try
		{
			if(hasError)
			{
				con.rollback();
			}
			else
			{
				con.commit();
			}
		}
		catch(Exception e)
		{
			Ctrl.errorLog("DB commit/rollback执行出错",e);
		}
		
		ManagerC.db.getConnect().reMainConnection(con);
		
		if(hasError)
			arr=null;
		
		return arr;
	}
	
	private String getBatchHead(int num)
	{
		String re=String.valueOf(num);
		
		if(re.length()<2)
			re='0'+re;
		
		return re;
	}
}
