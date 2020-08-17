using System;
using ShineEditor;
using ShineEngine;

/// <summary>
/// 场景编辑器数据
/// </summary>
public class SceneEditorData
{
	/** 配置 */
	public SceneConfig config;

	/** 摆放组 */
	public IntObjectMap<SceneElementEditorData> elements=new IntObjectMap<SceneElementEditorData>();

	public BytesWriteStream _stream;


	/** 读取数据 */
	public void read()
	{
		string path=SceneEditorWindow.getScenePlaceFilePath(config.id);

		BytesReadStream stream=FileUtils.readFileForBytesReadStream(path);

		if(stream!=null)
		{
			int len=stream.readLen();

			for(int i=0;i<len;i++)
			{
				ScenePlaceElementConfig eConfig=new ScenePlaceElementConfig();
				eConfig.readBytesSimple(stream);

				SceneElementEditorData eData=new SceneElementEditorData();
				eData.config=eConfig;

				elements.put(eData.config.instanceID,eData);
			}
		}
	}

	/** 写回 */
	public void write()
	{
		Ctrl.print("保存场景",config.id);

		if(_stream==null)
			_stream=new BytesWriteStream();
		else
			_stream.clear();

		_stream.writeLen(elements.size());

		foreach(int k in elements.getSortedKeyList())
		{
			ScenePlaceElementConfig scenePlaceElementConfig = elements.get(k).config;
			scenePlaceElementConfig.writeBytesSimple(_stream);
		}

		string path=SceneEditorWindow.getScenePlaceFilePath(config.id);
		FileUtils.writeFileForBytesWriteStream(path,_stream);
	}
}