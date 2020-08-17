package com.home.shineTool.utils;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.utils.MathUtils;
import com.home.shineTool.global.ShineToolSetting;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

/** 工具文件方法 */
public class ToolFileUtils
{
	/** 从文件中读取excel值组(只读sheet1)(将不同类型的都转为String) */
	public static String[][] readFileForExcelFirstSheet(String path)
	{
		String[][][] re=readFileForExcel(path,1);
		
		return re!=null ? re[0] : null;
	}
	
	/** 从文件中读取excel值组(全部sheet)(将不同类型的都转为String) */
	public static String[][][] readFileForExcel(String path)
	{
		return readFileForExcel(path,-1);
	}
	
	/** 从文件中读取excel值组(全部sheet)(将不同类型的都转为String) */
	public static String[][][] readFileForExcel(String path,int sheetNum)
	{
		XSSFWorkbook workbook=null;
		
		try
		{
			workbook=new XSSFWorkbook(new FileInputStream(new File(path)));
			
			int sheetReadNum=workbook.getNumberOfSheets();
			
			if(sheetReadNum==0)
			{
				workbook.close();
				return null;
			}
			
			if(sheetNum<=0)
				sheetNum=sheetReadNum;
			
			String[][][] tRe=new String[sheetNum][][];
			
			int len=Math.min(sheetReadNum,sheetNum);
			
			for(int s=0;s<len;s++)
			{
				XSSFSheet sheet=workbook.getSheetAt(s);
				
				//行数
				int rowNum=sheet.getLastRowNum();
				
				if(rowNum<0)
				{
					workbook.close();
					return null;
				}
				
				++rowNum;
				
				//列数
				int columnNum=0;
				
				XSSFRow row;
				int tempN;
				
				for(int i=0;i<rowNum;++i)
				{
					row=sheet.getRow(i);
					
					if(row!=null)
					{
						tempN=row.getLastCellNum();
					}
					else
					{
						tempN=0;
					}
					
					if(tempN>columnNum)
					{
						columnNum=tempN;
					}
				}
				
				if(columnNum==0)
				{
					continue;
				}
				
				
				String[][] re=new String[rowNum][columnNum];
				
				String[] temp;
				
				for(int i=0;i<rowNum;++i)
				{
					temp=re[i];
					
					row=sheet.getRow(i);
					
					if(row!=null)
					{
						for(int j=0;j<columnNum;++j)
						{
							XSSFCell cell=row.getCell(j);
							
							//空的
							if(cell==null)
							{
								temp[j]="";
								continue;
							}
							
							temp[j]=getValueFromCell(cell);
						}
					}
					else
					{
						for(int j=0;j<columnNum;++j)
						{
							temp[j]="";
						}
					}
				}
				
				tRe[s]=re;
			}
			
			workbook.close();
			
			return tRe;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static String getValueFromCell(XSSFCell cell)
	{
		return getValueFromCell(cell,cell.getCellTypeEnum());
	}
	
	private static String getValueFromCell(XSSFCell cell,CellType type)
	{
		String re;
		
		switch(type)
		{
			case _NONE:
			case BLANK:
			{
				re="";
			}
				break;
			case NUMERIC:
			{
				double dv=cell.getNumericCellValue();
				
				long iv=Math.round(dv);
				
				if(MathUtils.doubleEquals(iv,dv))
				{
					re=String.valueOf(iv);
				}
				else
				{
					re=String.valueOf(dv);
				}
			}
				break;
			case STRING:
			{
				re=cell.getStringCellValue();
			}
				break;
			case FORMULA:
			{
				CellType tt=cell.getCachedFormulaResultTypeEnum();
				
				if(tt==CellType.FORMULA)
				{
					Ctrl.throwError("formula不能嵌套了");
					re="";
				}
				
				re=getValueFromCell(cell,tt);
			}
				break;
			case BOOLEAN:
			{
				re=cell.getBooleanCellValue() ? "1" : "";
			}
				break;
			case ERROR:
			{
				re="";
			}
				break;
			default:
			{
				re="";
			}
				break;
		}
		
		return re;
	}
	
	/** 获取java工程路径 */
	public static String getJavaCodePath(String project)
	{
		return project+ShineToolSetting.javaCodeFront+project;
	}
	
	public static String getDBNameByURL(String url)
	{
		String last=url.substring(url.lastIndexOf("/") + 1,url.length());
		
		String aa=last.substring(0,last.indexOf(","));
		
		int wIndex;
		
		if((wIndex=aa.indexOf("?"))!=-1)
		{
			aa=aa.substring(0,wIndex);
		}
		
		return aa.toLowerCase();//全小写
	}
}
