package com.home.commonBase.sdk.xinge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XingeApp
{
	public static final String RESTAPI_PUSHSINGLEDEVICE="http://openapi.xg.qq.com/v2/push/single_device";
	public static final String RESTAPI_PUSHSINGLEACCOUNT="http://openapi.xg.qq.com/v2/push/single_account";
	public static final String RESTAPI_PUSHACCOUNTLIST="http://openapi.xg.qq.com/v2/push/account_list";
	public static final String RESTAPI_PUSHALLDEVICE="http://openapi.xg.qq.com/v2/push/all_device";
	public static final String RESTAPI_PUSHTAGS="http://openapi.xg.qq.com/v2/push/tags_device";
	public static final String RESTAPI_QUERYPUSHSTATUS="http://openapi.xg.qq.com/v2/push/get_msg_status";
	public static final String RESTAPI_QUERYDEVICECOUNT="http://openapi.xg.qq.com/v2/application/get_app_device_num";
	public static final String RESTAPI_QUERYTAGS="http://openapi.xg.qq.com/v2/tags/query_app_tags";
	public static final String RESTAPI_CANCELTIMINGPUSH="http://openapi.xg.qq.com/v2/push/cancel_timing_task";
	public static final String RESTAPI_BATCHSETTAG="http://openapi.xg.qq.com/v2/tags/batch_set";
	public static final String RESTAPI_BATCHDELTAG="http://openapi.xg.qq.com/v2/tags/batch_del";
	public static final String RESTAPI_QUERYTOKENTAGS="http://openapi.xg.qq.com/v2/tags/query_token_tags";
	public static final String RESTAPI_QUERYTAGTOKENNUM="http://openapi.xg.qq.com/v2/tags/query_tag_token_num";
	public static final String RESTAPI_CREATEMULTIPUSH="http://openapi.xg.qq.com/v2/push/create_multipush";
	public static final String RESTAPI_PUSHACCOUNTLISTMULTIPLE="http://openapi.xg.qq.com/v2/push/account_list_multiple";
	public static final String RESTAPI_PUSHDEVICELISTMULTIPLE="http://openapi.xg.qq.com/v2/push/device_list_multiple";
	public static final String RESTAPI_QUERYINFOOFTOKEN="http://openapi.xg.qq.com/v2/application/get_app_token_info";
	public static final String RESTAPI_QUERYTOKENSOFACCOUNT="http://openapi.xg.qq.com/v2/application/get_app_account_tokens";
	public static final String RESTAPI_DELETETOKENOFACCOUNT="http://openapi.xg.qq.com/v2/application/del_app_account_tokens";
	public static final String RESTAPI_DELETEALLTOKENSOFACCOUNT="http://openapi.xg.qq.com/v2/application/del_app_account_all_tokens";
	public static final String HTTP_POST="POST";
	public static final String HTTP_GET="GET";
	public static final int DEVICE_ALL=0;
	public static final int DEVICE_BROWSER=1;
	public static final int DEVICE_PC=2;
	public static final int DEVICE_ANDROID=3;
	public static final int DEVICE_IOS=4;
	public static final int DEVICE_WINPHONE=5;
	public static final int IOSENV_PROD=1;
	public static final int IOSENV_DEV=2;
	public static final long IOS_MIN_ID=2200000000L;
	private long m_accessId;
	private String m_secretKey;
	
	public static void main(String[] args)
	{
		System.out.println("Hello Xinge!");
	}
	
	public XingeApp(long accessId,String secretKey)
	{
		this.m_accessId=accessId;
		this.m_secretKey=secretKey;
	}
	
	protected String generateSign(String method,String url,Map<String,Object> params)
	{
		List<Map.Entry<String,Object>> paramList=new ArrayList(params.entrySet());
		paramList.sort(Comparator.comparing(Map.Entry::getKey));
		String paramStr="";
		String md5Str="";
		for(Map.Entry entry : paramList)
		{
			paramStr=paramStr + (String)entry.getKey() + "=" + entry.getValue().toString();
		}
		try
		{
			URL u=new URL(url);
			MessageDigest md5=MessageDigest.getInstance("MD5");
			String s=method + u.getHost() + u.getPath() + paramStr + this.m_secretKey;
			byte[] bArr=s.getBytes("UTF-8");
			byte[] md5Value=md5.digest(bArr);
			BigInteger bigInt=new BigInteger(1,md5Value);
			md5Str=bigInt.toString(16);
			while(md5Str.length()<32)
			{
				md5Str="0" + md5Str;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
		return md5Str;
	}
	
	protected JSONObject callRestful(String url,Map<String,Object> params)
	{
		String ret="";
		JSONObject jsonRet=null;
		String sign=generateSign("POST",url,params);
		if(sign.isEmpty())
		{
			return new JSONObject("{\"ret_code\":-1,\"err_msg\":\"generateSign error\"}");
		}
		params.put("sign",sign);
		try
		{
			URL u=new URL(url);
			HttpURLConnection conn=(HttpURLConnection)u.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(3000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			StringBuffer param=new StringBuffer();
			for(String key : params.keySet())
			{
				param.append(key).append("=").append(URLEncoder.encode(params.get(key).toString(),"UTF-8")).append("&");
			}
			conn.getOutputStream().write(param.toString().getBytes("UTF-8"));
			
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
			InputStreamReader isr=new InputStreamReader(conn.getInputStream());
			BufferedReader br=new BufferedReader(isr);
			String temp;
			while((temp=br.readLine())!=null)
			{
				ret=ret + temp;
			}
			br.close();
			isr.close();
			conn.disconnect();
			
			jsonRet=new JSONObject(ret);
		}
		catch(SocketTimeoutException e)
		{
			jsonRet=new JSONObject("{\"ret_code\":-1,\"err_msg\":\"call restful timeout\"}");
		}
		catch(Exception e)
		{
			jsonRet=new JSONObject("{\"ret_code\":-1,\"err_msg\":\"call restful error\"}");
		}
		return jsonRet;
	}
	
	protected boolean ValidateToken(String token)
	{
		if(this.m_accessId >= 2200000000L)
		{
			return token.length()==64;
		}
		return (token.length()==40) || (token.length()==64);
	}
	
	protected Map<String,Object> InitParams()
	{
		Map params=new HashMap();
		
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return params;
	}
	
	protected boolean ValidateMessageType(Message message)
	{
		if(this.m_accessId<2200000000L)
		{
			return true;
		}
		return false;
	}
	
	protected boolean ValidateMessageType(MessageIOS message,int environment)
	{
		if((this.m_accessId >= 2200000000L) && ((environment==1) || (environment==2)))
		{
			return true;
		}
		return false;
	}
	
	public static JSONObject pushTokenAndroid(long accessId,String secretKey,String title,String content,String token)
	{
		Message message=new Message();
		message.setType(1);
		message.setTitle(title);
		message.setContent(content);
		
		XingeApp xinge=new XingeApp(accessId,secretKey);
		JSONObject ret=xinge.pushSingleDevice(token,message);
		return ret;
	}
	
	public static JSONObject pushAccountAndroid(long accessId,String secretKey,String title,String content,String account)
	{
		Message message=new Message();
		message.setType(1);
		message.setTitle(title);
		message.setContent(content);
		
		XingeApp xinge=new XingeApp(accessId,secretKey);
		JSONObject ret=xinge.pushSingleAccount(0,account,message);
		return ret;
	}
	
	public static JSONObject pushAllAndroid(long accessId,String secretKey,String title,String content)
	{
		Message message=new Message();
		message.setType(1);
		message.setTitle(title);
		message.setContent(content);
		
		XingeApp xinge=new XingeApp(accessId,secretKey);
		JSONObject ret=xinge.pushAllDevice(0,message);
		return ret;
	}
	
	public static JSONObject pushTagAndroid(long accessId,String secretKey,String title,String content,String tag)
	{
		Message message=new Message();
		message.setType(1);
		message.setTitle(title);
		message.setContent(content);
		
		XingeApp xinge=new XingeApp(accessId,secretKey);
		List tagList=new ArrayList();
		tagList.add(tag);
		JSONObject ret=xinge.pushTags(0,tagList,"OR",message);
		return ret;
	}
	
	public static JSONObject pushTokenIos(long accessId,String secretKey,String content,String token,int env)
	{
		MessageIOS message=new MessageIOS();
		message.setAlert(content);
		message.setBadge(1);
		message.setSound("beep.wav");
		
		XingeApp xinge=new XingeApp(accessId,secretKey);
		JSONObject ret=xinge.pushSingleDevice(token,message,env);
		return ret;
	}
	
	public static JSONObject pushAccountIos(long accessId,String secretKey,String content,String account,int env)
	{
		MessageIOS message=new MessageIOS();
		message.setAlert(content);
		message.setBadge(1);
		message.setSound("beep.wav");
		
		XingeApp xinge=new XingeApp(accessId,secretKey);
		JSONObject ret=xinge.pushSingleAccount(0,account,message,env);
		return ret;
	}
	
	public static JSONObject pushAllIos(long accessId,String secretKey,String content,int env)
	{
		MessageIOS message=new MessageIOS();
		message.setAlert(content);
		message.setBadge(1);
		message.setSound("beep.wav");
		
		XingeApp xinge=new XingeApp(accessId,secretKey);
		JSONObject ret=xinge.pushAllDevice(0,message,env);
		return ret;
	}
	
	public static JSONObject pushTagIos(long accessId,String secretKey,String content,String tag,int env)
	{
		MessageIOS message=new MessageIOS();
		message.setAlert(content);
		message.setBadge(1);
		message.setSound("beep.wav");
		
		XingeApp xinge=new XingeApp(accessId,secretKey);
		List tagList=new ArrayList();
		tagList.add(tag);
		JSONObject ret=xinge.pushTags(0,tagList,"OR",message,env);
		return ret;
	}
	
	public JSONObject pushSingleDevice(String deviceToken,Message message)
	{
		if(!ValidateMessageType(message))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type error!'}");
		}
		if(!message.isValid())
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("send_time",message.getSendTime());
		params.put("multi_pkg",Integer.valueOf(message.getMultiPkg()));
		params.put("device_token",deviceToken);
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/push/single_device",params);
	}
	
	public JSONObject pushSingleDevice(String deviceToken,MessageIOS message,int environment)
	{
		if(!ValidateMessageType(message,environment))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type or environment error!'}");
		}
		if(!message.isValid())
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("send_time",message.getSendTime());
		params.put("device_token",deviceToken);
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		params.put("environment",Integer.valueOf(environment));
		
		if((message.getLoopInterval()>0) && (message.getLoopTimes()>0))
		{
			params.put("loop_interval",Integer.valueOf(message.getLoopInterval()));
			params.put("loop_times",Integer.valueOf(message.getLoopTimes()));
		}
		
		return callRestful("http://openapi.xg.qq.com/v2/push/single_device",params);
	}
	
	public JSONObject pushSingleAccount(int deviceType,String account,Message message)
	{
		if(!ValidateMessageType(message))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type error!'}");
		}
		if(!message.isValid())
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("send_time",message.getSendTime());
		params.put("multi_pkg",Integer.valueOf(message.getMultiPkg()));
		params.put("device_type",Integer.valueOf(deviceType));
		params.put("account",account);
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/push/single_account",params);
	}
	
	public JSONObject pushSingleAccount(int deviceType,String account,MessageIOS message,int environment)
	{
		if(!ValidateMessageType(message,environment))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type or environment error!'}");
		}
		if(!message.isValid())
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("send_time",message.getSendTime());
		params.put("device_type",Integer.valueOf(deviceType));
		params.put("account",account);
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		params.put("environment",Integer.valueOf(environment));
		
		return callRestful("http://openapi.xg.qq.com/v2/push/single_account",params);
	}
	
	public JSONObject pushAccountList(int deviceType,List<String> accountList,Message message)
	{
		if(!ValidateMessageType(message))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type error!'}");
		}
		if(!message.isValid())
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("multi_pkg",Integer.valueOf(message.getMultiPkg()));
		params.put("device_type",Integer.valueOf(deviceType));
		params.put("account_list",new JSONArray(accountList).toString());
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/push/account_list",params);
	}
	
	public JSONObject pushAccountList(int deviceType,List<String> accountList,MessageIOS message,int environment)
	{
		if(!ValidateMessageType(message,environment))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type or environment error!'}");
		}
		if(!message.isValid())
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("device_type",Integer.valueOf(deviceType));
		params.put("account_list",new JSONArray(accountList).toString());
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		params.put("environment",Integer.valueOf(environment));
		
		return callRestful("http://openapi.xg.qq.com/v2/push/account_list",params);
	}
	
	public JSONObject pushAllDevice(int deviceType,Message message)
	{
		if(!ValidateMessageType(message))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type error!'}");
		}
		if(!message.isValid())
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("send_time",message.getSendTime());
		params.put("multi_pkg",Integer.valueOf(message.getMultiPkg()));
		params.put("device_type",Integer.valueOf(deviceType));
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		if((message.getLoopInterval()>0) && (message.getLoopTimes()>0))
		{
			params.put("loop_interval",Integer.valueOf(message.getLoopInterval()));
			params.put("loop_times",Integer.valueOf(message.getLoopTimes()));
		}
		
		return callRestful("http://openapi.xg.qq.com/v2/push/all_device",params);
	}
	
	public JSONObject pushAllDevice(int deviceType,MessageIOS message,int environment)
	{
		if(!ValidateMessageType(message,environment))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type or environment error!'}");
		}
		if(!message.isValid())
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("send_time",message.getSendTime());
		params.put("device_type",Integer.valueOf(deviceType));
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		params.put("environment",Integer.valueOf(environment));
		
		if((message.getLoopInterval()>0) && (message.getLoopTimes()>0))
		{
			params.put("loop_interval",Integer.valueOf(message.getLoopInterval()));
			params.put("loop_times",Integer.valueOf(message.getLoopTimes()));
		}
		
		return callRestful("http://openapi.xg.qq.com/v2/push/all_device",params);
	}
	
	public JSONObject pushTags(int deviceType,List<String> tagList,String tagOp,Message message)
	{
		if(!ValidateMessageType(message))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type error!'}");
		}
		if((!message.isValid()) || (tagList.size()==0) || ((!tagOp.equals("AND")) && (!tagOp.equals("OR"))))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'param invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("send_time",message.getSendTime());
		params.put("multi_pkg",Integer.valueOf(message.getMultiPkg()));
		params.put("device_type",Integer.valueOf(deviceType));
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("tags_list",new JSONArray(tagList).toString());
		params.put("tags_op",tagOp);
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		if((message.getLoopInterval()>0) && (message.getLoopTimes()>0))
		{
			params.put("loop_interval",Integer.valueOf(message.getLoopInterval()));
			params.put("loop_times",Integer.valueOf(message.getLoopTimes()));
		}
		
		return callRestful("http://openapi.xg.qq.com/v2/push/tags_device",params);
	}
	
	public JSONObject pushTags(int deviceType,List<String> tagList,String tagOp,MessageIOS message,int environment)
	{
		if(!ValidateMessageType(message,environment))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type or environment error!'}");
		}
		if((!message.isValid()) || (tagList.size()==0) || ((!tagOp.equals("AND")) && (!tagOp.equals("OR"))))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'param invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("send_time",message.getSendTime());
		params.put("device_type",Integer.valueOf(deviceType));
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("tags_list",new JSONArray(tagList).toString());
		params.put("tags_op",tagOp);
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		params.put("environment",Integer.valueOf(environment));
		
		if((message.getLoopInterval()>0) && (message.getLoopTimes()>0))
		{
			params.put("loop_interval",Integer.valueOf(message.getLoopInterval()));
			params.put("loop_times",Integer.valueOf(message.getLoopTimes()));
		}
		
		return callRestful("http://openapi.xg.qq.com/v2/push/tags_device",params);
	}
	
	public JSONObject createMultipush(Message message)
	{
		if(!ValidateMessageType(message))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type error!'}");
		}
		if(!message.isValid())
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("multi_pkg",Integer.valueOf(message.getMultiPkg()));
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/push/create_multipush",params);
	}
	
	public JSONObject createMultipush(MessageIOS message,int environment)
	{
		if(!ValidateMessageType(message,environment))
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message type or environment error!'}");
		}
		if(!message.isValid())
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'message invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("expire_time",Integer.valueOf(message.getExpireTime()));
		params.put("message_type",Integer.valueOf(message.getType()));
		params.put("message",message.toJson());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		params.put("environment",Integer.valueOf(environment));
		
		return callRestful("http://openapi.xg.qq.com/v2/push/create_multipush",params);
	}
	
	public JSONObject pushAccountListMultiple(long pushId,List<String> accountList)
	{
		if(pushId<=0L)
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'pushId invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("push_id",Long.valueOf(pushId));
		params.put("account_list",new JSONArray(accountList).toString());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/push/account_list_multiple",params);
	}
	
	public JSONObject pushDeviceListMultiple(long pushId,List<String> deviceList)
	{
		if(pushId<=0L)
		{
			return new JSONObject("{'ret_code':-1,'err_msg':'pushId invalid!'}");
		}
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("push_id",Long.valueOf(pushId));
		params.put("device_list",new JSONArray(deviceList).toString());
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/push/device_list_multiple",params);
	}
	
	public JSONObject queryPushStatus(List<String> pushIdList)
	{
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		JSONArray jArray=new JSONArray();
		for(String pushId : pushIdList)
		{
			JSONObject js=new JSONObject();
			js.put("push_id",pushId);
			jArray.put(js);
		}
		params.put("push_ids",jArray.toString());
		
		return callRestful("http://openapi.xg.qq.com/v2/push/get_msg_status",params);
	}
	
	public JSONObject queryDeviceCount()
	{
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/application/get_app_device_num",params);
	}
	
	public JSONObject queryTags(int start,int limit)
	{
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("start",Integer.valueOf(start));
		params.put("limit",Integer.valueOf(limit));
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/tags/query_app_tags",params);
	}
	
	public JSONObject queryTags()
	{
		return queryTags(0,100);
	}
	
	public JSONObject queryTagTokenNum(String tag)
	{
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("tag",tag);
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/tags/query_tag_token_num",params);
	}
	
	public JSONObject queryTokenTags(String deviceToken)
	{
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("device_token",deviceToken);
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/tags/query_token_tags",params);
	}
	
	public JSONObject cancelTimingPush(String pushId)
	{
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("push_id",pushId);
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/push/cancel_timing_task",params);
	}
	
	public JSONObject BatchSetTag(List<TagTokenPair> tagTokenPairs)
	{
		for(TagTokenPair pair : tagTokenPairs)
		{
			if(!ValidateToken(pair.token))
			{
				String returnMsgJsonStr=String.format("{\"ret_code\":-1,\"err_msg\":\"invalid token %s\"}",new Object[]{pair.token});
				return new JSONObject(returnMsgJsonStr);
			}
		}
		String returnMsgJsonStr;
		Map params=InitParams();
		
		List tag_token_list=new ArrayList();
		
		for(TagTokenPair pair : tagTokenPairs)
		{
			List singleTagToken=new ArrayList();
			singleTagToken.add(pair.tag);
			singleTagToken.add(pair.token);
			
			tag_token_list.add(singleTagToken);
		}
		
		params.put("tag_token_list",new JSONArray(tag_token_list).toString());
		
		return callRestful("http://openapi.xg.qq.com/v2/tags/batch_set",params);
	}
	
	public JSONObject BatchDelTag(List<TagTokenPair> tagTokenPairs)
	{
		for(TagTokenPair pair : tagTokenPairs)
		{
			if(!ValidateToken(pair.token))
			{
				String returnMsgJsonStr=String.format("{\"ret_code\":-1,\"err_msg\":\"invalid token %s\"}",new Object[]{pair.token});
				return new JSONObject(returnMsgJsonStr);
			}
		}
		String returnMsgJsonStr;
		Map params=InitParams();
		
		List tag_token_list=new ArrayList();
		
		for(TagTokenPair pair : tagTokenPairs)
		{
			List singleTagToken=new ArrayList();
			singleTagToken.add(pair.tag);
			singleTagToken.add(pair.token);
			
			tag_token_list.add(singleTagToken);
		}
		
		params.put("tag_token_list",new JSONArray(tag_token_list).toString());
		
		return callRestful("http://openapi.xg.qq.com/v2/tags/batch_del",params);
	}
	
	public JSONObject queryInfoOfToken(String deviceToken)
	{
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("device_token",deviceToken);
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/application/get_app_token_info",params);
	}
	
	public JSONObject queryTokensOfAccount(String account)
	{
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("account",account);
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/application/get_app_account_tokens",params);
	}
	
	public JSONObject deleteTokenOfAccount(String account,String deviceToken)
	{
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("account",account);
		params.put("device_token",deviceToken);
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/application/del_app_account_tokens",params);
	}
	
	public JSONObject deleteAllTokensOfAccount(String account)
	{
		Map params=new HashMap();
		params.put("access_id",Long.valueOf(this.m_accessId));
		params.put("account",account);
		params.put("timestamp",Long.valueOf(System.currentTimeMillis() / 1000L));
		
		return callRestful("http://openapi.xg.qq.com/v2/application/del_app_account_all_tokens",params);
	}
}