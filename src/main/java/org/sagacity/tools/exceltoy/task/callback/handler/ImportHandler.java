/**
 * 
 */
package org.sagacity.tools.exceltoy.task.callback.handler;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.convert.ConvertDataSource;
import org.sagacity.tools.exceltoy.model.EqlModel;
import org.sagacity.tools.exceltoy.model.EqlParseResult;
import org.sagacity.tools.exceltoy.model.ImportModel;
import org.sagacity.tools.exceltoy.model.TableColumnMeta;
import org.sagacity.tools.exceltoy.model.TaskModel;
import org.sagacity.tools.exceltoy.model.TransationModel;
import org.sagacity.tools.exceltoy.model.XMLTableModel;
import org.sagacity.tools.exceltoy.plugin.ExcelToySpringContext;
import org.sagacity.tools.exceltoy.task.callback.TaskExcuteHandler;
import org.sagacity.tools.exceltoy.utils.CollectionUtil;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.DBHelper;
import org.sagacity.tools.exceltoy.utils.DataSourceUtils.DBType;
import org.sagacity.tools.exceltoy.utils.EQLUtil;
import org.sagacity.tools.exceltoy.utils.ExcelUtil;
import org.sagacity.tools.exceltoy.utils.FileUtil;
import org.sagacity.tools.exceltoy.utils.SqlUtils;
import org.sagacity.tools.exceltoy.utils.StringUtil;

import bsh.Interpreter;

