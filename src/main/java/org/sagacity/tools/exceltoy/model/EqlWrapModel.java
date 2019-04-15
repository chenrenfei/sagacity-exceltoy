/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;

/**
 * @project sagacity-tools
 * @description eql语句辅助器的任务配置模型
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:EqlWrapModel.java,Revision:v1.0,Date:2010-8-5 下午09:58:36
 */
public class EqlWrapModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1694204192850464798L;

	/**
	 * id
	 */
	private String id;

	/**
	 * 对应数据库
	 */
	private String datasource;

	private String table;
	/**
	 * excel文件
	 */
	private String file;

	/**
	 * excel sheet
	 */
	private String sheet;

	/**
	 * 标题行
	 */
	private int titleRow = 1;

	/**
	 * 开始列
	 */
	private int beginCol = 1;

	/**
	 * 截止列
	 */
	private int endCol = 255;

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
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table
	 *            the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
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

}
