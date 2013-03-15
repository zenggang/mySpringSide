package org.myspringside.dao.imp.jdbc.support;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.myspringside.dao.imp.jdbc.support.config.DataBaseConfig;
import org.myspringside.dao.imp.jdbc.tools.LoggerTool;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DataSourceFactory {
	private static Map<String, DataSource> dataSources_map = new HashMap<String, DataSource>();
	private static DataBaseConfig dbConfig = new DataBaseConfig();
	public static String DEFAULT_DATASOURCE_KEY = "default_datasource_key";
	public static String READ_ONLY_DATASOURCE_KEY = "read_only_default_datasource_key";
	private static Random ran = new Random();
	private static String[] readOnlyKeys=new String[]{};
	private static String[] otherKeys=new String[]{};
	public static DataBaseConfig getDbConfig() {
		return dbConfig;
	}

	public static void setDbConfig(DataBaseConfig dbConfig) {
		DataSourceFactory.dbConfig = dbConfig;
	}

	private DataSourceFactory() {
		super();
	}

	static public void init(String path) {
		
		if (dataSources_map.isEmpty()) {
			dbConfig.init(path);
			setDataSouce(dbConfig, DEFAULT_DATASOURCE_KEY);
			if(dbConfig.getReadOnlyDataSourceMap()!=null  ){
				readOnlyKeys =dbConfig.getReadOnlyDataSourceMap().keySet().toArray(readOnlyKeys);
				for(String key:readOnlyKeys){
					setDataSouce(dbConfig.getReadOnlyDataSourceMap().get(key), key);
				}
			}
			if(dbConfig.getOtherDataSourceMap()!=null){
				otherKeys=dbConfig.getOtherDataSourceMap().keySet().toArray(otherKeys);
				for(String key:otherKeys){
					setDataSouce(dbConfig.getOtherDataSourceMap().get(key), key);
				}
			}
		}
	}

	static public void init(DataBaseConfig dc) {
		if (dataSources_map.isEmpty()) {
			dbConfig = dc;
			setDataSouce(dbConfig, DEFAULT_DATASOURCE_KEY);
		}
	}

	public static void setDataSouce(DataBaseConfig config, String key) {
		if (config.getDataSourceType().equalsIgnoreCase(DataBaseConfig.C3P0))
			initC3P0(config, key);
		else if (config.getDataSourceType().equalsIgnoreCase(DataBaseConfig.DBCP))
			initDBCP(config, key);
		else if (config.getDataSourceType().equalsIgnoreCase(DataBaseConfig.SEVER))
			initServerDataSource(config, key);
	}

	private static void initServerDataSource(DataBaseConfig config, String key) {
		try {
			Context env = new InitialContext();
			DataSource ds = (DataSource) env.lookup(config.getUrl());
			if (ds == null)
				throw new Exception("unknown DataSource");
			else
				dataSources_map.put(key, ds);
		} catch (NamingException e) {
			e.printStackTrace(); LoggerTool.error( e);
		} catch (Exception e) {
			e.printStackTrace(); LoggerTool.error( e);
		}
	}

	private static void initC3P0(DataBaseConfig config, String key) {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass(config.getDriver());
		} catch (PropertyVetoException e) {
			e.printStackTrace(); LoggerTool.error(e);
		}
		System.out.println("数据源"+config.dataSourceName+"开始C3P0初始化。。。");
		cpds.setUser(config.getUserName());
		cpds.setPassword(config.getPassword());
		cpds.setJdbcUrl(config.getUrl());
		//初始化时获取N个连接，取值应在minPoolSize与maxPoolSize之间
		cpds.setInitialPoolSize(20);
		//最大空闲时间,N秒内未使用则连接被丢弃。若为0则永不丢
		cpds.setMaxIdleTime(60);
		//当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。Default: 3 
		cpds.setAcquireIncrement(5);
		//定义在从数据库获取新连接失败后重复尝试的次数
		cpds.setAcquireRetryAttempts(20);
		
		cpds.setMaxStatements(0);
		//连接池中保留的最大连接数。Default: 15
		System.out.println(config.dataSourceName+"初始化最大连接数:"+config.getMaxPoolSize());
		cpds.setMaxPoolSize(config.getMaxPoolSize());
		System.out.println(config.dataSourceName+"初始化最小连接数:"+config.getMinPoolSize());
		//连接池中保留的最小连接数。Default: 15
		cpds.setMinPoolSize(config.getMinPoolSize());
		//每30秒检查所有连接池中的空闲连接
		cpds.setIdleConnectionTestPeriod(30);
		//定义了连接池内单个连接所拥有的最大缓存statements数
		cpds.setMaxStatementsPerConnection(100);
		dataSources_map.put(key, cpds);
	}

	private static void initDBCP(DataBaseConfig config, String key) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(config.getDriver());
		ds.setUrl(config.getUrl());
		ds.setUsername(config.getUserName());
		ds.setPassword(config.getPassword());
		ds.setInitialSize(5);
		ds.setMaxActive(100);
		ds.setMaxIdle(30);
		ds.setMaxWait(1000);
		ds.setPoolPreparedStatements(true);
		dataSources_map.put(key, ds);
	}

	public static DataSource getMainDataSource() {
//		if (dataSources_map.isEmpty()) {
//			setDataSouce(dbConfig, DEFAULT_DATASOURCE_KEY);
//			if(dbConfig.getReadOnlyDataSourceMap()!=null  ){
//				readOnlyKeys =(String[]) dbConfig.getReadOnlyDataSourceMap().keySet().toArray();
//				for(String key:readOnlyKeys){
//					setDataSouce(dbConfig.getReadOnlyDataSourceMap().get(key), key);
//				}
//			}
//			if(dbConfig.getOtherDataSourceMap()!=null){
//				otherKeys=dbConfig.getOtherDataSourceMap().keySet().toArray(otherKeys);
//				for(String key:otherKeys){
//					setDataSouce(dbConfig.getOtherDataSourceMap().get(key), key);
//				}
//			}
//		}
		return dataSources_map.get(DEFAULT_DATASOURCE_KEY);
	}

	public static DataSource getDataSource(String key) {
		return dataSources_map.get(key);
	}

	public static void setDataSource(DataSource dataSource) {
		dataSources_map.put(DEFAULT_DATASOURCE_KEY, dataSource);
	}

	static public Connection getMainConnection() {
		try {
			return DataSourceFactory.getMainDataSource().getConnection();
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error( e);
		}
		return null;
	}

	static public Connection getConnection(Boolean isReadOnly,String key) {
		try {
			if(key==null){
				if(isReadOnly && dbConfig.getReadOnlyEnable()){
					
					int i=readOnlyKeys.length;
					if(i>0){
						if(i==1){
							
							return DataSourceFactory.getDataSource(readOnlyKeys[0]).getConnection();
						}else{
							int j =ran.nextInt(i);
							return DataSourceFactory.getDataSource(readOnlyKeys[j]).getConnection();
						}
					}else
						throw new RuntimeException("no readOnly dataSource was setting!");
				}else{
						return DataSourceFactory.getMainDataSource().getConnection();
				}
			}else
				return DataSourceFactory.getDataSource(key).getConnection();
			
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error( e);
		}
		return null;
	}

	
	static public Connection getConnection(String datasourceKey) {
		try {
			if(datasourceKey==null)
				return DataSourceFactory.getMainDataSource().getConnection();
			else
				return DataSourceFactory.getDataSource(datasourceKey).getConnection();
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(e);
		}
		return null;
	}
	

}
