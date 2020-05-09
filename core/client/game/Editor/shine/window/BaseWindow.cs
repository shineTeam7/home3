using UnityEditor;
using UnityEngine;

namespace ShineEditor
{
	public class BaseWindow:EditorWindow
	{
		private int _editorVersion=-1;

		// protected virtual Rect getShowRect()
		// {
		// 	return new Rect(0,0,400,300);
		// }

		public void show()
		{
			_editorVersion=EditorControl.getVersion();

			this.Show();
		}

		protected virtual void OnGUI()
		{

		}

		private void Update()
		{
			onUpdate();
			
			// if(_editorVersion!=EditorControl.getVersion())
			// {
			// 	//关闭
			// 	Close();
			// }
			// else
			// {
			// 	onUpdate();
			// }
		}

		protected virtual void onUpdate()
		{

		}
	}
}