using ShineEngine;
using System;

[Hotfix]
public class ConfigControl
{
	/** 拆分的配置部分 版本号 */
	public int splitConfigVersion=1;

	/** 是否初始化中 */
	private bool _initing=false;

	private Action _overCall;
	
	private ShineLoader _loader;

	private ConfigReadData _useData;

	private ConfigReadData _readData;

	private BytesReadStream _tempStream=new BytesReadStream();

	public virtual void preInit()
	{
		//常量构造
		_useData=GameC.factory.createConfigReadData();
		_useData.makeConstSize();
	}

	/** 加载配置 */
	public void load(Action overCall)
	{
		_overCall=overCall;

		_loader=new ShineLoader();
		_loader.setCompleteCall(loadOver);
		_loader.loadResource(ShineGlobal.configPath);
	}
	
	private void loadOver()
	{
		byte[] bytes=_loader.getBytes();

		_loader.unload();

		loadBytes(bytes,_overCall);
	}

	/** 从字节流读取 */
	public void loadBytes(byte[] bytes,Action over)
	{
		_overCall=over;
		
		long t=Ctrl.getTimer();

		ThreadControl.addAssistFunc(()=>
		{
			toRead(new BytesReadStream(bytes));

			ThreadControl.addMainFunc(()=>
			{
				afterReadConfig(_readData);

				if(ShineSetting.needDebugLog)
					Ctrl.debugLog("配置表加载耗时",Ctrl.getTimer()-t);

				Action func=_overCall;
				_overCall=null;

				if(func!=null)
					func();
			});
		});
	}

	private ConfigReadData toRead(BytesReadStream stream)
	{
		ConfigReadData data=doRead(stream);

		if(data==null)
		{
			ShineSetup.exit();
			return null;
		}

		_readData=data;
		return data;
	}

	private ConfigReadData doRead(BytesReadStream stream)
	{
		if(CommonSetting.configNeedCompress)
			stream.unCompress();

		if(!stream.checkVersion(ShineGlobal.configVersion))
		{
			Ctrl.errorLog("config结构版本不对");
			return null;
		}

		if(!checkStream(stream))
		{
			return null;
		}

		ConfigReadData data=GameC.factory.createConfigReadData();
		data.readBytes(stream);
		return data;
	}

	/** 同步加载配置(Editor用) */
	public void loadSyncForEditor()
	{
		string path=ResourceInfoControl.getStreamingAssetsPath(ShineGlobal.configForEditorPath,false);

		BytesReadStream stream=FileUtils.readFileForBytesReadStream(path);

		if(stream==null)
		{
			Ctrl.throwError("未找到"+ShineGlobal.configForEditorPath+",需要执行configExport");
		}
		else
		{
			afterReadConfig(toRead(stream));
		}
	}

	/** 同步加载全部配置(Editor用)(场景编辑器) */
	public void loadSyncForEditorAll()
	{
		string path=ResourceInfoControl.getStreamingAssetsPath(ShineGlobal.configPath,false);

		BytesReadStream stream=FileUtils.readFileForBytesReadStream(path);

		if(stream==null)
		{
			Ctrl.throwError("未找到"+ShineGlobal.configPath+",需要执行configExport");
		}
		else
		{

			afterReadConfig(toRead(stream));
		}
	}

	public string getSplitConfigPath(string configName,int key)
	{
		return ShineGlobal.configDirPath + "/" + configName + "/" + key + ".bin";
	}

	public void loadSplit(int type,string configName,int key,Action<BaseConfig> overFunc)
	{
		int configResourceID=LoadControl.getResourceIDByNameAbs(getSplitConfigPath(configName,key));

		LoadControl.loadOne(configResourceID,()=>
		{
			byte[] bytes=(byte[])LoadControl.getResource(configResourceID);

			BytesReadStream stream=_tempStream;

			stream.setBuf(bytes);

			if(CommonSetting.configNeedCompress)
				stream.unCompress();

			if(!stream.checkVersion(ShineGlobal.configVersion))
			{
				Ctrl.errorLog("config结构版本不对");
				return;
			}

			if(!checkSplitStream(stream))
			{
				return;
			}

			BaseConfig bConfig=_useData.readBytesOneSplit(type,stream);

			overFunc(bConfig);
		});
	}

