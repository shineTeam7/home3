package com.home.shineTool.app;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.constlist.UIElementType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.UIObjectData;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.MathUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.CSClassInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;

import java.io.File;
import java.util.List;

/** 生成UI模型类 */
public class UIModelApp
{
	private static int _codeTyp=CodeType.CS;
	
	private CodeInfo _code=CodeInfo.getCode(_codeTyp);
	
	private SMap<String,UIObjectData> _elements=new SMap<>();
	
	private SMap<String,UIObjectData> _models=new SMap<>();
	
	//temp
	
	private SMap<String,File> _tempFileNameDic=new SMap<>();
	
	public void execute()
	{
		BytesReadStream stream=FileUtils.readFileForBytesReadStream(ShineToolGlobal.clientTempPath + "/uiInfo.bin");
		
		int len=stream.readLen();
		
		for(int i=0;i<len;++i)
		{
			UIObjectData data=new UIObjectData();
			data.readBytesSimple(stream);
			data.type=UIElementType.Element;
			
			_elements.put(data.name,data);
		}
		
		len=stream.readLen();
		
		for(int i=0;i<len;++i)
		{
			UIObjectData data=new UIObjectData();
			data.readBytesSimple(stream);
			data.type=UIElementType.Model;
			
			_models.put(data.name,data);
		}
		
		String gPath=ShineToolGlobal.clientMainSrcPath + "/game/view/uiGenerate/";
		String hPath=ShineToolGlobal.clientHotfixSrcPath + "/hotfix/view/uiGenerate/";
		
		doOnePath(gPath + "elements",hPath+"elements",_elements);
		doOnePath(gPath + "models",hPath+"models",_models);
		
		Ctrl.print("OK!");
	}
	
	/** 执行一个路径 */
	private void doOnePath(String gPath,String hPath,SMap<String,UIObjectData> dic)
	{
		List<File> deepFileList=FileUtils.getDeepFileList(gPath);
		for(File f:deepFileList)
		{
			_tempFileNameDic.put(f.getName(),f);
		}
		
		deepFileList=FileUtils.getDeepFileList(hPath);
		for(File f:deepFileList)
		{
			_tempFileNameDic.put(f.getName(),f);
		}
		
		for(UIObjectData data : dic)
		{
			String p;
			//g层内容
			if(!ShineToolSetting.needHotfix || data.style.equals("G"))
			{
				p=gPath;
			}
			else
			{
				p=hPath;
			}
			
			makeOne(p,data,"");
		}
		
		//不为空
		if(!_tempFileNameDic.isEmpty())
		{
			_tempFileNameDic.forEachValue(v->
			{
				v.delete();
				Ctrl.print("删除一个",v.getName());
			});
			
			_tempFileNameDic.clear();
		}
	}
	
