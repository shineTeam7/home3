namespace Shine
{
	export class FileUtils
	{
		/** 获取bundle资源使用名字 */
		// public static getBundleResourceUseName(name:string):string
		// {
		// 	return ShineSetting.bundleSourceHead + name.toLowerCase();
		// }
	
	    // //--文件读写--//
	
	    // /** 获取文件扩展名 */
	    // public static getFileExName(name: string): string 
	    // {
	    //     return name.slice(name.lastIndexOf('.') + 1).toLowerCase();
	    // }
	
	    // /** 获取文件前名 */
	    // public static getFileFrontName(name: string): string 
	    // {
	    //     return name.slice(0, name.lastIndexOf('.'));
	    // }
	
	    // /** 获取文件名 */
	    // public static getFileName(path: string): string 
	    // {
	    //     return path.slice(path.lastIndexOf('/') + 1);
	    // }
	
	    // /** 获取文件前名 */
	    // public static getFileNameWithoutEx(path: string): string 
	    // {
	    //     return this.getFileFrontName(this.getFileName(path));
	    // }
	
	    // /** 获取文件所在目录 */
	    // public static getFileDicPath(path: string): string 
	    // {
	    //     return path.slice(0, path.lastIndexOf('/'));
	    // }
	
	    // /** 写二进制文件 */
	    // public static writeFileForBytesWriteStream(path: string, stream: BytesWriteStream): void 
	    // {
	    //     this.surePath(path);
	
	    //     var fs: FileStream = new FileStream(path, FileMode.Create);
	
	    //     fs.Write(stream.getBuf(), 0, stream.length());
	
	    //     fs.Close();
	    // }
	
	    // /** 写二进制文件 */
	    // public static writeFileForBytesArr(path: string, bytes:ArrayBuffer): void 
	    // {
	    //     this.surePath(path);
	
	    //     var fs: FileStream = new FileStream(path, FileMode.Create);
	
	    //     fs.Write(bytes, 0, bytes.byteLength);
	    //     fs.Close();
	    // }
	
	    // /** 写二进制文件 */
	    // public static writeFileForBytes(path: string, bytes:BytesWriteStream): void 
	    // {
	    //     this.surePath(path);
	    //     var fs: FileStream = new FileStream(path, FileMode.Create);
	    //     fs.Write(bytes.getBuf(), 0, bytes.length());
	    //     fs.Close();
	    // }
	
	    // /** 写字符串文件 */
	    // public static writeFileForUTF(path: string, str: string): void 
	    // {
	    //     this.writeFileForBytes(path, Encoding.UTF8.GetBytes(str));
	    // }
	
	    // /** 写xml文件 */
	    // public static writeFileForXML(path: string, xml:XML): void 
	    // {
	    //     this.writeFileForUTF(path, xml.ToString());
	    // }
	
	    // /** 读取二进制文件 */
	    // public static readFileForBytes(path: string): ArrayBuffer 
	    // {
	    //     if (!File.Exists(path))
	    //         return null;
	
	    //     var fs: FileStream = new FileStream(path, FileMode.Open);
	
	    //     var bs: ArrayBuffer = new ArrayBuffer(fs.Length);
	
	    //     fs.Read(bs, 0, bs.byteLength);
	    //     fs.Close();
	    //     return bs;
	    // }
	
	    // /** 从文件读出字节读流 */
	    // public static readFileForBytesReadStream(path: string): BytesReadStream 
	    // {
	    //     if (!File.Exists(path))
	    //         return null;
	
	    //     return new BytesReadStream(this.readFileForBytes(path));
	    // }
	
	    // /** 读字符串文件 */
	    // public static readFileForUTF(path: string): string 
	    // {
	    //     if (!File.Exists(path))
	    //         return "";
	
	    //     var b: ArrayBuffer = this.readFileForBytes(path);
	
	    //     return Encoding.UTF8.GetString(b, 0, b.byteLength);
	    // }
	
	    // /** 读xml */
	    // public static readFileForXML(path: string): XML 
	    // {
	    //     if (!File.Exists(path))
	    //         return null;
	
	    //     return XML.readXMLByString(this.readFileForUTF(path));
	    // }
	
	    // //--Resources读取--//
	
	    // /** 从Resources目录读取资源 */
	    // public static readResourceForUTF(path: string): string 
	    // {
	    //     var text: TextAsset = Resources.Load<TextAsset>(this.getFileFrontName(path));
	
	    //     if (text == null)
	    //         return "";
	
	    //     return text.text;
	    // }
	
	    // /** 从Resources目录读取xml */
	    // public static readResourceForXML(path: string): XML 
	    // {
	    //     return XML.readXMLByString(this.readResourceForUTF(path));
	    // }
	
	    // /** 文件是否存在 */
	    // public static fileExists(path: string): void 
	    // {
	    //     return File.Exists(path);
	    // }
	
	    // /** 删除文件 */
	    // public static deleteFile(path: string):boolean 
	    // {
	    //     if (File.Exists(path)) {
	    //         File.Delete(path);
	    //         return true;
	    //     }
	
	    //     return false;
	    // }
	
	    // /** 清空文件夹 */
	    // public static clearDir(path: string): void 
	    // {
	    //     if (!Directory.Exists(path))
	    //         return;
	
	    //     var files: string[] = Directory.GetFiles(path);
	
	    //     for (var i: number = 0; i < files.length; i++) 
	    //     {
	    //         File.Delete(files[i]);
	    //     }
	
	    //     var directories: string[] = Directory.GetDirectories(path);
	
	    //     for (var i: number = 0; i < directories.length; i++) 
	    //     {
	    //         var directory: string = directories[i];
	    //         this.clearDir(directory);
	    //         Directory.Delete(directory);
	    //     }
	    // }
	
	    // //--path相关--//
	
	    // /** 路径修复(\to/) */
	    // public static fixPath(path: string): string 
	    // {
	    //     return path.replace('\\', '/');
	    // }
	
	    // /** 取完整修复路径 */
	    // public static fixAndFullPath(path: string): string 
	    // {
	    //     return this.fixPath(Path.GetFullPath(path));
	    // }
	
	    // /** 将两个路径结合起来,支持/.. */
	    // public static subPath(path: string, part: string): string 
	    // {
	    //     return this.fixPath(Path.GetFullPath(path + part));
	    // }
	
	    // // /** 递归获取文件列表(fix过的) */
	    // // public static getDeepFileList(path:string):string[]
	    // // {
	    // // 	var re:string[]=Directory.GetFiles(path,"*.*",SearchOption.AllDirectories);
	
	    // // 	for(var i:number=re.length - 1;i>=0;--i)
	    // // 	{
	    // // 		re[i]=this.fixPath(re[i]);
	    // // 	}
	
	    // // 	return re;
	    // // }
	
	    // /** 递归获取文件列表(fix过的) */
	    // public static getDeepFileList(path: string, exName: string = ""): string[] 
	    // {
	    //     var re: string[] = Directory.GetFiles(path, "*." + exName, SearchOption.AllDirectories);
	
	    //     for (var i: number = re.length - 1; i >= 0; --i) {
	    //         re[i] = this.fixPath(re[i]);
	    //     }
	
	    //     return re;
	
	    // }
	
	    // /** 递归获取文件列表（多格式） */
	    // public static getDeepFileMutipleList(path: string, ...exNames: string[]): SList<string> 
	    // {
	    //     var re: SList<string> = new SList<string>();
	
	    //     exNames.forEach(exName => {
	    //         var files: string[] = Directory.GetFiles(path, "*." + exName, SearchOption.AllDirectories);
	
	    //         for (var i: number = files.length - 1; i >= 0; --i) {
	    //             if (files[i].EndsWith(".DS_Store"))
	    //                 continue;
	
	    //             re.add(this.fixPath(files[i]));
	    //         }
	    //     });
	
	
	    //     return re;
	    // }
	
	    // /** 递归获取所有文件列表(excludes:排除扩展名) */
	    // public static getDeepFileListForIgnore(path: string, ...exIgnore: string[]): SList<string> 
	    // {
	    //     var files: string[] = Directory.GetFiles(path, "*", SearchOption.AllDirectories);
	
	    //     var re: SList<string> = new SList<string>();
	
	    //     var s: string;
	
	    //     for (var i: number = files.length - 1; i >= 0; --i) 
	    //     {
	    //         s = files[i];
	
	    //         if (s.EndsWith(".DS_Store"))
	    //             continue;
	
	    //         if (exIgnore.length > 0) {
	    //             if (Array.IndexOf(exIgnore, this.getFileExName(s)) != -1) 
	    //             {
	    //                 continue;
	    //             }
	    //         }
	
	    //         re.add(this.fixPath(s));
	    //     }
	
	    //     return re;
	    // }
	
		// 	/** 获取文件的资源类型  */
		// public static getFileResourceType(url:string):number
		// {
		// 		//先通过扩展名
		// 	var exName:string=this.getFileExName(url);
	
		// 	var re:number=0;
	
		// 	switch(exName)
		// 	{
	    //         case "xml":
	    //         {
	    //             re=ResourceType.XML;
	    //         }
		// 			break;
		// 	}
	
		// 	return re;
		// }
	
		// /** 获取文件的md5值 */
		// public static getFileMD5(path:string):string
		// {
		// 	var bytes:ArrayBuffer =this.readFileForBytes(path);
	
		// 	if(bytes==null)
		// 		return "";
	
		// 	return StringUtils.md5(bytes);
		// }
	
	    // private static surePath(path: string): void
	    //  {
	    //     var index: number = path.lastIndexOf("/");
	
	    //     if (index != -1) 
	    //     {
	    //         var temp: string = path.substring(0, index);
	
	    //         if (!Directory.Exists(temp))
	    //             Directory.CreateDirectory(temp);
	    //     }
	    // }
	
		// /** 拷贝文件(会创建对应文件夹) */
		// public static  copyFile(srcPath:string,targetPath:string):void
		// {
		// 	this.surePath(targetPath);
		// 	File.Copy(srcPath,targetPath,true);
		// }
	}
}