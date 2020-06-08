/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import java.util.Date;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.DateUtil;

/**
 * @project sagacity-tools
 * @description 日期格式转换
 * @author chenrf
 * @version id:DateFormatConvert.java,Revision:v1.0,Date:2009-12-30
 */
public class DateFormatConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1740690921534840223L;

	/**
	 * 日期格式
	 */
	private String format = "yyyy-MM-dd";

	/**
	 * 结果类型
	 */
	private String dataType = "string";

	/**
	 * 是否中文日期
	 */
	private boolean isChinaDate = false;

	/**
	 * 使用当前日期
	 */
	private boolean currentDate = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java.lang
	 * .Object)
	 */
	public Object convert(Object key) throws Exception {
		key = super.replaceParams(key);
		Date parseDate = null;
		// @dateFormat()模式
		if (null == key || key.toString().trim().equals("")) {
			// 不使用当前日期
			if (!currentDate)
				return null;
			parseDate = DateUtil.getNowTime();
		} else {
			if (key instanceof Date) {
				parseDate = (Date) key;
			} else {
				String date = ConvertUtil.jsonParamSet(this, key.toString());
				if (isChinaDate) {
					date = DateUtil.parseChinaDate(date);
				}
				parseDate = DateUtil.parseString(date);
			}
		}
		// 字符串类型
		if (dataType.equalsIgnoreCase("string") || dataType.equalsIgnoreCase("str"))
			return DateUtil.formatDate(parseDate, format);
		return parseDate;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setIsChinaDate(boolean isChinaDate) {
		this.isChinaDate = isChinaDate;
	}

	/**
	 * @param currentDate the currentDate to set
	 */
	public void setCurrentDate(boolean currentDate) {
		this.currentDate = currentDate;
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
	public DateFormatConvert clone() {
		try {
			return (DateFormatConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
