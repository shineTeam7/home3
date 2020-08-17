package com.home.shineTool.global;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.XML;
import com.home.shine.support.collection.SSet;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.ExecuteReleaseType;
import com.home.shineTool.constlist.ToolClientType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 基础构造设置 */
public class ShineToolSetting
{
	private static boolean _inited=false;
	/** 默认语言类型(数据工程的) */
	public static int defaultCodeType=CodeType.Java;
	
	/** 数据工程record版本号 */
	public static String dataExportVersion="1.7";
	/** 配置表工程record版本号 */
	public static String configExportVersion="2.5";
	/** 数据库工程record版本号 */
	public static String dbExportVersion="1.5";
	
	/** 是否输出调试信息 */
	public static boolean needTrace=false;
	
	/** 是否全重刷(包括线上不允许重刷的部分,此开关只能上线前用) */
	public static boolean isAllRefresh=false;
	/** 是否全执行 */
	public static boolean isAll=false;
	/** 是否重刷消息部分 */
	public static boolean isAllRefreshMessage=false;
	/** 发布模式类型(见ExecuteReleaseType) */
	public static int releaseType=ExecuteReleaseType.Debug;
	/** 本次是否是dataExport工具 */
	public static boolean isDataExport=false;
	/** 客户端是否只输出数据(兼容部分公司需求，无Player，无资源，国际化，只有data,config,net) */
	public static boolean isClientOnlyData=false;
	/** 数据id定义部分是否可重复使用 */
	public static boolean isDataDefineReUsed=true;
	/** 协议是否每次执行all时都重新刷新 */
	public static boolean useIntInsteadByte=true;
	
	/** 是否S系列集合指定数组创建(java用)(创建时)(性能有提升,开) */
	public static boolean isSCollectionSignedArrayCreate=true;
	/** 是否S系列集合指定数组写出(java用)(序列化时)(性能有提升,但是书写会麻烦，暂时先关) */
	public static boolean isSCollectionSignedArrayWrite=false;
	///** 自定义数据对象是否可在data工程里直接赋初值 */
	//public static boolean isCustomObjectCanUseNewValue=false;
	/** 是否读取方法的对象全部new出 */
	public static boolean isReadAllNew=false;
	/** 是否cs类默认包含shine东西 */
	public static boolean isCSHasShineThing=true;
	
	/** 离线事务是否使用表执行 */
	public static boolean outlineWorkUseTable=true;
	/** 配置表全必须 */
	public static boolean configAllNecessary=true;
	/** 是否需要输出配置增量 */
	public static boolean needConfigIncrement=false;
	
	//UI
	
	/** UI是否需要重置位置 */
	public static boolean needUIResize=false;
	/** 是否启用指定UI元素生成 */
	public static boolean useUIElementExport=true;
	
	//包替换
	/** 自定义数据转换 */
	public static Map<String,String> varTransDic;
	
	/** cs unity namespace */
	public static String csUnityNamespace="UnityEngine";
	/** cs引擎namespace */
	public static String csShineNamespace="ShineEngine";
	/** 配置包扩展名 */
	public static String excelExName="xlsx";
	
	//count
	/** 包替换内容(input->output) */
	public static Map<Integer,List<GroupPackageData>> groupPackageChangeDic=new HashMap<>();
	
	public static class GroupPackageData
	{
		public String inputRootPackge;
		
		public String outRootPackage;
		
		public String mark;
		
		public String tail;
		
		public GroupPackageData(String inputRoot,String outRoot,String mark,String tail)
		{
			this.inputRootPackge=inputRoot;
			this.outRootPackage=outRoot;
			this.mark=mark;
			this.tail=tail;
		}
	}
	
	//data
	
	//全局设定
	/** 总包名 */
	public static String globalPackage="com.home.";
	/** java路径前缀 */
	public static String javaCodeFront="/src/main/java/com/home/";
	/** 附加集合类包名 */
	public static String exCollectionPackage=globalPackage + "shine.support.collection";
	
	/** 数据基类命名(完全限定名) */
	public static String dataBaseQName=globalPackage + "shine.data.BaseData";
	/** 数据基类命名(类名) */
	public static String dataBaseName="BaseData";
	
	/** 数据构造接口名称(完全限定名) */
	public static String dataMakerQName=globalPackage + "shine.tool.DataMaker";
	/** 消息绑定Tool名称(完全限定名) */
	public static String messageBindToolQName=globalPackage + "shine.tool.MessageBindTool";
	
	/** 数据基类命名(完全限定名) */
	public static String configBaseQName=globalPackage + "commonBase.config.base.BaseConfig";
	
