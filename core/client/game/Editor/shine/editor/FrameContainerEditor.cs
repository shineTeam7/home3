using ShineEngine;
using UnityEditor;

namespace ShineEditor
{
	[CustomEditor(typeof(FrameContainer), true)]
	public class FrameContainerEditor:Editor
	{
		public override void OnInspectorGUI()
		{
			base.OnInspectorGUI();

			Ctrl.print("inspector");
		}
	}
}