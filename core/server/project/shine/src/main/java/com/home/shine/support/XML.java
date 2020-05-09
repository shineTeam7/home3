package com.home.shine.support;

import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** XML */
public class XML
{
	/** 属性组 */
	private SMap<String,String> _propertys=new SMap<>();
	
	/** 子组 */
	private SList<XML> _children=new SList<>(XML[]::new);
	
	/** 名字 */
	private String _name;
	
	/** 值 */
	private String _value;
	
	/** XML */
	public XML()
	{
	
	}
	
	/** 设置名字 */
	public void setName(String name)
	{
		_name=name;
	}
	
	/** 获取名字 */
	public String getName()
	{
		return _name;
	}
	
	/** 设置值 */
	public void setValue(String value)
	{
		if(value!=null && !StringUtils.isNoneContent(value))
			_value=value;
		else
			_value=null;
	}
	
	/** 获取值 */
	public String getValue()
	{
		return _value;
	}
	
	/** 设置属性 */
	public void setProperty(String property,String value)
	{
		_propertys.put(property,value);
	}
	
	/** 获取属性 */
	public String getProperty(String property)
	{
		String re=_propertys.get(property);
		
		if(re==null)
		{
			return "";
		}
		
		return re;
	}
	
	/** 获取int属性 */
	public int getPropertyInt(String property)
	{
		String v=getProperty(property);
		
		if(v.isEmpty())
			return 0;
		
		return Integer.parseInt(v);
	}
	
	/** 获取boolean属性 */
	public boolean getPropertyBoolean(String property)
	{
		return StringUtils.strToBoolean(getProperty(property));
	}
	
	/** 获取全部属性 */
	public SMap<String,String> propertys()
	{
		return _propertys;
	}
	
	/** 添加子项 */
	public void appendChild(XML xml)
	{
		_children.add(xml);
	}
	
	/** 获取全部子项 */
	public SList<XML> children()
	{
		return _children;
	}
	
	/** 获取名为name的子组 */
	public SList<XML> getChildrenByName(String name)
	{
		SList<XML> list=new SList<>(XML[]::new);
		
		XML[] values=_children.getValues();
		XML v;
		
		for(int i=0,len=_children.size();i<len;++i)
		{
			if((v=values[i])._name.equals(name))
			{
				list.add(v);
			}
		}
		
		return list;
	}
	
	/** 获取名为name的子组 */
	public XML getChildrenByNameOne(String name)
	{
		XML[] values=_children.getValues();
		XML v;
		
		for(int i=0,len=_children.size();i<len;++i)
		{
			if((v=values[i])._name.equals(name))
			{
				return v;
			}
		}
		
		return null;
	}
	
	/** 获取名为name的子项并且名为property的属性值为value的一组子项 */
	public SList<XML> getChildrenByNameAndProperty(String name,String property,String value)
	{
		SList<XML> list=new SList<>(XML[]::new);
		
		XML[] values=_children.getValues();
		XML v;
		
		for(int i=0,len=_children.size();i<len;++i)
		{
			if((v=values[i])._name.equals(name) && v.getProperty(property).equals(value))
			{
				list.add(v);
			}
		}
		
		return list;
	}
	
	/** 获取名为name的子项并且名为property的属性值为value的一个子项 */
	public XML getChildrenByNameAndPropertyOne(String name,String property,String value)
	{
		XML[] values=_children.getValues();
		XML v;
		
		for(int i=0,len=_children.size();i<len;++i)
		{
			if((v=values[i])._name.equals(name) && v.getProperty(property).equals(value))
			{
				return v;
			}
		}
		
		return null;
	}
	
	/** 删除子 */
	public void removeChild(XML xml)
	{
		int index=_children.indexOf(xml);
		
		if(index!=-1)
		{
			_children.remove(index);
		}
	}

	/** 替换子节点 */
	public void replaceChild(XML oldXml, XML newXml)
	{
		int index=_children.indexOf(oldXml);

		if(index!=-1)
		{
			_children.set(index, newXml);
		}
	}
	
	/** 获取子组 */
	public SList<XML> getChildren()
	{
		return _children;
	}
	
	/** 删除全部子节点 */
	public void removeAllChildren()
	{
		_children.clear();
	}
	
	/** 转String */
	@Override
	public String toString()
	{
		StringBuilder sb=StringBuilderPool.create();
		
		buildXMLString(sb,0);
		
		return StringBuilderPool.releaseStr(sb);
	}
	
	protected void buildXMLString(StringBuilder sb,int tabNum)
	{
		for(int i=0;i<tabNum;i++)
		{
			sb.append("  ");
		}
		
		sb.append('<');
		sb.append(_name);
		
		Object[] table=_propertys.getTable();
		Object key;
		
		for(int i=table.length - 2;i >= 0;i-=2)
		{
			if((key=table[i])!=null)
			{
				sb.append(' ');
				sb.append((String)key);
				sb.append('=');
				sb.append('"');
				sb.append((String)table[i + 1]);
				sb.append('"');
			}
		}
		
		if(_children.isEmpty())
		{
			if (_value != null)
			{
				sb.append(">");
				sb.append(_value);
				sb.append("</");
				sb.append(_name);
				sb.append(">\n");
			}
			else
			{
				sb.append("/>\n");
			}
		}
		else
		{
			if (_value != null)
			{
				sb.append(">");
				sb.append(_value);
				sb.append("\n");
			}
			else
			{
				sb.append(">\n");
			}
			
			XML[] values=_children.getValues();
			
			for(int i=0,len=_children.size();i<len;++i)
			{
				values[i].buildXMLString(sb,tabNum + 1);
			}
			
			for(int i=0;i<tabNum;i++)
			{
				sb.append("  ");
			}
			
			sb.append("</");
			sb.append(_name);
			sb.append(">\n");
		}
	}
	
	/** Document转XML */
	public static XML readXMLByDocument(Document doc)
	{
		Element element=doc.getDocumentElement();
		
		XML xml=makeXML(element);
		
		return xml;
	}
	
	/** 用对应的节点初始化XML,递归实现 */
	private static XML makeXML(Node node)
	{
		XML xml=new XML();
		xml.setName(node.getNodeName());
		xml.setValue(node.getNodeValue());

		int i=0;
		
		NamedNodeMap namedNodeMap=node.getAttributes();
		
		if(namedNodeMap!=null)
		{
			for(i=0;i<namedNodeMap.getLength();i++)
			{
				xml.setProperty(namedNodeMap.item(i).getNodeName(),namedNodeMap.item(i).getNodeValue());
			}
		}
		
		NodeList nodes=node.getChildNodes();
		
		for(i=0;i<nodes.getLength();i++)
		{
			if(nodes.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				xml.appendChild(makeXML(nodes.item(i)));
			}
			else if (nodes.item(i).getNodeType() == Node.TEXT_NODE)
			{
				xml.setValue(nodes.item(i).getNodeValue());
			}
		}
		
		return xml;
	}
}
