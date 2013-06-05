package org.myspringside.dao.imp.jdbc.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.myspringside.dao.imp.jdbc.support.processor.StatementProcessor;
import org.myspringside.dao.imp.jdbc.tools.DBUtils;
import org.myspringside.dao.imp.jdbc.tools.LoggerTool;

public class CommonQueryUtils<T> {
	StatementProcessor<T> sp;

	public CommonQueryUtils(StatementProcessor<T> sp) {
		this.sp = sp;
	}

	public int getRecordCountBySQL(String dataSourceKey,StatementProcessor<T> sp, String sql, Object... params) {
		Connection conn = DataSourceFactory.getConnection(dataSourceKey);
		// 查询表记录数
		String countSQL = "select count(*) from (" + sql + ") aa";

		ResultSet rs_count = null;
		int count = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(countSQL);
			sp.setStatementParamsValue(ps, params);
			rs_count = ps.executeQuery();
			//System.out.println(countSQL);
			while (rs_count.next()) {
				count = rs_count.getInt(1);
			} 
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			DBUtils.close(conn, ps, rs_count);
		}
		return count;
	}

	public int SQLQueryForInt(String dataSourceKey,String sql, Object... params) {
		Connection conn = DataSourceFactory.getConnection(dataSourceKey);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			sp.setStatementParamsValue(ps, params);
			rs = ps.executeQuery();
			//System.out.println(sql);
			while (rs.next()) {
				int res = rs.getInt(1);
				return res;
			}

		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} finally {
			DBUtils.close(conn, ps, rs);
		}
		return -1;
	}

}
