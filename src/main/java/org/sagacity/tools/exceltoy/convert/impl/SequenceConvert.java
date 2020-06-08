/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.convert.ConvertDataSource;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.DBHelper;
import org.sagacity.tools.exceltoy.utils.IdUtil;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 产生流水号
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:SequenceConvert.java,Revision:v1.0,Date:2009-12-30
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SequenceConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2248974090935996917L;

	private HashMap seqMap = new HashMap();

	private String defaultKey = "defaultKey";

	/**
	 * 
	 */
	private String sql;

	/**
	 * 减去excel数据行数
	 */
	private boolean reduceSize = false;

	/**
	 * 起始值
	 */
	private Long initValue = new Long(0);

	/**
	 * 应用级流水
	 */
	private boolean appSeq = false;

	private String type = "nanotime";

	/**
	 * 采用uuid
	 */
	private boolean uuid = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java
	 * .lang.Object)
	 */
	public Object convert(Object key) throws Exception {
		key = super.replaceParams(key);
		// 应用级流水
		if (appSeq)
			return IdUtil.getShortNanoTimeId(null).toString();
		if (uuid)
			return IdUtil.getUUID();
		if (type.equalsIgnoreCase("nanotime"))
			return IdUtil.getNanoTimeId(null).toString();
		if (type.equalsIgnoreCase("shortnanotime"))
			return IdUtil.getShortNanoTimeId(null).toString();
		if (type.equalsIgnoreCase("uuid"))
			return IdUtil.getUUID();

		String mapKey = (key == null) ? defaultKey : key.toString();
		long sequenceNo = initValue.longValue();
		if (seqMap.get(mapKey) == null) {
			// 通过数据库获取当前最大值
			if (StringUtil.isNotBlank(sql)) {
				String realSql = sql;
				// sql中存在参数
				if (sql.indexOf("#{") != -1) {
					HashMap paramMap = ConvertUtil.parseParam(key.toString());
					Iterator keyIter = paramMap.entrySet().iterator();
					Map.Entry entry;
					while (keyIter.hasNext()) {
						entry = (Map.Entry) keyIter.next();
						realSql = StringUtil.replaceAllStr(realSql, "#{" + entry.getKey() + "}",
								entry.getValue().toString());
					}
				}
				List result = DBHelper.findByJdbcQuery(realSql, null);
				if (result != null && !result.isEmpty()) {
					// 取第一行的第一个值
					List row = (List) result.get(0);
					if (row != null && !row.isEmpty() && row.get(0) != null) {
						logger.info("sql执行max()值为:" + row.get(0));
						sequenceNo = Long.parseLong(row.get(0).toString())
								- (reduceSize ? (ConvertDataSource.getExcelRowsData() == null ? 0
										: ConvertDataSource.getExcelRowsData().size()) : 0);
					}
				}
			}
			sequenceNo++;
		} else {
			sequenceNo = ((Long) seqMap.get(mapKey)).longValue() + 1;
		}
		// 放进hashMap中
		seqMap.put(mapKey, new Long(sequenceNo));
		// logger.info("返回sequenceNo:" + sequenceNo);
		return new Long(sequenceNo);
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @return the initValue
	 */
	public Long getInitValue() {
		return initValue;
	}

	/**
	 * @param initValue the initValue to set
	 */
	public void setInitValue(Long initValue) {
		this.initValue = initValue;
	}

	/**
	 * @return the reduceSize
	 */
	public boolean isReduceSize() {
		return reduceSize;
	}

	/**
	 * @param reduceSize the reduceSize to set
	 */
	public void setReduceSize(boolean reduceSize) {
		this.reduceSize = reduceSize;
	}

	/**
	 * @return the appSeq
	 */
	public boolean isAppSeq() {
		return appSeq;
	}

	/**
	 * @param appSeq the appSeq to set
	 */
	public void setAppSeq(boolean appSeq) {
		this.appSeq = appSeq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#reset()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		seqMap.clear();
	}

	/**
	 * @return the uuid
	 */
	public boolean isUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(boolean uuid) {
		this.uuid = uuid;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public SequenceConvert clone() {
		try {
			return (SequenceConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
