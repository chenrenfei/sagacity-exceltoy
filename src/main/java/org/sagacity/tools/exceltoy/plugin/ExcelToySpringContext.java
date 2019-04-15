/**
 * 
 */
package org.sagacity.tools.exceltoy.plugin;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.config.XMLConfigLoader;
import org.sagacity.tools.exceltoy.task.TaskController;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * @project sagacity-tools
 * @description 跟Spring以及其它框架集成
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ExcelToySpringContext.java,Revision:v1.0,Date:2012-1-5
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExcelToySpringContext {
	private final static Logger logger = LogManager.getLogger(ExcelToySpringContext.class);

	/**
	 * 存放导入任务的错误信息
	 */
	private static ThreadLocal<HashMap<String, HashMap>> threadLocal = new ThreadLocal<HashMap<String, HashMap>>();

	/**
	 * 配置参数
	 */
	private String property = null;

	/**
	 * 数据库
	 */
	private DataSource dataSource;

	/**
	 * 任务配置
	 */
	private String taskConfig;

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the taskConfig
	 */
	public String getTaskConfig() {
		return taskConfig;
	}

	/**
	 * @param taskConfig
	 *            the taskConfig to set
	 */
	public void setTaskConfig(String taskConfig) {
		this.taskConfig = taskConfig;
	}

	/**
	 * @todo <b>初始化</b>
	 * @author chenrenfei
	 * @date 2012-1-5 下午06:05:33
	 */
	public void initialize() throws Exception {
		// 初始化参数
		ExcelToyConstants.loadDefaultProperties();
		ExcelToyConstants.loadPropertyFile(this.property, true);
		// 设置任务文件
		ExcelToyConstants.DEFAULT_TASK_FILE = this.taskConfig;
		ExcelToyConstants.IMPORT_FILE_LOCAL = false;
		if (!XMLConfigLoader.parseTasks(true))
			throw new Exception("ExcelToy工具加载任务配置文件错误,请检查!");
	}

	/**
	 * @todo 执行指定全局id的任务
	 * @param dataSource
	 * @param globaIdentity
	 * @param tasks
	 * @param excelIS
	 */
	public HashMap doTask(DataSource dataSource, String globaIdentity, String[] tasks,
			HashMap<String, File[]> taskFiles) throws Exception {
		this.reloadConvert();
		ExcelToyConstants.mockGlobaIdentity(globaIdentity);
		StringBuilder taskBuffer = new StringBuilder();
		for (int i = 0; i < tasks.length; i++) {
			if (i > 0)
				taskBuffer.append(",");
			taskBuffer.append(tasks[i]);
		}
		ExcelToyConstants.mockFilterTasks(taskBuffer.toString());
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			TaskController.execute(null, conn, taskFiles);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
			// conn = null;
		}
		HashMap message = getTaskMessage(null);
		clearMessage();
		return message;
	}

	/**
	 * @todo 执行多个任务并指定执行文件
	 * @param dataSource
	 * @param tasks
	 * @param taskFiles
	 * @return
	 * @throws Exception
	 */
	public HashMap doTask(DataSource dataSource, String[] tasks, HashMap<String, File[]> taskFiles) throws Exception {
		this.reloadConvert();
		StringBuilder taskBuffer = new StringBuilder();
		for (int i = 0; i < tasks.length; i++) {
			if (i > 0)
				taskBuffer.append(",");
			taskBuffer.append(tasks[i]);
		}
		ExcelToyConstants.mockFilterTasks(taskBuffer.toString());
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			TaskController.execute(null, conn, taskFiles);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
			// conn = null;
		}
		HashMap message = getTaskMessage(null);
		clearMessage();
		return message;
	}

	/**
	 * @todo <b>执行单个excel文件导入功能</b>
	 * @author chenrenfei
	 * @param dataSource
	 * @param task
	 * @param excelFile
	 */
	public HashMap doTask(DataSource dataSource, final String task, final File excelFile) throws Exception {
		this.reloadConvert();
		if (excelFile != null)
			logger.warn("执行导入任务:" + task + ";文件长度=" + excelFile.length());
		ExcelToyConstants.mockFilterTasks(task);
		Connection conn = DataSourceUtils.getConnection(dataSource == null ? this.dataSource : dataSource);
		try {
			TaskController.execute(null, conn, new HashMap() {
				{
					put(task, new File[] { excelFile });
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
			// conn = null;
		}
		HashMap message = getTaskMessage(task);
		clearMessage();
		return message;
	}

	/**
	 * @todo 设置导入任务中的常量属性的值
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		ExcelToyConstants.setProperty(key, value);
	}

	/**
	 * @todo <b>清除消息</b>
	 */
	private void clearMessage() {
		threadLocal.remove();
	}

	/**
	 * @todo <b>增加错误信息</b>
	 * @author chenrenfei
	 * @date 2012-1-11 下午05:32:43
	 * @param taskId
	 * @param message
	 */
	public static void putMessage(final String message) {
		if (!ExcelToyConstants.IMPORT_FILE_LOCAL) {
			Object obj = threadLocal.get();
			String taskId = TaskController.getCurrentTask();
			if (obj == null) {
				HashMap hash = new HashMap();
				hash.put(taskId, new HashMap() {
					{
						put(message, message);
					}
				});
				threadLocal.set(hash);
			} else {
				HashMap hash = (HashMap) obj;
				if (hash.get(taskId) == null) {
					hash.put(taskId, new HashMap() {
						{
							put(message, message);
						}
					});
				} else {
					((HashMap) hash.get(taskId)).put(message, message);
				}
			}
		}
	}

	public HashMap getTaskMessage(String taskId) {
		HashMap obj = threadLocal.get();
		if (obj != null) {
			if (taskId != null)
				return (HashMap) obj.get(taskId);
			else
				return obj;
		}
		return null;
	}

	public void reloadConvert() {
		// 转换器加载
		ConvertUtil.resetConvert();
	}
}
