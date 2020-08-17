package com.home.shineTool.tool.trigger.ui;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.trigger.TriggerConfigData;
import com.home.shine.data.trigger.TriggerFuncData;
import com.home.shine.data.trigger.TriggerFuncListData;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shineTool.app.TriggerUIApp;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.tool.trigger.TriggerDataExportTool;
import com.home.shineTool.tool.trigger.TriggerMakeTool;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/** 主界面 */
public class TriggerMainUI
{
	public static Insets insets=new Insets(0,0,0,0);
	
	/** ui工具 */
	public TriggerUIApp uiApp;
	
	public TriggerMakeTool gameMakeTool;
	
	public TriggerDataExportTool dataTool;
	
	public TriggerUIDataExportTool uiDataTool;
	
	private CodeInfo _code;
	
	/** 方法组 */
	private IntObjectMap<MethodInfo> _methodDic=new IntObjectMap<>();
	
	/** 方法分组列表(key1:返回值,key2:tType) */
	private SMap<String,SMap<String,SList<MethodInfo>>> _methodFuncDic=new SMap<>();
	
	/** 方法分组列表(key1:返回值,key2:tType) */
	private SMap<String,SList<MethodInfo>> _methodEventDic=new SMap<>();
	
	/** 使用集合(key1:groupType,key2:groupID,key3:name) */
	private IntObjectMap<IntObjectMap<SMap<String,TriggerConfigData>>> _allDic=new IntObjectMap<>();
	
	/** 数组组 */
	private IntObjectMap<TriggerConfigData> _dataDic=new IntObjectMap<>(TriggerConfigData[]::new);
	/** triggerID */
	private int _triggerNextID=0;
	
	private int _selectGroupType=-1;
	
	private int _selectGroupID=-1;
	
	private String _selectTriggerName="";
	
	private TriggerConfigData _currentTrigger;
	
	
	
	
	private JFrame _frame;
	
	private JPanel _panel;
	
	private Choice _groupTypeChoice;
	/** trigger组数据 */
	private DefaultListModel<Integer> _triggerConfigGroupList=new DefaultListModel<Integer>();
	private JList<Integer> _triggerGroupListUI;
	private JScrollPane _triggerGroupPanel;
	
	private DefaultListModel<String> _triggerNameDataList=new DefaultListModel<String>();
	private JList<String> _triggerNameListUI;
	private JScrollPane _triggerNamePanel;
	
	//right
	private JCheckBox _isOpenCheckBox;
	private JTextField _priorityText;
	
	private LineBorder _lineBorder;
	
	private TriggerContentUI _content;
	
	private boolean _triggerContentShow=false;
	
	private JLabel[] _partLabels=new JLabel[3];
	private TriggerFuncListData[] _partListDatas=new TriggerFuncListData[3];
	private TriggerContentUI.ObjInfo[] _partInfos=new TriggerContentUI.ObjInfo[3];
	
	//dialog
	
	/** 初始化数据 */
	protected void initData()
	{
		gameMakeTool=uiApp.export.gameTool;
		dataTool=uiApp.export.dataTool;
		
		_code=CodeInfo.getCode(CodeType.Java);
		
		_methodDic=gameMakeTool.functionInfoDic.clone();
		
		_methodDic.getSortedKeyList().forEach(k->
		{
			MethodInfo methodInfo=_methodDic.get(k);
			
			String tType=getMethodTriggerType(methodInfo.name);
			
			//event
			if(methodInfo.returnType.equals(_code.Void) && gameMakeTool.isEventFunc(methodInfo.name))
			{
				_methodEventDic.computeIfAbsent(tType,k3->new SList<>()).add(methodInfo);
			}
			//func
			else
			{
				_methodFuncDic.computeIfAbsent(methodInfo.returnType,k2->new SMap<>()).computeIfAbsent(tType,k3->new SList<>()).add(methodInfo);
			}
		});
		
		_dataDic=dataTool.getAllDic();
		
		_dataDic.forEachValue(v->
		{
			_allDic.computeIfAbsent(v.groupType,k1->new IntObjectMap<>())
					.computeIfAbsent(v.groupID,k2->new SMap<>())
					.put(v.name,v);
		});
		
		for(int i=0;i<_partInfos.length;i++)
		{
			_partInfos[i]=new TriggerContentUI.ObjInfo();
		}
		
		for(int i=0;i<_partListDatas.length;i++)
		{
			_partListDatas[i]=new TriggerFuncListData();
		}
	}
	
	public String getMethodTriggerType(String name)
	{
		String tType=gameMakeTool.functionTTypeDic.get(name);
		
		if(tType== null || tType.equals(""))
		{
			tType="基础";
		}
		
		return tType;
	}
	
