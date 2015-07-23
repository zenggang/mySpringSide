package org.myspringside.dao.api.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.myspringside.dao.imp.jdbc.tools.Page;
import org.myspringside.dao.imp.jdbc.tools.Query;

/**
 * @author Alex
 */
@SuppressWarnings("unchecked")
public interface IjdbcEntityDao<T> {
	T get(Object... id);
	public T getFromMainSource(Object... id);
	List<T> findByMutiAttr(String... params);

	public List<T> getAll();
	public List<T> getAllFromMainSource();

	public void update(T vo) throws SQLException;

	public T insert(T vo) throws SQLException;
	public List<T> insertListVo(List<T> listVo) throws SQLException;
	public List<T> insertListMutipleEntityVo(List<T> listVo,Boolean isSingleTable) throws SQLException;
	public void remove(T vo);

	public void remove(Object... id);

	public T findUniqueByMutiAttr(String... params);

	public Connection getConnection();

	public Page<T> pageQuery(int pageNo, int pageSize,String sql, String... params);

	public List<T> findBySQLQuery(String sql, Object... params);
	public List<T> findBySQLQueryFromMainSource(String sql, Object... params);

	public int executeSQLUpdate(String sql, Object... params);
	
	public int executeMuttSQLUpdate(Map<String, Object[]> sqls) ;

	public Query<T> addTempColumnMapping(String... params);

	public int SQLQueryForInt(String sql, Object... params);

	public Query<T> addTempEntityMapping(Class... vos);

	public T findUniqueBySQLQuery(String sql, Object... params);
	public T findUniqueBySQLQueryFromMainSource(String sql, Object... params);

	public List<T> resultSetTovoList(ResultSet rs);

	public void save(T vo) throws SQLException;
	
	public int findIntValueBySQLQuery(String sql, Object... params);
	
	public List<Integer> findIntValueListBySQLQuery(String sql, Object... params);
	
	public String findStringValueBySQLQuery(String sql, Object... params);
	
	public List<String> findStringValueListBySQLQuery(String sql, Object... params);

}
