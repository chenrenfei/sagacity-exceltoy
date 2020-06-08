/**
 * 
 */
package org.sagacity.tools.exceltoy.config.parse;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.model.OrderTableModel;
import org.sagacity.tools.exceltoy.utils.FileUtil;

/**
 * @project sagacity-tools
 * @description 解析数据库表信息导出任务解析
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:OrderTableTaskParse.java,Revision:v1.0,Date:2010-6-8 下午01:58:52
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class OutputTablesTaskParse {
	/**
	 * 定义日志
	 */
	protected final static Logger logger = LogManager.getLogger(OutputTablesTaskParse.class);

	/**
	 * 
	 * @todo 解析输出表顺序xml文件任务的配置
	 * @param elts
	 */
	public static HashMap parse(List elts) {
		if (elts == null || elts.isEmpty())
			return null;
		HashMap orderTableTask = new HashMap();
		Element elt;
		for (int i = 0; i < elts.size(); i++) {
			elt = (Element) elts.get(i);
			if (elt.attribute("active") == null || elt.attributeValue("active").equalsIgnoreCase("true")) {
				OrderTableModel model = new OrderTableModel();
				model.setId(ExcelToyConstants.replaceConstants(elt.attributeValue("id")));
				if (elt.attribute("datasource") != null) {
					model.setDatasource(ExcelToyConstants.replaceConstants(elt.attributeValue("datasource")));
				}
				// 排列顺序
				if (elt.attribute("order") != null) {
					model.setOrder(ExcelToyConstants.replaceConstants(elt.attributeValue("order")));
				}
				String file = ExcelToyConstants.replaceConstants(elt.attributeValue("file"));
				if (file == null) {
					file = model.getId() + ".xml";
				}
				if (FileUtil.isRootPath(file)) {
					model.setOutFile(file);
				} else {
					model.setOutFile(FileUtil.linkPath(ExcelToyConstants.getBaseDir(), file));
				}
				String matchReg;
				List includeElts = elt.elements("include");
				if (includeElts != null && !includeElts.isEmpty()) {
					String[] includes = new String[includeElts.size()];
					for (int j = 0; j < includeElts.size(); j++) {
						matchReg = ExcelToyConstants
								.replaceConstants(((Element) includeElts.get(j)).attributeValue("value"));
						includes[j] = matchReg.startsWith("(?i)") ? matchReg : ("(?i)" + matchReg);
					}
					model.setIncludes(includes);
				}
				List unMatchedTableElt = elt.elements("unmatch-tables");
				if (unMatchedTableElt != null && !unMatchedTableElt.isEmpty()) {
					String[] unMatchedTables = new String[unMatchedTableElt.size()];
					for (int j = 0; j < unMatchedTableElt.size(); j++) {
						matchReg = ExcelToyConstants
								.replaceConstants(((Element) unMatchedTableElt.get(j)).attributeValue("value"));
						unMatchedTables[j] = matchReg.startsWith("(?i)") ? matchReg : ("(?i)" + matchReg);
					}
					model.setUnmatchTables(unMatchedTables);
				}

				List excludeElts = elt.elements("exclude");
				if (excludeElts != null && !excludeElts.isEmpty()) {
					String[] excludes = new String[excludeElts.size()];
					for (int j = 0; j < excludeElts.size(); j++) {
						matchReg = ExcelToyConstants
								.replaceConstants(((Element) excludeElts.get(j)).attributeValue("value"));
						excludes[j] = matchReg.startsWith("(?i)") ? matchReg : ("(?i)" + matchReg);
					}
					model.setExcludes(excludes);
				}
				orderTableTask.put(model.getId(), model);
			}
		}
		return orderTableTask;
	}
}
