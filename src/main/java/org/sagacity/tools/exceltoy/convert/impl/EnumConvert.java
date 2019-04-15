/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import java.util.HashMap;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;

/**
 * @project sagacity-tools
 * @description 枚举类型数据转换
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:EnumConvert.java,Revision:v1.0,Date:2009-12-30
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EnumConvert extends AbstractConvert {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3459711052637985905L;

	private HashMap enumMap = null;

	/**
	 * 分割符号
	 */
	private String splitSign = ",";

	/**
	 * 键
	 */
	private String enumKeys;

	/**
	 * 键值
	 */
	private String enumValues;

	/**
	 * other
	 */
	private String other;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java.lang
	 * .Object)
	 */
	public Object convert(Object key) throws Exception {
		key = super.replaceParams(key);
		Object result = null;
		try {
			if (enumMap == null) {
				enumMap = new HashMap();
				String[] keys = enumKeys.split(splitSign);
				String[] values = enumValues.split(splitSign);
				for (int i = 0; i < keys.length; i++) {
					enumMap.put(keys[i], values[i]);
				}
			}
			if (enumMap.get(key) == null) {
				if (other == null)
					return key;
				else
					return other;
			} else
				return enumMap.get(key);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("枚举匹配错误!key=" + key, e.fillInStackTrace());
		}
		return result;
	}

	/**
	 * @param splitSign
	 *            the splitSign to set
	 */
	public void setSplitSign(String splitSign) {
		this.splitSign = splitSign;
	}

	/**
	 * @param enumKeys
	 *            the enumKeys to set
	 */
	public void setEnumKeys(String enumKeys) {
		this.enumKeys = enumKeys;
	}

	/**
	 * @param enumValues
	 *            the enumValues to set
	 */
	public void setEnumValues(String enumValues) {
		this.enumValues = enumValues;
	}

	/**
	 * @param other
	 *            the other to set
	 */
	public void setOther(String other) {
		this.other = other;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#reset()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		// enumMap=null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public EnumConvert clone() {
		try {
			// TODO Auto-generated method stub
			return (EnumConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
