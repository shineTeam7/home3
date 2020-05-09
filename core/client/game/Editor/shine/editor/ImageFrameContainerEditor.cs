using ShineEngine;
using UnityEditor;
using UnityEngine;

namespace ShineEditor
{
	[CustomEditor(typeof(ImageFrameContainer),true)]
	public class ImageFrameContainerEditor:Editor
	{
		private SerializedProperty frame;

		private ImageFrameContainer _imageFrameContainer;

		private bool showSprites;

		private Sprite newSprite;

		private void OnEnable()
		{
			frame=serializedObject.FindProperty("_curFrame");
			_imageFrameContainer=(ImageFrameContainer)target;
		}

		public override void OnInspectorGUI()
		{
			if(_imageFrameContainer)
			{
				serializedObject.Update();
				EditorGUILayout.PropertyField(frame);

				showSprites=EditorGUILayout.Foldout(showSprites,"图片集");
				if(showSprites)
				{
					Sprite[] sprites=_imageFrameContainer.sprites;
					for(int i=0;i<sprites.Length;++i)
					{
						GUILayout.BeginHorizontal();

						using(var color=new ContentColor(Color.green))
						{
							GUILayout.Label(i.ToString(),GUILayout.MaxWidth(20));
						}

						sprites[i]=EditorGUILayout.ObjectField(sprites[i],typeof(Sprite),false,GUILayout.Height(32),GUILayout.Width(32)) as Sprite;

						// ReSharper disable once UnusedVariable
						using(var color=new BgColor(Color.red))
						{
							if(GUILayout.Button("X",GUILayout.Width(22f)))
							{
								ArrayUtility.RemoveAt(ref sprites,i);
								_imageFrameContainer.sprites=sprites;
							}
						}

						GUILayout.EndHorizontal();
					}

					GUILayout.BeginHorizontal();
					newSprite=EditorGUILayout.ObjectField(newSprite,typeof(Sprite),false,GUILayout.Height(32),GUILayout.Width(32)) as Sprite;

					// ReSharper disable once UnusedVariable
					using(var color=new BgColor(Color.green))
					{
						if(GUILayout.Button("添加",GUILayout.Width(35f)))
						{

							if(sprites.Length>0)
							{
								Sprite[] arr=new Sprite[sprites.Length + 1];
								sprites.CopyTo(arr,0);
								arr[sprites.Length]=newSprite;
								_imageFrameContainer.sprites=arr;
							}
							else
							{
								_imageFrameContainer.sprites=new[] {newSprite};
							}
							newSprite=null;
						}
					}

					GUILayout.EndHorizontal();
				}

				serializedObject.ApplyModifiedProperties();

				_imageFrameContainer.frame=frame.intValue;
			}
		}
	}
}