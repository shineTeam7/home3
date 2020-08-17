using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 单位显示逻辑基类
/// </summary>
public class UnitShowLogic:UnitLogicBase
{
	private GameObject _gameObject;

	protected Transform _transform;
	/** 摄像机位置点 */
	protected Vector3 _cameraPosition=new Vector3();

	/** 当前动作id */
	protected int _actionID=MotionType.Idle;

	/** 显示模型ID */
	private int _showModelID=-1;
	/** 模型配置 */
	protected ModelConfig _modelConfig;
	/** 模型是否准备好 */
	protected bool _modelReady=false;
	/** 部件ID组 */
	private IntIntMap _partDic=new IntIntMap();
	/** 部件是否准备好 */
	private IntObjectMap<GameObject> _partReady=new IntObjectMap<GameObject>();

	/** 模型加载器 */
	private AssetPoolLoadTool _modelLoadTool;
	/** 部件加载器 */
	private IntObjectMap<AssetPoolLoadTool> _partLoadToolDic=new IntObjectMap<AssetPoolLoadTool>();

	/** 特效数目字典 */
	private IntIntMap _effectNumDic=new IntIntMap();
	/** 特效列表(因为特效同时存在不多，所以用List) */
	private SList<UnitEffect> _effectList=new SList<UnitEffect>();

	//motionUse
	protected int _currentMotionID=-1;

	private int _currentMotionUseID=-1;

	private float _currentMotionSpeed;

	private float _currentMotionMaxTime;

	private float _currentMotionPassTime;

	private bool _currentIsLoop;

	//temp
	private Vector3 _tempVector=new Vector3();

	public UnitShowLogic()
	{

	}

	public override void construct()
	{
		_modelLoadTool=new AssetPoolLoadTool(AssetPoolType.UnitModel,modelLoadOver,null);
	}

	public override void init()
	{
		base.init();

		//TODO:绑定层级

		_gameObject=AssetPoolControl.getCustomAsset(AssetPoolType.UnitMain,AssetCustomType.UnitMainObj);

		_gameObject.name="unit_" + _unit.instanceID;
		_gameObject.SetActive(true);
		_gameObject.layer=_unit.isHero ? LayerType.Hero : LayerType.Unit;
		_gameObject.transform.SetParent(_scene.show.getUnitLayer().transform);

		_transform=_gameObject.transform;

		_actionID=MotionType.Idle;

		initMainShow();

		if(_unit.canFight())
		{
			BuffData[] values=_data.fightDataLogic.buff.getBuffDatas().getValues();
			BuffData bData;

			for(int i=values.Length-1;i>=0;--i)
			{
				if((bData=values[i])!=null)
				{
					addBuffShow(bData);
				}
			}
		}

		playMotionAbs(_actionID);

	}

	public override void afterInit()
	{
		base.afterInit();

		updateShow();
	}

	public override void dispose()
	{
		if(!_effectList.isEmpty())
		{
			SList<UnitEffect> unitEffects=_effectList;
			UnitEffect effect;

			for(int i=unitEffects.size()-1;i>=0;--i)
			{
				effect=unitEffects[i];
				effect.dispose();
				GameC.pool.unitEffectPool.back(effect);
			}

			unitEffects.clear();
		}

		_effectNumDic.clear();

		disposeMainShow();

		//析构GameObject
		_transform=null;

		//TODO:拆掉Part


		_modelLoadTool.clear();

		AssetPoolLoadTool[] values;
		AssetPoolLoadTool v;

		for(int i=(values=_partLoadToolDic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.clear();
			}
		}

		AssetPoolControl.unloadOne(AssetPoolType.UnitMain,AssetCustomType.UnitMainObj,_gameObject);
		_gameObject=null;

		_actionID=MotionType.Idle;
		_showModelID=-1;
		_modelReady=false;
		_partDic.clear();
		_partReady.clear();

		clearMotion();

		base.dispose();
	}

	public GameObject gameObject
	{
		get {return _gameObject;}
	}