	//	/** 字节流完全限定名 */
	//	public static String bytesQName=globalPackage+"shine.bytes.Bytes";
	/** 读流完全限定名 */
	public static String bytesReadStreamQName=globalPackage + "shine.bytes.BytesReadStream";
	/** 写流完全限定名 */
	public static String bytesWriteStreamQName=globalPackage + "shine.bytes.BytesWriteStream";
	/** 数据控制完全限定名 */
	public static String bytesControlQName=globalPackage + "shine.control.BytesControl";
	/** 数据控制名 */
	public static String bytesControlName="BytesControl";
	
	/** 读写流变量名 */
	public static String bytesSteamVarName="stream";
	
	/** as3Long类型完全限定名 */
	public static String as3LongQName=globalPackage + "shine.bytes.Long";
	
	/** 双整形数据类完全限定名 */
	public static String dIntDOQName=globalPackage + "shineData.data.DIntDO";
	/** BigFloat数据类完全限定名 */
	public static String BigFloatDOQName=globalPackage + "commonData.data.system.BigFloatDO";
	
	/** 数据写出工具类名 */
	public static String dataWriterName="DataWriter";
	/** 数据写出工具完全限定类名 */
	public static String dataWriterQName=globalPackage + "shine.support.DataWriter";
	
	/** 数据池类名 */
	public static String dataPoolName="DataPool";
	/** 数据池完全限定类名 */
	public static String dataPoolQName=globalPackage + "shine.support.pool.DataPool";
	
	/** 字节数组名 */
	public static String byteArrName="byte[]";
	
	public static String CtrlQName=globalPackage+"shine.ctrl.Ctrl";
	
	///** 字符串构建类名 */
	//public static String stringBuilderMame="StringBuilder";
	///** 字符串的命名空间 */
	//public static String csStringNameSpace="System.Text";
	
	static
	{
		ShineToolSetting.varTransDic=new HashMap<>();
		//替换数据
		ShineToolSetting.varTransDic.put("DO","Data");
	}
	
	//net
	/** BaseRequest(完全限定名) */
	public static String baseRequestQName=globalPackage + "shine.net.base.BaseRequest";
	/** BaseResponse(完全限定名) */
	public static String baseResponseQName=globalPackage + "shine.net.base.BaseResponse";
	
	/** BaseRequest(完全限定名) */
	public static String baseHttpRequestQName=globalPackage + "shine.net.httpRequest.BytesHttpRequest";
	/** BaseResponse(完全限定名) */
	public static String baseHttpResponseQName=globalPackage + "shine.net.httpResponse.BytesHttpResponse";
	
	//table
	
	/** BaseTable(完全限定名) */
	public static String tableQName=globalPackage + "shine.table.BaseTable";
	/** BaseDBTask(完全限定名) */
	public static String taskQName=globalPackage + "shine.table.task.BaseDBTask";
	/** BaseDBResult(完全限定名) */
	public static String resultQName=globalPackage + "shine.table.BaseDBResult";
	/** 日期数据(完全限定名) */
	public static String dateDataQName=globalPackage + "shine.data.DateData";
	
	//part
	
	/** 客户端基础数据基类QName */
	public static String clientBasePartDataQName=ShineToolSetting.globalPackage + "commonBase.baseData.BaseClientPartData";
	
	/** 数据复制是深拷还是潜拷(目前是深拷) */
	public static boolean copyServerUseShadowCopy=true;
	
	/** 事务基类QName */
	public static String workDOQName=ShineToolSetting.globalPackage + "commonData.data.system.WorkDO";
	/** 角色事务基类QName */
	public static String playerWorkDOQName=ShineToolSetting.globalPackage + "commonData.data.system.PlayerWorkDO";
	/** 区服事务基类QName */
	public static String areaWorkDOQName=ShineToolSetting.globalPackage + "commonData.data.system.AreaGlobalWorkDO";
	/** 中心服事务基类QName */
	public static String centerWorkDOQName=ShineToolSetting.globalPackage + "commonData.data.system.CenterGlobalWorkDO";
	/** 角色对角色TCC事务基类QName */
	public static String playerToPlayerTCCWorkDOQName=ShineToolSetting.globalPackage + "commonData.data.system.PlayerToPlayerTCCWDO";
	/** 玩家群事务基类QName */
	public static String roleGroupWorkDOQName=ShineToolSetting.globalPackage + "commonData.data.social.roleGroup.work.RoleGroupWorkDO";
	/** 角色对玩家群TCC事务基类QName */
	public static String playerToRoleGroupTCCWorkDOQName=ShineToolSetting.globalPackage + "commonData.data.social.roleGroup.work.PlayerToRoleGroupTCCWDO";
	
