/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;

/**
 * @project sagacity-tools
 * @description 导入过程中的前置后置事务配置
 * @author zhongxuchen
 * @version id:TransationModel.java,Revision:v1.0,Date:2011-5-16
 */
public class TransationModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5160663749228341563L;

	/**
	 * sql语句
	 */
	private String sql;

	/**
	 * 是否自动提交
	 */
	private boolean autoCommit = false;

	/**
	 * 是否是sql文件
	 */
	private boolean isSqlFile = false;

	/**
	 * sql文件的分割符号
	 */
	private String splitSign;

	/**
	 * 导入任务中动态定义属性，并为属性指定特定的值
	 */
	private String property;

	/**
	 * 属性值
	 */
	private String propertyValue;

	/**
	 * 字符集
	 */
	private String encoding = null;

	/**
	 * 在main excel循环前
	 */
	private boolean loopBeforeMain = false;

	/**
	 * 在main excel循环后
	 */
	private boolean loopAfterMain = false;

	/**
	 * 异常后是否执行
	 */
	private boolean errorRun = false;

	/**
	 * 仅仅在错误时执行
	 */
	private boolean onlyError = false;

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
	 * @return the autoCommit
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}

	/**
	 * @param autoCommit
	 *            the autoCommit to set
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * @return the isSqlFile
	 */
	public boolean isSqlFile() {
		return isSqlFile;
	}

	/**
	 * @param isSqlFile
	 *            the isSqlFile to set
	 */
	public void setSqlFile(boolean isSqlFile) {
		this.isSqlFile = isSqlFile;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return the splitSign
	 */
	public String getSplitSign() {
		return splitSign;
	}

	/**
	 * @param splitSign
	 *            the splitSign to set
	 */
	public void setSplitSign(String splitSign) {
		this.splitSign = splitSign;
	}

	/**
	 * @return the loopBeforeMain
	 */
	public boolean isLoopBeforeMain() {
		return loopBeforeMain;
	}

	/**
	 * @param loopBeforeMain
	 *            the loopBeforeMain to set
	 */
	public void setLoopBeforeMain(boolean loopBeforeMain) {
		this.loopBeforeMain = loopBeforeMain;
	}

	/**
	 * @return the loopAfterMain
	 */
	public boolean isLoopAfterMain() {
		return loopAfterMain;
	}

	/**
	 * @param loopAfterMain
	 *            the loopAfterMain to set
	 */
	public void setLoopAfterMain(boolean loopAfterMain) {
		this.loopAfterMain = loopAfterMain;
	}

	/**
	 * @return the errorRun
	 */
	public boolean isErrorRun() {
		return errorRun;
	}

	/**
	 * @param errorRun
	 *            the errorRun to set
	 */
	public void setErrorRun(boolean errorRun) {
		this.errorRun = errorRun;
	}

	/**
	 * @return the onlyError
	 */
	public boolean isOnlyError() {
		return onlyError;
	}

	/**
	 * @param onlyError
	 *            the onlyError to set
	 */
	public void setOnlyError(boolean onlyError) {
		this.onlyError = onlyError;
	}

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * @return the propertyValue
	 */
	public String getPropertyValue() {
		return propertyValue;
	}

	/**
	 * @param propertyValue
	 *            the propertyValue to set
	 */
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

}
