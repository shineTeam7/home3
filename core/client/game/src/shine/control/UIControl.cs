using System.IO;
using Spine.Unity;
using UnityEngine;
using UnityEngine.UI;
#if UNITY_EDITOR
using UnityEditor;
#endif

namespace ShineEngine
{
	/// <summary>
	/// ui控制
	/// </summary>
	public class UIControl
	{
		private static GameObject _uiContainer;

		private static GameObject _uiRoot;

		private static GameObject _uiCamera;

		private static Camera _uiCameraCom;

		private static GameObject _uiSceneEffectLayer;

		private static LayerMask _uiLayer;
		
		private static GameObject _uiMask = null;

		/** 忽略触摸 */
		private static bool _touchEnabled=true;

		private static Rect _screenRect;

		public static void init()
		{
			_uiContainer=GameObject.Find(ShineSetting.uiContainerName);
			_uiRoot=GameObject.Find(ShineSetting.uiRootName);
			_uiCamera=GameObject.Find(ShineSetting.uiCameraName);
			_uiCameraCom=_uiCamera.GetComponent<Camera>();
			_uiSceneEffectLayer=GameObject.Find(ShineSetting.uiSceneEffectLayer);
			_uiLayer=LayerMask.NameToLayer("UI");

			Transform maskImageTrans = _uiRoot.transform.Find(ShineSetting.uiMaskName);

			if (maskImageTrans != null)
			{
				_uiMask=maskImageTrans.gameObject;
			}

			_screenRect=_uiCameraCom.rect;
		}

		/** ui根 */
		public static GameObject getUIContainer()
		{
			return _uiContainer;
		}

		/** ui根 */
		public static GameObject getUIRoot()
		{
			return _uiRoot;
		}

		/** ui摄像机 */
		public static GameObject getUICamera()
		{
			return _uiCamera;
		}

		/** ui摄像机对象 */
		public static Camera getUICameraComponent()
		{
			return _uiCameraCom;
		}

		/** UI上渲染的场景特效层 */
		public static GameObject getUISceneEffectLayer()
		{
			return _uiSceneEffectLayer;
		}

		/** ui层 */
		public static LayerMask getUILayer()
		{
			return _uiLayer;
		}
		
		/** uiMask */
		public static GameObject getUIMask()
		{
			return _uiMask;
		}
		
		/** 设置UI是否响应 */
		public static void setTouchEnabled(bool value)
		{
			if(_touchEnabled==value)
				return;

			_touchEnabled=value;

			if(value)
			{
				UITouchIgnoreCom cp=_uiRoot.GetComponent<UITouchIgnoreCom>();

				if(cp!=null)
					GameObject.DestroyImmediate(cp);
			}
			else
			{
				_uiRoot.AddComponent<UITouchIgnoreCom>();
			}
		}

		//--以下是Editor支持部分--//
		/** 处理一个prefab */
		public static void makeOnePrefab(GameObject obj)
		{
#if UNITY_EDITOR
			ImageLoader[] imageLoaders=obj.GetComponentsInChildren<ImageLoader>(true);
			foreach(ImageLoader imageLoader in imageLoaders)
			{
				imageLoader.source=null;

				Image image=imageLoader.GetComponent<Image>();
				if(image.sprite)
				{
					string path=Path.GetFileName(AssetDatabase.GetAssetPath(image.sprite));
					if(path=="unity_builtin_extra")
						continue;

					imageLoader.source=FileUtils.getBundleResourcePath(AssetDatabase.GetAssetPath(image.sprite));
					image.sprite=null;
				}
			}
			
			RawImageLoader[] rawImageLoaders=obj.GetComponentsInChildren<RawImageLoader>(true);
			foreach(RawImageLoader rawImageLoader in rawImageLoaders)
			{
				rawImageLoader.source=null;

				RawImage image=rawImageLoader.GetComponent<RawImage>();
				if(image.texture)
				{
					string path=Path.GetFileName(AssetDatabase.GetAssetPath(image.texture));
					if(path=="unity_builtin_extra")
						continue;

					rawImageLoader.source=FileUtils.getBundleResourcePath(AssetDatabase.GetAssetPath(image.texture));
					image.texture=null;
				}
			}

			SkeletonGraphicLoader[] skeletonGraphicLoaders=obj.GetComponentsInChildren<SkeletonGraphicLoader>(true);
			foreach(SkeletonGraphicLoader skeletonGraphicLoader in skeletonGraphicLoaders)
			{
				SkeletonGraphic skeletonGraphic=skeletonGraphicLoader.GetComponent<SkeletonGraphic>();
				if(skeletonGraphic.skeletonDataAsset)
				{
					skeletonGraphicLoader.source=FileUtils.getBundleResourcePath(AssetDatabase.GetAssetPath(skeletonGraphic.skeletonDataAsset));
					skeletonGraphic.skeletonDataAsset=null;
					skeletonGraphic.Clear();
				}
				else
				{
					skeletonGraphicLoader.source=null;
				}
			}

			SScrollView[] sScrollViews=obj.GetComponentsInChildren<SScrollView>(true);
			foreach(SScrollView scrollView in sScrollViews)
			{
				GameObject tmpObj=scrollView.gameObject;
				Transform tmpTrans=tmpObj.transform;
				SList<GameObject> tmpList=new SList<GameObject>();
				for(int i=0;i<tmpTrans.childCount;i++)
				{
					GameObject tmpGo=tmpTrans.GetChild(i).gameObject;
					if(tmpGo.name!="g0")
						tmpList.add(tmpGo);
				}
				for(var i=0;i<tmpList.Count;i++)
				{
					Object.DestroyImmediate(tmpList[i]);
				}
			}

			SPageView[] sPageViews=obj.GetComponentsInChildren<SPageView>(true);
			foreach(SPageView sPageView in sPageViews)
			{
				GameObject tmpObj=sPageView.gameObject;
				Transform tmpTrans=tmpObj.transform;
				SList<GameObject> tmpList=new SList<GameObject>();
				for(int i=0;i<tmpTrans.childCount;i++)
				{
					GameObject tmpGo=tmpTrans.GetChild(i).gameObject;
					if(tmpGo.name!="g0")
						tmpList.add(tmpGo);
				}
				for(var i=0;i<tmpList.Count;i++)
				{
					Object.DestroyImmediate(tmpList[i]);
				}
			}
#endif
		}
	}
}