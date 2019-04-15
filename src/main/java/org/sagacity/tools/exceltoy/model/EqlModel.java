/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;

/**
 * @project sagacity-tools
 * @description excel query laugage 设置模型
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:EqlModel.java,Revision:v1.0,Date:2010-1-29
 */
public class EqlModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1088828742725872488L;

	/**
	 * 循环列
	 */
	private String loopCol;

	/**
	 * 分隔符
	 */
	private String splitSign;

	/**
	 * 切割后变量
	 */
	private String loopAs;

	/**
	 * 剔除空白
	 */
	private boolean skipNull = false;

	/**
	 * 排除重复
	 */
	private boolean skipRepeat = false;

	/**
	 * 最小记录数
	 */
	private int minSize = 1;

	/**
	 * eql内容
	 */
	private String eqlContent;

	/**
	 * 消息
	 */
	private String message = "";

	/**
	 * 是否清空
	 */
	private String clear = "false";

	/**
	 * 条件检查不符合是否异常退出
	 */
	private boolean isBreak = false;

	/**
	 * @return the isBreak
	 */
	public boolean isBreak() {
		return isBreak;
	}

	/**
	 * @param isBreak
	 *            the isBreak to set
	 */
	public void setBreak(boolean isBreak) {
		this.isBreak = isBreak;
	}

	/**
	 * @return the loopCol
	 */
	public String getLoopCol() {
		return loopCol;
	}

	/**
	 * @param loopCol
	 *            the loopCol to set
	 */
	public void setLoopCol(String loopCol) {
		this.loopCol = loopCol;
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
	 * @return the loopAs
	 */
	public String getLoopAs() {
		return loopAs;
	}

	/**
	 * @param loopAs
	 *            the loopAs to set
	 */
	public void setLoopAs(String loopAs) {
		this.loopAs = loopAs;
	}

	/**
	 * @return the eqlContent
	 */
	public String getEqlContent() {
		return eqlContent;
	}

	/**
	 * @param eqlContent
	 *            the eqlContent to set
	 */
	public void setEqlContent(String eqlContent) {
		this.eqlContent = eqlContent;
	}

	/**
	 * @return the clear
	 */
	public String getClear() {
		return clear;
	}

	/**
	 * @param clear
	 *            the clear to set
	 */
	public void setClear(String clear) {
		this.clear = clear;
	}

	/**
	 * @return the skipNull
	 */
	public boolean isSkipNull() {
		return skipNull;
	}

	/**
	 * @param skipNull
	 *            the skipNull to set
	 */
	public void setSkipNull(boolean skipNull) {
		this.skipNull = skipNull;
	}

	/**
	 * @return the skipRepeat
	 */
	public boolean isSkipRepeat() {
		return skipRepeat;
	}

	/**
	 * @param skipRepeat
	 *            the skipRepeat to set
	 */
	public void setSkipRepeat(boolean skipRepeat) {
		this.skipRepeat = skipRepeat;
	}

	/**
	 * @return the minSize
	 */
	public int getMinSize() {
		return minSize;
	}

	/**
	 * @param minSize
	 *            the minSize to set
	 */
	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
