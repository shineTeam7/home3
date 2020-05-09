using System;
using ShineEngine;
using UnityEngine;
using UnityEngine.AI;
using UnityEngine.UI;
using Object = UnityEngine.Object;

/// <summary>
/// 基础游戏方法
/// </summary>
public class BaseGameUtils
{
	private static char _replaceChar= '*';
	private static char[] _ignoreChars= {	'　', ' ', '*', '-', '_', '+', '/', '.', '(', ')', '&', '%', '$', '#', '@', '!' };

	private static ACStringFilter _strFilter=new ACStringFilter(_ignoreChars,_replaceChar);

	//
	private static RaycastHit _tempHit;
	private static RaycastHit[] _tempHits;
	private static Ray _tempRay = new Ray(Vector3.zero, Vector3.down);

	public static void initStrFilter(SList<string> list)
	{
		_strFilter.init(list);
	}

	/** 是否有敏感词 */
	public static bool hasSensitiveWord(string str)
	{
		return _strFilter.contain(str);
	}

	/** 是否有敏感词 */
	public static string replaceSensitiveWord(string str)
	{
		return _strFilter.replace(str);
	}

	/** 计算一个变量值 */
	public static int calculateOneSkillVarValue(int[] args,UnitFightDataLogic self,UnitFightDataLogic target)
	{
		int key;

		if((key=args[0])==SkillVarSourceType.ConstValue)
			return args[1];

		if(BaseC.constlist.skillVarSource_isTarget(key))
		{
			if(target==null)
				return 0;

			return target.getSkillVarSourceValue(args,false);
		}
		else
		{
			if(self==null)
				return 0;

			return self.getSkillVarSourceValue(args,true);
		}
	}

	/** 计算技能变量值(完整) */
	public static int calculateSkillVarValueFull(int varID,UnitFightDataLogic self,UnitFightDataLogic target)
	{
		return calculateSkillVarValueFull(SkillVarConfig.get(varID),self,target);
	}

	/** 计算技能变量值(完整) */
	public static int calculateSkillVarValueFull(SkillVarConfig config,UnitFightDataLogic self,UnitFightDataLogic target)
	{
		return BaseC.logic.calculateSkillVar(config.formulaType,config.args,self,target,null,0);
	}

	/** 计算技能变量值(完整) */
	public static int calculateSkillVarValueForSelf(SkillVarConfig config,int[] selfValues,int start,UnitFightDataLogic target)
	{
		return BaseC.logic.calculateSkillVar(config.formulaType,config.args,null,target,selfValues,start);
	}

	/** 获取目标地面位置 */
	public static Vector3 getTerrainPos(in Vector3 vec)
	{
		return getTerrainPos(vec,CommonSetting.findTerrainHeight);
	}

	/** 获取目标地面位置 */
	public static void makeTerrainPos(PosData pos)
	{
		if(getHeight(pos.x,pos.z,out float height,LayerType.TerrainMask,pos.y+CommonSetting.findTerrainHeight))
		{
			pos.y=height;
		}
	}

	/** 获取目标地面位置 */
	public static Vector3 getTerrainPos(in Vector3 vec,float fromHeight)
	{
		getHeight(vec.x,vec.z, out float height, LayerType.TerrainMask, vec.y+fromHeight);
		return new Vector3(vec.x, height, vec.z);
	}

	/** 某位置是否可用 */
	public static bool isPosEnabled(int moveType,PosData pos)
	{
		Vector3 re=getTerrainPos(new Vector3(pos.x,200,pos.z));

		NavMeshHit hit;
		if(NavMesh.SamplePosition(re,out hit,Global.mapSamplePositionRadius,BaseC.constlist.mapMoveType_getMask(moveType)))
		{
			return true;
		}

		return false;
	}

	/** 获取高度 */
	public static bool getHeight(float x, float z, out float y, int mask,float fromHeight)
	{
		_tempRay.origin = new Vector3(x, fromHeight, z);
		Physics.Raycast(_tempRay, out _tempHit, float.MaxValue, mask);

		if (_tempHit.collider != null)
		{
			// Debug.DrawLine(new Vector3(x, fromHeight, z), _tempHit.point,Color.cyan);
			y = _tempHit.point.y;
			return true;
		}

		y = 0;
		return false;
	}

	/** 生成playerID */
	public static long makePlayerID(int areaID,int index)
	{
		if(index >= CommonSetting.areaRegistMax)
		{
			Ctrl.throwError("没有新角色ID了");
			return -1L;
		}

		//单服1亿注册上限
		return (long)areaID * CommonSetting.areaRegistMax + index;
	}

	/** 生成自定义playerID */
	public static long makeCustomPlayerID(int type,int index)
	{
		return makePlayerID(CommonSetting.areaMax+type,index);
	}

	/** 从playerID上反解areaID(创建服) */
	public static int getAreaIDByPlayerID(long playerID)
	{
		return (int)(playerID/CommonSetting.areaRegistMax);
	}

	/** 移除imagematerial */
	public static void removeMaterial(Image img)
	{
		if (img == null)
		{
			return;
		}

		img.material = null;
	}
	
	/** 置灰(找到图片) */
	public static void turnGray(Image img)
	{
		if (CommonSetting.colorGrayMat.Equals(""))
			return;
		
		addImageMaterial(img, CommonSetting.colorGrayMat);
	}
	
	/** 添加imagematerial */
	private static void addImageMaterial(Image img, string materialName)
	{
		if (img == null)
		{
			return;
		}

		LoadControl.loadOne(materialName , () =>
		{
			Material material = LoadControl.getResource(materialName) as Material;
			if (material == null)
			{
				Ctrl.print("addImageMaterial material not exist!", materialName);
				return;
			}
			img.material = material;
		});
	}

	/** 获取UI模型的资源ID */
	public static int getUIModelResourceID(string name,bool needCheck=true)
	{
		if(ShineSetting.localLoadWithOutBundle)
		{
			return LoadControl.getResourceIDByName("ui/models/"+name+".prefab",needCheck);
		}
		else
		{
			return LoadControl.getResourceIDByName("ui/generateModels/"+name+".prefab",needCheck);
		}
	}

	/** 获取url替换 */
	public static string getURLReplace(string url)
	{
		if(ShineSetting.localLoadWithOutBundle)
		{
			return url.Replace("/generateModels/","/models/");
		}
		else
		{
			return url.Replace("/models/","/generateModels/");
		}
	}
	
	/** 从数组中随机指定数量数据 */
	public static SList<T> randomDataList<T>(SList<T> list,int num)
	{
		IntList indexList=new IntList(list.size());
		
		for(int i=0;i<list.length();++i)
		{
			indexList.add(i);
		}
		
		IntList resList=new IntList(num);
		
		for(int i=0;i<num;i++)
		{
			if(indexList.length()==0)
				break;
			
			int index = MathUtils.randomRange(0,indexList.length());
			resList.add(indexList.get(index));
			indexList.remove(index);
		}
		
		SList<T> resultList = new SList<T>(indexList.length());
		
		for(int i=0;i<resList.length();++i)
		{
			resultList.add(list.get(resList.get(i)));
		}
		
		return resultList;
	}

	/** 获取屏幕位置 射线与地面交点 */
	public static Vector3 getScreenToTerrainPoint(Vector2 pos,float terrainHeight=0f)
	{
		Ray ray=CameraControl.mainCamera.camera.ScreenPointToRay(pos);

		Vector3 dir=ray.direction;

		if(dir.y.Equals(0))
			return Vector3.zero;

		float num=(terrainHeight - ray.origin.y) / dir.y;

		return ray.origin + ray.direction * num;
	}
}