package com.home.shineTool.tool.base;

import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.cls.ClassInfo;

public class BaseOutputInfo
{
	/** 路径 */
	public String path;
	/** 第几组(0:服务器,1:客户端,2:机器人)(影响packageChange) */
	public int group=0;
	/** 代码类型 */
	public int codeType=CodeType.Java;
	/** 对应定义器的序号(-1为不定义) */
	public int defineIndex=-1;
	/** 对应构造器的序号(-1为不构造) */
	public int makeIndex=-1;
	/** 名字结尾(默认为Data) */
	public String nameTail="";
	/** 定义变量名(mid) */
	public String defineVarName="_dataID";
	/** 类定义变量名(mid) */
	public String staticDefineVarName="dataID";
	
	/** 变量默认访问类型 */
	public int defaultVarVisitType=VisitType.Public;
	
	/** 使用静态属性 */
	public boolean useStaticField=false;
	
	/** 默认继承完全限定类名 */
	public String superQName="";
	
	//count
	/** 根包 */
	public String rootPackage;
	
	/** 额外执行方法 */
	public OutputExFunc outputExFunc;
	/** 额外继承方法(返回继承完全限定类名) */
	public OutputExExtendFunc outputExExtendFunc;
	
	/** 输出额外执行方法 */
	public static interface OutputExFunc
	{
		void execute(int group,ClassInfo inCls,ClassInfo cls);
	}
	
	/** 输出额外继承方法 */
	public static interface OutputExExtendFunc
	{
		String execute(String qName);
	}
	
	public void setGroup(int value)
	{
		group=value;
		defineIndex=value;
		makeIndex=value;
	}
	
	/** 是客户端或机器人 */
	public boolean isClientOrRobot()
	{
		return !isServer();
	}
	
	public boolean isServer()
	{
		switch(group)
		{
			case DataGroupType.Server:
			case DataGroupType.Server2:
			case DataGroupType.ServerScene:
				return true;
		}
		
		return false;
	}
	
	public boolean isServerOrRobot()
	{
		switch(group)
		{
			case DataGroupType.Server:
			case DataGroupType.Server2:
			case DataGroupType.ServerScene:
			case DataGroupType.Robot:
			case DataGroupType.ClientDefine:
				return true;
		}
		
		return false;
	}
}