	public Transform transform
	{
		get {return _transform;}
	}

	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		//非循环动作
		if(_currentMotionID>0 && !_currentIsLoop)
		{
			if((_currentMotionPassTime+=delay)>=_currentMotionMaxTime)
			{
				_currentMotionPassTime=0;

				motionOver();
			}
		}
	}

	//--位置部分--//

	/** 从数据更新显示 */
	protected virtual void updateShow()
	{
		//设置坐标/朝向
		setPos(_unit.pos.getPos());
		setDir(_unit.pos.getDir());

		//TODO:这里需要根据3D的实现,看是修改为走帧，还是走时间，现在尚未同步好

		if(_currentMotionID>0)
		{
			rePlayMotion();
		}
	}

	/** 显示层设置坐标 */
	public void setPos(PosData pos)
	{
		doSetPos(pos);
	}

	protected virtual void doSetPos(PosData pos)
	{
		if(_transform!=null)
		{
			pos.setToVector(ref _tempVector);
			_transform.position=_tempVector;

			calculateCameraPosition();

			//主角
			if(_unit.isHero)
			{
				_scene.camera.updateHeroPos(_cameraPosition);
			}
		}
	}

	/** 只设置朝向 */
	public void setDir(DirData dir)
	{
		doSetDir(dir);

		SList<UnitEffect> unitEffects=_effectList;

		for(int i=0,len=unitEffects.length();i<len;++i)
		{
			unitEffects[i].onSetDir(dir);
		}
	}

	protected virtual void doSetDir(DirData dir)
	{
		
	}

	//--主显示部分--//

	/** 初始化主显示 */
	protected virtual void initMainShow()
	{
		bool hasModel=false;

		if(_data.avatar!=null && _data.avatar.modelID>0)
		{
			hasModel=true;

			setModel(_data.avatar.modelID);

			_data.avatar.parts.forEach((k,v)=>
			{
				setPart(k,_unit.avatar.getShowPart(k));

			});
		}

		if(BaseC.constlist.unit_canFight(_data.identity.type))
		{
			int fightUnitID=_data.getFightIdentity().getFightUnitID();

			if(fightUnitID>0)
			{
				FightUnitConfig fConfig=FightUnitConfig.get(fightUnitID);

				if(!hasModel && fConfig.modelID>0)
				{
					setModel(fConfig.modelID);
				}

				if(fConfig.effectID>0)
				{
					addEffect(fConfig.effectID);
				}
			}
		}

		//特殊的

		switch(_unit.type)
		{
			case UnitType.SceneEffect:
			{
				SceneEffectIdentityData iData=_data.getSceneEffectIdentity();

				if(iData.signedEffectID>0)
				{
					addOrPlayEffect(iData.signedEffectID);
				}
				else
				{
					SceneEffectConfig config=SceneEffectConfig.get(((SceneEffectIdentityData)_data.identity).signedEffectID);

					if(config.modelID>0)
					{
						setModel(config.modelID);
					}

					if(config.effectID>0)
					{
						addOrPlayEffect(config.effectID);
					}
				}
			}
				break;
			case UnitType.FieldItem:
			{
				FieldItemIdentityData iData=(FieldItemIdentityData)_data.identity;

				ItemConfig itemConfig=ItemConfig.get(iData.item.id);

				if(itemConfig.fieldItemModelID>0)
				{
					setModel(itemConfig.fieldItemModelID);
				}
			}
				break;
		}
	}

	/** 析构主显示 */
	protected virtual void disposeMainShow()
	{
		
	}

	/** 获取单位高度 */
	public float getUnitHeight()
	{
		if(_modelConfig!=null)
			return _modelConfig.height;

		return 0;
	}

	/** 设置显示模型 */
	public void setModel(int modelID)
	{
		if(_showModelID==modelID)
			return;

		_showModelID=modelID;
		_modelConfig=ModelConfig.get(modelID);

		loadModel();
	}

	protected virtual void loadModel()
	{
		if(ShineSetting.isWholeClient)
		{
			_modelLoadTool.loadOne(_modelConfig.sourceT);
		}
		else
		{
			modelLoadOver(null);
		}
	}

	protected void modelLoadOver(GameObject obj)
	{
		if(!_unit.enabled)
			return;

		_modelReady=true;

		onModelLoadOver(_showModelID,obj);

		foreach(var kv in _partReady.entrySet())
		{
			onPartLoadOver(kv.key,_partDic.get(kv.key),kv.value);
		}
	}

	/** 模型加载完毕 */
	protected virtual void onModelLoadOver(int modelID,GameObject obj)
	{

	}

	/** 获取部件加载器 */
	private AssetPoolLoadTool getPartLoadTool(int type)
	{
		AssetPoolLoadTool tool=_partLoadToolDic.get(type);

		if(tool==null)
		{
			_partLoadToolDic.put(type,tool=new AssetPoolLoadTool(AssetPoolType.UnitPart));
		}

		return tool;
	}

	/** 设置部件 */
	public void setPart(int type,int partID)
	{
		//空部件不处理
		if(partID<=0)
			return;

		_partDic.put(type,partID);

		_partReady.remove(type);

		AssetPoolLoadTool tool=getPartLoadTool(type);

		AvatarPartConfig config=AvatarPartConfig.get(type,partID);
		tool.loadOne(config.sourceT,obj=>partLoadOver(type,partID,obj));
	}

	private void partLoadOver(int type,int partID,GameObject obj)
	{
		if(_unit.avatar.getShowPart(partID)!=partID)
			return;

		obj.SetActive(false);
		_partReady.put(type,obj);

		//必须等模型准备好
		if(!_modelReady)
			return;

		onPartLoadOver(type,partID,obj);
	}

	/** 部件加载完毕 */
	protected virtual void onPartLoadOver(int type,int partID,GameObject obj)
	{

	}

	//--动作部分--//

	/** 当前动作ID */
	public int getActionID()
	{
		return _actionID;
	}

	/** 获取当前的模型动作配置 */
	public ModelMotionConfig getModelActionConfig()
	{
		if(_actionID==-1)
			return null;

		return ModelMotionConfig.get(_unit.avatar.getModelID(),_actionID);
	}

	/** 播放动作 */
	public void playMotion(int id,bool isLoop=true)
	{
		playMotion(id,1f,isLoop);
	}

	/** 播放动作(如当前不是再播放) */
	public void playMotion(int id,float speed,bool isLoop)
	{
		if(_actionID==id)
			return;

		playMotionAbs(id,speed,0f,isLoop);
	}

	/** 播放动作(立即更新) */
	public void playMotionAbs(int id,bool isLoop=true)
	{
		playMotionAbs(id,1f,0f,isLoop);
	}

	/** 播放动作(立即更新) */
	public void playMotionAbs(int id,float speed,float startTime,bool isLoop)
	{
		_actionID=id;

		prePlayMotion(id,speed,startTime,isLoop);
	}

	private void prePlayMotion(int id,float speed,float startTime,bool isLoop)
	{
		int modelID;

		if((modelID=_data.avatar!=null ? _data.avatar.modelID : -1)<=0)
			return;

		_currentMotionID=id;

		ModelMotionConfig config=findUseMotionConfig(modelID,id);

		if(config!=null)
		{
			id=config.id;
			_currentMotionMaxTime=config.motionTime;
		}
		else
		{
			_currentMotionMaxTime=MotionConfig.get(id).defaultMotionTime;
		}

		_currentMotionUseID=id;
		_currentMotionSpeed=speed;
		_currentMotionPassTime=startTime;
		_currentIsLoop=isLoop;

		toPlayMotion(id,speed,startTime,isLoop);
	}

	protected void clearMotion()
	{
		_currentMotionID=-1;
		_currentMotionUseID=-1;
		_currentMotionSpeed=1f;
		_currentMotionPassTime=0;
		_currentMotionMaxTime=0;
		_currentIsLoop=false;
	}

	protected virtual void motionOver()
	{
		clearMotion();
	}

	private void rePlayMotion()
	{
		toPlayMotion(_currentMotionUseID,_currentMotionSpeed,_currentMotionPassTime,_currentIsLoop);
	}

	/** 找到可用的动作配置 */
	private ModelMotionConfig findUseMotionConfig(int modelID,int id)
	{
		ModelMotionConfig config=ModelMotionConfig.get(modelID,id);

		if(config==null)
		{
			int backID;

			MotionConfig mConfig;

			if((mConfig=MotionConfig.get(id))==null)
				return null;

			if((backID=mConfig.backID)==id)
				return null;

			return findUseMotionConfig(modelID,backID);
		}

		return config;
	}

	/** 执行播放动作 */
	protected virtual void toPlayMotion(int id,float speed,float startFrame,bool isLoop)
	{

	}

	/** 暂停动作 */
	public virtual void pauseAction()
	{

	}

	/** 重播动作 */
	public virtual void resumeAction()
	{

	}

	/** 修改当前动作帧率 */
	public virtual void setSpeed(float speed)
	{

	}

	//--特效部分--//

	/** 添加特效 */
	public void addEffect(int id)
	{
		EffectConfig config=EffectConfig.get(id);

		if(config.isOnce)
		{
			Ctrl.throwError("不能添加一次性特效",id);
			return;
		}

		toAddEffect(config);
	}

	/** 播放特效 */
	public void playEffect(int id)
	{
		EffectConfig config=EffectConfig.get(id);

		if(!config.isOnce)
		{
			Ctrl.throwError("不能播放持续特效",id);
			return;
		}

		toAddEffect(config);
	}

	/** 添加或播放特效(场景特效用) */
	public void addOrPlayEffect(int id)
	{
		toAddEffect(EffectConfig.get(id));
	}

	private void toAddEffect(EffectConfig config)
	{
		int num=_effectNumDic.addValue(config.id,1);

		//超了
		if(config.plusMax>0 && num>config.plusMax)
			return;

		UnitEffect effect=GameC.pool.unitEffectPool.getOne();
		effect.setConfig(config);
		effect.setScene(_scene);
		effect.bindUnit(_unit);

		_effectList.add(effect);

		//开始
		effect.init();
	}

	/** 移除特效 */
	public void removeEffect(int id)
	{
		int num=_effectNumDic.get(id);

		if(num<=0)
			return;

		_effectNumDic.put(id,--num);

		EffectConfig config=EffectConfig.get(id);

		//还超
		if(config.plusMax>0 && num>=config.plusMax)
			return;

		SList<UnitEffect> unitEffects=_effectList;
		UnitEffect effect;

		for(int i=0,len=unitEffects.length();i<len;++i)
		{
			effect=unitEffects[i];

			//是
			if(effect.id==id)
			{
				unitEffects.remove(i);
				effect.dispose();
				GameC.pool.unitEffectPool.back(effect);
				break;
			}
		}
	}

	/** 特效加载完毕 */
	public virtual void effectLoadOver(UnitEffect effect)
	{

	}

	/** 特效播放完毕 */
	public void effectPlayOver(UnitEffect effect)
	{
		int id=effect.id;

		int num=_effectNumDic.get(id);

		if(num<=0)
		{
			Ctrl.throwError("出错,effect数目小于0");
			return;
		}

		_effectNumDic.put(id,--num);

		_effectList.removeObj(effect);

		effect.dispose();
		GameC.pool.unitEffectPool.back(effect);

		//场景特效
		if(_unit.type==UnitType.SceneEffect)
		{
			SceneEffectIdentityData iData=_data.getSceneEffectIdentity();

			if(iData.signedEffectID>0)
			{
				if(id==iData.signedEffectID)
				{
					sceneEffectOver();
				}
			}
			else
			{
				SceneEffectConfig config=SceneEffectConfig.get(iData.id);

				if(config.effectID>0)
				{
					sceneEffectOver();
				}
			}
		}
	}

	/** 场景特效结束 */
	private void sceneEffectOver()
	{
		//移除
		_scene.removeUnit(_unit.instanceID);
	}

	/** 添加buff显示 */
	public void addBuffShow(BuffData data)
	{
		int effectID;
		if((effectID=data.config.effect)>0)
		{
			addEffect(effectID);
		}
	}

	/** 移除buff显示 */
	public void removeBuffShow(BuffData data)
	{
		int effectID;
		if((effectID=data.config.effect)>0)
		{
			removeEffect(effectID);
		}
	}

	public virtual void onAttributeChange(bool[] changeSet)
	{
		_unit.head?.onAttributeChange(changeSet);
	}

	public virtual void onDamage(Unit from,AttackConfig config,DamageOneData data)
	{
		if(config.hitEffect>0)
		{
			_unit.show.playEffect(config.hitEffect);
		}

		_scene.show.onUnitDamage(_unit,from,config,data);
	}

	/** 刷新角色外显数据 */
	public void onRefreshRoleShowData(RoleShowChangeData data)
	{
		switch(data.type)
		{
			case RoleShowDataPartType.Name:
			{
				_unit.head?.onRefreshRoleName();
			}
				break;
			case RoleShowDataPartType.Sex:
			{
				//				_unit.head.onRefreshRoleName();
			}
				break;
			case RoleShowDataPartType.Vocation:
			{
				//				_unit.head.onRefreshRoleName();
			}
				break;
			case RoleShowDataPartType.Level:
			{
				//其他单位升级特效(目前暂时不考虑降级的情况)
			}
				break;
		}
	}

	//点相关

	/** 获取摄像机位置点 */
	public Vector3 getCameraPosition()
	{
		return _cameraPosition;
	}

	/** 计算摄像机位置点 */
	protected virtual void calculateCameraPosition()
	{
		_cameraPosition=_transform.localPosition;
		_cameraPosition.y+=getUnitHeight()/2;
	}

	/** 获取单位头位置 */
	public Vector3 getUnitHeadPos()
	{
		Vector3 vec=_transform.position;
		vec.y+=getUnitHeight();
		return vec;
	}
}