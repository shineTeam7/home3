using System;
using System.Text;
using ShineEditor;
using ShineEngine;
using UnityEditor;
using UnityEditor.SceneManagement;
using UnityEngine;
using UnityEngine.AI;
using UnityEngine.SceneManagement;
using EventType = UnityEngine.EventType;

/** 场景编辑器 */
public class SceneEditorWindow:BaseWindow
{
	private const int Stoped=0;
	private const int Waitting=1;
	private const int Running=2;

	private int _state=Stoped;

	protected float _drawHeight=1f;

	//
	private IntObjectMap<SceneEditorData> _sceneDic=new IntObjectMap<SceneEditorData>();

	//UI


	/** 当前编辑的场景数据 */
	protected SceneEditorData _currentSceneData;
	/** 当前场景 */
	private UnityEngine.SceneManagement.Scene _currentScene;

	/** 可用的单位实例ID */
	private int _elementInstanceID=-1;

	private IntObjectMap<SceneElementEditorData> _elementDic=new IntObjectMap<SceneElementEditorData>();

	private SMap<GameObject,SceneElementEditorData> _elementDicByObj=new SMap<GameObject,SceneElementEditorData>();

	//layer
	private GameObject _unitLayer;

	//choose
	private int _chooseElementType;

	//场景
	private SList<SceneConfig> _sceneList;
	private string[] _sceneListStrs;
	private int _chooseSceneIndex=0;

	//怪物
	private SList<MonsterConfig> _monsterList;
	private string[] _monsterListStrs;
	private int _chooseMonsterIndex=0;
	private MonsterConfig _chooseMonsterConfig;

	//操作体
	private SList<OperationConfig> _operationList;
	private string[] _operationListStrs;
	private int _chooseOperationIndex=0;
	private OperationConfig _chooseOperationConfig;

	//掉落物品
	private string _chooseFieldItemIDStr;
	private ItemConfig _chooseItemConfig;

	//鼠标位置

	private Vector3 _mousePos;
	/** 是否处理了pickDown */
	private bool _isHandledPickDown;
	//select
	/** 当前选择对象 */
	protected SceneElementEditorData _selectData;

	/** 临时数据 */
	private SceneElementEditorData _tempData;

	private GameObject _lastSelectionObject;

	//阻挡相关

	private bool _showMask=false;

	private GameObject _maskObj;

	protected byte[,] _grids;
	protected byte[,] _secondGrids;

	protected PosData _originPos=new PosData();
	protected PosData _sizePos=new PosData();

	//temp
	private PosDirData _tempPosDir=new PosDirData();

	protected float _halfGridSize;

	protected virtual void doInit()
	{
		//初始化点
		_tempPosDir.initDefault();
	}

	private void OnDestroy()
	{
		exitEditor();
	}

	private void initConfig()
	{
		if(_sceneList==null)
		{
			reloadConfig();
		}
	}

	/** 重新加载配置 */
	private void reloadConfig()
	{
		long now=Ctrl.getTimer();

		BaseC.config.loadSyncForEditorAll();

		long now2=Ctrl.getTimer();

		Ctrl.print("editor加载配置All完成,耗时:",now2-now);

		int i=0;
		_sceneList=new SList<SceneConfig>();
		_sceneListStrs=new string[SceneConfig.getDic().size()];

		SceneConfig.getDic().getSortedKeyList().forEach(k=>
		{
			SceneConfig config=SceneConfig.get(k);
			_sceneList.add(config);
			_sceneListStrs[i++]=config.id+":"+config.name;
		});

		i=0;
		_monsterList=new SList<MonsterConfig>();
		_monsterListStrs=new string[MonsterConfig.getDic().size()];

		MonsterConfig.getDic().getSortedKeyList().forEach(k=>
		{
			MonsterConfig config=MonsterConfig.get(k);
			_monsterList.add(config);
			_monsterListStrs[i++]=config.id + ":" + config.name;
		});

		i=0;
		_operationList=new SList<OperationConfig>();
		_operationListStrs=new string[OperationConfig.getDic().size()];

		OperationConfig.getDic().getSortedKeyList().forEach(k=>
		{
			OperationConfig config=OperationConfig.get(k);
			_operationList.add(config);
			_operationListStrs[i++]=config.id + ":" + config.name;
		});
	}

