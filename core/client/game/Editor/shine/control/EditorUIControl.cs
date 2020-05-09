using System;
using ShineEngine;
using ShineEditor;
using System.Collections.Generic;
using System.IO;
using UnityEditor;
using UnityEngine;
using Spine.Unity;
using Spine.Unity.Editor;
using UnityEditor.SceneManagement;
using UnityEditor.UI;
using UnityEngine.SceneManagement;
using UnityEngine.UI;
using Object = UnityEngine.Object;

namespace ShineEditor
{
	/// <summary>
	/// 工具UI控制
	/// </summary>
	public class EditorUIControl
	{
		/** 模型生成路径 */
		private static string _uiModelGeneratePath=ToolFileUtils.getAssetsPath(ShineToolGlobal.uiGenerateModelsPath);

		public static void CreateFramaeContainerGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("frameContainer",typeof(FrameContainer));
			gameObject.transform.SetParent(parentTransform,false);
			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateBloodBarGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("bloodBar",typeof(BloodBar));
			gameObject.transform.SetParent(parentTransform,false);
			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateI18NTextGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("i18nText",typeof(I18NText));
			gameObject.transform.SetParent(parentTransform,false);
			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateAdvancedTextGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("advancedText",typeof(AdvancedText));
			gameObject.transform.SetParent(parentTransform,false);
			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateSScrollViewGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("sScrollView",typeof(SScrollView));
			gameObject.transform.SetParent(parentTransform,false);

			//默认mask方式遮罩
			gameObject.AddComponent<Image>();
			gameObject.AddComponent<Mask>().showMaskGraphic=false;

			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateSCustomScrollViewGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("sCustomScrollView",typeof(SCustomScrollView));
			gameObject.transform.SetParent(parentTransform,false);
			Mask mask=gameObject.GetComponent<Mask>();
			if(mask)
				mask.showMaskGraphic=false;
			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}
		
		public static void CreateSPageViewGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("sPageView",typeof(SPageView));
			gameObject.transform.SetParent(parentTransform,false);
			Mask mask=gameObject.GetComponent<Mask>();
			if(mask)
				mask.showMaskGraphic=false;
			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateImageFrameContainerGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("imageFrameContainer",typeof(ImageFrameContainer));
			gameObject.transform.SetParent(parentTransform,false);
			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateGuideMaskGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("guideMask",typeof(GuideMask));
			RectTransform objTransform=(RectTransform)gameObject.transform;
			objTransform.SetParent(parentTransform,false);
			objTransform.anchorMin=Vector2.zero;
			objTransform.anchorMax=Vector2.one;
			objTransform.offsetMax=Vector2.zero;
			objTransform.offsetMin=Vector2.zero;

			GameObject hallow=new GameObject("hollow",typeof(RectTransform));
			RectTransform hallowTransform=(RectTransform)hallow.transform;
			hallowTransform.SetParent(objTransform);
			hallowTransform.localPosition=Vector3.zero;

