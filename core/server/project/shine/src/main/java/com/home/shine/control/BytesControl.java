package com.home.shine.control;

import com.home.shine.constlist.generate.ShineRequestType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseResponse;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.thread.AbstractThread;
import com.home.shine.tool.CreateDataFunc;
import com.home.shine.tool.DataMaker;
import com.home.shine.tool.generate.ShineDataMaker;

import java.lang.reflect.Field;

/** 字节控制(包括Data序列化/反序列化)(compress部分没封装完) */
public class BytesControl
{
	/** 数据Maker(包括Response) */
	private static DataMaker _dataMaker=new DataMaker();
	/** 发送消息(只Request) */
	private static DataMaker _requestMaker=new DataMaker();
	/** 发送名字组 */
	private static IntObjectMap<String> _requestNames=new IntObjectMap<>();
	/** 接收名字组 */
	private static IntObjectMap<String> _responseNames=new IntObjectMap<>();
	/** 协议忽略mid */
	private static IntSet _messageIgnoreSet=new IntSet();
	
	/** 添加数据构造器 */
	public static void addDataMaker(DataMaker maker)
	{
		_dataMaker.addDic(maker);
	}
	
	/** 添加消息maker */
	public static void addRequestMaker(DataMaker maker)
	{
		_requestMaker.addDic(maker);
	}
	
	/** 初始化 */
	public static void init()
	{
		addDataMaker(new ShineDataMaker());
		
		//添加shine消息
		for(int i=ShineRequestType.off;i<ShineRequestType.count;i++)
		{
			addIgnoreMessage(i);
		}
	}
	
	/** 添加消息名字注册 */
	public static void addMessageConst(Class<?> cls,boolean isRequest,boolean isServer)
	{
		Field[] fields=cls.getFields();
		
		try
		{
			String name;
			
			for(Field v:fields)
			{
				name=v.getName();
				
				//排除
				if(!name.equals("off") && !name.equals("count"))
				{
					int id=v.getInt(cls);
					
					if(isRequest)
					{
						_requestNames.put(id,isServer ? name+"ServerRequest" : name+"Request");
					}
					else
					{
						_responseNames.put(id,isServer ? name+"ServerResponse" : name+"Response");
					}
				}
			}
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
	}
	
	//---Data---//
	
	/** 通过id取得Data类型 */
	public static BaseData getDataByID(int dataID)
	{
		if(dataID==-1)
		{
			return null;
		}
		
		BaseData re=_dataMaker.getDataByID(dataID);
		
		if(re==null)
		{
			Ctrl.throwError("找不到Data类型" + dataID);
			return null;
		}
		
		return re;
	}
	
	/** 通过id取得Request类型 */
	public static BaseData getRequestByID(int dataID)
	{
		if(dataID==-1)
		{
			return null;
		}
		
		BaseData re=_requestMaker.getDataByID(dataID);
		
		if(re==null)
		{
			Ctrl.throwError("找不到Request类型" + dataID);
			return null;
		}
		
		return re;
	}
	
	///** 通过其他数据创建数据(只创建) */
	//public static BaseData createData(BaseData data)
	//{
	//	return getDataByID(data.getDataID());
	//}
	
	///** 获取某数据ID的名字 */
	//public static String getDataName(int dataID)
	//{
	//	return _dataNames[dataID];
	//}
	
	/** 数组拷贝 */
	public static byte[] byteArrCopy(byte[] src)
	{
		byte[] re=new byte[src.length];
		System.arraycopy(src,0,re,0,src.length);
		return re;
	}
	
	/** 数组拷贝(从src拷贝到des) */
	public static void arrayCopy(Object src,Object des,int length)
	{
		System.arraycopy(src,0,des,0,length);
	}
	
	/** 获取request名字 */
	public static String getRequestName(int mid)
	{
		return _requestNames.get(mid);
	}
	
	/** 获取response名字 */
	public static String getResponseName(int mid)
	{
		return _responseNames.get(mid);
	}
	
	/** 添加要被忽略的消息mid */
	public static void addIgnoreMessage(int mid)
	{
		_messageIgnoreSet.add(mid);
	}
	
	/** 是否shine消息 */
	public static boolean isIgnoreMessage(int mid)
	{
		return _messageIgnoreSet.contains(mid);
	}
	
	/** 创建data */
	public static BaseData createData(int dataID)
	{
		AbstractThread thread;
		if(ShineSetting.messageUsePool && (thread=ThreadControl.getCurrentShineThread())!=null)
		{
			BaseData data=thread.pool.createData(dataID);
			//data.createThreadInstance=thread.instanceIndex;
			return data;
		}
		else
		{
			return getDataByID(dataID);
		}
	}
	
	/** 创建Request消息 */
	public static BaseData createRequest(int dataID)
	{
		AbstractThread thread;
		if(ShineSetting.messageUsePool && (thread=ThreadControl.getCurrentShineThread())!=null)
		{
			BaseData request=thread.pool.createRequest(dataID);
			//request.createThreadInstance=thread.instanceIndex;
			return request;
		}
		else
		{
			return getRequestByID(dataID);
		}
	}
	
	/** 创建Request消息 */
	public static BaseResponse createResponse(int dataID)
	{
		AbstractThread thread;
		if(ShineSetting.messageUsePool && (thread=ThreadControl.getCurrentShineThread())!=null)
		{
			BaseResponse response=(BaseResponse)thread.pool.createData(dataID);
			response.createThreadInstance=thread.instanceIndex;
			return response;
		}
		else
		{
			return (BaseResponse)getDataByID(dataID);
		}
	}
	
	/** 预备回收response */
	public static void preReleaseResponse(BaseResponse response)
	{
		if(!response.needRelease())
			return;
		
		AbstractThread thread;
		if(ShineSetting.messageUsePool && (thread=ThreadControl.getCurrentShineThread())!=null)
		{
			//析构
			response.dispose();
			thread.dataCache.cacheData(response);
		}
	}
}
