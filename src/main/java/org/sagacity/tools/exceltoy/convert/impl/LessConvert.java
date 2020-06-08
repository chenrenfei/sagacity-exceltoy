package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.utils.DateUtil;

/**
 * 小于比较, 格式:10,4,a1,5,a2,11,a3,other
 * 
 * 
 * @author zhong
 *
 */
public class LessConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6834903716104010484L;

	/**
	 * 分隔符号
	 */
	private String splitSign = ",";

	private String type = "number";

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
		// 格式:10,4,a1,5,a2,11,a3,other
		String[] args = param.toString().concat(" ").split(",");
		String compare = args[0].trim();
		int length = args.length;
		for (int i = 0; i <= ((length - 1) / 2); i = i + 2) {
			if (type.equals("number")) {
				if (Double.parseDouble(compare) < Double.parseDouble(args[i + 1].trim())) {
					return args[i + 2];
				}
			} else if (type.equals("timestamp")) {
				if (DateUtil.parseString(compare).before(DateUtil.parseString(args[1].trim()))) {
					return args[i + 2];
				}
			} else {
				if (compare.compareTo(args[1].trim()) < 0) {
					return args[i + 2];
				}
			}
		}
		String other = args[length - 1];
		return other.substring(0, other.length() - 1);
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		if (type.equalsIgnoreCase("decimal") || type.equalsIgnoreCase("bigdecimal") || type.equalsIgnoreCase("double")
				|| type.equalsIgnoreCase("float") || type.equalsIgnoreCase("int") || type.equalsIgnoreCase("integer")) {
			this.type = "number";
		} else if (type.equalsIgnoreCase("date") || type.equalsIgnoreCase("time")
				|| type.equalsIgnoreCase("timestamp")) {
			this.type = "timestamp";
		} else {
			this.type = "string";
		}
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
	public LessConvert clone() {
		try {
			return (LessConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