	protected override void OnGUI()
	{
		GUILayout.Space(10);

		if(_state==Stoped)
		{
			if(EditorApplication.isPlaying)
			{
				if(EditorSceneManager.GetActiveScene().path==ShineToolGlobal.sceneEditorScenePath)
				{
					if(GUILayout.Button("结束",GUI.skin.button,GUILayout.Width(100),GUILayout.Height(20)))
					{
						exitEditor();
					}
				}
				else
				{
					GUILayout.Label("需要先停止运行，才能开始场景编辑");
					return;
				}
			}
			else
			{
				if(GUILayout.Button("开始",GUI.skin.button,GUILayout.Width(100),GUILayout.Height(20)))
				{
					startEditor();
					return;
				}
			}
		}
		else if(_state==Running)
		{
			GUILayout.BeginHorizontal();

			if(GUILayout.Button("结束",GUI.skin.button,GUILayout.Width(100),GUILayout.Height(20)))
			{
				exitEditor();
			}

			if(GUILayout.Button("保存",GUI.skin.button,GUILayout.Width(100),GUILayout.Height(20)))
			{
				saveEditor();
			}

			if(GUILayout.Button("保存服务器阻挡",GUI.skin.button,GUILayout.Width(100),GUILayout.Height(20)))
			{
				saveCurrentMapInfo();
			}

			_showMask=GUILayout.Toggle(_showMask,new GUIContent("是否显示阻挡"),GUILayout.Width(100));

			drawGrids();

			GUILayout.EndHorizontal();
			GUILayout.BeginHorizontal();
			onGUIScene();
			GUILayout.EndHorizontal();

			GUILayout.Space(10);
			GUILayout.BeginHorizontal();
			onGUIMonster();
			GUILayout.EndHorizontal();

			GUILayout.Space(10);
			GUILayout.BeginHorizontal();
			onGUIOperation();
			GUILayout.EndHorizontal();

			GUILayout.Space(10);
			GUILayout.BeginHorizontal();
			onGUIFieldItem();
			GUILayout.EndHorizontal();
		}
	}

	private void drawGrids()
	{
		if(_showMask)
		{
			if(_grids!=null)
			{
				if(_maskObj==null)
				{
					_maskObj=new GameObject("mask");

					MeshFilter meshFilter=_maskObj.AddComponent<MeshFilter>();
					MeshRenderer meshRenderer=_maskObj.AddComponent<MeshRenderer>();

					int width=_grids.GetLength(0);
					int length=_grids.GetLength(1);

					Mesh mesh=new Mesh();


					Vector3[] vecs=new Vector3[(width+1)*(length+1)];
					int index;
					int v;

					float gridSize=Global.mapBlockSize;

					IntList tList=new IntList();

					for(int i=0;i<=width;i++)
					{
						if(i<width)
						{
						}

						for(int j=0;j<=length;j++)
						{
							index=i * (length+1) + j;

							vecs[index].x=i*gridSize;
							vecs[index].y=_drawHeight;
							vecs[index].z=j*gridSize;

							if(i<width && j<length)
							{
								v=_grids[i,j];

								if(v==MapBlockType.Land)
								{
									tList.add(i * (length+1) + j);
									tList.add(i * (length+1) + j+1);
									tList.add((i+1) * (length+1) + j);

									tList.add((i+1) * (length+1) + j);
									tList.add(i * (length+1) + j+1);
									tList.add((i+1) * (length+1) + j+1);
								}
							}
						}
					}

					mesh.vertices=vecs;
					mesh.triangles=tList.toArray();
					mesh.RecalculateBounds();
					mesh.RecalculateNormals();

					meshFilter.mesh=mesh;
				}
			}

		}
		else
		{
			if(_maskObj!=null)
			{
				GameObject.DestroyImmediate(_maskObj);
				_maskObj=null;
			}
		}
	}

	protected void onGUIScene()
	{
		if(_sceneList==null)
			return;

		GUILayout.Label("地图列表：",GUILayout.Width(60));
		_chooseSceneIndex=EditorGUILayout.Popup(_chooseSceneIndex,_sceneListStrs,GUILayout.Width(100),GUILayout.Height(20));

		if(GUILayout.Button("选择地图",GUI.skin.button,GUILayout.Width(60),GUILayout.Height(20)))
		{
			enterScene(_sceneList[_chooseSceneIndex]);
		}
	}

	protected void onGUIMonster()
	{
		if(_currentSceneData==null)
			return;

		if(_monsterList==null)
			return;

		GUILayout.Label("怪物列表：",GUILayout.Width(60));
		_chooseMonsterIndex=EditorGUILayout.Popup(_chooseMonsterIndex,_monsterListStrs,GUILayout.Width(100),GUILayout.Height(20));

		if(GUILayout.Button("选择怪物",GUI.skin.button,GUILayout.Width(60),GUILayout.Height(20)))
		{
			chooseMonster(_monsterList[_chooseMonsterIndex]);
		}
	}

	protected void onGUIOperation()
	{
		if(_currentSceneData==null)
			return;

		if(_operationList==null)
			return;

		GUILayout.Label("操作体列表：",GUILayout.Width(60));
		_chooseOperationIndex=EditorGUILayout.Popup(_chooseOperationIndex,_operationListStrs,GUILayout.Width(100),GUILayout.Height(20));

		if(GUILayout.Button("选择操作体",GUI.skin.button,GUILayout.Width(60),GUILayout.Height(20)))
		{
			chooseOperation(_operationList[_chooseOperationIndex]);
		}
	}

	protected void onGUIFieldItem()
	{
		if(_currentSceneData==null)
			return;

		GUILayout.Label("物品ID：",GUILayout.Width(60));

		_chooseFieldItemIDStr=GUILayout.TextField(_chooseFieldItemIDStr,GUILayout.Width(100));

		if(GUILayout.Button("选择物品",GUI.skin.button,GUILayout.Width(60),GUILayout.Height(20)))
		{
			int itemID;

			if(int.TryParse(_chooseFieldItemIDStr,out itemID))
			{
				ItemConfig itemConfig=ItemConfig.get(itemID);

				if(itemConfig==null)
				{
					Ctrl.print("找不到物品数据",itemID);
				}
				else
				{
					chooseFieldItem(itemConfig);
				}
			}
			else
			{
				Ctrl.print("输入错误");
			}
		}
	}

