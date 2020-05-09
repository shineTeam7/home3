package com.home.commonCenter.control;

import com.home.commonBase.constlist.system.CVersionEnum;
import com.home.commonBase.control.BaseVersionControl;
import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.data.system.SaveVersionData;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.part.centerGlobal.CenterGlobal;
import com.home.shine.support.collection.LongObjectMap;

/** 中心服版本控制 */
public class CenterVersionControl extends BaseVersionControl<CenterGlobal>
{
	public void init()
	{
		registCFunc(CVersionEnum.Version2,this::changeVersion2);
	}
	
	@Override
	protected SaveVersionData getVersionData(CenterGlobal me)
	{
		return me.system.getPartData().version;
	}
	
	private void changeVersion2(CenterGlobal me)
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
					CenterC.db.addNewRoleSocial(v);
				}
			}
		}
		
		me.social.getPartData().roleSocialDataDic=new LongObjectMap<>(RoleSocialData[]::new);
		me.social.setRoleSocialDic(dic);
	}
}
