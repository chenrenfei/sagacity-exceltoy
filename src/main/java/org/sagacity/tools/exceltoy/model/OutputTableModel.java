/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;
import java.util.List;

/**
 * @project sagacity-tools
 * @description 提供不同于核心框架的outputTable数据模型，用于扩展原对象模型缺少的数据
 * @author renfei.chen <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version Revision:v1.0,Date:2013-11-6
 * @Modification Date:2013-11-6 {填写修改说明}
 */
public class OutputTableModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4450923910688241657L;
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 表注释
	 */
	private String tableRemark;
	
	/**
	 * 是否非字段完全匹配的表（字段不匹配则直接完整的输出select 字段 from table，方便修改）
	 */
	private String unMatchTable="false";
	
	/**
	 * 表对应的字段信息
	 */
	private List<TableColumnMeta> colMetas;
	
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return the tableRemark
	 */
	public String getTableRemark() {
		return tableRemark;
	}
	/**
	 * @param tableRemark the tableRemark to set
	 */
	public void setTableRemark(String tableRemark) {
		this.tableRemark = tableRemark;
	}
	/**
	 * @return the unMatchTable
	 */
	public String getUnMatchTable() {
		return unMatchTable;
	}
	/**
	 * @param unMatchTable the unMatchTable to set
	 */
	public void setUnMatchTable(String unMatchTable) {
		this.unMatchTable = unMatchTable;
	}
	/**
	 * @return the colMetas
	 */
	public List<TableColumnMeta> getColMetas() {
		return colMetas;
	}
	/**
	 * @param colMetas the colMetas to set
	 */
	public void setColMetas(List<TableColumnMeta> colMetas) {
		this.colMetas = colMetas;
	}
	
	
}