	protected override void onUpdate()
	{
		base.onUpdate();

		switch(_state)
		{
			case Waitting:
			{
				if(EditorApplication.isPlaying)
				{
					_state=Running;

					onEditorStart();
				}
			}
				break;
			case Running:
			{
				if(!EditorApplication.isPlaying)
				{
					exitEditor();
					_state=Stoped;
				}
			}
				break;
		}
	}

	private void startEditor()
	{
		if(EditorSceneManager.GetActiveScene().path!=ShineToolGlobal.sceneEditorScenePath)
		{
			EditorSceneManager.OpenScene(ShineToolGlobal.sceneEditorScenePath);
		}

		EditorApplication.isPlaying=true;
		_state=Waitting;
	}

	private void onEditorStart()
	{
		initConfig();

		doInit();

		EditorApplication.ExecuteMenuItem("Window/General/Scene");

		SceneView.duringSceneGui+=onEditorFrame;
		EditorApplication.hierarchyChanged+=onHierarchyChanged;
	}

	private void exitEditor()
	{
		EditorApplication.isPlaying=false;

		if(_state==Stoped)
			return;

		_state=Stoped;

		SceneView.duringSceneGui-=onEditorFrame;
		EditorApplication.hierarchyChanged-=onHierarchyChanged;

		if(_maskObj!=null)
		{
			GameObject.DestroyImmediate(_maskObj);
			_maskObj=null;
		}

		// exitScene();
		// writeCombine();
	}

	private void saveEditor()
	{
		saveScene();
		writeCombine();

		Ctrl.print("保存成功");
	}

	/** 保存当前地图阻挡信息 */
	private void saveCurrentMapInfo()
	{
		saveMap();
		writeMapCombine();

		Ctrl.print("保存阻挡成功");
	}

	private void onHierarchyChanged()
	{
		countDeleteGameObject();
	}

	private void onEditorFrame(SceneView view)
	{
		if(_state!=Running)
			return;

		GUI.skin.label.normal.background = GUI.skin.button.normal.background;

		onEditorShow();

		refreshSelectGameObject();

		onHandle(Event.current);

		GUI.skin.label.normal.background = null;
	}

	/** 刷新选择的gameObject */
	private void refreshSelectGameObject()
	{
		if(_lastSelectionObject!=Selection.activeGameObject)
		{
			if(Selection.activeGameObject!=null)
			{
				SceneElementEditorData eData=findDataByGameObject(Selection.activeGameObject);

				if(eData!=null)
				{
					selectElement(eData);
				}
				else
				{
					cancelSelect();
				}
			}
			else
			{
				cancelSelect();
			}

			_lastSelectionObject=Selection.activeGameObject;
		}

		//当前选择对象不为空
		if(_selectData!=null && !_selectData.isTemp && !_selectData.isPicked)
		{
			setPosFromGameObject(_selectData);
		}
	}

	/** 计算删除单位 */
	private void countDeleteGameObject()
	{
		_elementDic.forEachValueS(v=>
		{
			if(v.gameObject!=null)
			{
				if(!v.gameObject.activeInHierarchy)
				{
					//删除
					deleteElement(v);
				}
			}
			else
			{
				deleteElement(v);
			}
		});
	}

	/** 通过game查找对象数据 */
	private SceneElementEditorData findDataByGameObject(GameObject obj)
	{
		Transform transform=obj.transform;

		while(true)
		{
			SceneElementEditorData data=toFindSceneElementEditorDataByGameObject(transform.gameObject);

			if(data!=null)
				return data;

			if(transform.parent!=null)
			{
				transform=transform.parent;
			}
			else
			{
				return null;
			}
		}

		return null;
	}

	protected virtual SceneElementEditorData toFindSceneElementEditorDataByGameObject(GameObject gameObject)
	{
		//是临时数据
		if(_tempData!=null && _tempData.gameObject==gameObject)
			return _tempData;

		SceneElementEditorData eData=_elementDicByObj.get(gameObject);

		if(eData!=null)
			return eData;

		return null;
	}

	private void onEditorShow()
	{
		Vector3 mousePosition=Event.current.mousePosition;

		if(Screen.safeArea.Contains(mousePosition))
		{
			Ray ray;

			try
			{
				ray = HandleUtility.GUIPointToWorldRay(mousePosition);
			}
			catch(Exception e)
			{
				return;
			}

			RaycastHit hitInfo;

			if(Physics.Raycast(ray,out hitInfo,CommonSetting.maxDistance,LayerType.TerrainMask))
			{
				Vector3 origin = hitInfo.point;
				origin.y += 100;

				// 垂直查询;
				bool rayHit = Physics.Raycast(origin, Vector3.down, out hitInfo, 500, LayerType.UnitMask);
				if(rayHit)
				{
					EditorUtils.showLine(hitInfo.point, origin, Color.green);
					showArrowAndPosition(hitInfo.point);
					refreshPickPos(hitInfo.point);
				}
				else
				{
					if(Physics.Raycast(origin,Vector3.down,out hitInfo,500,LayerType.TerrainMask))
					{
						EditorUtils.showLine(hitInfo.point,origin,Color.yellow);
						showArrowAndPosition(hitInfo.point);
						refreshPickPos(hitInfo.point);
					}
				}
			}
		}

	}