			gameObject.GetComponent<GuideMask>().hollow=(RectTransform)hallow.transform;

			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateImageLoaderGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("imageLoader",typeof(ImageLoader));
			gameObject.transform.SetParent(parentTransform,false);
			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateSkeletonGraphicLoaderGameObject()
		{
			GameObject parentGameObject = Selection.activeObject as GameObject;
			RectTransform parentTransform = parentGameObject == null ? null : parentGameObject.GetComponent<RectTransform>();

			if (parentTransform == null)
				Debug.LogWarning("Your new SkeletonGraphic will not be visible until it is placed under a Canvas");

			GameObject gameObject=new GameObject("skeletonGraphicLoader",typeof(SkeletonGraphicLoader));
			SkeletonGraphic graphic = gameObject.GetComponent<SkeletonGraphic>();
			graphic.material = SkeletonGraphicInspector.DefaultSkeletonGraphicMaterial;

			gameObject.transform.SetParent(parentTransform, false);
			EditorUtility.FocusProjectWindow();
			Selection.activeObject = gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateElement()
		{
			Transform parentTransform=GameObject.Find("uiElements").transform;

			GameObject gameObject=new GameObject("element",typeof(RectTransform));
			gameObject.transform.SetParent(parentTransform);

			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateModel()
		{
			Transform parentTransform=GameObject.Find("uiModels").transform;

			GameObject gameObject=new GameObject("model",typeof(RectTransform));
			gameObject.transform.SetParent(parentTransform);
			gameObject.AddComponent<Canvas>().renderMode = RenderMode.ScreenSpaceCamera;
			gameObject.AddComponent<CanvasScaler>().uiScaleMode = CanvasScaler.ScaleMode.ScaleWithScreenSize;
			gameObject.AddComponent<GraphicRaycaster>();
			RectTransform transform = (RectTransform)gameObject.transform;
			transform.anchorMin = Vector2.zero;
			transform.anchorMax = Vector2.one;
			transform.anchoredPosition = Vector2.zero;
			transform.sizeDelta = Vector2.zero;

			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}

		public static void CreateRawImageLoaderGameObject()
		{
			GameObject parentGameObject=Selection.activeObject as GameObject;
			Transform parentTransform=parentGameObject==null ? null : parentGameObject.transform;

			GameObject gameObject=new GameObject("rawImageLoader",typeof(RawImageLoader));
			gameObject.transform.SetParent(parentTransform,false);
			EditorUtility.FocusProjectWindow();
			Selection.activeObject=gameObject;
			EditorGUIUtility.PingObject(Selection.activeObject);
		}
		
//		[MenuItem("GameObject/Create UI SPrefab",false,11)]
//		static void CreatSPrefab()
//		{
//			GameObject gameObject=Selection.activeObject as GameObject;
//			if(gameObject!=null)
//			{
//				string rootPath;
//				if(gameObject.transform.parent.name=="uiElements")
//					rootPath=ShineToolGlobal.uiElementsPath;
//				else if(gameObject.transform.parent.name=="uiModels")
//					rootPath=ShineToolGlobal.uiModelsPath;
//				else
//				{
//					EditorUtility.DisplayDialog("生成失败","仅支持UIModel和UIElement的生成","确定");
//					return;
//				}
//
//				string path = ToolFileUtils.getAssetsPath(rootPath + "/" + gameObject.name + ".prefab");
//				if(!File.Exists(path))
//				{
//					gameObject.AddComponent<SPrefab>().InitGUID();
//					PrefabUtility.CreatePrefab(path,gameObject,ReplacePrefabOptions.ConnectToPrefab);
//					//下面的写法崩溃，不知为何
////					GameObject prefab=PrefabUtility.CreatePrefab(path,gameObject);
////					PrefabUtility.ConnectGameObjectToPrefab(gameObject,prefab);
//				}
//				else
//				{
//					EditorUtility.DisplayDialog("生成失败","已存在同名prefab","确定");
//				}
//			}
//			else
//			{
//				EditorUtility.DisplayDialog("生成失败","请选择一个GameObject","确定");
//			}
//		}



		/** 当前是否在UI场景 */
		public static bool isInUIScene()
		{
			return SceneManager.GetActiveScene().name.Equals("ui");
		}
		/** 确认当前在UI场景 */
		public static void ensureUIScene()
		{
			Ctrl.print(SceneManager.GetActiveScene().name);

			if(!isInUIScene())
			{
				Ctrl.throwError("请先切换到UI场景");
			}
		}

		/// <summary>
		/// 生成UI预制
		/// </summary>
		/// <param name="force">是否强制全部生成（如果否，则增量生成，只生成改变量）</param>
		public static void make(bool force=false)
		{
			EditorUtility.DisplayProgressBar("请耐心等待","正在抽离UI预制体...",0.0f);

			/** 需要处理的预制体列表 */
			SMap<string,long> generateList=new SMap<string,long>();
			/** 全部预处理列表(包含当前所有的预制体修改记录，用于生成记录文件) */
			SMap<string,long> allGenerateList=new SMap<string,long>();

			if(!Directory.Exists(ShineToolGlobal.uiModelsPath))
			{
				Directory.CreateDirectory(ShineToolGlobal.uiModelsPath);
			}

			if(!Directory.Exists(ShineToolGlobal.uiGenerateModelsPath))
			{
				Directory.CreateDirectory(ShineToolGlobal.uiGenerateModelsPath);
				force=true;
			}

			//生成新预处理列表
			string[] files=FileUtils.getDeepFileList(ShineToolGlobal.uiModelsPath,"prefab");
			int pathLen=ShineToolGlobal.gamePath.Length + 1;
			for(int i=0;i<files.Length;i++)
			{
				string file=files[i];
				long time=File.GetLastWriteTime(file).Ticks;
				file=file.Substring(pathLen);
				generateList.put(file,time);
				allGenerateList.put(file,time);
			}

			//读取旧预处理列表
			BytesReadStream stream=FileUtils.readFileForBytesReadStream(ShineToolGlobal.uiRecordPath);

			//读取旧预处理列表
			if(force || stream==null || !stream.checkVersion(ShineToolGlobal.uiRecordVersion))
			{
				FileUtils.clearDir(ShineToolGlobal.uiGenerateModelsPath);
			}
			else
			{
				/** 旧预处理列表 */
				SMap<string,long> oldGenerateList=new SMap<string,long>();

				int len=stream.readInt();
				for(int i=0;i<len;++i)
				{
					oldGenerateList[stream.readUTF()]=stream.readLong();
				}

				//比较新旧预制列表，并删除无效预制
				oldGenerateList.forEach((k,v)=>
				{
					long newTime;
					string genPath=ShineToolGlobal.uiGenerateModelsPath + "/";
					if(generateList.tryGetValue(k,out newTime))  //如果新列表里有
					{
						if(newTime==oldGenerateList[k])  //如果未修改，则不用抽离
						{
							generateList.remove(k);
						}
						else  //如果已修改
						{
							File.Delete(genPath + Path.GetFileName(k));
						}
					}
					else  //新列表里没有，说明已删除
					{
						File.Delete(genPath + Path.GetFileName(k));
					}
				});
			}

			//生成UI预制
			int progressNum=generateList.length() + 1;
			int curNum=0;

			generateList.forEach((k,v)=>
			{
				generateUIPrefab(k);
				++curNum;
				EditorUtility.DisplayProgressBar("请耐心等待","正在抽离UI预制体...",(float)curNum / progressNum);
			});

			//写入记录
			BytesWriteStream buffer=new BytesWriteStream();
			buffer.writeVersion(ShineToolGlobal.uiRecordVersion);

			buffer.writeInt(allGenerateList.Count);

			allGenerateList.forEach((k,v)=>
			{
				buffer.writeUTF(k);
				buffer.writeLong(allGenerateList[k]);
			});

			FileUtils.writeFileForBytes(ShineToolGlobal.uiRecordPath,buffer);

			EditorUtility.ClearProgressBar();
		}

		/// <summary>
		/// 预处理预制体
		/// </summary>
		/// <param name="file"></param>
		private static void generateUIPrefab(string file)
		{
			Object asset=AssetDatabase.LoadMainAssetAtPath(file);
			GameObject obj=Object.Instantiate(asset) as GameObject;
			if(!obj)
				Ctrl.throwError("Cannot load asset:" + file);
			obj.SetActive(false);

			Ctrl.print("generateUI:"+file);
			
			UIControl.makeOnePrefab(obj);
			
			PrefabUtility.CreatePrefab(_uiModelGeneratePath + "/" + Path.GetFileName(file),obj);

			Object.DestroyImmediate(obj);
		}

		public static void makeUITextureImport()
		{
			SList<string> files=FileUtils.getDeepFileMutipleList(ShineToolGlobal.assetSourcePath + "/uiTexture","png","jpg","jpeg","tga");

			foreach(string path in files)
			{
				string assetUsePath=FileUtils.getAssetUsePath(path);

				TextureImporter texture = AssetImporter.GetAtPath(assetUsePath) as TextureImporter;

				if(texture!=null && texture.textureType!=TextureImporterType.Sprite)
				{
					Ctrl.print("修改",assetUsePath);

					texture.textureType = TextureImporterType.Sprite;
					// texture.spritePixelsPerUnit = 1;
					// texture.filterMode = FilterMode.Trilinear;
					// texture.mipmapEnabled = false;
					// texture.textureFormat = TextureImporterFormat.AutomaticTruecolor;
					AssetDatabase.ImportAsset(assetUsePath);
				}
			}

			Ctrl.print("OK");
		}
	}
}