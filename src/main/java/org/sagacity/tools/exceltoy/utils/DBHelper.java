/**
 * 
 */
package org.sagacity.tools.exceltoy.utils;

import java.io.File;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.model.DataSourceModel;
import org.sagacity.tools.exceltoy.model.PaginationModel;
import org.sagacity.tools.exceltoy.model.TableColumnMeta;
import org.sagacity.tools.exceltoy.model.TableConstractModel;
import org.sagacity.tools.exceltoy.model.TableMeta;
import org.sagacity.tools.exceltoy.model.TreeTableModel;
import org.sagacity.tools.exceltoy.utils.DataSourceUtils.DBType;
import org.sagacity.tools.exceltoy.utils.callback.PreparedStatementResultHandler;

/**
 * @project sagacity-tools
 * @description 针对excelToy工具的数据库连接、操作工具包
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:DBHelper.java,Revision:v1.0,Date:2009-12-30
 */
@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
public class DBHelper {
	private final static Logger logger = LogManager.getLogger(DBHelper.class);
	/**
	 * 数据库配置参数的map信息
	 */
	private static HashMap<String, DataSourceModel> datasourceMap = new HashMap<String, DataSourceModel>();

	private static ThreadLocal<Connection> connLocal = new ThreadLocal<Connection>();

	/**
	 * 数据库模型
	 */
	private static DataSourceModel dbModel = null;

	/**
	 * 数据库名称
	 */
	private static String datasourceName = "";

	public static DataSourceModel getDataSource(String dataSourceName) {
		return datasourceMap.get(dataSourceName);
	}

	/**
	 * 
	 * @todo 获取数据库连接
	 * @param datasourceName
	 * @return
	 */
	public static Connection getConnection() throws Exception {
		return connLocal.get();
	}

	/**
	 * @todo 登记当前的数据库连接
	 * @param datasource
	 * @param isolationlevel
	 * @param autoCommit
	 * @throws Exception
	 */
	public static void registConnection(String datasource, String isolationlevel, String autoCommit) throws Exception {
		datasourceName = datasource;
		connLocal.remove();
		Connection conn;
		dbModel = null;
		if (datasource != null) {
			dbModel = (DataSourceModel) datasourceMap.get(datasource);
		} else {
			if (datasourceMap.size() == 1) {
				Iterator iter = datasourceMap.values().iterator();
				dbModel = (DataSourceModel) iter.next();
			}
		}
		if (dbModel == null) {
			throw new Exception("任务设置的对应datasource='" + datasource + "',未能正确匹配，请检查配置!");
		}
		try {
			logger.info("数据库地址信息:{}", dbModel.getUrl());
			Class.forName(dbModel.getDriver());
			conn = DriverManager.getConnection(dbModel.getUrl(), dbModel.getUsername(), dbModel.getPassword());
			// dbType = DataSourceUtils.getDbType(conn);
			connLocal.set(conn);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			logger.error("数据库驱动未能加载，请在/drivers 目录下放入正确的数据库驱动jar包!", cnfe);
			throw cnfe;
		} catch (SQLException se) {
			logger.error("获取数据库连接失败!", se);
			throw se;
		}

		if (StringUtil.isNotBlank(autoCommit)) {
			conn.setAutoCommit(new Boolean(autoCommit));
		} else {
			conn.setAutoCommit(true);
		}

		// 设置事务隔离级别
		if (StringUtil.isNotBlank(isolationlevel)) {
			if (isolationlevel.equalsIgnoreCase("TRANSACTION_NONE")) {
				conn.setTransactionIsolation(conn.TRANSACTION_NONE);
			} else if (isolationlevel.equalsIgnoreCase("TRANSACTION_READ_COMMITTED")) {
				conn.setTransactionIsolation(conn.TRANSACTION_READ_COMMITTED);
			} else if (isolationlevel.equalsIgnoreCase("TRANSACTION_READ_UNCOMMITTED")) {
				conn.setTransactionIsolation(conn.TRANSACTION_READ_UNCOMMITTED);
			} else if (isolationlevel.equalsIgnoreCase("TRANSACTION_REPEATABLE_READ")) {
				conn.setTransactionIsolation(conn.TRANSACTION_REPEATABLE_READ);
			} else if (isolationlevel.equalsIgnoreCase("TRANSACTION_SERIALIZABLE")) {
				conn.setTransactionIsolation(conn.TRANSACTION_SERIALIZABLE);
			}
		}
	}

	public static void registConnection(Connection conn) throws Exception {
		connLocal.set(conn);
	}

	public static int getDbType() throws Exception {
		return DataSourceUtils.getDbType(connLocal.get());
	}

	/**
	 * @todo 关闭数据库并销毁
	 */
	public static void destory() {
		connLocal.remove();
	}