/**
 * @project sagacity-tools
 * @description excel文件导入数据库,导入分两类：1、先通过导出任务产生数据和表映射xml文件，
 *              再通过映射文件找到相关excel文件导入数据库 ； 2、写eql语句导入
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ImportHandler.java,Revision:v1.0,Date:2009-6-8 下午04:51:21
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ImportHandler extends TaskExcuteHandler {
	private static TaskExcuteHandler me;

	/**
	 * 实际导入数据量
	 */
	private long realRecordCnt = 0;

	private long batchCount = 0;

	/**
	 * 数据库preparedStatment map，key:insertSql
	 */
	private HashMap pstMap = new HashMap();

	/**
	 * 主任务数据库statement
	 */
	private PreparedStatement mainPst = null;

	/**
	 * 导入配置信息
	 */
	private ImportModel importModel;

	public static TaskExcuteHandler getInstance() {
		if (me == null)
			me = new ImportHandler();
		return me;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.excel.task.ITask#process(java.lang.Object)
	 */
	public synchronized void doTask(TaskModel taskModel, List fileList) throws Exception {
		// 重置初始化参数
		realRecordCnt = 0;
		batchCount = 0;
		pstMap = new HashMap();
		mainPst = null;
		logger.info("开始执行导入任务:{}", taskModel.getId());
		importModel = (ImportModel) this.getTask(taskModel.getId());
		/**
		 * 通过映射配置文件导入数据库，一般用于outputTables/export两种任务组合使用产生的表和excel文件的导入
		 */
		List tablesMap = importModel.getTablesMap();
		if (tablesMap != null && tablesMap.size() > 0) {
			doImportTableExcelByXML(tablesMap, importModel.getCharset());
		} else
		// eql模式单表处理
		{
			// 找到对应任务的excel文件
			List excelFiles = null;
			if (ExcelToyConstants.IMPORT_FILE_LOCAL) {
				excelFiles = (importModel.getFiles() == null) ? null
						: FileUtil.getPathFiles(importModel.getDist(), new String[] { importModel.getFiles() });
				logger.info("共有:{}文件需要导入!", (excelFiles == null ? 0 : excelFiles.size()));
			} else {
				excelFiles = fileList;
			}
			if (excelFiles == null || excelFiles.isEmpty()) {
				logger.info("import task:{} excel 文件{}为空!", importModel.getId(), importModel.getFiles());
				return;
			}
			doEqlTask(excelFiles, importModel.getCharset());
		}
	}

	/**
	 * @todo 处理export任务生成的excel数据并依照export过程中产生的用于存放表对照关系的xml文件， 将数据导入到新的数据库中
	 * @param tablesMap
	 * @throws Exception
	 */
	private void doImportTableExcelByXML(List tablesMap, String charset) throws Exception {
		int dbType = DBHelper.getDBDialect();
		String tableName = "";
		XMLTableModel table;
		HashMap fieldExcelTitleMap;
		// do pre-do 前置事务
		preTransation(false);

		// 清空记录
		if (importModel.getMainClear()) {
			for (int k = tablesMap.size() - 1; k >= 0; k--) {
				table = (XMLTableModel) tablesMap.get(k);
				DBHelper.execute("delete from " + table.getDist(), true);
			}
		}
		List importReport = null;
		boolean isReport = StringUtil.isBlank(importModel.getReportFile()) ? false : true;
		if (isReport) {
			importReport = new ArrayList();
			importReport.add(new ArrayList() {
				{
					add("Table Name");
					add("Excel Count");
					add("Now Table Count");
				}
			});
		}
		long tableImportCnt = 0;
		// 执行批量表导入
		boolean hasAutoIncrementField;
		for (int i = 0; i < importModel.getTablesMap().size(); i++) {
			try {
				tableImportCnt = 0;
				table = (XMLTableModel) importModel.getTablesMap().get(i);
				fieldExcelTitleMap = table.getFieldMapping();
				tableName = table.getDist();
				logger.info("正在开始向表:{}导入数据!", tableName);
				HashMap<String, TableColumnMeta> fieldsMap = DBHelper.getTableColumnMeta(tableName);
				// 判断是否存在自增字段
				hasAutoIncrementField = hasAutoIncrementField(fieldsMap);
				// 表外键信息
				HashMap fkMaps = null;
				if (table.isFkFilter())
					fkMaps = DBHelper.getTableImpForeignKeys(tableName);
				// 找到对应表的excel文件
				List excelFiles = FileUtil.getPathFiles(importModel.getDist(), new String[] {
						"(?i)^" + tableName + "(\\_\\d*)?." + ExcelToyConstants.getExcelFileSuffix() + "$" });
				if (excelFiles == null || excelFiles.isEmpty()) {
					logger.info("任务:{}中的表:{}没有对应的excel文件!", importModel.getId(), tableName);
				} else {
					String fileName;
					for (int j = 0; j < excelFiles.size(); j++) {
						fileName = ((File) excelFiles.get(j)).getName();
						logger.info("正在处理文件:{}", fileName);
						List excelData = ExcelUtil.read(EQLUtil.getExcelFileSuffix(fileName), excelFiles.get(j), null,
								1, null, 1, null);
						// 表头对应的数据库字段信息
						List fields = (List) excelData.get(0);
						if (fields.size() != fieldsMap.size())
							logger.error("数据库表:{}的字段数量和excel文件:{}字段数量不符!", tableName, excelFiles.get(j));
						// 去除表头
						excelData.remove(0);

						// 记录数量累加
						tableImportCnt += excelData.size();
						StringBuilder insertSql = new StringBuilder();
						StringBuilder values = new StringBuilder();
						// 外键字段列
						String[][] fkFieldsIndex = null;
						if (fkMaps != null)
							fkFieldsIndex = new String[fkMaps.size()][3];
						int count = 0;
						String fieldName;
						// 构造insert 插入语句
						Map.Entry entry;
						HashMap filedIndexMap = new HashMap();
						for (int k = 0; k < fields.size(); k++) {
							filedIndexMap.put(fields.get(k), k);
						}
						int meter = 0;
						String excelTitle;
						for (Iterator iter = fieldsMap.entrySet().iterator(); iter.hasNext();) {
							if (meter != 0) {
								insertSql.append(",");
								values.append(",");
							}
							entry = (Map.Entry) iter.next();
							fieldName = (String) entry.getKey();
							if (fieldExcelTitleMap.get(fieldName) != null)
								excelTitle = fieldExcelTitleMap.get(fieldName).toString();
							else
								excelTitle = fieldName;
							// 记录字段对应excel数据列
							if (fkMaps != null && fkMaps.get(fieldName) != null) {
								fkFieldsIndex[count][0] = fieldName;
								fkFieldsIndex[count][1] = fkMaps.get(fieldName).toString();
								fkFieldsIndex[count][2] = filedIndexMap.get(excelTitle).toString();
								count++;
							}
							insertSql.append(fieldName);
							values.append("?");
							meter++;
						}
						if (ExcelToyConstants.insertIgnore())
							insertSql.insert(0, " insert ignore into " + tableName + " (");
						else
							insertSql.insert(0, " insert into " + tableName + " (");
						insertSql.append(") values (");
						insertSql.append(values);
						insertSql.append(")");
						// 将自增字段设置为可插入
						if (hasAutoIncrementField) {
							if (dbType == DBType.SYBASE_IQ) {
								DBHelper.execute("SET TEMPORARY OPTION IDENTITY_INSERT='" + tableName + "' ", true);
							}
						}
						// 执行数据库插入操作
						DBHelper.insertDB(fkFieldsIndex, insertSql.toString(), fields, fieldsMap, filedIndexMap,
								fieldExcelTitleMap, excelData, importModel.getBlobFile(), charset);
						// 是否有自增字段
						if (hasAutoIncrementField) {
							if (dbType == DBType.SYBASE_IQ) {
								DBHelper.execute(" SET TEMPORARY OPTION IDENTITY_INSERT=''", true);
							}
						}
					}
				}
				if (isReport) {
					List rowStat = new ArrayList();
					rowStat.add(tableName);
					rowStat.add(tableImportCnt);
					rowStat.add(DBHelper.getJdbcRecordCount("select count(1) from " + tableName));
					importReport.add(rowStat);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("任务:{}以及对应表:{}数据导入过程发生错误!", importModel.getId(), tableName);
			}
		}
		// 生成数据导入报告
		if (isReport)
			ExcelUtil.writer(importReport, importModel.getReportFile(), null);
		// do end-do 后置事务
		afterTransation(false, false);
	}

	/**
	 * @todo 执行eql语句进行数据表插入,分别执行前置任务、表插入、插入后的后置任务 前置后后置事务可以通过常量设置条件,如:update table
	 *       set A.value=${param}
	 * @param excelFiles
	 * @throws Exception
	 */
	private void doEqlTask(List excelFiles, String charset) throws Exception {
		EqlParseResult mainEqlParseResult = EQLUtil.parseEql(importModel.getMainEql());
		// 解析过程中有错误
		if (importModel.getMainEql() != null && mainEqlParseResult == null) {
			logger.info("任务:{}没有配置<do></do>事务!", importModel.getId());
		}
		final int dbType = DBHelper.getDbType();
		// do pre-do 前置事务
		preTransation(false);
		int beginRow = 0;
		try {
			// 存在导入语句
			if (mainEqlParseResult != null) {
				// 定义beanshell脚本处理，用来处理filter条件
				Interpreter interpreter = new Interpreter();
				String filter;
				String evalResult;
				// 表外键信息
				HashMap fkMaps = null;
				if (importModel.isFkFilter())
					fkMaps = DBHelper.getTableImpForeignKeys(mainEqlParseResult.getTableName());
				mainEqlParseResult.setTableMeta(DBHelper.getTableColumnMeta(mainEqlParseResult.getTableName()));
				logger.info("正在开始向表:{}导入数据!", mainEqlParseResult.getTableName());
				// 清除记录
				clearTable();
				// do eql
				List excelTitles = null;
				String fileName = ((File) excelFiles.get(0)).getName();
				if (importModel.getTitleRow() > 0)
					excelTitles = ExcelUtil.read(EQLUtil.getExcelFileSuffix(fileName), excelFiles.get(0),
							importModel.getSheet(), importModel.getTitleRow(), importModel.getTitleRow(),
							importModel.getBeginCol(), importModel.getEndCol());
				/**
				 * excel文件标题列对应数据列
				 */
				HashMap excelTitleMapDataIndex = EQLUtil.mappingExcelTitleAndDataIndex(excelTitles,
						importModel.getBeginCol(), importModel.getEndCol());
				/**
				 * excel列对应解析后的excel数据列
				 */
				HashMap excelColMapDataIndex = EQLUtil.mappingExcelColAndDataIndex(importModel.getBeginCol(),
						importModel.getEndCol());
				ConvertDataSource.setExcelColMapDataIndex(excelColMapDataIndex);
				ConvertDataSource.setExcelTitleMapDataIndex(excelTitleMapDataIndex);

				beginRow = importModel.getBeginRow();
				if (beginRow <= importModel.getTitleRow()) {
					logger.info("标题所在行大于数据开始行!");
					beginRow = importModel.getTitleRow() + 1;
				}

				HashMap subTableEqlParseResult = new HashMap();
				EqlModel eqlModel;
				EqlParseResult subEqlParseResult;
				// 解析子表插入
				if (importModel.getSubEqls() != null) {
					for (int i = 0; i < importModel.getSubEqls().size(); i++) {
						eqlModel = (EqlModel) importModel.getSubEqls().get(i);
						subEqlParseResult = EQLUtil.parseEql(eqlModel.getEqlContent());
						subEqlParseResult.setTableMeta(DBHelper.getTableColumnMeta(subEqlParseResult.getTableName()));
						subTableEqlParseResult.put(Integer.toString(i), subEqlParseResult);
					}
				}
				List excelRowsData;
				boolean hasFilter = (importModel.getFilter() != null) ? true : false;

				for (int i = 0; i < excelFiles.size(); i++) {
					fileName = ((File) excelFiles.get(i)).getName();
					logger.info("正在导入文件:{}", fileName);
					excelRowsData = ExcelUtil.read(EQLUtil.getExcelFileSuffix(fileName), excelFiles.get(i),
							importModel.getSheet(), beginRow, importModel.getEndRow(), importModel.getBeginCol(),
							importModel.getEndCol());

					// 使用复合主键
					boolean usePK = StringUtil.isNotBlank(importModel.getMainPK()) ? true : false;
					String[] pkCols = usePK ? EQLUtil.parseExcelFields(importModel.getMainPK()) : null;
					if (importModel.isPkDataMerge())
						excelRowsData = CollectionUtil.merge(excelRowsData, importModel.getMainPK(), pkCols);
					int excelRowCount = excelRowsData.size();
					logger.info("文件共有:{}条记录!", excelRowCount);
					if (excelRowCount > 0) {
						// 设置excel文件数据
						ConvertDataSource.setExcelRowsData(excelRowsData);
						// 清空主表转换后的数据
						ConvertDataSource.setConvertedRowsData(new ArrayList());
						preTransation(true);
						// 保存唯一主键数据，避免重复插入
						HashMap pkMap = new HashMap();
						String pkValue;
						List rowData;
						// 是否插入主表标识
						boolean isInsertMain = true;
						boolean hasMainEql = StringUtil.isNotBlank(importModel.getMainEql());
						boolean isSkip = false;
						String colValue;
						// 外键不存在的数据，构建hashMap，在后续处理中填充hashmap返回
						HashMap fkNoExistMap = new HashMap();
						boolean filterFk = (fkMaps == null) ? false : true;

						for (int j = 0; j < excelRowCount; j++) {
							isSkip = false;
							rowData = (List) excelRowsData.get(j);
							// 设置行数据
							ConvertDataSource.setExcelRowData(rowData);
							isInsertMain = true;
							// 使用复合主键
							if (usePK) {
								pkValue = EQLUtil.replaceHolder(importModel.getMainPK(), pkCols);
								// pk重复
								if (pkMap.get(pkValue) != null) {
									isInsertMain = false;
								} else {
									// 放入一个任意值表示pk已经插入过数据库
									pkMap.put(pkValue, "1");
									isInsertMain = true;
								}
							} else
								// 清空主表
								ConvertDataSource.setMainTableRowData(null);
							// 条件过滤
							if (hasFilter) {
								filter = ConvertUtil.executeConvert(importModel.getFilter(), null, null).toString();
								// 替换换行符号
								filter = filter.replaceAll("\n", " ");
								evalResult = interpreter.eval(filter).toString();
								// 过滤结果为true，则不执行主表
								if (Boolean.valueOf(evalResult)) {
									// isInsertMain = false;
									isSkip = true;
									// 跳过,子表也不执行插入
									// ConvertDataSource.setMainTableRowData(null);
									ExcelToySpringContext
											.putMessage("[第:" + (j + beginRow) + "行记录]" + importModel.getFilterMsg());
									if (importModel.isFilterBreak()) {
										realRecordCnt++;
										throw new Exception(
												"[第:" + (j + beginRow) + "行记录]" + importModel.getFilterMsg());
									}
								}
							}

							if (hasMainEql && isInsertMain && !isSkip) {
								// 执行主表插入并返回主表插入的数据
								ConvertDataSource.setMainTableRowData(doMainTableInsert(dbType, fkMaps, filterFk,
										fkNoExistMap, mainEqlParseResult.getInsertSql(), mainEqlParseResult.getParams(),
										mainEqlParseResult.getFields(), mainEqlParseResult.getTableMeta(),
										importModel.getBlobFile(), beginRow, j, excelRowCount,
										importModel.getSubEqls() != null ? true : false, hasFilter,
										importModel.isIgnoreMainInsert(), charset));
							}
							if (importModel.getSubEqls() != null && !isSkip) {
								// logger.info("插入子表信息!");
								// 插入子表
								for (int k = 0; k < importModel.getSubEqls().size(); k++) {
									eqlModel = (EqlModel) importModel.getSubEqls().get(k);
									// 子表插入如果依赖字段切割循环的，主表数据未插入则不执行
									if (!isInsertMain && StringUtil.isNotBlank(eqlModel.getLoopCol())) {
										continue;
									} else {
										// 进行转换器处理，排除
										colValue = StringUtil.isBlank(eqlModel.getLoopCol()) ? null
												: ConvertUtil.executeConvert(eqlModel.getLoopCol(), null, null)
														.toString();
										if (StringUtil.isNotBlank(eqlModel.getLoopCol()) && eqlModel.isSkipNull()
												&& StringUtil.isBlank(colValue)) {
											logger.info("子表循环切割的值为空!");
											if (eqlModel.isBreak()) {
												ExcelToySpringContext.putMessage("[第:" + (realRecordCnt + beginRow - 1)
														+ "条记录]" + eqlModel.getMessage());
												throw new Exception("[第:" + (realRecordCnt + beginRow - 1) + "条记录]"
														+ eqlModel.getMessage());
											}
										} else {
											subEqlParseResult = (EqlParseResult) subTableEqlParseResult
													.get(Integer.toString(k));
											// 执行子表插入
											doSubTableInsert(dbType, subEqlParseResult.getInsertSql(),
													subEqlParseResult.getParams(), subEqlParseResult.getFields(),
													subEqlParseResult.getTableMeta(), importModel.getBlobFile(),
													beginRow, eqlModel,
													(StringUtil.isNotBlank(eqlModel.getSplitSign()) && colValue != null)
															? StringUtil.split(colValue, eqlModel.getSplitSign())
															: new String[] { colValue },
													charset);
										}
									}
								}
							}
							// 最后一条记录，有批处理未提交的量
							if (!importModel.isIgnoreMainInsert() && hasMainEql) {
								if (j == excelRowCount - 1 && batchCount > 0 && mainPst != null) {
									mainPst.executeBatch();
									mainPst.clearBatch();
								}
							}
						}
						afterTransation(true, false);
					} else
						ExcelToySpringContext.putMessage("导入文件数据为空,请检查!");
				}
				// 关闭插入数据过程中使用的preparedStatement
				closePreparedStatement();
			}
		} catch (Exception e) {
			// 发生错误是必须执行的后续事务
			afterTransation(false, true);
			ExcelToySpringContext.putMessage("在导入第：" + (realRecordCnt + beginRow) + "记录开始错误!");
			throw e;
		}

		// do end-do 后置事务
		afterTransation(false, false);
	}

	/**
	 * @todo <b>执行主表数据插入过程</b>
	 * @param fkFields
	 *            外键字段
	 * @param isFkFilter
	 *            是否进行外键过滤
	 * @param fkNoExistMap
	 * @param insertSql
	 * @param params
	 * @param fileds
	 * @param tableMetaMap
	 * @param blobFile
	 * @param rowIndex
	 * @param rowCounts
	 * @param hasSubTables
	 * @param hasFilter
	 * @return
	 * @throws Exception
	 */
	private Object[][] doMainTableInsert(int dbType, HashMap fkFields, boolean isFkFilter, HashMap fkNoExistMap,
			String insertSql, String[] params, String[] fileds, HashMap tableMetaMap, String blobFile, int beginRow,
			int rowIndex, int rowCounts, boolean hasSubTables, boolean hasFilter, boolean ignoreInsert, String charset)
			throws Exception {
		realRecordCnt++;
		Object[][] result = new Object[params.length][2];
		Object obj;
		String fieldName;
		TableColumnMeta colMeta;
		Object[] objs = new Object[params.length];
		int fkCount = 1;
		HashMap fkNoExistDatas;
		for (int i = 0; i < params.length; i++) {
			obj = ConvertUtil.executeConvert(params[i], null, null);
			objs[i] = obj;
			fieldName = fileds[i];
			colMeta = (TableColumnMeta) tableMetaMap.get(fieldName);

			// 外键约束过滤
			if (isFkFilter && fkFields.get(fieldName) != null) {
				if (obj == null) {
					fkCount = 0;
					break;
				}
				// 检查每个外键值是否存在
				if (fkNoExistMap.get(fieldName) == null)
					fkNoExistMap.put(fieldName, new HashMap());
				fkNoExistDatas = (HashMap) fkNoExistMap.get(fieldName);
				// 数值不在外键清单中()，则连接数据库查询
				if (fkNoExistDatas.get(obj.toString()) == null) {
					fkCount = DBHelper.getCount(fkFields.get(fieldName).toString(), obj, colMeta);
					// 外键值不存在时，将不存在的值存放于hashMap中
					if (fkCount < 1) {
						fkNoExistDatas.put(obj.toString(), "1");
						break;
					}
				} else {
					fkCount = 0;
					break;
				}
			}
		}
		// 外键不存在
		if (fkCount < 1)
			return null;
		if (!ignoreInsert) {
			if (pstMap.get(insertSql) == null) {
				mainPst = DBHelper.getConnection().prepareStatement(insertSql);
				pstMap.put(insertSql, mainPst);
			}
			mainPst = (PreparedStatement) pstMap.get(insertSql);
		}
		for (int i = 0; i < params.length; i++) {
			fieldName = fileds[i];
			result[i][0] = fieldName;
			result[i][1] = (objs[i] == null) ? "" : objs[i];
			colMeta = (TableColumnMeta) tableMetaMap.get(fieldName);
			if (colMeta == null)
				logger.error("字段:" + fieldName + "在表中不存在!");
			if (!ignoreInsert)
				DBHelper.setParam(objs[i], colMeta, mainPst, dbType, i + 1, blobFile, charset);
		}
		// 存在子表数据插入需要及时提交主表的数据
		if (!ignoreInsert) {
			if (1 == rowCounts || hasSubTables) {
				mainPst.execute();
				if (realRecordCnt % 100 == 0)
					logger.info("已经完成:" + realRecordCnt + "条数据导入!");
			} else {
				mainPst.addBatch();
				batchCount++;
				if (((rowIndex + 1) % ExcelToyConstants.getBatchSize()) == 0 || (rowIndex + 1) == rowCounts) {
					mainPst.executeBatch();
					mainPst.clearBatch();
					batchCount = 0;
					logger.info("已经完成:第:{}行，共计:{}条数据导入!", (rowIndex + beginRow - 1), realRecordCnt);
				}
			}
		}
		return result;
	}

	/**
	 * @todo <b> 插入子表数据</b>
	 * @param insertSql
	 * @param params
	 * @param fileds
	 * @param tableMetaMap
	 * @param blobFile
	 * @param loopAs
	 * @param skipNull
	 * @param loopValues
	 * @throws Exception
	 */
	private void doSubTableInsert(int dbType, String insertSql, String[] params, String[] fileds, HashMap tableMetaMap,
			String blobFile, int beginRow, EqlModel eqlModel, String[] loopValues, String charset) throws Exception {
		try {
			// 子表采用分割循环，分割后的结果为空则跳出处理
			if (StringUtil.isNotBlank(eqlModel.getLoopAs()) && ((loopValues == null || loopValues.length == 0)
					|| (loopValues.length == 1 && loopValues[0].trim().equalsIgnoreCase("")))) {
				if (eqlModel.isBreak()) {
					ExcelToySpringContext
							.putMessage("[第:" + (realRecordCnt + beginRow - 1) + "条记录]" + eqlModel.getMessage());
					throw new Exception("[第:" + (realRecordCnt + beginRow - 1) + "条记录]" + eqlModel.getMessage());
				}
				return;
			}
			int loopSize = (loopValues == null) ? 0 : loopValues.length;
			if (eqlModel.isSkipNull()) {
				if (loopValues == null) {
					if (eqlModel.isBreak()) {
						ExcelToySpringContext
								.putMessage("[第:" + (realRecordCnt + beginRow) + "条记录]" + eqlModel.getMessage());
						throw new Exception("[第:" + (realRecordCnt + beginRow) + "条记录]" + eqlModel.getMessage());
					}
					return;
				}
				int nullCnt = 0;
				for (int k = 0; k < loopSize; k++) {
					if (StringUtil.isBlank(loopValues[k]))
						nullCnt++;
				}
				if (nullCnt == loopSize) {
					if (eqlModel.isBreak()) {
						ExcelToySpringContext
								.putMessage("[第:" + (realRecordCnt + beginRow - 1) + "条记录]" + eqlModel.getMessage());
						throw new Exception("[第:" + (realRecordCnt + beginRow - 1) + "条记录]" + eqlModel.getMessage());
					}
					return;
				}
			}
			List loopValuesList = CollectionUtil.arrayToList(loopValues);
			// 是否过滤空
			if (eqlModel.isSkipNull()) {
				loopValuesList.remove(null);
				for (Iterator iter = loopValuesList.iterator(); iter.hasNext();) {
					if (iter.next().toString().trim().equals(""))
						iter.remove();
				}

			}
			// 过滤重复数据
			if (eqlModel.isSkipRepeat()) {
				HashSet hashSet = new HashSet(loopValuesList);
				loopValuesList = new ArrayList(hashSet);
			}
			int rowSize = loopValuesList.size();
			if (rowSize < eqlModel.getMinSize()) {
				if (eqlModel.isBreak()) {
					ExcelToySpringContext
							.putMessage("[第:" + (realRecordCnt + beginRow - 1) + "条记录]" + eqlModel.getMessage());
					throw new Exception("[第:" + (realRecordCnt + beginRow - 1) + "条记录]" + eqlModel.getMessage());
				}
				return;
			}

			PreparedStatement pst;
			if (pstMap.get(insertSql) == null) {
				pst = DBHelper.getConnection().prepareStatement(insertSql);
				pstMap.put(insertSql, pst);
			}
			pst = (PreparedStatement) pstMap.get(insertSql);
			Object obj;
			String fieldName;
			TableColumnMeta colMeta;
			// 此处loopValues==null 表示采用PK模式进行子表插入
			for (int k = 0; k < loopValuesList.size(); k++) {
				for (int i = 0; i < params.length; i++) {
					obj = ConvertUtil.executeConvert(StringUtil.replaceAllStr(params[i],
							"#{" + eqlModel.getLoopAs() + ".index}", Integer.toString(k)), eqlModel.getLoopAs(),
							(String) loopValuesList.get(k));
					fieldName = fileds[i];
					colMeta = (TableColumnMeta) tableMetaMap.get(fieldName);
					if (colMeta == null)
						logger.error("字段:{}在表中不存在!", fieldName);
					DBHelper.setParam(obj, colMeta, pst, dbType, i + 1, blobFile, charset);
				}
				pst.addBatch();
			}
			if (loopValuesList.size() > 0) {
				pst.executeBatch();
				pst.clearBatch();
			}
		} catch (Exception e) {
			ExcelToySpringContext.putMessage("插入子表错误:" + e.getMessage());
			logger.error("插入子表错误!");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @todo 清空表中已有数据
	 */
	private void clearTable() {
		// 先清空子表
		if (importModel.getSubEqls() != null) {
			EqlModel eqlModel;
			for (Iterator iter = importModel.getSubEqls().iterator(); iter.hasNext();) {
				eqlModel = (EqlModel) iter.next();
				if (eqlModel.getClear().equalsIgnoreCase("true")) {
					DBHelper.execute("delete from " + EQLUtil.getTableName(eqlModel.getEqlContent()), true);
				}
			}
		}
		// 主表
		if (importModel.getMainClear())
			DBHelper.execute("delete from " + EQLUtil.getTableName(importModel.getMainEql()), true);
	}

	/**
	 * @todo 关闭插入数据过程中产生的数据库PreparedStatement
	 * @throws SQLException
	 */
	private void closePreparedStatement() {
		if (!pstMap.isEmpty()) {
			Iterator iter = pstMap.entrySet().iterator();
			while (iter.hasNext()) {
				try {
					PreparedStatement pst = (PreparedStatement) ((Map.Entry) iter.next()).getValue();
					if (pst != null) {
						pst.close();
						pst = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pstMap = null;
		}
	}

	/**
	 * @todo 前置事务
	 */
	private void preTransation(boolean loopBeforeMain) {
		// do end-do 后置事务
		doTransation(importModel.getBefores(), "执行前置事务!", true, loopBeforeMain, false);
	}

	/**
	 * @todo 后置事务
	 * @param loopAfterMain
	 * @param errorRun
	 */
	private void afterTransation(boolean loopAfterMain, boolean errorRun) {
		// do end-do 后置事务
		doTransation(importModel.getAfters(), "执行后置事务!", false, loopAfterMain, errorRun);
	}

	/**
	 * @todo <b>执行事务</b>
	 * @param transList
	 * @param message
	 * @param beforeOrEnd
	 * @param filterTrue
	 * @param errorRun
	 */
	private void doTransation(List transList, String message, boolean beforeOrEnd, boolean filterTrue,
			boolean errorRun) {
		if (transList != null && !transList.isEmpty()) {
			logger.info(message);
			Iterator iter = transList.iterator();
			int meter = 1;
			TransationModel transModel = null;
			boolean isRun = false;
			while (iter.hasNext()) {
				try {
					logger.info("开始执行第:{}条事务!", meter);
					transModel = (TransationModel) iter.next();
					// 动态设置属性，用于导入过程中进行关联
					if (StringUtil.isNotBlank(transModel.getProperty())) {
						ExcelToyConstants.setProperty(transModel.getProperty(),
								ConvertUtil.executeConvert(transModel.getPropertyValue(), null, null).toString());
					}
					String sql = null;
					if (beforeOrEnd && (transModel.isLoopBeforeMain() == filterTrue))
						isRun = true;
					if (!beforeOrEnd && (transModel.isLoopAfterMain() == filterTrue))
						isRun = true;
					if (errorRun && transModel.isErrorRun()) {
						isRun = true;
					}
					// 仅仅错误时才执行
					if (!errorRun && transModel.isOnlyError())
						isRun = false;

					if (isRun) {
						// sql文件
						if (transModel.isSqlFile()) {
							File sqlFile;
							// 跟路径
							if (FileUtil.isRootPath(transModel.getSql()))
								sqlFile = new File(transModel.getSql());
							else
								sqlFile = new File(ExcelToyConstants.getBaseDir(), transModel.getSql());
							if (!sqlFile.exists()) {
								logger.error("导入sql文件:{}不存在请确认!", transModel.getSql());
							}
							// 增加剔除注释
							sql = SqlUtils.clearMark(FileUtil.readAsString(sqlFile, transModel.getEncoding()));
						} else
							sql = transModel.getSql();
						int index = sql.indexOf("@");
						if (index != -1) {
							// 转换器调用
							sql = (String) ConvertUtil.executeConvert(sql, null, null);
						}
						if (StringUtil.isNotBlank(sql) && sql.trim().indexOf(" ") != -1) {
							// 修改成批量sql执行方式，方便批量sql语句的执行
							// update by chenrenfei 2012-12-4 增加替换常量
							DBHelper.batchSqlText(ExcelToyConstants.replaceConstants(sql), transModel.getSplitSign(),
									transModel.isAutoCommit());
						}
						meter++;
					}
				} catch (Exception e) {
					ExcelToySpringContext
							.putMessage("第:" + meter + "条事务:[" + message + "]sql:" + transModel.getSql() + "发生错误!");
					logger.error("第:{}条事务,错误信息:{},sql:{}发生错误", meter, message, transModel.getSql(), e);
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @todo 判断表的字段是否属于自增identity
	 * @param fieldsMap
	 * @return
	 */
	private boolean hasAutoIncrementField(HashMap<String, TableColumnMeta> fieldsMap) {
		Iterator<TableColumnMeta> iter = fieldsMap.values().iterator();
		while (iter.hasNext()) {
			if (iter.next().isAutoIncrement())
				return true;
		}
		return false;
	}

}
