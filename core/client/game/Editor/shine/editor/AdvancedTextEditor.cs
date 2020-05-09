using System;
using ShineEngine;
using UnityEngine;
using UnityEditor;

namespace ShineEditor
{
    [CustomEditor(typeof(AdvancedText), true)]
    public class AdvancedTextEditor : UnityEditor.UI.GraphicEditor
    {
        GUIStyle gs;
        SerializedProperty s_OriginText;
        SerializedProperty s_ImageFont;
        SerializedProperty s_UnderlineColor;
        SerializedProperty s_FontData;
        SerializedProperty s_Text;
        SerializedProperty s_LinkDefautColor;
        GUIContent g1, g2, g3, g4;
        protected override void OnEnable()
        {
            base.OnEnable();
            gs = new GUIStyle();
            gs.normal.textColor = new Color(0.8f, 0.3f, 0.7f);
            //struct 类型的返回值以值传递，需要接收后更改。
            gs.fontSize = 15;
            s_OriginText = serializedObject.FindProperty("s_OriginText");
            s_ImageFont = serializedObject.FindProperty("s_ImageFont");
            s_UnderlineColor = serializedObject.FindProperty("s_UnderlineColor");
            s_Text = serializedObject.FindProperty("m_Text");
            s_LinkDefautColor = serializedObject.FindProperty("s_LinkDefautColor");
            s_FontData = serializedObject.FindProperty("m_FontData");
            g1 = new GUIContent("内容");
            g2 = new GUIContent("ImageFont");
            g3 = new GUIContent("下划线颜色");
            g4 = new GUIContent("超链接默认颜色");
        }
        public override void OnInspectorGUI()
        {
            //这个只能应用于serializable的成员，而不是属性！！！！
            //并且这样的方式产生了掩码，调用了基类方法也没有多少。
            //adt.ImageFont=EditorGUILayout.ObjectField("ImageFont",adt.ImageFont,typeof(ImageFont),true) as ImageFont;
            //返回值要自行接收。
            //至少要使用默认的inspector.
            //textarea是多行输入。
            serializedObject.Update();
            EditorGUILayout.PropertyField(s_OriginText, g1);
            EditorGUILayout.PropertyField(s_ImageFont, g2);
            EditorGUILayout.PropertyField(s_UnderlineColor, g3);
            EditorGUILayout.PropertyField(s_LinkDefautColor, g4);
            GUILayout.Label("______________________________________________________________________", gs);

            EditorGUILayout.PropertyField(s_Text);
            EditorGUILayout.PropertyField(s_FontData);
            AppearanceControlsGUI();
            RaycastControlsGUI();

            serializedObject.ApplyModifiedProperties();
        }
    }
}