	/**
	 * @todo 关闭数据库并销毁
	 */
	public static void close() {
		try {
			Connection conn = connLocal.get();
			if (conn != null) {
				if (!conn.getAutoCommit()) {
					conn.commit();
				}
				conn.close();
				conn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @todo 数据库事务回滚
	 */
	public static void rollback() {
		try {
			Connection conn = connLocal.get();
			if (conn != null) {
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @todo 加载数据库配置
	 * @param datasouceElts
	 * @throws Exception
	 */
	public static void loadDatasource(List datasouceElts) throws Exception {
		if (datasouceElts == null || datasouceElts.isEmpty()) {
			logger.error("没有配置相应的数据库");
			throw new Exception("没有配置相应的数据库");
		}
		Element elt = null;
		String catalog = null;
		String schema = null;
		String forbid = null;
		for (int i = 0; i < datasouceElts.size(); i++) {
			catalog = null;
			schema = null;
			forbid = null;
			elt = (Element) datasouceElts.get(i);
			if (elt.attribute("catalog") != null) {
				catalog = ExcelToyConstants.getPropertyValue(elt.attributeValue("catalog"));
			}
			if (elt.attribute("schema") != null) {
				schema = ExcelToyConstants.getPropertyValue(elt.attributeValue("schema"));
			}
			if (elt.attribute("forbid") != null) {
				forbid = ExcelToyConstants.getPropertyValue(elt.attributeValue("forbid"));
			}
			DataSourceModel dbModel = new DataSourceModel(ExcelToyConstants.getPropertyValue(elt.attributeValue("url")),
					ExcelToyConstants.getPropertyValue(elt.attributeValue("driver")),
					ExcelToyConstants.getPropertyValue(elt.attributeValue("username")),
					ExcelToyConstants.getPropertyValue(elt.attributeValue("password")), catalog, schema, forbid);
			datasourceMap.put(elt.attributeValue("id"), dbModel);
		}
	}

	/**
	 * @todo <b>按照表依赖顺序获取数据库表信息</b>
	 * @param includes
	 * @param excludes
	 * @return
	 * @throws Exception
	 */
	public static List getTablesByOrderLink(String[] includes, String[] excludes) throws Exception {
		Connection conn = connLocal.get();
		List orderTables = getConnectionTableAndViews(conn, (dbModel == null) ? null : dbModel.getCatalog(),
				(dbModel == null) ? null : dbModel.getSchema(), new String[] { "TABLE" }, includes, excludes);
		if (orderTables == null || orderTables.isEmpty())
			return null;
		TableMeta tableMeta;
		int moveCnt;

		// 设置hashMap存放表对应外键信息，避免循环过程中不断的调用数据库
		HashMap tableFkMap = new HashMap();
		List tableFKs;
		String tableName;
		// 提取表的字段
		for (int i = 0; i < orderTables.size(); i++) {
			tableMeta = (TableMeta) orderTables.get(i);
			tableMeta.setColMetas(getTableColumnMetaList(tableMeta.getTableName()));
			// 针对sqlserver
			if (StringUtil.isBlank(tableMeta.getTableRemark())) {
				tableMeta.setTableRemark(getTableComment(conn, tableMeta.getTableName()));
			}
		}
		for (int i = 0; i < orderTables.size(); i++) {
			tableName = ((TableMeta) orderTables.get(i)).getTableName();
			if (tableFkMap.get(tableName) == null) {
				tableFKs = getTableImpForeignKeys(conn, (dbModel == null) ? null : dbModel.getCatalog(),
						(dbModel == null) ? null : dbModel.getSchema(), tableName);
				tableFkMap.put(tableName, tableFKs);
			} else {
				tableFKs = (List) tableFkMap.get(tableName);
			}

			moveCnt = 0;
			if (tableFKs != null && !tableFKs.isEmpty()) {
				TableConstractModel fkTable;
				// 外键关联的表从当前位置向下寻找，找到则将位置移到当前位置前面
				for (int j = 0; j < tableFKs.size(); j++) {
					fkTable = (TableConstractModel) tableFKs.get(j);
					for (int k = i + moveCnt + 1; k < orderTables.size(); k++) {
						tableMeta = (TableMeta) orderTables.get(k);
						// 位置在后面则向前移动
						if (fkTable.getFkRefTableName().equalsIgnoreCase(tableMeta.getTableName())) {
							orderTables.remove(k);
							orderTables.add(i, tableMeta);
							// 计数移动了多少个
							moveCnt++;
							break;
						}
					}
				}
			}
			if (moveCnt != 0) {
				i--;
			}
		}
		return orderTables;
	}

	/**
	 * @todo <b>sql 查询并返回List集合</b>
	 * @param queryStr
	 * @param params
	 * @return
	 */
	public static List findByJdbcQuery(final String queryStr, final Object[] params) {
		List result = null;
		try {
			final Connection conn = connLocal.get();
			ResultSet rs = null;
			PreparedStatement pst = conn.prepareStatement(queryStr);
			result = (List) SqlUtils.preparedStatementProcess(null, pst, rs, new PreparedStatementResultHandler() {
				public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws Exception {
					SqlUtils.setParamsValue(conn, pst, params, null, 0);
					rs = pst.executeQuery();
					this.setResult(SqlUtils.processResultSet(rs, 0));
				}
			});
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		if (result == null) {
			result = new ArrayList();
		}
		return result;
	}

	/**
	 * @todo 获取表的注释
	 * @param conn
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	private static String getTableComment(final Connection conn, final String tableName) throws Exception {
		final int dbType = DataSourceUtils.getDbType(conn);
		PreparedStatement pst = null;
		ResultSet rs;
		// sybase or sqlserver
		String tableComment = null;
		if (dbType == DBType.SQLSERVER || dbType == DBType.SQLSERVER2014 || dbType == DBType.SQLSERVER2016
				|| dbType == DBType.SQLSERVER2017) {
			StringBuilder queryStr = new StringBuilder();
			queryStr.append("select cast(isnull(f.value,'') as varchar(1000)) COMMENTS");
			queryStr.append(" from syscolumns a");
			queryStr.append(" inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name<>'dtproperties'");
			queryStr.append(" left join sys.extended_properties f on d.id=f.major_id and f.minor_id=0");
			queryStr.append(" where a.colorder=1 and d.name=?");
			pst = conn.prepareStatement(queryStr.toString());
			pst.setString(1, tableName);
			rs = pst.executeQuery();
			tableComment = (String) SqlUtils.preparedStatementProcess(null, pst, rs,
					new PreparedStatementResultHandler() {
						public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws SQLException {
							while (rs.next()) {
								this.setResult(rs.getString("COMMENTS"));
							}
						}
					});
		}
		return tableComment;
	}

	/**
	 * @todo <b>执行无结果返回的sql语句，一般针对增删改操作</b>
	 * @param sql
	 * @param autoCommit
	 */
	public static void execute(String sql, boolean autoCommit) {
		if (StringUtil.isBlank(sql)) {
			logger.info("sql 为空白!");
			return;
		}
		// 判断是否禁止操作
		String forbid = isForbid(sql);
		if (forbid != null) {
			logger.info("datasource:" + datasourceName + " 禁止" + forbid + "操作!");
			return;
		}
		try {
			Connection conn = connLocal.get();
			boolean hasSetAutoCommit = false;
			if (!autoCommit == conn.getAutoCommit()) {
				conn.setAutoCommit(autoCommit);
				hasSetAutoCommit = true;
			}
			PreparedStatement pst = conn.prepareStatement(sql);
			SqlUtils.preparedStatementProcess(null, pst, null, new PreparedStatementResultHandler() {
				public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws SQLException {
					pst.execute();
				}
			});
			if (hasSetAutoCommit) {
				conn.setAutoCommit(!autoCommit);
			}
		} catch (Exception e) {
			logger.error("执行SQL错误:" + sql, e);
			e.fillInStackTrace();
		}
	}

	/**
	 * 
	 * @todo 获取数据库表的schema信息
	 * @param tableName
	 * @return
	 */
	public static HashMap<String, TableColumnMeta> getTableColumnMeta(String tableName) throws Exception {
		Connection conn = connLocal.get();
		List<TableColumnMeta> tableColumnsMeta = getTableColumnMeta(conn,
				(dbModel == null) ? null : dbModel.getCatalog(), (dbModel == null) ? null : dbModel.getSchema(),
				tableName);
		HashMap result = new HashMap();
		for (TableColumnMeta columnMeta : tableColumnsMeta) {
			result.put(columnMeta.getColName(), columnMeta);
		}
		return result;
	}

	/**
	 * 
	 * @todo 获取数据库表的schema信息
	 * @param tableName
	 * @return
	 */
	public static List<TableColumnMeta> getTableColumnMetaList(String tableName) throws Exception {
		Connection conn = connLocal.get();
		return getTableColumnMeta(conn, (dbModel == null) ? null : dbModel.getCatalog(),
				(dbModel == null) ? null : dbModel.getSchema(), tableName);
	}

	/**
	 * 
	 * @todo 获取表的所有字段的类型、名称、是否可以为null、长度等信息
	 * @param conn
	 * @param catalog
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private static List<TableColumnMeta> getTableColumnMeta(final Connection conn, final String catalog,
			final String schema, final String tableName) throws Exception {
		String realTableName = (tableName.indexOf(".") > 0) ? tableName.substring(tableName.indexOf(".") + 1)
				: tableName;
		final int dbType = DataSourceUtils.getDbType(conn);
		ResultSet rs;
		if (dbType == DBType.MYSQL || dbType == DBType.MYSQL8) {
			rs = conn.getMetaData().getColumns(catalog, schema, realTableName, "%");
		} else {
			rs = conn.getMetaData().getColumns(catalog, schema, realTableName, null);
		}
		return (List) SqlUtils.preparedStatementProcess(null, null, rs, new PreparedStatementResultHandler() {
			public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws SQLException {
				List result = new ArrayList();
				String isAutoIncrement;
				String dataType;
				while (rs.next()) {
					TableColumnMeta colMeta = new TableColumnMeta();
					colMeta.setColName(rs.getString("COLUMN_NAME").toUpperCase());
					colMeta.setColDefault(clearDefaultValue(StringUtil.trim(rs.getString("COLUMN_DEF"))));
					colMeta.setColRemark(rs.getString("REMARKS"));

					colMeta.setDataType(rs.getInt("DATA_TYPE"));
					dataType = rs.getString("TYPE_NAME");
					colMeta.setTypeName(dataType);
					if (dataType.equals("string") || dataType.equals("varchar") || dataType.equals("nvarchar")
							|| dataType.equals("varchar2") || dataType.equals("text") || dataType.equals("long varchar")
							|| dataType.equals("long text")) {
						colMeta.setTypeName("string");
					}
					colMeta.setLength(rs.getInt("COLUMN_SIZE"));
					colMeta.setPrecision(colMeta.getLength());
					colMeta.setScale(rs.getInt("DECIMAL_DIGITS"));
					colMeta.setNumPrecRadix(rs.getInt("NUM_PREC_RADIX"));
					try {
						isAutoIncrement = rs.getString("IS_AUTOINCREMENT");
						if (isAutoIncrement != null
								&& (isAutoIncrement.equalsIgnoreCase("true") || isAutoIncrement.equalsIgnoreCase("YES")
										|| isAutoIncrement.equalsIgnoreCase("Y") || isAutoIncrement.equals("1"))) {
							colMeta.setAutoIncrement(true);
						} else {
							colMeta.setAutoIncrement(false);
						}
					} catch (Exception e) {
					}

					if (rs.getInt("NULLABLE") == 1) {
						colMeta.setNullable(true);
					} else {
						colMeta.setNullable(false);
					}
					result.add(colMeta);
				}
				this.setResult(result);
			}
		});
	}

	public static int getDBDialect() throws Exception {
		return DataSourceUtils.getDbType(connLocal.get());
	}

	// ==================== 取当前会话数据库的类型以及版本,example:oracle10 ================/
	/**
	 * 
	 * @todo 获取表的外键信息
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static HashMap getTableImpForeignKeys(String tableName) throws Exception {
		String realTableName = (tableName.indexOf(".") > 0) ? tableName.substring(tableName.indexOf(".") + 1)
				: tableName;
		ResultSet rs = connLocal.get().getMetaData().getImportedKeys((dbModel == null) ? null : dbModel.getCatalog(),
				(dbModel == null) ? null : dbModel.getSchema(), realTableName);
		HashMap fkMaps = (HashMap) SqlUtils.preparedStatementProcess(null, null, rs,
				new PreparedStatementResultHandler() {
					public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws SQLException {
						HashMap result = new HashMap();
						while (rs.next()) {
							result.put(rs.getString("PKCOLUMN_NAME"),
									"select count(1) from ".concat(rs.getString("PKTABLE_NAME")).concat(" where ")
											.concat(rs.getString("PKCOLUMN_NAME")).concat("=?"));
						}
						this.setResult(result);
					}
				});
		if (fkMaps == null || fkMaps.isEmpty())
			return null;
		return fkMaps;
	}

	/**
	 * 获取分页查询中总记录数
	 * 
	 * @param queryStr
	 * @param params
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static Long getJdbcRecordCount(final String queryStr) throws Exception {
		Connection conn = connLocal.get();
		return SqlUtils.getJdbcRecordCount(queryStr, null, conn, DataSourceUtils.getDbType(conn));
	}

	/**
	 * @todo 数据库分页查询
	 * @param queryStr
	 * @param paginationModel
	 * @param hasFieldLabel
	 * @return
	 * @throws Exception
	 */
	public static PaginationModel findPageByJdbc(final String queryStr, final PaginationModel paginationModel)
			throws Exception {
		logger.debug("findPageByJdbc:分页查询sql为:" + queryStr);
		return SqlUtils.findPageByJdbc(queryStr, null, paginationModel, connLocal.get());
	}

	/**
	 * @todo <b>批量插入数据库</b>
	 * @param fkFieldsIndex
	 * @param insertSql
	 * @param fields
	 * @param fieldsMap
	 * @param data
	 * @param blobFile
	 */
	public static void insertDB(String[][] fkFieldsIndex, String insertSql, List fields, HashMap fieldsMap,
			HashMap excelTitleMap, HashMap fieldExcelTitleMap, List data, String blobFile, String charset) {
		if (data == null || data.size() < 1)
			return;
		// 判断是否禁止操作
		String forbid = isForbid(insertSql);
		if (forbid != null) {
			logger.info("datasource:" + datasourceName + " 禁止" + forbid + "操作!");
			return;
		}
		PreparedStatement pst = null;
		int rowMeter = 0;
		try {
			pst = connLocal.get().prepareStatement(insertSql);
			int dbType = DataSourceUtils.getDbType(connLocal.get());
			List rowData;
			String fieldName;
			String excelTitle;
			// 外键不存在的数据
			HashMap noExistFk = new HashMap();
			boolean filterFk = (fkFieldsIndex == null) ? false : true;
			TableColumnMeta colMeta;
			Object fkRefData;
			HashMap fkNoExistDatas;
			int fkCount = 1;
			int dataSize = data.size();
			Map.Entry entry;
			for (rowMeter = 0; rowMeter < dataSize; rowMeter++) {
				rowData = (List) data.get(rowMeter);
				fkCount = 1;
				// 外键约束过滤
				if (filterFk) {
					// 检查每个外键值是否存在
					for (int k = 0; k < fkFieldsIndex.length; k++) {
						fkRefData = rowData.get(Integer.parseInt(fkFieldsIndex[k][2]));
						// 外键值为空直接跳过该行数据
						if (fkRefData == null) {
							fkCount = 0;
							break;
						}
						if (noExistFk.get(fkFieldsIndex[k][0]) == null)
							noExistFk.put(fkFieldsIndex[k][0], new HashMap());
						fkNoExistDatas = (HashMap) noExistFk.get(fkFieldsIndex[k][0]);
						// 数值没有在不存在清单中则连接数据库查询
						if (fkNoExistDatas.get(fkRefData.toString()) == null) {
							fkCount = getCount(fkFieldsIndex[k][1], fkRefData,
									(TableColumnMeta) (TableColumnMeta) fieldsMap.get(fkFieldsIndex[k][0]));
							// 外键值不存在时，将不存在的值存放于hashMap中
							if (fkCount < 1) {
								fkNoExistDatas.put(fkRefData.toString(), "1");
								break;
							}
						} else {
							fkCount = 0;
							break;
						}
					}
				}
				// 外键存在
				if (fkCount > 0) {
					int j = 0;
					for (Iterator iter = fieldsMap.entrySet().iterator(); iter.hasNext();) {
						entry = (Map.Entry) iter.next();
						fieldName = entry.getKey().toString();
						if (fieldExcelTitleMap.get(fieldName) != null) {
							excelTitle = fieldExcelTitleMap.get(fieldName).toString();
						} else {
							excelTitle = fieldName;
						}
						colMeta = (TableColumnMeta) fieldsMap.get(fieldName);
						setParam(
								excelTitleMap.get(excelTitle) == null ? null
										: rowData.get(((Integer) excelTitleMap.get(excelTitle)).intValue()),
								colMeta, pst, dbType, j + 1, blobFile, charset);
						j++;
					}
					if (1 == dataSize) {
						pst.execute();
					} else {
						pst.addBatch();
						if (((rowMeter + 1) % ExcelToyConstants.getBatchSize()) == 0 || (rowMeter + 1) == data.size()) {
							logger.info("正在执行第[" + (rowMeter + 1) + "]条记录的插入,请耐心等待!");
							pst.executeBatch();
						}
					}
				} else {
					logger.error("第 [" + (rowMeter + 1) + "]行数据因外键不存在跳过!");
				}
			}
		} catch (Exception e) {
			logger.error("在执行第:\"" + (rowMeter + 1) + "\"行时发生错误，sql语句:" + insertSql, e);
			e.printStackTrace();
		} finally {
			try {
				if (pst != null)
					pst.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	/**
	 * @todo 处理sqlserver default值为((value))问题
	 * @param defaultValue
	 * @return
	 */
	private static String clearDefaultValue(String defaultValue) {
		if (defaultValue == null)
			return null;
		// 针对postgresql
		if (defaultValue.indexOf("(") != -1 && defaultValue.indexOf(")") != -1 && defaultValue.indexOf("::") != -1) {
			return defaultValue.substring(defaultValue.indexOf("(") + 1, defaultValue.indexOf("::"));
		}
		if (defaultValue.startsWith("((") && defaultValue.endsWith("))")) {
			return defaultValue.substring(2, defaultValue.length() - 2);
		}
		if (defaultValue.startsWith("(") && defaultValue.endsWith(")")) {
			return defaultValue.substring(1, defaultValue.length() - 1);
		}

		return defaultValue;
	}

	/**
	 * 
	 * @todo <b>设置PreparedStatement参数</b>
	 * @param colData
	 * @param colModel
	 * @param pst
	 * @param index
	 * @param blobFile
	 * @throws SQLException
	 */
	public static void setParam(Object colData, TableColumnMeta colModel, PreparedStatement pst, int dbType, int index,
			String blobFile, String charset) throws SQLException {
		String dataType = colModel.getTypeName().toLowerCase();
		String defaultValue = colModel.getColDefault();
		try {
			// 数据为空
			if (colData == null || StringUtil.isBlank(colData.toString())) {
				if (dataType.equals("string")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.VARCHAR);
					} else {
						pst.setString(index, defaultValue != null ? defaultValue : "");
					}
				} else if (dataType.equals("char")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.CHAR);
					} else {
						pst.setString(index, defaultValue != null ? defaultValue : "");
					}
				} else if (dataType.equals("boolean")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.BOOLEAN);
					} else {
						pst.setBoolean(index, Boolean.parseBoolean(defaultValue != null ? defaultValue : "false"));
					}
				} else if (dataType.startsWith("timestamp")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.TIMESTAMP);
					} else {
						if (defaultValue != null) {
							pst.setTimestamp(index, DateUtil.getTimestamp(
									"CURRENT_TIMESTAMP".equalsIgnoreCase(defaultValue) || defaultValue.indexOf("0") == 0
											? new Date()
											: defaultValue));
						} else {
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
						}
					}
				} else if (dataType.equals("date")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.DATE);
					} else {
						if (defaultValue != null) {
							pst.setTimestamp(index, DateUtil.getTimestamp(defaultValue));
						} else {
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
						}
					}
				} else if (dataType.equals("datetime")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.DATE);
					} else {
						if (defaultValue != null) {
							pst.setTimestamp(index, DateUtil.getTimestamp(defaultValue));
						} else {
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
						}
					}
				} else if (dataType.equals("time")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.TIME);
					} else {
						if (defaultValue != null)
							pst.setTime(index, new java.sql.Time(DateUtil.parseString(defaultValue).getTime()));
						else
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
					}
				} else if (dataType.equals("double")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.DOUBLE);
					} else {
						if (defaultValue != null) {
							pst.setDouble(index, new Double(defaultValue));
						} else {
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
						}
					}
				} else if (dataType.equals("float")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.FLOAT);
					} else {
						if (defaultValue != null)
							pst.setFloat(index, new Float(defaultValue));
						else
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
					}
				} else if (dataType.equals("number") || dataType.equals("numeric")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.NUMERIC);
					} else {
						if (defaultValue != null) {
							pst.setBigDecimal(index, new BigDecimal(defaultValue));
						} else {
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
						}
					}
				} else if (dataType.equals("byte")) {
					if (colModel.isNullable()) {
						if (dbType == DBType.POSTGRESQL)
							pst.setNull(index, java.sql.Types.BINARY);
						else
							pst.setNull(index, java.sql.Types.BIT);
					} else {
						if (defaultValue != null)
							pst.setByte(index, defaultValue.getBytes()[0]);
						else
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
					}
				} else if (dataType.equals("decimal")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.DECIMAL);
					} else {
						if (defaultValue != null) {
							pst.setBigDecimal(index, new BigDecimal(defaultValue));
						} else {
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
						}
					}
				} else if (dataType.equals("integer") || dataType.equals("int")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.INTEGER);
					} else {
						if (defaultValue != null) {
							pst.setInt(index, new Integer(defaultValue));
						} else {
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
						}
					}
				} else if (dataType.equals("long")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.DECIMAL);
					} else {
						if (defaultValue != null) {
							pst.setLong(index, new Long(defaultValue));
						} else {
							throw new SQLException("第:" + index + "列数据位空!" + "colName:" + colModel.getColName());
						}
					}
				} else if (dataType.equals("blob") || dataType.equals("bytea") || dataType.equals("image")
						|| dataType.equals("binary")) {
					if (colModel.isNullable()) {
						if (dbType == DBType.POSTGRESQL) {
							pst.setNull(index, java.sql.Types.BINARY);
						} else {
							pst.setNull(index, java.sql.Types.BLOB);
						}
					} else {
						pst.setBytes(index, "".getBytes());
					}
				} else if (dataType.equals("clob")) {
					if (colModel.isNullable()) {
						pst.setNull(index, java.sql.Types.CLOB);
					} else {
						pst.setString(index, "");
					}
				} else {
					pst.setNull(index, java.sql.Types.NULL);
				}
			} else {
				String colDataStr = colData.toString().trim();
				if (dataType.equals("string")) {
					if (colModel.getPrecision() > 0 && colDataStr.length() > colModel.getPrecision()) {
						colDataStr = colDataStr.substring(0, colModel.getPrecision());
					}
					pst.setString(index, colDataStr);
				} else if (dataType.equals("boolean")) {
					pst.setBoolean(index, Boolean.parseBoolean(colDataStr));
				} else if (dataType.startsWith("timestamp")) {
					pst.setTimestamp(index, DateUtil.getTimestamp(colDataStr));
				} else if (dataType.equals("date") || dataType.equals("datetime")) {
					if (colData instanceof java.util.Date) {
						pst.setTimestamp(index, new Timestamp(((java.util.Date) colData).getTime()));
					} else {
						pst.setTimestamp(index, DateUtil.getTimestamp(colDataStr));
					}
				} else if (dataType.equals("time")) {
					if (colData instanceof java.util.Date) {
						pst.setTime(index, new java.sql.Time(((java.util.Date) colData).getTime()));
					} else {
						pst.setTime(index, new java.sql.Time(DateUtil.parseString(colDataStr).getTime()));
					}
				} else if (dataType.equals("double")) {
					pst.setDouble(index, new Double(colDataStr));
				} else if (dataType.equals("float")) {
					if (colData instanceof Double) {
						pst.setFloat(index, ((Double) colData).floatValue());
					} else {
						pst.setFloat(index, new Float(colDataStr));
					}
				} else if (dataType.equals("short")) {
					if (colData instanceof Double) {
						pst.setShort(index, ((Double) colData).shortValue());
					} else {
						pst.setShort(index, new Short(colDataStr));
					}
				} else if (dataType.equals("byte")) {
					pst.setByte(index, colDataStr.getBytes()[0]);
				} else if (dataType.equals("number") || dataType.equals("numeric")) {
					pst.setBigDecimal(index, new BigDecimal(new Double(colDataStr)));
				} else if (dataType.equals("decimal")) {
					if (colData instanceof Double) {
						pst.setBigDecimal(index, new BigDecimal((Double) colData));
					} else {
						pst.setBigDecimal(index, new BigDecimal(colDataStr));
					}
				} else if (dataType.equals("integer") || dataType.equals("int")) {
					if (colData instanceof Double) {
						pst.setInt(index, ((Double) colData).intValue());
					} else {
						pst.setInt(index, Integer.valueOf(colDataStr));
					}
				} else if (dataType.equals("long")) {
					if (colData instanceof Double) {
						pst.setLong(index, ((Double) colData).longValue());
					} else {
						pst.setLong(index, new Long(colDataStr));
					}
				} else if (dataType.equals("clob")) {
					if (blobFile != null && isFilePath(colDataStr)) {
						File tmp = new File(colDataStr);
						pst.setString(index, FileUtil.readAsString(tmp, charset));
					} else {
						pst.setString(index, colDataStr);
					}
				} else if (dataType.equals("blob") || dataType.equals("image") || dataType.equals("bytea")
						|| dataType.equals("binary")) {
					byte[] bytes = null;
					if (blobFile != null) {
						File tmp = new File(colDataStr);
						if (tmp.exists()) {
							bytes = FileUtil.readAsByteArray(tmp);
						}
					} else {
						bytes = colDataStr.getBytes();
					}
					if (bytes != null) {
						pst.setBytes(index, bytes);
					} else {
						if (dataType.equals("blob")) {
							pst.setNull(index, java.sql.Types.BLOB);
						}
						if (dataType.equals("binary")) {
							pst.setNull(index, java.sql.Types.BINARY);
						}
						if (dataType.equals("image")) {
							pst.setNull(index, java.sql.Types.BINARY);
						}
						if (dataType.equals("bytea")) {
							pst.setNull(index, java.sql.Types.BINARY);
						}
					}
				} else {
					if (colModel.getPrecision() > 0 && colDataStr.length() > colModel.getPrecision()) {
						colDataStr = colDataStr.substring(0, colModel.getPrecision());
					}
					pst.setString(index, colDataStr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置插入值操作失败!" + e.getMessage(), e);
		}

	}

	/**
	 * 
	 * @todo 判断是否是被禁止的操作
	 * @param sql
	 * @return
	 */
	public static String isForbid(String sql) {
		if (dbModel == null || dbModel.getForbids() == null)
			return null;
		String[] forbids = dbModel.getForbids();
		String sqlTemp = StringUtil.clearMistyChars(sql, " ");
		sqlTemp = StringUtil.replaceAllStr(sqlTemp, ";", " ").toLowerCase();
		if (forbids == null || forbids.length == 0)
			return null;
		String forbid;
		for (int i = 0; i < forbids.length; i++) {
			forbid = forbids[i].trim().toLowerCase();
			if (StringUtil.indexOfIgnoreCase(sqlTemp.trim(), forbid) == 0) {
				return forbid;
			}
			if (StringUtil.indexOfIgnoreCase(sqlTemp, " " + forbid + " ") != -1)
				return forbid;
		}
		return null;
	}

	/**
	 * 
	 * @todo 对树形数据结构的表设置相关的节点路径，节点等级，是否叶子节点参数，便于树形数据检索
	 * @param treeTableModel
	 * @return
	 * @throws Exception
	 */
	public static boolean wrapTreeTableRoute(final TreeTableModel treeTableModel) throws Exception {
		// 判断是否禁止操作
		String forbid = isForbid("update");
		if (forbid != null) {
			logger.info("datasource:" + datasourceName + " 禁止" + forbid + "操作!");
			return false;
		}
		return SqlUtils.wrapTreeTableRoute(treeTableModel, connLocal.get());
	}

	/**
	 * @todo 取记录数量
	 * @param sql
	 * @param data
	 * @param colModel
	 * @return
	 * @throws SQLException
	 */
	public static int getCount(String sql, Object data, TableColumnMeta colModel) throws SQLException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int result = 0;
		try {
			pst = connLocal.get().prepareStatement(sql);
			int dbType = DataSourceUtils.getDbType(connLocal.get());
			setParam(data, colModel, pst, dbType, 1, null, null);
			rs = pst.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (pst != null)
				pst.close();
		}
		return result;

	}

	/**
	 * @todo <b>获取表的主键信息</b>
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static List getTablePrimaryKeys(String tableName) throws Exception {
		String realTableName = (tableName.indexOf(".") > 0) ? tableName.substring(tableName.indexOf(".") + 1)
				: tableName;
		ResultSet rs = connLocal.get().getMetaData().getPrimaryKeys((dbModel == null) ? null : dbModel.getCatalog(),
				(dbModel == null) ? null : dbModel.getSchema(), realTableName);
		return (List) SqlUtils.preparedStatementProcess(null, null, rs, new PreparedStatementResultHandler() {
			public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws SQLException {
				List result = new ArrayList();
				while (rs.next()) {
					result.add(rs.getString("COLUMN_NAME"));
				}
				this.setResult(result);
			}
		});
	}

	/**
	 * 
	 * @todo 调用无结果的存储过程
	 * @param store
	 */
	public static void callStore(String store) {
		CallableStatement callStat = null;
		try {
			callStat = connLocal.get().prepareCall(store);
			callStat.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @todo 判断是否是存储过程
	 * @param sql
	 * @return
	 */
	public static boolean isStore(String sql) {
		if (StringUtil.matches(sql, "\\{(\\s*\\?\\s*\\=\\s*)?call\\s+\\w+\\("))
			return true;
		return false;
	}

	/**
	 * 
	 * @todo <b>sql文件自动创建到数据库</b>
	 * @author zhongxuchen
	 * @date 2011-5-16 下午05:12:21
	 * @param sqlFile
	 * @param isAutoCommit
	 * @throws Exception
	 */
	public static void batchSqlFile(Object sqlFile, String splitSign, boolean isAutoCommit) throws Exception {
		String sqlContent;
		if (sqlFile instanceof String) {
			sqlContent = FileUtil.readAsString(new File((String) sqlFile), ExcelToyConstants.getXMLCharSet());
		} else {
			sqlContent = FileUtil.readAsString((File) sqlFile, ExcelToyConstants.getXMLCharSet());
		}
		batchSqlText(sqlContent, splitSign, isAutoCommit);
	}

	/**
	 * @todo 按照表依赖顺序获取数据库表信息
	 * @param conn
	 * @param catalog
	 * @param schema
	 * @param includes
	 * @param excludes
	 * @return
	 * @throws Exception
	 */
	public static List getTablesByOrderLink(Connection conn, String catalog, String schema, String[] includes,
			String[] excludes) throws Exception {
		List orderTables = getConnectionTableAndViews(conn, catalog, schema, new String[] { "TABLE" }, includes,
				excludes);
		if (null == orderTables || orderTables.isEmpty())
			return null;
		TableMeta tableMeta;
		int moveCnt;

		// 设置hashMap存放表对应外键信息，避免循环过程中不断的调用数据库
		HashMap tableFkMap = new HashMap();
		List tableFKs;
		String tableName;
		for (int i = 0; i < orderTables.size(); i++) {
			tableName = ((TableMeta) orderTables.get(i)).getTableName();
			if (null == tableFkMap.get(tableName)) {
				tableFKs = getTableImpForeignKeys(conn, catalog, schema, tableName);
				tableFkMap.put(tableName, tableFKs);
			} else {
				tableFKs = (List) tableFkMap.get(tableName);
			}
			moveCnt = 0;
			if (tableFKs != null && !tableFKs.isEmpty()) {
				TableConstractModel fkTable;
				// 外键关联的表从当前位置向下寻找，找到则将位置移到当前位置前面
				for (int j = 0; j < tableFKs.size(); j++) {
					fkTable = (TableConstractModel) tableFKs.get(j);
					for (int k = i + moveCnt + 1; k < orderTables.size(); k++) {
						tableMeta = (TableMeta) orderTables.get(k);
						// 位置在后面则向前移动
						if (fkTable.getFkRefTableName().equalsIgnoreCase(tableMeta.getTableName())) {
							orderTables.remove(k);
							orderTables.add(i, tableMeta);
							// 计数移动了多少个
							moveCnt++;
							break;
						}
					}
				}
			}
			if (moveCnt != 0)
				i = i - 1;
		}
		return orderTables;
	}

	public static void commit(Boolean commit) {
		try {
			Connection conn = connLocal.get();
			if (commit != null && commit && !conn.getAutoCommit()) {
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @todo <b>sql文件自动创建到数据库</b>
	 * @author zhongxuchen
	 * @date 2011-5-16 下午05:12:10
	 * @param sqlContent
	 * @param isAutoCommit
	 * @throws Exception
	 */
	public static void batchSqlText(String sqlContent, String splitSign, boolean isAutoCommit) throws Exception {
		Connection conn = connLocal.get();
		SqlUtils.executeBatchSql(conn, sqlContent,
				splitSign == null ? DataSourceUtils.getDatabaseSqlSplitSign(conn) : splitSign, isAutoCommit);
	}

	public static boolean isFilePath(String str) {
		if (str.trim().length() > 300)
			return false;
		if (new File(str).isFile())
			return true;
		return false;
	}

	private static List getConnectionTableAndViews(final Connection conn, final String catalog, final String schema,
			final String[] types, final String[] includes, final String[] excludes) throws Exception {
		int dbType = DataSourceUtils.getDbType(conn);
		PreparedStatement pst = null;
		ResultSet rs = null;
		// 数据库表注释，默认为remarks，不同数据库其名称不一样
		String commentName = "REMARKS";
		// oracle数据库
		if (dbType == DBType.ORACLE || dbType == DBType.ORACLE12) {
			pst = conn.prepareStatement("select * from user_tab_comments");
			rs = pst.executeQuery();
			commentName = "COMMENTS";
		} // mysql数据库
		else if (dbType == DBType.MYSQL || dbType == DBType.MYSQL8) {
			StringBuilder queryStr = new StringBuilder("SELECT TABLE_NAME,TABLE_SCHEMA,TABLE_TYPE,TABLE_COMMENT ");
			queryStr.append(" FROM INFORMATION_SCHEMA.TABLES where 1=1 ");
			if (schema != null)
				queryStr.append(" and TABLE_SCHEMA='").append(schema).append("'");
			if (types != null) {
				queryStr.append(" and (");
				for (int i = 0; i < types.length; i++) {
					if (i > 0) {
						queryStr.append(" or ");
					}
					queryStr.append(" TABLE_TYPE like '%").append(types[i]).append("'");
				}
				queryStr.append(")");
			}
			pst = conn.prepareStatement(queryStr.toString());
			rs = pst.executeQuery();
			commentName = "TABLE_COMMENT";
		} else {
			rs = conn.getMetaData().getTables(catalog, schema, null, types);
		}
		return (List) SqlUtils.preparedStatementProcess(commentName, pst, rs, new PreparedStatementResultHandler() {
			public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws Exception {
				List tables = new ArrayList();
				String tableName;
				// 是否包含标识，通过正则表达是判断是否是需要获取的表
				boolean is_include = false;
				while (rs.next()) {
					is_include = false;
					tableName = rs.getString("TABLE_NAME");
					if (includes != null && includes.length > 0) {
						for (int i = 0; i < includes.length; i++) {
							if (StringUtil.matches(tableName, includes[i])) {
								is_include = true;
								break;
							}
						}
					} else {
						is_include = true;
					}
					if (excludes != null && excludes.length > 0) {
						for (int j = 0; j < excludes.length; j++) {
							if (StringUtil.matches(tableName, excludes[j])) {
								is_include = false;
								break;
							}
						}
					}
					if (is_include) {
						TableMeta tableMeta = new TableMeta();
						tableMeta.setTableName(tableName);
						tableMeta.setSchema(schema);
						// tableMeta.setSchema(rs.getString("TABLE_SCHEMA"));
						tableMeta.setTableType(rs.getString("TABLE_TYPE"));
						tableMeta.setTableRemark(rs.getString(obj.toString()));
						tables.add(tableMeta);
					}
				}
				this.setResult(tables);
			}
		});
	}

	/**
	 * 
	 * @todo 获取表的外键信息
	 * @param conn
	 * @param catalog
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static List getTableImpForeignKeys(Connection conn, String catalog, String schema, String tableName)
			throws Exception {
		String realTableName = (tableName.indexOf(".") > 0) ? tableName.substring(tableName.indexOf(".") + 1)
				: tableName;
		ResultSet rs = conn.getMetaData().getImportedKeys(catalog, schema, realTableName);
		return (List) SqlUtils.preparedStatementProcess(null, null, rs, new PreparedStatementResultHandler() {
			public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws SQLException {
				List result = new ArrayList();
				while (rs.next()) {
					TableConstractModel constractModel = new TableConstractModel();
					constractModel.setFkRefTableName(rs.getString("PKTABLE_NAME"));
					constractModel.setFkColName(rs.getString("FKCOLUMN_NAME"));
					constractModel.setPkColName(rs.getString("PKCOLUMN_NAME"));
					constractModel.setUpdateRule(rs.getInt("UPDATE_RULE"));
					constractModel.setDeleteRule(rs.getInt("DELETE_RULE"));
					result.add(constractModel);
				}
				this.setResult(result);
			}
		});
	}

	/**
	 * 
	 * @todo 获取表主键被哪些表作为外键
	 * @param conn
	 * @param catalog
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static List<TableConstractModel> getTableExportKeys(Connection conn, String catalog, String schema,
			String tableName) throws Exception {
		String realTableName = (tableName.indexOf(".") > 0) ? tableName.substring(tableName.indexOf(".") + 1)
				: tableName;
		ResultSet rs = conn.getMetaData().getExportedKeys(catalog, schema, realTableName);
		return (List) SqlUtils.preparedStatementProcess(null, null, rs, new PreparedStatementResultHandler() {
			public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws SQLException {
				List<TableConstractModel> result = new ArrayList<TableConstractModel>();
				while (rs.next()) {
					TableConstractModel constractModel = new TableConstractModel();
					constractModel.setPkRefTableName(rs.getString("FKTABLE_NAME"));
					constractModel.setPkColName(rs.getString("PKCOLUMN_NAME"));
					constractModel.setPkRefColName(rs.getString("FKCOLUMN_NAME"));
					constractModel.setUpdateRule(rs.getInt("UPDATE_RULE"));
					constractModel.setDeleteRule(rs.getInt("DELETE_RULE"));
					result.add(constractModel);
				}
				this.setResult(result);
			}
		});
	}
}
