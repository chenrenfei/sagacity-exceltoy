/**
 * 
 */
package org.sagacity.tools.exceltoy.task.callback.handler;

import java.util.List;

import org.sagacity.tools.exceltoy.config.XMLConfigLoader;
import org.sagacity.tools.exceltoy.model.EqlWrapModel;
import org.sagacity.tools.exceltoy.model.TableColumnMeta;
import org.sagacity.tools.exceltoy.model.TaskModel;
import org.sagacity.tools.exceltoy.task.callback.TaskExcuteHandler;
import org.sagacity.tools.exceltoy.utils.DBHelper;
import org.sagacity.tools.exceltoy.utils.EQLUtil;
import org.sagacity.tools.exceltoy.utils.ExcelUtil;

/**
 * @project sagacity-tools
 * @description 辅助生成eql语句的任务
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:EQLWrapHandler.java,Revision:v1.0,Date:2010-8-8 下午09:38:51
 */
public class EQLWrapHandler extends TaskExcuteHandler {
	private static TaskExcuteHandler me;

	public static TaskExcuteHandler getInstance() {
		if (me == null) {
			me = new EQLWrapHandler();
		}
		return me;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.task.callback.TaskExcuteHandler#doTask(org
	 * .sagacity.tools.exceltoy.model.TaskModel)
	 */
	public void doTask(TaskModel taskModel, List fileList) throws Exception {
		logger.info("开始执行Eql语句辅助生成任务：" + taskModel.getId());
		EqlWrapModel eqlWrapModel = (EqlWrapModel) this.getTask(taskModel.getId());
		StringBuilder eqlQuery = new StringBuilder();
		eqlQuery.append("\r\n(");
		// excel
		if (eqlWrapModel.getFile() != null) {
			List excelTitle = ExcelUtil.read(EQLUtil.getExcelFileSuffix(eqlWrapModel.getFile()), eqlWrapModel.getFile(),
					eqlWrapModel.getSheet(), new Integer(eqlWrapModel.getTitleRow()),
					new Integer(eqlWrapModel.getTitleRow()), new Integer(eqlWrapModel.getBeginCol()),
					new Integer(eqlWrapModel.getEndCol()));
			if (excelTitle != null && !excelTitle.isEmpty()) {
				List titles = (List) excelTitle.get(0);
				boolean first = true;
				for (int i = 0; i < titles.size(); i++) {
					if (titles.get(i) != null) {
						if (first) {
							eqlQuery.append("${" + titles.get(i).toString() + "}");
						} else {
							eqlQuery.append(",${" + titles.get(i).toString() + "}");
						}
						first = false;
					}
				}
			}
		}
		eqlQuery.append(")\r\n into " + (eqlWrapModel.getTable() == null ? "xxx" : eqlWrapModel.getTable()));
		// table fileds
		eqlQuery.append(" (");
		if (eqlWrapModel.getTable() != null) {
			List colMates = DBHelper.getTableColumnMetaList(eqlWrapModel.getTable());
			TableColumnMeta colMeta;
			for (int i = 0; i < colMates.size(); i++) {
				colMeta = (TableColumnMeta) colMates.get(i);
				if (i != 0) {
					eqlQuery.append(",");
				}
				eqlQuery.append(colMeta.getColName());
			}
		}
		eqlQuery.append(")\r\n");
		XMLConfigLoader.writeEql(taskModel.getId(), eqlQuery.toString());
		logger.info("Eql辅助生成任务：" + taskModel.getId() + "执行完成!");
	}
}
