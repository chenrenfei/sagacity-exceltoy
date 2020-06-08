/**
 * 
 */
package org.sagacity.tools.exceltoy.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.config.parse.EQLWrapTaskParse;
import org.sagacity.tools.exceltoy.config.parse.ExportTaskParse;
import org.sagacity.tools.exceltoy.config.parse.ImportTaskParse;
import org.sagacity.tools.exceltoy.config.parse.OutputTablesTaskParse;
import org.sagacity.tools.exceltoy.config.parse.UpdateTaskParse;
import org.sagacity.tools.exceltoy.model.XMLTableModel;
import org.sagacity.tools.exceltoy.task.TaskController;
import org.sagacity.tools.exceltoy.task.callback.handler.DataFactoryHandler;
import org.sagacity.tools.exceltoy.task.callback.handler.EQLWrapHandler;
import org.sagacity.tools.exceltoy.task.callback.handler.ExportHandler;
import org.sagacity.tools.exceltoy.task.callback.handler.ImportHandler;
import org.sagacity.tools.exceltoy.task.callback.handler.OutputTablesHandler;
import org.sagacity.tools.exceltoy.task.callback.handler.UpdateHandler;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.DBHelper;
import org.sagacity.tools.exceltoy.utils.FileUtil;
import org.sagacity.tools.exceltoy.utils.IOUtil;
import org.sagacity.tools.exceltoy.utils.StringUtil;
import org.sagacity.tools.exceltoy.utils.XMLUtil;
import org.sagacity.tools.exceltoy.utils.callback.XMLCallbackHandler;

