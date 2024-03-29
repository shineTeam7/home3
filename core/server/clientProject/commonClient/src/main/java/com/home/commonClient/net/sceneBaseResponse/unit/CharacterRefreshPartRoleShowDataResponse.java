package com.home.commonClient.net.sceneBaseResponse.unit;
import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.scene.base.Unit;
import com.home.commonClient.constlist.generate.GameResponseType;
import com.home.commonClient.constlist.generate.SceneBaseResponseType;
import com.home.commonClient.net.sceneBaseResponse.base.UnitSResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.data.BaseData;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 角色更新部分角色外显数据消息(generated by shine) */
public class CharacterRefreshPartRoleShowDataResponse extends UnitSResponse
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseResponseType.CharacterRefreshPartRoleShowData;
	
	/** 改变数据 */
	public RoleShowChangeData data;
	
	public CharacterRefreshPartRoleShowDataResponse()
	{
		_dataID=SceneBaseResponseType.CharacterRefreshPartRoleShowData;
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		Unit unit=scene.getFightUnitAbs(instanceID);
		
		//更新数据
		unit.getUnitData().getCharacterIdentity().roleShowData.onChange(data);
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "CharacterRefreshPartRoleShowDataResponse";
	}
	
	/** 读取字节流(完整版) */
	@Override
	protected void toReadBytesFull(BytesReadStream stream)
	{
		super.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		BaseData dataT=stream.readDataFullNotNull();
		if(dataT!=null)
		{
			if(dataT instanceof RoleShowChangeData)
			{
				this.data=(RoleShowChangeData)dataT;
			}
			else
			{
				this.data=new RoleShowChangeData();
				if(!(dataT.getClass().isAssignableFrom(RoleShowChangeData.class)))
				{
					stream.throwTypeReadError(RoleShowChangeData.class,dataT.getClass());
				}
				this.data.shadowCopy(dataT);
			}
		}
		else
		{
			this.data=null;
		}
		
		stream.endReadObj();
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		this.data=(RoleShowChangeData)stream.readDataSimpleNotNull();
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
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
		
		this.data=null;
	}
	
}
