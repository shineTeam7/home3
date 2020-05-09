package com.home.shineTool.tool.data;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.collection.StringIntMap;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.global.ShineToolSetting;


/** 数据定义类(类型枚举类) */
public class DataDefineTool
{
	/** 路径 */
	private String _path;
	/** 偏移 */
	private int _offSet=-1;
	
	private int _len;
	/** 是否主构造 */
	private boolean _isMain;
	
	/** 类信息 */
	private ClassInfo _cls;
	
	/** 主计数 */
	private FieldInfo _mainCount;
	
	private int _countValue;
	
	private IntList _usedList=new IntList();
	
	private int _nextIndex;
	
	public DataDefineTool(String path,int offSet,int len,boolean isMain)
	{
		_path=path;
		_offSet=offSet;
		_len=len;
		_isMain=isMain;
		
		if(ShineToolSetting.isAllRefresh)
		{
			_cls=ClassInfo.getVoidClassInfoFromPath(_path);
		}
		else
		{
			_cls=ClassInfo.getClassInfoFromPathAbs(_path);
		}
		
		FieldInfo off=_cls.getField("off");
		
		if(off==null)
		{
			off=new FieldInfo();
			off.name="off";
			off.describe="起始";
			off.isConst=true;
			off.isStatic=true;
			off.visitType=VisitType.Public;
			off.type=_cls.getCode().Int;
			off.defaultValue=String.valueOf(_offSet);
			
			_cls.addField(off);
		}
		else
		{
			off.defaultValue=String.valueOf(_offSet);
		}
		
		_mainCount=_cls.getField("count");
		
		if(_mainCount==null)
		{
			_mainCount=new FieldInfo();
			_mainCount.name="count";
			_mainCount.describe="计数";
			_mainCount.isConst=true;
			_mainCount.isStatic=true;
			_mainCount.visitType=VisitType.Public;
			_mainCount.type=_cls.getCode().Int;
			_mainCount.defaultValue=String.valueOf(_offSet);
			
			_cls.addField(_mainCount);
		}
		
		_countValue=Integer.parseInt(_mainCount.defaultValue);
		
		if(_isMain)
		{
			if(ShineToolSetting.isDataDefineReUsed)
			{
				for(String k:_cls.getFieldNameList())
				{
					if(!k.equals("off") && !k.equals("count"))
					{
						FieldInfo field=_cls.getField(k);
						
						int v=Integer.parseInt(field.defaultValue);
						
						int index=v - _offSet;
						
						if(index>=_usedList.size())
						{
							_usedList.setLength(index+1);
						}
						
						_usedList.set(index,1);
					}
				}
				
				_nextIndex=_offSet;
				
				if(!_usedList.isEmpty())
				{
					int re=-1;
					
					int[] values=_usedList.getValues();
					
					for(int i=0,len2=_usedList.size();i<len2;i++)
					{
						if(values[i]==0)
						{
							re=i+_offSet;
							break;
						}
					}
					
					if(re!=-1)
					{
						_nextIndex=re;
					}
					else
					{
						_nextIndex=_usedList.size()+_offSet;
					}
				}
			}
		}
	}
	
	/** 偏移 */
	public int getOffSet()
	{
		return _offSet;
	}
	
	/** 是否主构造 */
	public boolean isMain()
	{
		return _isMain;
	}
	
	/** 获取类 */
	public ClassInfo getCls()
	{
		return _cls;
	}
	
	/** 添加一个类的存在(cName:属性名)(返回对应的序号) */
	public int addOne(String cName,String des,int num)
	{
		//if(ShineToolSetting.isAll)
		//{
		//	if(_recordDic.contains(cName))
		//	{
		//		Ctrl.throwError("类名重复:",cName);
		//		return -1;
		//	}
		//
		//	_recordDic.add(cName);
		//}
		
		FieldInfo ff=_cls.getField(cName);
		
		if(ff!=null)
		{
			if(!des.isEmpty())
			{
				ff.describe=des;
			}
			
			if(_countValue<num+1)
			{
				_countValue=num+1;
				_mainCount.defaultValue=String.valueOf(_countValue);
			}
			
			//主就读
			if(_isMain)
			{
				return Integer.parseInt(ff.defaultValue);
			}
			else
			{
				ff.defaultValue=String.valueOf(num);
				return num;
			}
		}
		
		//主就读
		if(_isMain)
		{
			if(ShineToolSetting.isDataDefineReUsed)
			{
				num=_nextIndex;
				addUse(num);
			}
			else
			{
				num=_countValue;
			}
		}
		
		if(_countValue<num+1)
		{
			_countValue=num+1;
			_mainCount.defaultValue=String.valueOf(_countValue);
		}
		
		ff=new FieldInfo();
		ff.name=cName;
		ff.describe=des;
		ff.visitType=VisitType.Public;
		ff.isStatic=true;
		ff.isConst=true;
		ff.type=_cls.getCode().Int;
		ff.defaultValue=String.valueOf(num);
		
		int index;
		
		if(ShineToolSetting.isDataDefineReUsed)
		{
			index=num-_offSet+2;
		}
		else
		{
			index=_cls.getFieldNameList().size();
		}
		
		_cls.addField(ff,index);
		
		return num;
	}
	
	private void addUse(int num)
	{
		if(num<_nextIndex)
		{
			Ctrl.errorLog("不应该出现比 nextIndex小的 序号");
			return;
		}
		
		//末尾填充
		if(_nextIndex==_offSet+_usedList.size())
		{
			_nextIndex++;
			_usedList.add(1);
		}
		else
		{
			int re=-1;
			
			for(int i=num-_offSet+1,len=_usedList.size();i<len;i++)
			{
				if(_usedList.get(i)==0)
				{
					re=i+_offSet;
					break;
				}
			}
			
			if(re!=-1)
			{
				_nextIndex=re;
			}
			else
			{
				_nextIndex=_offSet+_usedList.size();
			}
		}
	}
	
	private void removeUse(int num)
	{
		_usedList.set(num-_offSet,0);
		
		if(num>=_nextIndex)
			return;
		
		_nextIndex=num;
	}
	
	/** 删除一个(cName) */
	public void removeOne(String cName)
	{
		FieldInfo ff=_cls.getField(cName);
		
		if(ff!=null)
		{
			_cls.removeField(cName);
			
			if(_isMain)
			{
				removeUse(Integer.parseInt(ff.defaultValue));
			}
		}
	}
	
	/** 写入文件 */
	public void write()
	{
		if(_isMain)
		{
			//1个的不统计
			if(_len>1)
			{
				int num=Integer.parseInt(_mainCount.defaultValue) - _offSet;
				
				if(num >= _len)
				{
					Ctrl.throwError(_cls.clsName+"超出范围!");
				}
				
				if(ShineToolSetting.needTrace || num >= (_len/2))
				{
					Ctrl.print(_cls.clsName + "当前使用率:" + num * 100 / _len);
				}
			}
		}
		
		_cls.writeToPath(_path);
	}
	
	/** 从cls上，获取定义组 */
	public static void getDefineDicFromCls(StringIntMap dic,ClassInfo cls,boolean UorL)
	{
		for(String s : cls.getFieldNameList())
		{
			FieldInfo field=cls.getField(s);
			
			if(!field.name.equals("off") && !field.name.equals("count"))
			{
				String fName=UorL ? field.name : StringUtils.lcWord(field.name);
				
				dic.put(fName,Integer.parseInt(field.defaultValue));
			}
		}
	}
}
