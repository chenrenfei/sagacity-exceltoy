/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 字符串补齐
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:AppendConvert.java,Revision:v1.0,Date:2010-6-22 下午10:16:01
 */
public class AppendConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4994701889937547009L;
	/**
	 * 长度
	 */
	private int size;
	// 方向
	private String orient = "left";
	/**
	 * 增加的字符
	 */
	private String append = " ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java .lang
	 * .Object)
	 */
	public Object convert(Object param) throws Exception {
		if (param == null)
			return param;
		return StringUtil.appendStr(super.replaceParams(param).toString(), append, size,
				orient.equalsIgnoreCase("left") ? true : false);
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @param orient
	 *            the orient to set
	 */
	public void setOrient(String orient) {
		this.orient = orient;
	}

	/**
	 * @param append
	 *            the append to set
	 */
	public void setAppend(String append) {
		this.append = append;
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
	public AppendConvert clone() {
		try {
			// TODO Auto-generated method stub
			return (AppendConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
