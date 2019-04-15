/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;

/**
 * @project sagacity-tools
 * @description 数据库表排序任务定义模型
 * @author chenrf
 * @version id:OrderTableModel.java,Revision:v1.0,Date:2009-12-31
 */
public class OrderTableModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9076074643395192662L;

	/**
	 * id
	 */
	private String id;

	/**
	 * 对应数据库
	 */
	private String datasource;

	/**
	 * 输出文件
	 */
	private String outFile;

	/**
	 * 包含表信息
	 */
	private String[] includes;

	/**
	 * 排除的表
	 */
	private String[] excludes;

	/**
	 * 默认按照依赖升序排列
	 */
	private String order = "asc";

	/**
	 * 字段未完全匹配的表
	 */
	private String[] unmatchTables;

	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
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
	 * @return the outFile
	 */
	public String getOutFile() {
		return outFile;
	}

	/**
	 * @param outFile
	 *            the outFile to set
	 */
	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	/**
	 * @return the includes
	 */
	public String[] getIncludes() {
		return includes;
	}

	/**
	 * @param includes
	 *            the includes to set
	 */
	public void setIncludes(String[] includes) {
		this.includes = includes;
	}

	/**
	 * @return the excludes
	 */
	public String[] getExcludes() {
		return excludes;
	}

	/**
	 * @param excludes
	 *            the excludes to set
	 */
	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	/**
	 * @return the unmatchTables
	 */
	public String[] getUnmatchTables() {
		return unmatchTables;
	}

	/**
	 * @param unmatchTables
	 *            the unmatchTables to set
	 */
	public void setUnmatchTables(String[] unmatchTables) {
		this.unmatchTables = unmatchTables;
	}

}
