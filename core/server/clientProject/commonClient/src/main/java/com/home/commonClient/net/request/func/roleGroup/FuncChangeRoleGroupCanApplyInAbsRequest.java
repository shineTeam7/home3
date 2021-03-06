package com.home.commonClient.net.request.func.roleGroup;
import com.home.commonClient.constlist.generate.GameRequestType;
import com.home.commonClient.net.request.func.base.FuncPlayerRoleGroupRRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 更改申请时是否可直接入群(generated by shine) */
public class FuncChangeRoleGroupCanApplyInAbsRequest extends FuncPlayerRoleGroupRRequest
{
	/** 数据类型ID */
	public static final int dataID=GameRequestType.FuncChangeRoleGroupCanApplyInAbs;
	
	/** 申请时是否可直接入群(无需同意) */
	public boolean canApplyInAbs;
	
	public FuncChangeRoleGroupCanApplyInAbsRequest()
	{
		_dataID=GameRequestType.FuncChangeRoleGroupCanApplyInAbs;
	}
	
	@Override
	protected void copyData()
	{
		super.copyData();
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "FuncChangeRoleGroupCanApplyInAbsRequest";
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.writeBoolean(this.canApplyInAbs);
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		stream.writeBoolean(this.canApplyInAbs);
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("canApplyInAbs");
		writer.sb.append(':');
		writer.sb.append(this.canApplyInAbs);
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.canApplyInAbs=false;
	}
	
	/** 创建实例 */
	public static FuncChangeRoleGroupCanApplyInAbsRequest create(int funcID,long groupID,boolean canApplyInAbs)
	{
		FuncChangeRoleGroupCanApplyInAbsRequest re=(FuncChangeRoleGroupCanApplyInAbsRequest)BytesControl.createRequest(dataID);
		re.funcID=funcID;
		re.groupID=groupID;
		re.canApplyInAbs=canApplyInAbs;
		return re;
	}
	
}
