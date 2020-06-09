/**
 * 
 */
package org.sagacity.tools.exceltoy.config.parse;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.model.ExportModel;
import org.sagacity.tools.exceltoy.utils.FileUtil;
import org.sagacity.tools.exceltoy.utils.SqlUtils;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 数据库表数据或者sql结果导出任务解析
 * @author chenrenfei <a href="mailto:zhongxuchen@gmail.com">联系作者</a>
 * @version id:ExportTaskParse.java,Revision:v1.0,Date:2010-6-8 下午01:57:59
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExportTaskParse {

	/**
	 * 
	 * @todo 加载解析export任务信息，
	 * @param elts
	 */
	public static HashMap parse(List elts) {
		if (elts == null || elts.isEmpty()) {
			return new HashMap();
		}
		HashMap exportTasks = new HashMap();
		Element elt;
		String dist;
		for (int i = 0; i < elts.size(); i++) {
			elt = (Element) elts.get(i);
			if (elt.attribute("active") == null || elt.attributeValue("active").equalsIgnoreCase("true")) {
				ExportModel model = new ExportModel();
				model.setId(ExcelToyConstants.replaceConstants(elt.attributeValue("id")));
				if (elt.attribute("datasource") != null) {
					model.setDatasource(ExcelToyConstants.replaceConstants(elt.attributeValue("datasource")));
				}
				// 是否自动提交
				if (elt.attribute("autoCommit") != null) {
					model.setAutoCommit(ExcelToyConstants.replaceConstants(elt.attributeValue("autoCommit")));
				}
				// 导出clob存放文件时的字符集
				if (elt.attribute("charset") != null) {
					model.setCharset(ExcelToyConstants.replaceConstants(elt.attributeValue("charset")));
				}
				if (elt.attribute("sheet") != null) {
					model.setSheet(ExcelToyConstants.replaceConstants(elt.attributeValue("sheet")));
				}
				// 最大记录限制
				if (elt.attribute("maxLimit") != null) {
					model.setMaxLimit(ExcelToyConstants.replaceConstants(elt.attributeValue("maxLimit")));
				}
				// 删除方式
				if (elt.attribute("delete") != null) {
					model.setDelete(ExcelToyConstants.replaceConstants(elt.attributeValue("delete")));
				}
				// 目标路径
				if (elt.attribute("dist") != null) {
					dist = ExcelToyConstants.replaceConstants(elt.attributeValue("dist"));
					if (FileUtil.isRootPath(dist)) {
						model.setDist(dist);
					} else {
						model.setDist(FileUtil.linkPath(ExcelToyConstants.getBaseDir(), dist));
					}
				} else {
					model.setDist(ExcelToyConstants.getBaseDir());
				}
				model.setSql(elt.getText());
				// 数据库大数据类型字段导成excel文件时数据的存放方式
				if (elt.attribute("blobAsFile") != null) {
					model.setBlobFile(ExcelToyConstants.replaceConstants(elt.attributeValue("blobAsFile")));
				} else {
					model.setBlobFile(ExcelToyConstants.getKeyValue("export.blob.save"));
				}
				if (StringUtil.isNotBlank(model.getBlobFile())) {
					if (!FileUtil.isRootPath(model.getBlobFile())) {
						model.setBlobFile(FileUtil.linkPath(ExcelToyConstants.getBaseDir(), model.getBlobFile()));
					}
				}
				if (elt.attribute("mapping-tables") != null) {
					model.setMappingTables(ExcelToyConstants.replaceConstants(elt.attributeValue("mapping-tables")));
				} else {
					model.setSql(SqlUtils.clearMark(ExcelToyConstants.replaceConstants(elt.getText())).trim());
				}
				exportTasks.put(model.getId(), model);
			}
		}
		return exportTasks;
	}
}
