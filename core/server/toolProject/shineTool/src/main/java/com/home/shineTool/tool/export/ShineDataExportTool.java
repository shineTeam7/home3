package com.home.shineTool.tool.export;

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
	}
	
	@Override
	protected void toExecute()
	{
		//shine组公用100
		
		DataExportTool data=makeOneData("shineData","Shine",false,_dataProjectRoot + "/data",_serverProjectRoot,_clientProjectRoot,null,1,39,false,this::inputCheck,null);
		
		data.executeAll();
		
		//game
		DataExportTool rquestTool=makeOneRequest("","Shine",false,_dataProjectRoot + "/message",_serverProjectRoot,_clientProjectRoot,null,40,20,false,null);
		makeOneResponse("","Shine",false,_dataProjectRoot + "/message",_serverProjectRoot,_clientProjectRoot,null,40,20,rquestTool,null);
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