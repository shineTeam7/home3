using ShineEngine;
using UnityEditor;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEditor
{
    [CustomEditor(typeof(SScrollViewFake3D), true)]
    public class SScrollViewFake3DEditor : Editor
    {
        private static string[] MASK_TYPE_LIST = { "Mask", "RectMask2D" };

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

        private SerializedProperty AlignGrid;

        private SerializedProperty AlignTime;

        private SerializedProperty NeedChangeAlpha;

        private SerializedProperty ChangeAlphaLen;

        private SerializedProperty maskType;

        private SerializedProperty minScale;

        private SerializedProperty minBrightness;

        protected SScrollView _scrollView;

        private bool _isShowRarelyUse;

        private RectTransform _rectTransform;

        private void OnEnable()
        {
            _scrollView = (SScrollView)target;
            _rectTransform = _scrollView.gameObject.GetComponent<RectTransform>();

            InitProperty();
        }

        private void OnDestroy()
        {
        }

        protected void InitProperty()
        {
            gridElement = serializedObject.FindProperty("gridElement");
            scrollType = serializedObject.FindProperty("_scrollType");
            rowOrColumn = serializedObject.FindProperty("_rowOrColumn");
            horizontalSpace = serializedObject.FindProperty("_horizontalSpace");
            verticalSpace = serializedObject.FindProperty("_verticalSpace");
            loop = serializedObject.FindProperty("_loop");
            maxOutDistancePercent = serializedObject.FindProperty("_maxOutDistancePercent");
            maskType = serializedObject.FindProperty("maskType");

            SpeedRatio = serializedObject.FindProperty("SpeedRatio");
            SpeedBegin = serializedObject.FindProperty("SpeedBegin");
            SpeedEnd = serializedObject.FindProperty("SpeedEnd");
            SpeedMax = serializedObject.FindProperty("SpeedMax");
            SpeedDis = serializedObject.FindProperty("SpeedDis");
            OutSpeedDis = serializedObject.FindProperty("OutSpeedDis");
            BackTime = serializedObject.FindProperty("BackTime");
            AlignGrid = serializedObject.FindProperty("AlignGrid");
            AlignTime = serializedObject.FindProperty("AlignTime");
            NeedChangeAlpha = serializedObject.FindProperty("NeedChangeAlpha");
            ChangeAlphaLen = serializedObject.FindProperty("ChangeAlphaLen");
            ScrollLogicDistance = serializedObject.FindProperty("ScrollLogicDistance");
            minScale = serializedObject.FindProperty("minScale");
            minBrightness = serializedObject.FindProperty("minBrightness");
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
            if (EditorGUI.EndChangeCheck())
            {
                serializedObject.ApplyModifiedProperties();
                _scrollView.changeGridElement();
            }

            EditorGUILayout.PropertyField(loop);
            if (!loop.boolValue)
                EditorGUILayout.PropertyField(maxOutDistancePercent);

            EditorGUILayout.PropertyField(AlignGrid);
            if (AlignGrid.boolValue)
                EditorGUILayout.PropertyField(AlignTime);

            GUILayout.BeginHorizontal();
            GUILayout.Label("裁剪类型:", GUILayout.Width(60));
            EditorGUI.BeginChangeCheck();
            maskType.intValue = EditorGUILayout.Popup(maskType.intValue, MASK_TYPE_LIST, GUILayout.Width(100), GUILayout.Height(20));
            if (EditorGUI.EndChangeCheck())
            {
                EditorControl.callLater(changeMaskType);
            }
            GUILayout.EndHorizontal();

            _isShowRarelyUse = EditorGUILayout.Foldout(_isShowRarelyUse, "手感属性");
            if (_isShowRarelyUse)
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

            EditorGUILayout.PropertyField(NeedChangeAlpha);

            if (NeedChangeAlpha.boolValue)
                EditorGUILayout.PropertyField(ChangeAlphaLen);

            EditorGUILayout.PropertyField(minScale);

            EditorGUILayout.PropertyField(minBrightness);

            serializedObject.ApplyModifiedProperties();

            if (string.IsNullOrEmpty(gridElement.stringValue))
            {
                EditorGUILayout.HelpBox("请指定gridElement", MessageType.Error);
            }
        }

        /**
		 * 更改裁剪类型
		 */
        private void changeMaskType()
        {
            clearMask();

            GameObject targetGameObject = _scrollView.gameObject;
            switch (maskType.intValue)
            {
                case 0:  //Mask控件实现裁剪
                    targetGameObject.AddComponent<Image>();
                    targetGameObject.AddComponent<Mask>().showMaskGraphic = false;
                    break;
                case 1:  //RectMask2D控件实现裁剪
                    targetGameObject.AddComponent<RectMask2D>();
                    break;
            }

        }

        /**
		 * 清理所有裁剪
		 */
        private void clearMask()
        {
            GameObject targetGameObject = _scrollView.gameObject;

            Image image = targetGameObject.GetComponent<Image>();
            if (image)
                DestroyImmediate(image);

            Mask mask = targetGameObject.GetComponent<Mask>();
            if (mask)
                DestroyImmediate(mask);

            RectMask2D rectMask2D = targetGameObject.GetComponent<RectMask2D>();
            if (rectMask2D)
                DestroyImmediate(rectMask2D);
        }
    }
}