using System;
using ShineEngine;

using UnityEngine;

namespace ShineEditor
{
    /// <summary>
    /// 工具元件颜色辅助类
    /// </summary>
    public class ContentColor : IDisposable
    {
        private Color _color;

        public ContentColor(Color color)
        {
            _color = GUI.contentColor;
            GUI.contentColor = color;
        }

        public void Dispose()
        {
            GUI.contentColor = _color;
        }
    }
}
