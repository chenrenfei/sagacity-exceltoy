/**
 * 
 */
package org.sagacity.tools.exceltoy.task.callback.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.model.OrderTableModel;
import org.sagacity.tools.exceltoy.model.OutputTableModel;
import org.sagacity.tools.exceltoy.model.TableMeta;
import org.sagacity.tools.exceltoy.model.TaskModel;
import org.sagacity.tools.exceltoy.task.TaskController;
import org.sagacity.tools.exceltoy.task.callback.TaskExcuteHandler;
import org.sagacity.tools.exceltoy.utils.BeanUtil;
import org.sagacity.tools.exceltoy.utils.DBHelper;
import org.sagacity.tools.exceltoy.utils.ExcelUtil;
import org.sagacity.tools.exceltoy.utils.FileUtil;
import org.sagacity.tools.exceltoy.utils.StringUtil;
import org.sagacity.tools.exceltoy.utils.TemplateGenerator;

/**
 * @project sagacity-tools
 * @description 按依赖关系顺序产生数据库表信息到xml文件中
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:OutputTablesHandler.java,Revision:v1.0,Date:2009-12-31 上午12:30:08
 */
@SuppressWarnings("rawtypes")
public class OutputTablesHandler extends TaskExcuteHandler {

	private static TaskExcuteHandler me;

	public static TaskExcuteHandler getInstance() {
		if (me == null) {
			me = new OutputTablesHandler();
		}
		return me;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.excel.task.ITask#process(java.lang.Object)
	 */
	public void doTask(TaskModel taskModel, List fileList) throws Exception {
		OrderTableModel orderTable = (OrderTableModel) this.getTask(taskModel.getId());
		String distFile = orderTable.getOutFile();
		boolean outExcel = false;
		if (distFile.toLowerCase().indexOf(".xml") == -1) {
			outExcel = true;
		}
		List<TableMeta> orderTables = DBHelper.getTablesByOrderLink(orderTable.getIncludes(), orderTable.getExcludes());
		logger.info("总计查询出表数量:{}", orderTables.size());
		// 判断是否逆序
		if (orderTable.getOrder().equalsIgnoreCase("desc")) {
			Collections.reverse(orderTables);
		}
		// 输出excel
		if (outExcel) {
			List titleList = new ArrayList() {
				{
					add("TABLE_NAME");
					add("TABLE_SCHEMA");
					add("TABLE_TYPE");
					add("TABLE_COMMENT");
				}
			};
			List rowDatas = BeanUtil.reflectBeansToList(orderTables,
					new String[] { "tableName", "schema", "tableType", "tableRemark" });
			rowDatas.add(0, titleList);
			ExcelUtil.writer(rowDatas, distFile, null);
		} else {
			List<OutputTableModel> outputTables = new ArrayList<OutputTableModel>();
			boolean hasUnMatchedTables = (orderTable.getUnmatchTables() != null
					&& orderTable.getUnmatchTables().length > 0) ? true : false;
			for (TableMeta tableMeta : orderTables) {
				OutputTableModel outputTable = new OutputTableModel();
				outputTable.setColMetas(tableMeta.getColMetas());
				outputTable.setTableName(tableMeta.getTableName());
				outputTable.setTableRemark(tableMeta.getTableRemark());
				if (hasUnMatchedTables) {
					for (String matchReg : orderTable.getUnmatchTables()) {
						if (StringUtil.matches(tableMeta.getTableName(), matchReg)) {
							outputTable.setUnMatchTable("true");
							break;
						}
					}
				}
				outputTables.add(outputTable);
			}
			TemplateGenerator.getInstance().create(new String[] { "orderTables" }, new Object[] { outputTables },
					FileUtil.getResourceAsStream(ExcelToyConstants.ORDER_TABLE_TEMPLAT), distFile);
			logger.info("实际输出符合条件的表数量:{}", outputTables.size());
		}
		TaskController.setTaskStatus(taskModel.getId(), TaskController.STATUS_FINISH);
	}
}