	private void refreshPickPos(Vector3 vec)
	{
		_mousePos=vec;

		if(_selectData!=null && _selectData.isPicked && _selectData.gameObject!=null)
		{
			_selectData.gameObject.transform.position=vec;
		}
	}

	private void onHandle(Event evt)
	{
		//限定类型
		if(UnityEditor.Tools.viewTool!=ViewTool.Pan && UnityEditor.Tools.viewTool!=ViewTool.FPS)
			return;

		bool used = false;

		switch(evt.type)
		{
			case EventType.MouseDown:
			{
				//左键
				if(evt.button==0)
				{
					if(_selectData!=null && _selectData.isPicked)
					{
						used=pickDown();
						_isHandledPickDown=true;
					}
				}
			}
				break;
			case EventType.MouseUp:
			{
				//左键
				if(evt.button==0)
				{
					if(_selectData!=null && !_selectData.isPicked && !_isHandledPickDown)
					{
						//是当前的
						if(Selection.activeGameObject==_selectData.gameObject)
						{
							used=pickUpSelect();
						}
					}

					_isHandledPickDown=false;
				}
				//右键
				else if(evt.button==1)
				{
					used=cancelPick();
				}
			}
				break;
			case EventType.KeyUp:
			{
				used=onKeyUp(evt);
			}
				break;
		}

		if(used)
		{
			evt.Use();
		}
	}

	protected virtual bool onKeyUp(Event evt)
	{
		switch(evt.keyCode)
		{
			//代理左键
			case KeyCode.BackQuote:
			{
				return leftButton();
			}
				break;
			case KeyCode.Escape:
			{
				return cancelSelect();
			}
				break;
			case KeyCode.Delete:
			{
				//靠h视图删
				// used=deleteSelect();
			}
				break;
			case KeyCode.S:
			{
				if(evt.control || evt.command)
				{
					saveEditor();
					return true;
				}
			}
				break;
			case KeyCode.D:
			{
				if(evt.control || evt.command)
				{
					//不在拖拽中
					if(_selectData==null || !_selectData.isPicked)
					{
						reChoose();
					}
				}
			}
				break;
			case KeyCode.G:
			{
				//不在拖拽中
				if(_selectData!=null || !_selectData.isPicked)
				{
					Vector3 pos=_selectData.gameObject.transform.position;
					Vector3 vec=_mousePos;
					vec.y=pos.y;//高度一致

					_selectData.gameObject.transform.LookAt(vec);

					setPosFromGameObject(_selectData);

					return true;
				}
			}
				break;
		}

		return false;
	}

	/** 左键功能 */
	private bool leftButton()
	{
		if(_selectData!=null)
		{
			if(_selectData.isPicked)
			{
				return pickDown();
			}
			else
			{
				//是当前的
				if(Selection.activeGameObject==_selectData.gameObject)
				{
					return pickUpSelect();
				}
			}
		}

		return false;
	}

	private void showArrowAndPosition(Vector3 pos)
	{
		EditorUtils.showArrows(pos);
		Handles.Label(pos + (Vector3.up * 2),pos.ToString());
	}

	/** 获取场景摆放配置保存路径 */
	public static string getScenePlaceFilePath(int sceneID)
	{
		return ShineToolGlobal.scenePlacePath + "/" + sceneID + ".bin";
	}

	/** 获取场景摆放配置保存路径(astart) */
	public static string getMapFilePathFront(int mapID)
	{
		return ShineToolGlobal.mapInfoPath + "/" + mapID;
	}

	/** 获取场景摆放配置保存路径(astart) */
	public static string getMapGridFilePath(int mapID)
	{
		return getMapFilePathFront(mapID) + ".bin";
	}

	/** 获取场景摆放配置保存路径(astart) */
	public static string getMapNavFilePath(int mapID)
	{
		return getMapFilePathFront(mapID)+"_nav.bin";
	}

