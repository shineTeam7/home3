package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.XML;
import com.home.shine.support.collection.SList;
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
    public void FixCsprojApp()
    {

    }

    /**
     * 修复csproj中的compile itemGroup
     */
    public void fix()
    {
        File src = new File(ShineToolGlobal.clientHotfixSrcPath);

        if (!src.exists())
        {
            Ctrl.throwError("hotfix目录不存在！");
            return;
        }

        String logicPath = src.getParent() + File.separator;

        // 创建csproj.user
        createUserCsproj(logicPath);

        String csprojPath = logicPath + "hotfix.csproj";

        XML csproj = FileUtils.readFileForXML(csprojPath);

        SList<XML> itemGroups = csproj.getChildrenByName("ItemGroup");
    
        boolean has=false;

        for (XML xml : itemGroups)
        {
            if (xml.getChildrenByNameOne("Compile")!=null)
            {
                csproj.replaceChild(xml, makeCompiles(src.getPath(), logicPath));
                has=true;
                break;
            }
        }

        if(!has)
        {
            csproj.appendChild(makeCompiles(src.getPath(), logicPath));
        }

        FileUtils.writeFileForXML(csprojPath, csproj);
    
        Ctrl.print("Fix Over, OK!");
    }

    private XML makeCompiles(String srcPath, String prePath)
    {
        List<File> csFiles = FileUtils.getDeepFileList(srcPath, "cs");

        StringBuilder content = new StringBuilder("<ItemGroup>");
        // 排个序
        csFiles.sort(Comparator.comparing(File::getPath));

        for (File f : csFiles)
        {
            String compile = '"' + f.getPath().replace(prePath, "") + '"';

            content.append("<Compile Include="+ compile +" />");
        }

        content.append("</ItemGroup>");

        return FileUtils.readXML(content.toString());
    }

    /**
     * 创建csproj.user
     */
    private void createUserCsproj(String logicPath)
    {
        File userFile = new File(logicPath + "hotfix.csproj.user");

        if (!userFile.exists())
        {
            XML propertyGroup = new XML();
            propertyGroup.setName("PropertyGroup");

            Map<String, String> propertys = new HashMap<>();
            propertys.put("ManagedDir", getUnityDir("Managed"));
            propertys.put("ClientDir", ShineToolGlobal.clientPath);
            propertys.put("UnityExtensionsDir", getUnityDir("UnityExtensions"));

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

            FileUtils.writeFileForXML(userFile.getPath(), userCsproj);
        }
    }

    /**
     * 获取Unity相关路径
     * 目前从game工程的csproj中获取，也可以通过设置系统环境变量，然后从系统环境变量中取
     * @return Unity相关路径，取不到返回null
     */
    private String getUnityDir(String dir)
    {
        File file = new File(ShineToolGlobal.clientPath + "/game/Assembly-CSharp.csproj");

        if (!file.exists())
        {
            Ctrl.throwError("请至少用Unity打开一次game工程，确保game/Assembly-CSharp.csproj文件存在");
            return null;
        }

        XML gameProj = FileUtils.readFileForXML(file.getPath());

        SList<XML> itemGroups = gameProj.getChildrenByName("ItemGroup");

        for (XML xml : itemGroups)
        {
            SList<XML> references = xml.getChildrenByName("Reference");

            if (!references.isEmpty())
            {
                for (XML reference : references)
                {
                    SList<XML> hintPaths = reference.getChildrenByName("HintPath");

                    for (XML hintPath : hintPaths)
                    {
                        String hitPathStr = hintPath.getValue();
                        if (hitPathStr != null && hitPathStr.contains(dir))
                        {
                            return hitPathStr.substring(0, hitPathStr.indexOf(dir) - 1) + File.separator + dir;
                        }
                    }
                }
            }
        }

        // 通过系统环境变量取
        // return System.getenv(dir);
        Ctrl.throwError("取不到Unity目录：" + dir + "; 请联系开发者");
        return null;
    }

    public static void main(String[] args)
    {
        ShineToolSetup.init();

        new FixHotfixProjApp().fix();
    }
}
