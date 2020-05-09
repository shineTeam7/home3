using System;
using UnityEditor;
using UnityEngine;

namespace ShineEditor
{
	public class EditorUtils
	{
		/** 标题居中格式 */
		private static GUIStyle _labelCenterStyle;

		public static float HandlesSize=0.5f;

		public static GUIStyle labelCenterStyle
		{
			get
			{
				if(_labelCenterStyle==null)
				{
					_labelCenterStyle=new GUIStyle(GUI.skin.label);
					_labelCenterStyle.alignment=TextAnchor.MiddleCenter;
				}

				return _labelCenterStyle;
			}
		}


		private class GUIHandleColor : IDisposable
		{
			private readonly Color old;

			public GUIHandleColor(Color color)
			{
				old = Handles.color;
				Handles.color = color;
			}

			public void Dispose()
			{
				Handles.color = old;
			}
		}

		public static IDisposable handleColor(Color color)
		{
			return new GUIHandleColor(color);
		}

		public static void showLine(Vector3 p1,Vector3 p2,Color color)
		{
			using(handleColor(color))
			{
				Handles.DrawLine(p1,p2);
			}
		}

		public static void showArrows(Vector3 pos)
		{
			Quaternion quat;
			float arrowSize=HandlesSize;
			using(handleColor(Color.green))
			{
				quat=Quaternion.LookRotation(Vector3.up,Vector3.up);
				Handles.ArrowCap(0,pos,quat,arrowSize);
			}

			using(handleColor(Color.red))
			{
				quat=Quaternion.LookRotation(Vector3.right,Vector3.up);
				Handles.ArrowCap(0,pos,quat,arrowSize);
			}

			using(handleColor(Color.blue))
			{
				quat=Quaternion.LookRotation(Vector3.forward,Vector3.up);
				Handles.ArrowCap(0,pos,quat,arrowSize);
			}
		}
	}
}