package com.home.shineTool.tool.export;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.tool.data.DataExportTool;

import java.io.File;

public class HttpResponseExportTool extends DataExportTool
{
	private HttpResponseResultExportTool _resultTool;
	
	public void setResultTool(HttpResponseResultExportTool tool)
	{
		_resultTool=tool;
	}
	
	@Override
	protected void toMakeAfter()
	{
		super.toMakeAfter();
		
		String path=FileUtils.fixPath(_inputFile.getPath());
		
		path=_resultTool.getInputRootPath()+path.substring(_inputRootPath.length(),path.length());
		
		boolean isResponse=_outputInfo.nameTail.endsWith("HttpResponse");
		
		String vv=isResponse ? "httpResponse" : "httpRequest";
		
		path=path.replace("/"+vv+"/","/httpResponseResult/");
		
		String fileFrontName=FileUtils.getFileFrontName(path);
		String fileExName=FileUtils.getFileExName(path);
		
		path=fileFrontName.substring(0,fileFrontName.length() - _mark.length()) + _resultTool.getMark() + "." + fileExName;
		
		String resultClsPath=_resultTool.getOutFilePath(_outputInfo.group,new File(path));
		
		ClassInfo resultCls=ClassInfo.getSimpleClassInfoFromPath(resultClsPath);
		
		if(resultCls==null)
		{
			//支持没有RO
			//if(!_inputCls.isAbstract)
			//{
			//	Ctrl.throwError("找不到对应的RO:",_inputCls.clsName);
			//}
			
			_outputCls.removeField("_re");
			_outputCls.removeMethodByName("reBack");
		}
		else
		{
			_outputCls.addImport(resultCls.getQName());
			
			FieldInfo field=new FieldInfo();
			field.name="_re";
			field.type=resultCls.clsName;
			field.visitType=VisitType.Protected;
			
			if(isResponse)
				field.defaultValue=_outputCls.getCode().createNewObject(resultCls.clsName);
			
			field.describe="返回值";
			
			_outputCls.addField(field);
			
			if(isResponse)
			{
				MethodInfo reBackMethod=_resultTool.getReBackMethod(resultCls.getQName());
				
				if(reBackMethod!=null)
				{
					_outputCls.removeMethodByName(reBackMethod.name);
					_outputCls.addMethod(reBackMethod);
				}
			}
		}
	}
}
