package com.home.shineTool.tool.trigger.ui;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.trigger.TriggerBooleanData;
import com.home.shine.data.trigger.TriggerFloatData;
import com.home.shine.data.trigger.TriggerFuncData;
import com.home.shine.data.trigger.TriggerFuncListData;
import com.home.shine.data.trigger.TriggerIntData;
import com.home.shine.data.trigger.TriggerLongData;
import com.home.shine.data.trigger.TriggerObjData;
import com.home.shine.data.trigger.TriggerStringData;
import com.home.shine.support.collection.SList;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.pool.ObjectPool;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.code.CodeInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** trigger内容部分 */
public class TriggerContentUI
{
	private static int _charSize=7;
	
	private static int _labelBoard=0;
	
	private static int _labelHeight=20;
	
	private static int _lineHeight=25;
	/** 缩进尺寸 */
	private static int _braceSize=20;
	
	private static Color _listOperateColor=new Color(0,100,100);
	
	private static Color _funcNameColor=new Color(100,100,0);
	
	private static Color _constColor=new Color(0,100,0);
	
	private static Color _nullColor=new Color(150,0,0);
	
	public static Cursor handCursor=Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	
	public TriggerMainUI mainUI;
	
	private CodeInfo _code;
	
	private JFrame _parentFrame;
	private JDialog _parentDialog;
	
	/** label池 */
	public static ObjectPool<FuncLabel> labelPool=new ObjectPool<>(FuncLabel::new);
	/** obj池 */
	public static ObjectPool<ObjInfo> objPool=new ObjectPool<>(ObjInfo::new);
	
	private JPanel _contentPanel;
	private JScrollPane _contentScrollPanel;
	private Dimension _contentDimension;
	
	private int _width;
	private int _tx;
	private int _ty;
	private int _brace=0;
	
	public void init(TriggerMainUI mainUI,JPanel parent,int x,int y,int w,int h)
	{
		this.mainUI=mainUI;
		_code=CodeInfo.getCode(CodeType.Java);
		
		_contentPanel=new JPanel();
		_contentPanel.setLayout(null);
		_contentPanel.setLocation(0,0);
		_contentScrollPanel=new JScrollPane(_contentPanel);
		_contentScrollPanel.setBounds(x,y,w,h);
		parent.add(_contentScrollPanel);
		_contentDimension=new Dimension();
		
	}
	
	public JPanel getContentPanel()
	{
		return _contentPanel;
	}
	
	public void setFrame(JFrame frame)
	{
		_parentFrame=frame;
	}
	
	public void setDialog(JDialog dialog)
	{
		_parentDialog=dialog;
	}
	
	public void addX(int v)
	{
		_tx+=v;
		
		if(_width<_tx)
			_width=_tx;
	}
	
	public void reset()
	{
		_tx=0;
		_ty=0;
		_width=0;
		_brace=0;
	}
	
	public void resetX()
	{
		_tx=0;
	}
	
	public void leftBrace()
	{
		_ty+=_lineHeight;
		_brace++;
		_tx=_brace*_braceSize;
	}
	
	public void nextLine()
	{
		_ty+=_lineHeight;
		_tx=_brace*_braceSize;
	}
	
	public void rightBrace()
	{
		_ty+=_lineHeight;
		_brace--;
		_tx=_brace*_braceSize;
	}
	
	public int getTx()
	{
		return _tx;
	}
	
	public int getTy()
	{
		return _ty;
	}
	
	public int getWidth()
	{
		return _width;
	}
	
	public void makeSize()
	{
		_contentDimension.setSize(_width,_ty);
		_contentPanel.setSize(_width,_ty);
		_contentPanel.setPreferredSize(_contentDimension);
	}
	
	public void setVisible(boolean value)
	{
		_contentPanel.setVisible(value);
		_contentScrollPanel.setVisible(value);
	}
	
	public void setEnabled(boolean value)
	{
		_contentPanel.setEnabled(value);
		_contentScrollPanel.setEnabled(value);
	}
	
	/** 返回机器字数目 */
	public static int getCharMachineNum(String str)
	{
		if(str.isEmpty())
			return 0;
		
		float re=0f;
		int len=str.length();
		int code;
		for(int i=0;i<len;++i)
		{
			code=str.charAt(i);
			//code=Character.codePointAt(str,i);
			
			if(code >= 0 && code<=255)
			{
				if(code>='A' && code<='Z')
					re+=1.5f;
				else if(code>='0' && code<='9')
					re+=1.5f;
				else
					re+=1f;
			}
			else
			{
				re+=2f;
			}
		}
		
		return (int)Math.ceil(re);
	}
	
	public void showDialog(String returnType,boolean isEventFunc,TriggerObjData data,ObjectCall<TriggerObjData> func)
	{
		TriggerInputDialogUI dialog=new TriggerInputDialogUI();
		
		if(_parentFrame!=null)
		{
			dialog.show(_parentFrame,mainUI,returnType,isEventFunc,data,func);
		}
		else if(_parentDialog!=null)
		{
			dialog.show(_parentDialog,mainUI,returnType,isEventFunc,data,func);
		}
	}
	
