/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import java.util.List;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.convert.ConvertDataSource;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;

/**
 * @project sagacity-tools
 * @description 树节点查询
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:TreeMapConvert.java,Revision:v1.0,Date:2010-1-5
 */
@SuppressWarnings({ "rawtypes" })
public class TreeMapConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2191869759748995659L;

	/**
	 * 根父节点值，默认为-1
	 */
	private String rootPid = "-1";

	/**
	 * 树主键列
	 */
	private int idIndex;

	/**
	 * 主键名称列
	 */
	private int idNameIndex;

	/**
	 * 动态id
	 */
	private boolean dynId = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java .lang
	 * .Object)
	 */
	public Object convert(Object param) throws Exception {
		if (param != null) {
			param = super.replaceParams(param);
			String key = ConvertUtil.jsonParamSet(this, param);
			if (dynId && ConvertDataSource.getConvertedRowsData() != null) {
				Object[][] rowList;
				for (int i = 0; i < ConvertDataSource.getConvertedRowsData().size(); i++) {
					rowList = (Object[][]) ConvertDataSource.getConvertedRowsData().get(i);
					if (rowList[idNameIndex - 1][1] != null && key.equals(rowList[idNameIndex - 1][1].toString())) {
						return rowList[idIndex - 1][1];
					}
				}
			} else {
				int realIdIndex = (Integer) ConvertDataSource.getExcelColMapDataIndex().get(Integer.toString(idIndex));
				int realIdNameIndex = (Integer) ConvertDataSource.getExcelColMapDataIndex()
						.get(Integer.toString(idNameIndex));
				List rowList;
				for (int i = 0; i < ConvertDataSource.getExcelRowsData().size(); i++) {
					rowList = (List) ConvertDataSource.getExcelRowsData().get(i);
					if (rowList.get(realIdNameIndex) != null && key.equals(rowList.get(realIdNameIndex).toString())) {
						return rowList.get(realIdIndex);
					}
				}
			}
		}
		logger.error("根据父节点名称未找到树对应父节点id!");
		return rootPid;
	}

	/**
	 * @param idIndex the idIndex to set
	 */
	public void setIdIndex(String idIndex) {
		this.idIndex = Integer.valueOf(idIndex);
	}

	/**
	 * @param idIndex the idIndex to set
	 */
	public void setIdIndex(int idIndex) {
		this.idIndex = idIndex;
	}

	/**
	 * @param idNameIndex the idNameIndex to set
	 */
	public void setIdNameIndex(int idNameIndex) {
		this.idNameIndex = idNameIndex;
	}

	/**
	 * @param dynId the dynId to set
	 */
	public void setDynId(boolean dynId) {
		this.dynId = dynId;
	}

	/**
	 * @param rootPid the rootPid to set
	 */
	public void setRootPid(String rootPid) {
		this.rootPid = rootPid;
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
	public TreeMapConvert clone() {
		try {
			return (TreeMapConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