	public IntObjectMap<IntObjectMap<SMap<String,TriggerConfigData>>> getAllDic()
	{
		return _allDic;
	}
	
	public TriggerConfigData getTriggerData(int id)
	{
		return _dataDic.get(id);
	}
	
	public IntObjectMap<MethodInfo> getMethodDic()
	{
		return _methodDic;
	}
	
	public SMap<String,SList<MethodInfo>> getMethodEventDic()
	{
		return _methodEventDic;
	}
	
	public SMap<String,SList<MethodInfo>> getMethodFuncDic(String returnType)
	{
		return _methodFuncDic.get(returnType);
	}
	
	public int getNextTriggerID()
	{
		++_triggerNextID;
		
		while(_dataDic.contains(_triggerNextID))
		{
			++_triggerNextID;
		}
		
		return _triggerNextID;
	}
	
	/** 初始化 */
	public void init()
	{
		initData();
		
		_frame=new JFrame();
		_frame.setSize(1280, 720);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.setLayout(null);
		
		_panel=new JPanel();
		_panel.setLayout(null);
		_panel.setBounds(0,0,1280,720);
		_frame.add(_panel);
		
		JPanel line=new JPanel();
		line.setLayout(null);
		line.setBackground(Color.gray);
		line.setBounds(320,0,10,720);
		_panel.add(line);
		
		JPanel line2=new JPanel();
		line2.setLayout(null);
		line2.setBackground(Color.gray);
		line2.setBounds(330,50,1280-330,10);
		_panel.add(line2);
		
		initLeftPanel();
		
		initRightPanel();
		
		//refresh
		selectGroupType(_groupTypeChoice.getItem(0));
		
		// 设置界面可见
		_frame.setVisible(true);
	}
	
	public JFrame getFrame()
	{
		return _frame;
	}
	
	public JPanel getPanel()
	{
		return _panel;
	}
	
