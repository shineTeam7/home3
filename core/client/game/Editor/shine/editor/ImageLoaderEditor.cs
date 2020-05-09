using ShineEngine;
using UnityEditor;
using UnityEngine;
namespace ShineEditor
{
	[CustomEditor(typeof(ImageLoader), true)]
	public class ImageLoaderEditor:Editor
	{
		private ImageLoader _imageLoader;

        private bool isFirst = true;
		private void OnEnable()
		{
			_imageLoader=(ImageLoader)target;
        }

		public override void OnInspectorGUI()
		{
			base.OnInspectorGUI();
			EditorGUILayout.LabelField("source:" + _imageLoader.source);

            EditorGUILayout.BeginHorizontal();
            EditorGUILayout.LabelField("是否自适应sprite大小:");
            _imageLoader.isAutoNativeSpriteSize = EditorGUILayout.Toggle(_imageLoader.isAutoNativeSpriteSize);
            EditorGUILayout.EndHorizontal();
            if (_imageLoader.isAutoNativeSpriteSize)
            {
                if (isFirst)
                {
                    if (_imageLoader.spriteWidth <= 0 || _imageLoader.spriteHeigh <= 0)
                    {
                        RectTransform rectTransform = _imageLoader.transform.GetComponent<RectTransform>();
                        _imageLoader.spriteWidth = rectTransform.sizeDelta.x;
                        _imageLoader.spriteHeigh = rectTransform.sizeDelta.y;
                    }
                    isFirst = false;
                }

                EditorGUILayout.BeginHorizontal();
                EditorGUILayout.LabelField("spriteWidth:");
                _imageLoader.spriteWidth = EditorGUILayout.FloatField(_imageLoader.spriteWidth);
                EditorGUILayout.EndHorizontal();

                EditorGUILayout.BeginHorizontal();
                EditorGUILayout.LabelField("spriteHeigh:");
                _imageLoader.spriteHeigh = EditorGUILayout.FloatField(_imageLoader.spriteHeigh);
                EditorGUILayout.EndHorizontal();
            }
		}
	}
}