	//trigger
	public static String triggerFuncEntryQName=ShineToolSetting.globalPackage + "commonBase.support.func.TriggerFuncEntry";
	public static String triggerExecutorQName=ShineToolSetting.globalPackage + "commonBase.trigger.TriggerExecutor";
	public static String triggerFuncDataQName=ShineToolSetting.globalPackage + "shine.data.trigger.TriggerFuncData";
	public static String triggerArgQName=ShineToolSetting.globalPackage + "commonBase.trigger.TriggerArg";
	
	/** 数据构造接口名称(完全限定名) */
	public static String TriggerFuncMakerQName=globalPackage + "commonBase.tool.TriggerFuncMaker";
	
	//settings
	
	/** 是否启用国际化 */
	public static boolean useLanguage=true;
	/** 是否启用客户端离线游戏 */
	public static boolean useClientOfflineGame=false;
	/** 是否启用协议完整写入(适用于不用热更的项目) */
	public static boolean useMessageFull=false;
	/** 是否需要热更(包括配置部分) */
	public static boolean needHotfix=true;
	/** 是否需要客户端部分 */
	public static boolean needClient=true;
	/** 是否需要场景服部分 */
	public static boolean needScene=true;
	/** 客户端类型 */
	public static int clientType=ToolClientType.Unity;
	/** ts语言，是否全使用Shine命名空间 */
	public static boolean TSUseShineNamespace=true;
	/** 配置表是否需要压缩 */
	public static boolean configNeedCompress=true;
	/** 忽略的自定义配置组 */
	public static SSet<String> ignoreCustomConfigs=new SSet<>();
	/** 占用的输入field名 */
	public static SSet<String> usedInputFieldName=new SSet<>();
	/** 是否使用旧版HData代码段 */
	public static boolean useOldHDataSection=false;
	
	/** 初始化设置部分 */
	public static synchronized void init()
	{
		if(_inited)
			return;
		
		_inited=true;
		
		ShineToolGlobal.init();
		
		XML xml=FileUtils.readFileForXML(ShineToolGlobal.toolSettingPath);
		
		if(xml!=null)
		{
			XML child;
			
			if((child=xml.getChildrenByNameOne("useLanguage"))!=null)
				useLanguage=StringUtils.strToBoolean(child.getProperty("value"));
			
			if((child=xml.getChildrenByNameOne("useClientOfflineGame"))!=null)
				useClientOfflineGame=StringUtils.strToBoolean(child.getProperty("value"));
			
			if((child=xml.getChildrenByNameOne("useMessageFull"))!=null)
				useMessageFull=StringUtils.strToBoolean(child.getProperty("value"));
			
			if((child=xml.getChildrenByNameOne("needHotfix"))!=null)
				needHotfix=StringUtils.strToBoolean(child.getProperty("value"));
			
			if((child=xml.getChildrenByNameOne("needClient"))!=null)
				needClient=StringUtils.strToBoolean(child.getProperty("value"));
			
			if((child=xml.getChildrenByNameOne("needScene"))!=null)
				needScene=StringUtils.strToBoolean(child.getProperty("value"));
			
			if((child=xml.getChildrenByNameOne("useOldHDataSection"))!=null)
				useOldHDataSection=StringUtils.strToBoolean(child.getProperty("value"));
			
			if((child=xml.getChildrenByNameOne("clientType"))!=null)
				clientType=Integer.parseInt(child.getProperty("value"));

			if((child=xml.getChildrenByNameOne("configNeedCompress"))!=null)
				configNeedCompress=StringUtils.strToBoolean(child.getProperty("value"));
			
			for(XML xl:xml.getChildrenByName("ignoreCustomConfig"))
			{
				ignoreCustomConfigs.add(xl.getProperty("value"));
			}
			
			if((child=xml.getChildrenByNameOne("bytesUseBitBoolean"))!=null)
				ShineSetting.bytesUseBitBoolean=StringUtils.strToBoolean(child.getProperty("value"));
			
			//if((child=xml.getChildrenByNameOne("useCombineGameDB"))!=null)
			//	useCombineGameDB=StringUtils.strToBoolean(child.getProperty("value"));
			
		}
		
		ShineToolGlobal.setPathByCode();
		
		ShineToolGlobal.checkNotCorePath();
		
		
		usedInputFieldName.add("dataID");
		usedInputFieldName.add("stream");
		usedInputFieldName.add("pool");
		usedInputFieldName.add("re");
	}
	
	/** 获取客户端代码类型 */
	public static int getClientCodeType()
	{
		switch(clientType)
		{
			case ToolClientType.Unity:
				return CodeType.CS;
			case ToolClientType.Flash:
				return CodeType.AS3;
			case ToolClientType.Laya:
				return CodeType.TS;
		}
		
		return CodeType.CS;
	}
}
