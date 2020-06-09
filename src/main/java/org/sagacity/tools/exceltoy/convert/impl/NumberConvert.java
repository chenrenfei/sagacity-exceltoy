/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.NumberUtil;

/**
 * @project sagacity-tools
 * @description 数字格式化
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:NumberConvert.java,Revision:v1.0,Date:2009-12-30
 */
public class NumberConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3576830381314029507L;

	/**
	 * 数字格式
	 */
	private String format;

	/**
	 * 是否大写金额
	 */
	private boolean capital = false;

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
		String key = ConvertUtil.jsonParamSet(this, param);
		// 大写
		if (capital) {
			if (format != null) {
				return NumberUtil.format(NumberUtil.capitalMoneyToNum(key), format);
			}
			return NumberUtil.capitalMoneyToNum(key);
		}
		return NumberUtil.format(key, format);
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @param capital the capital to set
	 */
	public void setCapital(String capital) {
		this.capital = new Boolean(capital).booleanValue();
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
	public NumberConvert clone() {
		try {
			return (NumberConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
