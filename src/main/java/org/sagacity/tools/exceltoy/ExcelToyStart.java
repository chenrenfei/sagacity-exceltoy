/**
 * @Copyright 2009 版权归陈仁飞，ExcelToy可以直接使用但不允许抄袭改名,ExcelToy代码虽已陈旧
 * 但其所蕴含的设计理念非常具有创意,其是很多后来核心产品的算法起源
 */
package org.sagacity.tools.exceltoy;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.sagacity.tools.exceltoy.config.XMLConfigLoader;
import org.sagacity.tools.exceltoy.task.TaskController;
import org.sagacity.tools.exceltoy.utils.ClassLoaderUtil;
import org.sagacity.tools.exceltoy.utils.FileUtil;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 数据库excel导入导出以及数据库表之间数据互copy工具
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ExcelToolStart.java,Revision:v1.0,Date:2009-6-8 下午10:26:30
 */
public class ExcelToyStart {
	/**
	 * 日志参数定义文件
	 */
	private final static String logFile = "org/sagacity/tools/exceltoy/log4j2.xml";

	/**
	 * 定义日志
	 */
	private Logger logger = null;

	/**
	 * 是否执行的标志
	 */
	private boolean isRunFlag = true;

	/**
	 * 数据库驱动文件路径
	 */
	private String DB_DRIVER_FILE = "drivers/";

	/**
	 * 程序扩展类存放路径
	 */
	private final static String extPath = "ext/";

	/**
	 * 解析任务文件
	 * 
	 * @param taskFile
	 */
	private final void loadTasks() {
		if (ExcelToyConstants.DEFAULT_TASK_FILE == null) {
			logger.error("taskFile 为空,任务调用方式为:java -jar excel-imp-1.x.jar excelTask.xml");
			isRunFlag = false;
			return;
		}
		// 去掉空格
		if (ExcelToyConstants.DEFAULT_TASK_FILE.toLowerCase()
				.indexOf(".xml") != ExcelToyConstants.DEFAULT_TASK_FILE.length() - 4) {
			logger.error("请用xml格式定义任务,任务调用方式为:java -jar excel-imp-1.x.jar ***.xml");
			isRunFlag = false;
			return;
		}
		// 加载默认参数
		ExcelToyConstants.loadDefaultProperties();
		// 设置指定任务以及全局参数
		String tasks = System.getProperty("tasks");
		String globaIdentity = System.getProperty("globa.identity");
		if (StringUtil.isNotBlank(tasks))
			ExcelToyConstants.mockFilterTasks(tasks);
		if (StringUtil.isNotBlank(globaIdentity))
			ExcelToyConstants.mockGlobaIdentity(globaIdentity);

		File tasksFile = new File(ExcelToyConstants.getBaseDir(), ExcelToyConstants.DEFAULT_TASK_FILE);
		if (!tasksFile.exists()) {
			logger.info("任务文件:" + ExcelToyConstants.DEFAULT_TASK_FILE + "不存在,请正确定义相关文件");
			isRunFlag = false;
			return;
		}

		// 解析任务
		isRunFlag = XMLConfigLoader.parseTasks(false);
	}

	/**
	 * 任务调度
	 */
	private final void invoke() {
		long startTime = System.currentTimeMillis();
		if (isRunFlag == false)
			return;
		TaskController.execute(null, null, null);
		logger.info("整个任务执行时间总计:" + (new Float(System.currentTimeMillis() - startTime)).floatValue() / 1000);
	}

	/**
	 * 加载环境
	 */
	private final void init() {
		try {
			String realLogFile = logFile;
			if (realLogFile.charAt(0) == '/')
				realLogFile = realLogFile.substring(1);
			URL url = Thread.currentThread().getContextClassLoader().getResource(realLogFile);
			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(realLogFile);
			ConfigurationSource source = new ConfigurationSource(stream, url);
			Configurator.initialize(null, source);

			ClassLoaderUtil.loadJarFiles(FileUtil.getPathFiles(new File(ExcelToyConstants.getBaseDir(), DB_DRIVER_FILE),
					new String[] { "[\\w|\\-|\\.]+\\.jar$" }));
			System.out.println("..............success load jdbc drivers.................!");
			// 加载位于ext目录下的扩展功能类库
			System.out.println("Begin load extend jar path from ./ext!");
			File extPathFile = new File(ExcelToyConstants.getBaseDir(), extPath);
			if (extPathFile.exists()) {
				ClassLoaderUtil
						.loadJarFiles(FileUtil.getPathFiles(extPathFile, new String[] { "[\\w|\\-\\.]+\\.jar$" }));
				System.out.println("extend jar load success....................!");
			}

			logger = LogManager.getLogger(getClass());
			logger.info("=======================       系统提示           ==================================");
			logger.info("         Copyright @2008 SAGACITY.ORG, All Rights Reserved   chenrenfei    ");
			logger.info("                   (任何修改作者名称的行为都被视为侵权)                      ");
			logger.info("======================= SAGACITY 睿智开发团队(陈仁飞倾情提供) ===============");
			logger.info("---------------------------------------------------------------------------");
			logger.info("log4j properties is loaded");
		} catch (Exception io) {
			io.printStackTrace();
			logger.error(io.getMessage(), io);
		}
	}

	public final void submit() {
		// 加载环境信息
		init();

		// 解析任务
		loadTasks();

		// 任务调度执行
		invoke();
	}

	public static void main(String[] args) {
		// 获得任务定义文件
		// 实例化dbExcel
		ExcelToyStart excelToy = new ExcelToyStart();
		if (args != null && args.length > 0)
			ExcelToyConstants.DEFAULT_TASK_FILE = args[0];
		if (args != null && args.length > 1)
			ExcelToyConstants.BASE_DIR = args[1];
		else
			ExcelToyConstants.BASE_DIR = System.getProperty("user.dir");
		// test
		if (args == null || args.length == 0) {
			ExcelToyConstants.BASE_DIR = "D:/workspace/personal/sagacity2.0/sqltoy-orm/tools/exceltoy";
			ExcelToyConstants.DEFAULT_TASK_FILE = "exceltoyTasks.xml";

			ExcelToyConstants.mockFilterTasks("importTablesData");
		}
		excelToy.submit();
	}
}