	/** 写合并项 */
	private void writeCombine()
	{
		if(_sceneList==null)
			return;

		Ctrl.print("writeCombine");

		IntSet sceneSet=new IntSet();

		BytesWriteStream stream=new BytesWriteStream();

		stream.writeLen(_sceneList.length());

		for(int i=0;i<_sceneList.length();i++)
		{
			SceneConfig config=_sceneList.get(i);

			sceneSet.add(config.id);

			stream.writeInt(config.id);

			string path=getScenePlaceFilePath(config.id);

			byte[] bytes=FileUtils.readFileForBytes(path);

			if(bytes!=null)
			{
				stream.writeByteArr(bytes);
			}
			else
			{
				stream.writeLen(0);
			}

			BytesWriteStream clientStream=new BytesWriteStream();
			BaseC.config.writeSplitConfigVersion(clientStream);

			clientStream.writeInt(config.id);

			if(bytes!=null)
			{
				clientStream.writeByteArr(bytes);
			}
			else
			{
				clientStream.writeLen(0);
			}

			if(CommonSetting.configNeedCompress)
				clientStream.compress();

			FileUtils.writeFileForBytesWriteStream(ShineToolGlobal.sourceCommonConfigDirPath + "/scenePlaceEditor/" + config.id + ".bin",clientStream);
		}

		//只写服务器，不写客户端
		FileUtils.writeFileForBytesWriteStream(ShineToolGlobal.serverSavePath+"/config/scenePlaceEditor.bin",stream);
		// FileUtils.writeFileForBytesWriteStream(ShineToolGlobal.clientSavePath+"/config/scenePlaceEditor.bin",stream);

		//调用configExport
		ToolFileUtils.executeServerTool("configExport");

		//移除无用的
		string[] fileList=FileUtils.getFileList(ShineToolGlobal.sourceCommonConfigDirPath + "/scenePlaceEditor/","bin");

		foreach(string v in fileList)
		{
			int sid=int.Parse(FileUtils.getFileFrontName(FileUtils.getFileName(v)));

			if(!sceneSet.contains(sid))
			{
				FileUtils.deleteFile(v);
			}
		}
	}

	/** 保存当前场景 */
	private void saveScene()
	{
		if(_currentSceneData==null)
			return;

		_currentSceneData.write();
	}

	/** 退出当前的场景 */
	private void exitScene()
	{
		if(_currentSceneData==null)
			return;

		// saveScene();

		// EditorSceneManager.CloseScene(_currentScene,true);
		_currentSceneData=null;
	}

	/** 场景进入场景 */
	private void enterScene(SceneConfig config)
	{
		if(_currentSceneData!=null)
		{
			exitScene();
		}

		SceneEditorData sData=_sceneDic.get(config.id);

		if(sData==null)
		{
			sData=new SceneEditorData();
			sData.config=config;
			sData.read();

			_sceneDic.put(config.id,sData);
		}

		_currentSceneData=sData;

		_elementInstanceID=-1;

		SceneMapConfig mapConfig=SceneMapConfig.get(config.mapID);
		_originPos.setByIArr(mapConfig.origin);
		_sizePos.setByIArr(mapConfig.size);

		EditorSceneManager.sceneLoaded+=onSceneLoaded;
		EditorSceneManager.LoadScene(ShineToolGlobal.assetSourceStr + "/" + mapConfig.source);
	}

	private void onSceneLoaded(UnityEngine.SceneManagement.Scene scene,LoadSceneMode mod)
	{
		EditorSceneManager.sceneLoaded-=onSceneLoaded;

		_unitLayer=GameObject.Find(ShineGlobal.unitLayer);

		if(_unitLayer==null)
		{
			_unitLayer=new GameObject(ShineGlobal.unitLayer);
		}

		doShowScene();
	}

	protected virtual void doShowScene()
	{
		_elementDic=_currentSceneData.elements;
		_elementDicByObj.clear();

		_elementDic.forEachValue(v=>
		{
			makeElementModel(v);

			if(v.gameObject!=null)
			{
				_elementDicByObj.put(v.gameObject,v);
			}
			else
			{
				Ctrl.errorLog("不支持的单位类型",v.config.type);
			}
		});
	}

	private string getElementName(SceneElementEditorData data)
	{
		switch(data.config.type)
		{
			case SceneElementType.Npc:
			{

			}
				break;
			case SceneElementType.Monster:
			{
				return "monster:" + data.config.instanceID;
			}
			case SceneElementType.FieldItem:
			{
				return "fieldItem:" + data.config.instanceID;
			}
		}

		return "";
	}

	private void makeElementModel(SceneElementEditorData data)
	{
		switch(data.config.type)
		{
			case SceneElementType.Npc:
			{

			}
				break;
			case SceneElementType.Monster:
			{
				MonsterConfig monsterConfig=MonsterConfig.get(data.config.id);

				data.gameObject=createModel(FightUnitConfig.get(monsterConfig.fightUnitID).modelID,data);
			}
				break;
			case SceneElementType.Operation:
			{
				OperationConfig operationConfig=OperationConfig.get(data.config.id);

				data.gameObject=createModel(operationConfig.modelID,data);
			}
				break;
			case SceneElementType.FieldItem:
			{
				ItemConfig itemConfig=ItemConfig.get(data.config.id);

				data.gameObject=createModel(itemConfig.fieldItemModelID,data);
			}
				break;
		}

		setPosToGameObject(data);
	}

	private GameObject createModel(int modelID,SceneElementEditorData eData)
	{
		ModelConfig config=ModelConfig.get(modelID);

		GameObject obj=new GameObject(getElementName(eData));

		SceneElementComponent component=obj.AddComponent<SceneElementComponent>();
		component.instanceID=eData.config.instanceID;

		if(config!=null)
		{
			GameObject asset=AssetDatabase.LoadAssetAtPath<GameObject>(ShineToolGlobal.assetSourceStr + "/" + config.source);
			GameObject newOne=GameObject.Instantiate(asset);
			newOne.transform.SetParent(obj.transform);
		}

		//TODO:根据type设置layer
		obj.transform.SetParent(_unitLayer.transform);

		return obj;
	}

