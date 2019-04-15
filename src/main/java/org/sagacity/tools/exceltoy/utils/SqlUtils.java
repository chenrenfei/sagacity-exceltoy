/**
 * 
 */
package org.sagacity.tools.exceltoy.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.model.PaginationModel;
import org.sagacity.tools.exceltoy.model.SqlWithAnalysis;
import org.sagacity.tools.exceltoy.model.TreeTableModel;
import org.sagacity.tools.exceltoy.utils.DataSourceUtils.DBType;
import org.sagacity.tools.exceltoy.utils.callback.InsertRowCallbackHandler;
import org.sagacity.tools.exceltoy.utils.callback.PreparedStatementResultHandler;
import org.sagacity.tools.exceltoy.utils.callback.RowCallbackHandler;

/**
 * @project sagacity-tools
 * @description 提供针对Exceltoy特殊使用sql公用函数,通用性不高
 * @author chenrenfei <a href="mailto:zhongxuchen@gmail.com">联系作者</a>
 * @version id:SqlUtils.java,Revision:v1.0,Date:2015年3月9日
 */
public class SqlUtils {
	/**
	 * 定义日志
	 */
	private final static Logger logger = LogManager.getLogger(SqlUtils.class);

	/**
	 * db2 脏读sql查询语句正则表达式
	 */
	public static final Pattern DB2_QUERY_UR_PATTERN = Pattern.compile("(?i)\\s+with\\s+ur\\s*$");

	/**
	 * db2 是否查詢時是否增加with ur,db2新的版本不需要增加
	 */
	public static final String DB2_QUERY_UR_FLAG = "db2.search.with.ur";

	public static final String DB2_QUERY_APPEND = " with ur";

	// top 匹配模式
	public static final Pattern TOP_PATTERN = Pattern.compile("(?i)^select\\s+top\\s+");
	// distinct 匹配模式
	public static final Pattern DISTINCT_PATTERN = Pattern.compile("(?i)^select\\s+distinct\\s+");

	// union 匹配模式
	public static final Pattern UNION_PATTERN = Pattern.compile("(?i)\\W+union\\W+");

	/**
	 * sql中的单行注释
	 */
	private final static Pattern maskPattern = Pattern.compile("\\/\\*[^(+|!)]");

	/**
	 * alibaba druid clob
	 */
	private static final String ALIBABA_DRUID_JDBC_CLOBPROXY = "com.alibaba.druid.proxy.jdbc.ClobProxyImpl";

	/**
	 * alibaba druid nclob
	 */
	private static final String ALIBABA_DRUID_JDBC_NCLOBPROXY = "com.alibaba.druid.proxy.jdbc.NClobProxyImpl";

	// sql 注释过滤器
	private static HashMap sqlCommentfilters = new HashMap();

	static {
		// 排除表字段说明（注释）中的";"符号
		sqlCommentfilters.put("'", "'");
		sqlCommentfilters.put("(", ")");
		sqlCommentfilters.put("{", "}");
	}

