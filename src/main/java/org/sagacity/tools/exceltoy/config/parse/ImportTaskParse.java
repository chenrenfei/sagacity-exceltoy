/**
 * 
 */
package org.sagacity.tools.exceltoy.config.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.model.EqlModel;
import org.sagacity.tools.exceltoy.model.ImportModel;
import org.sagacity.tools.exceltoy.model.TransationModel;
import org.sagacity.tools.exceltoy.utils.FileUtil;
import org.sagacity.tools.exceltoy.utils.SqlUtils;

/**
 * @project sagacity-tools
 * @description 解析excel导入任务配置信息
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ImportTaskParse.java,Revision:v1.0,Date:2010-6-8 下午01:58:17
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ImportTaskParse {
	/**
	 * @todo 解析任务信息
	 * @param elts
	 */
	public static HashMap parse(List elts) {
		if (elts == null || elts.isEmpty())
			return new HashMap();
		HashMap importTasks = new HashMap();
		Element elt;
		String dist;
		for (int i = 0; i < elts.size(); i++) {
			elt = (Element) elts.get(i);
			if (elt.attribute("active") == null || elt.attributeValue("active").equalsIgnoreCase("true")) {
				ImportModel model = new ImportModel();
				model.setId(ExcelToyConstants.replaceConstants(elt.attributeValue("id")));
				if (elt.attribute("datasource") != null)
					model.setDatasource(ExcelToyConstants.replaceConstants(elt.attributeValue("datasource")));
				if (elt.attribute("charset") != null)
					model.setCharset(ExcelToyConstants.replaceConstants(elt.attributeValue("charset")));
				// 主表是否清空
				if (elt.attribute("clear") != null)
					model.setMainClear(new Boolean(ExcelToyConstants.replaceConstants(elt.attributeValue("clear")))
							.booleanValue());
				if (elt.attribute("isolationlevel") != null)
					model.setIsolationlevel(ExcelToyConstants.replaceConstants(elt.attributeValue("isolationlevel")));
				if (elt.attribute("autoCommit") != null)
					model.setAutoCommit(ExcelToyConstants.replaceConstants(elt.attributeValue("autoCommit")));
				if (elt.attribute("depends") != null)
					model.setDepends(ExcelToyConstants.replaceConstants(elt.attributeValue("depends")));
				if (elt.attribute("mapping-tables") != null)
					model.setMappingTables(ExcelToyConstants.replaceConstants(elt.attributeValue("mapping-tables")));
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

				// 启用外键约束过滤
				if (elt.attribute("fkFilter") != null)
					model.setFkFilter(new Boolean(ExcelToyConstants.replaceConstants(elt.attributeValue("fkFilter")))
							.booleanValue());
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
				if (elt.attribute("files") != null) {
					model.setFiles(ExcelToyConstants.replaceConstants(elt.attributeValue("files")));
				}
				if (elt.attribute("sheet") != null) {
					model.setSheet(ExcelToyConstants.replaceConstants(elt.attributeValue("sheet")));
				}
				if (elt.attribute("blobAsFile") != null) {
					model.setBlobFile(ExcelToyConstants.replaceConstants(elt.attributeValue("blobAsFile")));
					if (!FileUtil.isRootPath(model.getBlobFile())) {
						model.setBlobFile(FileUtil.linkPath(ExcelToyConstants.getBaseDir(), model.getBlobFile()));
					}
				}
				if (elt.attribute("reportFile") != null) {
					model.setReportFile(ExcelToyConstants.replaceConstants(elt.attributeValue("reportFile")));
					if (!FileUtil.isRootPath(model.getReportFile())) {
						model.setReportFile(FileUtil.linkPath(ExcelToyConstants.getBaseDir(), model.getReportFile()));
					}
				}
				try {
					// 解析导入前置事务
					Iterator beforeIters = elt.elementIterator("pre-do");
					List beforeList = new ArrayList();
					Element beforeElt;
					while (beforeIters.hasNext()) {
						beforeElt = (Element) beforeIters.next();
						TransationModel beforeTrans = new TransationModel();
						// 编码格式
						if (beforeElt.attribute("encoding") != null) {
							beforeTrans.setEncoding(beforeElt.attributeValue("encoding"));
						}
						// 动态定义property
						if (beforeElt.attribute("property") != null) {
							beforeTrans.setProperty(beforeElt.attributeValue("property"));
							beforeTrans.setPropertyValue(
									ExcelToyConstants.replaceConstants(beforeElt.attributeValue("value")));
						}
						if (beforeElt.attribute("file") != null) {
							beforeTrans.setSqlFile(true);
							// 文件模式默认设置为自动提交
							beforeTrans.setAutoCommit(true);
							beforeTrans.setSql(ExcelToyConstants.replaceConstants(beforeElt.attributeValue("file")));
						} else {
							beforeTrans.setSql(ExcelToyConstants.replaceConstants(beforeElt.getTextTrim()));
						}
						if (beforeElt.attribute("split") != null) {
							beforeTrans.setSplitSign(
									ExcelToyConstants.replaceConstants(beforeElt.attributeValue("split")));
						}
						if (beforeElt.attribute("autoCommit") != null) {
							beforeTrans
									.setAutoCommit(new Boolean(beforeElt.attributeValue("autoCommit")).booleanValue());
						}
						// 前置事务是否在每个excel文件导入前执行，而不是一批excel导入前
						if (beforeElt.attribute("loopBeforeMain") != null) {
							beforeTrans.setLoopBeforeMain(
									new Boolean(beforeElt.attributeValue("loopBeforeMain")).booleanValue());
						}
						beforeList.add(beforeTrans);
					}
					model.setBefores(beforeList);

					Element doElt = elt.element("do");
					if (doElt != null) {
						Element tmpElt = doElt.element("filter");
						// 设置filter
						if (tmpElt != null) {
							model.setFilter(tmpElt.getTextTrim());
							if (tmpElt.attribute("message") != null)
								model.setFilterMsg(tmpElt.attributeValue("message"));
							// 过滤条件不符合是否异常退出
							if (tmpElt.attribute("break") != null)
								model.setFilterBreak(Boolean.parseBoolean(tmpElt.attributeValue("break")));

						}
						tmpElt = doElt.element("mainTable");
						model.setMainEql(SqlUtils.clearMark(tmpElt.getText()).trim());
						// 主键
						if (tmpElt.attribute("pk") != null)
							model.setMainPK(ExcelToyConstants.replaceConstants(tmpElt.attributeValue("pk")));
						// 依据主键对数据值进行合并,非空的代替空数据
						if (tmpElt.attribute("pk-data-merge") != null) {
							model.setPkDataMerge(Boolean.parseBoolean(
									ExcelToyConstants.replaceConstants(tmpElt.attributeValue("pk-data-merge"))));
						}
						// 判断clear是否设置在主表上
						if (tmpElt.attribute("clear") != null) {
							model.setMainClear(
									new Boolean(ExcelToyConstants.replaceConstants(tmpElt.attributeValue("clear")))
											.booleanValue());
						}
						if (tmpElt.attribute("ignore-insert") != null) {
							model.setIgnoreMainInsert(new Boolean(
									ExcelToyConstants.replaceConstants(tmpElt.attributeValue("ignore-insert")))
											.booleanValue());
							if (model.isIgnoreMainInsert()) {
								model.setMainClear(false);
							}
						}
						Element subTables = doElt.element("subTables");
						if (subTables != null) {
							// 解析eql
							Iterator eqlIters = subTables.elementIterator();
							List eqlList = new ArrayList();
							Element eqlElt;
							while (eqlIters.hasNext()) {
								eqlElt = (Element) eqlIters.next();
								if (eqlElt.attribute("active") == null
										|| eqlElt.attributeValue("active").equalsIgnoreCase("true")) {
									EqlModel eqlModel = new EqlModel();
									if (eqlElt.attribute("loop-column") != null) {
										eqlModel.setLoopCol(eqlElt.attributeValue("loop-column"));
									}

									if (eqlElt.attribute("minSize") != null) {
										eqlModel.setMinSize(Integer.parseInt(eqlElt.attributeValue("minSize")));
									}

									if (eqlElt.attribute("message") != null) {
										eqlModel.setMessage(eqlElt.attributeValue("message"));
									}

									if (eqlElt.attribute("break") != null) {
										eqlModel.setBreak(Boolean.parseBoolean(eqlElt.attributeValue("break")));
									}
									if (eqlElt.attribute("split") != null) {
										eqlModel.setSplitSign(eqlElt.attributeValue("split"));
									}
									// 子表loop-column通过split分割后是否过滤空或null的值
									if (eqlElt.attribute("skipNull") != null) {
										eqlModel.setSkipNull(
												new Boolean(eqlElt.attributeValue("skipNull")).booleanValue());
									}
									// 排除过滤loop-column切割后重复的值
									if (eqlElt.attribute("skipRepeat") != null) {
										eqlModel.setSkipRepeat(
												new Boolean(eqlElt.attributeValue("skipRepeat")).booleanValue());
									}
									if (eqlElt.attribute("as") != null) {
										eqlModel.setLoopAs(eqlElt.attributeValue("as"));
									}
									if (eqlElt.attribute("clear") != null) {
										eqlModel.setClear(eqlElt.attributeValue("clear"));
									}
									// subtable eql常量替换在执行过程中处理，要优先替换主表值
									eqlModel.setEqlContent(SqlUtils.clearMark(eqlElt.getText()).trim());
									eqlList.add(eqlModel);
								}
							}
							model.setSubEqls(eqlList);
						}
					}
					// 解析导入后置事务
					Iterator endIters = elt.elementIterator("end-do");
					List endList = new ArrayList();
					Element endElt;
					while (endIters.hasNext()) {
						endElt = (Element) endIters.next();
						TransationModel endTrans = new TransationModel();
						if (endElt.attribute("split") != null) {
							endTrans.setSplitSign(ExcelToyConstants.replaceConstants(endElt.attributeValue("split")));
						}
						if (endElt.attribute("encoding") != null) {
							endTrans.setEncoding(ExcelToyConstants.replaceConstants(endElt.attributeValue("encoding")));
						}
						if (endElt.attribute("file") != null) {
							endTrans.setSqlFile(true);
							// 文件模式默认设置为自动提交
							endTrans.setAutoCommit(true);
							endTrans.setSql(ExcelToyConstants.replaceConstants(endElt.attributeValue("file")));
						} else {
							endTrans.setSql(
									SqlUtils.clearMark(ExcelToyConstants.replaceConstants(endElt.getText())).trim());
						}
						if (endElt.attribute("autoCommit") != null) {
							endTrans.setAutoCommit(
									new Boolean(ExcelToyConstants.replaceConstants(endElt.attributeValue("autoCommit")))
											.booleanValue());
						}
						if (endElt.attribute("loopAfterMain") != null) {
							endTrans.setLoopAfterMain(
									new Boolean(endElt.attributeValue("loopAfterMain")).booleanValue());
						}
						if (endElt.attribute("errorRun") != null) {
							endTrans.setErrorRun(new Boolean(endElt.attributeValue("errorRun")).booleanValue());
						}
						if (endElt.attribute("onlyError") != null) {
							endTrans.setOnlyError(new Boolean(endElt.attributeValue("onlyError")).booleanValue());
						}
						endList.add(endTrans);
					}
					model.setAfters(endList);
				} catch (Exception e) {
					e.printStackTrace();
				}
				importTasks.put(model.getId(), model);
			}
		}
		return importTasks;
	}
}
