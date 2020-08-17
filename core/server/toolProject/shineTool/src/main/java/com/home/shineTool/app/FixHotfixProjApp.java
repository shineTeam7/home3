package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.VBoolean;
import com.home.shine.support.XML;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.global.ShineToolGlobal;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhm
 * @date 2018/06/30
 *
 * 修复c#热更工程的csproj
 */
public class FixHotfixProjApp
{
    private XML _gameProj;
    
    private SList<String> _dirKeys=new SList<>(String[]::new);
    
    private SMap<String,String> _dirPathDic=new SMap<>();
    
    private SList<XML> _references=new SList<>(XML[]::new);
    
    public FixHotfixProjApp()
    {
        _dirKeys.add("Managed");
        _dirKeys.add("game");
        _dirKeys.add("iOSSupport");
        _dirKeys.add("MonoBleedingEdge");
    }
    
    /**
     * 修复csproj中的compile itemGroup
     */
    public void fix()
    {
        String hotfixPath=ShineToolGlobal.clientHotfixSrcPath;
        
        File src = new File(hotfixPath);

        if (!src.exists())
        {
            Ctrl.throwError("hotfix目录不存在！");
            return;
        }
    
        loadUnityProj();
        
        String logicPath = FileUtils.fixPath(src.getParent());

        // 创建csproj.user
        createUserCsproj(logicPath);
    
        loadReference();
        
        String csprojPath = logicPath + "/hotfix.csproj";

        XML csproj = FileUtils.readFileForXML(csprojPath);
    
        XML reference=getItemGroup(csproj,"Reference");
        XML newReference=makeReferences();
        
        if(reference!=null)
        {
            csproj.replaceChild(reference,newReference);
        }
        else
        {
            csproj.appendChild(newReference);
        }
    
        XML compile=getItemGroup(csproj,"Compile");
        XML newCompile=makeCompiles(hotfixPath,logicPath);
        
        if(compile!=null)
        {
            csproj.replaceChild(compile,newCompile);
        }
        else
        {
            csproj.appendChild(newCompile);
        }

        FileUtils.writeFileForXML(csprojPath, csproj);
    
        Ctrl.print("Fix Over, OK!");
    }

    private void loadUnityProj()
    {
        File file = new File(ShineToolGlobal.clientPath + "/game/Assembly-CSharp.csproj");
    
        if (!file.exists())
        {
            Ctrl.throwError("请至少用Unity打开一次game工程，确保game/Assembly-CSharp.csproj文件存在");
            return;
        }
    
        _gameProj = FileUtils.readFileForXML(file.getPath());
    
        _dirKeys.forEach(v->
        {
            String onePath=findOnePath(v);
            
            if(onePath.isEmpty())
            {
               Ctrl.throwError("找不到Key",v);
            }
            else
            {
                _dirPathDic.put(v,onePath);
            }
        });
    }
    
    private String findOnePath(String key)
    {
        String useKey="/"+key+"/";
        for (XML xml : _gameProj.getChildrenByName("ItemGroup"))
        {
            SList<XML> references = xml.getChildrenByName("Reference");
        
            if (!references.isEmpty())
            {
                for (XML reference : references)
                {
                    XML hintPath=reference.getChildrenByNameOne("HintPath");
                
                    if(hintPath!=null)
                    {
                        String value=FileUtils.fixPath(hintPath.getValue());
    
                        int index=value.indexOf(useKey);
                        
                        if(index>0)
                        {
                            return value.substring(0,index+useKey.length()-1);
                        }
                    }
                }
            }
        }
        
        return "";
    }
    
    private XML getItemGroup(XML root,String childName)
    {
        for (XML xml : root.getChildrenByName("ItemGroup"))
        {
            if (xml.getChildrenByNameOne(childName)!=null)
            {
                return xml;
            }
        }
        
        return null;
    }
    
    private XML makeCompiles(String srcPath, String prePath)
    {
        List<File> csFiles = FileUtils.getDeepFileList(srcPath, "cs");
        // 排个序
        csFiles.sort(Comparator.comparing(File::getPath));
        
        XML re=new XML();
        re.setName("ItemGroup");
        
        for (File f : csFiles)
        {
            String absolutePath=f.getAbsolutePath();
            String fPath=FileUtils.fixPath(absolutePath.substring(prePath.length() + 1));
            
            XML d=new XML();
            d.setName("Compile");
            d.setProperty("Include",fPath);
            re.appendChild(d);
        }

        return re;
    }
    
    private XML makeReferences()
    {
        XML re=new XML();
        re.setName("ItemGroup");
    
        for(XML reference : _references)
        {
            re.appendChild(reference);
        }
        
        return re;
    }

    /**
     * 创建csproj.user
     */
    private void createUserCsproj(String logicPath)
    {
        String userPath=logicPath +"/hotfix.csproj.user";
    
        XML propertyGroup = new XML();
        propertyGroup.setName("PropertyGroup");
    
        Map<String, String> propertys = new HashMap<>();
        
        _dirPathDic.forEach((k,v)->
        {
            propertys.put(k+"Dir", v);
        });
        
        for (Map.Entry<String, String> kvs : propertys.entrySet())
        {
            XML xml = new XML();
            xml.setName(kvs.getKey());
            xml.setValue(kvs.getValue());
            propertyGroup.appendChild(xml);
        }
    
        XML userCsproj = new XML();
        userCsproj.setName("Project");
        userCsproj.setProperty("ToolsVersion", "15.0");
        userCsproj.setProperty("xmlns", "http://schemas.microsoft.com/developer/msbuild/2003");
        userCsproj.appendChild(propertyGroup);
    
        FileUtils.writeFileForXML(userPath, userCsproj);
    }
    
    private void addReference(String assembly,String path)
    {
        XML re=new XML();
        re.setName("Reference");
        re.setProperty("Include",assembly);
        
        XML hintPath=new XML();
        hintPath.setName("HintPath");
        hintPath.setValue(path);
        re.appendChild(hintPath);
        
        _references.add(re);
    }
    
    private void loadReference()
    {
        addReference("Assembly-CSharp","$(gameDir)/Library/ScriptAssemblies/Assembly-CSharp.dll");
        
        VBoolean vb=new VBoolean();
        
        for (XML xml : _gameProj.getChildrenByName("ItemGroup"))
        {
            SList<XML> references = xml.getChildrenByName("Reference");
        
            if (!references.isEmpty())
            {
                for (XML reference : references)
                {
                    XML hintPath=reference.getChildrenByNameOne("HintPath");
                
                    if(hintPath!=null)
                    {
                        String value=FileUtils.fixPath(hintPath.getValue());
    
                        vb.value=false;
                        
                        _dirPathDic.forEach((k,v)->
                        {
                            if(!vb.value && value.startsWith(v))
                            {
                                XML rc=reference.clone();
                                String newV="$("+k+"Dir)"+value.substring(v.length(),value.length());
    
                                XML hintPath2=rc.getChildrenByNameOne("HintPath");
                                hintPath2.setValue(newV);
                                
                                _references.add(rc);
                                vb.value=true;
                            }
                        });
                        
                        if(!vb.value)
                        {
                            Ctrl.print("未录入的Reference",value);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args)
    {
        ShineToolSetup.init();

        new FixHotfixProjApp().fix();
    }
}