	/** 创建新对象 */
	private SceneElementEditorData createNewElement(int type,int id)
	{
		SceneElementEditorData eData=new SceneElementEditorData();
		eData.isTemp=true;
		ScenePlaceElementConfig eConfig=eData.config=new ScenePlaceElementConfig();
		eConfig.sceneID=_currentSceneData.config.id;
		eConfig.instanceID=0;
		eConfig.type=type;
		eConfig.id=id;
		eConfig.level=1;
		eConfig.pos=new float[5];
		eConfig.force=1;
		eConfig.isInitAdd=true;//默认初始添加
		eConfig.iArgs=ObjectUtils.EmptyIntArr;
		eConfig.fArgs=ObjectUtils.EmptyFloatArr;

		makeElementModel(eData);

		return eData;
	}

	private void setPosToGameObject(SceneElementEditorData data)
	{
		if(data.gameObject!=null)
		{
			if(data.config!=null)
			{
				_tempPosDir.setByFArr(data.config.pos);
				data.gameObject.transform.position=_tempPosDir.pos.getVector();
				data.gameObject.transform.rotation=_tempPosDir.dir.getQuaternion();
			}
		}
	}

	private void setPosFromGameObject(SceneElementEditorData data)
	{
		if(data.gameObject!=null && data.gameObject.active)
		{
			if(data.config!=null)
			{
				//更新位置
				Transform tt=data.gameObject.transform;

				_tempPosDir.pos.setByVector(tt.position);
				_tempPosDir.dir.setByQuaternion(tt.rotation);

				data.config.pos=_tempPosDir.getFArr();
			}

		}
	}

	/** 再选择 */
	private void reChoose()
	{
		switch(_chooseElementType)
		{
			case SceneElementType.Monster:
			{
				chooseMonster(_chooseMonsterConfig);
			}
				break;
			case SceneElementType.FieldItem:
			{
				chooseFieldItem(_chooseItemConfig);
			}
				break;
		}
	}

	/** 选择怪物 */
	public void chooseMonster(MonsterConfig config)
	{
		_chooseElementType=SceneElementType.Monster;
		_chooseMonsterConfig=config;

		cancelPick();
		cancelSelect();

		_tempData=createNewElement(SceneElementType.Monster,config.id);

		selectElement(_tempData);
		pickUpSelect();
	}

	/** 选择怪物 */
	public void chooseOperation(OperationConfig config)
	{
		_chooseElementType=SceneElementType.Operation;
		_chooseOperationConfig=config;

		cancelPick();
		cancelSelect();

		_tempData=createNewElement(SceneElementType.Operation,config.id);

		selectElement(_tempData);
		pickUpSelect();
	}

	/** 选择掉落物品 */
	public void chooseFieldItem(ItemConfig config)
	{
		_chooseElementType=SceneElementType.FieldItem;
		_chooseItemConfig=config;

		cancelPick();
		cancelSelect();

		_tempData=createNewElement(SceneElementType.FieldItem,config.id);
		_tempData.config.iArgs=new int[]{1};//1个

		selectElement(_tempData);
		pickUpSelect();
	}

	/** 获取一个可用的instanceID(会填补空位) */
	private int getOneElementInstanceID()
	{
		if(_elementInstanceID==-1)
			_elementInstanceID=0;

		while(_elementDic.contains(++_elementInstanceID))
		{

		}

		return _elementInstanceID;
	}

	protected void selectElement(SceneElementEditorData data)
	{
		if(_selectData!=data)
		{
			if(_selectData!=null)
			{
				cancelSelect();
			}

			_selectData=data;
		}

		//更新选择
		if(data!=null && data.gameObject!=null)
		{
			Selection.activeGameObject=data.gameObject;
		}
	}

	/** 取消选择 */
	private bool cancelSelect()
	{
		if(_selectData==null)
			return false;

		if(_selectData.isPicked)
		{
			cancelPick();
		}

		if(_selectData!=null)
		{
			doCancelSelect();
		}

		return true;
	}

	private void doCancelSelect()
	{
		if(Selection.activeGameObject==_selectData.gameObject)
		{
			Selection.activeGameObject=null;
		}

		_selectData=null;
	}

	/** 拿起选择对象 */
	private bool pickUpSelect()
	{
		if(_selectData==null)
			return false;

		if(_selectData.isPicked)
			return false;

		_selectData.isPicked=true;
		// Ctrl.print("pickUp");

		return true;
	}

	/** 选择落下 */
	private bool pickDown()
	{
		if(_selectData==null)
			return false;

		if(!_selectData.isPicked)
			return false;

		_selectData.isPicked=false;

		if(_selectData.isTemp)
		{
			_tempData=null;
			//变实体
			_selectData.isTemp=false;
			_selectData.config.instanceID=getOneElementInstanceID();
			_selectData.gameObject.name=getElementName(_selectData);

			//写入
			_elementDic.put(_selectData.config.instanceID,_selectData);
			_elementDicByObj.put(_selectData.gameObject,_selectData);
		}

		// Ctrl.print("pickDown");

		setPosFromGameObject(_selectData);

		return true;
	}

