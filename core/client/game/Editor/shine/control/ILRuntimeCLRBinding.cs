#if UNITY_EDITOR
using UnityEditor;
using UnityEngine;
using System;
using System.Text;
using System.Collections.Generic;
using System.Reflection;

namespace ShineEngine
{
    [System.Reflection.Obfuscation(Exclude=true)]
    public class ILRuntimeCLRBinding
    {
        /** 回车 */
        private const string Enter="\r\n";
        /** 换行 */
        private const string Tab="\t";

        /** 模板文字 */
        private static string _templete;

        private static bool _isShine;

        public static void GenerateCLRBinding()
        {
            List<Type> list=new List<Type>();

            ILRuntimeControl.instance.initSGenerateList(list);
            if(list.Count>0)
            {
                ILRuntime.Runtime.CLRBinding.BindingCodeGenerator.GenerateBindingCode(list,"Assets/src/shine/ILRuntime/Generated");
                list.Clear();
            }

            ILRuntimeControl.instance.initCGenerateList(list);

            if(list.Count>0)
            {
                ILRuntime.Runtime.CLRBinding.BindingCodeGenerator.GenerateBindingCode(list,"Assets/src/commonMain/ILRuntime/Generated");
                list.Clear();
            }

            ILRuntimeControl.instance.initGGenerateList(list);

            if(list!=null && list.Count>0)
            {
                ILRuntime.Runtime.CLRBinding.BindingCodeGenerator.GenerateBindingCode(list,"Assets/src/main/ILRuntime/Generated");
                list.Clear();
            }
        }

        public static void GenerateCLRBindingByAnalysis()
        {
            //用新的分析热更dll调用引用来生成绑定代码
            ILRuntime.Runtime.Enviorment.AppDomain domain=new ILRuntime.Runtime.Enviorment.AppDomain();
            using(System.IO.FileStream fs=new System.IO.FileStream("Assets/StreamingAssets/logic.dll",System.IO.FileMode.Open,System.IO.FileAccess.Read))
            {
                domain.LoadAssembly(fs);
            }

            //Crossbind Adapter is needed to generate the correct binding code
            ILRuntimeControl.instance.initILRuntime(domain);

            ILRuntime.Runtime.CLRBinding.BindingCodeGenerator.GenerateBindingCode(domain,"Assets/src/main/ILRuntime/Generated");
        }

        public static void GenerateAll()
        {
            initGenerate();

            string front="Assets/src/commonGame";
            doOneGenerate(front + "/cGenerateAdapter.xml"
                ,front + "/control/CILRuntimeControl.cs"
                ,front+"/adapters",Project_Common);

            front="Assets/src/game";

            doOneGenerate(front + "/gGenerateAdapter.xml"
                ,front + "/control/GILRuntimeControl.cs"
                ,front+"/adapters",Project_Game);

            Ctrl.print("OK!");
            AssetDatabase.Refresh();
        }

        public static void GenerateCommon()
        {
            initGenerate();

            string front="Assets/src/commonGame";

            doOneGenerate(front + "/cGenerateAdapter.xml"
                ,front + "/control/CILRuntimeControl.cs"
                ,front+"/adapters",Project_Common);

            Ctrl.print("OK!");
            AssetDatabase.Refresh();
        }

        public static void GenerateGame()
        {
            initGenerate();

            string front="Assets/src/game";

            doOneGenerate(front + "/gGenerateAdapter.xml"
                ,front + "/control/GILRuntimeControl.cs"
                ,front+"/adapters",Project_Game);

            Ctrl.print("OK!");
            AssetDatabase.Refresh();
        }

        /** 替换方法字符串 */
        private static string replaceMethod(string content,string methodKey,string methodContent)
        {
            int index=content.IndexOf(methodKey);

            if(index==-1)
            {
                Ctrl.throwError("未找到该方法",methodKey);
                return "";
            }

            int leftIndex=content.IndexOf("{",index+methodKey.Length);

            int rightIndex=StringUtils.getAnotherIndex(content,"{","}",leftIndex);

            string re=content.Substring(0,leftIndex + 1) + methodContent + content.Substring(rightIndex);

            return re;
        }

