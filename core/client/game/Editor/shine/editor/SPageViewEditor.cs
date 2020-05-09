using ShineEngine;
using UnityEditor;

namespace ShineEditor
{
	[CustomEditor(typeof(SPageView),true)]
	public class SPageViewEditor:Editor
	{
		private SerializedProperty gridElement;

		private SerializedProperty row;

		private SerializedProperty column;

		private SerializedProperty scrollType;

		private SerializedProperty horizontalSpace;

		private SerializedProperty verticalSpace;

		private SerializedProperty pageSpace;

		private SerializedProperty loop;

		private SerializedProperty BackTime;

		private SerializedProperty ScrollLogicDistance;

		private SerializedProperty ChangePageTime;

		private SerializedProperty ChangePageDistancePer;

		private SerializedProperty AutoChangePageDistancePer;

		private SPageView _pageView;

		private bool _isShowRarelyUse;

		private void OnEnable()
		{
			_pageView=(SPageView)target;

			gridElement=serializedObject.FindProperty("gridElement");
			scrollType=serializedObject.FindProperty("_scrollType");
			row=serializedObject.FindProperty("_row");
			column=serializedObject.FindProperty("_column");
			horizontalSpace=serializedObject.FindProperty("_horizontalSpace");
			verticalSpace=serializedObject.FindProperty("_verticalSpace");
			pageSpace=serializedObject.FindProperty("_pageSpace");
			loop=serializedObject.FindProperty("_loop");

			BackTime=serializedObject.FindProperty("BackTime");
			ScrollLogicDistance=serializedObject.FindProperty("ScrollLogicDistance");
			ChangePageTime=serializedObject.FindProperty("ChangePageTime");
			ChangePageDistancePer=serializedObject.FindProperty("ChangePageDistancePer");
			AutoChangePageDistancePer=serializedObject.FindProperty("AutoChangePageDistancePer");
		}

		public override void OnInspectorGUI()
		{
			serializedObject.Update();

			EditorGUILayout.PropertyField(gridElement);
			EditorGUI.BeginChangeCheck();
			EditorGUILayout.PropertyField(scrollType);
			EditorGUILayout.PropertyField(row);
			EditorGUILayout.PropertyField(column);
			EditorGUILayout.PropertyField(horizontalSpace);
			EditorGUILayout.PropertyField(verticalSpace);
			EditorGUILayout.PropertyField(pageSpace);
			if(EditorGUI.EndChangeCheck())
			{
				serializedObject.ApplyModifiedProperties();
				_pageView.changeGridElement();
			}

			EditorGUILayout.PropertyField(loop);

			_isShowRarelyUse=EditorGUILayout.Foldout(_isShowRarelyUse,"手感属性");
			if(_isShowRarelyUse)
			{
				EditorGUI.indentLevel++;
				EditorGUILayout.PropertyField(BackTime);
				EditorGUILayout.PropertyField(ChangePageTime);
				EditorGUILayout.PropertyField(ChangePageDistancePer);
				EditorGUILayout.PropertyField(AutoChangePageDistancePer);
				EditorGUILayout.PropertyField(ScrollLogicDistance);
				EditorGUI.indentLevel--;
			}

			serializedObject.ApplyModifiedProperties();

			if(string.IsNullOrEmpty(gridElement.stringValue))
			{
				EditorGUILayout.HelpBox("请指定gridElement",MessageType.Error);
			}
		}
	}
}