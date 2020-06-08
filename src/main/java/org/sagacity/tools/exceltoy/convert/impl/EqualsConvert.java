/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;

/**
 * @project sagacity-tools
 * @description 连接字符串
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:EqualsConvert.java,Revision:v1.0,Date:2011-11-23
 */
public class EqualsConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6834903716104010484L;

	/**
	 * 分隔符号
	 */
	private String splitSign = ",";

	/**
	 * 是否区分大小写
	 */
	private boolean ignoreCase = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java
	 * .lang.Object)
	 */
	@Override
	public Object convert(Object param) throws Exception {
		param = super.replaceParams(param);
		// 增加一个空格避免split出空数组
		String[] args = param.toString().concat(" ").split(",");
		String compare = args[0].trim();
		if (compare.equals("null")) {
			compare = "";
		}
		int length = args.length;
		for (int i = 0; i <= ((length - 1) / 2); i = i + 2) {
			if (args[i + 1].trim().equals("null")) {
				args[i + 1] = "";
			}
			if (ignoreCase) {
				if (compare.equalsIgnoreCase(args[i + 1].trim())) {
					return args[i + 2];
				}
			} else if (compare.equals(args[i + 1].trim())) {
				return args[i + 2];
			}
		}
		if ((length % 2) == 0) {
			String other = args[length - 1];
			return other.substring(0, other.length() - 1);
		}
		return compare;
	}

	/**
	 * @return the splitSign
	 */
	public String getSplitSign() {
		return splitSign;
	}

	/**
	 * @param splitSign the splitSign to set
	 */
	public void setSplitSign(String splitSign) {
		this.splitSign = splitSign;
	}

	/**
	 * @return the ignoreCase
	 */
	public boolean getIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * @param ignoreCase the ignoreCase to set
	 */
	public void setIgnoreCase(String ignoreCase) {
		this.ignoreCase = Boolean.parseBoolean(ignoreCase);
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
	public EqualsConvert clone() {
		try {
			// TODO Auto-generated method stub
			return (EqualsConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
