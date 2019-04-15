/**
 * 
 */
package org.sagacity.tools.exceltoy.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.convert.ConvertDataSource;
import org.sagacity.tools.exceltoy.model.EqlParseResult;

/**
 * @project sagacity-tools
 * @description excel 执行指令解析工具
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:EQLUtil.java,Revision:v1.0,Date:2009-12-30
 */
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class EQLUtil {
	/**
	 * 定义日志
	 */
	private final static Logger logger = LogManager.getLogger(EQLUtil.class);

	private static HashMap templateFieldsMap = new HashMap();

	private static final HashMap symMarkFilters = new HashMap() {
		{
			put("(", ")");
			put("'", "'");
			put("\"", "\"");
			put("[", "]");
			put("{", "}");
		}
	};

	/**
	 * @todo <b>
	 *       解析eql语句,EQL语句模式：(${excelTitle1},${excelTitle2},@marcXX(${excelTitle3
	 *       })) into Table (FIELD1,FIELD2,FIELD3)</b>
	 * @param eql
	 * @return
	 * @throws Exception
	 */
	public static EqlParseResult parseEql(String eql) throws Exception {
		if (StringUtil.isBlank(eql))
			return null;
		EqlParseResult result = new EqlParseResult();
		// 剔除回车、换行、tab
		eql = StringUtil.clearMistyChars(eql, " ");
		int intoIndex = StringUtil.indexOfIgnoreCase(eql, "into", StringUtil.getSymMarkIndex("(", ")", eql, 0));

		// 组织实际发生的sql语句
		StringBuilder insertSql = null;
		if (ExcelToyConstants.insertIgnore())
			insertSql = new StringBuilder("insert ignore ").append(eql.substring(intoIndex)).append(" values (");
		else
			insertSql = new StringBuilder("insert ").append(eql.substring(intoIndex)).append(" values (");
		// 排除eql中的宏内逗号
		String preSql = eql.substring(0, intoIndex);
		preSql = preSql.substring(preSql.indexOf("(") + 1, preSql.lastIndexOf(")"));
		// 需要插入表相应字段的相关excel列参数
		String[] insertParams = StringUtil.splitExcludeSymMark(preSql, ",", symMarkFilters);
		// 将数据trim
		for (int i = 0; i < insertParams.length - 1; i++)
			insertParams[i] = insertParams[i].trim();
		// 表名
		String table = eql.substring(intoIndex + 5, eql.indexOf("(", intoIndex)).trim();
		// 提取表字段
		String[] tableFields = eql
				.substring(eql.indexOf("(", intoIndex) + 1, StringUtil.getSymMarkIndex("(", ")", eql, intoIndex))
				.split(",");
		for (int i = 0; i < tableFields.length; i++) {
			tableFields[i] = tableFields[i].trim();
		}
		for (int i = 0; i < insertParams.length - 1; i++)
			insertSql.append("?,");
		insertSql.append("?)");

		List<String> excelFileds = new ArrayList();
		String excelFiled;
		for (int i = 0; i < insertParams.length; i++) {
			excelFiled = insertParams[i];
			String[] tempFields = parseExcelFields(excelFiled);
			for (int j = 0; j < tempFields.length; j++) {
				if (!excelFileds.contains(tempFields[j]))
					excelFileds.add(tempFields[j].trim());
			}
		}
		if (insertParams.length != tableFields.length) {
			logger.error("请检查EQL配置,Excel列跟数据库表列不相符!");
			return null;
		}
		result.setExcelCols((String[]) excelFileds.toArray(new String[excelFileds.size()]));
		result.setFields(tableFields);
		result.setParams(insertParams);
		result.setTableName(table);
		result.setInsertSql(insertSql.toString());
		return result;
	}

	/**
	 * @todo 获取EQL语句中对应的表名
	 * @param eql
	 * @return
	 */
	public static String getTableName(String eql) {
		eql = StringUtil.clearMistyChars(eql, " ");
		int intoIndex = StringUtil.indexOfIgnoreCase(eql, "into", StringUtil.getSymMarkIndex("(", ")", eql, 0));
		// 表名
		String tableName = eql.substring(intoIndex + 5, eql.indexOf("(", intoIndex));
		return tableName;
	}

	/**
	 * @todo 获取模板中的excel title字段信息
	 * @param template
	 * @return
	 */
	public static String[] parseExcelFields(final String template) {
		if (templateFieldsMap.get(template) == null) {
			String tmp = template;
			List fields = new ArrayList();
			int beginIndex = template.indexOf(ExcelToyConstants.EXCEL_TITLE_BEGIN_MARK);
			int symIndex = StringUtil.getSymMarkIndex(ExcelToyConstants.EXCEL_TITLE_BEGIN_MARK,
					ExcelToyConstants.EXCEL_TITLE_END_MARK, tmp, 0);
			String field;
			while (beginIndex != -1) {
				field = tmp.substring(beginIndex + 2, symIndex);
				tmp = tmp.substring(symIndex + 1);
				beginIndex = tmp.indexOf(ExcelToyConstants.EXCEL_TITLE_BEGIN_MARK);
				symIndex = StringUtil.getSymMarkIndex(ExcelToyConstants.EXCEL_TITLE_BEGIN_MARK,
						ExcelToyConstants.EXCEL_TITLE_END_MARK, tmp, 0);
				if (!fields.contains(field))
					fields.add(field);
			}
			String[] result = (String[]) fields.toArray(new String[fields.size()]);
			templateFieldsMap.put(template, result);
		}
		return (String[]) templateFieldsMap.get(template);
	}

	/**
	 * @todo 以excel数据替换excel标题占位符号
	 * @param template
	 * @param pkCols
	 * @return
	 */
	public static String replaceHolder(String template, String[] pkCols) throws Exception {
		if (pkCols == null || pkCols.length == 0)
			return template;
		String result = template;
		int index;
		String replace;
		for (int i = 0; i < pkCols.length; i++) {
			index = getExcelFieldMapIndex(pkCols[i]);
			// 换用StringUtil替换解决result.replaceAll("\\$\\{\\}","$"),替换内容中存在"$"符合报错问题
			if (index != -1) {
				if (ConvertDataSource.getExcelRowData().size() < index + 1
						|| ConvertDataSource.getExcelRowData().get(index) == null)
					replace = "";
				else
					replace = ConvertDataSource.getExcelRowData().get(index).toString();
				result = StringUtil.replaceAllStr(result, ExcelToyConstants.EXCEL_TITLE_BEGIN_MARK.concat(pkCols[i])
						.concat(ExcelToyConstants.EXCEL_TITLE_END_MARK), replace);
			} else {
				logger.error("标题名:[{}]称跟excel标题匹配不成功,请检查命名!", pkCols[i]);
				throw new Exception("标题名称:[" + pkCols[i] + "]跟excel标题匹配不成功,请检查命名!");
			}
		}
		return result;
	}

	/**
	 * @todo 获取excel标题对应excel数据的列
	 * @param field
	 * @return
	 */
	public static int getExcelFieldMapIndex(String field) {
		Object index;
		index = ConvertDataSource.getExcelTitleMapDataIndex().get(field);
		if (index == null)
			index = ConvertDataSource.getExcelColMapDataIndex().get(field);
		if (index == null)
			return -1;
		else
			return (Integer) index;
	}

	/**
	 * 
	 * @todo 将excel标题跟具体解析出的excel数据列对应成hashMap,根据map.get("title")获取到对应excel的列
	 * @param titles
	 * @param excelBeginCol
	 * @param excelEndCol
	 * @return
	 */
	public static HashMap mappingExcelTitleAndDataIndex(List excelTitleList, int excelBeginCol, int excelEndCol) {
		if (excelTitleList == null) {
			return mappingExcelColAndDataIndex(excelBeginCol, excelEndCol);
		} else {
			HashMap excelTitleMapDataIndex = new HashMap();
			List realTitle = (List) excelTitleList.get(0);
			String title;
			for (int i = 0; i < realTitle.size(); i++) {
				if (realTitle.get(i) != null) {
					title = realTitle.get(i).toString().trim();
					excelTitleMapDataIndex.put(title, i);
					excelTitleMapDataIndex.put(StringUtil.clearMistyChars(title.replaceAll("\\s+", ""), ""), i);
					excelTitleMapDataIndex.put(ExcelToyConstants.EXCEL_TITLE_BEGIN_MARK.concat(title)
							.concat(ExcelToyConstants.EXCEL_TITLE_END_MARK), i);
				}
			}
			return excelTitleMapDataIndex;
		}
	}

	/**
	 * @todo <b>匹配excel列跟读取后的数据集合的列，用于数据读取时的匹配</b>
	 * @param beginCol
	 * @param endCol
	 * @return
	 */
	public static HashMap mappingExcelColAndDataIndex(int excelBeginCol, int excelEndCol) {
		HashMap excelColMapDataIndex = new HashMap();
		// 最大excel列为255
		int endCol = excelEndCol > 0 ? excelEndCol : 255;
		for (int i = 1; i < endCol; i++) {
			excelColMapDataIndex.put(Integer.toString(i), i - excelBeginCol);
		}
		return excelColMapDataIndex;
	}

	/**
	 * @todo 获取excel文件的后缀名，判定是xls还是xlsx
	 * @param fileName
	 * @return
	 */
	public static String getExcelFileSuffix(String fileName) {
		String lowercaseName = (fileName == null) ? "" : fileName.toLowerCase();
		if (StringUtil.matches(lowercaseName, "\\.xlsx$"))
			return "xlsx";
		else if (StringUtil.matches(lowercaseName, "\\.xls$"))
			return "xls";
		else {
			if (StringUtil.isBlank(ExcelToyConstants.getExcelFileSuffix()))
				return "xls";
			else {
				return ExcelToyConstants.getExcelFileSuffix();
			}
		}
	}
}
