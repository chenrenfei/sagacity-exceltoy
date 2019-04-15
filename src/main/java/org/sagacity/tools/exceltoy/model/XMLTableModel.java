/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @project sagacity-tools
 * @description 通过xml导入导出表数据的对象模型
 * @author chenrenfei
 * @version id:XMLTableModel.java,Revision:v1.0,Date:2010-7-9
 */
public class XMLTableModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1322881288424880082L;
	private String name;
	private String dist;
	private String endRow = null;

	/**
	 * 自定义sql
	 */
	private String sql;

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
	 * 字段映射关系
	 */
	private HashMap<String, String> fieldMapping = new HashMap<String, String>();

	/**
	 * 是否过滤外键约束
	 */
	private boolean fkFilter = false;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the endRow
	 */
	public String getEndRow() {
		return endRow;
	}

	/**
	 * @param endRow
	 *            the endRow to set
	 */
	public void setEndRow(String endRow) {
		this.endRow = endRow;
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
	 * @return the fieldMapping
	 */
	public HashMap<String, String> getFieldMapping() {
		return fieldMapping;
	}

	/**
	 * @param fieldMapping
	 *            the fieldMapping to set
	 */
	public void setFieldMapping(HashMap<String, String> fieldMapping) {
		this.fieldMapping = fieldMapping;
	}

}
