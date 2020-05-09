package com.home.commonLogin.test;

import com.home.commonBase.app.App;
import com.home.commonBase.global.AppSetting;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.table.table.UserTable;
import com.home.commonLogin.app.LoginApp;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.table.DBConnect;
import com.home.shine.utils.MathUtils;

public class TestUserTable
{
	private static DBConnect[] _connects=new DBConnect[10];
	
	private static int _index;
	
	private static int _completeNum;
	
	public static void execute()
	{
		App.test(()->
		{
			for(int i=0;i<_connects.length;i++)
			{
				_connects[i]=new DBConnect(ServerConfig.getLoginConfig(1).mysql);
			}
			
			ThreadControl.getMainTimeDriver().setInterval(TestUserTable::once,100);
		});
	}
	
	private static void once(int delay)
	{
		_index++;
		
		Ctrl.print("完成数",_completeNum);
		
		for(int i=0;i<100;i++)
		{
			doOne(_index*1000+i,false);
		}
		
		int aa=MathUtils.randomInt(_index);
		
		for(int i=0;i<100;i++)
		{
			doOne(aa*1000+i,true);
		}
	}
	
	private static void doOne(int key,boolean onlySelect)
	{
		DBConnect cc=_connects[MathUtils.randomInt(10)];
		UserTable ut=BaseC.factory.createUserTable();
		ut.puid="youke_"+key;
		
		ObjectCall<Boolean> func=k->
		{
			if(k)
			{
				++_completeNum;
				
				////自动绑定的区服ID
				//int useAreaID=CommonSetting.areaDivideType==GameAreaDivideType.AutoBindGame ? ut.areaID : areaID;
				//
				////userID
				//eData.userID=ut.userID;
				//eData.data.areaID=useAreaID;
				//
				//LoginC.server.getCenterSocket().send(UserLoginToCenterServerRequest.create(eData));
			}
			else
			{
				Ctrl.throwError("创建账号失败");
			}
		};
		
		//puid查询
		ut.load2(cc,b->
		{
			if(onlySelect)
				return;
			
			//有数据
			if(b)
			{
				ut.isAdult=true;
				//保存一下(不等返回)
				ut.update(cc,null);
				
				func.apply(true);
			}
			else
			{
				ut.platform="youke";
				ut.isAdult=true;
				
				//插入库
				ut.insert(cc,b2->
				{
					if(b2)
					{
						//再读取
						ut.load2(cc,func);
					}
					else
					{
						Ctrl.throwError("插入账号失败");
					}
				});
			}
		});
	}
}
