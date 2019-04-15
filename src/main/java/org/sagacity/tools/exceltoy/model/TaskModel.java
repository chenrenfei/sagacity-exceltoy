/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;

/**
 * @project sagacity-tools
 * @description 任务对象模型
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:TaskModel.java,Revision:v1.0,Date:2010-1-4
 */
public class TaskModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8146438720512992287L;
	private String id;
	private String depends;
	private String type;
	private int status;
	private String datasource;
	private String isolationlevel;
	private String autoCommit;
	// 执行安全码
	private String securityCode;

	/**
	 * 安全提示
	 */
	private String securityTip;

	public TaskModel(String id, String type, String depends, String datasource, String autoCommit,
			String isolationlevel, String securityCode, String securityTip) {
		this.id = id;
		this.type = type;
		this.depends = depends;
		this.datasource = datasource;
		this.autoCommit = autoCommit;
		this.isolationlevel = isolationlevel;
		this.securityCode = securityCode;
		this.securityTip = securityTip;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
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
	 * @return the securityCode
	 */
	public String getSecurityCode() {
		return securityCode;
	}

	/**
	 * @param securityCode
	 *            the securityCode to set
	 */
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	/**
	 * @return the securityTip
	 */
	public String getSecurityTip() {
		return securityTip;
	}

	/**
	 * @param securityTip
	 *            the securityTip to set
	 */
	public void setSecurityTip(String securityTip) {
		this.securityTip = securityTip;
	}

}
