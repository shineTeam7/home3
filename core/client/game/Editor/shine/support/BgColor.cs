using System;
using ShineEngine;

using UnityEngine;

namespace ShineEditor
{
    /// <summary>
    /// 工具元件背景颜色辅助类
    /// </summary>
    public class BgColor : IDisposable
    {
        private Color _color;

        public BgColor(Color color)
        {
            _color = GUI.backgroundColor;
            GUI.backgroundColor = color;
        }

        public void Dispose()
        {
            GUI.backgroundColor = _color;
        }
    }
}
