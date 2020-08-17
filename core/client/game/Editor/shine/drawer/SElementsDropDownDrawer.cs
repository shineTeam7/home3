using ShineEngine;
using UnityEditor;
using UnityEngine;

namespace ShineEditor
{
	[CustomPropertyDrawer(typeof(SElementAttribute))]
	public class SElementsDropDownDrawerBase:SDropDownDrawerBase<SElementAttribute>
	{
		protected override void populateMenu(GenericMenu menu,SerializedProperty property,SElementAttribute attribute)
		{
			string[] files=FileUtils.getDeepFileList(ToolFileUtils.getAssetsPath(ShineToolGlobal.uiElementsPath),"prefab");
			for (int i = 0; i < files.Length; i++)
			{
				GameObject prefab = AssetDatabase.LoadAssetAtPath<GameObject>(files[i]);
				
				string name=prefab.name;
				menu.AddItem(new GUIContent(name),name==property.stringValue,onSelectItem,name);
			}
		}

		protected override string getNullString()
		{
			return "未指定";
		}

		protected override void onSelectItem(object menuItemObject)
		{
			base.onSelectItem(menuItemObject);

			var targetObj=targetProperty.serializedObject.targetObject;
			if(targetObj is SScrollView)
			{
				((SScrollView)targetObj).changeGridElement();
			}
			else if(targetObj is SPageView)
			{
				((SPageView)targetObj).changeGridElement();
			}
		}
	}
}