        /** 执行一个Adapater */
        private static void doOneClass(ILClassInfo clsInfo,String path,bool isShine,StringBuilder writer)
        {
            string str=_templete;

            if(isShine)
            {
                str=str.Replace("$namespace","namespace ShineEngine" + Enter + "{");
                str=str.Replace("$_namespace","}");
            }
            else
            {
                str=str.Replace("$namespace","using ShineEngine;"+Enter);
                str=str.Replace("$_namespace","");
            }

            str=str.Replace("$clsName",clsInfo.clsName);

            StringBuilder sb1=StringBuilderPool.create();
            StringBuilder sb2=StringBuilderPool.create();
            IntSet marks=new IntSet();

            for(int i=0,len=clsInfo.methods.size();i<len;++i)
            {
                doOneMethod(clsInfo.methods.get(i),i,sb1,sb2,marks);
            }

            str=str.Replace("$1",StringBuilderPool.releaseStr(sb1));
            str=str.Replace("$2",StringBuilderPool.releaseStr(sb2));

            FileUtils.writeFileForUTF(path + "/" + clsInfo.clsName + "Adapter.cs",str);

            writer.Append("appdomain.RegisterCrossBindingAdaptor(new " + clsInfo.clsName + "Adapter());");
            endClsLine(writer);
        }

        private static void endClsLine(StringBuilder sb)
        {
            if(_isShine)
                endLine(sb);
            else
                endLine(sb,-1);
        }

        private static void endLine(StringBuilder sb)
        {
            sb.Append(Enter);
            sb.Append(Tab);
            sb.Append(Tab);
            sb.Append(Tab);
        }

        private static void endLine(StringBuilder sb,int off)
        {
            sb.Append(Enter);

            int len=3 + off;

            for(int i=0;i<len;i++)
            {
                sb.Append(Tab);
            }
        }

        /** 执行一个方法 */
        private static void doOneMethod(ILMethodInfo method,int index,StringBuilder sb1,StringBuilder sb2,IntSet marks)
        {
            int aSize;

            if((aSize=method.args.size())>0)
            {
                if(!marks.contains(aSize))
                {
                    marks.add(aSize);

                    sb1.Append("private object[] _p" + aSize + "=new object[" + aSize + "];");
                    endLine(sb1);
                    endLine(sb1);
                }
            }

            sb2.Append("IMethod _m" + index + ";");
            endLine(sb2);
            sb2.Append("bool _g" + index + ";");
            endLine(sb2);

            if(method.needBaseCall)
            {
                sb2.Append("bool _b" + index + ";");
                endLine(sb2);
            }

            switch(method.visitType)
            {
                case VisitType.Private:
                {
                    //private不写
                }
                    break;
                case VisitType.Public:
                {
                    sb2.Append("public ");
                    sb2.Append("override ");
                }
                    break;
                case VisitType.Protected:
                {
                    sb2.Append("protected ");
                    sb2.Append("override ");
                }
                    break;
            }

            bool needReturn=!string.IsNullOrEmpty(method.returnType);

            if(!needReturn)
            {
                sb2.Append("void");
            }
            else
            {
                sb2.Append(method.returnType);
            }

            sb2.Append(" " + method.name + "(");

            MethodArgInfo arg;

            for(int i=0;i<method.args.size();i++)
            {
                arg=method.args[i];

                if(i>0)
                    sb2.Append(",");

                if(arg.isRef)
                    sb2.Append("ref ");

                if(arg.isOut)
                    sb2.Append("out ");

                sb2.Append(arg.type);
                sb2.Append(" ");
                sb2.Append(arg.name);
            }

            sb2.Append(")");
            endLine(sb2);
            sb2.Append("{");
            endLine(sb2,1);

            sb2.Append("if(!_g" + index + ")");
            endLine(sb2,1);
            sb2.Append("{");
            endLine(sb2,2);
            sb2.Append("_m" + index + "=instance.Type.GetMethod(\"" + method.name + "\"," + aSize + ");");
            endLine(sb2,2);
            sb2.Append("_g" + index + "=true;");
            endLine(sb2,1);
            sb2.Append("}");
            endLine(sb2,1);
            endLine(sb2,1);

            sb2.Append("if(_m" + index + "!=null");

            if(method.needBaseCall)
            {
                sb2.Append(" && !_b" + index);
            }

            sb2.Append(")");
            endLine(sb2,1);
            sb2.Append("{");
            endLine(sb2,2);

            if(method.needBaseCall)
            {
                sb2.Append("_b" + index + "=true;");
                endLine(sb2,2);
            }

            if(aSize>0)
            {
                for(int i=0;i<aSize;i++)
                {
                    sb2.Append("_p" + aSize + "[" + i + "]=" + method.args.get(i).name + ";");
                    endLine(sb2,2);
                }
            }

            if(needReturn)
            {
                sb2.Append(method.returnType + " re=(" + method.returnType + ")");
            }

            sb2.Append("appdomain.Invoke(_m" + index + ",instance,");

            if(aSize>0)
            {
                sb2.Append("_p" + aSize);
            }
            else
            {
                sb2.Append("null");
            }

            sb2.Append(");");
            endLine(sb2,2);

            if(aSize>0)
            {
                for(int i=0;i<aSize;i++)
                {
                    sb2.Append("_p" + aSize + "[" + i + "]=null;");
                    endLine(sb2,2);
                }
            }

            if(method.needBaseCall)
            {
                sb2.Append("_b" + index + "=false;");
                endLine(sb2,2);
            }


            if(needReturn)
            {
                sb2.Append("return re;");
                endLine(sb2,2);
            }

            endLine(sb2,1);
            sb2.Append("}");

            if(method.needBaseCall || needReturn)
            {
                endLine(sb2,1);
                sb2.Append("else");
                endLine(sb2,1);
                sb2.Append("{");
                endLine(sb2,2);

                if(needReturn)
                {
                    sb2.Append("return ");
                }

                sb2.Append("base." + method.name + "(");

                for(int i=0;i<aSize;i++)
                {
                    arg=method.args.get(i);

                    if(i>0)
                        sb2.Append(",");

                    if(arg.isRef)
                        sb2.Append("ref ");

                    if(arg.isOut)
                        sb2.Append("out ");

                    sb2.Append(arg.name);
                }

                sb2.Append(");");
                endLine(sb2,1);
                sb2.Append("}");
            }

            endLine(sb2);
            sb2.Append("}");
            endLine(sb2);
            endLine(sb2);
        }