	public BaseConfig getSplitConfigSync(int type,string configName,int key)
	{
		int configResourceID=LoadControl.getResourceIDByNameAbs(getSplitConfigPath(configName,key));

		byte[] bytes=(byte[])LoadControl.getResource(configResourceID);

		if(bytes==null)
			return null;

		BytesReadStream stream=_tempStream;

		stream.setBuf(bytes);

		if(CommonSetting.configNeedCompress)
			stream.unCompress();

		if(!stream.checkVersion(ShineGlobal.configVersion))
		{
			Ctrl.errorLog("config结构版本不对");
			return null;
		}

		if(!checkSplitStream(stream))
		{
			return null;
		}

		BaseConfig bConfig=_useData.readBytesOneSplit(type,stream);

		return bConfig;
	}

	/** 卸载 */
	public void unloadSplit(string configName,int key)
	{
		string path=ShineGlobal.configDirPath + "/" + configName + "/" + key + ".bin";
		int configResourceID=LoadControl.getResourceIDByNameAbs(path);
		LoadControl.unloadOne(configResourceID);
	}

	/** 读取热更配置 */
	public void loadHotfix(byte[] bytes)
	{
		if(bytes==null)
			return;

		ConfigReadData data=doRead(new BytesReadStream(bytes));

		if(data==null)
		{
			Ctrl.warnLog("configLoadHotfix失败");
			return;
		}

		afterReadConfigForHotfix(data);

		Ctrl.print("ok");
	}


	public virtual int getMsgDataVersion()
	{
		return 1;
	}

	/** 存本地数据版本 */
	public virtual int getDBDataVersion()
	{
		return 1;
	}

	/** 检查客户端game配置版本 */
	protected virtual int getGameConfigVersion()
	{
		return 0;
	}

	/** 检查客户端热更代码版本 */
	protected virtual int getHotfixConfigVersion()
	{
		return 0;
	}

	public bool isIniting()
	{
		return _initing;
	}

	private bool checkStream(BytesReadStream stream)
	{
		//代码版本校验
		if(CodeCheckRecord.configVersion!=stream.readInt())
		{
			Ctrl.throwError("common代码版本与配置文件版本不一致,请刷新工程或重新导入配置(执行configExport)");
			return false;
		}

		if(getGameConfigVersion()!=stream.readInt())
		{
			Ctrl.throwError("game代码版本与配置文件版本不一致,请刷新工程或重新导入配置(执行configExport)");
			return false;
		}

		if(CommonSetting.clientNeedHotfix)
		{
			if(getHotfixConfigVersion()!=stream.readInt())
			{
				Ctrl.throwError("hotfix代码版本与配置文件版本不一致,请刷新工程或重新导入配置(执行configExport)");
				return false;
			}
		}

		return true;
	}

	private bool checkSplitStream(BytesReadStream stream)
	{
		if(splitConfigVersion!=stream.readInt())
		{
			Ctrl.throwError("split版本与配置文件版本不一致,需要执行SceneEditor重新导入");
			return false;
		}

		return true;
	}

	private void afterReadConfig(ConfigReadData data)
	{
		_initing=true;
		data.setToConfig();

		data.refreshData();

		SensitiveWordConfig.init();
		refreshConfig();
		AttributeControl.init();

		data.afterReadConfigAll();
		_initing=false;
	}

	private void afterReadConfigForHotfix(ConfigReadData data)
	{
		data.addToConfig();
		data.refreshData();
		data.afterReadConfigAll();
	}

	/** 刷新配置 */
	public virtual void refreshConfig()
	{
		TextEnum.readConfig();

		BaseGameUtils.initStrFilter(SensitiveWordConfig.getWordList(CommonSetting.languageType));
	}

	/** 刷新配置(在更改语言后) */
	public virtual void refreshConfigForLanguage()
	{
		_readData.refreshData();

		refreshConfig();
		
		_readData.afterReadConfigAll();
	}

	public void writeConfigVersion(BytesWriteStream stream)
	{
		stream.writeVersion(ShineGlobal.configVersion);
		stream.writeInt(CodeCheckRecord.configVersion);
		stream.writeInt(getGameConfigVersion());

		if(CommonSetting.clientNeedHotfix)
		{
			stream.writeInt(getHotfixConfigVersion());
		}
	}

	public void writeSplitConfigVersion(BytesWriteStream stream)
	{
		stream.writeVersion(ShineGlobal.configVersion);
		stream.writeInt(splitConfigVersion);
	}
}
