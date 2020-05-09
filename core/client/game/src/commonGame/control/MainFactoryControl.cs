using System;
using ShineEngine;

/// <summary>
/// 
/// </summary>
public class MainFactoryControl
{
	/** 创建登录控制 */
	public virtual MainLoginControl createLoginControl()
	{
		return new MainLoginControl();
	}

	/** 热更控制 */
	public virtual CILRuntimeControl createILRuntimeControl()
	{
		return new CILRuntimeControl();
	}

	/** 音频控制 */
	public virtual AudioControl createAudioControl()
	{
		return new AudioControl();
	}

	/** 本地存错 */
	public virtual LocalSaveControl createLocalSaveControl()
	{
		return new LocalSaveControl();
	}

	/** 创建debug控制 */
	public virtual GameNatureUIControl createGameNUIControl()
	{
		return new GameNatureUIControl();
	}
}