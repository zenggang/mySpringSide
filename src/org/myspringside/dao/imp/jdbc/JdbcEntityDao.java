package org.myspringside.dao.imp.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.myspringside.dao.api.database.IjdbcEntityDao;
import org.myspringside.dao.imp.jdbc.support.DataSourceFactory;
import org.myspringside.dao.imp.jdbc.support.CommonQueryUtils;
import org.myspringside.dao.imp.jdbc.support.config.DataBaseConfig;
import org.myspringside.dao.imp.jdbc.support.config.EntityInfo;
import org.myspringside.dao.imp.jdbc.support.config.EntityInfoFactory;
import org.myspringside.dao.imp.jdbc.support.processor.ResultSetProcessor;
import org.myspringside.dao.imp.jdbc.support.processor.StatementProcessor;
import org.myspringside.dao.imp.jdbc.support.processor.crud.CRUDProcessor;
import org.myspringside.dao.imp.jdbc.tools.BeanUtils;
import org.myspringside.dao.imp.jdbc.tools.GenericsUtils;
import org.myspringside.dao.imp.jdbc.tools.Page;
import org.myspringside.dao.imp.jdbc.tools.Query;

@SuppressWarnings("unchecked")
public class JdbcEntityDao<T> implements IjdbcEntityDao<T> {
	private EntityInfo<T> ei;
	private StatementProcessor sp;
	private ResultSetProcessor rp;
	private CommonQueryUtils<T> queryUtils;
	private CRUDProcessor<T> curdProcessor;

	public JdbcEntityDao() {
	}
	
	public String getMulTipleTableName(int multipleFieldValue){
		return ei.getMultiPleTableName(multipleFieldValue);
	}
	
	public JdbcEntityDao(Class entityClass) {
		String path = BeanUtils.getClassPath(entityClass) + "/" + DataBaseConfig.CONFIG_FILE_NAME;
		init(entityClass, path);
	}

	public JdbcEntityDao(Class entityClass, String path) {
		init(entityClass, path);
	}

	// 在脱离spring单独使用时，使用此初始化函数
	private void init(Class entityClass, String path) {
		innerInit(entityClass);
		DataSourceFactory.init(path);
	}

	//集成spring时，使用此初始化函数
	@SuppressWarnings("unused")
	private void init() {
		Class<T> entityClass = GenericsUtils.getSuperClassGenricType(this.getClass());
		if (entityClass == null) {
			throw new RuntimeException("Generics Parameter is not specify.");
		}
		innerInit(entityClass);
	}

	private void innerInit(Class entityClass) {
		this.ei = EntityInfoFactory.initEntityInfoFromClassAnnotation(entityClass);
		rp = new ResultSetProcessor();
		sp = new StatementProcessor<T>(ei);
		queryUtils = new CommonQueryUtils<T>(sp);
		curdProcessor = new CRUDProcessor<T>(ei, queryUtils, sp, rp);
	}

	public String getDialect() {
		return DataSourceFactory.getDbConfig().getDialect();
	}

	public void setDialect(String dia) {
		DataSourceFactory.getDbConfig().setDialect(dia);
	}

	public Connection getConnection() {
		return DataSourceFactory.getConnection(ei.getDataSource());
	}

	public List<T> findBySQLQuery(String sql, Object... params) {
		return curdProcessor.findBySQLQuery(true,sql, params);
	}

	public T findUniqueBySQLQuery(String sql, Object... params) {
		List<T> vol = findBySQLQuery(sql, params);
		if (vol == null || vol.size() == 0)
			return null;
		else
			return vol.get(0);
	}

	public int executeSQLUpdate(String sql, Object... params) {
		return curdProcessor.executeSQLUpdate(sql, params);
	}
	
	public int executeMuttSQLUpdate(Map<String, Object[]> sqls) {
		return curdProcessor.executeMuttSQLUpdate(sqls);
	}

	public List<T> findByMutiAttr(String... params) {
		return curdProcessor.findByMutiAttr(params);
	}

	public T findUniqueByMutiAttr(String... params) {
		List<T> vl = findByMutiAttr(params);
		if (vl==null || vl.size() == 0 ) {
			return null;
		} else {
			return vl.get(0);
		}
	}

	public int findIntValueBySQLQuery(String sql, Object... params) {
		return curdProcessor.findIntValueBySQLQuery(true,sql, params);
	}
	
	public List<Integer> findIntValueListBySQLQuery(String sql, Object... params) {
		return curdProcessor.findIntValueListBySQLQuery(true,sql, params);
	}
	
	public T get(Object... id) {
		return curdProcessor.get(true,id);
	}
	public T getFromMainSource(Object... id) {
		return curdProcessor.get(false,id);
	}

	public List<T> getAll() {
		return curdProcessor.getAll(true);
	}
	public List<T> getAllFromMainSource() {
		return curdProcessor.getAll(false);
	}

	public T insert(T vo) throws SQLException {
		return curdProcessor.insert(vo);
	}
	public List<T> insertListVo(List<T> listVo) throws SQLException{
		return curdProcessor.insertListVo(listVo);
	} 
	
	public List<T> insertListMutipleEntityVo(List<T> listVo,
			Boolean isSingleTable) throws SQLException {
		return curdProcessor.insertListMutipleEntityVo(listVo, isSingleTable);
	}
	@Deprecated
	public void save(T vo) throws SQLException {
		curdProcessor.save(vo);
	}

	public void update(T vo) throws SQLException {
		curdProcessor.update(vo);
	}

	public void remove(Object... id) {
		curdProcessor.remove(id);
	}

	public void remove(T vo) {
		curdProcessor.remove(vo);
	}

	public Page<T> pageQuery(int pageNo, int pageSize,String sql, String... params) {
		return curdProcessor.pageQuery(pageNo, pageSize,sql, params);
	}

	public int SQLQueryForInt(String sql, Object... params) {
		return queryUtils.SQLQueryForInt(ei.getDataSource(),sql, params);
	}

	public DataSource getDatasource() {
		return DataSourceFactory.getDataSource(ei.getDataSource());
	}

	public void setDatasource(DataSource datasource) {
		DataSourceFactory.setDataSource(datasource);
	}

	public int getTableRecordCount() {
		return queryUtils.getRecordCountBySQL(ei.getDataSource(),sp, ei.getTable());
	}

	public List<T> resultSetTovoList(ResultSet rs) {
		return rp.resultSetTovoList(rs, ei);
	}

	public Query<T> addTempColumnMapping(String... params) {
		Query<T> query = new Query<T>(ei);
		query.addTempColumnMapping(params);
		return query;
	}

	public Query<T> addTempEntityMapping(Class... vos) {
		Query<T> query = new Query<T>(ei);
		query.addTempEntityMapping(vos);
		return query;
	}

	@Override
	public String findStringValueBySQLQuery(String sql, Object... params) {
		
		return curdProcessor.findStringValueBySQLQuery(true,sql, params);
	}

	@Override
	public List<String> findStringValueListBySQLQuery(String sql,
			Object... params) {
		
		return curdProcessor.findStringValueListBySQLQuery(true,sql, params);
	}

	@Override
	public List<T> findBySQLQueryFromMainSource(String sql, Object... params) {
		return curdProcessor.findBySQLQuery(false,sql, params);
	}

	@Override
	public T findUniqueBySQLQueryFromMainSource(String sql, Object... params) {
		List<T> vol = findBySQLQueryFromMainSource(sql, params);
		if (vol == null || vol.size() == 0)
			return null;
		else
			return vol.get(0);
	}
	
	

}
