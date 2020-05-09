using ShineEngine;
using UnityEditor;
using UnityEngine;

namespace ShineEditor
{
	[CustomEditor(typeof(SCustomScrollView),true)]
	public class SCustomScrollViewEditor:Editor
	{
		private SerializedProperty gridElement;

		private SerializedProperty rowOrColumn;

		private SerializedProperty scrollType;

		private SerializedProperty horizontalSpace;

		private SerializedProperty verticalSpace;

		private SerializedProperty loop;

		private SerializedProperty maxOutDistancePercent;

		private SerializedProperty SpeedRatio;

		private SerializedProperty SpeedBegin;

		private SerializedProperty SpeedEnd;

		private SerializedProperty SpeedMax;

		private SerializedProperty SpeedDis;

		private SerializedProperty OutSpeedDis;

		private SerializedProperty BackTime;

		private SerializedProperty ScrollLogicDistance;

		private SCustomScrollView _scrollView;

		private bool _isShowRarelyUse;

		private void OnEnable()
		{
			_scrollView=(SCustomScrollView)target;

			gridElement=serializedObject.FindProperty("gridElement");
			scrollType=serializedObject.FindProperty("_scrollType");
			rowOrColumn=serializedObject.FindProperty("_rowOrColumn");
			horizontalSpace=serializedObject.FindProperty("_horizontalSpace");
			verticalSpace=serializedObject.FindProperty("_verticalSpace");
			loop=serializedObject.FindProperty("_loop");
			maxOutDistancePercent=serializedObject.FindProperty("_maxOutDistancePercent");

			SpeedRatio=serializedObject.FindProperty("SpeedRatio");
			SpeedBegin=serializedObject.FindProperty("SpeedBegin");
			SpeedEnd=serializedObject.FindProperty("SpeedEnd");
			SpeedMax=serializedObject.FindProperty("SpeedMax");
			SpeedDis=serializedObject.FindProperty("SpeedDis");
			OutSpeedDis=serializedObject.FindProperty("OutSpeedDis");
			BackTime=serializedObject.FindProperty("BackTime");
			ScrollLogicDistance=serializedObject.FindProperty("ScrollLogicDistance");
		}

		public override void OnInspectorGUI()
		{
			serializedObject.Update();

			EditorGUILayout.PropertyField(gridElement);
			EditorGUI.BeginChangeCheck();
			EditorGUILayout.PropertyField(scrollType);
			EditorGUILayout.PropertyField(rowOrColumn);
			EditorGUILayout.PropertyField(horizontalSpace);
			EditorGUILayout.PropertyField(verticalSpace);
			if(EditorGUI.EndChangeCheck())
			{
				serializedObject.ApplyModifiedProperties();
				_scrollView.changeGridElement();
			}

			EditorGUILayout.PropertyField(loop);
			if(!loop.boolValue)
				EditorGUILayout.PropertyField(maxOutDistancePercent);

			_isShowRarelyUse=EditorGUILayout.Foldout(_isShowRarelyUse,"手感属性");
			if(_isShowRarelyUse)
			{
				EditorGUI.indentLevel++;
				EditorGUILayout.PropertyField(SpeedRatio);
				EditorGUILayout.PropertyField(SpeedBegin);
				EditorGUILayout.PropertyField(SpeedEnd);
				EditorGUILayout.PropertyField(SpeedMax);
				EditorGUILayout.PropertyField(SpeedDis);
				EditorGUILayout.PropertyField(OutSpeedDis);
				EditorGUILayout.PropertyField(BackTime);
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