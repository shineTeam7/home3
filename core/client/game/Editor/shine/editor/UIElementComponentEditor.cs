using ShineEngine;
using UnityEditor;
using UnityEditorInternal;
using UnityEngine;

namespace ShineEditor
{
	/** UI控件的自定义 */
	[CustomEditor(typeof(UIElementComponent))]
	public class UIElementComponentEditor:Editor
	{
		private ReorderableList list;

		private SSet<string> _checkNameRepeatSet=new SSet<string>();
		private SSet<UnityEngine.Object> _checkObjRepeatSet=new SSet<Object>();

		public override void OnInspectorGUI()
		{
			_checkNameRepeatSet.clear();
			_checkObjRepeatSet.clear();

			serializedObject.Update();

			// EditorGUILayout.BeginHorizontal();
			list.DoLayoutList();

			serializedObject.ApplyModifiedProperties();
		}

		private void OnEnable()
		{
			if(this.target==null)
				return;

			SerializedObject sObj=this.serializedObject;

			SerializedProperty exports=sObj.FindProperty("exports");

			list=new ReorderableList(sObj,exports);
			list.drawHeaderCallback+=rect=>
			{
				GUI.Label(rect,"Elements");
			};

			list.elementHeight=EditorGUIUtility.singleLineHeight;
			list.drawElementCallback=(rect,index,active,focused)=>{ drawOne(exports,rect,index); };
		}

		private void OnDestroy()
		{
			_checkNameRepeatSet.clear();
			_checkNameRepeatSet.clear();
		}

		private void drawOne(SerializedProperty property,Rect rect,int index)
		{
			SerializedProperty one=property.GetArrayElementAtIndex(index);

			SerializedProperty nameP=one.FindPropertyRelative("name");
			SerializedProperty objP=one.FindPropertyRelative("obj");

			if(objP.objectReferenceValue!=null)
			{
				if(_checkObjRepeatSet.contains(objP.objectReferenceValue))
				{
					objP.objectReferenceValue=null;
				}
				else
				{
					_checkObjRepeatSet.add(objP.objectReferenceValue);
				}
			}


			float halfWidth=rect.width / 2;
			Rect leftRect=new Rect(rect.x,rect.y,halfWidth,rect.height);

			string text=nameP.stringValue;

			EditorGUI.BeginChangeCheck();

			if (objP.objectReferenceValue!=null)
			{
				if(text.Equals(""))
					text = objP.objectReferenceValue.name;

				if(_checkNameRepeatSet.contains(text))
					text="";
				else
					_checkNameRepeatSet.add(text);
			}
			else
			{
				text="";
			}

			nameP.stringValue=text;

			text=EditorGUI.TextField(leftRect,text);

			if(EditorGUI.EndChangeCheck())
			{
				nameP.stringValue=text;
			}

			Rect rightRect=new Rect(rect.x+halfWidth,rect.y,halfWidth,rect.height);

			EditorGUI.PropertyField(rightRect,objP,GUIContent.none);
		}
	}
}