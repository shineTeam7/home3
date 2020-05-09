using System;
using ShineEngine;

/// <summary>
/// 物品提示文本替换
/// </summary>
[Hotfix]
public class ItemTipsReplaceTextTool:ReplaceTextTool
{
	public override string replace(string mark,object obj)
	{
		ItemData data=(ItemData)obj;

		switch(mark)
		{
			case ReplaceTextMarkType.ItemName:
			{
				return data.config.name;
			}
		}

		return mark;
	}
}