	private void initLeftPanel()
	{
		JLabel label=new JLabel();
		label.setText("组别");
		label.setBounds(15,5,100,20);
		
		_panel.add(label);
		
		_groupTypeChoice= new Choice();
		
		IntObjectMap<String> map=new IntObjectMap<>();
		gameMakeTool.groupDefineDic.forEach((k,v)->
		{
			map.put(v,k);
		});
		
		map.getSortedKeyList().forEach(k->
		{
			_groupTypeChoice.add(map.get(k));
		});
		
		_groupTypeChoice.addItemListener(v->
		{
			selectGroupType((String)v.getItem());
		});
		
		_groupTypeChoice.select(0);
		
		_groupTypeChoice.setBounds(10,30,100,30);
		
		_panel.add(_groupTypeChoice);
		
		_triggerGroupListUI=new JList<Integer>(_triggerConfigGroupList);
		
		_triggerGroupListUI.setBounds(10,70,200,300);
		
		_triggerGroupListUI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		_triggerGroupListUI.addListSelectionListener(v->
		{
			Integer sv=_triggerGroupListUI.getSelectedValue();
			
			if(sv!=null)
				selectGroup(sv);
		});
		
		_triggerGroupPanel = new JScrollPane(_triggerGroupListUI);
		_triggerGroupPanel.setBounds(10,70,200,300);
		_panel.add(_triggerGroupPanel);
		
		JButton addGroupBt=new JButton("添加组");
		addGroupBt.setBounds(230,70,50,20);
		addGroupBt.addActionListener(v->
		{
			TriggerAddDialogUI dialog=new TriggerAddDialogUI();
			dialog.mainUI=this;
			dialog.showInt("添加组",v2->
			{
				IntObjectMap<SMap<String,TriggerConfigData>> dic=_allDic.get(_selectGroupType);
				
				if(dic!=null)
				{
					if(dic.contains(v2))
					{
						JOptionPane.showMessageDialog(_panel,"组id重复: "+v2);
						return;
					}
					
					_triggerConfigGroupList.addElement(v2);
					dic.put(v2,new SMap<>());
				}
			});
		});
		
		_panel.add(addGroupBt);
		
		JButton removeGroupBt=new JButton("删除组");
		removeGroupBt.setBounds(230,90,50,20);
		removeGroupBt.addActionListener(v->
		{
			if(_selectGroupID==-1)
				return;
			
			IntObjectMap<SMap<String,TriggerConfigData>> dic=_allDic.get(_selectGroupType);
			dic.remove(_selectGroupID);
			resetGroupID();
		});
		
		_panel.add(removeGroupBt);
		
		//triggers
		_triggerNameListUI=new JList<String>(_triggerNameDataList);
		
		_triggerNameListUI.setBounds(10,380,200,300);
		
		_triggerNameListUI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		_triggerNameListUI.addListSelectionListener(v->
		{
			String sv=_triggerNameListUI.getSelectedValue();
			
			if(sv!=null)
				selectTrigger(sv);
		});
		
		//_triggerNameListUI.setVisibleRowCount(10);
		
		_triggerNamePanel = new JScrollPane(_triggerNameListUI);
		_triggerNamePanel.setBounds(10,380,200,300);
		_panel.add(_triggerNamePanel);
		
		JButton addTriggerBt=new JButton("添加触发");
		addTriggerBt.setBounds(230,380,80,20);
		addTriggerBt.addActionListener(v->
		{
			TriggerAddDialogUI dialog=new TriggerAddDialogUI();
			dialog.mainUI=this;
			dialog.showString("添加触发",v2->
			{
				IntObjectMap<SMap<String,TriggerConfigData>> dic=_allDic.get(_selectGroupType);
				
				if(dic!=null)
				{
					SMap<String,TriggerConfigData> dic2=dic.get(_selectGroupID);
					
					if(dic2.contains(v2))
					{
						JOptionPane.showMessageDialog(_panel,"triggerID重复: "+v2);
						return;
					}
					
					TriggerConfigData config=new TriggerConfigData();
					config.id=getNextTriggerID();
					config.name=v2;
					config.groupType=_selectGroupType;
					config.groupID=_selectGroupID;
					config.isOpen=false;
					config.priority=0;
					
					config.events=new TriggerFuncData[0];
					config.conditions=new TriggerFuncData[0];
					config.actions=new TriggerFuncData[0];
					
					dic2.put(config.name,config);
					
					resetTriggers();
					
					_triggerNameListUI.setSelectedIndex(_triggerNameDataList.indexOf(config.name));
					
					selectTrigger(config.name);
				}
			});
		});
		
		_panel.add(addTriggerBt);
		
		JButton removeTriggerBt=new JButton("删除触发");
		removeTriggerBt.setBounds(230,400,80,20);
		removeTriggerBt.addActionListener(v->
		{
			if(_selectTriggerName.isEmpty())
				return;
			
			IntObjectMap<SMap<String,TriggerConfigData>> dic=_allDic.get(_selectGroupType);
			
			if(dic==null)
				return;
			
			SMap<String,TriggerConfigData> dic2=dic.get(_selectGroupID);
			
			dic2.remove(_selectTriggerName);
			
			resetTriggers();
		});
		
		_panel.add(removeTriggerBt);
	}
	
	private void initRightPanel()
	{
		_isOpenCheckBox=new JCheckBox("是否初始开启");
		_isOpenCheckBox.setBounds(350,5,120,20);
		_panel.add(_isOpenCheckBox);
		
		JLabel label=new JLabel("执行优先级:");
		label.setBounds(480,5,100,20);
		_panel.add(label);
		
		_priorityText=new JTextField();
		_priorityText.setBounds(550,5,50,20);
		_priorityText.setText("0");
		_panel.add(_priorityText);
		
		//JButton saveCurrentButton=new JButton();
		//saveCurrentButton.setBounds(700,5,60,20);
		//saveCurrentButton.setText("保存当前");
		//saveCurrentButton.addActionListener(v->
		//{
		//	if(_currentTrigger!=null)
		//	{
		//		uiDataTool.exportOneTrigger(_selectGroupType,_selectGroupID,_currentTrigger,false);
		//	}
		//	else
		//	{
		//		Ctrl.print("当前未选择trigger");
		//	}
		//});
		//
		//_panel.add(saveCurrentButton);
		
		JButton saveAllButton=new JButton();
		saveAllButton.setBounds(780,5,60,20);
		saveAllButton.setText("保存全部");
		saveAllButton.addActionListener(v->
		{
			uiDataTool.exportAll();
		});
		
		_panel.add(saveAllButton);
		
		_lineBorder=new LineBorder(Color.gray,1);
		
		_content=new TriggerContentUI();
		_content.setFrame(_frame);
		_content.init(this,_panel,350,70,1280-350-30,720-70-50);
		
		_partLabels[0]=new JLabel("事件:");
		_partLabels[1]=new JLabel("环境:");
		_partLabels[2]=new JLabel("动作:");
		
		for(JLabel la : _partLabels)
		{
			la.setBorder(_lineBorder);
			la.setSize(50,20);
			_content.getContentPanel().add(la);
			
			la.setVisible(false);
		}
	}
	
