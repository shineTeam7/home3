package com.home.commonBase.control;

import com.home.commonBase.config.base.ConfigReadData;
import com.home.commonBase.config.game.SensitiveWordConfig;
import com.home.commonBase.constlist.generate.TextEnum;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.shine.ShineSetup;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;

/** 配置控制(也做Data的版本校验) */
public class ConfigControl
{
	/** 只初始化一遍 */
	private boolean _inited=false;
	/** 初始化热更 */
	private boolean _initHotfix=false;
	
	/** 是否初始化中 */
	private boolean _initing=false;
	
	/** 配置增量md5 */
	private String incrementMD5="";
	
	/** 配置热更数组 */
	private byte[] _hotfixBytes=null;
	
	public ConfigControl()
	{
		//instance=this;
	}
	
	public void init()
	{
		if(_inited)
			return;
		
		_inited=true;
		
		if(CommonSetting.needConfigIncrement)
		{
			incrementMD5=StringUtils.md5(FileUtils.readFileForBytes(ShineGlobal.configIncrementFilePath));
		}
		
		long startTime=0L;
		
		if(ShineSetting.needDetailLog)
		{
			startTime=Ctrl.getTimer();
		}
		
		BytesReadStream stream=FileUtils.readFileForBytesReadStream(ShineGlobal.configMainFilePath);
		
		//解压缩
		if(CommonSetting.configNeedCompress)
			stream.unCompress();
		
		if(!stream.checkVersion(ShineGlobal.configVersion))
		{
			ShineSetup.exit("config结构版本不对");
			return;
		}
		
		if(!checkStream(stream))
		{
			ShineSetup.exit();
			return;
		}
		
		//解析
		ConfigReadData readData=BaseC.factory.createConfigReadData();
		readData.readBytes(stream);
		
		_initing=true;
		setConfig(readData);
		_initing=false;
		
		if(ShineSetting.needDetailLog)
		{
			Ctrl.runningLog("初始化配置耗时:" + (Ctrl.getTimer() - startTime) + "ms");
		}
	}
	
	/** 初始化热更 */
	public void initHotfix()
	{
		if(_initHotfix)
			return;
		
		_initHotfix=true;
		
		_hotfixBytes=FileUtils.readFileForBytes(ShineGlobal.configHotfixFilePath);
	}
	
	public void dispose()
	{
	
	}
	
	public boolean isIniting()
	{
		return _initing;
	}
	
	/** 获取热更数据 */
	public byte[] getHotfixBytes()
	{
		return _hotfixBytes;
	}
	
	/** 构造常量 */
	public void preInit()
	{
		//常量构造
		BaseC.factory.createConfigReadData().makeConstSize();
	}
	
	/** 检查客户端game配置版本 */
	protected int getGameConfigVersion()
	{
		return 0;
	}
	
	/** 检查客户端热更代码版本 */
	protected int getHotfixConfigVersion()
	{
		return 0;
	}
	
	private boolean checkStream(BytesReadStream stream)
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
	
	/** 检查子工程代码版本 */
	public void reload(Runnable resumeFunc,Runnable overCall)
	{
		ThreadControl.addMixedFunc(()->
		{
			byte[] serverBytes=FileUtils.readFileForBytes(CommonSetting.needConfigIncrement ? ShineGlobal.configIncrementFilePath : ShineGlobal.configMainFilePath);
			
			byte[] hotfixBytes=FileUtils.readFileForBytes(ShineGlobal.configHotfixFilePath);
			
			//没变化
			if(CommonSetting.needConfigIncrement && StringUtils.md5(serverBytes).equals(incrementMD5))
			{
				ThreadControl.addMainFunc(()->
				{
					if(overCall!=null)
						overCall.run();
				});
			}
			else
			{
				BytesReadStream stream=BytesReadStream.create(serverBytes);
				
				//解压缩
				if(CommonSetting.configNeedCompress)
					stream.unCompress();
				
				if(!stream.checkVersion(ShineGlobal.configVersion))
				{
					ShineSetup.exit("config结构版本不对");
					return;
				}
				
				if(!checkStream(stream))
				{
					Ctrl.warnLog("配置md5检查未通过");
					return;
				}
				
				//解析
				ConfigReadData data=BaseC.factory.createConfigReadData();
				
				data.readBytes(stream);
				
				ThreadControl.pauseAllLogicThread(()->
				{
					setConfig(data);
					
					_hotfixBytes=hotfixBytes;
					
					ThreadControl.resumePauseAllLogicThread(resumeFunc);
					
					ThreadControl.addMainFunc(()->
					{
						if(overCall!=null)
							overCall.run();
					});
				});
			}
		});
	}
	
	/** 数据库数据结构版本 */
	public int getDBDataVersion()
	{
		return 1;
	}
	
	/** 获取数据结构版本 */
	public int getMsgDataVersion()
	{
		return 1;
	}
	
	/** 读完配置后续补充(暂停中) */
	private void setConfig(ConfigReadData data)
	{
		data.setToConfig();
		data.refreshData();
		
		SensitiveWordConfig.init();
		refreshConfig();
		AttributeControl.init();
		
		data.afterReadConfigAll();
	}
	
	/** 刷新配置 */
	protected void refreshConfig()
	{
		TextEnum.readConfig();
		
		BaseGameUtils.initStrFilter(SensitiveWordConfig.getWordList(CommonSetting.languageType));
	}
}
