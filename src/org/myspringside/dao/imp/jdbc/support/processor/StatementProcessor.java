package org.myspringside.dao.imp.jdbc.support.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oracle.jdbc.driver.OracleTypes;

import org.myspringside.dao.imp.jdbc.support.DataSourceFactory;
import org.myspringside.dao.imp.jdbc.support.config.DataBaseConfig;
import org.myspringside.dao.imp.jdbc.support.config.EntityInfo;
import org.myspringside.dao.imp.jdbc.tools.QueryConditions;

public class StatementProcessor<T> {
	private EntityInfo<T> ei;

	public StatementProcessor(EntityInfo<T> ei) {
		super();
		this.ei = ei;
	}

	public String getDialect() {
		return DataSourceFactory.getDbConfig().getDialect();
	}

	private void oracleSetNull(PreparedStatement ps, int i) throws SQLException {
		try {
			ps.setNull(i + 1, OracleTypes.VARCHAR);
		} catch (SQLException e) {
			ps.setNull(i + 1, OracleTypes.CHAR);
			e.printStackTrace();
		}
	}

	public String getOraclePageQueryStatementByParams(List<String> colValue, String... params) {
		String sql = "select * FROM (SELECT T.*, ROWNUM RN FROM (SELECT * FROM " + ei.getTable()
				+ ") T WHERE ROWNUM <= ?) WHERE RN >= ?";
		if (params.length != 0) {
			String temp = getQuerySqlStrFromParams(colValue, params);
			String condition = temp.substring(temp.indexOf("where") + 5);
			sql = sql + " and " + condition;
		}
		return sql;
	}

	public String getCountStatementWithIDcondition() {
		String is_exist_sql = "select count(*) from " + ei.getTable() + " where "
				+ getIDConditionPrepareStatementString();
		return is_exist_sql;
	}
	public String getMultipleEntityUpdateStatement(ArrayList<String> col_list,int multipleFieldValue) {
		String sql = "update " + ei.getMultiPleTableName(multipleFieldValue) + " set ";
		//update时的主键约束
		String sql2 = " where " + getIDConditionPrepareStatementString();
		String pair = "";
		for (String col : col_list) {
			if (ei.isNotIDColumn(col))
				pair += col + "=?,";
		}
		pair = pair.substring(0, pair.length() - 1);
		sql = sql + pair + sql2;
		return sql;
	}
	public String getUpdateStatement(ArrayList<String> col_list) {
		String sql = "update " + ei.getTable() + " set ";
		//update时的主键约束
		String sql2 = " where " + getIDConditionPrepareStatementString();
		String pair = "";
		for (String col : col_list) {
			if (ei.isNotIDColumn(col))
				pair += col + "=?,";
		}
		pair = pair.substring(0, pair.length() - 1);
		sql = sql + pair + sql2;
		return sql;
	}

	public String createGetStatement() {
		String sql = "select * from " + ei.getTable() + " where " + getIDConditionPrepareStatementString();
		return sql;
	}

	public String createGetAllStatement() {
		String sql = "select * from " + ei.getTable();
		return sql;
	}

	public String getDeleteStatement() {
		String sql = "delete from " + ei.getTable() + " where " + getIDConditionPrepareStatementString();
		return sql;
	}

	public String getInsertStatement(ArrayList<String> cols) {
		String sql = "insert into " + ei.getTable();
		String[] col_array = {};
		col_array = cols.toArray(col_array);
		sql += entityToString(col_array, ei.getIdentityColumn(), true);
		return sql;
	}
	public String getMultipleEntityInsertStatement(ArrayList<String> cols,int multipleFieldValue) {
		String sql = "insert into " + ei.getMultiPleTableName(multipleFieldValue);
		String[] col_array = {};
		col_array = cols.toArray(col_array);
		sql += entityToString(col_array, ei.getIdentityColumn(), true);
		return sql;
	}

	public String getIDConditionPrepareStatementString() {
		String sql = "";
		for (String idCol : ei.getIdCols()) {
			sql += idCol + "=? and ";
		}
		sql = sql.substring(0, sql.length() - 5);
		return sql;
	}

