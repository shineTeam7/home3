using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 角色本地保存数据
/// </summary>
public class PlayerSaveControl
{
	protected ClientPlayerLocalCacheData _data;

	protected bool _dirty=false;

	private BytesWriteStream _stream=new BytesWriteStream();

	/** 本地存储文件路径 */
	private string _savePath;

	public void init()
	{
		TimeDriver.instance.setFrame(onFrame);
	}

	/** 读取角色数据 */
	public void loadPlayer(long playerID)
	{
		int serverBornCode=GameC.save.getCacheServerBornCode();

		//兼容旧版
		if(serverBornCode<=0)
		{
			_savePath=Application.persistentDataPath+"/player_"+playerID + "/playerSave.bin";
		}
		else
		{
			_savePath=Application.persistentDataPath+"/player_"+serverBornCode+"_"+playerID + "/playerSave.bin";
		}

		_data=GameC.factory.createClientPlayerLocalCacheData();

		BytesReadStream stream=FileUtils.readFileForBytesReadStream(_savePath);

		if(stream!=null && stream.checkVersion(ShineGlobal.playerSaveVersion))
		{
			_data.readBytesFull(stream);
		}
		else
		{
			_data.initDefault();
		}
	}

	/** 卸载当前数据 */
	public void unloadPlayer()
	{
		_data=null;
		_dirty=false;
		_savePath=null;
	}

	/** 标记修改 */
	public void modify()
	{
		_dirty=true;
	}

	/// <summary>
	/// 获取缓存数据,修改完了，要调用modify
	/// </summary>
	public ClientPlayerLocalCacheData getData()
	{
		return _data;
	}

	private void onFrame(int delay)
	{
		if(_data==null)
			return;

		if(!_dirty)
			return;

		_dirty=false;

		doWrite();
	}

	private void doWrite()
	{
		ClientPlayerLocalCacheData data=GameC.factory.createClientPlayerLocalCacheData();
		data.copy(_data);

		//缓存一手
		string path=_savePath;

		ThreadControl.addIOFunc(()=>
		{
			_stream.clear();
			_stream.writeVersion(ShineGlobal.playerSaveVersion);

			data.writeBytesFull(_stream);
			FileUtils.writeFileForBytes(path,_stream);
		});
	}

	public void setBool(int key,bool value)
	{
		_dirty=true;
		_data.keep.booleanDic.put(key,value);
	}

	public bool getBool(int key)
	{
		return _data.keep.booleanDic.get(key);
	}

	public bool hasBool(int key)
	{
		return _data.keep.booleanDic.contains(key);
	}

	public void removeBool(int key)
	{
		_dirty=true;
		_data.keep.booleanDic.remove(key);
	}

	public void setInt(int key,int value)
	{
		_dirty=true;
		_data.keep.intDic.put(key,value);
	}

	public int getInt(int key)
	{
		return _data.keep.intDic.get(key);
	}

	public bool hasInt(int key)
	{
		return _data.keep.intDic.contains(key);
	}

	public void removeInt(int key)
	{
		_dirty=true;
		_data.keep.intDic.remove(key);
	}

	public void setString(string key,string value)
	{
		_dirty=true;
		_data.keep.stringDic.put(key,value);
	}

	public string getString(string key)
	{
		string str=_data.keep.stringDic.get(key);

		if(str==null)
			return "";

		return str;
	}

	public bool hasString(string key)
	{
		return _data.keep.stringDic.contains(key);
	}

	public void removeString(string key)
	{
		_dirty=true;
		_data.keep.stringDic.remove(key);
	}
}