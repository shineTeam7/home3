using ShineEngine;
using UnityEngine;

/// <summary>
/// 离线游戏控制
/// </summary>
public class GameOfflineControl
{
    private bool _playerLoaded=false;

    private int _currentBornCode = -1;
    /** 当前角色ID */
    private long _currentPlayerID = -1;
    /** 本地存储文件路径 */
    private string _playerSavePath;
    /** 角色缓存数据 */
    private BytesWriteStream _stream = new BytesWriteStream();
    /** 角色列表数据 */
    private PlayerListData _listData;
    /** 离线事务列表数据 */
    private ClientOfflineWorkListData _offlineWorkListData;
    /** 角色离线缓存数据 */
    private PlayerOfflineCacheExData _offLineExData;

    /** 当前序号 */
    private int _currentIndex = 0;
    // /** 总数据 */
    // private SList<ClientOfflineWorkData> _workList=new SList<ClientOfflineWorkData>();

    /** 初始化 */
    public void init()
    {
        SystemControl.applicationQuitFunc+=onApplicationQuit;
    }

    private void onApplicationQuit()
    {
        saveOnce();
    }

    /** 读取某角色(离线用) */
    public void loadPlayer(long playerID)
    {
        int serverBornCode = GameC.save.getCacheServerBornCode();

        if (_currentBornCode == serverBornCode && _currentPlayerID == playerID)
            return;

        _currentBornCode = serverBornCode;
        _currentPlayerID = playerID;

        //兼容旧版
        if (_currentBornCode <= 0)
        {
            _playerSavePath = Application.persistentDataPath + "/player_" + playerID + "/offlinePlayer.bin";
        }
        else
        {
            _playerSavePath = Application.persistentDataPath + "/player_" + serverBornCode + "_" + playerID + "/offlinePlayer.bin";
        }

        _listData = null;

        if (FileUtils.fileExists(_playerSavePath))
        {
            BytesReadStream stream = FileUtils.readFileForBytesReadStream(_playerSavePath);

            int version = stream.readInt();

            //版本号不匹配
            if (version != BaseC.config.getDBDataVersion())
            {
                Ctrl.errorLog("本地存储结构版本不匹配,已清空persistant!,old:" + version + ",new:" + BaseC.config.getDBDataVersion());

                //不是正式版
                if (!ShineSetting.isOfficial)
                {
                    if(ShineSetting.isRelease)
                    {
                        FileUtils.deleteFile(_playerSavePath);
                    }
                    else
                    {
                        FileUtils.clearDir(Application.persistentDataPath);
                    }

                    initNewPlayer();
                    return;
                }

                //跳过list
                stream.startReadObj();
                stream.endReadObj();
                _listData = null;
            }
            else
            {
                _listData = GameC.player.createListData();
                _listData.readBytesFull(stream);
            }

            _offlineWorkListData = GameC.factory.createClientOfflineWorkListData();
            _offlineWorkListData.readBytesFull(stream);

            _offLineExData = GameC.factory.createPlayerOfflineCacheExData();
            _offLineExData.readBytesFull(stream);

            _currentIndex = _offlineWorkListData.index;
        }
        else
        {
            initNewPlayer();
        }

        if(!_playerLoaded)
        {
            _playerLoaded=true;

            //需要离线部分
            if (CommonSetting.useOfflineGame)
            {
                TimeDriver.instance.setInterval(onSave, CommonSetting.offlineSaveDelay);
            }
        }
    }

    private void initNewPlayer()
    {
        _listData = null;

        _offlineWorkListData = GameC.factory.createClientOfflineWorkListData();
        _offlineWorkListData.initDefault();

        _offLineExData = GameC.factory.createPlayerOfflineCacheExData();
        _offLineExData.initDefault();

        _currentIndex = _offlineWorkListData.index;
    }

    public void setCurrentIndex(int value)
    {
        _currentIndex = value;
    }

    /** 获取列表数据 */
    public PlayerListData getListData()
    {
        return _listData;
    }

    /** 获取附属数据 */
    public PlayerOfflineCacheExData getExData()
    {
        return _offLineExData;
    }

    public ClientOfflineWorkListData getOfflineWorkListData()
    {
        return _offlineWorkListData;
    }

    /** 准备推送离线事务数据 */
    public SList<ClientOfflineWorkData> flushOfflineWork()
    {
        SList<ClientOfflineWorkData> list = new SList<ClientOfflineWorkData>();

        list.addAll(_offlineWorkListData.list);

        return list;
    }

    /** 接受服务发送序号 */
    public void receiveIndex(int sendIndex)
    {
        //如果不足,赋值(来解决多客户端互相离线登录的序号问题)
        if (_currentIndex < sendIndex)
            _currentIndex = sendIndex;

        ThreadControl.addAssistFunc(() =>
        {
            ClientOfflineWorkData data;

            int find = -1;

            SList<ClientOfflineWorkData> list = _offlineWorkListData.list;

            for (int i = 0, len = list.size(); i < len; i++)
            {
                data = list.get(i);

                if (data.workIndex > sendIndex)
                {
                    find = i;
                    break;
                }
            }

            if (find == -1)
            {
                list.clear();
            }
            else
            {
                list.removeRange(0, find);
            }
        });
    }

    /** 保存一次 */
    public void saveOnce()
    {
        if(!_playerLoaded)
            return;

        //初始化过的
        if (GameC.player.system.inited())
        {
            string path = _playerSavePath;

            PlayerListData data = GameC.player.createListData();
            GameC.player.writeListData(data);

            BaseData exData = _offLineExData?.clone();

            ThreadControl.addAssistFunc(() =>
            {
                _stream.clear();
                _stream.writeInt(BaseC.config.getDBDataVersion());

                data.writeBytesFull(_stream);
                _offlineWorkListData.writeBytesFull(_stream);

                exData?.writeBytesFull(_stream);

                FileUtils.writeFileForBytesWriteStream(path, _stream);
            });
        }
    }

    private void onSave(int delay)
    {
        saveOnce();
    }

    /** 添加离线事务 */
    public void addOfflineWork(ClientOfflineWorkData data)
    {
        data.workIndex = ++_currentIndex;
        data.workTime = DateControl.getTimeMillis();

        ThreadControl.addAssistFunc(() =>
        {
            _offlineWorkListData.index = data.workIndex;
            _offlineWorkListData.list.add(data);
        });

        //当前在线
        if (GameC.player.system.isOnline())
        {
            SendClientOfflineWorkRequest.create(data).send();
        }
    }

    // /** 客户端离线事务失败 */
    // public void offlineWorkFailed(int receiveIndex,int serverIndex)
    // {
    // 	//出问题了，所以不相等
    // 	if(receiveIndex!=serverIndex)
    // 	{
    // 		_currentIndex=serverIndex;
    //
    // 		ThreadControl.addAssistFunc(()=>
    // 		{
    // 			_offlineWorkListData.index=serverIndex;
    // 			_offlineWorkListData.list.clear();
    // 		});
    // 	}
    // 	else
    // 	{
    // 		this.receiveIndex(receiveIndex);
    // 	}
    //
    // 	GameC.info.showInfoCode(InfoCodeType.OfflineWorkFailed);
    // }

    /** 收到服务器序号 */
    public void onReceive(int index, bool isSuccess)
    {
        this.receiveIndex(index);

        if (!isSuccess)
        {
            Ctrl.warnLog("离线事务失败", index);
            GameC.info.showInfoCode(InfoCodeType.OfflineWorkFailed);
        }
    }
}