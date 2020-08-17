using ShineEngine;

/// <summary>
/// 服务连接基类
/// </summary>
public class BaseServer
{
	private bool _inited=false;
	/** 连接 */
	private BaseSocket _socket;

	/** 数据构造器 */
	private DataMaker _maker=new DataMaker();

	/** 消息绑定 */
	protected MessageBindTool _messageBind=new MessageBindTool();

	public BaseServer()
	{
		_inited=true;

		_socket=new BaseSocket();
		_socket.setServer(this);
		_socket.setConnectCall(onConnect);
		_socket.setConnectFailedCall(onConnectFailed);
		_socket.setCloseCall(onClose);
		_socket.setCreateResponseFunc(createResponse);

		NetControl.addSocket(_socket);
	}

	public virtual void init()
	{
		TimeDriver.instance.setFrame(onFrame);

		initMessage();

		_socket.init();
	}

	public virtual void initMessage()
	{
		addRequestMaker(new ShineRequestMaker());
		addResponseMaker(new ShineResponseMaker());
	}

	/// <summary>
	/// 析构(就是close+clear)
	/// </summary>
	public virtual void dispose()
	{
		_inited=false;
		_socket.closeAndClear();
	}

	public bool inited()
	{
		return _inited;
	}

	protected virtual void onFrame(int delay)
	{
		_socket.onFrame(delay);
	}

	/// <summary>
	/// 关闭连接
	/// </summary>
	public void close()
	{
		_socket.close();
	}

	/// <summary>
	/// 获取连接
	/// </summary>
	public BaseSocket getSocket()
	{
		return _socket;
	}

	/** 连接 */
	public void connect(string host,int port)
	{
		_socket.connect(host,port);
	}

	// /** 尝试连接 */
	// public void tryConnect(string host,int port)
	// {
	// 	_socket.tryConnect(host,port);
	// }

	/** 创建响应对象 */
	public BaseResponse createResponse(int mid)
	{
		if(ShineSetting.messageUsePool)
		{
			if(!_maker.contains(mid))
				return null;

			return BytesControl.createResponse(mid);
		}
		else
		{
			return (BaseResponse)_maker.getDataByID(mid);
		}
	}

	/** 添加发送消息构造 */
	public void addRequestMaker(DataMaker maker)
	{
		BytesControl.addRequestMaker(maker);
	}

	/** 添加构造器 */
	public void addResponseMaker(DataMaker maker)
	{
		BytesControl.addDataMaker(maker);
		_maker.addDic(maker);
	}

	/** 添加客客户端发送消息绑定 */
	public void addClientRequestBind(MessageBindTool tool)
	{
		_messageBind.addDic(tool);
	}

	protected virtual void onConnect()
	{
	}

	protected virtual void onConnectFailed()
	{
	}

	protected virtual void onClose()
	{

	}

	/** 消息是否忽略统计 */
	public virtual bool isClientMessageIgnore(int mid)
	{
		return false;
	}

	/** 检查消息绑定(返回是否可发送) */
	public virtual bool checkRequestBind(int mid)
	{
		return true;
	}

	/** 检查消息的解绑 */
	public virtual void checkResponseUnbind(int mid)
	{

	}
}