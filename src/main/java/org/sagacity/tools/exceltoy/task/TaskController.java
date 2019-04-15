/**
 * 
 */
package org.sagacity.tools.exceltoy.task;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.config.XMLConfigLoader;
import org.sagacity.tools.exceltoy.model.TaskModel;
import org.sagacity.tools.exceltoy.plugin.ExcelToySpringContext;
import org.sagacity.tools.exceltoy.task.callback.handler.EQLWrapHandler;
import org.sagacity.tools.exceltoy.task.callback.handler.ExportHandler;
import org.sagacity.tools.exceltoy.task.callback.handler.ImportHandler;
import org.sagacity.tools.exceltoy.task.callback.handler.OutputTablesHandler;
import org.sagacity.tools.exceltoy.task.callback.handler.UpdateHandler;
import org.sagacity.tools.exceltoy.utils.CollectionUtil;
import org.sagacity.tools.exceltoy.utils.DBHelper;
import org.sagacity.tools.exceltoy.utils.SecurityConfirm;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description excelToy任务执行控制器
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:TaskController.java,Revision:v1.0,Date:2009-5-24 下午04:24:14
 */
@SuppressWarnings("rawtypes")
public class TaskController {
	/**
	 * 定义日志
	 */
	private final static Logger logger = LogManager.getLogger(TaskController.class);

	/**
	 * 所有任务集合
	 */
	private static List taskList = new ArrayList();

	/**
	 * 任务正在执行
	 */
	public final static int STATUS_DOING = 1;

	/**
	 * 任务执行异常
	 */
	public final static int STATUS_EXCEPTION = 2;

	/**
	 * 任务执行完成
	 */
	public final static int STATUS_FINISH = 3;

	private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();

	/**
	 * @todo 总任务调度器
	 * @param dataSource
	 * @param taskFiles
	 */
	public static void execute(DataSource dataSource, Connection conn, HashMap<String, File[]> taskFiles) {
		if (taskList == null || taskList.isEmpty()) {
			logger.info("请查看配置文件,没有可执行的任务!");
			threadLocal.remove();
			return;
		}
		// update 2012-5-6,修改原本任务按照配置顺序执行机制，改为按照指定执行的任务顺序执行的方式
		List realRunTasks = null;
		String[] pointTasks = ExcelToyConstants.getPointTasks();
		if (pointTasks == null)
			realRunTasks = taskList;
		else {
			realRunTasks = new ArrayList();
			String pointTask;
			boolean matched = false;
			for (int i = 0; i < pointTasks.length; i++) {
				pointTask = pointTasks[i];
				matched = false;
				for (int j = 0; j < taskList.size(); j++) {
					if (((TaskModel) taskList.get(j)).getId().equals(pointTask)) {
						realRunTasks.add(taskList.get(j));
						matched = true;
						break;
					}
				}
				if (!matched) {
					logger.error("=====编号为:" + pointTask + "的任务不存在，请检查!======");
					threadLocal.remove();
					return;
				}
			}
			if (realRunTasks.size() != pointTasks.length) {
				logger.error("=====请检查任务配置情况，指定的任务跟配置文件中的任务不完全匹配!==========");
				threadLocal.remove();
				return;
			}
		}
		TaskModel taskModel;
		// 区分是否是exceltoy单独调用，还是通过spring集成调用
		boolean isInnerTask = (dataSource == null && conn == null);
		for (int i = 0; i < realRunTasks.size(); i++) {
			taskModel = (TaskModel) realRunTasks.get(i);
			// 高风险任务输入验证
			if (SecurityConfirm.confirm(taskModel)) {
				logger.info("=================任务id=:" + taskModel.getId() + "开始执行================");
				threadLocal.set(taskModel.getId());
				try {
					File[] files = taskFiles == null ? null : taskFiles.get(taskModel.getId());
					logger.info("使用数据库:{}",taskModel.getDatasource());
					// 为每个任务登记数据库连接
					if (isInnerTask)
						DBHelper.registConnection(taskModel.getDatasource(), taskModel.getIsolationlevel(),
								taskModel.getAutoCommit());
					else {
						DBHelper.registConnection(dataSource == null ? conn : dataSource.getConnection());
						// 设置connection的提交方式
						if (StringUtil.isNotBlank(taskModel.getAutoCommit())) {
							conn.setAutoCommit(Boolean.parseBoolean(taskModel.getAutoCommit()));
						}
					}
					if (taskModel.getType().equalsIgnoreCase("export"))
						ExportHandler.getInstance().doTask(taskModel, null);
					else if (taskModel.getType().equalsIgnoreCase("import"))
						ImportHandler.getInstance().doTask(taskModel, CollectionUtil.arrayToList(files));
					else if (taskModel.getType().equalsIgnoreCase("outputTables"))
						OutputTablesHandler.getInstance().doTask(taskModel, null);
					else if (taskModel.getType().equalsIgnoreCase("eqlwrap"))
						EQLWrapHandler.getInstance().doTask(taskModel, null);
					else if (taskModel.getType().equalsIgnoreCase("update"))
						UpdateHandler.getInstance().doTask(taskModel, null);
					// 设置任务执行状态为完成
					setTaskStatus(taskModel.getId(), STATUS_FINISH);
					XMLConfigLoader.disableTask(taskModel.getId());
					logger.info("*********************任务id=:" + taskModel.getId() + " 执行成功****************");
				} catch (Exception e) {
					ExcelToySpringContext.putMessage(e.getMessage());
					logger.error("xxxxxxxxxxxxxxxxxxxxxx任务id=:" + taskModel.getId() + " 执行失败!xxxxxxxxxxxxxxxx", e);
					e.printStackTrace();
					// 数据库事务回滚
					if (dataSource == null)
						DBHelper.rollback();
					// 设置任务执行状态
					setTaskStatus(taskModel.getId(), STATUS_EXCEPTION);
				} finally {
					if (isInnerTask) {
						DBHelper.close();
					} else {
						// spring集成conn的销毁交给spring
						if (StringUtil.isNotBlank(taskModel.getAutoCommit()))
							DBHelper.commit(Boolean.parseBoolean(taskModel.getAutoCommit()));
						else
							DBHelper.commit(false);
					}
				}
			}
		}
		threadLocal.remove();
	}

	/**
	 * @todo 提供解析器增加任务信息
	 * @param id
	 * @param type
	 * @param depends
	 * @param datasource
	 * @param autoCommit
	 * @param isolationlevel
	 * @param securityCode
	 * @param securityTip
	 */
	public static void addTask(String id, String type, String depends, String datasource, String autoCommit,
			String isolationlevel, String securityCode, String securityTip) {
		taskList.add(
				new TaskModel(id, type, depends, datasource, autoCommit, isolationlevel, securityCode, securityTip));
	}

	/**
	 * @todo 设置任务执行状态
	 * @param taskId
	 * @param status
	 */
	public static void setTaskStatus(String taskId, int status) {
		TaskModel taskModel;
		for (int i = 0; i < taskList.size(); i++) {
			taskModel = (TaskModel) taskList.get(i);
			if (taskModel.getId().equals(taskId)) {
				taskModel.setStatus(STATUS_DOING);
				break;
			}
		}
	}

	public static String getCurrentTask() {
		return (String) threadLocal.get();
	}
}
