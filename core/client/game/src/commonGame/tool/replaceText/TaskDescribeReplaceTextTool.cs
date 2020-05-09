using System;
using ShineEngine;

/// <summary>
/// 物品提示文本替换
/// </summary>
[Hotfix]
public class TaskDescribeReplaceTextTool:ReplaceTextTool
{
	public override string replace(string mark,object obj)
	{
		TaskData data=(TaskData)obj;

		switch(mark)
		{
			case ReplaceTextMarkType.NeedNum:
			{
				return data.config.needNum.ToString();
			}
		}

		return mark;
	}
}