        private static void initGenerate()
        {
            _templete=FileUtils.readFileForUTF("Assets/src/shine/ILRuntime/adapterTemplate.txt");
        }

        /** 允许的命名空间 */
        private static SSet<string> _allowNameSpace=new SSet<string>();
        /** 类信息 */
        private static SMap<string,TypeInfo> _typeDic=new SMap<string,TypeInfo>();
        /** 类信息 */
        private static SMap<string,string> _clsNameToPathDic=new SMap<string,string>();
        /** 需要工厂方法类信息 */
        private static SSet<string> _adapterClsDic=new SSet<string>();
        /** 需要工厂方法类信息 */
        private static SSet<string> _factoryClsDic=new SSet<string>();
        /** 类忽略方法 */
        private static SMap<string,SSet<string>> _clsIgnoreMethods=new SMap<string,SSet<string>>();
        /** 类允许方法 */
        private static SMap<string,SSet<string>> _clsAllowMethods=new SMap<string,SSet<string>>();

        private const int Project_Common=1;
        private const int Project_Game=2;
        private const int Project_Hotfix=3;

        private static void clearGenerate()
        {
            _allowNameSpace.clear();
            _typeDic.clear();
            _clsNameToPathDic.clear();
            _adapterClsDic.clear();
            _factoryClsDic.clear();
            _clsIgnoreMethods.clear();
            _clsAllowMethods.clear();

        }

        /** 类型信息 */
        private class TypeInfo
        {
            /** 类名 */
            public string clsName;
            /** 类路径 */
            public string clsPath;
            /** 类内容 */
            public Type clsType;
            /** ILClass信息 */
            public ILClassInfo iCls;
            /** 基类信息 */
            public TypeInfo baseTypeInfo;
            /** 是否有热更标记 */
            public bool hasHotfixMark;
            /** 是否需要生成工厂方法*/
            public bool needFactory;

            private bool _inited=false;

