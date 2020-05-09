package com.home.commonGame.control;

import com.home.commonBase.constlist.system.CVersionEnum;
import com.home.commonBase.control.BaseVersionControl;
import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.data.system.SaveVersionData;
import com.home.commonGame.part.gameGlobal.GameGlobal;
import com.home.commonGame.global.GameC;
import com.home.shine.support.collection.LongObjectMap;

public class GameVersionControl extends BaseVersionControl<GameGlobal>
{
	/** 角色版本控制 */
	public PlayerVersionControl playerVersion;
	
	public void init()
	{
		(playerVersion=GameC.factory.createPlayerVersionControl()).init();
		
		registCFunc(CVersionEnum.Version2,this::changeVersion2);
	}
	
	@Override
	protected SaveVersionData getVersionData(GameGlobal me)
	{
		return me.system.getPartData().version;
	}
	
	private void changeVersion2(GameGlobal me)
	{
		LongObjectMap<RoleSocialData> dic=me.social.getPartData().roleSocialDataDic;
		
		LongObjectMap<RoleSocialData> fDic;
		if(!(fDic=dic).isEmpty())
		{
			RoleSocialData[] values;
			RoleSocialData v;
			
			for(int i=(values=fDic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					GameC.db.addNewRoleSocial(v);
				}
			}
		}
		
		me.social.getPartData().roleSocialDataDic=new LongObjectMap<>(RoleSocialData[]::new);
		me.social.setRoleSocialDic(dic);
	}
}
