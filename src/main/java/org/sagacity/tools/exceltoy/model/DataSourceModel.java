/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;

import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 数据库配置信息数据模型
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:DataSourceModel.java,Revision:v1.0,Date:2009-12-31
 */
public class DataSourceModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1579818177066481584L;
	private String url;
	private String driver;
	private String username;
	private String password;
	private String catalog;
	private String schema;
	// 被禁止的操作
	private String forbid = null;

	private String[] forbids = null;

	public DataSourceModel(String url, String driver, String username, String password, String catalog, String schema,
			String forbid) {
		this.url = url;
		this.driver = driver;
		this.username = username;
		this.password = password;
		this.catalog = catalog;
		this.schema = schema;
		setForbid(forbid);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the catalog
	 */
	public String getCatalog() {
		return catalog;
	}

	/**
	 * @param catalog
	 *            the catalog to set
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	/**
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @param schema
	 *            the schema to set
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * @return the forbid
	 */
	public String getForbid() {
		return forbid;
	}

	/**
	 * @param forbid
	 *            the forbid to set
	 */
	public void setForbid(String forbid) {
		this.forbid = forbid;
		if (StringUtil.isNotBlank(forbid)) {
			this.forbids = forbid.split(",");
		}
	}

	/**
	 * @return the forbids
	 */
	public String[] getForbids() {
		return forbids;
	}

}
