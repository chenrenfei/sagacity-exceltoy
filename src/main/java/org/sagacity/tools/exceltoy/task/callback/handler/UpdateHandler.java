/**
 * 
 */
package org.sagacity.tools.exceltoy.task.callback.handler;

import java.io.File;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sagacity.tools.exceltoy.convert.ConvertDataSource;
import org.sagacity.tools.exceltoy.model.TaskModel;
import org.sagacity.tools.exceltoy.model.UpdateModel;
import org.sagacity.tools.exceltoy.task.callback.TaskExcuteHandler;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.DBHelper;
import org.sagacity.tools.exceltoy.utils.DateUtil;
import org.sagacity.tools.exceltoy.utils.EQLUtil;
import org.sagacity.tools.exceltoy.utils.ExcelUtil;
import org.sagacity.tools.exceltoy.utils.FileUtil;

/**
 * @project sagacity-tools
 * @description update任务处理器,支持执行update、insert、存储过程调用等
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:UpdateHandler.java,Revision:v1.0,Date:2010-10-14
 */
@SuppressWarnings({ "rawtypes" })
public class UpdateHandler extends TaskExcuteHandler {
	private static TaskExcuteHandler me;

	public static TaskExcuteHandler getInstance() {
		if (me == null)
			me = new UpdateHandler();
		return me;
	}