            public void init()
            {
                if(_inited)
                    return;

                _inited=true;

                iCls=new ILClassInfo(clsName);

                Type baseType=clsType.BaseType;

                if(baseType!=null)
                {
                    baseTypeInfo=getTypeInfo(baseType.Name);

                    if(baseTypeInfo!=null)
                    {
                        // //类型居然不匹配
                        // if(bInfo.clsType!=baseType)
                        // {
                        //     Ctrl.throwError("类型居然不匹配",baseType.Name);
                        //     return;
                        // }

                        //从基类传递
                        hasHotfixMark=baseTypeInfo.hasHotfixMark;
                        needFactory=baseTypeInfo.needFactory;

                        //添加基类进来
                        foreach(ILMethodInfo ilMethodInfo in baseTypeInfo.iCls.methods)
                        {
                            if(ilMethodInfo.isAbstract)
                            {
                                doAddMethod(ilMethodInfo.clone());
                            }
                            else
                            {
                                doAddMethod(ilMethodInfo);
                            }
                        }
                    }
                }

                //接口和枚举除外
                if(!clsType.IsInterface && !clsType.IsEnum)
                {
                    //是否虚类
                    iCls.isAbstract=clsType.IsAbstract;

                    bool hotfixMark=clsType.GetCustomAttribute<Hotfix>()!=null;
                    bool adapterMark=clsType.GetCustomAttribute<HotfixAdapter>()!=null;
                    //热更标记
                    hasHotfixMark=hotfixMark || adapterMark;
                    needFactory=hotfixMark;

                    MethodInfo[] methodInfos=clsType.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);

                    foreach(MethodInfo methodInfo in methodInfos)
                    {
                        //自己声明的
                        if(!methodInfo.IsPrivate && !methodInfo.IsStatic && !methodInfo.IsFinal && (methodInfo.IsVirtual || methodInfo.IsAbstract))
                        {
                            switch(methodInfo.Name)
                            {
                                case "Equals":
                                case "GetHashCode":
                                case "ToString":
                                case "Finalize":
                                {

                                }
                                    break;
                                default:
                                {
                                    ILMethodInfo iMethod=new ILMethodInfo();
                                    iMethod.name=methodInfo.Name;
                                    iMethod.visitType=methodInfo.IsPublic ? VisitType.Public : VisitType.Protected;
                                    iMethod.isAbstract=methodInfo.IsAbstract;
                                    iMethod.needBaseCall=!methodInfo.IsAbstract;//虚函数不需要
                                    iMethod.returnType=getTypeName(methodInfo.ReturnType);

                                    foreach(ParameterInfo parameterInfo in methodInfo.GetParameters())
                                    {
                                        MethodArgInfo iArg=new MethodArgInfo();
                                        iArg.name=parameterInfo.Name;
                                        iArg.type=getTypeName(parameterInfo.ParameterType);
                                        iArg.isOut=parameterInfo.IsOut;
                                        iArg.isRef=parameterInfo.ParameterType.Name.EndsWith("&");
                                        // iArg.autoLength=parameterInfo.IsOptional;
                                        iArg.defaultValue=parameterInfo.DefaultValue!=null ? parameterInfo.DefaultValue.ToString() : "";
                                        iMethod.args.add(iArg);
                                    }

                                    //TODO:还是采用clone的方式，来对needBaseCall赋值

                                    if(methodInfo.DeclaringType==clsType)
                                    {
                                        doAddMethod(iMethod);
                                    }
                                    else
                                    {
                                        if(methodInfo.IsAbstract)
                                        {
                                            iCls.methodKeys.get(iMethod.getKey()).needBaseCall=false;
                                        }
                                    }
                                        break;
                                }
                            }
                        }
                    }
                }


            }

            private void doAddMethod(ILMethodInfo method)
            {
                SSet<string> allowSet=_clsAllowMethods.get(iCls.clsName);

                //不在允许列表
                if(allowSet!=null && !allowSet.contains(method.name))
                    return;

                SSet<string> ignoreSet=_clsIgnoreMethods.get(iCls.clsName);

                //在允屏蔽列表
                if(ignoreSet!=null && ignoreSet.contains(method.name))
                    return;

                iCls.toAddMethod(method);
            }

            public bool isEmpty()
            {
                return iCls.methods.isEmpty();
            }

            public XML writeXML()
            {
                XML re=new XML();
                re.name="cls";
                re.setProperty("name",iCls.clsName);

                foreach(ILMethodInfo ilMethodInfo in iCls.methods)
                {
                    XML mXML=new XML();
                    mXML.name="method";
                    mXML.setProperty("name",ilMethodInfo.name);
                    mXML.setProperty("returnType",ilMethodInfo.returnType);
                    mXML.setProperty("visitType",ilMethodInfo.visitType.ToString());

                    foreach(MethodArgInfo argInfo in ilMethodInfo.args)
                    {
                        XML aXML=new XML();
                        aXML.name="arg";
                        aXML.setProperty("name",argInfo.name);
                        aXML.setProperty("type",argInfo.type);
                        aXML.setProperty("defaultValue",argInfo.defaultValue);
                        aXML.setProperty("autoLength",argInfo.autoLength ? "true" : "false");
                        aXML.setProperty("isOut",argInfo.isOut ? "true" : "false");
                        aXML.setProperty("isRef",argInfo.isRef ? "true" : "false");

                        mXML.appendChild(aXML);
                    }

                    re.appendChild(mXML);
                }

                return re;
            }
        }

