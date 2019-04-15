/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;
import java.util.List;

import org.sagacity.tools.exceltoy.config.XMLConfigLoader;

/**
 * @project sagacity-tools
 * @description 导入模型
 * @author chrenfei
 * @version id:ImportModel.java,Revision:v1.0,Date:2009-6-8
 */
@SuppressWarnings("rawtypes")
public class ImportModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5917154172056549308L;
	private String id;
	/**
	 * 数据库
	 */
	private String datasource;

	/**
	 * 隔离级别
	 */
	private String isolationlevel;
	/**
	 * 是否自动提交
	 */
	private String autoCommit;
	private String mappingTables;
	private String dist;
	private String depends;
	
	/**
	 * 忽略插入
	 */
	private boolean ignoreMainInsert=false; 

	private String charset = "UTF-8";

	/**
	 * 条件过滤器
	 */
	private String filter = null;

	/**
	 * 过滤条件成立则退出
	 */
	private boolean filterBreak = false;
	
	/**
	 * 根据主键进行数据合并
	 */
	private boolean pkDataMerge=false;

	/**
	 * 过滤器提示信息
	 */
	private String filterMsg = "";

	/**
	 * 主表是否清空
	 */
	private boolean mainClear = false;

	/**
	 * 前置事务
	 */
	private List befores;

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset
	 *            the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * 基于主表的重复数据过滤，一般由多个字段组合成
	 */
	private String mainPK;

	/**
	 * 外键约束过滤
	 */
	private boolean fkFilter = false;

	/**
	 * @return the mainPK
	 */
	public String getMainPK() {
		return mainPK;
	}

	/**
	 * @param mainPK
	 *            the mainPK to set
	 */
	public void setMainPK(String mainPK) {
		this.mainPK = mainPK;
	}

	/**
	 * 主表插入信息，一个任务只能对应一个主表
	 */
	private String mainEql;
	/**
	 * 子表插入信息
	 */
	private List subEqls;

	/**
	 * 后置事务
	 */
	private List afters;

	/**
	 * 导入报告文件
	 */
	private String reportFile;

	/**
	 * blob是否以文件形式保存
	 */
	private String blobFile;

	/**
	 * excel文件
	 */
	private String files;

	/**
	 * excel文件sheet
	 */
	private String sheet;

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
	 * 行数据全为空则终止向下读取
	 */
	private boolean rowEmptyEnd = true;

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
	 * @return the isolationlevel
	 */
	public String getIsolationlevel() {
		return isolationlevel;
	}

	/**
	 * @param isolationlevel
	 *            the isolationlevel to set
	 */
	public void setIsolationlevel(String isolationlevel) {
		this.isolationlevel = isolationlevel;
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
	 * @return the tablesMap
	 */
	public List getTablesMap() {
		return XMLConfigLoader.parseMappingTables(id, mappingTables);
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
	 * @return the depends
	 */
	public String getDepends() {
		return depends;
	}

	/**
	 * @param depends
	 *            the depends to set
	 */
	public void setDepends(String depends) {
		this.depends = depends;
	}

	/**
	 * @return the mainClear
	 */
	public boolean getMainClear() {
		return mainClear;
	}

	/**
	 * @param mainClear
	 *            the mainClear to set
	 */
	public void setMainClear(boolean mainClear) {
		this.mainClear = mainClear;
	}

	/**
	 * @return the befores
	 */
	public List getBefores() {
		return befores;
	}

	/**
	 * @param befores
	 *            the befores to set
	 */
	public void setBefores(List befores) {
		this.befores = befores;
	}

	/**
	 * @return the mainEql
	 */
	public String getMainEql() {
		return mainEql;
	}

	/**
	 * @param mainEql
	 *            the mainEql to set
	 */
	public void setMainEql(String mainEql) {
		this.mainEql = mainEql;
	}

	/**
	 * @return the subEqls
	 */
	public List getSubEqls() {
		return subEqls;
	}

	/**
	 * @param subEqls
	 *            the subEqls to set
	 */
	public void setSubEqls(List subEqls) {
		this.subEqls = subEqls;
	}

	/**
	 * @return the afters
	 */
	public List getAfters() {
		return afters;
	}

	/**
	 * @param afters
	 *            the afters to set
	 */
	public void setAfters(List afters) {
		this.afters = afters;
	}

	/**
	 * @return the mappingTables
	 */
	public String getMappingTables() {
		return mappingTables;
	}

	/**
	 * @param mappingTables
	 *            the mappingTables to set
	 */
	public void setMappingTables(String mappingTables) {
		this.mappingTables = mappingTables;
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
	 * @return the rowEmptyEnd
	 */
	public boolean isRowEmptyEnd() {
		return rowEmptyEnd;
	}

	/**
	 * @param rowEmptyEnd
	 *            the rowEmptyEnd to set
	 */
	public void setRowEmptyEnd(boolean rowEmptyEnd) {
		this.rowEmptyEnd = rowEmptyEnd;
	}

	/**
	 * @return the blobFile
	 */
	public String getBlobFile() {
		return this.blobFile;
	}

	/**
	 * @param blobFile
	 *            the blobFile to set
	 */
	public void setBlobFile(String blobFile) {
		this.blobFile = blobFile;
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
	 * @return the fkFilter
	 */
	public boolean isFkFilter() {
		return fkFilter;
	}

	/**
	 * @param fkFilter
	 *            the fkFilter to set
	 */
	public void setFkFilter(boolean fkFilter) {
		this.fkFilter = fkFilter;
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
	 * @return the filterMsg
	 */
	public String getFilterMsg() {
		return filterMsg;
	}

	/**
	 * @param filterMsg
	 *            the filterMsg to set
	 */
	public void setFilterMsg(String filterMsg) {
		this.filterMsg = filterMsg;
	}

	/**
	 * @return the filterBreak
	 */
	public boolean isFilterBreak() {
		return filterBreak;
	}

	/**
	 * @param filterBreak
	 *            the filterBreak to set
	 */
	public void setFilterBreak(boolean filterBreak) {
		this.filterBreak = filterBreak;
	}

	/**
	 * @return the reportFile
	 */
	public String getReportFile() {
		return reportFile;
	}

	/**
	 * @param reportFile
	 *            the reportFile to set
	 */
	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	/**
	 * @return the pkDataMerge
	 */
	public boolean isPkDataMerge() {
		return pkDataMerge;
	}

	/**
	 * @param pkDataMerge the pkDataMerge to set
	 */
	public void setPkDataMerge(boolean pkDataMerge) {
		this.pkDataMerge = pkDataMerge;
	}

	/**
	 * @return the ignoreMainInsert
	 */
	public boolean isIgnoreMainInsert() {
		return ignoreMainInsert;
	}

	/**
	 * @param ignoreMainInsert the ignoreMainInsert to set
	 */
	public void setIgnoreMainInsert(boolean ignoreMainInsert) {
		this.ignoreMainInsert = ignoreMainInsert;
	}

	
	
}
