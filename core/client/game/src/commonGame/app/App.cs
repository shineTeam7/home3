using ShineEngine;
using UnityEngine;

/// <summary>
/// App基类
/// </summary>
public class App
{
	public App()
	{

	}

	/// <summary>
	/// 预设置
	/// </summary>
	protected virtual void preInit()
	{
		
	}

	//启动
	public void start()
	{
		preInit();

		//构造必需controls
		makeControls();

		onStart();
	}

	/** 准备(for Editor) */
	public void startForEditor()
	{
		preInit();
		//构造必需controls
		makeControls();

		onStartForEditor();
	}

	/** 启动 */
	protected virtual void makeControls()
	{

	}

	/** 启动 */
	protected virtual void onStart()
	{

	}

	/** 启动(forEditor) */
	protected virtual void onStartForEditor()
	{

	}
}