	/**
	 * @todo <b>jdbc分页查询</b>
	 * @param queryStr
	 * @param paramsValue
	 * @param paginationModel
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static PaginationModel findPageByJdbc(final String queryStr, final Object[] paramsValue,
			final PaginationModel paginationModel, final Connection conn) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("findPageByJdbc:分页查询sql为:".concat(queryStr));
		int dbType = DataSourceUtils.getDbType(conn);
		PaginationModel result = null;
		switch (dbType) {
		// 使用不同数据库提供的自身分页机制进行分页查询
		// iq15.4支持limit offset
		case DBType.SYBASE_IQ:
		case DBType.ORACLE:
		case DBType.ORACLE12:
		case DBType.DB2:
		case DBType.DB2_11:
		case DBType.SQLSERVER:
		case DBType.SQLSERVER2017:
		case DBType.SQLSERVER2014:
		case DBType.SQLSERVER2016:
		case DBType.POSTGRESQL:
		case DBType.POSTGRESQL10:
		case DBType.MYSQL:
		case DBType.MYSQL8:
		case DBType.SQLITE:
			result = findPageByNative(queryStr, paramsValue, paginationModel, conn, dbType);
			break;
		// access 之类数据库采用游标(基本没有人用这类数据库,就不去研究怎么分页了)
		default:
			result = findPageByCursor(queryStr, paramsValue, paginationModel, conn, dbType);
			break;
		}
		if (logger.isDebugEnabled())
			logger.debug("findPageByJdbc" + (result == null ? "结果为空"
					: "记录条数:" + result.getRecordCount() + "共" + result.getTotalPage() + "页!"));
		return result;
	}

	/**
	 * @todo <b>oracle,mysql,db2,sqlserver2005+原生数据库支持的分页查询</b>
	 * @param queryStr
	 * @param params
	 * @param paginationModel
	 * @param dbType
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private static PaginationModel findPageByNative(final String queryStr, final Object[] params,
			final PaginationModel paginationModel, final Connection conn, final int dbType) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		return (PaginationModel) preparedStatementProcess(null, pst, rs, new PreparedStatementResultHandler() {
			public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws Exception {
				long recordCount = getJdbcRecordCount(queryStr, params, conn, dbType);
				// 总记录数为零则不需要再查询
				if (recordCount == 0) {
					this.setResult(new PaginationModel(null, 0, paginationModel.getPageSize(), 1));
					return;
				}
				long realStartPage = 1;
				// 取resultSet结果开始列,一些数据库分页会产生辅助的列，在提取结果时要排除掉
				int startColIndex = 0;
				StringBuilder pageSql = new StringBuilder(queryStr.length() + 100);
				// 组织分页sql语句
				if (paginationModel.getPageNo() != -1) {
					SqlWithAnalysis withSql = new SqlWithAnalysis(queryStr);
					String rejectWithSql = withSql.getRejectWithSql();
					realStartPage = (paginationModel.getPageNo() * paginationModel.getPageSize()) >= (recordCount
							+ paginationModel.getPageSize()) ? 1 : paginationModel.getPageNo();
					switch (dbType) {
					// oracle
					case DBType.ORACLE:
						pageSql.append("SELECT * FROM (SELECT ROWNUM page_row_id,SAG_Paginationtable.* FROM ( ");
						pageSql.append(rejectWithSql);

						/*
						 * sql中不存在排序，因为oracle排序的查询机制通过ROWNUM<=?每次查出的结果可能不一样 ， 请参见ROWNUM机制以及oracle SORT
						 * ORDER BY STOPKEY
						 */
						if (!hasOrderBy(rejectWithSql, true)) {
							pageSql.append(" ) SAG_Paginationtable where ROWNUM <=? ");
							pageSql.append(" ) WHERE  page_row_id >? ");
						} else {
							pageSql.append(" ) SAG_Paginationtable ");
							pageSql.append(" ) WHERE  page_row_id<=? and page_row_id >? ");
						}
						startColIndex = 1;
						break;
					// db2
					case DBType.DB2:
						pageSql.append("SELECT t_sag_pageTable.* FROM ");
						pageSql.append("(SELECT rownumber() over() as page_row_id,SAG_Paginationtable.* ");
						pageSql.append(" FROM ( ");
						pageSql.append(rejectWithSql);
						pageSql.append(" ) SAG_Paginationtable ) t_sag_pageTable ");
						pageSql.append("where t_sag_pageTable.page_row_id <=? and t_sag_pageTable.page_row_id >?");
						// db2 排除正在修改和插入的数据
						if (appendWithUR())
							pageSql.append(DB2_QUERY_APPEND);
						startColIndex = 1;
						break;
					// sqlserver2012
					case DBType.SQLSERVER:
					case DBType.SQLSERVER2017:
					case DBType.SQLSERVER2014:
					case DBType.SQLSERVER2016:
					case DBType.ORACLE12:
						pageSql.append(rejectWithSql);
						pageSql.append(" offset ? rows fetch next ? rows only");
						break;
					// mysql
					case DBType.MYSQL:
					case DBType.MYSQL8:
						pageSql.append(rejectWithSql);
						pageSql.append(" limit ?,? ");
						break;
					// postgresql,sqlite
					case DBType.POSTGRESQL:
					case DBType.POSTGRESQL10:
					case DBType.SQLITE:
					case DBType.SYBASE_IQ:
						pageSql.append(rejectWithSql);
						pageSql.append(" limit ? offset ?");
						break;
					}
					pageSql.insert(0, withSql.getWithSql());
				} else
					pageSql.append(queryStr);

				if (logger.isDebugEnabled())
					logger.debug("分页sql:" + pageSql.toString());
				pst = conn.prepareStatement(pageSql.toString());

				// informix数据库参数从2开始
				int paramIndex = 0;
				// 设置分页时的查询参数
				if (paginationModel.getPageNo() != -1) {
					int paramLength = (params == null) ? 0 : params.length;
					switch (dbType) {
					// mysql or sqlserver2012
					case DBType.MYSQL:
					case DBType.MYSQL8:
					case DBType.SQLSERVER:
					case DBType.SQLSERVER2014:
					case DBType.SQLSERVER2016:
					case DBType.SQLSERVER2017:
					case DBType.ORACLE12:
						pst.setLong(paramLength + 1, (realStartPage - 1) * paginationModel.getPageSize());
						pst.setLong(paramLength + 2, paginationModel.getPageSize());
						break;
					// postgresql
					case DBType.POSTGRESQL:
					case DBType.POSTGRESQL10:
					case DBType.SQLITE:
						pst.setLong(paramLength + 1, paginationModel.getPageSize());
						pst.setLong(paramLength + 2, (realStartPage - 1) * paginationModel.getPageSize());
						break;
					// oracle、db2
					default:
						pst.setLong(paramLength + 1, realStartPage * paginationModel.getPageSize());
						pst.setLong(paramLength + 2, (realStartPage - 1) * paginationModel.getPageSize());
						break;
					}
				}

				// 设置查询参数
				if (params != null && params.length > 0)
					setParamsValue(conn, pst, params, null, paramIndex);

				rs = pst.executeQuery();
				this.setResult(processPageResultSet(rs, paginationModel, recordCount, realStartPage, startColIndex));
			}
		});
	}

	/**
	 * 
	 * @todo <b>本方法一般针对非主流数据库没有提供分页机制的查询, 采用游标方式进行分页,
	 *       使用游标建议采用新的jdbc3.0驱动程序，低版本对absoult等操作缺乏很好的支持
	 *       一般不建议采取通过游标的方式实现分页，游标分页当数据量非常大时易造成内存溢出</b>
	 * @param queryStr
	 * @param params
	 * @param paginationModel
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	private static PaginationModel findPageByCursor(final String queryStr, final Object[] params,
			final PaginationModel paginationModel, final Connection conn, final int dbType) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		return (PaginationModel) preparedStatementProcess(null, pst, rs, new PreparedStatementResultHandler() {
			public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws Exception {
				long recordCount = getJdbcRecordCount(queryStr, params, conn, dbType);
				// 总记录数为零则不需要再查询，直接返回
				if (recordCount == 0) {
					this.setResult(new PaginationModel(null, 0, paginationModel.getPageSize(), 1));
					return;
				}
				long realStartPage = 0;
				// 获取记录总笔数
				if (paginationModel.getPageNo() != -1) {
					realStartPage = (paginationModel.getPageNo() * paginationModel.getPageSize()) >= (recordCount
							+ paginationModel.getPageSize()) ? 1 : paginationModel.getPageNo();
				} else {
					recordCount = 0;
					realStartPage = -1;
				}

				pst = conn.prepareStatement(queryStr);
				// 设置条件参数
				setParamsValue(conn, pst, params, null, 0);

				// 设置最大查询行
				if (realStartPage != -1)
					pst.setMaxRows((new Long(realStartPage * paginationModel.getPageSize())).intValue());
				rs = pst.executeQuery();

				if (realStartPage > 1) {
					// 将游标移动到第一条记录
					rs.beforeFirst();
					// 游标移动到要输出的第一条记录
					rs.absolute(new Long((realStartPage - 1) * paginationModel.getPageSize()).intValue());
				}
				this.setResult(SqlUtils.processPageResultSet(rs, paginationModel, recordCount, realStartPage, 0));
			}
		});
	}

	/**
	 * @todo 获取分页查询中总记录数
	 * @param queryStr
	 * @param params
	 * @param conn
	 * @param dbType
	 * @return
	 * @throws SQLException
	 */
	public static Long getJdbcRecordCount(final String queryStr, final Object[] params, final Connection conn,
			final int dbType) throws Exception {
		final SqlWithAnalysis sqlWith = new SqlWithAnalysis(queryStr);
		// 用空白符号替代tab、回车、换行
		String query_tmp = StringUtil.clearMistyChars(sqlWith.getRejectWithSql(), " ").trim();
		// 是否包含top
		if ((dbType == DBType.SQLSERVER || dbType == DBType.SQLSERVER2014 || dbType == DBType.SQLSERVER2016
				|| dbType == DBType.SQLSERVER2017) && StringUtil.matches(query_tmp, TOP_PATTERN)) {
			String topAfter = query_tmp.substring(StringUtil.indexOfIgnoreCase(query_tmp, "top") + 3).trim();
			return Long.decode(topAfter.substring(0, topAfter.indexOf(" ")).trim());
		}

		int lastBracketIndex = query_tmp.lastIndexOf(")");
		// 剔除order提高运行效率
		int orderByIndex = StringUtil.lastIndexOfIgnoreCase(query_tmp, " order ");
		if (orderByIndex > lastBracketIndex)
			query_tmp = query_tmp.substring(0, orderByIndex);
		int groupIndex = StringUtil.lastIndexOfIgnoreCase(query_tmp, " group ");
		final StringBuilder countQueryStr = new StringBuilder();
		// 是否包含union,update 2012-11-21
		boolean hasUnion = StringUtil.matches(query_tmp, UNION_PATTERN);
		// 不包含distinct和group by
		if (!StringUtil.matches(query_tmp.trim(), DISTINCT_PATTERN) && !hasUnion
				&& (groupIndex == -1 || groupIndex < lastBracketIndex)) {
			int sql_from_index = 0;
			// sql不以from开头，截取from 后的部分语句
			if (StringUtil.indexOfIgnoreCase(query_tmp, "from") != 0)
				sql_from_index = StringUtil.getSymMarkMatchIndex("(?i)select\\s+", "(?i)\\s+from[\\(|\\s+]", query_tmp,
						0);
			// 截取from后的部分
			countQueryStr.append("select count(1) ")
					.append((sql_from_index != -1 ? query_tmp.substring(sql_from_index) : query_tmp));
		} // 包含distinct 或包含union则直接将查询作为子表
		else {
			countQueryStr.append("select count(1) from (").append(query_tmp).append(") sag_count_tmpTable ");
		}
		// 只针对db2,将查询作为脏读模式
		if (dbType == DBType.DB2 && appendWithUR()) {
			if (!StringUtil.matches(query_tmp, DB2_QUERY_UR_PATTERN))
				countQueryStr.append(DB2_QUERY_APPEND);
		}
		final int paramCnt = getParamsCount(countQueryStr.toString(), true);
		final int withParamCnt = getParamsCount(sqlWith.getWithSql(), true);
		countQueryStr.insert(0, sqlWith.getWithSql());
		if (logger.isDebugEnabled())
			logger.debug("获取记录总数的语句:countQueryStr=" + countQueryStr.toString());

		PreparedStatement pst = conn.prepareStatement(countQueryStr.toString());
		ResultSet rs = null;
		return (Long) preparedStatementProcess(null, pst, rs, new PreparedStatementResultHandler() {
			public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws SQLException, IOException {
				long resultCount = 0;
				if (params != null && params.length > 0) {
					Object[] realParams = CollectionUtil.convertArray(params);
					// 将from前的参数剔除
					realParams = CollectionUtil.subtractArray(realParams, withParamCnt,
							realParams.length - paramCnt - withParamCnt);
					setParamsValue(conn, pst, realParams, null, 0);
				}
				rs = pst.executeQuery();
				while (rs.next())
					resultCount = rs.getLong(1);
				this.setResult(resultCount);
			}
		});
	}

	/**
	 * @todo 提供统一的ResultSet,PreparedStatemenet 关闭功能
	 * @param userData
	 * @param pst
	 * @param rs
	 * @param preparedStatementResultHandler
	 * @return
	 */
	public static Object preparedStatementProcess(Object userData, PreparedStatement pst, ResultSet rs,
			PreparedStatementResultHandler preparedStatementResultHandler) throws Exception {
		try {
			preparedStatementResultHandler.execute(userData, pst, rs);
		} catch (Exception se) {
			logger.error(se.getMessage(), se);
			throw se;
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pst != null) {
					pst.close();
					pst = null;
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return preparedStatementResultHandler.getResult();
	}

	/**
	 * 
	 * @todo <b>判断sql语句中是否有order by排序</b>
	 * @param sql
	 * @param judgeUpcase
	 * @return
	 */
	public static boolean hasOrderBy(String sql, boolean judgeUpcase) {
		// 最后的收括号位置
		int lastBracketIndex = sql.lastIndexOf(")");
		boolean result = false;
		int orderByIndex = StringUtil.matchLastIndex(sql, "(?i)\\Worder\\s+by\\s+");
		if (orderByIndex > lastBracketIndex)
			result = true;
		// 特殊处理 order by，通过ORder这种非常规写法代表
		if (judgeUpcase) {
			int upcaseOrderBy = StringUtil.matchLastIndex(sql, "\\WORder\\s+");
			if (upcaseOrderBy > lastBracketIndex)
				result = false;
		}
		return result;
	}

	/**
	 * @todo 判断是否复杂分页查询(union,多表关联、存在top 、distinct等)
	 * @param queryStr
	 * @return
	 */
	private static boolean isComplexPageQuery(String queryStr) {
		String tmpQuery = StringUtil.clearMistyChars(queryStr.toLowerCase(), " ");
		boolean isComplexQuery = hasUnion(tmpQuery, false);
		// from 和 where之间有","表示多表查询
		if (!isComplexQuery) {
			int fromIndex = StringUtil.getSymMarkMatchIndex("(?i)select\\s+", "(?i)\\s+from[\\(|\\s+]", tmpQuery, 0);
			int fromWhereIndex = StringUtil.getSymMarkMatchIndex("\\s+from[\\(|\\s+]", "\\s+where[\\(|\\s+]", tmpQuery,
					fromIndex - 1);
			String fromLastStr = (fromWhereIndex == -1) ? tmpQuery.substring(fromIndex)
					: tmpQuery.substring(fromIndex, fromWhereIndex).toLowerCase();
			if (fromLastStr.indexOf(",") != -1 || fromLastStr.indexOf(" join ") != -1 || fromLastStr.indexOf("(") != -1)
				isComplexQuery = true;

			// 不存在union且非复杂关联查询
			if (!isComplexQuery) {
				// 截取select 到 from之间的字段
				String tmpColumn = tmpQuery.substring(0, fromIndex);
				if (tmpColumn.indexOf(" top ") != -1 || tmpColumn.indexOf(" distinct ") != -1)
					isComplexQuery = true;
			}
		}
		return isComplexQuery;
	}

	/**
	 * @todo 处理分页查询对应的结果集,构造paginationModel分页数据模型
	 * @param rs
	 * @param pageModel
	 * @param recordCount
	 * @param realStartPage
	 * @param startColIndex
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static PaginationModel processPageResultSet(ResultSet rs, PaginationModel pageModel, long recordCount,
			long realStartPage, int startColIndex) throws Exception {
		// 取得字段列数,在没有rowCallbackHandler用数组返回
		int rowCnt = rs.getMetaData().getColumnCount();
		List items = new ArrayList();
		// 存在查询字段的标题
		List titleLabel = new ArrayList();
		for (int i = startColIndex; i < rowCnt; i++)
			titleLabel.add(rs.getMetaData().getColumnLabel(i + 1).toUpperCase());
		items.add(titleLabel);
		Object fieldValue = null;
		while (rs.next()) {
			List rowData = new ArrayList();
			for (int i = startColIndex; i < rowCnt; i++) {
				// 处理clob
				fieldValue = rs.getObject(i + 1);
				if (fieldValue != null && fieldValue instanceof java.sql.Clob) {
					fieldValue = clobToString((java.sql.Clob) fieldValue);
				}
				rowData.add(fieldValue);
			}
			items.add(rowData);
		}

		// 构造分页模型
		PaginationModel resultPageModel = new PaginationModel(items,
				(pageModel.getPageNo() != -1) ? recordCount : items.size(),
				(pageModel.getPageNo() != -1) ? (realStartPage - 1) * pageModel.getPageSize() + 1 : 1);

		resultPageModel.setPageSize(pageModel.getPageSize());
		if (pageModel.getPageNo() != -1)
			resultPageModel.setPageNo(realStartPage);
		else
			resultPageModel.setPageNo(-1);
		return resultPageModel;
	}

	/**
	 * @todo 根据sagacity.properties配置判定db2 查询语句后面是否需要增加 with ur
	 * @return
	 */
	private static boolean appendWithUR() {
		String flag = ExcelToyConstants.getKeyValue(DB2_QUERY_UR_FLAG);
		if (flag == null)
			return true;
		else
			return Boolean.parseBoolean(flag);
	}

	/**
	 * @todo clob转换成字符串
	 * @param clob
	 * @return
	 */
	private static String clobToString(Clob clob) {
		if (clob == null)
			return null;
		StringBuffer sb = new StringBuffer(1024 * 8);// 8K
		Reader clobStream = null;
		try {
			clobStream = clob.getCharacterStream();
			char[] b = new char[1024];// 每次获取1K
			int i = 0;
			while ((i = clobStream.read(b)) != -1) {
				sb.append(b, 0, i);
			}
		} catch (Exception ex) {
			sb = null;
		} finally {
			IOUtil.closeQuietly(clobStream);
		}
		if (sb == null)
			return null;
		else
			return sb.toString();
	}

	/**
	 * @todo 判断是否内包含union 查询,即是否是select * from (select * from t union select * from
	 *       t2 ) 形式的查询,将所有()剔除后判定是否有union 存在
	 * @param sql
	 * @return
	 */
	private static boolean hasUnion(String sql, boolean clearMistyChar) {
		StringBuilder lastSql = new StringBuilder(
				clearMistyChar ? StringUtil.clearMistyChars(sql.toLowerCase(), " ") : sql);
		// 找到第一个select 所对称的from位置，排查掉子查询中的内容
		// int fromIndex = StringUtil.getSymMarkIndex("select ", " from", sql, 0);
		int fromIndex = StringUtil.getSymMarkMatchIndex("select\\s+", "\\s+from[\\(|\\s+]", sql.toLowerCase(), 0);
		lastSql.delete(0, fromIndex);
		// 删除所有对称的括号中的内容
		int start = lastSql.indexOf("(");
		int symMarkEnd;
		while (start != -1) {
			symMarkEnd = StringUtil.getSymMarkIndex("(", ")", lastSql.toString(), start);
			if (symMarkEnd != -1) {
				lastSql.delete(start, symMarkEnd + 1);
				start = lastSql.indexOf("(");
			} else
				break;
		}
		if (StringUtil.matches(lastSql.toString(), UNION_PATTERN))
			return true;
		return false;
	}

	/**
	 * 
	 * @todo 剔除sql中的注释(提供三种形态的注释剔除)
	 * @param sql
	 * @return
	 */
	public static String clearMark(String sql) {
		if (StringUtil.isBlank(sql))
			return sql;
		int endMarkIndex;
		// 剔除<!-- -->形式的多行注释
		int markIndex = sql.indexOf("<!--");
		while (markIndex != -1) {
			endMarkIndex = sql.indexOf("-->", markIndex);
			if (endMarkIndex == -1 || endMarkIndex == sql.length() - 3) {
				sql = sql.substring(0, markIndex);
				break;
			} else
				// update 2017-6-5
				sql = sql.substring(0, markIndex).concat(" ").concat(sql.substring(endMarkIndex + 3));
			markIndex = sql.indexOf("<!--");
		}
		// 剔除/* */形式的多行注释(如果是/*+ALL_ROWS*/ 或 /*! ALL_ROWS*/形式的诸如oracle hint的用法不看作是注释)
		markIndex = StringUtil.matchIndex(sql, maskPattern);
		while (markIndex != -1) {
			endMarkIndex = sql.indexOf("*/", markIndex);
			if (endMarkIndex == -1 || endMarkIndex == sql.length() - 2) {
				sql = sql.substring(0, markIndex);
				break;
			} else
				// update 2017-6-5
				sql = sql.substring(0, markIndex).concat(" ").concat(sql.substring(endMarkIndex + 2));
			markIndex = StringUtil.matchIndex(sql, maskPattern);
		}
		// 剔除单行注释
		markIndex = sql.indexOf("--");
		while (markIndex != -1) {
			// 换行符号
			endMarkIndex = sql.indexOf("\n", markIndex);
			if (endMarkIndex == -1 || endMarkIndex == sql.length() - 1) {
				sql = sql.substring(0, markIndex);
				break;
			} else
				// update 2017-6-5 增加concat(" ")避免因换行导致sql语句直接相连
				sql = sql.substring(0, markIndex).concat(" ").concat(sql.substring(endMarkIndex + 1));
			markIndex = sql.indexOf("--");
		}
		return sql;
	}

	/**
	 * @todo 自动进行类型转换,设置sql中的参数条件的值
	 * @param conn
	 * @param pst
	 * @param params
	 * @param paramsType
	 * @param fromIndex
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void setParamsValue(Connection conn, PreparedStatement pst, Object[] params, Integer[] paramsType,
			int fromIndex) throws SQLException, IOException {
		if (null != params && params.length > 0) {
			if (null == paramsType || paramsType.length == 0)
				for (int i = 0, n = params.length; i < n; i++)
					setParamValue(conn, pst, params[i], -1, fromIndex + 1 + i);
			else
				for (int i = 0, n = params.length; i < n; i++)
					setParamValue(conn, pst, params[i], paramsType[i], fromIndex + 1 + i);
		}
	}

	/**
	 * @todo 自动进行类型转换,设置sql中的参数条件的值
	 * @param conn
	 * @param pst
	 * @param params
	 * @param paramsType
	 * @param fromIndex
	 * @param endIndex
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void setParamsValue(Connection conn, PreparedStatement pst, Object[] params, Integer[] paramsType,
			int fromIndex, int endIndex) throws SQLException, IOException {
		if (null != params && params.length > 0) {
			if (null == paramsType || paramsType.length == 0)
				for (int i = 0; i < endIndex; i++)
					setParamValue(conn, pst, params[i], -1, fromIndex + 1 + i);
			else
				for (int i = 0; i < endIndex; i++)
					setParamValue(conn, pst, params[i], paramsType[i], fromIndex + 1 + i);
		}
	}

	/**
	 * update 2017-6-14 修复使用driud数据库dataSource时clob处理的错误
	 * 
	 * @todo 设置sql中的参数条件的值
	 * @param conn
	 * @param pst
	 * @param paramValue
	 * @param jdbcType
	 * @param paramIndex
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void setParamValue(Connection conn, PreparedStatement pst, Object paramValue, int jdbcType,
			int paramIndex) throws SQLException, IOException {
		// jdbc部分数据库赋null值时必须要指定数据类型
		String tmpStr;
		if (null == paramValue) {
			if (jdbcType != -1)
				pst.setNull(paramIndex, jdbcType);
			else
				pst.setNull(paramIndex, java.sql.Types.NULL);
		} else {
			if (paramValue instanceof java.lang.String) {
				tmpStr = (String) paramValue;
				if (jdbcType == java.sql.Types.CLOB) {
					try {
						Clob clob = conn.createClob();
						// 针对druid clob类型进行兼容，通过反射模式解决对druid库的强依赖问题
						String className = clob.getClass().getName();
						if (ALIBABA_DRUID_JDBC_CLOBPROXY.equals(className)) {
							Method method = clob.getClass().getMethod("getRawClob", null);
							clob = (Clob) method.invoke(clob, null);
						}
						clob.setString(1, tmpStr);
						pst.setClob(paramIndex, clob);
					} catch (Exception e) {
						pst.setString(paramIndex, tmpStr);
					}
				} else if (jdbcType == java.sql.Types.NCLOB) {
					try {
						NClob nclob = conn.createNClob();
						String className = nclob.getClass().getName();
						if (ALIBABA_DRUID_JDBC_NCLOBPROXY.equals(className)) {
							Method method = nclob.getClass().getMethod("getRawNClob", null);
							nclob = (NClob) method.invoke(nclob, null);
						}
						nclob.setString(1, tmpStr);
						pst.setNClob(paramIndex, nclob);
					} catch (Exception e) {
						pst.setString(paramIndex, tmpStr);
					}
				} else {
					pst.setString(paramIndex, tmpStr);
				}
			} else if (paramValue instanceof java.lang.Integer) {
				pst.setInt(paramIndex, ((Integer) paramValue).intValue());
			} else if (paramValue instanceof java.lang.Double) {
				pst.setDouble(paramIndex, ((Double) paramValue).doubleValue());
			} else if (paramValue instanceof java.sql.Timestamp) {
				pst.setTimestamp(paramIndex, (java.sql.Timestamp) paramValue);
			} else if (paramValue instanceof java.sql.Time) {
				pst.setTime(paramIndex, (java.sql.Time) paramValue);
			} else if (paramValue instanceof java.util.Date) {
				pst.setTimestamp(paramIndex, new Timestamp(((java.util.Date) paramValue).getTime()));
			} else if (paramValue instanceof java.lang.Long) {
				pst.setLong(paramIndex, ((Long) paramValue).longValue());
			} else if (paramValue instanceof java.lang.Boolean) {
				pst.setBoolean(paramIndex, (Boolean) paramValue);
			} else if (paramValue instanceof BigDecimal) {
				pst.setBigDecimal(paramIndex, (BigDecimal) paramValue);
			} else if (paramValue instanceof java.sql.Clob) {
				tmpStr = clobToString((java.sql.Clob) paramValue);
				pst.setString(paramIndex, tmpStr);
			} else if (paramValue instanceof java.sql.Blob) {
				Blob tmp = (java.sql.Blob) paramValue;
				pst.setBytes(paramIndex, tmp.getBytes(0, new Long(tmp.length()).intValue()));
			} else if (paramValue instanceof java.sql.Date) {
				pst.setTimestamp(paramIndex, new Timestamp(((java.sql.Date) paramValue).getTime()));
			} else if (paramValue instanceof java.lang.Character) {
				tmpStr = ((Character) paramValue).toString();
				pst.setString(paramIndex, tmpStr);
			} else if (paramValue instanceof java.lang.Byte) {
				pst.setByte(paramIndex, (Byte) paramValue);
			} else if (paramValue instanceof byte[]) {
				if (jdbcType == java.sql.Types.BLOB) {
					Blob blob = null;
					try {
						blob = conn.createBlob();
						OutputStream out = blob.setBinaryStream(1);
						out.write((byte[]) paramValue);
						out.flush();
						out.close();
						pst.setBlob(paramIndex, blob);
					} catch (Exception e) {
						pst.setBytes(paramIndex, (byte[]) paramValue);
					}
				} else
					pst.setBytes(paramIndex, (byte[]) paramValue);
			} else if (paramValue instanceof java.lang.Short) {
				pst.setShort(paramIndex, (java.lang.Short) paramValue);
			} else if (paramValue instanceof java.lang.Float) {
				pst.setFloat(paramIndex, ((Float) paramValue).floatValue());
			} else {
				if (jdbcType != -1)
					pst.setObject(paramIndex, paramValue, jdbcType);
				else
					pst.setObject(paramIndex, paramValue);
			}
		}
	}

	/**
	 * @todo 获取查询sql语句中参数的个数
	 * @param queryStr
	 * @param skipField
	 *            是否跳过from前的字段中的参数，如:select ? from
	 * @return
	 */
	private static int getParamsCount(String queryStr, boolean skipField) {
		int paramCnt = 0;
		if (StringUtil.isBlank(queryStr))
			return paramCnt;
		// 将回车、换行、tab换成空白
		String tmpStr = StringUtil.clearMistyChars(queryStr, " ");
		int fromIndex = StringUtil.indexOfIgnoreCase(tmpStr, " from ");
		// 是否跳过from前的field
		if (skipField && fromIndex != -1) {
			tmpStr = tmpStr.substring(fromIndex);
		}
		// 判断sql中参数模式，?或:named 模式，两种模式不可以混合使用
		String sign = "?";
		if (tmpStr.indexOf("?") == -1)
			sign = ":";
		int index = 0;
		while ((index = tmpStr.indexOf(sign, index + 1)) != -1) {
			paramCnt++;
		}
		return paramCnt;
	}

	/**
	 * 
	 * @todo 处理sql查询时的结果集,当没有反调或voClass反射处理时以数组方式返回resultSet的数据
	 * @param rs
	 *            ResultSet
	 * @param voClass
	 * @param rowCallbackHandler
	 * @param startColIndex
	 * @return
	 * @throws SQLException
	 */
	public static List processResultSet(ResultSet rs, int startColIndex) throws Exception {
		List result;
		// 取得字段列数,在没有rowCallbackHandler用数组返回
		int rowCnt = rs.getMetaData().getColumnCount();
		List items = new ArrayList();
		Object fieldValue = null;
		while (rs.next()) {
			List rowData = new ArrayList();
			for (int i = startColIndex; i < rowCnt; i++) {
				// 处理clob
				fieldValue = rs.getObject(i + 1);
				if (fieldValue != null && fieldValue instanceof java.sql.Clob) {
					fieldValue = clobToString((java.sql.Clob) fieldValue);
				}
				rowData.add(fieldValue);
			}
			items.add(rowData);
		}
		result = items;
		return result;
	}

	/**
	 * @todo 计算树形结构表中的:节点层级、节点对应所有上级节点的路径、是否叶子节点
	 * @param treeTableModel
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static boolean wrapTreeTableRoute(final TreeTableModel treeTableModel, Connection conn) throws Exception {
		if (StringUtil.isBlank(treeTableModel.getTableName()) || StringUtil.isBlank(treeTableModel.getIdField())
				|| StringUtil.isBlank(treeTableModel.getPidField())) {
			logger.error("请设置树形表的table名称、id字段名称、pid字段名称!");
			throw new Exception("没有对应的table名称、id字段名称、pid字段名称");
		}
		String flag = "";
		// 判断是否字符串类型
		if (treeTableModel.isChar()) {
			flag = "'";
		}
		// 修改nodeRoute和nodeLevel
		if (StringUtil.isNotBlank(treeTableModel.getNodeRouteField())
				&& StringUtil.isNotBlank(treeTableModel.getNodeLevelField())) {
			StringBuilder nextNodeQueryStr = new StringBuilder("select ").append(treeTableModel.getIdField())
					.append(",").append(treeTableModel.getNodeRouteField()).append(",")
					.append(treeTableModel.getPidField()).append(" from ").append(treeTableModel.getTableName())
					.append(" where ").append(treeTableModel.getPidField()).append(" in (${inStr})");
			String idInfoSql = "select ".concat(treeTableModel.getNodeLevelField()).concat(",")
					.concat(treeTableModel.getNodeRouteField()).concat(" from ").concat(treeTableModel.getTableName())
					.concat(" where ").concat(treeTableModel.getIdField()).concat("=").concat(flag)
					.concat(treeTableModel.getRootId().toString()).concat(flag);
			if (StringUtil.isNotBlank(treeTableModel.getConditions())) {
				idInfoSql = idInfoSql.concat(" and ").concat(treeTableModel.getConditions());
			}
			// 获取层次等级
			List idInfo = findByJdbcQuery(idInfoSql, null, null, null, conn);
			// 设置第一层level
			int nodeLevel = 0;
			String nodeRoute = "";
			if (idInfo != null && !idInfo.isEmpty()) {
				nodeLevel = Integer.parseInt(((List) idInfo.get(0)).get(0).toString());
				nodeRoute = ((List) idInfo.get(0)).get(1).toString();
			}
			StringBuilder updateLevelAndRoute = new StringBuilder("update ").append(treeTableModel.getTableName())
					.append(" set ").append(treeTableModel.getNodeLevelField()).append("=?,")
					.append(treeTableModel.getNodeRouteField()).append("=? ").append(" where ")
					.append(treeTableModel.getIdField()).append("=?");
			if (StringUtil.isNotBlank(treeTableModel.getConditions())) {
				nextNodeQueryStr.append(" and ").append(treeTableModel.getConditions());
				updateLevelAndRoute.append(" and ").append(treeTableModel.getConditions());
			}

			// 模拟指定节点的信息
			HashMap pidsMap = new HashMap();
			pidsMap.put(treeTableModel.getRootId().toString(), nodeRoute);
			// 下级节点
			List ids;
			if (treeTableModel.getIdValue() != null) {
				StringBuilder firstNextNodeQuery = new StringBuilder("select ").append(treeTableModel.getIdField())
						.append(",").append(treeTableModel.getNodeRouteField()).append(",")
						.append(treeTableModel.getPidField()).append(" from ").append(treeTableModel.getTableName())
						.append(" where ").append(treeTableModel.getIdField()).append("=?");
				if (StringUtil.isNotBlank(treeTableModel.getConditions())) {
					firstNextNodeQuery.append(" and ").append(treeTableModel.getConditions());
				}
				ids = findByJdbcQuery(firstNextNodeQuery.toString(), new Object[] { treeTableModel.getIdValue() }, null,
						null, conn);
			} else
				ids = findByJdbcQuery(nextNodeQueryStr.toString().replaceFirst("\\$\\{inStr\\}",
						flag + treeTableModel.getRootId() + flag), null, null, null, conn);
			if (ids != null && !ids.isEmpty()) {
				processNextLevel(updateLevelAndRoute.toString(), nextNodeQueryStr.toString(), treeTableModel, pidsMap,
						ids, nodeLevel + 1, conn);
			}
		}
		// 设置节点是否为叶子节点，（mysql不支持update table where in 机制）
		if (StringUtil.isNotBlank(treeTableModel.getLeafField())) {
			// 将所有记录先全部设置为叶子节点(isLeaf=1)
			StringBuilder updateLeafSql = new StringBuilder();
			updateLeafSql.append("update ").append(treeTableModel.getTableName());
			updateLeafSql.append(" set ").append(treeTableModel.getLeafField()).append("=1");
			// 附加条件(保留)
			if (StringUtil.isNotBlank(treeTableModel.getConditions())) {
				updateLeafSql.append(" where ").append(treeTableModel.getConditions());
			}
			executeSql(updateLeafSql.toString(), null, null, conn, true);

			// 设置被设置为父节点的记录为非叶子节点(isLeaf=0)
			StringBuilder updateTrunkLeafSql = new StringBuilder();
			updateTrunkLeafSql.append("update ").append(treeTableModel.getTableName());
			int dbType = DataSourceUtils.getDbType(conn);
			if (dbType == DataSourceUtils.DBType.MYSQL || dbType == DataSourceUtils.DBType.MYSQL8) {
				// update sys_organ_info a inner join (select t.organ_pid from
				// sys_organ_info t) b
				// on a.organ_id=b.organ_pid set IS_LEAF=0
				updateTrunkLeafSql.append(" inner join (select ");
				updateTrunkLeafSql.append(treeTableModel.getPidField());
				updateTrunkLeafSql.append(" from ").append(treeTableModel.getTableName());
				if (StringUtil.isNotBlank(treeTableModel.getConditions())) {
					updateTrunkLeafSql.append(" where ").append(treeTableModel.getConditions());
				}
				updateTrunkLeafSql.append(") as t_wrapLeaf ");
				updateTrunkLeafSql.append(" on ");
				updateTrunkLeafSql.append(treeTableModel.getIdField()).append("=t_wrapLeaf.")
						.append(treeTableModel.getPidField());
				updateTrunkLeafSql.append(" set ");
				updateTrunkLeafSql.append(treeTableModel.getLeafField()).append("=0");
				if (StringUtil.isNotBlank(treeTableModel.getConditions())) {
					updateTrunkLeafSql.append(" where ").append(treeTableModel.getConditions());
				}
			} else {
				// update organ_info set IS_LEAF=0
				// where organ_id in (select organ_pid from organ_info)
				updateTrunkLeafSql.append(" set ");
				updateTrunkLeafSql.append(treeTableModel.getLeafField()).append("=0");
				updateTrunkLeafSql.append(" where ").append(treeTableModel.getIdField());
				updateTrunkLeafSql.append(" in (select ").append(treeTableModel.getPidField());
				updateTrunkLeafSql.append(" from ").append(treeTableModel.getTableName());
				if (StringUtil.isNotBlank(treeTableModel.getConditions())) {
					updateTrunkLeafSql.append(" where ").append(treeTableModel.getConditions());
				}
				updateTrunkLeafSql.append(") ");
				if (StringUtil.isNotBlank(treeTableModel.getConditions())) {
					updateTrunkLeafSql.append(" and ").append(treeTableModel.getConditions());
				}
			}
			executeSql(updateTrunkLeafSql.toString(), null, null, conn, true);
		}
		return true;
	}

	/**
	 * @todo TreeTableRoute中处理下一层级的递归方法，逐层计算下一级节点的节点层次和路径
	 * @param updateLevelAndRoute
	 * @param nextNodeQueryStr
	 * @param treeTableModel
	 * @param pidsMap
	 * @param ids
	 * @param nodeLevel
	 * @param conn
	 * @throws Exception
	 */
	private static void processNextLevel(final String updateLevelAndRoute, final String nextNodeQueryStr,
			final TreeTableModel treeTableModel, final HashMap pidsMap, List ids, final int nodeLevel, Connection conn)
			throws Exception {
		// 修改节点level和节点路径
		batchUpdateByJdbc(updateLevelAndRoute, ids, 500, new InsertRowCallbackHandler() {
			public void process(PreparedStatement pst, int index, Object rowData) throws SQLException {
				String id = ((List) rowData).get(0).toString();
				// 获得父节点id和父节点路径
				String pid = ((List) rowData).get(2).toString();
				String nodeRoute = (String) pidsMap.get(pid);
				int size = treeTableModel.getIdLength();
				if (nodeRoute == null || nodeRoute.trim().equals("")) {
					nodeRoute = "";
					if (!treeTableModel.isChar() || treeTableModel.isAppendZero()) {
						// 负数
						if (NumberUtil.isInteger(pid) && pid.indexOf("-") == 0)
							nodeRoute = nodeRoute.concat("-")
									.concat(StringUtil.addLeftZero2Len(pid.substring(1), size - 1));
						else
							nodeRoute = nodeRoute.concat(StringUtil.addLeftZero2Len(pid, size));
					} else {
						nodeRoute = nodeRoute.concat(StringUtil.addRightBlank2Len(pid, size));
					}
				} else
					nodeRoute = nodeRoute.trim();
				// update 2018-1-9 增加判断是否以逗号结尾,解决修改过程中出现双逗号问题
				if (!nodeRoute.endsWith(treeTableModel.getSplitSign()))
					nodeRoute = nodeRoute.concat(treeTableModel.getSplitSign());
				// 回置节点的nodeRoute值
				if (!treeTableModel.isChar() || treeTableModel.isAppendZero())
					nodeRoute = nodeRoute.concat(StringUtil.addLeftZero2Len(id, size));
				else
					nodeRoute = nodeRoute.concat(StringUtil.addRightBlank2Len(id, size));

				((List) rowData).set(1, nodeRoute);
				// 节点等级
				pst.setInt(1, nodeLevel);
				// 节点路径(当节点路径长度不做补充统一长度操作,则末尾自动加上一个分割符)
				pst.setString(2, nodeRoute + ((size < 2) ? treeTableModel.getSplitSign() : ""));

				if (treeTableModel.isChar()) {
					pst.setString(3, id);
				} else
					pst.setLong(3, Long.parseLong(id));
			}
		}, null, false, conn);

		// 处理节点的下一层次
		int size = ids.size();
		int fromIndex = 0;
		int toIndex = -1;

		// 避免in()中的参数过多，每次500个
		String inStrs;
		List subIds = null;
		List nextIds = null;
		boolean exist = false;
		while (toIndex < size) {
			fromIndex = toIndex + 1;
			toIndex += 500;
			if (toIndex >= size - 1) {
				toIndex = size - 1;
				exist = true;
			}
			if (fromIndex >= toIndex) {
				subIds = new ArrayList();
				subIds.add(ids.get(toIndex));
			} else
				subIds = ids.subList(fromIndex, toIndex + 1);

			inStrs = combineQueryInStr(subIds, 0, null, treeTableModel.isChar());

			// 获取下一层节点
			nextIds = findByJdbcQuery(nextNodeQueryStr.replaceFirst("\\$\\{inStr\\}", inStrs), null, null, null, conn);
			// 递归处理下一层
			if (nextIds != null && !nextIds.isEmpty()) {
				processNextLevel(updateLevelAndRoute, nextNodeQueryStr, treeTableModel,
						CollectionUtil.hashList(subIds, 0, 1, true), nextIds, nodeLevel + 1, conn);
			}
			if (exist)
				break;
		}
	}

	/**
	 * @todo <b>sql 查询并返回List集合结果</b>
	 * @param queryStr
	 * @param params
	 * @param voClass
	 * @param rowCallbackHandler
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private static List findByJdbcQuery(final String queryStr, final Object[] params, final Class voClass,
			final RowCallbackHandler rowCallbackHandler, final Connection conn) throws Exception {
		ResultSet rs = null;
		PreparedStatement pst = conn.prepareStatement(queryStr, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		List result = (List) preparedStatementProcess(null, pst, rs, new PreparedStatementResultHandler() {
			public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws Exception {
				setParamsValue(conn, pst, params, null, 0);
				rs = pst.executeQuery();
				this.setResult(processResultSet(rs, 0));
			}
		});
		// 为null返回一个空集合
		if (result == null)
			result = new ArrayList();
		return result;
	}

	/**
	 * @todo 执行Sql语句完成修改操作
	 * @param executeSql
	 * @param params
	 * @param paramsType
	 * @param conn
	 * @param autoCommit
	 * @throws Exception
	 */
	private static Long executeSql(final String executeSql, final Object[] params, final Integer[] paramsType,
			final Connection conn, final Boolean autoCommit) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("executeJdbcSql=" + executeSql);
		boolean hasSetAutoCommit = false;
		Long updateCounts = null;
		if (autoCommit != null) {
			if (!autoCommit == conn.getAutoCommit()) {
				conn.setAutoCommit(autoCommit);
				hasSetAutoCommit = true;
			}
		}
		PreparedStatement pst = conn.prepareStatement(executeSql);
		Object result = preparedStatementProcess(null, pst, null, new PreparedStatementResultHandler() {
			public void execute(Object obj, PreparedStatement pst, ResultSet rs) throws SQLException, IOException {
				setParamsValue(conn, pst, params, paramsType, 0);
				pst.executeUpdate();
				// 返回update的记录数量
				this.setResult(new Long(pst.getUpdateCount()));
			}
		});
		if (result != null)
			updateCounts = (Long) result;
		if (hasSetAutoCommit && autoCommit != null)
			conn.setAutoCommit(!autoCommit);
		return updateCounts;
	}

	/**
	 * @todo 通过jdbc方式批量插入数据，一般提供给数据采集时或插入临时表使用，一般采用hibernate 方式插入
	 * @param updateSql
	 * @param rowDatas
	 * @param batchSize
	 * @param insertCallhandler
	 * @param updateTypes
	 * @param autoCommit
	 * @param conn
	 * @throws Exception
	 */
	private static Long batchUpdateByJdbc(final String updateSql, final Collection rowDatas, final int batchSize,
			final InsertRowCallbackHandler insertCallhandler, final Integer[] updateTypes, final Boolean autoCommit,
			final Connection conn) throws Exception {
		if (rowDatas == null) {
			logger.error("数据为空!");
			return new Long(0);
		}
		PreparedStatement pst = null;
		long updateCount = 0;
		try {
			boolean hasSetAutoCommit = false;
			boolean useCallHandler = true;
			// 是否使用反调方式
			if (insertCallhandler == null)
				useCallHandler = false;
			// 是否自动提交
			if (autoCommit != null && !autoCommit == conn.getAutoCommit()) {
				conn.setAutoCommit(autoCommit);
				hasSetAutoCommit = true;
			}
			pst = conn.prepareStatement(updateSql);
			int totalRows = rowDatas.size();
			boolean useBatch = (totalRows > 1) ? true : false;
			Object rowData;
			int index = 0;
			// 批处理计数器
			int meter = 0;
			for (Iterator iter = rowDatas.iterator(); iter.hasNext();) {
				rowData = iter.next();
				index++;
				if (rowData != null) {
					// 使用反调
					if (useCallHandler)
						insertCallhandler.process(pst, index, rowData);
					else {
						// 使用对象properties方式传值
						if (rowData.getClass().isArray()) {
							Object[] tmp = CollectionUtil.convertArray(rowData);
							for (int i = 0; i < tmp.length; i++) {
								setParamValue(conn, pst, tmp[i], updateTypes == null ? -1 : updateTypes[i], i + 1);
							}
						} else if (rowData instanceof Collection) {
							Collection tmp = (Collection) rowData;
							int tmpIndex = 0;
							for (Iterator tmpIter = tmp.iterator(); tmpIter.hasNext();) {
								setParamValue(conn, pst, tmpIter.next(),
										updateTypes == null ? -1 : updateTypes[tmpIndex], tmpIndex + 1);
								tmpIndex++;
							}
						}
					}
					meter++;
					if (useBatch) {
						pst.addBatch();
						if ((meter % batchSize) == 0 || index == totalRows) {
							int[] updateRows = pst.executeBatch();
							for (int t : updateRows) {
								updateCount = updateCount + ((t > 0) ? t : 0);
							}
							pst.clearBatch();
						}
					} else {
						pst.execute();
						updateCount = updateCount + ((pst.getUpdateCount() > 0) ? pst.getUpdateCount() : 0);
					}
				}
			}
			// updateCount = new Long(pst.getUpdateCount());
			if (hasSetAutoCommit)
				conn.setAutoCommit(!autoCommit);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			try {
				if (pst != null) {
					pst.close();
					pst = null;
				}
			} catch (SQLException se) {
				logger.error(se.getMessage(), se);
			}
		}
		return updateCount;
	}

	/**
	 * @todo 合成数据库in 查询的条件(不建议使用)
	 * @param conditions
	 *            :数据库in条件的数据集合，可以是POJO List或Object[]
	 * @param colIndex
	 *            :二维数组对应列编号
	 * @param property
	 *            :POJO property
	 * @param isChar
	 *            :in 是否要加单引号
	 * @return:example:1,2,3或'1','2','3'
	 * @throws Exception
	 */
	@Deprecated
	public static String combineQueryInStr(Object conditions, Integer colIndex, String property, boolean isChar)
			throws Exception {
		StringBuilder conditons = new StringBuilder(64);
		String flag = "";
		// 是否是字符类型
		if (isChar)
			flag = "'";
		// 判断数据集合维度
		int dimen = CollectionUtil.judgeObjectDimen(conditions);
		switch (dimen) {
		// 单个数据
		case 0:
			conditons.append(flag).append(conditions.toString()).append(flag);
			break;
		// 一维数组
		case 1: {
			Object[] array;
			if (conditions instanceof Collection)
				array = ((Collection) conditions).toArray();
			else if (conditions.getClass().isArray())
				array = CollectionUtil.convertArray(conditions);
			else
				array = ((Map) conditions).values().toArray();

			for (int i = 0; i < array.length; i++) {
				if (i != 0)
					conditons.append(",");
				conditons.append(flag);
				if (null == property)
					conditons.append(array[i]);
				else
					conditons.append(BeanUtils.getProperty(array[i], property));
				conditons.append(flag);
			}
			break;
		}
		// 二维数据
		case 2: {
			Object[][] array;
			if (conditions instanceof Collection)
				array = CollectionUtil.twoDimenlistToArray((Collection) conditions);
			else if (conditions instanceof Object[][])
				array = (Object[][]) conditions;
			else
				array = CollectionUtil.twoDimenlistToArray(((Map) conditions).values());
			for (int i = 0; i < array.length; i++) {
				if (i != 0)
					conditons.append(",");
				conditons.append(flag);
				if (null == property)
					conditons.append(array[i][colIndex.intValue()]);
				else
					conditons.append(BeanUtils.getProperty(array[i][colIndex.intValue()], property));
				conditons.append(flag);
			}
			break;
		}
		}
		return conditons.toString();
	}

	/**
	 * @todo <b>sql文件自动创建到数据库</b>
	 * @param conn
	 * @param sqlContent
	 * @param splitSign
	 * @param autoCommit
	 * @throws Exception
	 */
	public static void executeBatchSql(Connection conn, String sqlContent, String splitSign, boolean autoCommit)
			throws Exception {
		String lastSplitSign = StringUtil.isBlank(splitSign) ? ";" : splitSign;
		// 剔除sql中的注释
		sqlContent = clearMark(sqlContent);
		if (lastSplitSign.toLowerCase().indexOf("go") != -1)
			sqlContent = StringUtil.clearMistyChars(sqlContent, " ");
		// sqlserver sybase 数据库以go 分割,则整个sql文件作为一个语句执行
		String[] statments = StringUtil.splitExcludeSymMark(sqlContent, lastSplitSign, sqlCommentfilters);
		boolean setAuto = false;
		if (conn.getAutoCommit()) {
			conn.setAutoCommit(false);
			setAuto = true;
		}
		Statement stat = null;
		String sql;
		for (int i = 0; i < statments.length; i++) {
			sql = statments[i].trim();
			if (StringUtil.isNotBlank(sql)) {
				try {
					stat = conn.createStatement();
					if (logger.isDebugEnabled())
						logger.debug("######正在执行sql:" + sql.trim());
					stat.execute(sql.trim());
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				} finally {
					if (stat != null)
						stat.close();
				}
			}
		}
		if (autoCommit)
			conn.commit();
		if (setAuto)
			conn.setAutoCommit(true);
	}
}
