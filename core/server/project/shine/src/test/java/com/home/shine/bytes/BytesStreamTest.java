package com.home.shine.bytes;

import com.home.shine.ShineSetup;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DIntData;
//import mockit.Mock;
//import mockit.MockUp;
//import mockit.integration.junit5.JMockitExtension;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** 字节流测试 */
public class BytesStreamTest
{
	@Before
	public void init()
	{
		ShineSetup.setupForUnitTest();
	}
	
	@Test
	public void test()
	{
		BytesWriteStream ws=BytesWriteStream.create();
		ws.writeByte(1);
		ws.writeByte(127);
		ws.writeShort(12312);
		ws.writeUTF("abc");
		ws.compress();

		BytesReadStream rs=BytesReadStream.create(ws.getByteArray());
		rs.unCompress();
		Assert.assertEquals(1,rs.readByte());
		Assert.assertEquals(127,rs.readByte());
		Assert.assertEquals(12312,rs.readShort());
		Assert.assertEquals("abc",rs.readUTF());
	}
	
	@Test
	public void testMock()
	{
//		DIntData aa=new DIntData();
//
//		new MockUp<DIntData>(){
//			@Mock
//			public String getDataClassName()
//			{
//				return "AAA";
//			};
//		};
//
//		String dataClassName=aa.getDataClassName();
//
//		Ctrl.print(dataClassName);
	}
}