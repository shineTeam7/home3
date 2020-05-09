using System;
using ShineEngine;

/// <summary>
/// 版本记录数据(公共部分)
/// </summary>
public class VersionRecordData
{
	/** 当前app版本 */
	public int appVersion;
	/** 最低App版本 */
	public int leastAppVersion;
	/** 资源版本 */
	public int resourceVersion;
	/** 最低资源版本 */
	public int leastResourceVersion;
	/** 显示版本 */
	public string version;
	/** 是否正式发包(为了让手机本地调试打包时不必再清空缓存) */
	public bool isRelease;

	public void readXML(XML xml)
	{
		appVersion=int.Parse(xml.getChildrenByName("appVersion")[0].getProperty("value"));
		leastAppVersion=int.Parse(xml.getChildrenByName("leastAppVersion")[0].getProperty("value"));
		resourceVersion=int.Parse(xml.getChildrenByName("resourceVersion")[0].getProperty("value"));
		leastResourceVersion=int.Parse(xml.getChildrenByName("leastResourceVersion")[0].getProperty("value"));
		version=xml.getChildrenByName("version")[0].getProperty("value");
		isRelease=bool.Parse(xml.getChildrenByName("isRelease")[0].getProperty("value"));
	}

	public XML writeXML()
	{
		XML re=new XML();
		re.name="version";

		XML xl;

		xl=new XML();
		xl.name="appVersion";
		xl.setProperty("value",appVersion.ToString());
		re.appendChild(xl);

		xl=new XML();
		xl.name="leastAppVersion";
		xl.setProperty("value",leastAppVersion.ToString());
		re.appendChild(xl);

		xl=new XML();
		xl.name="resourceVersion";
		xl.setProperty("value",resourceVersion.ToString());
		re.appendChild(xl);

		xl=new XML();
		xl.name="leastResourceVersion";
		xl.setProperty("value",leastResourceVersion.ToString());
		re.appendChild(xl);

		xl=new XML();
		xl.name="version";
		xl.setProperty("value",version);
		re.appendChild(xl);

		xl=new XML();
		xl.name="isRelease";
		xl.setProperty("value",isRelease.ToString());
		re.appendChild(xl);

		return re;
	}
}