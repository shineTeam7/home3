using System;
using ShineEngine;

namespace ShineEditor
{
    /// <summary>
    /// 打包类型
    /// </summary>
    public static class PackType
    {
        /** 路径中所有文件打包成同一个bundle */
        public const int One = 1;
        /** 路径中所有文件单独打包，每个文件一个bundle 此类型只处理文件，忽略根目录中的文件夹 */
        public const int Single = 2;
        /** 路径中所有文件夹打包成一个bundle，此类型只处理文件夹，忽略根目录中的单独文件 */
        public const int Directory = 3;
    }
}
