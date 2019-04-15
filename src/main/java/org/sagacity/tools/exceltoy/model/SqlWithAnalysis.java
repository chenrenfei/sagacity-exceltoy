/**
 * 
 */
package org.sagacity.tools.exceltoy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-core
 * @description Sql语句中存在with的分析,只支持单个with 但支持多个as
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version Revision:v1.0,Date:2013-6-20
 * @Modification Date:2013-6-20 {填写修改说明}
 */
public class SqlWithAnalysis implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5841684922722930298L;
	private final Pattern withPattern = Pattern
			.compile("(?i)\\s*with\\s+[a-z|0-9|\\_]+\\s+as\\s*\\(");
	//with 下面多个as
	private final Pattern otherWithPattern = Pattern
			.compile("(?i)\\s*\\,\\s*[a-z|0-9|\\_]+\\s+as\\s*\\(");

	private String sql;

	private String withSql = "";

	/**
	 * 排除with之后的语句
	 */
	private String rejectWithSql;

	/**
	 * 是否有with as
	 */
	private boolean hasWith = false;

	/**
	 * 多个with存放的集合，正向排序(第一版采用逆向排序){asTableName,sql script}
	 */
	private List<String[]> withSqlSet = null;

	public SqlWithAnalysis(String sql) {
		this.sql = sql;
		this.parse();
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @return the withSql
	 */
	public String getWithSql() {
		return withSql;
	}

	/**
	 * @return the rejectWithSql
	 */
	public String getRejectWithSql() {
		return rejectWithSql;
	}

	/**
	 * @return the withSqlSet
	 */
	public List<String[]> getWithSqlSet() {
		return withSqlSet;
	}

	/**
	 * 将带with 的sql解析成2部分:with as table () 和 select 部分
	 */
	private void parse() {
		rejectWithSql = this.sql;
		String headSql="";
		String tailSql = this.sql;
		String aliasTable;
		int endWith;
		StringBuilder withSqlBuffer = null;
		// 单个with
		Matcher withAsMatcher = withPattern.matcher(tailSql);
		if (withAsMatcher.find()) {
			headSql=tailSql.substring(0,  withAsMatcher.start());
			hasWith = true;
			withSqlBuffer = new StringBuilder();
			withSqlSet = new ArrayList<String[]>();		
			aliasTable = withAsMatcher.group().trim().substring(4).trim();
			aliasTable = aliasTable.substring(0,
					aliasTable.toLowerCase().indexOf(" as")).trim();
			endWith = StringUtil.getSymMarkIndex("(", ")", tailSql, withAsMatcher.start());
			withSqlBuffer.append(tailSql.substring(withAsMatcher.start(), endWith + 1));
			withSqlSet.add(new String[] { aliasTable,
					tailSql.substring(withAsMatcher.end(), endWith) });
			tailSql = tailSql.substring(endWith + 1);
		} else
			return;
		// with 中包含多个 as
		Matcher otherMatcher = otherWithPattern.matcher(tailSql);
		while (otherMatcher.find()) {
			if (otherMatcher.start() != 0)
				break;
			aliasTable = otherMatcher.group().trim();
			aliasTable = aliasTable.substring(aliasTable.indexOf(",") + 1)
					.trim();
			aliasTable = aliasTable.substring(0,
					aliasTable.toLowerCase().indexOf(" as")).trim();
			endWith = StringUtil.getSymMarkIndex("(", ")", tailSql,
					otherMatcher.start());
			withSqlBuffer.append(tailSql.substring(0, endWith + 1));
			withSqlSet.add(new String[] { aliasTable,
					tailSql.substring(otherMatcher.end(), endWith) });
			tailSql = tailSql.substring(endWith + 1);
			otherMatcher.reset(tailSql);
		}
		rejectWithSql =headSql.concat(" ").concat(tailSql);
		this.withSql = withSqlBuffer.append(" ").toString();
	}

	/**
	 * @return the hasWith
	 */
	public boolean isHasWith() {
		return hasWith;
	}

	/**
	 * @param hasWith
	 *            the hasWith to set
	 */
	public void setHasWith(boolean hasWith) {
		this.hasWith = hasWith;
	}

}