        private static TypeInfo getTypeInfo(string name)
        {
            TypeInfo typeInfo=_typeDic.get(name);

            if(typeInfo!=null)
                typeInfo.init();

            return typeInfo;
        }

        /** 获取类型显示名 */
        private static string getTypeName(Type type)
        {
            string name=type.Name;

            //ref
            if(name.EndsWith("&"))
                name=name.Substring(0,name.Length - 1);

            int index=name.IndexOf('`');

            if(index!=-1)
            {
                StringBuilder sb=new StringBuilder(name.Substring(0,index));
                sb.Append('<');

                Type[] args=type.GenericTypeArguments;

                for(int i=0;i<args.Length;i++)
                {
                    if(i>0)
                        sb.Append(',');

                    sb.Append(getTypeName(args[i]));
                }

                sb.Append('>');

                return sb.ToString();
            }

            return transType(name);
        }

        /** 类型转换 */
        private static string transType(string type)
        {
            if(type.EndsWith("[]"))
            {
                return transType(type.Substring(0,type.Length - 2)) + "[]";
            }

            switch(type)
            {
                case "String":
                    return "string";
                case "Object":
                    return "object";
                case "Int32":
                    return "int";
                case "Int64":
                    return "long";
                case "Single":
                    return "float";
                case "Double":
                    return "double";
                case "Boolean":
                    return "bool";
                case "Void":
                    return "";
            }

            return type;
        }

        private static string getProjectMark(int projectType)
        {
            switch(projectType)
            {
                case Project_Common:
                    return "";
                case Project_Game:
                    return "G";
                case Project_Hotfix:
                    return "H";
            }

            return "";
        }

        private static string getFactoryClsPath(int projectType)
        {
            switch(projectType)
            {
                case Project_Common:
                    return "Assets/src/commonGame/control/GameFactoryControl.cs";
                case Project_Game:
                    return "Assets/src/game/control/GGameFactoryControl.cs";
                case Project_Hotfix:
                    return "../hotfix/src/hotfix/control/HGameFactoryControl.cs";
            }

            return "";
        }

