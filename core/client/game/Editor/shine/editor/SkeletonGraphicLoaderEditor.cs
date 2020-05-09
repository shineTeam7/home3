using ShineEngine;
using UnityEditor;

namespace ShineEditor
{
	[CustomEditor(typeof(SkeletonGraphicLoader), true)]
	public class SkeletonGraphicLoaderEditor:Editor
	{
		private SkeletonGraphicLoader _skeletonGraphicLoader;

		private void OnEnable()
		{
			_skeletonGraphicLoader=(SkeletonGraphicLoader)target;
		}

		public override void OnInspectorGUI()
		{
			base.OnInspectorGUI();
			EditorGUILayout.LabelField("source:" + _skeletonGraphicLoader.source);
		}
	}
}