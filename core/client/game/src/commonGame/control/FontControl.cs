using System;
using ShineEngine;

/// <summary>
/// 国际化控制
/// </summary>
public static class FontControl
{
	/** 文本列表 */
	private static SSet<I18NText> _textSet=new SSet<I18NText>();

	public static void addText(I18NText text)
	{
		_textSet.add(text);
	}

	public static void removeText(I18NText text)
	{
		_textSet.remove(text);
	}

	/// <summary>
	/// 刷新语言
	/// </summary>
	public static void refreshLanguage()
	{
		if(!_textSet.isEmpty())
		{
			I18NText[] keys=_textSet.getKeys();

			for(int i=keys.Length-1;i>=0;--i)
			{
				I18NText k;
				if((k=keys[i])!=null)
				{
					k.refreshLanguage();
				}
			}
		}
	}
}