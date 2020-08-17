package com.home.shineTool.tool.trigger.ui;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.func.IntCall;
import com.home.shine.support.func.ObjectCall;

import javax.swing.*;

/** trigger添加 对话框 */
public class TriggerAddDialogUI
{
	public TriggerMainUI mainUI;
	
	private JDialog _dialog;
	
	public void showInt(String msg,IntCall func)
	{
		init(msg,v->
		{
			int re=0;
			
			try
			{
				re=Integer.parseInt(v);
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(_dialog,"必须为Int: "+v);
				return;
			}
			
			this._dialog.setVisible(false);
			this._dialog.dispose();
			
			func.call(re);
		});
	}
	
	public void showString(String msg,ObjectCall<String> func)
	{
		init(msg,v->
		{
			if(v.isEmpty())
			{
				JOptionPane.showMessageDialog(_dialog,"不能为空: "+v);
				return;
			}
			
			this._dialog.setVisible(false);
			this._dialog.dispose();
			
			func.apply(v);
		});
	}
	
	private void init(String msg,ObjectCall<String> func)
	{
		_dialog=new JDialog(mainUI.getFrame());
		_dialog.setModal(true);
		_dialog.setLayout(null);
		_dialog.setBounds(0,0,400,300);
		
		JLabel title=new JLabel();
		title.setBounds(200,5,200,20);
		title.setText(msg);
		_dialog.add(title);
		
		JLabel label=new JLabel();
		label.setBounds(30,100,50,20);
		label.setText("输入:");
		_dialog.add(label);
		
		JTextField textField=new JTextField();
		textField.setBounds(100,100,200,30);
		
		_dialog.add(textField);
		
		JButton button=new JButton();
		button.setMargin(TriggerMainUI.insets);
		button.setBounds(150,200,50,20);
		button.setText("确定");
		button.addActionListener(v->
		{
			func.apply(textField.getText());
		});
		
		_dialog.add(button);
		
		_dialog.setLocationRelativeTo(mainUI.getFrame());
		_dialog.setVisible(true);
	}
}
