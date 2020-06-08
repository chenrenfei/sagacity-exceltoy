/**
 * 
 */
package org.sagacity.tools.exceltoy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Element;
import org.sagacity.tools.exceltoy.utils.FileUtil;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description exceltoy常量管理,用来加载和获取exceltoy定义的参数
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ExcelToyConstants.java,Revision:v1.0,Date:2009-12-30 下午04:05:57
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExcelToyConstants {

	/**
	 * 全局常量map
	 */
	private static HashMap constantMap = new HashMap();

	/**
	 * 运行时默认路径
	 */
	public static String BASE_DIR;

	/**
	 * 默认任务配置文件
	 */
	public static String DEFAULT_TASK_FILE = "exceltoy-default.xml";

	/**
	 * 系统默认的参数文件名称
	 */
	public final static String DEFAULT_CONFIG_PROPERTY = "org/sagacity/tools/exceltoy/exceltoy.properties";

	/**
	 * 系统默认转换器定义文件
	 */
	public final static String DEFAULT_CONVERT_XML = "org/sagacity/tools/exceltoy/default-convert.xml";

	/**
	 * excel后缀参数名称
	 */
	public final static String DEFAULT_EXCEL_SUFFIX_PARAM = "excel.suffix";

	/**
	 * 任务是否自动置为非激活状态
	 */
	public final static String TASK_AUTO_DISABLED = "task.auto.disabled";

	/**
	 * xml字符集
	 */
	public final static String XML_CHARSET = "xml.charset";

	/**
	 * 导出数据每个excelsheet的记录数
	 */
	public final static String EXPORT_SHEET_MAXCOUNT = "export.sheet.maxcount";

	/**
	 * 插入时是否忽视重复值
	 */
	public final static String INSERT_IGNORE = "insert.ignore.repeat";

	/**
	 * order table 模板
	 */
	public final static String ORDER_TABLE_TEMPLAT = "org/sagacity/tools/exceltoy/ordertable.ftl";

	/**
	 * 默认的excel后缀名称
	 */
	public final static String DEFAULT_EXCEL_SUFFIX_VALUE = "xls";

	/**
	 * excel标题开始引入符号，如${excelTitle}
	 */
	public static final String EXCEL_TITLE_BEGIN_MARK = "${";

	/**
	 * excel标题截止引入符号
	 */
	public static final String EXCEL_TITLE_END_MARK = "}";

	private static final String GLOBA_IDENTITY_NAME = "globa.identity";

	/**
	 * 全局唯一变量
	 */
	private static final String GLOBA_IDENTITY = "##{globa.identity}";

	private static final String GLOBA_TASK = "global.tasks";

	/**
	 * 批量提交记录数量，默认200条一个批次
	 */
	public final static String BATCH_SIZE = "batch.size";

	/**
	 * 
	 */
	public static boolean IMPORT_FILE_LOCAL = true;

	public static void loadDefaultProperties() {
		try {
			// 加载默认的参数
			Properties excelToyProps = new Properties();
			excelToyProps.load(FileUtil.getResourceAsStream(DEFAULT_CONFIG_PROPERTY));
			constantMap.putAll(excelToyProps);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @todo 获取常量信息
	 * @param key
	 * @return
	 */
	public static String getPropertyValue(String key) {
		if (StringUtil.isBlank(key))
			return key;
		// 常量格式#{xxx}模式
		if (StringUtil.matches(key.trim(), "^\\#\\{[\\w|\\.]+\\}$"))
			return (String) getKeyValue(key.substring(key.indexOf("#{") + 2, key.lastIndexOf("}")));
		if (StringUtil.matches(key.trim(), "^\\$\\{[\\w|\\.]+\\}$"))
			return (String) getKeyValue(key.substring(key.indexOf("${") + 2, key.lastIndexOf("}")));
		String value = getKeyValue(key);
		if (null != value)
			return value;
		return key;
	}

	/**
	 * @todo 获取常量信息
	 * @param key
	 * @return
	 */
	public static String getKeyValue(String key) {
		if (StringUtil.isBlank(key))
			return key;
		String value = (String) constantMap.get(key);
		if (null == value) {
			value = System.getProperty(key);
		}
		return value;
	}

	public static boolean insertIgnore() {
		String value = getKeyValue(INSERT_IGNORE);
		if (value == null)
			return false;
		return Boolean.parseBoolean(value);
	}

	/**
	 * @todo 获取excel文件默认后缀
	 * @return
	 */
	public static String getExcelFileSuffix() {
		if (getKeyValue(DEFAULT_EXCEL_SUFFIX_PARAM) != null) {
			String suffix = getKeyValue(DEFAULT_EXCEL_SUFFIX_PARAM);
			if (suffix.indexOf(".") == -1)
				return suffix;
			return suffix.substring(suffix.lastIndexOf(".") + 1);
		}
		return DEFAULT_EXCEL_SUFFIX_VALUE;
	}

	public static String getBaseDir() {
		String dir = System.getProperty("BASE_DIR");
		if (dir != null)
			return dir;
		return BASE_DIR;
	}

	/**
	 * @todo 加载xml中的参数
	 * @param paramElts
	 * @throws Exception
	 */
	public static void loadProperties(List paramElts) throws Exception {
		String guid = getKeyValue(GLOBA_IDENTITY_NAME);
		if (guid == null) {
			guid = "";
		}
		/*
		 * 加载任务配置文件中的参数，property格式<property name="" value=""> 或<property
		 * file="xxx.properties"/>
		 */
		if (paramElts != null && !paramElts.isEmpty()) {
			Element elt;
			for (int i = 0; i < paramElts.size(); i++) {
				elt = (Element) paramElts.get(i);
				if (elt.attribute("name") != null) {
					if (elt.attribute("value") != null) {
						constantMap.put(elt.attributeValue("name"), replaceConstants(
								StringUtil.replaceAllStr(elt.attributeValue("value"), GLOBA_IDENTITY, guid)));
					} else {
						constantMap.put(elt.attributeValue("name"),
								replaceConstants(StringUtil.replaceAllStr(elt.getText(), GLOBA_IDENTITY, guid)));
					}
				} else if (elt.attribute("file") != null) {
					loadPropertyFile(replaceConstants(
							StringUtil.replaceAllStr(elt.attributeValue("file"), GLOBA_IDENTITY, guid)), false);
				}
			}
		}
	}

	/**
	 * @todo 替换常量参数
	 * @param target
	 * @return
	 */
	public static String replaceConstants(String target) {
		if (constantMap == null || constantMap.size() < 1 || target == null)
			return target;
		String result = target;
		if (StringUtil.matches(result, "\\#\\{[\\w|\\.]+\\}")) {
			Iterator iter = constantMap.entrySet().iterator();
			Map.Entry entry;
			while (iter.hasNext()) {
				entry = (Map.Entry) iter.next();
				if (entry.getValue() != null) {
					result = StringUtil.replaceAllStr(result, "#{" + entry.getKey() + "}", (String) entry.getValue());
				}
			}
		}
		return result;
	}

	/**
	 * @todo 加载properties文件
	 * @param propertyFile
	 * @throws IOException
	 */
	public static void loadPropertyFile(String propertyFile, boolean classPath) throws Exception {
		if (StringUtil.isNotBlank(propertyFile)) {
			Properties excelToyProps = new Properties();
			if (classPath) {
				excelToyProps.load(FileUtil.getResourceAsStream(propertyFile));
			} else {
				File propFile;
				// 根路径
				if (FileUtil.isRootPath(propertyFile)) {
					propFile = new File(propertyFile);
				} else {
					propFile = new File(ExcelToyConstants.BASE_DIR, propertyFile);
				}
				if (!propFile.exists()) {
					throw new Exception("参数文件:" + propertyFile + "不存在请确认");
				}
				excelToyProps.load(new FileInputStream(propFile));
			}
			constantMap.putAll(excelToyProps);
		}
	}

	/**
	 * @todo 获取xml文件的字符集
	 * @return
	 */
	public static String getXMLCharSet() {
		String charset = getKeyValue(XML_CHARSET);
		if (charset == null)
			return "UTF-8";
		return charset;
	}

	/**
	 * @todo 获取数据库批量操作长度
	 * @return
	 */
	public static int getBatchSize() {
		String batchSize = getKeyValue(BATCH_SIZE);
		if (batchSize == null)
			return 200;
		return Integer.parseInt(batchSize);
	}

	/**
	 * @deprecated
	 * @todo <b>验证是否通过-Dtasks=task1,task2方式传递任务</b>
	 * @param taskId
	 * @return
	 */
	public static synchronized boolean validateTask(String taskId) {
		HashMap tasksMap = new HashMap();
		String tasks = getKeyValue(GLOBA_TASK);
		if (StringUtil.isNotBlank(tasks)) {
			System.err.println("-Dtasks=task1,task2方式传入任务为:" + tasks);
			String[] taskAry = tasks.replaceAll("\'", "").split(",");
			for (int i = 0; i < taskAry.length; i++) {
				tasksMap.put(taskAry[i], "1");
			}
			if (tasksMap.size() == 0 || tasksMap.containsKey(taskId))
				return true;
			return false;
		}
		return true;
	}

	/**
	 * @todo <b>提取制定执行的任务，用于改变以往按照任务配置顺序执行的机制，从而按指定的任务顺序执行</b>
	 * @author chenrenfei
	 * @date 2012-5-6 下午1:31:31
	 * @return
	 */
	public static synchronized String[] getPointTasks() {
		String tasks = getKeyValue(GLOBA_TASK);
		if (StringUtil.isNotBlank(tasks)) {
			System.err.println("-Dtasks=task1,task2方式传入任务为:" + tasks);
			String[] taskAry = tasks.replaceAll("\'", "").split(",");
			return taskAry;
		}
		return null;
	}

	/**
	 * @todo <b>模拟-Dtasks设置任务</b>
	 * @author chenrenfei
	 * @date 2011-11-2 下午10:25:26
	 * @param tasks
	 */
	public static void mockFilterTasks(String tasks) {
		constantMap.put(GLOBA_TASK, tasks);
	}

	public static void mockGlobaIdentity(String globaIdentity) {
		constantMap.put(GLOBA_IDENTITY_NAME, globaIdentity);
	}

	/**
	 * 重新设置参数值
	 * 
	 * @param key
	 * @param value
	 */
	public static void setProperty(String key, String value) {
		constantMap.put(key, value);
	}

}
