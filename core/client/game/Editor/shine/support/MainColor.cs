using System;
using ShineEngine;

using UnityEngine;

namespace ShineEditor
{
    /// <summary>
    /// 工具元件颜色辅助类
    /// </summary>
    public class MainColor : IDisposable
    {
        private Color _color;

        public MainColor(Color color)
        {
            _color = GUI.color;
            GUI.color = color;
        }

        public void Dispose()
        {
            GUI.color = _color;
        }
    }
}
