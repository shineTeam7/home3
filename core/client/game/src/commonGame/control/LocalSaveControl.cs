using System;
using System.IO;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 本地存储控制
/// </summary>
public class LocalSaveControl
{
	private KeepSaveData _data;

	private bool _dirty=false;

	private BytesWriteStream _stream=new BytesWriteStream();

	/** 本地存储文件路径 */
	private static string _localSavePath=Application.persistentDataPath + "/localSave.bin";
	/** 本地登录数据缓存 */
	private static string _loginDataPath=Application.persistentDataPath + "/clientLoginData.bin";
	/** 登录数据 */
	private static ClientLoginCacheData _loginData;

	public void init()
	{
		if(!CommonSetting.localSaveUsePlayerPrefs)
		{
			load();

			TimeDriver.instance.setFrame(onFrame);
		}
	}

	private void onFrame(int delay)
	{
		if(!_dirty)
			return;

		_dirty=false;

		if(!CommonSetting.localSaveUsePlayerPrefs)
		{
			save();
		}
	}

	public void setBool(int key,bool value)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			PlayerPrefs.SetInt(Convert.ToString(key),value ? 1 : 0);
		}
		else
		{
			_dirty=true;
			_data.booleanDic.put(key,value);
		}
	}

	public bool getBool(int key)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			return PlayerPrefs.GetInt(Convert.ToString(key))==1;
		}
		else
		{
			return _data.booleanDic.get(key);
		}
	}

	public bool hasBool(int key)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			return PlayerPrefs.HasKey(Convert.ToString(key));
		}
		else
		{
			return _data.booleanDic.contains(key);
		}
	}

	public void removeBool(int key)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			PlayerPrefs.DeleteKey(Convert.ToString(key));
		}
		else
		{
			_dirty=true;
			_data.booleanDic.remove(key);
		}
	}

	public void setInt(int key,int value)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			PlayerPrefs.SetInt(Convert.ToString(key),value);
		}
		else
		{
			_dirty=true;
			_data.intDic.put(key,value);
		}
	}

	public int getInt(int key)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			return PlayerPrefs.GetInt(Convert.ToString(key));
		}
		else
		{
			return _data.intDic.get(key);
		}
	}

	public bool hasInt(int key)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			return PlayerPrefs.HasKey(Convert.ToString(key));
		}
		else
		{
			return _data.intDic.contains(key);
		}
	}

	public void removeInt(int key)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			PlayerPrefs.DeleteKey(Convert.ToString(key));
		}
		else
		{
			_dirty=true;
			_data.intDic.remove(key);
		}
	}

	public void setString(string key,string value)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			PlayerPrefs.SetString(key,value);
		}
		else
		{
			_dirty=true;
			_data.stringDic.put(key,value);
		}
	}

	public string getString(string key)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			return PlayerPrefs.GetString(key);
		}
		else
		{
			string str=_data.stringDic.get(key);

			if(str==null)
				return "";

			return str;
		}
	}

	public bool hasString(string key)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			return PlayerPrefs.HasKey(key);
		}
		else
		{
			return _data.stringDic.contains(key);
		}
	}

	public void removeString(string key)
	{
		if(CommonSetting.localSaveUsePlayerPrefs)
		{
			PlayerPrefs.DeleteKey(key);
		}
		else
		{
			_dirty=true;
			_data.stringDic.remove(key);
		}
	}

	/// <summary>
	/// 保存本地文件
	/// </summary>
	private void save()
	{
		KeepSaveData data=new KeepSaveData();
		data.copy(_data);

		ThreadControl.addIOFunc(()=>
		{
			doSave(data);
		});
	}

	private void doSave(KeepSaveData data)
	{
		_stream.clear();
		_stream.writeVersion(ShineGlobal.localSaveVersion);

		data.writeBytesFull(_stream);

		FileUtils.writeFileForBytes(_localSavePath,_stream);
	}

	/// <summary>
	/// 读取本地文件
	/// </summary>
	private void load()
	{
		_data=new KeepSaveData();

		BytesReadStream stream=FileUtils.readFileForBytesReadStream(_localSavePath);

		if(stream!=null && stream.checkVersion(ShineGlobal.localSaveVersion))
		{
			_data.readBytesFull(stream);
		}
		else
		{
			_data.initDefault();
		}
	}

	/** 读取登录数据缓存 */
	public ClientLoginCacheData loadLoginCache()
	{
		if(_loginData!=null)
			return _loginData;

		BytesReadStream stream=FileUtils.readFileForBytesReadStream(_loginDataPath);

		if(stream!=null && stream.checkVersion(ShineGlobal.loginDataVersion))
		{
			_loginData=new ClientLoginCacheData();
			_loginData.readBytesFull(stream);
			return _loginData;
		}

		return null;
	}

	/** 通过登录数据，保存缓存数据 */
	public void saveByLogin(ClientLoginData data)
	{
		if(_loginData==null)
		{
			_loginData=new ClientLoginCacheData();
			_loginData.serverBornCode=-1;//没有的时候
		}

		_loginData.uid=data.uid;
		_loginData.platform=data.platform;

		//保存本次登录数据
		saveLoginCache(_loginData);
	}

	/** 绑定平台的存储 */
	public void saveByBindPlatform(string uid,string platform)
	{
		ClientLoginCacheData cacheData=loadLoginCache();

		if(cacheData==null)
		{
			Ctrl.throwError("此时不该没有登录数据");
			return;
		}

		cacheData.uid=uid;
		cacheData.platform=platform;

		saveLoginCache(cacheData);
	}

	public void saveByServerBornCode(int serverBornCode)
	{
		ClientLoginCacheData cacheData=loadLoginCache();

		if(cacheData==null)
		{
			Ctrl.throwError("此时不该没有登录数据");
			return;
		}

		cacheData.serverBornCode=serverBornCode;
		saveLoginCache(cacheData);
	}

	/** 保存areaID */
	public void saveByAreaID(int areaID)
	{
		ClientLoginCacheData cacheData=loadLoginCache();

		if(cacheData==null)
		{
			Ctrl.throwError("此时不该没有登录数据");
			return;
		}

		cacheData.areaID=areaID;
		saveLoginCache(cacheData);
	}

	/** 保存本次登录的角色记录 */
	public void cacheLoginPlayer(string uid,long playerID,int serverBornCode)
	{
		if(_loginData==null)
		{
			Ctrl.throwError("此时不该没有登录数据");
			return;
		}

		if(_loginData.uid!=uid)
		{
			Ctrl.throwError("uid不该不匹配");
			return;
		}

		_loginData.lastPlayerID=playerID;
		//服务器生成码
		_loginData.serverBornCode=serverBornCode;

		saveLoginCache(_loginData);
	}

	/** 保存登录缓存数据 */
	private void saveLoginCache(ClientLoginCacheData data)
	{
		_loginData=data;
		BytesWriteStream stream=new BytesWriteStream();
		stream.writeVersion(ShineGlobal.loginDataVersion);
		data.writeBytesFull(stream);
		FileUtils.writeFileForBytesWriteStream(_loginDataPath,stream);
	}

	/** 是否有上次登录的角色 */
	public bool hasLastLoginPlayer()
	{
		ClientLoginCacheData data=loadLoginCache();

		return data!=null && data.lastPlayerID>0;
	}

	/** 获取缓存服务器生成码 */
	public int getCacheServerBornCode()
	{
		ClientLoginCacheData data=loadLoginCache();

		return data!=null ? data.serverBornCode : -1;
	}

	/** 是否可离线登录 */
	public bool canOfflineLogin()
	{
		return CommonSetting.useOfflineGame && hasLastLoginPlayer();
	}

	/** 清除登录数据(注销后调用) */
	public void clearLoginCache()
	{
		if(_loginData!=null)
		{
			FileUtils.deleteFile(_loginDataPath);
			_loginData=null;
		}
	}
}