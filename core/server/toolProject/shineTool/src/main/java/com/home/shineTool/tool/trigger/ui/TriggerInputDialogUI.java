package com.home.shineTool.tool.trigger.ui;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.trigger.TriggerBooleanData;
import com.home.shine.data.trigger.TriggerConfigData;
import com.home.shine.data.trigger.TriggerFloatData;
import com.home.shine.data.trigger.TriggerFuncData;
import com.home.shine.data.trigger.TriggerFuncListData;
import com.home.shine.data.trigger.TriggerIntData;
import com.home.shine.data.trigger.TriggerLongData;
import com.home.shine.data.trigger.TriggerObjData;
import com.home.shine.data.trigger.TriggerStringData;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.func.ObjectCall;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.code.CodeInfo;

import javax.swing.*;
import java.awt.*;

/** 输入框 */
public class TriggerInputDialogUI
{
	private static final int Value_Text=1;
	private static final int Value_Bool=2;
	private static final int Value_TriggerCls=3;
	
	public TriggerMainUI mainUI;
	
	private CodeInfo _code;
	
	
	private JDialog _dialog;
	private JPanel _panel;
	
	private Component _parent;
	
	private JLabel _title;
	
	private JRadioButton _useFuncBt;
	private JRadioButton _useValueBt;
	private ButtonGroup _buttonGroup;
	private boolean _isFunc=false;
	
	private JRadioButton _trueBt;
	private JRadioButton _falseBt;
	private ButtonGroup _boolButtonGroup;
	private boolean _isTrue=false;
	
	private JTextField _valueText;
	
	/** trigger类选择 */
	private Choice _triggerClsChoice;
	
	private int _valueShowType=Value_Text;
	
	private SMap<String,TriggerConfigData> _triggerClsDic;
	
	private String _selectTriggerCls="";
	
	
	private Choice _triggerTypeChoice;
	
	private Choice _triggerChoice;
	
	private JButton _sureButton;
	
	private SMap<String,SList<MethodInfo>> _triggerTypeDic;
	
	private SMap<String,MethodInfo> _triggerDic=new SMap<>();
	
	private String _selectTriggerType="";
	
	private String _selectTriggerName="";
	
	//content
	private TriggerContentUI _content;
	
	private TriggerObjData _currentData;
	private String _returnType;
	private int _returnVarType;
	
	private TriggerContentUI.ObjInfo _objInfo=new TriggerContentUI.ObjInfo();
	
	private ObjectCall<TriggerObjData> _callFunc;
	
	private boolean _isTriggerCls=false;
	
	public void show(JFrame frame,TriggerMainUI mainUI,String returnType,boolean isEventFunc,TriggerObjData currentData,ObjectCall<TriggerObjData> func)
	{
		_dialog=new JDialog(frame);
		_parent=frame;
		this.mainUI=mainUI;
		init();
		doShow(returnType,isEventFunc,currentData,func);
	}
	
	public void show(JDialog dialog,TriggerMainUI mainUI,String returnType,boolean isEventFunc,TriggerObjData currentData,ObjectCall<TriggerObjData> func)
	{
		_dialog=new JDialog(dialog);
		_parent=dialog;
		this.mainUI=mainUI;
		init();
		doShow(returnType,isEventFunc,currentData,func);
	}
	
