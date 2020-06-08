/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;

/**
 * @project sagacity-tools
 * @description 字符串位置获取转换器
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:IndexOfConvert.java,Revision:v1.0,Date:2010-1-5
 */
public class IndexOfConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2849290858099109868L;

	/**
	 * 需要确定位置的字符
	 */
	private String regex;

	private int fromIndex = 0;

	/**
	 * 是否取最后的位置
	 */
	private boolean isLast = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java.lang
	 * .Object)
	 */
	public Object convert(Object param) throws Exception {
		param = super.replaceParams(param);
		String source = ConvertUtil.jsonParamSet(this, param);
		int result = -1;
		try {
			if (isLast)
				return source.lastIndexOf(regex);
			return source.indexOf(regex, fromIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param regex the regex to set
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}

	/**
	 * @param fromIndex the fromIndex to set
	 */
	public void setFromIndex(String fromIndex) {
		this.fromIndex = Integer.parseInt(fromIndex);
	}

	/**
	 * @param isLast the isLast to set
	 */
	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#reset()
	 */
	@Override
	public void reset() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public IndexOfConvert clone() {
		try {
			return (IndexOfConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
