/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.utils.ExpressionUtil;

/**
 * @project sagacity-tools
 * @description 表达式计算转换
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ExpressionConvert.java,Revision:v1.0,Date:2010-7-13
 */
public class ExpressionConvert extends AbstractConvert {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2962636323186026746L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java
	 * .lang.Object)
	 */
	public Object convert(Object key) throws Exception {
		key = super.replaceParams(key);
		String expression = (String) key;
		return ExpressionUtil.calculate(expression);
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
	public ExpressionConvert clone() {
		try {
			// TODO Auto-generated method stub
			return (ExpressionConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