	public static class FuncLabel extends JLabel
	{
		public Runnable click;
		
		public ObjectCall<MouseEvent> rightClick;
		
		public FuncLabel()
		{
			this.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if(e.getButton()==MouseEvent.BUTTON1)
					{
						if(click!=null)
							click.run();
					}
					else if(e.getButton()==MouseEvent.BUTTON3)
					{
						if(rightClick!=null)
							rightClick.apply(e);
					}
				}
				
			});
		}
		
		public void setNeedHandle(boolean value)
		{
			if(value)
				this.setCursor(handCursor);
			else
				this.setCursor(null);
		}
		
		public void dispose()
		{
			this.setVisible(false);
			
			Container p=getParent();
			
			if(p!=null)
			{
				p.remove(this);
			}
			
			click=null;
			rightClick=null;
		}
	}
	
	/** 对象信息 */
	public static class ObjInfo
	{
		/** 父级 */
		public ObjInfo parent=null;
		
		private TriggerContentUI _con;
		
		/** 数据 */
		public TriggerObjData data;
		
		public String returnType;
		
		private ObjectCall<TriggerObjData> _changeFunc;
		
		public MethodInfo method=null;
		
		/** 固定主函数 */
		public boolean mainMethodFixed=false;
		/** 是否是event函数 */
		public boolean isEventFunc=false;
		
		public FuncLabel mainLabel=null;
		
		public SList<FuncLabel> allLabels=new SList<>();
		
		public SList<ObjInfo> children=new SList<>();
		/** 是否是方法组 */
		public boolean isFuncList=false;
		
		//public int width=0;
		///** 与ny相同 */
		//public int height=0;
		//private int _nx=0;
		//public int _ny=0;
		
		public ObjInfo()
		{
		
		}
		
		public void init(TriggerContentUI con,TriggerObjData data,String returnType,ObjectCall<TriggerObjData> changeFunc)
		{
			_con=con;
			this.data=data;
			this.returnType=returnType;
			this._changeFunc=changeFunc;
			
			if(data instanceof TriggerIntData)
			{
				mainLabel=addLabel(String.valueOf(((TriggerIntData)data).value),true,_constColor,this::onMainClick);
			}
			else if(data instanceof TriggerBooleanData)
			{
				mainLabel=addLabel(((TriggerBooleanData)data).value ? "true" : "false",true,_constColor,this::onMainClick);
			}
			else if(data instanceof TriggerFloatData)
			{
				mainLabel=addLabel(String.valueOf(((TriggerFloatData)data).value),true,_constColor,this::onMainClick);
			}
			else if(data instanceof TriggerLongData)
			{
				mainLabel=addLabel(String.valueOf(((TriggerLongData)data).value),true,_constColor,this::onMainClick);
			}
			else if(data instanceof TriggerStringData)
			{
				mainLabel=addLabel(String.valueOf(((TriggerStringData)data).value),true,_constColor,this::onMainClick);
			}
			else if(data instanceof TriggerFuncData)
			{
				TriggerFuncData fData=(TriggerFuncData)data;
				method=_con.mainUI.getMethodDic().get(fData.id);
				
				mainLabel=addLabel(method.getShowName(),!mainMethodFixed,_funcNameColor,mainMethodFixed ? null : this::onMainClick);
				
				if(method.args.length()>0)
				{
					addLabel("(",false,null);
					
					for(int i=0;i<method.args.size();i++)
					{
						if(i>0)
						{
							addLabel(",",false,null);
						}
						
						int fi=i;
						
						MethodArgInfo methodArgInfo=method.args.get(i);
						TriggerObjData aData=i<fData.args.length ? fData.args[i] : null;
						
						//list补充
						if(aData==null && methodArgInfo.type.equals("Runnable"))
						{
							TriggerFuncListData fListData=new TriggerFuncListData();
							fListData.funcList=new TriggerFuncData[0];
							aData=fListData;
						}
						
						if(aData!=null)
						{
							ObjInfo objInfo=objPool.getOne();
							objInfo.parent=this;
							children.add(objInfo);
							
							objInfo.init(_con,aData,method.args.get(i).type,v->
							{
								fData.args[fi]=v;
								_changeFunc.apply(fData);
							});
						}
						else
						{
							addLabel("?",true,_nullColor,()->
							{
								_con.showDialog(methodArgInfo.type,false,null,v->
								{
									fData.args[fi]=v;
									_changeFunc.apply(fData);
								});
							});
						}
					}
					
					addLabel(")",false,null);
				}
			}
			else if(data instanceof TriggerFuncListData)
			{
				TriggerFuncListData flData=(TriggerFuncListData)data;
				
				addLabel("()->",true,_listOperateColor,()->
				{
					_con.showDialog(_con._code.Void,isEventFunc,null,v->
					{
						flData.funcList=insertMethod(flData.funcList,(TriggerFuncData)v,0);
						
						_changeFunc.apply(flData);
					});
				});
				
				_con.leftBrace();
				
				for(int i=0;i<flData.funcList.length;i++)
				{
					if(i>0)
					{
						_con.nextLine();
					}
					
					int index=i;
					
					TriggerFuncData fData=flData.funcList[i];
					
					ObjInfo objInfo=objPool.getOne();
					objInfo.parent=this;
					objInfo.isEventFunc=isEventFunc;
					children.add(objInfo);
					objInfo.init(_con,fData,_con._code.Void,v->
					{
						flData.funcList[index]=(TriggerFuncData)v;
						
						_changeFunc.apply(flData);
					});
					
					objInfo.mainLabel.rightClick=e->
					{
						ListMethodPopupUI popup=new ListMethodPopupUI();
						popup.init(objInfo.mainLabel,e.getX(),e.getY(),()->
						{
							_con.showDialog(_con._code.Void,isEventFunc,null,v->
							{
								flData.funcList=insertMethod(flData.funcList,(TriggerFuncData)v,index+1);
								_changeFunc.apply(flData);
							});
						},()->
						{
							flData.funcList=removeMethod(flData.funcList,index);
							_changeFunc.apply(flData);
						});
					};
				}
				
				_con.rightBrace();
			}
		}
		
		//public void init(ObjectCall<TriggerObjData> changeFunc)
		//{
		//
		//}
		
		/** 插入新方法 */
		public TriggerFuncData[] insertMethod(TriggerFuncData[] list,TriggerFuncData data,int index)
		{
			TriggerFuncData[] re=new TriggerFuncData[list.length+1];
			
			for(int i=0;i<re.length;i++)
			{
				if(i<index)
				{
					re[i]=list[i];
				}
				else if(i==index)
				{
					re[i]=data;
				}
				else
				{
					re[i]=list[i-1];
				}
			}
			
			return re;
		}
		
		/** 删除方法 */
		public TriggerFuncData[] removeMethod(TriggerFuncData[] list,int index)
		{
			if(list.length==0)
				return list;
			
			TriggerFuncData[] re=new TriggerFuncData[list.length-1];
			
			for(int i=0;i<re.length;i++)
			{
				if(i<index)
				{
					re[i]=list[i];
				}
				else
				{
					re[i]=list[i+1];
				}
			}
			
			return re;
		}
		
		public void dispose()
		{
			allLabels.forEachAndClear(v->
			{
				v.dispose();
				labelPool.back(v);
			});
			
			parent=null;
			
			children.forEachAndClear(v->
			{
				v.dispose();
			});
			
			mainLabel=null;
			method=null;
			isEventFunc=false;
			
			//mainMethodFixed=false;
			
			data=null;
		}
		
		private FuncLabel addLabel(String content,boolean useHand,Runnable clickFunc)
		{
			return addLabel(content,useHand,Color.black,clickFunc,null);
		}
		
		private FuncLabel addLabel(String content,boolean useHand,Color color,Runnable clickFunc)
		{
			return addLabel(content,useHand,color,clickFunc,null);
		}
		
		private FuncLabel addLabel(String content,boolean useHand,Color color,Runnable clickFunc,ObjectCall<MouseEvent> rightClickFunc)
		{
			FuncLabel label=labelPool.getOne();
			label.setNeedHandle(useHand);
			
			String str=useHand ? "<html><u>"+content+"</u></html>" : content;
			label.setText(str);
			label.setForeground(color);
			
			int w=getCharMachineNum(content)* _charSize + _labelBoard;
			label.setLocation(_con._tx,_con._ty);
			label.setSize(w,_labelHeight);
			label.click=clickFunc;
			label.rightClick=rightClickFunc;
			
			_con.addX(w);
			
			_con._contentPanel.add(label);
			allLabels.add(label);
			
			label.setVisible(true);
			
			return label;
		}
		
		private void onMainClick()
		{
			_con.showDialog(returnType,isEventFunc,data,_changeFunc);
		}
	}
	
	/** 对象信息 */
	public static class ListMethodPopupUI
	{
		public JPopupMenu menu;
		
		public void init(Component target,int x,int y,Runnable appendFunc,Runnable deleteFunc)
		{
			menu=new JPopupMenu();
			
			JMenuItem mt1=new JMenuItem("向后添加");
			menu.add(mt1);
			
			JMenuItem mt2=new JMenuItem("删除");
			menu.add(mt2);
			
			mt1.addActionListener(v->
			{
				if(appendFunc!=null)
					appendFunc.run();
			});
			
			mt2.addActionListener(v->
			{
				if(deleteFunc!=null)
					deleteFunc.run();
			});
			
			menu.show(target,x,y);
		}
	}
}
