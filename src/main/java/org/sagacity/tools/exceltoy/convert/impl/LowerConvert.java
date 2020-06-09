/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;

/**
 * @project sagacity-tools
 * @description 字符串小写
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:LowerConvert.java,Revision:v1.0,Date:2010-6-22
 */
public class LowerConvert extends AbstractConvert {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5236798724880739532L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java.lang
	 * .Object)
	 */
	public Object convert(Object param) throws Exception {
		if (param == null) {
			return null;
		}
		param = super.replaceParams(param);
		return ((String) param).toLowerCase();
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
	public LowerConvert clone() {
		try {
			return (LowerConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