	/** 取消选中单位 */
	private bool cancelPick()
	{
		if(_selectData==null)
			return false;

		if(!_selectData.isPicked)
			return false;

		_selectData.isPicked=false;

		if(_selectData.isTemp)
		{
			_tempData=null;
			GameObject.DestroyImmediate(_selectData.gameObject);

			doCancelSelect();
		}
		else
		{
			setPosToGameObject(_selectData);
		}

		return true;
	}

	private bool deleteSelect()
	{
		if(_selectData!=null)
		{
			SceneElementEditorData selectData=_selectData;

			if(!selectData.isTemp)
			{
				cancelPick();
			}
			else
			{
				// cancelPick();
				cancelSelect();
				deleteElement(selectData);
			}

			return true;
		}

		return false;
	}

	/** 删除单位 */
	private void deleteElement(SceneElementEditorData data)
	{
		GameObject gameObject=data.gameObject;

		_elementDicByObj.remove(gameObject);
		_elementDic.remove(data.config.instanceID);

		_elementInstanceID=-1;

		try
		{
			GameObject.DestroyImmediate(gameObject);
		}
		catch(Exception e)
		{

		}
	}

	/** 清空场景编辑器数据 */
	public static void clearSceneEditorData()
	{
		//清空目录
		FileUtils.clearDir(ShineToolGlobal.scenePlacePath);

		FileUtils.deleteFile(ShineToolGlobal.serverSavePath + "/config/scenePlaceEditor.bin");
		FileUtils.deleteFile(ShineToolGlobal.clientSavePath + "/config/scenePlaceEditor.bin");

		FileUtils.deleteFile(ShineToolGlobal.serverSavePath+"/config/mapInfo.bin");
		FileUtils.deleteFile(ShineToolGlobal.clientSavePath+"/config/mapInfo.bin");

		//调用configExport
		ToolFileUtils.executeServerTool("configExport");

		Ctrl.print("清空完毕");
	}

	/** 保存当前地图信息 */
	private void saveMap()
	{
		if(_currentSceneData==null)
			return;

		if(CommonSetting.serverMapNeedGrid)
		{
			writeForGrid();
		}

		if(CommonSetting.serverMapNeedRecast)
		{
			int mapID=_currentSceneData.config.mapID;
			NavMeshExport.writeMesh(mapID);
		}
	}

	/** 读取格子 */
	protected virtual void readGrid()
	{
		float gridSize=Global.mapBlockSize;

		int mapID=_currentSceneData.config.mapID;

		BytesReadStream stream=FileUtils.readFileForBytesReadStream(getMapGridFilePath(mapID));

		PosData originPos=_originPos;
		PosData sizePos=_sizePos;

		int xNum=(int)Math.Ceiling((sizePos.x - originPos.x) / gridSize);
		int zNum=(int)Math.Ceiling((sizePos.z - originPos.z) / gridSize);

		int readXNum=stream.readLen();
		int readZNum=stream.readLen();

		if(readXNum!=xNum || readZNum!=zNum)
		{
			Ctrl.warnLog("旧的grid数据，因尺寸有变，只能使用部分");
		}

		_grids=new byte[xNum,zNum];

		if(CommonSetting.serverMapNeedSecondGrid)
			_secondGrids=new byte[xNum,zNum];

		byte secondV=0;

		for(int i=0;i<readXNum;i++)
		{
			for(int j=0;j<readZNum;j++)
			{
				byte v=stream.readByteB();

				if(CommonSetting.serverMapNeedSecondGrid)
					secondV=stream.readByteB();

				if(i<xNum && j<zNum)
				{
					_grids[i,j]=v;

					if(CommonSetting.serverMapNeedSecondGrid)
						_secondGrids[i,j]=secondV;
				}
			}
		}
	}

	protected virtual void writeForGrid()
	{
		float gridSize=Global.mapBlockSize;
		float halfGridSize=_halfGridSize=gridSize / 2;

		int mapID=_currentSceneData.config.mapID;

		PosData originPos=_originPos;
		PosData sizePos=_sizePos;

		int xNum=(int)Math.Ceiling((sizePos.x - originPos.x) / gridSize);
		int zNum=(int)Math.Ceiling((sizePos.z - originPos.z) / gridSize);

		BytesWriteStream stream=new BytesWriteStream(xNum*zNum+8);
		stream.writeLen(xNum);
		stream.writeLen(zNum);

		_grids=new byte[xNum,zNum];

		if(CommonSetting.serverMapNeedSecondGrid)
			_secondGrids=new byte[xNum,zNum];

		//0 1
		//2 3
		//4在中心
		int[] gs=new int[5];
		int[] rs=new int[2];
		int[] temp=new int[MapBlockType.size];

		float x0;
		float x1=originPos.x;

		for(int i=1;i<=xNum;i++)
		{
			x0=x1;
			x1=originPos.x + (gridSize * i);

			gs[2]=getBlockType(x0,originPos.z);
			gs[3]=getBlockType(x1,originPos.z);

			for(int j=1;j<=zNum;j++)
			{
				gs[0]=gs[2];
				gs[1]=gs[3];
				float gz=originPos.z + (gridSize * j);
				gs[2]=getBlockType(x0,gz);
				gs[3]=getBlockType(x1,gz);
				gs[4]=getBlockType(x0+halfGridSize,gz-halfGridSize);

				calculate(gs,rs,temp);

				//main
				stream.writeByte(rs[0]);
				//second
				stream.writeByte(rs[1]);

				_grids[i - 1,j - 1]=(byte)rs[0];

				if(CommonSetting.serverMapNeedSecondGrid)
					_secondGrids[i - 1,j - 1]=(byte)rs[1];
			}
		}

		FileUtils.writeFileForBytesWriteStream(getMapGridFilePath(mapID),stream);
	}

