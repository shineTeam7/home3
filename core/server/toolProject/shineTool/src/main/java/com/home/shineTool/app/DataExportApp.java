package com.home.shineTool.app;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.ExecuteReleaseType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.dataEx.DataRecordTotalData;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.tool.base.BaseExportTool;
import com.home.shineTool.tool.export.CommonDataExportTool;
import com.home.shineTool.tool.export.GameDataExportTool;
import com.home.shineTool.tool.export.NormalDataExportTool;

/** 数据导出 */
public class DataExportApp
{
	protected CommonDataExportTool _commonTool;
	
	protected GameDataExportTool _gameTool;
	
	public DataExportApp()
	{
		makeCommonTool();
		makeGameTool();
	}
	
	protected void makeCommonTool()
	{
		_commonTool=new CommonDataExportTool();
		_commonTool.initNormal("commonDataRecord");
	}
	
	protected void makeGameTool()
	{
		_gameTool=new GameDataExportTool();
		_gameTool.initNormal("dataRecord");
		_gameTool.setCommonExport(_commonTool);
	}
	
	/** 注册定义 */
	public void registDefine()
	{
		_commonTool.registDefine();
	}
	
	/** 执行全部 */
	public void executeAll(boolean isAll,int releaseType)
	{
		ShineToolSetting.isAll=isAll;
		ShineToolSetting.releaseType=releaseType;
		ShineToolSetting.isDataExport=true;
		
		if(releaseType==ExecuteReleaseType.AllClearAndCommon)
		{
			ShineToolSetting.isAllRefresh=true;
		}
		
		CommonDataExportTool.makeShinePackage();
		
		preExecute();
		
		_commonTool.execute();
		
		if(ShineToolSetting.needTrace)
		{
			Ctrl.print("commonEnd:" + _commonTool.getEnd());
		}
		
		makeVersion(true);
		
		if(releaseType==ExecuteReleaseType.AllClear)
		{
			ShineToolSetting.isAllRefresh=true;
		}
		
		_gameTool.setStart(_commonTool.getEnd());
		_gameTool.execute();
		
		if(ShineToolSetting.needTrace)
		{
			Ctrl.print("gameEnd:" + _gameTool.getEnd());
		}
		
		makeVersion(false);
		
		Ctrl.print("OK");
	}
	
	private void makeVersion(boolean isCommon)
	{
		NormalDataExportTool tool=isCommon ? _commonTool : _gameTool;
		String front=isCommon ? "" : ProjectType.getProjectFront(ProjectType.getLastProject());
		
		String path=tool.projectRoots[NormalDataExportTool.ServerBase] + "/control/"+front+"CodeCheckRecord." + CodeType.getExName(CodeType.Java);
		
		ClassInfo cls=ClassInfo.getClassInfoFromPathAbs(path);
		
		toCountSaveVersion(isCommon,"服务器","db",cls,null,NormalDataExportTool.ServerBase,NormalDataExportTool.ServerGame,NormalDataExportTool.ServerCenter);
		
		String clientPath=tool.projectRoots[(isCommon ? NormalDataExportTool.ClientGame : NormalDataExportTool.ClientHotfix)] + "/control/"+front+"CodeCheckRecord." + CodeType.getExName(ShineToolSetting.getClientCodeType());
		ClassInfo clientCls=ClassInfo.getClassInfoFromPathAbs(clientPath);
		
		toCountSaveVersion(isCommon,"通信","msg",cls,clientCls,NormalDataExportTool.ClientMessage);
		cls.write();
		
		if(ShineToolSetting.needClient)
		{
			//需要客户端离线
			if(ShineToolSetting.useClientOfflineGame)
			{
				toCountSaveVersion(isCommon,"客户端","db",clientCls,null,NormalDataExportTool.ClientGame,NormalDataExportTool.ClientHotfix);
			}
			
			clientCls.write();
		}
		
		if(!isCommon && ExecuteReleaseType.isRelease(ShineToolSetting.releaseType))
		{
			DataRecordTotalData data=new DataRecordTotalData();
			data.clsDic=BaseExportTool.newRecordClsDic;
			BytesWriteStream stream=new BytesWriteStream();
			stream.writeVersion(ShineToolGlobal.serverDBRecordVersion);
			data.writeBytes(stream);
			FileUtils.writeFileForBytes(ShineToolGlobal.serverDBRecordPath,stream.getByteArray());
		}
	}
	
