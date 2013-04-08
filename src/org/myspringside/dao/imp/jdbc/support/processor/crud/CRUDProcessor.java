package org.myspringside.dao.imp.jdbc.support.processor.crud;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.myspringside.dao.imp.jdbc.support.CommonQueryUtils;
import org.myspringside.dao.imp.jdbc.support.DataSourceFactory;
import org.myspringside.dao.imp.jdbc.support.config.DataBaseConfig;
import org.myspringside.dao.imp.jdbc.support.config.EntityInfo;
import org.myspringside.dao.imp.jdbc.support.processor.ResultSetProcessor;
import org.myspringside.dao.imp.jdbc.support.processor.StatementProcessor;
import org.myspringside.dao.imp.jdbc.tools.BeanUtils;
import org.myspringside.dao.imp.jdbc.tools.DBUtils;
import org.myspringside.dao.imp.jdbc.tools.LoggerTool;
import org.myspringside.dao.imp.jdbc.tools.MyStringTool;
import org.myspringside.dao.imp.jdbc.tools.Page;

@SuppressWarnings( { "unchecked" })
public class CRUDProcessor<T> {
	CommonQueryUtils<T> cqu;
	StatementProcessor<T> sp;
	ResultSetProcessor rp;
	EntityInfo<T> ei;

	public CommonQueryUtils<T> getCqu() {
		return cqu;
	}

	public void setCqu(CommonQueryUtils<T> cqu) {
		this.cqu = cqu;
	}

	public StatementProcessor<T> getSp() {
		return sp;
	}

	public void setSp(StatementProcessor<T> sp) {
		this.sp = sp;
	}

	public ResultSetProcessor getRp() {
		return rp;
	}

	public void setRp(ResultSetProcessor rp) {
		this.rp = rp;
	}

	public EntityInfo<T> getEi() {
		return ei;
	}

	public void setEi(EntityInfo<T> ei) {
		this.ei = ei;
	}

	public CRUDProcessor(EntityInfo<T> ei, CommonQueryUtils<T> cqu, StatementProcessor<T> sp, ResultSetProcessor rp) {
		super();
		this.ei = ei;
		this.cqu = cqu;
		this.sp = sp;
		this.rp = rp;
	}

	private String getDialect() {
		return DataSourceFactory.getDbConfig().getDialect();
	}

	public List<T> findBySQLQuery(Boolean isFromReadOnlySource,String sql, Object... params) {
		Connection conn = getConnection(isFromReadOnlySource);
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			sp.setStatementParamsValue(ps, params);
			if(DataBaseConfig.IsDebug){
				System.out.println(sql);
				System.out.println(MyStringTool.stringFromObjects(params));
			}
			rs = ps.executeQuery();
			List<T> vol = rp.resultSetTovoList(rs, ei);
			
			return vol;
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return null;
		} finally {
			DBUtils.close(conn, ps, rs);
		}
	}
	
	public String findStringValueBySQLQuery(Boolean isFromReadOnlySource,String sql, Object... params) {
		Connection conn = getConnection(isFromReadOnlySource);
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			sp.setStatementParamsValue(ps, params);
			if(DataBaseConfig.IsDebug){
				System.out.println(sql);
				System.out.println(MyStringTool.stringFromObjects(params));
			}
			rs = ps.executeQuery();
			String value ="";
			while (rs.next()) {
				value = rs.getString(1);
			}
			return value;
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return "";
		} finally {
			DBUtils.close(conn, ps, rs);
		}
	}
	public List<String> findStringValueListBySQLQuery(Boolean isFromReadOnlySource,String sql, Object... params) {
		Connection conn = getConnection(isFromReadOnlySource);
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			sp.setStatementParamsValue(ps, params);
			if(DataBaseConfig.IsDebug){
				System.out.println(sql);
				System.out.println(MyStringTool.stringFromObjects(params));
			}
			rs = ps.executeQuery();
			List<String> valueList =new ArrayList<String>();
			while (rs.next()) {
				String value = rs.getString(1);
				valueList.add(value);
			}
			return valueList;
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return null;
		} finally {
			DBUtils.close(conn, ps, rs);
		}
	}
	public int findIntValueBySQLQuery(Boolean isFromReadOnlySource,String sql, Object... params) {
		String result = findStringValueBySQLQuery(isFromReadOnlySource,sql, params);
		if(!"".equals(result)){
			return Integer.valueOf(result);
		}else
			return 0;
	}
	
	public List<Integer> findIntValueListBySQLQuery(Boolean isFromReadOnlySource,String sql, Object... params) {
		Connection conn = getConnection(isFromReadOnlySource);
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			sp.setStatementParamsValue(ps, params);
			if(DataBaseConfig.IsDebug){
				System.out.println(sql);
				System.out.println(MyStringTool.stringFromObjects(params));
			}
			rs = ps.executeQuery();
			List<Integer> valueList =new ArrayList<Integer>();
			while (rs.next()) {
				int value = rs.getInt(1);
				valueList.add(value);
			}
			return valueList;
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return null;
		} finally {
			DBUtils.close(conn, ps, rs);
		}
	}

	public Page<T> pageQuery(String sql_params, int pageNo, int pageSize) {
		if (getDialect().equalsIgnoreCase(DataBaseConfig.ORACLE)) {
			return oraclePageQuery(sql_params, pageNo, pageSize);
		} else {
			throw new RuntimeException(
					"page Query for mysql with temp column mapping and custom sql hasn't been support.");
		}
	}
	
	//该分页查询可加临时列映射，可传入sql语句
