package com.home.shineTool.tool.export;

import com.home.shineTool.constlist.DataSectionType;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.tool.data.DataExportTool;

public class ShineDataExportTool extends BaseDataExportTool
{
	//shine预留
	public static final int shineDataMax=200;
	
	/** 数据工程根 */
	protected String _dataProjectRoot;
	/** s-base工程根 */
	protected String _serverProjectRoot;
	/** c-base工程根 */
	protected String _clientProjectRoot;
	
	public ShineDataExportTool()
	{
		setIsCommon(true);
	}
	
	@Override
	protected void initNext()
	{
		_dataProjectRoot=ShineToolGlobal.dataPath + "/shineData/src/main/java/com/home/shineData";
		_serverProjectRoot=ShineToolGlobal.serverProjectPath + "/shine/src/main/java/com/home/shine";
		_clientProjectRoot=ShineToolGlobal.clientMainSrcPath + "/shine";
		
		addSection(DataSectionType.Data,1,39);
		addSection(DataSectionType.GameRequest,40,20);
	}
	
	
	@Override
	protected void toExecute()
	{
		//shine组公用100
		
		DataExportTool data=makeOneData("shineData","Shine",false,_dataProjectRoot + "/data",_serverProjectRoot,_clientProjectRoot,null,DataSectionType.Data,this::inputCheck,null);
		
		data.executeAll();
		
		//game
		DataExportTool rquestTool=makeOneRequest("","Shine",false,_dataProjectRoot + "/message",_serverProjectRoot,_clientProjectRoot,null,DataSectionType.GameRequest,null);
		makeOneResponse("","Shine",false,_dataProjectRoot + "/message",_serverProjectRoot,_clientProjectRoot,null,DataSectionType.GameRequest,rquestTool,null);
	}
	
	private int inputCheck(String fileName)
	{
		switch(fileName)
		{
			case "BaseDO":
			{
				return 1;
			}
		}
		
		return 0;
	}
}