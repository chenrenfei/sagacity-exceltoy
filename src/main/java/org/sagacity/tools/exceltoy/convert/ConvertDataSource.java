/**
 * 
 */
package org.sagacity.tools.exceltoy.convert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @project sagacity-tools
 * @description 用于给转换器提供数据的对象，数据交互中心，convert直接从中获取数据
 * @author chenrenfei $<a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ConvertDataSource.java,Revision:v1.0,Date:2010-6-9
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConvertDataSource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3868593350078481042L;

	/**
	 * excel当前行数据
	 */
	private static List excelRowData;

	/**
	 * 单个excel文件所有数据
	 */
	private static List excelRowsData;

	/**
	 * 转换后的数据
	 */
	private static List convertedRowsData = new ArrayList();

	/**
	 * 主表计算后的单行数据
	 */
	private static Object[][] mainTableRowData;

	/**
	 * excel文件标题列对应数据列
	 */
	private static HashMap excelTitleMapDataIndex;

	/**
	 * excel用户理解的列对应实际数据列，如excel前2列为空， 指定时从第3列开始，excel读取后第3列实际对应数据的第0列
	 */
	private static HashMap excelColMapDataIndex;

	/**
	 * @return the excelRowData
	 */
	public static List getExcelRowData() {
		return excelRowData;
	}

	/**
	 * @param excelRowData
	 *            the excelRowData to set
	 */
	public static void setExcelRowData(List excelRowData) {
		ConvertDataSource.excelRowData = excelRowData;
	}

	/**
	 * @return the excelRowsData
	 */
	public static List getExcelRowsData() {
		return excelRowsData;
	}

	/**
	 * @param excelRowsData
	 *            the excelRowsData to set
	 */
	public static void setExcelRowsData(List excelRowsData) {
		ConvertDataSource.excelRowsData = excelRowsData;
	}

	/**
	 * @return the mainTableRowData
	 */
	public static Object[][] getMainTableRowData() {
		return mainTableRowData;
	}

	/**
	 * @param mainTableRowData
	 *            the mainTableRowData to set
	 */
	public static void setMainTableRowData(Object[][] mainTableRowData) {
		ConvertDataSource.mainTableRowData = mainTableRowData;
		// 插入主表转换后的数据，用于treeConvert
		if (convertedRowsData == null) {
			convertedRowsData = new ArrayList();
		}
		convertedRowsData.add(mainTableRowData);
	}

	/**
	 * @return the excelTitleMapDataIndex
	 */
	public static HashMap getExcelTitleMapDataIndex() {
		return excelTitleMapDataIndex;
	}

	/**
	 * @param excelTitleMapDataIndex
	 *            the excelTitleMapDataIndex to set
	 */
	public static void setExcelTitleMapDataIndex(HashMap excelTitleMapDataIndex) {
		ConvertDataSource.excelTitleMapDataIndex = excelTitleMapDataIndex;
	}

	/**
	 * @return the excelColMapDataIndex
	 */
	public static HashMap getExcelColMapDataIndex() {
		return excelColMapDataIndex;
	}

	/**
	 * @param excelColMapDataIndex
	 *            the excelColMapDataIndex to set
	 */
	public static void setExcelColMapDataIndex(HashMap excelColMapDataIndex) {
		ConvertDataSource.excelColMapDataIndex = excelColMapDataIndex;
	}

	/**
	 * @return the convertedRowsData
	 */
	public static List getConvertedRowsData() {
		return convertedRowsData;
	}

	/**
	 * @param convertedRowsData
	 *            the convertedRowsData to set
	 */
	public static void setConvertedRowsData(List convertedRowsData) {
		ConvertDataSource.convertedRowsData = convertedRowsData;
	}
}
