/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import java.util.List;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.convert.ConvertDataSource;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.DBHelper;
import org.sagacity.tools.exceltoy.utils.DateUtil;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 重置tableSequence的值
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:TableSequence.java,Revision:v1.0,Date:2010-10-15 下午03:05:53
 */
@SuppressWarnings({ "rawtypes" })
public class TableSequenceConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6389245760459639181L;

	/**
	 * 
	 */
	private String seqDate;

	/**
	 * 表名,seq key值，一般是表名
	 */
	private String keyName;

	/**
	 * 是否类风格
	 */
	private boolean classStyle = true;

	/**
	 * 前置任务设置
	 */
	private boolean preSet = false;

	/**
	 * 
	 */
	private String seqTableName = "SAG_TABLE_SEQUENCE";

	private String keyNameField = "SEQUENCE_NAME";

	private String keyValueField = "CURRENT_KEY_VALUE";

	private String dateValueField = "DATE_VALUE";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java .lang
	 * .Object)
	 */
	public Object convert(Object param) throws Exception {
		param = super.replaceParams(param);
		ConvertUtil.jsonParamSet(this, param);
		if (preSet) {
			StringBuilder queryStr = new StringBuilder("update ");
			queryStr.append(seqTableName);
			queryStr.append(" set ").append(keyValueField).append("=" + keyValueField
					+ (ConvertDataSource.getExcelRowsData() == null ? 0 : ConvertDataSource.getExcelRowsData().size()));
			queryStr.append(" where " + keyNameField);
			queryStr.append("='" + (classStyle ? StringUtil.toHumpFirstUpperCase(this.keyName) : this.keyName) + "'");
			DBHelper.execute(queryStr.toString(), true);
		} else {
			List pk = DBHelper.getTablePrimaryKeys(keyName);
			// 存在单主键
			if (pk.size() == 1) {
				String pkName = pk.get(0).toString();
				String realSeqDate = (seqDate == null)
						? ("" + DateUtil.getYear() + DateUtil.getMonth() + DateUtil.getDay())
						: DateUtil.formatDate(seqDate, "yyyyMMdd");
				String realKeyName = (classStyle ? StringUtil.toHumpFirstUpperCase(this.keyName) : this.keyName);
				String selectSql = "select * from " + this.seqTableName + " where " + keyNameField + "='" + realKeyName
						+ "'";
				String updateSql = "update " + this.seqTableName + " set " + keyValueField + "=(select max(" + pkName
						+ ") from " + this.keyName + ")," + this.dateValueField + "=" + realSeqDate + " where "
						+ keyNameField + "='" + realKeyName + "'";
				String insertSql = "insert into " + this.seqTableName + "(" + this.keyNameField + "," + keyValueField
						+ "," + this.dateValueField + ") values ('" + realKeyName + "',(select max(" + pkName
						+ ") from " + this.keyName + ")," + realSeqDate + ")";
				List result = DBHelper.findByJdbcQuery(selectSql, null);
				if (result == null || result.isEmpty())
					DBHelper.execute(insertSql, true);
				else
					DBHelper.execute(updateSql, true);
			} else {
				logger.error("主键存在零个或多个，请检查数据库连接配置中的schema、catalog或者表设计是否有问题!");
				throw new Exception("主键不存在或存在多个主键，请检查数据库连接配置中的schema、catalog或者表设计是否有问题!");
			}
		}
		return null;
	}

	/**
	 * @param seqDate
	 *            the seqDate to set
	 */
	public void setSeqDate(String seqDate) {
		this.seqDate = seqDate;
	}

	/**
	 * @param keyName
	 *            the keyName to set
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	/**
	 * @param isClassStyle
	 *            the isClassStyle to set
	 */
	public void setClassStyle(boolean classStyle) {
		this.classStyle = classStyle;
	}

	/**
	 * @param seqTableName
	 *            the seqTableName to set
	 */
	public void setSeqTableName(String seqTableName) {
		this.seqTableName = seqTableName;
	}

	/**
	 * @param keyNameField
	 *            the keyNameField to set
	 */
	public void setKeyNameField(String keyNameField) {
		this.keyNameField = keyNameField;
	}

	/**
	 * @param keyValueField
	 *            the keyValueField to set
	 */
	public void setKeyValueField(String keyValueField) {
		this.keyValueField = keyValueField;
	}

	/**
	 * @param dateValueField
	 *            the dateValueField to set
	 */
	public void setDateValueField(String dateValueField) {
		this.dateValueField = dateValueField;
	}

	/**
	 * @return the preSet
	 */
	public boolean isPreSet() {
		return preSet;
	}

	/**
	 * @param preSet
	 *            the preSet to set
	 */
	public void setPreSet(boolean preSet) {
		this.preSet = preSet;
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
	public TableSequenceConvert clone() {
		try {
			// TODO Auto-generated method stub
			return (TableSequenceConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
