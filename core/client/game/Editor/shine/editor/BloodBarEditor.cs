using System;
using ShineEngine;
using UnityEngine.UI;
using UnityEditor;

namespace ShineEditor
{
    /// <summary>
    /// 
    /// </summary>
    [CustomEditor(typeof(BloodBar), true)]
    public class BloodBarEditor : Editor
    {
        private SerializedProperty progress;
        private SerializedProperty image;

        private BloodBar _bloodBar;

        private void OnEnable()
        {
            progress = serializedObject.FindProperty("_progress");

            _bloodBar = (BloodBar)target;
        }

        public override void OnInspectorGUI()
        {
            base.OnInspectorGUI();

            _bloodBar.setProgress(progress.floatValue);
        }
    }
}