	public String entityToString(String[] cols, String idcolName, boolean identity) {
		String sql = "";
		String attrStr = " ( ";
		String valueStr = "( ";
		try {
			for (String col : cols) {
				if (identity) {
					if (col.equals(idcolName)) {
						continue;
					}
				}
				attrStr += col + ",";
				valueStr += "?,";
			}
			if (valueStr.length() == 0)
				try {
					throw new Exception("the attribute of this Entity hasn't been assigned!!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			valueStr = valueStr.substring(0, valueStr.length() - 1) + ")";
			attrStr = attrStr.substring(0, attrStr.length() - 1) + ")";
			sql = attrStr + " values " + valueStr;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return sql;
	}

	public String getQuerySqlStrFromParams(List<String> colValue, String... params) {
		String sql = "select * from " + ei.getTable();
		sql += " where ";
		String[] nameValue;
		String tstr;
		String colName;
		for (String p : params) {
			if (QueryConditions.pat.matcher(p).matches()) {
				nameValue = p.split("=");
				colName = ei.getAttr_col().get(nameValue[0]);
				colValue.add(nameValue[1]);
				sql += colName + "=? and  ";
				continue;
			}
			if (QueryConditions.pat2.matcher(p).matches()) {
				nameValue = p.split(" like ");
				colName = ei.getAttr_col().get(nameValue[0]);
				colValue.add(nameValue[1]);
				sql += colName + " like ? and  ";
				continue;
			}
			if (QueryConditions.pat5.matcher(p).matches()) {
				nameValue = p.split(">");
				colName = ei.getAttr_col().get(nameValue[0]);
				colValue.add(nameValue[1]);
				sql += colName + ">? and  ";
				continue;
			}
			if (QueryConditions.pat6.matcher(p).matches()) {
				nameValue = p.split("<");
				colName = ei.getAttr_col().get(nameValue[0]);
				colValue.add(nameValue[1]);
				sql += colName + "<? and  ";
				continue;
			}
			if (QueryConditions.pat7.matcher(p).matches()) {
				nameValue = p.split(">=");
				colName = ei.getAttr_col().get(nameValue[0]);
				colValue.add(nameValue[1]);
				sql += colName + ">=? and  ";
				continue;
			}
			if (QueryConditions.pat8.matcher(p).matches()) {
				nameValue = p.split("<=");
				colName = ei.getAttr_col().get(nameValue[0]);
				colValue.add(nameValue[1]);
				sql += colName + "<=? and  ";
				continue;
			}
			if (QueryConditions.pat9.matcher(p).matches()) {
				nameValue = p.split(" between ");
				colName = ei.getAttr_col().get(nameValue[0]);
				String[] temp = nameValue[1].split(" and ");
				colValue.add(temp[0]);
				colValue.add(temp[1]);
				sql += colName + " between ? and ? and  ";
				continue;
			}
		}
		sql = sql.substring(0, sql.length() - 6);
		for (String p : params) {
			if (QueryConditions.pat3.matcher(p).matches()) {
				tstr = p.substring(0, p.lastIndexOf(" asc"));
				colName = ei.getAttr_col().get(tstr);
				if (sql.endsWith("and ")) {
					sql = sql.substring(0, sql.lastIndexOf("and "));
				}
				sql += " order by " + colName + " asc";
				break;
			} else if (QueryConditions.pat4.matcher(p).matches()) {
				tstr = p.substring(0, p.lastIndexOf(" desc"));
				colName = ei.getAttr_col().get(tstr);
				if (sql.endsWith("and ")) {
					sql = sql.substring(0, sql.lastIndexOf("and "));
				}
				sql += " order by " + colName + " desc";
				break;
			}
		}
		return sql;
	}

	public PreparedStatement getQueryStatementFromParams(Connection conn, String... params) {

		ArrayList<String> colValue = new ArrayList<String>(10);
		String sql = getQuerySqlStrFromParams(colValue, params);
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			setStatementParamsValue(ps, colValue.toArray());
			return ps;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void setStatementParamsValue(PreparedStatement ps, Object... params) {
		try {
			for (int i = 0; i < params.length; i++) {
				if (params[i] == null) {
					if (getDialect().equals(DataBaseConfig.ORACLE)) {
						oracleSetNull(ps, i);
					} else
						ps.setNull(i + 1, Types.NULL);
					continue;
				}
				Class pt = params[i].getClass();
				if (pt.equals(String.class)) {
					if (params[i].equals("NULL")) {
						if (getDialect().equals(DataBaseConfig.ORACLE)) {
							oracleSetNull(ps, i);
						} else {
							ps.setNull(i + 1, Types.NULL);
						}
					}

					else {
						ps.setString(i + 1, (String) params[i]);
					}
					continue;
				}
				if (pt.equals(Integer.TYPE) || pt.equals(Integer.class)) {
					ps.setInt(i + 1, (Integer) params[i]);
					continue;
				}
				if (pt.equals(Double.TYPE) || pt.equals(Double.class)) {
					ps.setDouble(i + 1, (Double) params[i]);
					continue;
				}
				if (pt.equals(Date.class)) {
					long totalTime = ((Date) params[i]).getTime();
					Timestamp ts = new Timestamp(totalTime);
					ps.setTimestamp(i + 1, ts);
					continue;
				}
				if (pt.equals(Boolean.TYPE) || pt.equals(Boolean.class)) {
					ps.setBoolean(i + 1, (Boolean) params[i]);
					continue;
				}
				if (pt.equals(Float.TYPE) || pt.equals(Float.class)) {
					ps.setFloat(i + 1, (Float) params[i]);
					continue;
				}
				if (pt.equals(Long.TYPE) || pt.equals(Long.class)) {
					ps.setLong(i + 1, (Long) params[i]);
					continue;
				}
				if (pt.equals(Short.TYPE) || pt.equals(Short.class)) {
					ps.setShort(i + 1, (Short) params[i]);
					continue;
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
