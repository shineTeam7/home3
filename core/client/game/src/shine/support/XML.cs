using System.Collections.Generic;
using System.Text;
using System.Xml;

namespace ShineEngine
{
	/// <summary>
	/// xml支持
	/// </summary>
	public class XML
	{
		private SMap<string,string> _propertys=new SMap<string,string>();

		private SList<XML> _children=new SList<XML>();

		private string _name;

		private string _value;

		public XML()
		{

		}

		/** 名字 */
		public string name
		{
			get {return _name;}
			set {_name=value;}
		}

		/** 值 */
		public string value
		{
			get {return _value;}
			set
			{
				if(value!=null && !StringUtils.isNoneContent(value))
					_value=value;
				else
					_value=null;
			}
		}

		/** 设置属性 */
		public void setProperty(string key,string value)
		{
			_propertys[key]=value;
		}

		/** 获取属性 */
		public string getProperty(string key)
		{
			return _propertys.getOrDefault(key,"");
		}

		/** 获取int属性 */
		public int getPropertyInt(string property)
		{
			string v=getProperty(property);

			if(v.isEmpty())
				return 0;

			return int.Parse(v);
		}

		/** 获取boolean属性 */
		public bool getPropertyBoolean(string property)
		{
			return StringUtils.strToBoolean(getProperty(property));
		}

		/** 全部属性 */
		public SMap<string,string> propertys()
		{
			return _propertys;
		}

		/** 添加子项 */
		public void appendChild(XML xml)
		{
			_children.add(xml);
		}

		/** 子项组 */
		public SList<XML> children()
		{
			return _children;
		}

		/** 获取名为name的子组 */
		public SList<XML> getChildrenByName(string name)
		{
			SList<XML> list=new SList<XML>();

			XML[] values=_children.getValues();
			XML v;

			for(int i=0,len=_children.size();i<len;++i)
			{
				if((v=values[i])._name.Equals(name))
				{
					list.add(v);
				}
			}

			return list;
		}

		/** 获取名为name的子组 */
		public XML getChildrenByNameOne(string name)
		{
			XML[] values=_children.getValues();
			XML v;

			for(int i=0,len=_children.size();i<len;++i)
			{
				if((v=values[i])._name.Equals(name))
				{
					return v;
				}
			}

			return null;
		}

		/** 获取名为name的子项并且名为property的属性值为value的一组子项 */
		public SList<XML> getChildrenByNameAndProperty(string name,string property,string value)
		{
			SList<XML> list=new SList<XML>();

			XML[] values=_children.getValues();
			XML v;

			for(int i=0,len=_children.size();i<len;++i)
			{
				if((v=values[i])._name.Equals(name) && v.getProperty(property).Equals(value))
				{
					list.add(v);
				}
			}

			return list;
		}

		/** 获取名为name的子项并且名为property的属性值为value的一个子项 */
		public XML getChildrenByNameAndPropertyOne(string name,string property,string value)
		{
			XML[] values=_children.getValues();
			XML v;

			for(int i=0,len=_children.size();i<len;++i)
			{
				if((v=values[i])._name.Equals(name) && v.getProperty(property).Equals(value))
				{
					return v;
				}
			}

			return null;
		}


		/** 删除子项 */
		public void removeChild(XML xml)
		{
			_children.removeObj(xml);
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

		/** 转成字符串 */
		override public string ToString()
		{
			StringBuilder sb=StringBuilderPool.create();
			buiildXMLString(sb,0);
			return StringBuilderPool.releaseStr(sb);
		}

		/** 递归构造字符串 */
		private void buiildXMLString(StringBuilder sb,int tabNum)
		{
			for(int i=0;i<tabNum;i++)
			{
				sb.Append("  ");
			}

			sb.Append('<');
			sb.Append(_name);

			string[] keys=_propertys.getKeys();
			string[] values=_propertys.getValues();
			string k;

			for(int i=keys.Length-1;i>=0;--i)
			{
				if((k=keys[i])!=null)
				{
					sb.Append(' ');
					sb.Append(k);
					sb.Append('=');
					sb.Append('"');
					sb.Append(values[i]);
					sb.Append('"');
				}
			}

			if(_children.isEmpty())
			{
				if (_value != null)
				{
					sb.Append(">");
					sb.Append(_value);
					sb.Append("</");
					sb.Append(_name);
					sb.Append(">\n");
				}
				else
				{
					sb.Append("/>\n");
				}
			}
			else
			{
				if (_value != null)
				{
					sb.Append(">");
					sb.Append(_value);
					sb.Append("\n");
				}
				else
				{
					sb.Append(">\n");
				}

				XML[] values1=_children.getValues();

				for(int i=0,len=_children.size();i<len;++i)
				{
					values1[i].buiildXMLString(sb,tabNum + 1);

				}

				for(int i=0;i<tabNum;i++)
				{
					sb.Append("  ");
				}

				sb.Append("</");
				sb.Append(_name);
				sb.Append(">\n");
			}
		}

		/** 通过文字读取xml */
		public static XML readXMLByString(string str)
		{
			str=str.Trim();

			//System.IO.StringReader stringReader = new System.IO.StringReader(xmlFile);
			//stringReader.Read(); // 跳过 BOM 
			//System.Xml.XmlReader reader = System.Xml.XmlReader.Create(stringReader);
			//xmlDocument.LoadXml(stringReader.ReadToEnd());
			//return true; 

			XmlDocument document=new XmlDocument();
			document.LoadXml(str);

			return readXMLByDocument(document);
		}

		/** 通过Document读取XML */
		public static XML readXMLByDocument(XmlDocument document)
		{
			//只取第一节点
			return makeXML(document.FirstChild);
		}

		/** 递归构造XML */
		private static XML makeXML(XmlNode node)
		{
			XML xml=new XML();

			xml.name=node.Name;
			xml.value=node.Value;

			int i=0;

			XmlAttributeCollection xac=node.Attributes;

			if(xac!=null)
			{
				for(i=0;i<xac.Count;i++)
				{
					xml.setProperty(xac[i].Name,xac[i].Value);
				}
			}

			XmlNodeList nodeList=node.ChildNodes;

			for(i=0;i<nodeList.Count;i++)
			{
				if(nodeList[i].NodeType==XmlNodeType.Element)
				{
					xml.appendChild(makeXML(nodeList[i]));
				}
			}

			return xml;
		}
	}
}