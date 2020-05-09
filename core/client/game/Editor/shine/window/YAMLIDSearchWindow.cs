using System;
using System.Reflection;
using UnityEditor;
using UnityEngine;

namespace ShineEditor
{
    public class YAMLIDSearchWindow:BaseWindow
    {
        private readonly string message = "This grabs the Unity class by YAML ID";
        private int searchValue = 0;
        private string result = "";
        
        public static void showWindow()
        {
            GetWindowWithRect<YAMLIDSearchWindow>(new Rect(0, 0, 320, 150),true,"find class by yamlID",true).init().show();
        }
        
        YAMLIDSearchWindow init()
        {
            searchValue = 0;
            result = "";
            return this;
        }
        
        void OnGUI()
        {
            EditorGUILayout.HelpBox(message, MessageType.None);
            EditorGUILayout.BeginHorizontal();
            EditorGUILayout.LabelField("YAML ID (int):");
            searchValue = EditorGUILayout.IntField(searchValue);
            EditorGUILayout.EndHorizontal();
            if (GUI.Button(new Rect((Screen.width - 50) / 2f, 120, 50, 20), "Search"))
            {
                Search();
            }
            EditorGUILayout.Space();
            EditorGUILayout.Space();

            EditorGUILayout.BeginVertical();
            EditorGUILayout.LabelField("Result:");
            
            GUIStyle titleStyle2 = new GUIStyle();
            titleStyle2.fontSize = 16;
            titleStyle2.normal.textColor = new Color(1f/256f, 163f/256f, 1f/256f, 256f/256f);
            
            EditorGUILayout.LabelField(result,titleStyle2);
            EditorGUILayout.EndVertical();
        }

        //UnityEditor.UnityType.FindTypeByPersistentTypeID(), 
        private void Search()
        {
            Type unityType = null;
            Assembly assemb = Assembly.GetAssembly(typeof(EditorGUILayout));
            foreach (Type tip in assemb.GetTypes())
            {
                if (tip.ToString().Contains("UnityEditor.UnityType"))
                {
                    unityType = tip;
                    break;
                }
            }
            if (unityType == null)
                result = "Can't find UnityEditor.UnityType";
            else
            {
                MethodInfo mInfo = unityType.GetMethod("FindTypeByPersistentTypeID");
                if (mInfo != null)
                {
                    System.Object[] parameterValues = new System.Object[1];
                    parameterValues[0] = searchValue;
                    var returnVal = mInfo.Invoke(null, parameterValues);
                    if (returnVal != null)
                    {
                        PropertyInfo pi = unityType.GetProperty("name");
                        if (pi != null)
                        {
                            result = "YAML ID [" + searchValue + "]: " + pi.GetValue(returnVal, null);
                        }
                    }
                    else
                    {
                        result = "YAML ID [" + searchValue + "]: NULL";
                    }
                }
                else
                {
                    result = "Can't find UnityEditor.UnityType.FindTypeByPersistentTypeID(int)";
                }
            }
        }
    }
}