/**
 * 
 */
package org.sagacity.tools.exceltoy.task.callback;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagacity.tools.exceltoy.model.TaskModel;

/**
 * @project sagacity-tools
 * @description 任务执行基础类
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:TaskExcuteHandler.java,Revision:v1.0,Date:2009-12-30
 */
public abstract class TaskExcuteHandler {
	private HashMap tasks = new HashMap();
	/**
	 * 定义日志
	 */
	protected final Logger logger = LogManager.getLogger(getClass());

	/**
	 * @todo 执行任务
	 * @param taskModel
	 * @param excelFiles
	 * @throws Exception
	 */
	public abstract void doTask(TaskModel taskModel, List excelFiles) throws Exception;

	/**
	 * @param tasks
	 *            the tasks to set
	 */
	public void setTasks(HashMap tasks) {
		this.tasks = tasks;
	}

	/**
	 * @return the tasks
	 */
	public HashMap getTasks() {
		return tasks;
	}

	public Object getTask(String taskId) {
		return tasks.get(taskId);
	}
}
