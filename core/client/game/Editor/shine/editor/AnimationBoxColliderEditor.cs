using System;
using System.Text;
using ShineEngine;
using UnityEngine;
using UnityEditor;
using UnityEditorInternal;
using UnityEditor.IMGUI.Controls;
using Spine;
using Spine.Unity;

namespace ShineEditor
{
    [CustomEditor(typeof(AnimationBoxCollider), true)]
    public class AnimationBoxColliderEditor : Editor
    {
        private readonly BoxBoundsHandle m_BoundsHandle = new BoxBoundsHandle();

        private SerializedProperty animationName;

        private SerializedProperty center;

        private SerializedProperty size;

        private bool _wasAnimationNameChanged;

        /** 当前动画 */
        private SkeletonAnimation _skeletonAnimation;

        /** 动画数据 */
        private SkeletonData _skeletonData;

        /** 动作总帧数 */
        private SMap<string, int> _frames = new SMap<string, int>();

        /** 当前帧数 */
        private int _curFrameIndex = 0;

        private bool isInspectingPrefab;

        private AnimationBoxCollider _target;

        private void OnEnable()
        {
            isInspectingPrefab = (PrefabUtility.GetPrefabType(base.target) == PrefabType.Prefab);

            animationName = serializedObject.FindProperty("_animationName");
            center = serializedObject.FindProperty("offset");
            size = serializedObject.FindProperty("size");

            _target = (AnimationBoxCollider)base.target;
            _skeletonAnimation = _target.gameObject.GetComponent<SkeletonAnimation>();

            if (_skeletonAnimation != null)
            {
                _skeletonData = _skeletonAnimation.SkeletonDataAsset.GetSkeletonData(true);

                var animations = _skeletonData.Animations;

                for (int i = 0; i < animations.Count; i++)
                {
                    string name = animations.Items[i].Name;
                    float duration = animations.Items[i].Duration;
                    _frames.put(name, (int)Math.Round(duration * 30f) + 1);
                }
            }

        }

        public override void OnInspectorGUI()
        {
            refreshAnimation();

            serializedObject.Update();
            if (_skeletonAnimation != null)
            {
                EditMode.DoEditModeInspectorModeButton(EditMode.SceneViewEditMode.Collider, "Edit Attack Collider", this.editModeButton, new Bounds(_target.offset, _target.size), this);
                EditorGUI.BeginChangeCheck();
                EditorGUILayout.PropertyField(animationName);
                _wasAnimationNameChanged |= EditorGUI.EndChangeCheck(); // Value used in the next update.
            }
            EditorGUILayout.PropertyField(center);
            EditorGUILayout.PropertyField(size);
            serializedObject.ApplyModifiedProperties();

        }

        protected virtual GUIContent editModeButton
        {
            get
            {
                return EditorGUIUtility.IconContent("EditCollider");
            }
        }

        public bool editingCollider
        {
            get
            {
                return ((EditMode.editMode == EditMode.SceneViewEditMode.Collider) && EditMode.IsOwner(this));
            }
        }