	/** 选择群组类型 */
	private void selectGroupType(String group)
	{
		int groupType=gameMakeTool.groupDefineDic.get(group);
		
		if(_selectGroupType==groupType)
			return;
		
		_selectGroupType=groupType;
		
		resetGroupID();
	}
	
	private void resetGroupID()
	{
		_selectGroupID=-1;
		_triggerGroupListUI.clearSelection();
		_triggerConfigGroupList.clear();
		
		IntObjectMap<SMap<String,TriggerConfigData>> dic=_allDic.get(_selectGroupType);
		
		if(dic!=null)
		{
			dic.getSortedKeyList().forEach(v->
			{
				_triggerConfigGroupList.addElement(v);
			});
		}
		
		clearGroupSelect();
	}
	
	private void clearGroupSelect()
	{
		_selectTriggerName="";
		_triggerNameListUI.clearSelection();
		_triggerNameDataList.clear();
		
		clearTriggerShow();
	}
	
	/** 选组序号 */
	private void selectGroup(int id)
	{
		//重复选择
		if(_selectGroupID==id)
			return;
		
		_selectGroupID=id;
		
		resetTriggers();
	}
	
	private void resetTriggers()
	{
		_selectTriggerName="";
		_triggerNameListUI.clearSelection();
		_triggerNameDataList.clear();
		
		IntObjectMap<SMap<String,TriggerConfigData>> dic=_allDic.get(_selectGroupType);
		
		if(dic!=null)
		{
			SMap<String,TriggerConfigData> dic2=dic.get(_selectGroupID);
			
			dic2.getSortedKeyList().forEach(v->
			{
				_triggerNameDataList.addElement(v);
			});
		}
		
		clearTriggerShow();
	}
	
	public SMap<String,TriggerConfigData> getCurrentTriggers()
	{
		IntObjectMap<SMap<String,TriggerConfigData>> dic=_allDic.get(_selectGroupType);
		
		if(dic==null)
			return null;
		
		return dic.get(_selectGroupID);
	}
	
	private void clearTriggerShow()
	{
		if(!_triggerContentShow)
			return;
		
		_triggerContentShow=false;
		
		_isOpenCheckBox.setSelected(false);
		_priorityText.setText("0");
		
		for(int i=0;i<3;i++)
		{
			_partLabels[i].setVisible(false);
			_partInfos[i].dispose();
		}
		
		//_panel.updateUI();
	}
	
	private void selectTrigger(String name)
	{
		if(_selectTriggerName.equals(name))
			return;
		
		_selectTriggerName=name;
		
		if(!_selectTriggerName.isEmpty())
		{
			IntObjectMap<SMap<String,TriggerConfigData>> dic=_allDic.get(_selectGroupType);
			
			SMap<String,TriggerConfigData> dic2=dic.get(_selectGroupID);
			
			_currentTrigger=dic2.get(_selectTriggerName);
			
			showTrigger();
		}
		else
		{
			_currentTrigger=null;
			clearTriggerShow();
		}
	}
	
	private void showTrigger()
	{
		if(_triggerContentShow)
		{
			clearTriggerShow();
		}
		
		_triggerContentShow=true;
		
		_isOpenCheckBox.setSelected(_currentTrigger.isOpen);
		_priorityText.setText(String.valueOf(_currentTrigger.priority));
		
		refreshTriggerContent();
	}
	
	private void refreshTriggerContent()
	{
		_content.reset();
		
		int w=100;
		
		for(int i=0;i<3;i++)
		{
			_content.resetX();
			
			_partLabels[i].setLocation(_content.getTx(),_content.getTy());
			_partLabels[i].setVisible(true);
			
			_content.nextLine();
			
			TriggerFuncData[] fList;
			
			int index=i;
			
			if(i==0)
				fList=_currentTrigger.events;
			else if(i==1)
				fList=_currentTrigger.conditions;
			else
				fList=_currentTrigger.actions;
			
			_partListDatas[index].funcList=fList;
			TriggerContentUI.ObjInfo objInfo=_partInfos[index];
			
			//event
			if(index==0)
			{
				objInfo.isEventFunc=true;
			}
			
			objInfo.init(_content,_partListDatas[index],_code.Void,v->
			{
				TriggerFuncListData lData=_partListDatas[index]=(TriggerFuncListData)v;
				
				if(index==0)
					_currentTrigger.events=lData.funcList;
				else if(index==1)
					_currentTrigger.conditions=lData.funcList;
				else
					_currentTrigger.actions=lData.funcList;
				
				showTrigger();
			});
		}
		
		_content.makeSize();
	}
}