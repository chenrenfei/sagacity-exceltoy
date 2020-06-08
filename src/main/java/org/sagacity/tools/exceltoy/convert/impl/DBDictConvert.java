/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.plugin.ExcelToySpringContext;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.DBHelper;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 数据字典查询匹配
 * @author chenrf
 * @version id:DBDictConvert.java,Revision:v1.0,Date:2009-12-30
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DBDictConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8208392064418642384L;

	private HashMap dictMap = null;

	/**
	 * 是否抛出异常
	 */
	private boolean throwException = false;

	/**
	 * 错误提示
	 */
	private String message = "";

	/**
	 * sql=select name,key from table
	 */
	private String sql;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java .lang
	 * .Object)
	 */
	public Object convert(Object key) throws Exception {
		if (key == null) {
			return null;
		}
		key = super.replaceParams(key);
		// sql中存在参数
		if (sql.indexOf("#{") != -1) {
			String realSql = sql;
			HashMap paramMap = ConvertUtil.parseParam(key.toString());
			Iterator keyIter = paramMap.entrySet().iterator();
			Map.Entry entry;
			while (keyIter.hasNext()) {
				entry = (Map.Entry) keyIter.next();
				realSql = StringUtil.replaceAllStr(realSql, "#{" + entry.getKey() + "}", entry.getValue().toString());
			}
			List result = DBHelper.findByJdbcQuery(realSql, null);
			if (result == null || result.isEmpty()) {
				logger.debug("数据库取对照为空!key=" + key);
				if (throwException) {
					System.err.println("数据库取对照为空!key=[" + key + "]");
					ExcelToySpringContext.putMessage(StringUtil.replaceAllStr(message, "{0}", key.toString()));
					throw new Exception(StringUtil.replaceAllStr(message, "{0}", key.toString()));
				}
				return null;
			}
			return ((List) result.get(0)).get(0);
		} else {
			if (dictMap == null) {
				dictMap = new HashMap();
				if (StringUtil.isBlank(sql))
					return null;
				logger.info("查询数据库获得数据对照关系,提供key,value形式的map取值!");
				List result = DBHelper.findByJdbcQuery(sql, null);
				List row;
				if (!result.isEmpty()) {
					for (int i = 0; i < result.size(); i++) {
						row = (List) result.get(i);
						if (row.get(0) instanceof String) {
							dictMap.put(row.get(0).toString().trim(), row.get(1));
						} else {
							dictMap.put(row.get(0), row.get(1));
						}
					}
				}
			}
			if (dictMap.get(key.toString().trim()) == null) {
				logger.debug("数据库取对照为空!key=" + key);
				if (throwException) {
					System.err.println("数据库取对照为空!key=[" + key + "]");
					ExcelToySpringContext.putMessage(StringUtil.replaceAllStr(message, "{0}", key.toString()));
					throw new Exception(StringUtil.replaceAllStr(message, "{0}", key.toString()));
				}
			}
			return dictMap.get(key.toString().trim());
		}
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @return the throwException
	 */
	public boolean isThrowException() {
		return throwException;
	}

	/**
	 * @param throwException the throwException to set
	 */
	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#reset()
	 */
	@Override
	public void reset() {
		dictMap = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public DBDictConvert clone() {
		try {
			return (DBDictConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
