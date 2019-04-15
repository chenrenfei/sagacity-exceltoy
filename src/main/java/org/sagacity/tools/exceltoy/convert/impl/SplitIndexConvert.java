/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 分割符号转换器
 * @author zhongxuchen <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:SplitIndexConvert.java,Revision:v1.0,Date:2011-7-26
 */
public class SplitIndexConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8702097882601817088L;

	/**
	 * 分割符号
	 */
	private String splitSign = "\\s+";

	/**
	 * 去除空格
	 */
	private String trim = "false";

	/**
	 * 分割后的返回位置
	 */
	private int index = 0;

	public Object convert(Object key) throws Exception {
		key = super.replaceParams(key);
		String source = ConvertUtil.jsonParamSet(this, key);
		if (StringUtil.isNotBlank(source)) {
			String[] splitAry = source.split(this.splitSign);
			if (index < splitAry.length)
				return trim.equalsIgnoreCase("true") ? splitAry[index].trim() : splitAry[index];
		}
		return "";
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
	 * @return the trim
	 */
	public String getTrim() {
		return trim;
	}

	/**
	 * @param trim
	 *            the trim to set
	 */
	public void setTrim(String trim) {
		this.trim = trim;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#reset()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public SplitIndexConvert clone() {
		try {
			// TODO Auto-generated method stub
			return (SplitIndexConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
