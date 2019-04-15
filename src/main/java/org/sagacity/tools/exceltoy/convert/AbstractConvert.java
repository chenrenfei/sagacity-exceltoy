/**
 * 
 */
package org.sagacity.tools.exceltoy.convert;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.utils.EQLUtil;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 数据转换器接口定义
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:AbstractConvert.java,Revision:v1.0,Date:2009-12-30
 */
public abstract class AbstractConvert implements Serializable, java.lang.Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3653060703303231419L;
	/**
	 * 定义日志
	 */
	protected final Logger logger = LogManager.getLogger(getClass());

	/**
	 * @todo 数据转换
	 * @param param
	 * @return
	 */
	public abstract Object convert(Object param) throws Exception;

	public abstract void reset();

	public Object replaceParams(Object param) throws Exception {
		if (param == null)
			return null;
		if (param instanceof String) {
			String paramValue = param.toString();
			paramValue = EQLUtil.replaceHolder(paramValue, EQLUtil.parseExcelFields(paramValue));
			// 使用#[parentTable_field]参数，则用主表的数据替换相关主表字段占位符号，其占位符号跟常量一致
			if (ConvertDataSource.getMainTableRowData() != null && ConvertDataSource.getMainTableRowData().length > 0) {
				for (int i = 0; i < ConvertDataSource.getMainTableRowData().length; i++) {
					paramValue = StringUtil.replaceAllStr(paramValue,
							"#{" + ConvertDataSource.getMainTableRowData()[i][0].toString() + "}",
							ConvertDataSource.getMainTableRowData()[i][1].toString());

				}
			}
			// 常量替换
			paramValue = ExcelToyConstants.replaceConstants(paramValue);
			return paramValue;
		}
		return param;
	}
}
