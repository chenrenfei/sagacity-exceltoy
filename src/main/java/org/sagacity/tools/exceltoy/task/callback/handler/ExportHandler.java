/**
 * 
 */
package org.sagacity.tools.exceltoy.task.callback.handler;

import java.io.File;
import java.util.List;

import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.model.ExportModel;
import org.sagacity.tools.exceltoy.model.PaginationModel;
import org.sagacity.tools.exceltoy.model.TaskModel;
import org.sagacity.tools.exceltoy.model.XMLTableModel;
import org.sagacity.tools.exceltoy.task.callback.TaskExcuteHandler;
import org.sagacity.tools.exceltoy.utils.DBHelper;
import org.sagacity.tools.exceltoy.utils.ExcelUtil;
import org.sagacity.tools.exceltoy.utils.FileUtil;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 数据库表或查询语句结果导出到excel文件
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ExportHandler.java,Revision:v1.0,Date:2009-6-8 下午04:50:35
 */
@SuppressWarnings("rawtypes")
public class ExportHandler extends TaskExcuteHandler {

	private static TaskExcuteHandler me;

	public static TaskExcuteHandler getInstance() {
		if (me == null)
			me = new ExportHandler();
		return me;
	}

	private ExportModel exportModel = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.excel.task.ITask#process(java.lang.Object)
	 */
	public void doTask(TaskModel taskModel, List fileList) throws Exception {
		logger.info("开始执行导出任务:{}!", taskModel.getId());
		// excel文件最大数据行数
		long maxcnt = Long.parseLong(ExcelToyConstants.getKeyValue(ExcelToyConstants.EXPORT_SHEET_MAXCOUNT));
		exportModel = (ExportModel) this.getTask(taskModel.getId());
		if (exportModel.getTables() != null && exportModel.getTables().size() > 0) {
			XMLTableModel table;
			String sql;
			String lowcaseSql;
			for (int i = 0; i < exportModel.getTables().size(); i++) {
				table = (XMLTableModel) exportModel.getTables().get(i);
				String maxLimit = null;
				if (exportModel.getMaxLimit() != null && table.getEndRow() != null) {
					if (exportModel.getMaxLimit().compareTo(table.getEndRow()) > 0)
						maxLimit = table.getEndRow();
					else
						maxLimit = exportModel.getMaxLimit();
				} else {
					maxLimit = (exportModel.getMaxLimit() == null) ? table.getEndRow() : exportModel.getMaxLimit();
				}
				sql = table.getSql();
				if (StringUtil.isBlank(sql))
					sql = "select * from " + table.getName();
				else {
					lowcaseSql = sql.toLowerCase().trim();
					if (!lowcaseSql.startsWith("select "))
						sql = "select " + sql;
					if (!StringUtil.matches(lowcaseSql, "\\Wfrom\\W"))
						sql = sql + " from " + table.getName();
				}
				logger.info("开始执行表:{}数据导出!", table.getName());
				export(sql, maxLimit, maxcnt, exportModel.getDist(), table.getDist(), exportModel.getSheet(),
						exportModel.getBlobFile(), exportModel.getCharset());
			}
		} else {
			export(exportModel.getSql(), exportModel.getMaxLimit(), maxcnt, exportModel.getDist(), null,
					exportModel.getSheet(), exportModel.getBlobFile(), exportModel.getCharset());

		}
		logger.info("导出任务:{}完成!", taskModel.getId());
	}

	/**
	 * @todo 导出(写)excel文件
	 * @param queryStr
	 * @param maxcnt
	 * @param dist
	 * @param tableName
	 * @param sheetName
	 * @param saveBlobFile
	 * @throws Exception
	 */
	private void export(String queryStr, String maxLimit, long pageCnt, String dist, String tableName, String sheetName,
			String saveBlobFile, String charset) throws Exception {
		// 总记录数
		long recordCnt = DBHelper.getJdbcRecordCount(queryStr);
		// 最大查询记录数
		long maxCnt = (maxLimit != null && recordCnt > Long.parseLong(maxLimit)) ? Long.parseLong(maxLimit) : recordCnt;
		if (tableName == null && dist.lastIndexOf(".") == -1)
			dist += "." + ExcelToyConstants.getExcelFileSuffix();
		String exportFile = (tableName == null) ? dist
				: FileUtil.linkPath(dist, tableName + "." + ExcelToyConstants.getExcelFileSuffix());
		// 清除之前导出的文件
		clearPreExportResult(exportFile);
		// 创建父文件目录
		FileUtil.createFolder((new File(exportFile)).getParent());
		List result = null;
		if (maxCnt <= pageCnt) {
			PaginationModel pageModel = new PaginationModel();
			pageModel.setPageSize(new Long(maxCnt).intValue());
			result = DBHelper.findPageByJdbc(queryStr, pageModel).getRows();
			if (result != null && !result.isEmpty()) {
				logger.info("正在将数据写到excel文件:" + exportFile);
				ExcelUtil.writer(result, exportFile, saveBlobFile, sheetName, charset);
			}
		} else {
			logger.info("大数据量将数据分批写到多个excel文件中................");
			// 多页数据，每页输出一个excel文件
			int page = 0;
			int pageSizeLength = Long.toString(maxCnt / pageCnt + 1).length();
			String exportDetailFile;
			while (maxCnt > 0) {
				maxCnt = maxCnt - pageCnt;
				PaginationModel pageModel = new PaginationModel();
				page++;
				pageModel.setPageNo(page);
				pageModel.setPageSize(new Long(pageCnt).intValue());
				result = DBHelper.findPageByJdbc(queryStr, pageModel).getRows();
				if (result != null && result.size() > 1) {
					exportDetailFile = exportFile.substring(0, exportFile.lastIndexOf(".")) + "_"
							+ StringUtil.addLeftZero2Len(Integer.toString(page), pageSizeLength) + "."
							+ ExcelToyConstants.getExcelFileSuffix();
					logger.info("正在将数据写到excel文件:{}", exportDetailFile);
					ExcelUtil.writer(result, exportDetailFile, saveBlobFile, sheetName, charset);
				}
			}
		}
	}

	/**
	 * @todo <b>清除上次任务执行产生的结果文件，确保导出文件的纯净(上次导出10个文件，下次可能导出5个文件，导入时将不纯净)</b>
	 * @param exportFile
	 */
	public void clearPreExportResult(String exportFile) {
		String exportFileName = new File(exportFile).getName();
		String matchRegex = "(?i)^" + exportFileName.substring(0, exportFileName.lastIndexOf(".")) + "(\\_\\d*)?"
				+ "\\." + ExcelToyConstants.getExcelFileSuffix();

		// 删除匹配的文件
		FileUtil.deleteMatchedFile((new File(exportFile)).getParent(), new String[] { matchRegex });
		logger.info("完成删除任务:{}之前产生的导出结果文件:{}!", exportModel.getId(), matchRegex);
	}
}
