/**
 * 
 */
package org.sagacity.tools.exceltoy.config.parse;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.model.UpdateModel;
import org.sagacity.tools.exceltoy.utils.FileUtil;
import org.sagacity.tools.exceltoy.utils.SqlUtils;

/**
 * @project sagacity-tools
 * @description update任务解析
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:UpdateTaskParse.java,Revision:v1.0,Date:2010-10-14 下午11:33:31
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UpdateTaskParse {
	/**
	 * 
	 * @todo 加载解析export任务信息，
	 * @param elts
	 */
	public static HashMap parse(List elts) {
		if (elts == null || elts.isEmpty())
			return new HashMap();
		HashMap updateTasks = new HashMap();
		Element elt;
		String dist;
		for (int i = 0; i < elts.size(); i++) {
			elt = (Element) elts.get(i);
			if (elt.attribute("active") == null || elt.attributeValue("active").equalsIgnoreCase("true")) {
				UpdateModel model = new UpdateModel();
				model.setId(ExcelToyConstants.replaceConstants(elt.attributeValue("id")));
				if (elt.attribute("datasource") != null)
					model.setDatasource(ExcelToyConstants.replaceConstants(elt.attributeValue("datasource")));
				// 是否自动提交
				if (elt.attribute("autoCommit") != null)
					model.setAutoCommit(ExcelToyConstants.replaceConstants(elt.attributeValue("autoCommit")));
				if (elt.attribute("sheet") != null)
					model.setSheet(ExcelToyConstants.replaceConstants(elt.attributeValue("sheet")));
				if (elt.attribute("loop") != null)
					model.setLoop(ExcelToyConstants.replaceConstants(elt.attributeValue("loop")));
				if (elt.attribute("loopType") != null)
					model.setLoopType(ExcelToyConstants.replaceConstants(elt.attributeValue("loopType")));
				if (elt.attribute("loopAlias") != null)
					model.setLoopAlias(ExcelToyConstants.replaceConstants(elt.attributeValue("loopAlias")));
				if (elt.attribute("loopDateType") != null)
					model.setDateType(
							ExcelToyConstants.replaceConstants(elt.attributeValue("loopDateType")).toLowerCase());
				if (elt.attribute("loopDateFormat") != null)
					model.setDateFormat(ExcelToyConstants.replaceConstants(elt.attributeValue("loopDateFormat")));
				if (elt.attribute("startRow") != null)
					model.setBeginRow(
							Integer.valueOf(ExcelToyConstants.replaceConstants(elt.attributeValue("startRow"))));
				if (elt.attribute("endRow") != null)
					model.setEndRow(Integer.valueOf(ExcelToyConstants.replaceConstants(elt.attributeValue("endRow"))));
				if (elt.attribute("startCol") != null)
					model.setBeginCol(
							Integer.valueOf(ExcelToyConstants.replaceConstants(elt.attributeValue("startCol"))));
				if (elt.attribute("endCol") != null)
					model.setEndCol(Integer.valueOf(ExcelToyConstants.replaceConstants(elt.attributeValue("endCol"))));
				if (elt.attribute("titleRow") != null)
					model.setTitleRow(
							Integer.valueOf(ExcelToyConstants.replaceConstants(elt.attributeValue("titleRow"))));
				// sql文件
				if (elt.attribute("sqlFile") != null)
					model.setSqlFile(ExcelToyConstants.replaceConstants(elt.attributeValue("sqlFile")));
				// 批量执行量
				if (elt.attribute("batchSize") != null)
					model.setBatchSize(
							new Integer(ExcelToyConstants.replaceConstants(elt.attributeValue("batchSize"))));
				// 批量sql的分割符号
				if (elt.attribute("split") != null)
					model.setSqlSplit(ExcelToyConstants.replaceConstants(elt.attributeValue("split")));

				// 判断是否采用快速预处理模式
				if (elt.attribute("prepared") != null)
					model.setPrepared(ExcelToyConstants.replaceConstants(elt.attributeValue("prepared")));
				if (elt.attribute("files") != null)
					model.setFiles(ExcelToyConstants.replaceConstants(elt.attributeValue("files")));
				// 目标路径
				if (elt.attribute("dist") != null) {
					dist = ExcelToyConstants.replaceConstants(elt.attributeValue("dist"));
					if (FileUtil.isRootPath(dist))
						model.setDist(dist);
					else
						model.setDist(FileUtil.linkPath(ExcelToyConstants.getBaseDir(), dist));
				} else
					model.setDist(ExcelToyConstants.getBaseDir());
				model.setSql(SqlUtils.clearMark(ExcelToyConstants.replaceConstants(elt.getText())).trim());
				updateTasks.put(model.getId(), model);
			}
		}
		return updateTasks;
	}
}
