package com.home.commonGame.net.request.func.rank.subsection;
import com.home.commonGame.constlist.generate.GameRequestType;
import com.home.commonGame.net.request.func.base.FuncSRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 推送获取分段翻页显示内容(generated by shine) */
public class FuncRefreshSubsectionIndexRequest extends FuncSRequest
{
	/** 数据类型ID */
	public static final int dataID=GameRequestType.FuncRefreshSubsectionIndex;
	
	/** 大组index */
	public int subsectionIndex;
	
	/** 小组index */
	public int subsectionSubIndex;
	
	public FuncRefreshSubsectionIndexRequest()
	{
		_dataID=GameRequestType.FuncRefreshSubsectionIndex;
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
		return "FuncRefreshSubsectionIndexRequest";
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.writeInt(this.subsectionIndex);
		
		stream.writeInt(this.subsectionSubIndex);
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		stream.writeInt(this.subsectionIndex);
		
		stream.writeInt(this.subsectionSubIndex);
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("subsectionIndex");
		writer.sb.append(':');
		writer.sb.append(this.subsectionIndex);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("subsectionSubIndex");
		writer.sb.append(':');
		writer.sb.append(this.subsectionSubIndex);
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.subsectionIndex=0;
		this.subsectionSubIndex=0;
	}
	
	/** 创建实例 */
	public static FuncRefreshSubsectionIndexRequest create(int funcID,int subsectionIndex,int subsectionSubIndex)
	{
		FuncRefreshSubsectionIndexRequest re=(FuncRefreshSubsectionIndexRequest)BytesControl.createRequest(dataID);
		re.funcID=funcID;
		re.subsectionIndex=subsectionIndex;
		re.subsectionSubIndex=subsectionSubIndex;
		return re;
	}
	
}