        private static void doOneGenerate(string xmlPath,string controlClsPath,string adapterOutPath,int projectType)
        {
            clearGenerate();

            _isShine=projectType==0;

            //TODO:根据类型取父factory

            XML xml=FileUtils.readFileForXML(xmlPath);

            int aLen="Assets/".Length;

            //处理屏蔽
            foreach(XML xl in xml.getChildrenByName("ignoreMethod"))
            {
                string[] methods=xl.getProperty("methods").Split(',');

                SSet<string> dd=new SSet<string>();
                dd.addAll(methods);
                _clsIgnoreMethods.put(xl.getProperty("cls"),dd);
            }

            foreach(XML xl in xml.getChildrenByName("allowMethod"))
            {
                string[] methods=xl.getProperty("methods").Split(',');

                SSet<string> dd=new SSet<string>();
                dd.addAll(methods);
                _clsAllowMethods.put(xl.getProperty("cls"),dd);
            }

            //先处理包路径
            foreach(XML xl in xml.getChildrenByName("root"))
            {
                 SList<string> ignoreList=new SList<string>();

                 foreach(XML pXml in xl.getChildrenByName("ignorePath"))
                 {
                     ignoreList.add(pXml.getProperty("path"));
                 }

                StringIntMap hotfixPathDic=new StringIntMap();

                foreach(XML pXml in xl.getChildrenByName("hotfixPath"))
                {
                    hotfixPathDic.put(pXml.getProperty("path"),pXml.getProperty("needFactory").Equals("true") ? 1 : 0);
                }

                string rootPath="Assets/"+xl.getProperty("path");

                string[] deepFileList=FileUtils.getDeepFileList(rootPath,"cs");

                foreach(string s in deepFileList)
                {
                    //编辑器类的跳过
                    if(s.Contains("/Editor/"))
                        continue;

                    string tailPath=s.Substring(rootPath.Length+1);

                    bool failed=false;

                    //排除组
                    foreach(string ig in ignoreList)
                    {
                        if(tailPath.StartsWith(ig))
                        {
                            failed=true;
                            break;
                        }
                    }

                    if(failed)
                        continue;

                    string fQName=FileUtils.getFileFrontName(s.Substring(aLen));

                    String fName=FileUtils.getFileName(fQName);

                    _clsNameToPathDic.put(fName,fQName);

                    string[] keys=hotfixPathDic.getKeys();
                    int[] values=hotfixPathDic.getValues();
                    string k;
                    int v;

                    for(int i=keys.Length-1;i>=0;--i)
                    {
                    	if((k=keys[i])!=null)
                    	{
                    	    v=values[i];

	                        //需要
	                        if(tailPath.StartsWith(k))
	                        {
	                            _adapterClsDic.add(fName);

	                            if(v==1)
	                            {
	                                _factoryClsDic.add(fName);
	                            }

	                            break;
	                        }
                    	}
                    }
                }
            }

            //再处理命名空间
            foreach(XML xl in xml.getChildrenByName("namespace"))
            {
                _allowNameSpace.add(xl.getProperty("name"));
            }

            //项目程序集
            Assembly assembly=typeof(ShineSetup).Assembly;

            Type[] types=assembly.GetTypes();

            foreach(Type vType in types)
            {
                string fullName=vType.FullName;

                //内部类不统计
                if(!fullName.Contains("+"))
                {
                    //命名空间允许
                    if(string.IsNullOrEmpty(vType.Namespace) || _allowNameSpace.contains(vType.Namespace))
                    {
                        string vName=vType.Name;

                        int index=vName.IndexOf('`');

                        //去掉泛型标记
                        if(index>0)
                        {
                            vName=vName.Substring(0,index);
                        }

                        TypeInfo typeInfo=new TypeInfo();
                        typeInfo.clsName=vName;
                        typeInfo.clsType=vType;

                        if(_typeDic.contains(vName))
                        {
                            // Ctrl.print("类名重复",vName);
                            continue;
                        }

                        _typeDic.put(vName,typeInfo);
                        //类路径
                        typeInfo.clsPath=_clsNameToPathDic.get(vName);
                    }
                }
            }

            string clsStr=FileUtils.readFileForUTF(controlClsPath);

            string factoryClsPath=getFactoryClsPath(projectType);

            ClsAnalysis.FactoryClassInfo factoryClass=ClsAnalysis.readFactory(factoryClsPath);
            string projectMark=getProjectMark(projectType);

            string factoryClsName=FileUtils.getFileFrontName(FileUtils.getFileName(factoryClsPath));

            //遍历父
            for(int i=projectType-1;i>=Project_Common;i--)
            {
                ClsAnalysis.FactoryClassInfo parentFactoryCls=ClsAnalysis.readFactory(getFactoryClsPath(i));

                factoryClass.addParent(parentFactoryCls);
            }


            StringBuilder sb=new StringBuilder();
            endClsLine(sb);

            if(!_isShine)
            {
                sb.Append("base.initOtherAdapters(appdomain);");
                endClsLine(sb);
            }


            XML reXMl=new XML();
            reXMl.name="info";

            //工厂最后执行
            TypeInfo factoryTypeInfo=getTypeInfo(factoryClsName);

            _clsNameToPathDic.remove(factoryClsName);

            foreach(string k in _clsNameToPathDic.getSortedMapKeys())
            {
                TypeInfo typeInfo=getTypeInfo(k);

                if(typeInfo==null)
                {
                    Ctrl.throwError("不该找不到类型",k);
                }

                if(typeInfo.hasHotfixMark || !typeInfo.isEmpty())
                {
                    //需要工厂,并且不是虚类
                    if(!typeInfo.iCls.isAbstract && (typeInfo.needFactory || _factoryClsDic.contains(k)))
                    {
                        ClsAnalysis.FMethod method=factoryClass.addMethod(k,projectMark);

                        //补方法
                        factoryTypeInfo.iCls.addMethod("create"+method.name,method.returnType,VisitType.Public,true);
                    }

                    //需要适配器
                    if(typeInfo.hasHotfixMark || _adapterClsDic.contains(k))
                    {
                        doOneClass(typeInfo.iCls,adapterOutPath,_isShine,sb);
                    }

                    // reXMl.appendChild(typeInfo.writeXML());
                }
            }

            doOneClass(factoryTypeInfo.iCls,adapterOutPath,_isShine,sb);

            //去掉最后一个tab
            sb.Remove(sb.Length - 1,1);

            clsStr=replaceMethod(clsStr,"protected "+(_isShine ? "virtual" : "override")+" void initOtherAdapters(AppDomain appdomain)",sb.ToString());

            factoryClass.writeToPath(factoryClsPath);
            FileUtils.writeFileForUTF(controlClsPath,clsStr);

            // FileUtils.writeFileForXML("/Users/sunming/E/temp3/aa.xml",reXMl);
        }
    }
}

#endif