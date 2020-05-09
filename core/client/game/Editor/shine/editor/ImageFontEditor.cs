using System;
using ShineEngine;
using UnityEngine;
using UnityEditor;

namespace ShineEditor
{
    [CustomEditor(typeof(ImageFont), true)]
    public class ImageFontEditor : Editor
    {
        bool showSyms = true;
        string newName = "";
        Sprite newSprite;

        public override void OnInspectorGUI()
        {
            ImageFont txt = target as ImageFont;
            if (txt)
            {
                showSyms = EditorGUILayout.Foldout(showSyms, "符号集");
                if (showSyms)
                {
                    for (int i = 0; i < txt.SymbolData.Length; i++)
                    {
                        SymbolData data = txt.SymbolData[i];
                        GUILayout.BeginHorizontal();
                        data.Value = GUILayout.TextField(data.Value, GUILayout.Width(150));
                        GUILayout.FlexibleSpace();
                        GUILayout.BeginVertical();
                        data.Sprite = EditorGUILayout.ObjectField(data.Sprite, typeof(Sprite), false, GUILayout.Height(32), GUILayout.Width(32)) as Sprite;
                        //if(data.Sprite)
                        //    GUILayout.Label(data.Sprite.name,GUILayout.MaxWidth(150));
                        GUILayout.EndVertical();
                        GUI.backgroundColor = Color.red;

                        if (GUILayout.Button("X", GUILayout.Width(22f)))
                        {
                            SymbolData[] arr = txt.SymbolData;
                            ArrayUtility.RemoveAt(ref arr, i);
                            txt.SymbolData = arr;
                        }
                        GUI.backgroundColor = Color.white;
                        GUILayout.EndHorizontal();
                    }

                    GUILayout.BeginHorizontal();
                    newName = GUILayout.TextField(newName, GUILayout.Width(150));
                    bool hasName = !string.IsNullOrEmpty(newName);
                    GUILayout.FlexibleSpace();
                    GUILayout.BeginVertical();
                    newSprite = EditorGUILayout.ObjectField(newSprite, typeof(Sprite), false, GUILayout.Height(32), GUILayout.Width(32)) as Sprite;
                    //bool hasSprite = newSprite;
                    //if (hasSprite)
                    //    GUILayout.Label(newSprite.name, GUILayout.MaxWidth(150));
                    GUILayout.EndVertical();

                    bool isValid = hasName;
                    GUI.backgroundColor = Color.green;

                    if (GUILayout.Button("添加", GUILayout.Width(35f)) && isValid)
                    {
                        SymbolData d = new SymbolData();
                        d.Value = newName;
                        d.Sprite = newSprite;
                        if (txt.SymbolData.Length > 0)
                        {
                            SymbolData[] arr = new SymbolData[txt.SymbolData.Length + 1];
                            txt.SymbolData.CopyTo(arr, 0);
                            arr[txt.SymbolData.Length] = d;
                            txt.SymbolData = arr;
                        }
                        else
                        {
                            txt.SymbolData = new SymbolData[1] { d };
                        }
                        newName = "";
                        newSprite = null;
                    }
                    GUI.backgroundColor = Color.white;
                    GUILayout.EndHorizontal();

                    if (txt.SymbolData.Length == 0)
                    {
                        EditorGUILayout.HelpBox("想要添加表情符号吗？ 在第一个文本框中输入表情字符，例如‘:)’，然后第二个图片栏中选上对应的图片，再点击添加即可", MessageType.Info);
                    }
                    else
                        GUILayout.Space(4f);
                }
            }
            serializedObject.Update();
            serializedObject.ApplyModifiedProperties();
        }
    }
}
