/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;

/**
 * @project sagacity-tools
 * @description 修改任务配置模型
 * @author chenrenfei
 * @version id:UpdateModel.java,Revision:v1.0,Date:2010-10-21
 */
public class UpdateModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3339466734207643015L;

	/**
	 * 任务id
	 */
	private String id;

	/**
	 * 数据库
	 */
	private String datasource;

	/**
	 * 数据库连接自动提交
	 */
	private String autoCommit = "false";

	/**
	 * 批量sql文件
	 */
	private String sqlSplit = null;

	/**
	 * sql文件
	 */
	private String sqlFile = null;

	/**
	 * 批量执行(默认200)
	 */
	private int batchSize = 200;

	/**
	 * 条件过滤器
	 */
	private String filter;

	/**
	 * 全部预处理
	 */
	private String prepared = "false";
	/**
	 * 导出目标路径
	 */
	private String dist;

	/**
	 * update 语句
	 */
	private String sql;

	/**
	 * excel的sheet
	 */
	private String sheet;

	/**
	 * excel文件
	 */
	private String files;

	/**
	 * 标题行
	 */
	private int titleRow = 1;

	/**
	 * 开始行
	 */
	private int beginRow = 2;

	/**
	 * 截止行
	 */
	private int endRow = -1;

	/**
	 * 开始列
	 */
	private int beginCol = 1;

	/**
	 * 截止列
	 */
	private int endCol = -1;

	/**
	 * 循环
	 */
	private String loop;

	/**
	 * 默认循环类别为int类型
	 */
	private String loopType = "int";

	/**
	 * 循环变量名称
	 */
	private String loopAlias = "loopAliasVar";

	/**
	 * 日期循环类型
	 */
	private String dateType = "day";

	private String dateFormat = "yyyy-MM-dd";

	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat
	 *            the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @return the dateType
	 */
	public String getDateType() {
		return dateType;
	}

	/**
	 * @param dateType
	 *            the dateType to set
	 */
	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	/**
	 * @return the loopAlias
	 */
	public String getLoopAlias() {
		return loopAlias;
	}

	/**
	 * @param loopAlias
	 *            the loopAlias to set
	 */
	public void setLoopAlias(String loopAlias) {
		this.loopAlias = loopAlias;
	}

	/**
	 * @return the loop
	 */
	public String getLoop() {
		return loop;
	}

	/**
	 * @param loop
	 *            the loop to set
	 */
	public void setLoop(String loop) {
		this.loop = loop;
	}

	/**
	 * @return the loopType
	 */
	public String getLoopType() {
		return loopType;
	}

	/**
	 * @param loopType
	 *            the loopType to set
	 */
	public void setLoopType(String loopType) {
		this.loopType = loopType;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the datasource
	 */
	public String getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource
	 *            the datasource to set
	 */
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	/**
	 * @return the dist
	 */
	public String getDist() {
		return dist;
	}

	/**
	 * @param dist
	 *            the dist to set
	 */
	public void setDist(String dist) {
		this.dist = dist;
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @return the sheet
	 */
	public String getSheet() {
		return sheet;
	}

	/**
	 * @param sheet
	 *            the sheet to set
	 */
	public void setSheet(String sheet) {
		this.sheet = sheet;
	}

	/**
	 * @return the autoCommit
	 */
	public String getAutoCommit() {
		return autoCommit;
	}

	/**
	 * @param autoCommit
	 *            the autoCommit to set
	 */
	public void setAutoCommit(String autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * @return the files
	 */
	public String getFiles() {
		return files;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles(String files) {
		this.files = files;
	}

	/**
	 * @return the titleRow
	 */
	public int getTitleRow() {
		return titleRow;
	}

	/**
	 * @param titleRow
	 *            the titleRow to set
	 */
	public void setTitleRow(int titleRow) {
		this.titleRow = titleRow;
	}

	/**
	 * @return the beginRow
	 */
	public int getBeginRow() {
		return beginRow;
	}

	/**
	 * @param beginRow
	 *            the beginRow to set
	 */
	public void setBeginRow(int beginRow) {
		this.beginRow = beginRow;
	}

	/**
	 * @return the endRow
	 */
	public int getEndRow() {
		return endRow;
	}

	/**
	 * @param endRow
	 *            the endRow to set
	 */
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	/**
	 * @return the beginCol
	 */
	public int getBeginCol() {
		return beginCol;
	}

	/**
	 * @param beginCol
	 *            the beginCol to set
	 */
	public void setBeginCol(int beginCol) {
		this.beginCol = beginCol;
	}

	/**
	 * @return the endCol
	 */
	public int getEndCol() {
		return endCol;
	}

	/**
	 * @param endCol
	 *            the endCol to set
	 */
	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}

	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * @return the sqlFile
	 */
	public String getSqlFile() {
		return sqlFile;
	}

	/**
	 * @param sqlFile
	 *            the sqlFile to set
	 */
	public void setSqlFile(String sqlFile) {
		this.sqlFile = sqlFile;
	}

	/**
	 * @return the sqlSplit
	 */
	public String getSqlSplit() {
		return sqlSplit;
	}

	/**
	 * @param sqlSplit
	 *            the sqlSplit to set
	 */
	public void setSqlSplit(String sqlSplit) {
		this.sqlSplit = sqlSplit;
	}

	/**
	 * @return the batchSize
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * @param batchSize
	 *            the batchSize to set
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 * @return the prepared
	 */
	public String getPrepared() {
		return prepared;
	}

	/**
	 * @param prepared
	 *            the prepared to set
	 */
	public void setPrepared(String prepared) {
		this.prepared = prepared;
	}

}