	private void makeOne(String rootPath,UIObjectData data,String superName)
	{
		switch(data.type)
		{
			case UIElementType.Element:
			{
				superName="UIElement";
			}
			break;
			case UIElementType.Model:
			{
				superName="UIModel";
			}
			break;
		}
		
		String clsName=superName + "_" + data.name;
		
		String fileName=clsName + "." + CodeType.getExName(_codeTyp);
		
		String path=rootPath + "/" + fileName;
		
		_tempFileNameDic.remove(fileName);
		_tempFileNameDic.remove(fileName+".meta");//包括.meta
		
		ClassInfo cls=ClassInfo.getVoidClassInfoFromPath(path);
		cls.addShineThings();
		
		((CSClassInfo)cls).addUnityEngine();
		
		if(ShineToolSetting.useUIElementExport && data.type==UIElementType.Container)
		{
			Ctrl.throwError("使用ElementExport导出的UI，不该再有Container");
		}
		
		switch(data.type)
		{
			case UIElementType.Element:
			{
				cls.extendsClsName="UIElement";
			}
				break;
			case UIElementType.Model:
			{
				cls.extendsClsName="UIModel";
			}
				break;
			case UIElementType.Container:
			{
				cls.extendsClsName="UIContainer";
			}
				break;
			case UIElementType.Object:
			{
				cls.extendsClsName="UIObject";
			}
				break;
		}
		
		int len=data.children.length();
		
		CodeWriter writer=cls.createWriter();
		writer.writeCustom(_code.Super + ".init(obj);");
		writer.writeEmptyLine();
		
		if(ShineToolSetting.useUIElementExport)
		{
			writer.writeVarCreate("exports","ElementInfo[]","obj.transform.GetComponent<UIElementComponent>().exports");
			writer.writeEmptyLine();
		}
		
		superName+="_" + data.name;
		
		for(int i=0;i<len;++i)
		{
			UIObjectData child=data.children.get(i);
			
			String childType;
			
			if((ShineToolSetting.useUIElementExport) && child.type==UIElementType.Container)
			{
				Ctrl.throwError("使用ElementExport导出的UI，不该再有Container");
			}
			
			if(child.type==UIElementType.Container)
			{
				if(data.type==UIElementType.Element)
				{
					superName="UIContainer_Element_" + data.name;
				}
				else if(data.type==UIElementType.Model)
				{
					superName="UIContainer_Model_" + data.name;
				}
				else if(data.type==UIElementType.Container)
				{
					superName=clsName;
				}
				else
				{
					Ctrl.throwError("出错");
				}
				
				makeOne(rootPath,child,superName);
				
				childType=superName + "_" + child.name;
			}
			else
			{
				childType=getElementTypeStr(child);
			}
			
			String genericityName=null;
			
			if(UIElementType.hasGenericity(child.type))
			{
				genericityName="UIElement_"+child.strArgs[0];
				childType+="<"+genericityName+ ">";
			}
			
			FieldInfo field=new FieldInfo();
			field.name=child.name;
			field.type=childType;
			field.visitType=VisitType.Public;
			cls.addField(field);
			
			writer.writeVarSet(child.name,_code.createNewObject(childType));
			
			if(UIElementType.hasGenericity(child.type))
			{
				writer.writeCustom(child.name+".setCreateFunc(()=>"+cls.getCode().createNewObject(genericityName)+");");
			}
			
			writer.writeCustom(child.name + ".name=\"" + child.name + "\";");
			
			if(ShineToolSetting.useUIElementExport)
			{
				writer.writeCustom(child.name + ".init(exports[" + i + "].obj);");
			}
			else
			{
				writer.writeCustom(child.name + ".init(obj.transform.Find(\"" + child.name + "\").gameObject);");
			}
			
			
			writer.writeCustom("registChild(" + child.name + ");");
			writer.writeEmptyLine();
		}
		
		writer.writeEnd();
		
		MethodInfo method=new MethodInfo();
		method.name="init";
		method.visitType=VisitType.Public;
		method.isOverride=true;
		method.args.add(new MethodArgInfo("obj","GameObject"));
		method.content=writer.toString();
		cls.addMethod(method);
		
		cls.write();
	}
	
	private String getElementTypeStr(UIObjectData data)
	{
		String re="";
		
		switch(data.type)
		{
			case UIElementType.Object:
			{
				re="UIObject";
			}
				break;
			case UIElementType.Container:
			{
			
			}
				break;
			case UIElementType.Element:
			{
				re="UIElement_" + data.style;
			}
				break;
			case UIElementType.Model:
			{
				re="UIModel_" + data.style;
			}
				break;
			case UIElementType.ImageFrameContainer:
			{
				re="UIImageFrameContainer";
			}
			break;
			case UIElementType.SScrollView:
			{
				re="UISScrollView";
			}
			break;
			case UIElementType.SScrollViewFake3D:
			{
				re="UISScrollViewFake3D";
			}
			break;
			case UIElementType.SPageView:
			{
				re="UISPageView";
			}
			break;
			case UIElementType.Button:
			{
				re="UIButton";
			}
			break;
			case UIElementType.TextField:
			{
				re="UITextField";
			}
			break;
			case UIElementType.InputField:
			{
				re="UIInputField";
			}
			break;
			case UIElementType.I18NText:
			{
				re="UII18NText";
			}
			break;
			case UIElementType.BloodBar:
			{
				re="UIBloodBar";
			}
			break;
			case UIElementType.ScrollBar:
			{
				re="UIScrollBar";
			}
			break;
			case UIElementType.Dropdown:
			{
				re="UIDropdown";
			}
			break;
			case UIElementType.ImageLoader:
			{
				re="UIImageLoader";
			}
			break;
			case UIElementType.Image:
			{
				re="UIImage";
			}
			break;
			case UIElementType.RawImageLoader:
			{
				re="UIRawImageLoader";
			}
			break;
			case UIElementType.RawImage:
			{
				re="UIRawImage";
			}
			break;
			case UIElementType.Text:
			{
				re="UIText";
			}
			break;
			case UIElementType.Toggle:
			{
				re="UIToggle";
			}
			break;
			case UIElementType.Slider:
			{
				re="UISlider";
			}
			break;
			case UIElementType.SkeletonGraphicLoader:
			{
				re="UISkeletonGraphicLoader";
			}
			break;
			case UIElementType.SkeletonGraphic:
			{
				re="UISkeletonGraphic";
			}
			break;
			case UIElementType.GuideMask:
			{
				re="UIGuideMask";
			}
			break;
		}
		
		return re;
	}
	
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		
		new UIModelApp().execute();
	}
}
