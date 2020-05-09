using System;
using ShineEngine;
using UnityEditor;
using UnityEditor.UI;

namespace ShineEditor
{
	[CustomEditor(typeof(I18NText), true)]
	public class I18NTextEditor:GraphicEditor
	{
		private SerializedProperty fontId;
		private SerializedProperty m_Text;
		private SerializedProperty m_FontData;
		private I18NText _i18NText;

		private static SList<FontConfig> _fontConfigList=new SList<FontConfig>();
		private static string[] options={};
		private int _lastIndex = 0;
		
		protected override void OnEnable()
		{
			base.OnEnable();

			_i18NText=(I18NText)target;

			GameC.app.initConfigForEditor();

			FontConfig[] configs = FontConfig.getDic();
			
			if(_fontConfigList.length() == 0)
			{
				foreach (var fontConfig in FontConfig.getDic())
				{
					if (fontConfig != null)
					{
						_fontConfigList.add(fontConfig);
					}
				}
				
				options = new string[_fontConfigList.length()];

				for (int i=0;i<_fontConfigList.length();++i)
				{
					options[i] = _fontConfigList[i].name;
				}
			}
			
			fontId=serializedObject.FindProperty("_fontId");
			m_Text = serializedObject.FindProperty("m_Text");
			m_FontData = serializedObject.FindProperty("m_FontData");
			
			//看看默认选第几个
			for (int i=0;i<_fontConfigList.length();++i)
			{
				if (_fontConfigList[i].id == fontId.intValue)
				{
					_lastIndex = i;
					break;
				}
			}
		}

		/// <summary>
		///   <para>See Editor.OnInspectorGUI.</para>
		/// </summary>
		public override void OnInspectorGUI()
		{
			bool isChange=false;

			serializedObject.Update();
			EditorGUI.BeginChangeCheck();
//			EditorGUILayout.PropertyField(fontId);
			
			int index=EditorGUILayout.Popup(_lastIndex, options);
			
			if(_lastIndex!=index)
			{
				isChange=true;
				fontId.intValue = _fontConfigList[index].id;
			}

			_lastIndex = index;
			
			EditorGUILayout.PropertyField(m_Text);
			EditorGUILayout.PropertyField(m_FontData);
			AppearanceControlsGUI();
			RaycastControlsGUI();
			serializedObject.ApplyModifiedProperties();

			if(isChange)
				_i18NText.refreshLanguage();
		}
	}
}