        private void OnSceneGUI()
        {
            if (editingCollider)
            {
                //开始绘制UI
                Handles.BeginGUI();
                GUILayout.BeginArea(new Rect(20, 20, 300, 300));
                using (var bg = new BgColor(Color.green))
                {
                    GUIStyle tmpStyle = new GUIStyle(GUI.skin.box);
                    tmpStyle.fontSize = 15;
                    tmpStyle.normal.textColor = Color.white;
                    GUILayout.Box("当前动画：" + animationName.stringValue, tmpStyle, GUILayout.MinWidth(300));
                }

                GUILayout.Label("总帧数：" + _frames.get(animationName.stringValue));
                GUILayout.BeginHorizontal();
                GUIStyle btStyle1 = new GUIStyle(GUI.skin.button);
                btStyle1.fontSize = 15;
                if (GUILayout.Button("-", btStyle1, GUILayout.MinHeight(26)))
                {
                    prevFrame();
                }
                using (var bg = new BgColor(Color.yellow))
                {
                    GUIStyle tmpStyle = new GUIStyle(GUI.skin.box);
                    tmpStyle.fontSize = 15;
                    tmpStyle.normal.textColor = Color.white;
                    GUILayout.Box(_curFrameIndex.ToString(), tmpStyle, GUILayout.MinWidth(200));
                }
                if (GUILayout.Button("+", btStyle1, GUILayout.MinHeight(26)))
                {
                    nextFrame();
                }
                GUILayout.EndHorizontal();

                GUILayout.Space(10);


                GUILayout.BeginHorizontal();
                using (var bg = new BgColor(Color.yellow))
                {
                    using (var cont = new ContentColor(Color.green))
                    {
                        if (GUILayout.Button("复制矩形"))
                        {
                            StringBuilder sb = new StringBuilder();
                            sb.Append(_target.offset.x);
                            sb.Append(",");
                            sb.Append(_target.offset.y);
                            sb.Append(",");
                            sb.Append(_target.size.x);
                            sb.Append(",");
                            sb.Append(_target.size.y);

                            TextEditor te = new TextEditor();
                            te.text = sb.ToString();
                            te.OnFocus();
                            te.Copy();
                        }
                    }
                }
                using (var bg = new BgColor(Color.yellow))
                {
                    if (GUILayout.Button("复制动作矩形"))
                    {
                        StringBuilder sb = new StringBuilder(animationName.stringValue);
                        sb.Append(":");
                        sb.Append(_target.offset.x);
                        sb.Append(",");
                        sb.Append(_target.offset.y);
                        sb.Append(",");
                        sb.Append(_target.size.x);
                        sb.Append(",");
                        sb.Append(_target.size.y);

                        TextEditor te = new TextEditor();
                        te.text = sb.ToString();
                        te.OnFocus();
                        te.Copy();
                    }
                }
                GUILayout.EndHorizontal();

                GUILayout.EndArea();
                Handles.EndGUI();

                //开始编辑矩形
                Matrix4x4 localToWorldMatrix = _target.transform.localToWorldMatrix;
                localToWorldMatrix.SetRow(0, Vector4.Scale(localToWorldMatrix.GetRow(0), new Vector4(1f, 1f, 0f, 1f)));
                localToWorldMatrix.SetRow(1, Vector4.Scale(localToWorldMatrix.GetRow(1), new Vector4(1f, 1f, 0f, 1f)));
                localToWorldMatrix.SetRow(2, new Vector4(0f, 0f, 1f, _target.transform.position.z));
                using (new Handles.DrawingScope(localToWorldMatrix))
                {
                    this.m_BoundsHandle.center = _target.offset;
                    this.m_BoundsHandle.size = _target.size;
                    this.m_BoundsHandle.SetColor(Color.red);
                    EditorGUI.BeginChangeCheck();
                    this.m_BoundsHandle.DrawHandle();
                    if (EditorGUI.EndChangeCheck())
                    {
                        Undo.RecordObject(_target, string.Format("Modify {0}", ObjectNames.NicifyVariableName(base.target.GetType().Name)));
                        Vector2 size = _target.size;
                        _target.size = this.m_BoundsHandle.size;
                        if (_target.size != size)
                        {
                            _target.offset = this.m_BoundsHandle.center;
                        }
                    }
                }
            }
        }

        private void refreshAnimation()
        {
            if (_skeletonAnimation == null) return;
            if (!_skeletonAnimation.valid)
                return;

            if (!isInspectingPrefab)
            {
                if (_wasAnimationNameChanged)
                {
                    if (_skeletonAnimation.state != null) _skeletonAnimation.state.ClearTrack(0);
                    _skeletonAnimation.skeleton.SetToSetupPose();

                    Spine.Animation animationToUse = _skeletonAnimation.skeleton.Data.FindAnimation(animationName.stringValue);

                    if (animationToUse != null) Spine.SkeletonExtensions.PoseSkeleton(animationToUse, _skeletonAnimation.Skeleton, 0f);
                    _skeletonAnimation.Update(0);
                    _skeletonAnimation.LateUpdate();
                    SceneView.RepaintAll();

                    _curFrameIndex = 0;
                    _wasAnimationNameChanged = false;
                }
            }
        }

        private void prevFrame()
        {
            setFrame(_curFrameIndex - 1);
        }

        private void nextFrame()
        {
            setFrame(_curFrameIndex + 1);
        }

        private void setFrame(int frame)
        {
            int frameMax = _frames.get(animationName.stringValue);
            if (frame < 0 || frame >= frameMax)
                return;

            _curFrameIndex = frame;

            if (!isInspectingPrefab)
            {
                if (_skeletonAnimation.state != null) _skeletonAnimation.state.ClearTrack(0);
                _skeletonAnimation.skeleton.SetToSetupPose();

                Spine.Animation animationToUse = _skeletonAnimation.skeleton.Data.FindAnimation(animationName.stringValue);

                if (animationToUse != null) Spine.SkeletonExtensions.PoseSkeleton(animationToUse, _skeletonAnimation.Skeleton, _curFrameIndex / 30f);
                _skeletonAnimation.Update(0);
                _skeletonAnimation.LateUpdate();
                SceneView.RepaintAll();
            }
        }
    }
}
