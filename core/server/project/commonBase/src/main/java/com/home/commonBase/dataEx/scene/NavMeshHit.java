package com.home.commonBase.dataEx.scene;

import com.home.commonBase.data.scene.base.PosData;
import com.home.shine.dataEx.ExternBuf;

/** navmesh hit结果 */
public class NavMeshHit
{
	public PosData position=new PosData();
	public PosData normal=new PosData();
	public float distance=0f;
	public int mask=0;
	public int hit=0;
	
	public void readExternBuf(ExternBuf buf)
	{
		position.readExternBuf(buf);
		normal.readExternBuf(buf);
		distance=buf.readFloat();
		mask=buf.readInt();
		hit=buf.readInt();
	}
}
