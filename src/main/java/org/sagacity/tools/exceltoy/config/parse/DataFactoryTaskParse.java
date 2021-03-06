/**
 * 
 */
package org.sagacity.tools.exceltoy.config.parse;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.model.EqlWrapModel;
import org.sagacity.tools.exceltoy.utils.FileUtil;

/**
 * @project sagacity-tools
 * @description 数据模拟器任务配置解析
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:DataFactoryTaskParse.java,Revision:v1.0,Date:2012-5-6 下午1:51:38
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DataFactoryTaskParse {
	/**
	 * @todo 解析数据构造任务的配置
	 * @param elts
	 */
	public static HashMap parse(List elts) {
		if (elts == null || elts.isEmpty()) {
			return null;
		}
		HashMap dataFactoryTask = new HashMap();
		Element elt;
		for (int i = 0; i < elts.size(); i++) {
			elt = (Element) elts.get(i);
			if (elt.attribute("active") == null || elt.attributeValue("active").equalsIgnoreCase("true")) {
				EqlWrapModel model = new EqlWrapModel();
				model.setId(ExcelToyConstants.replaceConstants(elt.attributeValue("id")));
				if (elt.attribute("datasource") != null) {
					model.setDatasource(ExcelToyConstants.replaceConstants(elt.attributeValue("datasource")));
				}
				if (elt.attribute("table") != null) {
					model.setTable(ExcelToyConstants.replaceConstants(elt.attributeValue("table")));
				}
				if (elt.attribute("titleRow") != null) {
					model.setTitleRow(
							Integer.parseInt(ExcelToyConstants.replaceConstants(elt.attributeValue("titleRow"))));
				}
				if (elt.attribute("beginCol") != null) {
					model.setBeginCol(
							Integer.parseInt(ExcelToyConstants.replaceConstants(elt.attributeValue("beginCol"))));
				}
				if (elt.attribute("endCol") != null) {
					model.setEndCol(Integer.parseInt(ExcelToyConstants.replaceConstants(elt.attributeValue("endCol"))));
				}
				if (elt.attribute("sheet") != null) {
					model.setSheet(ExcelToyConstants.replaceConstants(elt.attributeValue("sheet")));
				}
				if (elt.attribute("file") != null) {
					String file = ExcelToyConstants.replaceConstants(elt.attributeValue("file"));
					if (FileUtil.isRootPath(file)) {
						model.setFile(file);
					} else {
						model.setFile(FileUtil.linkPath(ExcelToyConstants.getBaseDir(), file));
					}
				}
				dataFactoryTask.put(model.getId(), model);
			}
		}
		return dataFactoryTask;
	}
}