	/**
	 * 导入配置信息
	 */
	private UpdateModel updateModel;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.task.callback.TaskExcuteHandler#doTask (org
	 * .sagacity.tools.exceltoy.model.TaskModel)
	 */
	public void doTask(TaskModel taskModel, List fileList) throws Exception {
		logger.info("开始执行修改任务:{}", taskModel.getId());
		updateModel = (UpdateModel) this.getTask(taskModel.getId());

		// 找到对应任务的excel文件
		List excelFiles = (updateModel.getFiles() == null) ? null
				: FileUtil.getPathFiles(updateModel.getDist(), new String[] { updateModel.getFiles() });

		if (excelFiles == null || excelFiles.isEmpty()) {
			if (updateModel.getLoop() != null) {
				String[] loopBeginEnd = updateModel.getLoop().split(",");
				if (loopBeginEnd.length != 2) {
					logger.error("循环体格式不正确!");
					return;
				}
				String dateType = updateModel.getDateType();
				boolean subOrAdd = false;
				if (updateModel.getLoopType().equalsIgnoreCase("int")
						|| updateModel.getLoopType().equalsIgnoreCase("integer")) {
					int base = Integer.parseInt(loopBeginEnd[0]);
					int target = Integer.parseInt(loopBeginEnd[1]);
					int count = target - base;
					if (count < 0)
						subOrAdd = true;
					int totalCount = Math.abs(count);
					for (int i = 0; i < totalCount; i++)
						doUpdate(i, totalCount, subOrAdd ? Integer.toString(base - i) : Integer.toString(base + i));
				} else if (updateModel.getLoopType().equalsIgnoreCase("date")) {
					Date base = DateUtil.parseString(loopBeginEnd[0]);
					Date target = DateUtil.parseString(loopBeginEnd[1]);
					long count = 0;
					if (dateType.equals("day"))
						count = new Double(DateUtil.getIntervalDays(base, target)).intValue();
					if (dateType.equals("month"))
						count = DateUtil.getIntervalMonths(base, target);
					if (dateType.equals("year"))
						count = DateUtil.getIntervalYears(base, target);
					if (dateType.equals("second"))
						count = Double.valueOf(DateUtil.getIntervalSeconds(base, target)).longValue();
					if (dateType.equals("hour"))
						count = Double.valueOf(DateUtil.getIntervalHours(base, target)).longValue();
					if (dateType.equals("millsecond"))
						count = DateUtil.getIntervalMillSeconds(base, target);
					if (count < 0)
						subOrAdd = true;
					Date tmp = null;
					int meter = 0;
					int totalCount = Long.valueOf(Math.abs(count)).intValue();
					for (int i = 0; i < totalCount; i++) {
						meter = subOrAdd ? 0 - i : i;
						if (dateType.equals("day"))
							tmp = DateUtil.addDay(base, meter);
						if (dateType.equals("month"))
							tmp = DateUtil.addMonth(base, meter);
						if (dateType.equals("year"))
							tmp = DateUtil.addYear(base, meter);
						if (dateType.equals("second"))
							tmp = DateUtil.addSecond(base, meter);
						if (dateType.equals("hour"))
							tmp = DateUtil.addHour(base, meter);
						if (dateType.equals("millsecond"))
							tmp = DateUtil.addMilliSecond(base, meter);
						doUpdate(i, totalCount, DateUtil.formatDate(tmp, updateModel.getDateFormat()));
					}
				}
			} else
				logger.info("update task:{} excel 文件:{}为空!", updateModel.getId(), updateModel.getFiles());
			// logger.info("执行批量sql语句!");
			// DBHelper.batchSqlText(updateModel.getSql(),updateModel.getSqlSplit());
			return;
		} else {
			logger.info("excel size={}!", excelFiles.size());
			doUpdate(excelFiles);
		}
	}

	/**
	 * @todo 执行update操作
	 * @param excelFiles
	 * @throws Exception
	 */
	private void doUpdate(List excelFiles) throws Exception {
		// do eql
		List excelTitles = null;
		if (updateModel.getTitleRow() > 0)
			excelTitles = ExcelUtil.read(EQLUtil.getExcelFileSuffix(((File) excelFiles.get(0)).getName()),
					excelFiles.get(0), updateModel.getSheet(), updateModel.getTitleRow(), updateModel.getTitleRow(),
					updateModel.getBeginCol(), updateModel.getEndCol());
		/**
		 * excel文件标题列对应数据列
		 */
		HashMap excelTitleMapDataIndex = EQLUtil.mappingExcelTitleAndDataIndex(excelTitles, updateModel.getBeginCol(),
				updateModel.getEndCol());
		/**
		 * excel列对应解析后的excel数据列
		 */
		HashMap excelColMapDataIndex = EQLUtil.mappingExcelColAndDataIndex(updateModel.getBeginCol(),
				updateModel.getEndCol());
		ConvertDataSource.setExcelColMapDataIndex(excelColMapDataIndex);
		ConvertDataSource.setExcelTitleMapDataIndex(excelTitleMapDataIndex);
		int beginRow = updateModel.getBeginRow();
		if (beginRow <= updateModel.getTitleRow()) {
			logger.info("标题所在行大于数据开始行!");
			beginRow = updateModel.getTitleRow() + 1;
		}
		// 判断是否是存储过程
		boolean isStore = DBHelper.isStore(updateModel.getSql());
		List excelRowsData;
		List rowData;
		String sql = null;
		long index = 1;
		Statement state = null;
		int i = 0, j = 0;
		try {
			state = DBHelper.getConnection().createStatement();
			long fileIndex = 0;
			String fileName;
			for (i = 0; i < excelFiles.size(); i++) {
				fileIndex = 0;
				fileName = ((File) excelFiles.get(i)).getName();
				logger.info("正在执行文件:{}", fileName);
				excelRowsData = ExcelUtil.read(EQLUtil.getExcelFileSuffix(fileName), excelFiles.get(i),
						updateModel.getSheet(), beginRow, updateModel.getEndRow(), updateModel.getBeginCol(),
						updateModel.getEndCol());
				// 设置excel文件数据
				ConvertDataSource.setExcelRowsData(excelRowsData);
				// 清空主表转换后的数据
				ConvertDataSource.setConvertedRowsData(new ArrayList());
				// Integer colIndex;
				for (j = 0; j < excelRowsData.size(); j++) {
					sql = updateModel.getSql();
					rowData = (List) excelRowsData.get(j);
					ConvertDataSource.setExcelRowData(rowData);
					// 通过转换器处理sql中的值
					sql = ConvertUtil.executeConvert(sql, null, null).toString();
					// 存储过程
					if (isStore) {
						DBHelper.callStore(sql);
						logger.info("完成第:{}个修改处理!", index);
					} else {
						if (updateModel.getBatchSize() < 2) {
							DBHelper.batchSqlText(sql, null, true);
							logger.info("完成第:{}个修改处理!", index);
						} else {
							state.addBatch(sql);
							if ((fileIndex + 1) % updateModel.getBatchSize() == 0 || (j == excelRowsData.size() - 1)) {
								state.executeBatch();
								logger.info("完成第:{}个修改处理!", index);
							}
						}
					}
					fileIndex++;
					index++;
				}
			}
		} catch (Exception e) {
			logger.error("第:{}个excel文件,第:{}行执行错误，执行的sql=[{}", i, j, sql);
			e.printStackTrace();
		} finally {
			if (state != null)
				state.close();
		}
	}

	/**
	 * @todo 执行update操作
	 * @param index
	 * @param count
	 * @param aliasValue
	 * @throws Exception
	 */
	private void doUpdate(int index, int count, String aliasValue) throws Exception {
		// 判断是否是存储过程
		boolean isStore = DBHelper.isStore(updateModel.getSql());
		String sql = updateModel.getSql();
		Statement state = null;
		try {
			state = DBHelper.getConnection().createStatement();
			sql = sql.replaceAll("\\$\\{" + updateModel.getLoopAlias() + "\\}", aliasValue);
			logger.info("正在循环第:{}批次执行!", index);
			// 通过转换器处理sql中的值
			sql = ConvertUtil.executeConvert(sql, null, null).toString();
			// 存储过程
			if (isStore) {
				DBHelper.callStore(sql);
				logger.info("完成第:{}个修改处理!", index);
			} else {
				if (updateModel.getBatchSize() < 2) {
					DBHelper.execute(sql, false);
				} else {
					state.addBatch(sql);
					if ((index + 1) % updateModel.getBatchSize() == 0 || (index == count - 1)) {
						state.executeBatch();
						state.getConnection().commit();
						logger.info("完成第:{}个修改处理!", index);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (state != null)
				state.close();
		}
	}
}
