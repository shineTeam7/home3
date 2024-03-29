package com.home.commonGame.net.serverResponse.game.social;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.serverResponse.game.base.GameToGameServerResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 更新角色显示数据到源服(generated by shine) */
public class RefreshRoleShowToSourceGameServerResponse extends GameToGameServerResponse
{
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.RefreshRoleShowToSourceGame;
	
	/** 角色ID */
	public long playerID;
	
	/** 改变数据 */
	public RoleShowChangeData data;
	
	public RefreshRoleShowToSourceGameServerResponse()
	{
		_dataID=ServerMessageType.RefreshRoleShowToSourceGame;
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		this.playerID=stream.readLong();
		
		this.data=(RoleShowChangeData)stream.readDataSimpleNotNull();
		
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		GameC.global.social.refreshRoleSocialData(playerID,data);
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "RefreshRoleShowToSourceGameServerResponse";
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("playerID");
		writer.sb.append(':');
		writer.sb.append(this.playerID);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("data");
		writer.sb.append(':');
		if(this.data!=null)
		{
			this.data.writeDataString(writer);
		}
		else
		{
			writer.sb.append("RoleShowChangeData=null");
		}
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.playerID=0L;
		this.data=null;
	}
	
}