	public void init()
	{
		_code=CodeInfo.getCode(CodeType.Java);
		
		_objInfo.mainMethodFixed=true;
		
		_dialog.setModal(true);
		_dialog.setLayout(null);
		_dialog.setBounds(0,0,600,400);
		
		_panel=new JPanel();
		_panel.setLayout(null);
		_panel.setBounds(0,0,600,400);
		_dialog.add(_panel);
		
		_title=new JLabel();
		_title.setBounds(200,0,200,20);
		_title.setText("Title");
		_panel.add(_title);
		
		_sureButton=new JButton();
		_sureButton.setMargin(TriggerMainUI.insets);
		_sureButton.setBounds(530,350,50,20);
		_sureButton.setText("确定");
		_sureButton.addActionListener(v->
		{
			sure();
		});
		
		_panel.add(_sureButton);
		
		_useFuncBt=new JRadioButton("函数");
		_useFuncBt.setBounds(5,25,80,20);
		_panel.add(_useFuncBt);
		_useFuncBt.addActionListener(v->
		{
			if(!_isFunc)
				selectFuncOrValue(true);
		});
		
		_useValueBt=new JRadioButton("值");
		_useValueBt.setBounds(5,350,80,20);
		_panel.add(_useValueBt);
		
		_useValueBt.addActionListener(v->
		{
			if(_isFunc)
				selectFuncOrValue(false);
		});
		
		_buttonGroup=new ButtonGroup();
		_buttonGroup.add(_useFuncBt);
		_buttonGroup.add(_useValueBt);
		
		_valueText=new JTextField();
		_valueText.setText("");
		_valueText.setBounds(85,350,200,20);
		_panel.add(_valueText);
		
		
		_trueBt=new JRadioButton("是");
		_trueBt.setBounds(85,350,50,20);
		_panel.add(_trueBt);
		_trueBt.addActionListener(v->
		{
			if(!_isTrue)
				selectTrueOfFalse(true);
		});
		
		_falseBt=new JRadioButton("非");
		_falseBt.setBounds(140,350,50,20);
		_panel.add(_falseBt);
		_falseBt.addActionListener(v->
		{
			if(_isTrue)
				selectTrueOfFalse(false);
		});
		
		_boolButtonGroup=new ButtonGroup();
		_boolButtonGroup.add(_trueBt);
		_boolButtonGroup.add(_falseBt);
		
		
		_triggerClsChoice= new Choice();
		_triggerClsChoice.setBounds(85,350,200,20);
		_triggerClsChoice.addItemListener(v->
		{
			String value=(String)v.getItem();
			
			if(_selectTriggerCls.equals(value))
				return;
			
			selectTriggerCls(value);
		});
		
		_panel.add(_triggerClsChoice);
		
		
		
		_triggerTypeChoice=new Choice();
		_triggerTypeChoice.setBounds(100,25,100,20);
		_triggerTypeChoice.addItemListener(v->
		{
			selectTriggerType((String)v.getItem());
		});
		
		_panel.add(_triggerTypeChoice);
		
		_triggerChoice= new Choice();
		_triggerChoice.setBounds(210,25,300,20);
		_triggerChoice.addItemListener(v->
		{
			selectTrigger((String)v.getItem());
		});
		
		_panel.add(_triggerChoice);
		
		_content=new TriggerContentUI();
		_content.setDialog(_dialog);
		_content.init(mainUI,_panel,100,100,480,240);
	}
	
	private void setEnable(boolean enableFunc,boolean enableValue)
	{
		_useFuncBt.setVisible(enableFunc);
		_useValueBt.setVisible(enableValue);
		
		if(!enableFunc)
		{
			selectFuncOrValue(false);
		}
		
		if(!enableValue)
		{
			selectFuncOrValue(true);
		}
		
		_triggerTypeChoice.setVisible(enableFunc);
		_triggerChoice.setVisible(enableFunc);
		_content.setVisible(enableFunc);
		
		
		_valueText.setVisible(enableValue);
		_trueBt.setVisible(enableValue);
		_falseBt.setVisible(enableValue);
		_triggerClsChoice.setVisible(enableValue);
	}
	
	/** true:func,false:value */
	private void setValueShowType(int type)
	{
		_valueShowType=type;
		
		_valueText.setVisible(type==Value_Text);
		_trueBt.setVisible(type==Value_Bool);
		_falseBt.setVisible(type==Value_Bool);
		_triggerClsChoice.setVisible(type==Value_TriggerCls);
	}
	
	/** true:func,false:value */
	private void selectFuncOrValue(boolean value)
	{
		_isFunc=value;
		
		_buttonGroup.setSelected(_useFuncBt.getModel(),value);
		_buttonGroup.setSelected(_useValueBt.getModel(),!value);
		
		_triggerTypeChoice.setEnabled(value);
		_triggerChoice.setEnabled(value);
		
		_content.setEnabled(value);
		
		_valueText.setEnabled(!value);
		_trueBt.setEnabled(!value);
		_trueBt.setEnabled(!value);
		_triggerClsChoice.setEnabled(!value);
	}
	
	private void selectTrueOfFalse(boolean value)
	{
		_isTrue=value;
		_boolButtonGroup.setSelected(_trueBt.getModel(),value);
		_boolButtonGroup.setSelected(_falseBt.getModel(),!value);
	}
	
