/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;

/**
 * @project sagacity-tools
 * @description 字符串截取
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:SubStrConvert.java,Revision:v1.0,Date:2010-1-5
 */
public class SubStrConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2217709779993788334L;

	/**
	 * 开始位置
	 */
	private int beginIndex;

	/**
	 * 截止位置
	 */
	private int endIndex;

	private String splitSign = ",";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java .lang
	 * .Object)
	 */
	public Object convert(Object param) throws Exception {
		if (param == null)
			return null;
		param = super.replaceParams(param);
		String result = ConvertUtil.jsonParamSet(this, param);
		String[] args = result.split(splitSign);
		if (args.length == 1) {
			result = args[0].substring(beginIndex, endIndex);
		} else if (args.length == 2) {
			int start = Integer.parseInt(args[1]);
			if (start > 0)
				result = args[0].substring(0, Integer.parseInt(args[1]));
			else
				result = args[0];
		} else {
			int start = Integer.parseInt(args[1]);
			int end = Integer.parseInt(args[2]);
			if (end < start || start < 0 || end < 0)
				result = args[0];
			else
				result = args[0].substring(start, Integer.parseInt(args[2]));
		}
		return result;
	}

	/**
	 * @param beginIndex
	 *            the beginIndex to set
	 */
	public void setBeginIndex(String beginIndex) {
		this.beginIndex = Integer.parseInt(beginIndex);
	}

	/**
	 * @param endIndex
	 *            the endIndex to set
	 */
	public void setEndIndex(String endIndex) {
		this.endIndex = Integer.parseInt(endIndex);
	}

	/**
	 * @param splitSign
	 *            the splitSign to set
	 */
	public void setSplitSign(String splitSign) {
		this.splitSign = splitSign;
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
	public SubStrConvert clone() {
		try {
			// TODO Auto-generated method stub
			return (SubStrConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
