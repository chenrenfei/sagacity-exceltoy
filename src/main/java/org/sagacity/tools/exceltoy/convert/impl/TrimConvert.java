/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;

/**
 * @project sagacity-tools
 * @description 字符串去除空格
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:TrimConvert.java,Revision:v1.0,Date:2010-1-5
 */
public class TrimConvert extends AbstractConvert {

	/**
	 * 
	 */
	private static final long serialVersionUID = 681613827691880876L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java .lang
	 * .Object)
	 */
	public Object convert(Object param) throws Exception {
		if (param == null) {
			return null;
		}
		param = super.replaceParams(param);
		String result = (String) param;
		return result.trim();
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
	public TrimConvert clone() {
		try {
			return (TrimConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
