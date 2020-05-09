using System;
using ShineEngine;
using UnityEngine;

/// <summary>
///
/// </summary>
public class UII18NText:UIObject
{
	private I18NText _i18NText;

	public UII18NText()
	{
		_type=UIElementType.I18NText;
	}

	public I18NText i18NText
	{
		get {return _i18NText;}
	}

	public override void init(GameObject obj)
	{
		base.init(obj);

		_i18NText=gameObject.GetComponent<I18NText>();
		// FontControl.addText(_i18NText);
	}

	protected override void dispose()
	{
		base.dispose();

		// FontControl.removeText(_i18NText);
		_i18NText = null;
	}

	public void setString(string text)
	{
		_i18NText.text=text;
	}

	public void setFont(int fontId)
	{
		_i18NText.setFont(fontId);
	}
}