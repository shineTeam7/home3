using ShineEngine;
using Spine.Unity.Editor;
using UnityEditor;
using UnityEngine;

namespace ShineEditor
{
	public class SDropDownDrawerBase<T>:PropertyDrawer where T:SAttributeBase
	{
		protected T targetAttribute
		{
			get {return (T)attribute;}
		}

		protected SerializedProperty targetProperty
		{
			get;
			private set;
		}

		public override void OnGUI(Rect position,SerializedProperty property,GUIContent label)
		{
			targetProperty=property;

			if(property.propertyType!=SerializedPropertyType.String)
			{
				EditorGUI.LabelField(position,"ERROR:","May only apply to type string");
				return;
			}

			position=EditorGUI.PrefixLabel(position,label);

			if(GUI.Button(position,new GUIContent(string.IsNullOrEmpty(property.stringValue) ? getNullString() : property.stringValue),EditorStyles.popup))
				onSelectItem(property);
		}

		protected virtual void onSelectItem(SerializedProperty property)
		{
			var menu=new GenericMenu();
			populateMenu(menu,property,targetAttribute);
			menu.ShowAsContext();
		}

		protected virtual void populateMenu(GenericMenu menu,SerializedProperty property,T attribute)
		{
			SerializedProperty dataField = property.FindBaseOrSiblingProperty(attribute.dataField);

			if(dataField!=null && dataField.isArray)
			{
				int len=dataField.arraySize;
				for(int i=0;i<len;i++)
				{
					string name=dataField.GetArrayElementAtIndex(i).stringValue;
					menu.AddItem(new GUIContent(name),name==property.stringValue,onSelectItem,name);
				}
			}
		}

		protected virtual void onSelectItem(object menuItemObject)
		{
			string name=(string)menuItemObject;
			targetProperty.stringValue=name;
			targetProperty.serializedObject.ApplyModifiedProperties();
		}

		protected virtual string getNullString()
		{
			return "";
		}
	}
}