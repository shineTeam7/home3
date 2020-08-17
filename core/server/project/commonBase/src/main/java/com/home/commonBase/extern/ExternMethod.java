package com.home.commonBase.extern;

import com.home.commonBase.control.LogicExecutorBase;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.dataEx.scene.NavMeshHit;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.ExternBuf;
import com.home.shine.global.ShineGlobal;
import com.home.shine.support.collection.SList;

/** 外部方法 */
public class ExternMethod
{
	private static boolean _inited=false;
	
	/** 初始化 */
	public static synchronized void init()
	{
		if(_inited)
			return;
		
		_inited=true;
		
		ShineGlobal.loadLib();
		ExternMethodNative.init(ShineGlobal.binPath);
	}
	
	public static boolean samplePosition(long scenePtr,ExternBuf buf,PosData pos,NavMeshHit hit,float maxDistance,int areaMask)
	{
		buf.clear();
		pos.writeExternBuf(buf);
		
		boolean re=ExternMethodNative.samplePosition(scenePtr,buf.fArr,buf.iArr,maxDistance,areaMask);
		
		if(re)
		{
			buf.clear();
			hit.readExternBuf(buf);
		}
		
		return re;
	}
	
	public static boolean raycast(long scenePtr,ExternBuf buf,PosData from,PosData target,NavMeshHit hit,int areaMask)
	{
		buf.clear();
		from.writeExternBuf(buf);
		target.writeExternBuf(buf);
		
		boolean re=ExternMethodNative.raycast(scenePtr,buf.fArr,buf.iArr,areaMask);
		
		if(re)
		{
			buf.clear();
			hit.readExternBuf(buf);
		}
		
		return re;
	}
	
	public static boolean calculatePath(long scenePtr,ExternBuf buf,PosData from,PosData target,SList<PosData> list,int areaMask)
	{
		buf.clear();
		from.writeExternBuf(buf);
		target.writeExternBuf(buf);
		
		//C++层已经弄掉起始点
		boolean re=ExternMethodNative.calculatePath(scenePtr,buf.fArr,buf.iArr,areaMask);
		
		list.clear();
		
		if(re)
		{
			buf.clear();
			
			int len=buf.readInt();
			
			if(len>0)
			{
				for(int i=0;i<len;i++)
				{
					PosData pos=new PosData();
					pos.readExternBuf(buf);
					//区域
					int area=buf.readInt();
					
					//TODO:对于area的使用
					
					list.add(pos);
				}
			}
		}
		
		return re;
	}
	
	public static long addCylinderObstacle(long scenePtr,ExternBuf buf,PosData pos,float radius,float height)
	{
		buf.clear();
		pos.writeExternBuf(buf);
		buf.writeFloat(radius);
		buf.writeFloat(height);
		
		return ExternMethodNative.addCylinderObstacle(scenePtr,buf.fArr);
	}
}