	protected void writeForRecastEmpty(BytesWriteStream stream)
	{
		stream.writeLen(0);
		stream.writeLen(0);
		stream.writeLen(0);
	}

	/** 获取阻挡类型 */
	protected virtual int getBlockType(float x,float z)
	{
		Vector3 re=BaseGameUtils.getTerrainPos(new Vector3(x,500,z));

		NavMeshHit hit;
		if(NavMesh.SamplePosition(re,out hit,_halfGridSize,NavMesh.AllAreas))
		{
			return MapBlockType.Land;
		}

		//TODO:补充后续阻挡类型

		return MapBlockType.Block;
	}

	/** 计算点结果 */
	private void calculate(int[] gs,int[] rs,int[] temp)
	{
		int main=-1;
		bool sure=false;

		int t;

		for(int i=0;i<gs.Length;i++)
		{
			t=++temp[gs[i]];

			if(!sure)
			{
				//超过3个点
				if(t>=3)
				{
					sure=true;
					main=gs[i];
				}
				else
				{
					//值更小
					if(main==-1 || main>gs[i])
					{
						main=gs[i];
					}
				}
			}
		}

		if(main==-1)
		{
			Ctrl.errorLog("不该出现找不到主点");
		}

		int second=0;

		for(int i=0;i<gs.Length;i++)
		{
			temp[gs[i]]=0;

			if(gs[i]!=main)
			{
				// if(second<=0)
				// {
				// 	second=gs[i];
				// }
				// else
				// {
				// 	if(second!=gs[i])
				// 	{
				// 		Ctrl.warnLog("出现3种以上类型同在一点",gs.ToString());
				// 	}
				// }

				//标记是半格
				second=1;
			}
		}

		rs[0]=main;
		rs[1]=second;
	}

	/** 写合并项 */
	private void writeMapCombine()
	{
		if(_sceneList==null)
			return;

		Ctrl.print("writeCombine");

		BytesWriteStream stream=new BytesWriteStream();

		IntObjectMap<SceneMapConfig> dic=SceneMapConfig.getDic();

		stream.writeLen(dic.size());

		foreach(int k in dic.getSortedKeyList())
		{
			SceneMapConfig config=dic.get(k);

			stream.writeInt(config.id);

			if(CommonSetting.serverMapNeedGrid || CommonSetting.clientMapNeedGrid)
			{
				string path=getMapGridFilePath(config.id);
				byte[] bytes=FileUtils.readFileForBytes(path);

				if(bytes!=null)
				{
					stream.writeLen(bytes.Length);
					stream.writeByteArr(bytes);
				}
				else
				{
					stream.writeLen(0);
				}

				if(CommonSetting.clientMapNeedGrid)
				{
					BytesWriteStream clientStream=new BytesWriteStream();
					BaseC.config.writeSplitConfigVersion(clientStream);
					clientStream.writeInt(config.id);

					if(bytes!=null)
					{
						clientStream.writeLen(bytes.Length);
						clientStream.writeByteArr(bytes);
					}
					else
					{
						clientStream.writeLen(0);
					}

					byte[] buf=clientStream.getBuf();
					int len=clientStream.length();

					if(CommonSetting.configNeedCompress)
						clientStream.compress();

					BytesReadStream rs=new BytesReadStream(clientStream.getBuf(),0,clientStream.length());
					rs.unCompress();

					byte[] rBuf=rs.getBuf();
					int rLen=rs.length();

					if(len==rLen)
					{
						bool isSame=true;

						for(int i=0;i<len;i++)
						{
							if(buf[i]!=rBuf[i])
							{
								isSame=false;
								break;
							}
						}

					}


					FileUtils.writeFileForBytesWriteStream(ShineToolGlobal.sourceCommonConfigDirPath + "/mapInfo/" + config.id + ".bin",clientStream);
				}
			}

			if(CommonSetting.serverMapNeedRecast)
			{
				string path=getMapNavFilePath(config.id);

				byte[] bytes=FileUtils.readFileForBytes(path);

				if(bytes!=null)
				{
					stream.writeLen(bytes.Length);
					stream.writeByteArr(bytes);
				}
				else
				{
					stream.writeLen(0);
				}
			}

		}

		//写出服务器端配置
		FileUtils.writeFileForBytesWriteStream(ShineToolGlobal.serverSavePath+"/config/mapInfo.bin",stream);

		//调用configExport
		ToolFileUtils.executeServerTool("configExport");
	}

	public void setVectorByGrid(ref Vector3 vec,int x,int z)
	{
		float gridSize=Global.mapBlockSize;

		float halfGridSize=_halfGridSize=gridSize / 2;

		vec.x=_originPos.x + x * Global.mapBlockSize + halfGridSize;
		vec.z=_originPos.z + z * Global.mapBlockSize + halfGridSize;
	}
}