	private void doShow(String returnType,boolean isEventFunc,TriggerObjData currentData,ObjectCall<TriggerObjData> func)
	{
		_returnType=returnType;
		
		_currentData=currentData==null ? null : (TriggerObjData)currentData.clone();
		_callFunc=func;
		
		_triggerTypeChoice.removeAll();
		_triggerChoice.removeAll();
		
		_isTriggerCls=returnType.startsWith("Class<?");
		
		//修正为Int
		if(_isTriggerCls)
		{
			_returnVarType=VarType.Int;
			setEnable(false,true);
			
			setValueShowType(Value_TriggerCls);
			
			_triggerTypeDic=null;
			
			_triggerClsChoice.removeAll();
			
			_triggerClsDic=mainUI.getCurrentTriggers();
			
			_triggerClsDic.getSortedKeyList().forEach(v->
			{
				_triggerClsChoice.add(v);
			});
			
			if(_currentData!=null)
			{
				TriggerIntData iData=(TriggerIntData)_currentData;
				
				TriggerConfigData tData=mainUI.getTriggerData(iData.value);
				
				_selectTriggerCls=tData.name;
				_triggerClsChoice.select(tData.name);
			}
		}
		else
		{
			if(_returnType.equals(_code.Void))
			{
				_returnVarType=VarType.Null;
				setEnable(true,false);
				
				_triggerTypeDic=isEventFunc ? mainUI.getMethodEventDic() : mainUI.getMethodFuncDic(_code.Void);
				
				_triggerTypeDic.getSortedKeyList().forEach(v->
				{
					_triggerTypeChoice.add(v);
				});
			}
			else
			{
				_returnVarType=_code.getVarType(returnType);
				
				if(VarType.isBaseType(_returnVarType))
				{
					setEnable(true,true);
				}
				else
				{
					setEnable(true,false);
				}
				
				_triggerTypeDic=mainUI.getMethodFuncDic(returnType);
				
				_triggerTypeDic.getSortedKeyList().forEach(v->
				{
					_triggerTypeChoice.add(v);
				});
			}
			
			boolean has=false;
			
			switch(_returnVarType)
			{
				case VarType.Int:
				{
					setValueShowType(Value_Text);
					if(_currentData instanceof TriggerIntData)
					{
						has=true;
						_valueText.setText(String.valueOf(((TriggerIntData)_currentData).value));
					}
					else
					{
						_valueText.setText("");
					}
				}
				break;
				case VarType.Float:
				{
					setValueShowType(Value_Text);
					if(_currentData instanceof TriggerFloatData)
					{
						has=true;
						_valueText.setText(String.valueOf(((TriggerFloatData)_currentData).value));
					}
					else
					{
						_valueText.setText("");
					}
				}
				break;
				case VarType.Long:
				{
					setValueShowType(Value_Text);
					if(_currentData instanceof TriggerLongData)
					{
						has=true;
						_valueText.setText(String.valueOf(((TriggerLongData)_currentData).value));
					}
					else
					{
						_valueText.setText("");
					}
				}
				break;
				case VarType.Boolean:
				{
					setValueShowType(Value_Bool);
					if(_currentData instanceof TriggerBooleanData)
					{
						has=true;
						selectTrueOfFalse(((TriggerBooleanData)_currentData).value);
					}
					else
					{
						selectTrueOfFalse(false);
					}
				}
				break;
				case VarType.String:
				{
					setValueShowType(Value_Text);
					if(_currentData instanceof TriggerStringData)
					{
						has=true;
						_valueText.setText(((TriggerStringData)_currentData).value);
					}
					else
					{
						_valueText.setText("");
					}
				}
				break;
			}
			
			if(_currentData!=null)
			{
				if(_currentData instanceof TriggerFuncData)
				{
					selectFuncOrValue(true);
					
					MethodInfo method=mainUI.getMethodDic().get(((TriggerFuncData)_currentData).id);
					
					//先设置
					_selectTriggerName=method.getShowName();
					
					String tType=mainUI.getMethodTriggerType(method.name);
					_triggerTypeChoice.select(tType);
					selectTriggerType(tType);
					
					_triggerChoice.select(_selectTriggerName);
					
					refreshMethodContent();
				}
				else
				{
					if(has)
					{
						selectFuncOrValue(false);
					}
					else
					{
						Ctrl.warnLog("传入的triggerData类型不正确",_currentData,returnType);
					}
				}
			}
			else
			{
				if(VarType.isBaseType(_returnVarType))
				{
					selectFuncOrValue(false);
				}
				else
				{
					selectFuncOrValue(true);
				}
			}
		}
		
		_dialog.setLocationRelativeTo(_parent);
		Point pt=_dialog.getLocation();
		_dialog.setLocation(pt.x+20,pt.y+20);
		_dialog.setVisible(true);
	}
	