//	@SuppressWarnings("unchecked")
//	private Page<T> mysqlPageQuery(int pageNo, int pageSize,String sql,String... params ) {
//		int record_count = cqu.getRecordCountBySQL(sp, sql);
//		if (record_count == 0)
//			return null;
//		int start_count = (pageNo - 1) * pageSize + 1;
//		int end_count = start_count + pageSize - 1;
//		 sql = "";
//		Connection conn = DataSourceFactory.getConnection();
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		try {
//			ps = conn.prepareStatement(sql);
//			List p = new ArrayList();
//			p.add(end_count);
//			p.add(start_count);
//			sp.setStatementParamsValue(ps, p.toArray());
//			rs = ps.executeQuery();
//			List<T> vol = rp.resultSetTovoList(rs, ei);
//			Page<T> page = new Page<T>(start_count, record_count, pageSize, vol);
//			return page;
//		} catch (SQLException e) {
//			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
//			return null;
//		} finally {
//			DBUtils.close(conn, ps, rs);
//		}
//	}

	public Page<T> pageQuery(int pageNo, int pageSize,String sql, String... params) {

		if (getDialect().equalsIgnoreCase(DataBaseConfig.MYSQL)) {
			return mysqlPageQuery(pageNo, pageSize, sql,params);
		} else if (getDialect().equalsIgnoreCase(DataBaseConfig.SQL_SERVER)) {
			throw new InternalError("the pageQuery of SQL Server hasn't been implement.");
		} else if (getDialect().equalsIgnoreCase(DataBaseConfig.ORACLE)) {
			return oraclePageQuery(pageNo, pageSize, params);
		} else {
			throw new InternalError("the pageQuery of this database hasn't been implement.");
		}
	}

	//该分页查询可加临时列映射，可传入sql语句
	@SuppressWarnings("unchecked")
	private Page<T> oraclePageQuery(String sql_params, int pageNo, int pageSize) {
		int record_count = cqu.getRecordCountBySQL(ei.getDataSource(),sp, sql_params);
		if (record_count == 0)
			return null;
		int start_count = (pageNo - 1) * pageSize + 1;
		int end_count = start_count + pageSize - 1;
		String sql = "select * FROM (SELECT T.*, ROWNUM RN FROM (" + sql_params
				+ ") T WHERE ROWNUM <= ?) WHERE RN >= ?";
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			List p = new ArrayList();
			p.add(end_count);
			p.add(start_count);
			sp.setStatementParamsValue(ps, p.toArray());
			rs = ps.executeQuery();
			List<T> vol = rp.resultSetTovoList(rs, ei);
			Page<T> page = new Page<T>(start_count, record_count, pageSize, vol);
			return page;
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return null;
		} finally {
			DBUtils.close(conn, ps, rs);
		}
	}

	@SuppressWarnings("unchecked")
	private Page<T> oraclePageQuery(int pageNo, int pageSize, String... params) {

		int record_count = cqu.getRecordCountBySQL(ei.getDataSource(),sp, ei.getTable());
		if (record_count == 0)
			return null;
		// 构造查询语句
		List<String> colValue = new ArrayList<String>();
		int start_count = (pageNo - 1) * pageSize + 1;
		int end_count = start_count + pageSize - 1;
		String sql = sp.getOraclePageQueryStatementByParams(colValue, params);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			List p = new ArrayList();
			p.add(end_count);
			p.add(start_count);
			p.addAll(colValue);
			sp.setStatementParamsValue(ps, p.toArray());
			rs = ps.executeQuery();
			List<T> vol = rp.resultSetTovoList(rs, ei);
			Page<T> page = new Page<T>(start_count, record_count, pageSize, vol);
			return page;
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return null;
		} finally {
			DBUtils.close(conn, ps, rs);
		}
	}

	private Page<T> mysqlPageQuery(int pageNo, int pageSize, String sql,String... params) {
		int count = cqu.getRecordCountBySQL(ei.getDataSource(),sp, sql,params);
		if (count == 0)
			return null;
		int start = (pageNo - 1) * pageSize;
		sql += " limit " + start + "," + pageSize;
		Connection conn = getConnection(true);
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			sp.setStatementParamsValue(ps, params);
			rs = ps.executeQuery();
			if (rs != null) {
				List<T> vol = rp.resultSetTovoList(rs, ei);
				Page<T> p = new Page<T>(start, count, pageSize, vol);
				return p;
			}
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return null;
		} finally {
			DBUtils.close(conn, ps, rs);
		}
		return null;
	}

	public List<T> findByMutiAttr(String... params) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = getConnection(true);
		try {
			ps = sp.getQueryStatementFromParams(conn, params);
			rs = ps.executeQuery();
			if (rs != null) {
				List<T> vl = rp.resultSetTovoList(rs, ei);
				return vl;
			}
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} finally {
			DBUtils.close(conn, ps, rs);
		}
		return null;

	}
	
	
	
	public T get(Boolean isReadOnly,Object... id) {

		String sql = sp.createGetStatement();
		Connection conn = getConnection(isReadOnly);;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			sp.setStatementParamsValue(ps, id);
			rs = ps.executeQuery();
			List<T> vl = rp.resultSetTovoList(rs, ei);
			if(DataBaseConfig.IsDebug){
				System.out.println(sql);
				System.out.println(MyStringTool.stringFromObjects(id));
			}
			if (vl.size() == 0) {
				return null;
			} else if (vl.size() != 1) {
				throw new RuntimeException("the query result is not unique!");
			} else {
				return vl.get(0);
			}

		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return null;
		} finally {
			DBUtils.close(conn, ps, rs);
		}
	}

	public List<T> getAll(Boolean isReadOnly) {

		String sql = sp.createGetAllStatement();
		ResultSet rs = null;
		List<T> voList = null;
		Connection conn = getConnection(isReadOnly);
		Statement ps = null;
		try {
			ps = conn.createStatement();
			rs = ps.executeQuery(sql);
			if(DataBaseConfig.IsDebug){
				System.out.println(sql);
			}
			voList = rp.resultSetTovoList(rs, ei);
			return voList;
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return null;
		} finally {
			DBUtils.close(conn, ps, rs);
		}
	}

	public void remove(Object... id) {
		if (id.length < ei.getIdAttrs().size())
			throw new IllegalArgumentException("the count of id is not enough.");
		Connection conn =getConnection();
		PreparedStatement ps = null;
		try {
			String sql = sp.getDeleteStatement();
			ps = conn.prepareStatement(sql);
			sp.setStatementParamsValue(ps, id);
			if(DataBaseConfig.IsDebug){
				System.out.println(sql);
				System.out.println(MyStringTool.stringFromObjects(id));
			}
			ps.execute();
		} catch (SecurityException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} finally {
			DBUtils.close(conn, ps);
		}
	}

	@SuppressWarnings("unchecked")
	public void remove(T vo) {
		List identities = new ArrayList();
		try {
			for (String idattr : ei.getIdAttrs()) {
				identities.add(BeanUtils.forceGetProperty(vo, idattr));
			}
		} catch (NoSuchFieldException e) {
			
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		}
		remove(identities.toArray());
	}

	@SuppressWarnings("unused")
	@Deprecated
	public void save(T vo) throws SQLException{
		String is_exist_sql = sp.getCountStatementWithIDcondition();
		List id_values = ei.getIDAttrValues(vo);
		int res = cqu.SQLQueryForInt(ei.getDataSource(),is_exist_sql, id_values.toArray());
		if (res == 0) {
			insert(vo);
		} else {
			update(vo);
		}
	}
	
	private String getMultiPleEntitySql(T vo,ArrayList<String> cols,Boolean isInsert){
		String sql = "";
		if(!ei.isMultiPleEntity){
			if(isInsert)
				sql =sp.getInsertStatement(cols);
			else
				sql=sp.getUpdateStatement(cols);
		}else{
			Integer multipleFieldValue=0;
			try {
				multipleFieldValue = (Integer) BeanUtils.forceGetProperty(vo, ei.getMultipleField());
			} catch (NoSuchFieldException e) {
				e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			}
			if(isInsert)
				sql=sp.getMultipleEntityInsertStatement(cols, multipleFieldValue);
			else 
				sql=sp.getMultipleEntityUpdateStatement(cols, multipleFieldValue);
		}
		return sql;
	}
	
	public T insert(T vo) throws SQLException{
		ArrayList<String> cols = new ArrayList<String>(ei.getCol_attr().keySet());
		String sql = getMultiPleEntitySql(vo, cols,true);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			List<String> attr_list = ei.getAttrListWithoutIdentityColumnByColList(cols);
			List attrValues = BeanUtils.getValueListFromEntity(vo, attr_list);
			for (String attr : ei.getAttr_col().keySet()) {
				
				String colName = ei.getAttr_col().get(attr);
				Type ft = ei.getAll_attr_types().get(attr);				

			}			
			sp.setStatementParamsValue(ps, attrValues.toArray());
			if(DataBaseConfig.IsDebug){
				System.out.println(sql);
				System.out.println(MyStringTool.stringFromList(attrValues));
			}
			ps.execute();

			if (ei.getIdentityColumn() != null) {
				int id = -1;
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					// Retrieve the auto generated key(s).
					id = rs.getInt(1);
				}
				BeanUtils.forceSetProperty(vo, ei.getCol_attr().get(ei.getIdentityColumn()), id);
			}
			return vo;
		} catch (NoSuchFieldException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} finally {
			DBUtils.close(conn, ps, rs);
		}
		return null;
	}
	
	public List<T> insertListVo(List<T> listVo) throws SQLException{
		ArrayList<String> cols = new ArrayList<String>(ei.getCol_attr().keySet());
		Connection conn = getConnection();
		String sql =sp.getInsertStatement(cols);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			List<String> attr_list = ei.getAttrListWithoutIdentityColumnByColList(cols);
			for(T vo:listVo){
				
				ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				List attrValues = BeanUtils.getValueListFromEntity(vo, attr_list);
				for (String attr : ei.getAttr_col().keySet()) {
					String colName = ei.getAttr_col().get(attr);
					Type ft = ei.getAll_attr_types().get(attr);				
				}			
				sp.setStatementParamsValue(ps, attrValues.toArray());
				if(DataBaseConfig.IsDebug){
					System.out.println(sql);
					System.out.println(MyStringTool.stringFromList(attrValues));
				}
				ps.execute();
				if (ei.getIdentityColumn() != null) {
					int id = -1;
					rs = ps.getGeneratedKeys();
					if (rs.next()) {
						// Retrieve the auto generated key(s).
						id = rs.getInt(1);
					}
					BeanUtils.forceSetProperty(vo, ei.getCol_attr().get(ei.getIdentityColumn()), id);
				}
			}
			conn.commit(); //事务提交
			
		} catch (NoSuchFieldException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} finally {
			DBUtils.close(conn, ps, rs);
		}
		return listVo;
	}

	public List<T> insertListMutipleEntityVo(List<T> listVo,Boolean isSingleTable) throws SQLException{
		ArrayList<String> cols = new ArrayList<String>(ei.getCol_attr().keySet());
		String sql="";
		if(isSingleTable && listVo.size()>0)
			sql = getMultiPleEntitySql(listVo.get(0), cols,true);
		Connection conn =getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			conn.setAutoCommit(false);
			List<String> attr_list = ei.getAttrListWithoutIdentityColumnByColList(cols);
			for(T vo:listVo){
				if(!isSingleTable)
					sql = getMultiPleEntitySql(vo, cols,true);
				ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				List attrValues = BeanUtils.getValueListFromEntity(vo, attr_list);
				for (String attr : ei.getAttr_col().keySet()) {
					String colName = ei.getAttr_col().get(attr);
					Type ft = ei.getAll_attr_types().get(attr);				
				}			
				sp.setStatementParamsValue(ps, attrValues.toArray());
				if(DataBaseConfig.IsDebug){
					System.out.println(sql);
					System.out.println(MyStringTool.stringFromList(attrValues));
				}
				ps.execute();
				if (ei.getIdentityColumn() != null) {
					int id = -1;
					rs = ps.getGeneratedKeys();
					if (rs.next()) {
						// Retrieve the auto generated key(s).
						id = rs.getInt(1);
					}
					BeanUtils.forceSetProperty(vo, ei.getCol_attr().get(ei.getIdentityColumn()), id);
				}
			}
			conn.commit(); //事务提交
			
		} catch (NoSuchFieldException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} finally {
			DBUtils.close(conn, ps, rs);
		}
		return listVo;
	}

	public int executeSQLUpdate(String sql, Object... params) {
		Connection conn =getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			if(DataBaseConfig.IsDebug){
				System.out.println(sql);
				System.out.println(MyStringTool.stringFromObjects(params));
			}
			sp.setStatementParamsValue(ps, params);
			int res = ps.executeUpdate();
			return res;
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return -1;
		} finally {
			DBUtils.close(conn, ps);
		}
	}
	
	public int executeMuttSQLUpdate(Map<String, Object[]> sqls){
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {	
			conn.setAutoCommit(false);//禁止自动提交，设置回滚点
			for(Entry<String, Object[]> en : sqls.entrySet()){
				ps = conn.prepareStatement(en.getKey());
				sp.setStatementParamsValue(ps, en.getValue());
				ps.executeUpdate(); //数据库更新操作2
			}
	        conn.commit(); //事务提交
	        return 1;
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			return -1;
		} finally {
			DBUtils.close(conn, ps);
	}
	}

	public void update(T vo) {
		ArrayList<String> col_list = new ArrayList<String>(ei.getCol_attr().keySet());
		String sql = getMultiPleEntitySql(vo, col_list, false);
		Connection conn =  getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			// 在即将要处理的列列表中移除ID列，为了确保ID列的值在最末尾
			List<String> attr_list = ei.getAttrListWithoutIDColumnByColList(col_list);
			//取到除ID以外所有属性的值
			List attrValues = BeanUtils.getValueListFromEntity(vo, attr_list);

			for (String attr : ei.getAttr_col().keySet()) {
				String colName = ei.getAttr_col().get(attr);
				Type ft = ei.getAll_attr_types().get(attr);
			}
			//取到所有ID属性的值
			List identities = ei.getIDAttrValues(vo);
			attrValues.addAll(identities);
			sp.setStatementParamsValue(ps, attrValues.toArray());
			if(DataBaseConfig.IsDebug){
				System.out.println(sql);
				System.out.println(MyStringTool.stringFromList(attrValues));
			}
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} catch (NoSuchFieldException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} finally {
			DBUtils.close(conn, ps);
		}
	}
	
	public  Connection getConnection(Boolean isReadOnly){
		return DataSourceFactory.getConnection(isReadOnly,ei.getDataSource());
	}
	public  Connection getConnection(){
		return DataSourceFactory.getConnection(ei.getDataSource());
	}
}
