using System;
using ShineEngine;
using ShineEditor;
using Spine.Unity;
using UnityEditor;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEditor
{
	/// <summary>
	/// 模型构造控制
	/// </summary>
	public class MakeModelControl
	{
		private static SMap<string, UIObjectData> _uiElementDic = new SMap<string,UIObjectData>();

		private static SMap<string,UIObjectData> _uiModelDic = new SMap<string,UIObjectData>();

		private static SMap<string,string> _elementDic;

		private static SMap<string,GameObject> _elementPrefabDic;

		public static void make()
		{
			_uiElementDic.clear();
			_uiModelDic.clear();

			initElementNames();

			foreach(var v in _elementPrefabDic)
			{
				Transform tf=v.transform;

				UIObjectData element = new UIObjectData();
				element.name=tf.gameObject.name;
				element.type=UIElementType.Element;
				element.style = "";
				
				makeNode(element,tf);

				_uiElementDic.put(element.name,element);

			}

			//生成新预处理列表
			string[] files=FileUtils.getDeepFileList(ShineToolGlobal.uiModelsPath,"prefab");

			for(int i=0;i<files.Length;i++)
			{
				string assetsPath=ToolFileUtils.getAssetsPath(files[i]);

				GameObject prefab=AssetDatabase.LoadAssetAtPath<GameObject>(assetsPath);

				Transform tf=prefab.transform;

				UIObjectData element = new UIObjectData();
				element.name=tf.gameObject.name;
				element.type = UIElementType.Model;
				element.style = "";

				makeNode(element,tf);

				_uiModelDic.put(element.name,element);
			}

			writeBytes();

			ToolFileUtils.executeServerTool("uiModel");

			Ctrl.print("OK");
		}

		private static void initElementNames()
		{
			_elementDic=new SMap<string,string>();
			_elementPrefabDic=new SMap<string,GameObject>();

			string[] files=FileUtils.getDeepFileList(ShineToolGlobal.uiElementsPath,"prefab");
			for(int i=0;i<files.Length;i++)
			{
				string assetsPath=ToolFileUtils.getAssetsPath(files[i]);

				GameObject prefab=AssetDatabase.LoadAssetAtPath<GameObject>(assetsPath);

				_elementDic.put(assetsPath,prefab.name);
				_elementPrefabDic.put(assetsPath,prefab);
			}
		}

		private static void makeNode(UIObjectData data, Transform nodeTF)
		{
			if(ShineSetting.useUIElementExport)
			{
				UIElementComponent eCom=nodeTF.GetComponent<UIElementComponent>();

				if(eCom!=null)
				{
					ElementInfo[] exports=eCom.exports;

					if(exports!=null)
					{
						for(int i=0;i<exports.Length;i++)
						{
							string name=exports[i].name;

							GameObject gameObject=exports[i].obj;

							if(gameObject==null)
							{
								Ctrl.throwError($"{data.name}-{name}-missing对象");
								return;
							}

							addOneChild(data,gameObject,name);
						}
					}
				}
			}
			else
			{
				for (int i = 0, len = nodeTF.childCount; i < len; ++i)
				{
					Transform tf = nodeTF.GetChild(i);

					string name=tf.gameObject.name;

					if (name.StartsWith("_"))
					{
						continue;
					}

					addOneChild(data,tf.gameObject,name);
				}
			}


		}

		private static void addOneChild(UIObjectData data,GameObject obj,string name)
		{
			if(name.IndexOf(' ')!=-1 ||name.IndexOf('(')!=-1 || name.IndexOf(')')!=-1)
			{
				Ctrl.throwError($"{obj.transform.root.name}-{data.name}-{name}-中有非法字符");
				return;
			}

			UIObjectData element = new UIObjectData();
			element.name = name;
			element.style = "";

			makeOneNode(element, obj.transform);

			data.children.add(element);
		}

		private static void makeContainer(UIObjectData data,Transform tf)
		{
			if(ShineSetting.useUIElementExport)
			{
				data.type=UIElementType.Object;
			}
			else
			{
				data.type = UIElementType.Container;
				//构造子组
				makeNode(data, tf);
			}
		}

		private static void makeOneNode(UIObjectData data,Transform tf)
		{
			//先prefab
			if(PrefabUtility.IsAnyPrefabInstanceRoot(tf.gameObject))
			{
				string prefabPath=PrefabUtility.GetPrefabAssetPathOfNearestInstanceRoot(tf.gameObject);

				// Ctrl.print("element",prefabPath);

				if(_elementDic.contains(prefabPath))
				{
					data.type = UIElementType.Element;
					data.style=_elementDic.get(prefabPath);

					if(data.style==null)
						data.style="";
				}
				else
				{
					makeContainer(data,tf);
				}
			}
			else
			{
				//控件
				if(tf.GetComponent<CanvasRenderer>()!=null)
				{
					if(tf.GetComponent<SContainer>()!=null)
					{
						makeContainer(data,tf);
					}
					else if(tf.GetComponent<Button>()!=null)
					{
						data.type=UIElementType.Button;
					}
					else if(tf.GetComponent<ImageFrameContainer>() != null)
					{
						data.type = UIElementType.ImageFrameContainer;
					}
					else if(tf.GetComponent<SScrollView>() != null)
					{
						data.type = UIElementType.SScrollView;
						SScrollView sScrollView = tf.GetComponent<SScrollView>();
						data.strArgs = new string[1];
						data.strArgs[0] = sScrollView.gridElement;
					}else if(tf.GetComponent<SCustomScrollView>() != null)
					{
						data.type = UIElementType.SCustomScrollView;
						SCustomScrollView sScrollView = tf.GetComponent<SCustomScrollView>();
						data.strArgs = new string[1];
						data.strArgs[0] = sScrollView.gridElement;
					}
					else if(tf.GetComponent<SPageView>()!=null)
					{
						data.type=UIElementType.SPageView;
						SPageView sPageView=tf.GetComponent<SPageView>();
						data.strArgs=new string[1];
						data.strArgs[0]=sPageView.gridElement;
					}
					else if(tf.GetComponent<RawImageLoader>()!=null)
					{
						data.type=UIElementType.RawImageLoader;
					}
					else if (tf.GetComponent<RawImage>() != null)
					{
						data.type = UIElementType.RawImage;
					}
					else if (tf.GetComponent<AdvancedText>() != null)
					{
						data.type = UIElementType.TextField;
					}
					else if(tf.GetComponent<InputField>() != null)
					{
						data.type = UIElementType.InputField;
					}
					else if(tf.GetComponent<BloodBar>() != null)
					{
						data.type = UIElementType.BloodBar;
					}
					else if(tf.GetComponent<Scrollbar>()!=null)
					{
						data.type=UIElementType.ScrollBar;
					}
					else if(tf.GetComponent<Dropdown>()!=null)
					{
						data.type=UIElementType.Dropdown;
					}
					else if(tf.GetComponent<Toggle>() != null)
					{
						data.type = UIElementType.Toggle;
					}
					else if(tf.GetComponent<Slider>() != null)
					{
						data.type = UIElementType.Slider;
					}
					else if(tf.GetComponent<ImageLoader>()!=null)
					{
						data.type=UIElementType.ImageLoader;
					}
					else if (tf.GetComponent<Image>() != null)
					{
						data.type = UIElementType.Image;
					}
					else if(tf.GetComponent<I18NText>() != null)
					{
						data.type = UIElementType.I18NText;
					}
					else if(tf.GetComponent<Text>() != null)
					{
						data.type = UIElementType.Text;
					}
					else if(tf.GetComponent<SkeletonGraphicLoader>()!=null)
					{
						data.type=UIElementType.SkeletonGraphicLoader;
					}
					else if(tf.GetComponent<SkeletonGraphic>() != null)
					{
						data.type = UIElementType.SkeletonGraphic;
					}
					else if(tf.GetComponent<GuideMask>()!=null)
					{
						data.type=UIElementType.GuideMask;
					}
					else //其他脚本
					{
						makeContainer(data,tf);
					}
				}
				else
				{
					if(tf.GetComponent<Toggle>() != null)
					{
						data.type = UIElementType.Toggle;
					}
					else if(tf.GetComponent<Slider>() != null)
					{
						data.type = UIElementType.Slider;
					}
					else //什么都没有
					{
						makeContainer(data,tf);
					}
				}
			}
		}

		private static void writeBytes()
		{
			BytesWriteStream stream = new BytesWriteStream();

			stream.writeLen(_uiElementDic.size());

			foreach(string name in _uiElementDic.getSortedMapKeys())
			{
				_uiElementDic.get(name).writeBytesSimple(stream);
			}

			stream.writeLen(_uiModelDic.size());

			foreach(string name in _uiModelDic.getSortedMapKeys())
			{
				_uiModelDic.get(name).writeBytesSimple(stream);
			}

			FileUtils.writeFileForBytes(ShineToolGlobal.clientTempPath+"/uiInfo.bin",stream);
		}

	}
}