/**
 * @project sagacity-tools
 * @description 解析xml任务信息
 * @author chenrenfei <a href="mailto:zhongxuchen@gmail.com">联系作者</a>
 * @version id:XMLConfigLoader.java,Revision:v1.0,Date:2008-12-9 下午08:04:37
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class XMLConfigLoader {
	/**
	 * 定义日志
	 */
	private final static Logger logger = LogManager.getLogger(XMLConfigLoader.class);

	/**
	 * 用来存放所有配置文件，包含：import文件和主配置文件，在disable task时遍历处理
	 */
	private static List configXMLs = new ArrayList();

	/**
	 * 
	 * @todo 解析配置文件
	 * @param xmlConfig
	 */
	public static boolean parseTasks(boolean classPath) {
		try {
			logger.info("组合任务配置文件!");
			String fullConfigXML = XMLConfigLoader.combineXML(ExcelToyConstants.DEFAULT_TASK_FILE, classPath);
			// SAXReader saxReader = new SAXReader();
			Document doc = DocumentHelper.parseText(fullConfigXML);
			// Document doc=saxReader.read(new InputStreamReader(IOUtil
			// .String2InputStream(fullConfigXML, null)));

			Element root = doc.getRootElement();
			// 参数加载
			ExcelToyConstants.loadProperties(root.elements("property"));
			// 转换器加载
			ConvertUtil.loadConverts(root.elements("convert"));

			// 解析数据库
			if (!classPath
					|| (classPath && root.elements("datasource") != null && !root.elements("datasource").isEmpty())) {
				DBHelper.loadDatasource(root.elements("datasource"));
			}

			// 将任务加载到任务控制器中，便于主控程序中调用
			parseTasks2Controller(root.elements());

			// 解析导出顺序表关系
			OutputTablesHandler.getInstance().setTasks(OutputTablesTaskParse.parse(root.elements("outputTables")));

			// 解析导出任务
			ExportHandler.getInstance().setTasks(ExportTaskParse.parse(root.elements("export")));

			// 解析导入任务
			ImportHandler.getInstance().setTasks(ImportTaskParse.parse(root.elements("import")));

			// 解析辅助eql生成任务
			EQLWrapHandler.getInstance().setTasks(EQLWrapTaskParse.parse(root.elements("eqlwrap")));

			// 解析update任务
			UpdateHandler.getInstance().setTasks(UpdateTaskParse.parse(root.elements("update")));
			// 解析数据模拟器任务
			DataFactoryHandler.getInstance().setTasks(UpdateTaskParse.parse(root.elements("dataFactory")));

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("解析配置文件错误!请检查配置文件的格式!");
			return false;
		}
	}

	/**
	 * 
	 * @todo 解析任务，并将任务放到任务控制器中
	 * @param elts
	 */
	private static void parseTasks2Controller(List elts) {
		if (elts == null || elts.isEmpty())
			return;
		Element elt;
		String id = null;
		for (int i = 0; i < elts.size(); i++) {
			String depends = null;
			String datasource = null;
			// 自动提交
			String autoCommit = null;
			// 事务隔离级别
			String isolationlevel = null;
			String securityTip = null;
			String securityCode = null;
			elt = (Element) elts.get(i);
			id = ExcelToyConstants.replaceConstants(elt.attributeValue("id"));
			if (elt.attribute("datasource") != null)
				datasource = ExcelToyConstants.replaceConstants(elt.attributeValue("datasource"));
			if (elt.attribute("depends") != null)
				depends = ExcelToyConstants.replaceConstants(elt.attributeValue("depends"));
			if (elt.attribute("autoCommit") != null)
				autoCommit = ExcelToyConstants.replaceConstants(elt.attributeValue("autoCommit"));
			if (elt.attribute("isolationlevel") != null)
				isolationlevel = ExcelToyConstants.replaceConstants(elt.attributeValue("isolationlevel"));
			if (elt.attribute("securityWarn") != null)
				securityTip = ExcelToyConstants.replaceConstants(elt.attributeValue("securityWarn"));
			if (elt.attribute("securityCode") != null)
				securityCode = ExcelToyConstants.replaceConstants(elt.attributeValue("securityCode"));
			// 解析六类任务
			if (elt.getName().equalsIgnoreCase("import") || elt.getName().equalsIgnoreCase("export")
					|| elt.getName().equalsIgnoreCase("outputTables") || elt.getName().equalsIgnoreCase("eqlwrap")
					|| elt.getName().equalsIgnoreCase("update") || elt.getName().equalsIgnoreCase("dataFactory")) {
				// 状态必须是true
				if (elt.attribute("active") == null || (elt.attribute("active") != null
						&& ExcelToyConstants.replaceConstants(elt.attributeValue("active")).equalsIgnoreCase("true"))) {
					TaskController.addTask(id, elt.getName(), depends, datasource, autoCommit, isolationlevel,
							securityCode, securityTip);
				}
			}
		}
	}

	/**
	 * 
	 * @todo 在任务执行成功后置任务状态为active=false
	 * @param taskId
	 * @throws Exception
	 */
	public static void disableTask(final String taskId) throws Exception {
		if (!ExcelToyConstants.getKeyValue(ExcelToyConstants.TASK_AUTO_DISABLED).equals("true")) {
			logger.info("参数:task.auto.disabled==false,任务执行成功后不自动关闭!");
			return;
		}
		// 所有文件进行遍历寻找任务置任务为失效状态
		for (int i = 0; i < configXMLs.size(); i++) {
			if (XMLUtil.updateXML((File) configXMLs.get(i), ExcelToyConstants.getXMLCharSet(), false,
					new XMLCallbackHandler() {
						public Object process(Document doc, Element root) throws Exception {
							String xpath = "//" + doc.getRootElement().getName() + "/*[@id='" + taskId + "']";
							Object obj = doc.selectObject(xpath);
							if (obj != null) {
								Element elt = (Element) obj;
								if (elt.attribute("active") != null && ExcelToyConstants
										.replaceConstants(elt.attributeValue("active")).equalsIgnoreCase("true")) {
									elt.attribute("active").setData("false");
								} else {
									elt.addAttribute("active", "false");
								}
								return true;
							}
							return false;
						}
					}))
				break;
		}
	}

	/**
	 * 
	 * @todo 设置任务的内容，针对eqlwrap辅助生成eql语句
	 * @param taskId
	 * @param text
	 * @throws Exception
	 */
	public static void writeEql(final String taskId, final String text) throws Exception {
		// final String xpath = "//exceltoy/*[@id='" + taskId + "']";
		// 所有文件进行遍历寻找任务置任务为失效状态
		for (int i = 0; i < configXMLs.size(); i++) {
			if (XMLUtil.updateXML((File) configXMLs.get(i), ExcelToyConstants.getXMLCharSet(), false,
					new XMLCallbackHandler() {
						public Object process(Document doc, Element root) throws Exception {
							String xpath = "//" + doc.getRootElement().getName() + "/*[@id='" + taskId + "']";
							Object obj = doc.selectObject(xpath);
							if (obj != null) {
								Element elt = (Element) obj;
								elt.clearContent();
								elt.addCDATA(text);
								return true;
							}
							return false;
						}
					}))
				break;
		}
	}

	/**
	 * @todo 将所有include文件和系统默认convert配置文件组合成单个配置文件 便于统一的解析处理
	 * @param xmlConfig
	 * @param classPath
	 * @return
	 * @throws Exception
	 */
	public static String combineXML(String xmlConfig, boolean classPath) throws Exception {
		String parentPath = ExcelToyConstants.getBaseDir();
		InputStream ifile = null;
		if (!classPath) {
			File cfgFile = new File(parentPath, xmlConfig);
			if (!cfgFile.exists()) {
				cfgFile = new File(xmlConfig);
			}
			if (!cfgFile.exists()) {
				logger.error("任务配置文件:" + xmlConfig + "不存在,请检查确认");
				throw new Exception("主任务配置文件不存在!");
			}
			// 放入文件列表中
			configXMLs.add(cfgFile);
			ifile = new FileInputStream(cfgFile);
		} else {
			ifile = FileUtil.getResourceAsStream(xmlConfig);
		}
		SAXReader saxReader = new SAXReader();
		InputStreamReader ir = null;
		if (ExcelToyConstants.getXMLCharSet() != null) {
			ir = new InputStreamReader(ifile, ExcelToyConstants.getXMLCharSet());
		} else {
			ir = new InputStreamReader(ifile);
		}
		saxReader.setEncoding(ExcelToyConstants.getXMLCharSet());
		Document doc = saxReader.read(ir);
		Element root = doc.getRootElement();
		String rootNodeName = root.getName();
		StringBuilder fullXML = new StringBuilder("<?xml version=\"1.0\" encoding=\"" + doc.getXMLEncoding() + "\"?>");
		fullXML.append("<" + rootNodeName + ">");
		fullXML.append(IOUtil.inputStream2String(FileUtil.getResourceAsStream(ExcelToyConstants.DEFAULT_CONVERT_XML),
				doc.getXMLEncoding()));
		Element elt;
		List sonElements = root.elements();
		for (int i = 0; i < sonElements.size(); i++) {
			elt = (Element) sonElements.get(i);
			if (elt.getName().equalsIgnoreCase("include")) {
				addImportFileXML(fullXML, parentPath, elt.attributeValue("file"), classPath);
			} else {
				fullXML.append(elt.asXML());
			}
		}
		fullXML.append("</" + rootNodeName + ">");
		return fullXML.toString();
	}

	/**
	 * @todo 处理import文件，将其内容加入到主配置文件中
	 * @param fullXML
	 * @param parentPath
	 * @param xmlFile
	 * @param classPath
	 * @throws Exception
	 */
	private static void addImportFileXML(StringBuilder fullXML, String parentPath, String xmlFile, boolean classPath)
			throws Exception {
		if (StringUtil.isBlank(xmlFile))
			return;
		InputStream ifile = null;
		if (!classPath) {
			File cfgFile = new File(parentPath, xmlFile);
			if (!cfgFile.exists())
				cfgFile = new File(xmlFile);
			if (!cfgFile.exists()) {
				logger.error("文件:" + xmlFile + "不存在!");
				return;
			}
			// 放入文件列表中
			configXMLs.add(cfgFile);
			ifile = new FileInputStream(cfgFile);
		} else {
			ifile = FileUtil.getResourceAsStream(xmlFile);
		}
		SAXReader saxReader = new SAXReader();
		InputStreamReader ir = null;
		if (ExcelToyConstants.getXMLCharSet() != null) {
			ir = new InputStreamReader(ifile, ExcelToyConstants.getXMLCharSet());
		} else {
			ir = new InputStreamReader(ifile);
		}
		saxReader.setEncoding(ExcelToyConstants.getXMLCharSet());
		Document doc = saxReader.read(ir);
		Element root = doc.getRootElement();
		Element elt;
		List sonElements = root.elements();
		if (sonElements != null && !sonElements.isEmpty()) {
			for (int i = 0; i < sonElements.size(); i++) {
				elt = (Element) sonElements.get(i);
				fullXML.append(elt.asXML());
			}
		}
	}

	/**
	 * 
	 * @todo 解析由orderTable任务产生的数据库表信息
	 * @param id
	 * @param mappingTables
	 * @return
	 */
	public static List parseMappingTables(String id, String mappingTables) {
		List tables = new ArrayList();
		if (StringUtil.indexOfIgnoreCase(mappingTables, ".xml") != -1) {
			File mapXml = new File(ExcelToyConstants.getBaseDir(), mappingTables);
			if (!mapXml.exists()) {
				mapXml = new File(mappingTables);
			}
			if (!mapXml.exists()) {
				logger.error("excel导出任务:" + id + "属性mappint-tables对应的文件不存在!");
			} else {
				try {
					Object expTables = XMLUtil.getXPathElement(mapXml, "//tables/table[@active='true']");
					List tablesElt = null;
					if (expTables instanceof List) {
						tablesElt = (List) expTables;
					} else {
						if (expTables != null) {
							tablesElt = new ArrayList();
							tablesElt.add(expTables);
						}
					}
					if (tablesElt != null && !tablesElt.isEmpty()) {
						Element tableElt;
						for (int i = 0; i < tablesElt.size(); i++) {
							XMLTableModel table = new XMLTableModel();
							tableElt = (Element) tablesElt.get(i);
							table.setName(ExcelToyConstants.replaceConstants(tableElt.attributeValue("name")));
							table.setDist(ExcelToyConstants.replaceConstants(tableElt.attributeValue("dist")));
							if (tableElt.attribute("fkFilter") != null) {
								table.setFkFilter(new Boolean(
										ExcelToyConstants.replaceConstants(tableElt.attributeValue("fkFilter")))
												.booleanValue());
							}
							if (tableElt.attribute("endRow") != null) {
								table.setEndRow(ExcelToyConstants.replaceConstants(tableElt.attributeValue("endRow")));
							}
							if (tableElt.attribute("fieldMapping") != null) {
								String mapping = ExcelToyConstants
										.replaceConstants(tableElt.attributeValue("fieldMapping"));
								String[] mappings = mapping.split("\\;");
								HashMap<String, String> fieldMapping = new HashMap<String, String>();
								String[] keyValue;
								for (int j = 0; j < mappings.length; j++) {
									keyValue = mappings[j].split("\\:");
									fieldMapping.put(keyValue[0].trim(), keyValue[1].trim());
								}
								table.setFieldMapping(fieldMapping);
							}
							if (StringUtil.isNotBlank(tableElt.getTextTrim())) {
								table.setSql(StringUtil.clearMistyChars(tableElt.getTextTrim(), " "));
							}
							tables.add(table);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("解析excel导出任务:" + id + "对应的xml文件:" + mappingTables + "失败!");
				}
			}
		} else if (StringUtil.isNotBlank(mappingTables)) {
			String[] tablesAry = mappingTables.split(",");
			for (int i = 0; i < tablesAry.length; i++) {
				XMLTableModel table = new XMLTableModel();
				table.setName(tablesAry[i]);
				table.setDist(tablesAry[i]);
				tables.add(table);
			}
		}
		return tables;
	}
}
