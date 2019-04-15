/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @project sagacity-tools
 * @description eql解析结果模型
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:EqlParseResult.java,Revision:v1.0,Date:2010-6-8
 */
@SuppressWarnings({ "rawtypes" })
public class EqlParseResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4477440917058610939L;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 插入sql语句
	 */
	private String insertSql;

	/**
	 * 修改语句
	 */
	private String updateSql;

	/**
	 * 参数
	 */
	private String[] params;

	/**
	 * 字段
	 */
	private String[] fields;

	/**
	 * excel数据列
	 */
	private String[] excelCols;

	/**
	 * 数据库表字段类型对照
	 */
	private HashMap tableMeta = new HashMap();

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the insertSql
	 */
	public String getInsertSql() {
		return insertSql;
	}

	/**
	 * @param insertSql
	 *            the insertSql to set
	 */
	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	/**
	 * @return the params
	 */
	public String[] getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(String[] params) {
		this.params = params;
	}

	/**
	 * @return the fields
	 */
	public String[] getFields() {
		return fields;
	}

	/**
	 * @param fields
	 *            the fields to set
	 */
	public void setFields(String[] fields) {
		this.fields = fields;
	}

	/**
	 * @return the excelCols
	 */
	public String[] getExcelCols() {
		return excelCols;
	}

	/**
	 * @param excelCols
	 *            the excelCols to set
	 */
	public void setExcelCols(String[] excelCols) {
		this.excelCols = excelCols;
	}

	/**
	 * @return the tableMeta
	 */
	public HashMap getTableMeta() {
		return tableMeta;
	}

	/**
	 * @param tableMeta
	 *            the tableMeta to set
	 */
	public void setTableMeta(HashMap tableMeta) {
		this.tableMeta = tableMeta;
	}

	/**
	 * @return the updateSql
	 */
	public String getUpdateSql() {
		return updateSql;
	}

	/**
	 * @param updateSql
	 *            the updateSql to set
	 */
	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}

}
