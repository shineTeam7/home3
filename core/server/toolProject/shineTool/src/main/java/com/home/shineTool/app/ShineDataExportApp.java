package com.home.shineTool.app;

import com.home.shineTool.tool.export.ShineDataExportTool;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.hotfix.ILClassInfo;
import com.home.shineTool.tool.hotfix.ILRuntimeAdapterTool;

/** shine数据变更App */
public class ShineDataExportApp
{
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		
		exportData();
		
		//adapters已由客户端自行生成
		//exportShineAdapters();
		
		Ctrl.print("OK");
	}
	
	/** 导出shine data部分 */
	private static void exportData()
	{
		//ShineToolSetting.isAllRefresh=true;
		
		ShineToolSetting.isAll=true;
		ShineToolSetting.needTrace=true;
		
		ShineDataExportTool tool=new ShineDataExportTool();
		tool.initNormal("shineDataRecord");
		tool.execute();
	}
	
	//adapters
	
	/** 执行shine适配器列表 */
	private static void doShineAdapterList(SList<ILClassInfo> list)
	{
		ILRuntimeAdapterTool.init();
		
		String cPath=ShineToolGlobal.clientMainSrcPath+"/shine/control/ILRuntimeControl.cs";
		
		String outPath=ShineToolGlobal.clientMainSrcPath+"/shine/adapters";
		
		ClassInfo cCls=ClassInfo.getClassInfoFromPath(cPath);
		
		MethodInfo method=cCls.getMethodByName("initOtherAdapters");
		
		CodeWriter writer=cCls.createWriter();
		
		list.forEach(v->
		{
			ILRuntimeAdapterTool.doOneClass(v,outPath,true,writer);
		});
		
		writer.writeEnd();
		method.content=writer.toString();
		
		//写回
		cCls.write();
	}
	
	/** 导出shine适配器类 */
	private static void exportShineAdapters()
	{
		SList<ILClassInfo> list=new SList<>();
		
		ILClassInfo cls;
		
		list.add(cls=new ILClassInfo("MonoBehaviour"));
		cls.addMethod("Start",null,VisitType.Private,false);
		cls.addMethod("Update",null,VisitType.Private,false);
		cls.addMethod("FixedUpdate",null,VisitType.Private,false);
		cls.addMethod("OnGUI",null,VisitType.Private,false);
		cls.addMethod("OnDestroy",null,VisitType.Private,false);
		
		list.add(cls=new ILClassInfo("BaseData"));
		cls.addMethod("toReadBytesFull",VisitType.Protected,"BytesReadStream","stream");
		cls.addMethod("toWriteBytesFull",VisitType.Protected,"BytesWriteStream","stream");
		cls.addMethod("toReadBytesSimple",VisitType.Protected,"BytesReadStream","stream");
		cls.addMethod("toWriteBytesSimple",VisitType.Protected,"BytesWriteStream","stream");
		cls.addMethod("toCopy",VisitType.Protected,"BaseData","data");
		cls.addMethod("toShadowCopy",VisitType.Protected,"BaseData","data");
		cls.addMethod("toDataEquals","bool",VisitType.Protected,true,"BaseData","data");
		cls.addMethod("getDataClassName","string",VisitType.Public,true);
		cls.addMethod("toWriteDataString",VisitType.Protected,"DataWriter","writer");
		cls.addMethod("initDefault",VisitType.Public);
		cls.addMethod("beforeWrite",VisitType.Protected);
		cls.addMethod("afterRead",VisitType.Protected);
		
		list.add(cls=new ILClassInfo("BaseRequest"));
		cls.addMethod("copyData",VisitType.Protected);
		cls.addMethod("toWriteBytesSimple",VisitType.Protected,"BytesWriteStream","stream");
		
		list.add(cls=new ILClassInfo("BaseResponse"));
		cls.addMethod("toReadBytesSimple",VisitType.Protected,"BytesReadStream","stream");
		cls.addMethod("preExecute",VisitType.Protected);
		cls.addMethod("execute",null,VisitType.Protected,false);
		
		list.add(cls=new ILClassInfo("BytesHttpRequest"));
		cls.addMethod("copyData",VisitType.Protected);
		cls.addMethod("toWriteBytesSimple",VisitType.Protected,"BytesWriteStream","stream");
		cls.addMethod("toRead",null,VisitType.Protected,false);
		cls.addMethod("onComplete",null,VisitType.Protected,false);
		
		list.add(new ILClassInfo("DataMaker"));
		
		list.add(cls=new ILClassInfo("DebugControl"));
		cls.addMethod("init",VisitType.Public);
		cls.addMethod("dispose",VisitType.Public);
		
		list.add(cls=new ILClassInfo("UIContainer"));
		cls.addMethod("init",VisitType.Public,"GameObject","obj");
		
		list.add(cls=new ILClassInfo("UIElement"));
		cls.addMethod("init",VisitType.Public,"GameObject","obj");
		
		list.add(cls=new ILClassInfo("UIModel"));
		cls.addMethod("init",VisitType.Public,"GameObject","obj");
		
		list.add(cls=new ILClassInfo("PoolObject"));
		cls.addMethod("clear",null,VisitType.Public,false);
		
		doShineAdapterList(list);
	}
	
	
}