	private void selectTriggerType(String value)
	{
		if(_selectTriggerType.equals(value))
			return;
		
		_selectTriggerType=value;
		
		SList<MethodInfo> methodInfos=_triggerTypeDic.get(value);
		
		_triggerDic.clear();
		_triggerChoice.removeAll();
		
		methodInfos.forEach(v->
		{
			String showName=v.getShowName();
			_triggerChoice.add(showName);
			_triggerDic.put(showName,v);
		});
	}
	
	private void selectTrigger(String value)
	{
		if(_selectTriggerName.equals(value))
			return;
		
		_selectTriggerName=value;
		
		MethodInfo method=_triggerDic.get(value);
		
		TriggerFuncData fData=new TriggerFuncData();
		fData.id=mainUI.gameMakeTool.functionDefineDic.get(method.name);
		fData.args=new TriggerObjData[method.args.length()];
		
		for(int i=0;i<method.args.length();i++)
		{
			MethodArgInfo methodArgInfo=method.args.get(i);
			
			//list补null
			if(methodArgInfo.type.equals("Runnable"))
			{
				TriggerFuncListData fListData=new TriggerFuncListData();
				fListData.funcList=new TriggerFuncData[0];
				fData.args[i]=fListData;
			}
		}
		
		_currentData=fData;
		
		refreshMethodContent();
	}
	
	private void selectTriggerCls(String value)
	{
		_selectTriggerCls=value;
	}
	
	private void refreshMethodContent()
	{
		_objInfo.dispose();
		
		_content.reset();
		_objInfo.init(_content,_currentData,_returnType,v->
		{
			_currentData=v;
			refreshMethodContent();
		});
		
		_content.makeSize();
	}
	
	private void sure()
	{
		if(_isTriggerCls)
		{
			if(_selectTriggerCls.isEmpty())
			{
				errorAlert("需要选择trigger类");
				return;
			}
			
			TriggerConfigData triggerConfigData=mainUI.getCurrentTriggers().get(_selectTriggerCls);
			
			TriggerIntData iData=new TriggerIntData();
			iData.value=triggerConfigData.id;
			_currentData=iData;
		}
		else
		{
			if(_isFunc)
			{
				if(_currentData==null)
				{
					errorAlert("需要选择trigger");
					return;
				}
				
				//检查
				TriggerFuncData fData=(TriggerFuncData)_currentData;
				MethodInfo methodInfo=mainUI.getMethodDic().get(fData.id);
				
				if(fData.args.length!=methodInfo.args.length())
				{
					errorAlert("参数数目居然不对");
					return;
				}
				
				for(int i=0;i<fData.args.length;i++)
				{
					if(fData.args[i]==null)
					{
						errorAlert("参数"+i+"为空");
						return;
					}
				}
			}
			else
			{
				switch(_returnVarType)
				{
					case VarType.Int:
					{
						int re=0;
						try
						{
							re=Integer.parseInt(_valueText.getText());
						}
						catch(Exception e)
						{
							errorAlert("输入的Int非法: "+_valueText.getText());
							return;
						}
						
						TriggerIntData data=new TriggerIntData();
						data.value=re;
						_currentData=data;
					}
					break;
					case VarType.Float:
					{
						float re=0;
						try
						{
							re=Float.parseFloat(_valueText.getText());
						}
						catch(Exception e)
						{
							errorAlert("输入的Float非法: "+_valueText.getText());
							return;
						}
						
						TriggerFloatData data=new TriggerFloatData();
						data.value=re;
						_currentData=data;
					}
					break;
					case VarType.Long:
					{
						long re=0;
						try
						{
							re=Long.parseLong(_valueText.getText());
						}
						catch(Exception e)
						{
							errorAlert("输入的Long非法: "+_valueText.getText());
							return;
						}
						
						TriggerLongData data=new TriggerLongData();
						data.value=re;
						_currentData=data;
					}
					break;
					case VarType.Boolean:
					{
						TriggerBooleanData data=new TriggerBooleanData();
						data.value=_isTrue;
						_currentData=data;
					}
						break;
					case VarType.String:
					{
						TriggerStringData data=new TriggerStringData();
						data.value=_valueText.getText();
						_currentData=data;
					}
						break;
				}
			}
		}
		
		this._dialog.setVisible(false);
		this._dialog.dispose();
		
		if(_callFunc!=null)
			_callFunc.apply(_currentData);
	}
	
	private void errorAlert(String msg)
	{
		JOptionPane.showMessageDialog(_panel,msg);
	}
}
