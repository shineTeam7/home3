using System;
using System.IO;
using ShineEngine;
using UnityEngine;
using UnityEditor;

namespace ShineEditor
{
	/// <summary>
	///
	/// </summary>
	public class BMFontWindow:BaseWindow
	{
		private TextAsset _fnt;

		public static void showWindow()
		{
			GetWindowWithRect<BMFontWindow>(new Rect(0,0,250,100),true,"美术字生成",true).show();
		}

		protected override void OnGUI()
		{
			GUILayout.Space(10);
			GUILayout.BeginHorizontal();
			GUILayout.Label("fnt文件：",GUILayout.MinWidth(60));
			_fnt=(TextAsset)EditorGUILayout.ObjectField(_fnt,typeof(TextAsset),false);
			GUILayout.EndHorizontal();
			GUILayout.Space(20);

			GUILayout.BeginHorizontal();
			GUILayout.Space(45);
			GUIStyle tmpBtStyle=new GUIStyle(GUI.skin.button);
			tmpBtStyle.fontSize=15;
			using(var bg=new BgColor(Color.green))
			{
				if(GUILayout.Button("生成字体信息",tmpBtStyle,GUILayout.Width(160),GUILayout.Height(30)))
				{
					doMakeBMFont();
				}
			}
			GUILayout.EndHorizontal();
		}

		private void doMakeBMFont()
		{
			if(!_fnt)
			{
				EditorUtility.DisplayDialog("错误提示","请选择fnt文件","确定");
				return;
			}

			EditorUtility.DisplayProgressBar("正在生成字体文件...", "请稍等，正在生成bundle文件...", 0.0f);

			string fntPath=AssetDatabase.GetAssetPath(_fnt);
			string rootPath=Path.GetDirectoryName(fntPath) + "/";
			string fontName=Path.GetFileNameWithoutExtension(fntPath);
			string pathName=rootPath + fontName;

			Texture2D tex=(Texture2D)AssetDatabase.LoadAssetAtPath(pathName + ".png",typeof(Texture2D));

			string matPath=pathName + ".mat";
			Material mat=(Material)AssetDatabase.LoadAssetAtPath(matPath,typeof(Material));
			if(mat==null)
			{
				//创建材质球
				mat=new Material(Shader.Find("GUI/Text Shader"));
				mat.SetTexture("_MainTex",tex);
				AssetDatabase.CreateAsset(mat,matPath);
			}
			else
			{
				mat.shader=Shader.Find("GUI/Text Shader");
				mat.SetTexture("_MainTex",tex);
			}

			Font font=(Font)AssetDatabase.LoadAssetAtPath(pathName + ".fontsettings",typeof(Font));
			if(font==null)
			{
				font=new Font(fontName);
				AssetDatabase.CreateAsset(font,pathName + ".fontsettings");
			}
			font.material = mat;

			BMFont mbFont=new BMFont();
			BMFontReader.Load(mbFont,_fnt.name,_fnt.bytes);
			CharacterInfo[] characterInfo=new CharacterInfo[mbFont.glyphs.Count];
			for(int i=0;i<mbFont.glyphs.Count;i++)
			{
				BMGlyph bmInfo=mbFont.glyphs[i];
				CharacterInfo info=new CharacterInfo();
				info.index=bmInfo.index;

				float uvx=(float)bmInfo.x / (float)mbFont.texWidth;
				float uvy=1 - (float)bmInfo.y / (float)mbFont.texHeight;
				float uvw=(float)bmInfo.width / (float)mbFont.texWidth;
				float uvh=-1f * (float)bmInfo.height / (float)mbFont.texHeight;
				info.uvBottomLeft=new Vector2(uvx,uvy);
				info.uvBottomRight=new Vector2(uvx + uvw,uvy);
				info.uvTopLeft=new Vector2(uvx,uvy + uvh);
				info.uvTopRight=new Vector2(uvx + uvw,uvy + uvh);

				info.minX=bmInfo.offsetX;
				info.minY=-bmInfo.offsetY;
				info.maxX=bmInfo.offsetX + bmInfo.width;
				info.maxY=-bmInfo.offsetY - bmInfo.height;
				info.advance=bmInfo.advance;
				characterInfo[i]=info;
			}
			font.characterInfo=characterInfo;
			EditorUtility.SetDirty(font);
			EditorUtility.ClearProgressBar();

			AssetDatabase.SaveAssets();
			AssetDatabase.Refresh();

			EditorUtility.DisplayDialog("温馨提示","字体文件生成成功！","确定");
		}
	}
}