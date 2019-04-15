/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;
import java.util.List;

import org.sagacity.tools.exceltoy.config.XMLConfigLoader;

/**
 * @project sagacity-tools
 * @description 数据导出模型
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ExportModel.java,Revision:v1.0,Date:2009-6-8
 */
@SuppressWarnings("rawtypes")
public class ExportModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7883007588495390027L;

	/**
	 * 任务id
	 */
	private String id;

	/**
	 * 数据库
	 */
	private String datasource;

	/**
	 * 数据库自动提交
	 */
	private String autoCommit = "false";

	/**
	 * 导出clob的字符集
	 */
	private String charset = "UTF-8";

	/**
	 * 导出目标路径
	 */
	private String dist;

	/**
	 * 对应sql文件
	 */
	private String sql;

	/**
	 * 保存excel默认的sheet名称
	 */
	private String sheet = "sheet1";

	/**
	 * blob存放文件路径
	 */
	private String blobFile;
	/**
	 * 最大记录数
	 */
	private String maxLimit;

	/**
	 * 
	 */
	private String mappingTables;

	/**
	 * 删除之前的excel文件或路径，删除路径为：delete="path"
	 */
	private String delete = "file";

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
	 * @return the tables
	 */
	public List getTables() {
		return XMLConfigLoader.parseMappingTables(id, mappingTables);
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
	 * @return the maxLimit
	 */
	public String getMaxLimit() {
		return maxLimit;
	}

	/**
	 * @param maxLimit
	 *            the maxLimit to set
	 */
	public void setMaxLimit(String maxLimit) {
		this.maxLimit = maxLimit;
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
	 * @return the delete
	 */
	public String getDelete() {
		return delete;
	}

	/**
	 * @param delete
	 *            the delete to set
	 */
	public void setDelete(String delete) {
		this.delete = delete;
	}

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

}