	/** 统计服务器db版本 */
	private void toCountSaveVersion(boolean isCommon,String key,String front,ClassInfo cls,ClassInfo cls2,int... serverTypes)
	{
		NormalDataExportTool tool=isCommon ? _commonTool : _gameTool;
		String des=isCommon ? "common:" : "game:";
		
		StringBuilder sb=new StringBuilder();
		
		boolean hasBig=false;
		
		for(int type : serverTypes)
		{
			sb.append(tool.versionStrs[type]);
			
			if(!hasBig && tool.hasBigModifieds[type])
			{
				hasBig=true;
			}
		}
		
		String dbDataVersion=StringUtils.md5(sb.toString());
		
		//加""
		dbDataVersion='"' + dbDataVersion + '"';
		
		String oldDataVersion="";
		
		FieldInfo field=cls.getField(front+"DataMD5");
		
		if(field!=null)
		{
			//去""
			oldDataVersion=field.defaultValue;
		}
		else
		{
			field=new FieldInfo();
			field.visitType=VisitType.Public;
			field.isStatic=true;
			field.isConst=true;
			field.type=cls.getCode().String;
			field.name=front+"DataMD5";
			field.describe=key+"存库数据结构校验";
			
			cls.addField(field);
		}
		
		field.defaultValue=dbDataVersion;
		
		if(cls2!=null)
		{
			FieldInfo field2=new FieldInfo();
			field2.copy(field);
			field2.type=cls2.getCode().String;
			cls2.addField(field2);
		}
		
		boolean isClientMessage=cls2!=null;
		
		boolean needNewToken=false;
		boolean needCheck=false;
		boolean needRefreshClientMessage=false;
		
		field=cls.getField(  front+"DataVersion");
		
		if(ShineToolSetting.isAllRefresh || field==null)
		{
			needNewToken=true;
			needCheck=false;
		}
		else if((hasBig || (needRefreshClientMessage=(isClientMessage && !ShineToolSetting.useMessageFull))) && !oldDataVersion.equals(dbDataVersion))
		{
			needNewToken=true;
			needCheck=true;
			
			if(needRefreshClientMessage)
				needCheck=false;
			
			//跳过message的
			if(needCheck && isClientMessage && ShineToolSetting.useMessageFull && ExecuteReleaseType.isRelease(ShineToolSetting.releaseType) && ShineToolSetting.releaseType==ExecuteReleaseType.WithOutMessage)
				needCheck=false;
			
			//全刷
			if(needCheck && ShineToolSetting.releaseType==ExecuteReleaseType.AllRefresh)
				needCheck=false;
		}
		
		if(needNewToken && needCheck && ExecuteReleaseType.isRelease(ShineToolSetting.releaseType))
		{
			Ctrl.throwError(des+key+"出现不兼容");
			return;
		}
		
		if(field==null)
		{
			field=new FieldInfo();
			field.visitType=VisitType.Public;
			field.isStatic=true;
			field.isConst=true;
			field.type=cls.getCode().Int;
			field.name=front+"DataVersion";
			field.describe=key+"数据结构版本号(只记录不兼容修改)";
			
			cls.addField(field);
		}
		
		int vv;
		
		if(needNewToken)
		{
			field.defaultValue=String.valueOf(vv=MathUtils.getToken());
		}
		else
		{
			vv=Integer.parseInt(field.defaultValue);
		}
		
		if(needNewToken && needCheck)
		{
			Ctrl.print(des+key+"出现不兼容的修改,新的版本号:" + vv);
		}
		
		if(cls2!=null)
		{
			FieldInfo field2=new FieldInfo();
			field2.copy(field);
			field2.type=cls2.getCode().Int;
			cls2.addField(field2);
		}
	}
	
	/** 执行前 */
	private static void preExecute()
	{
		if(ExecuteReleaseType.isRelease(ShineToolSetting.releaseType))
		{
			BytesReadStream stream=FileUtils.readFileForBytesReadStream(ShineToolGlobal.serverDBRecordPath);
			
			if(stream!=null && stream.checkVersion(ShineToolGlobal.serverDBRecordVersion))
			{
				BaseExportTool.hasOldRecordDic=true;
				
				DataRecordTotalData data=new DataRecordTotalData();
				data.readBytes(stream);
				
				BaseExportTool.oldRecordClsDic=data.clsDic;//旧数据赋值
			}
			else
			{
				BaseExportTool.hasOldRecordDic=false;
			}
		}
	}
	
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		
		//ShineToolSetting.needTrace=true;
		//ShineToolSetting.isAllRefreshMessage=true;
		
		new DataExportApp().executeAll(false,ExecuteReleaseType.Debug);
	}
}
