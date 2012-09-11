package org.myspringside.dao.imp.jdbc.support.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Alex
 */
public class DataBaseConfig {
	private String driver;
	private String url;
	private String userName;
	private String password;
	private String dialect;
	private String dataSourceType;
	private String readOnlySource;
	private Boolean readOnlyEnable=false;
	private Map<String, DataBaseConfig> readOnlyDataSourceMap;

	public static String DBCP = "dbcp";
	public static String C3P0 = "c3p0";
	public static String SEVER="server";
	public static String CONFIG_FILE_NAME = "dbConfig.properties";

	public static String MYSQL = "MySQL";
	public static String SQL_SERVER = "SQL Server";
	public static String ORACLE = "Oracle";
	public static Boolean IsDebug=false;
	
	private int maxPoolSize=250;
	private int minPoolSize=20;
	public String dataSourceName="dbConfig";
	public DataBaseConfig() {
	
	}
	
	public DataBaseConfig(String fn) {
		init(fn);
	}
	
	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void init(String fn) {
		if (driver == null || url == null || userName == null || password == null || dialect == null
				|| dataSourceType == null) {
			Properties p;
			FileInputStream fi;
			File file = new File(fn);
			try {
				fi = new FileInputStream(file);
				p = new Properties();
				p.load(fi);
				fi.close();
				driver = p.getProperty("driver");
				url = p.getProperty("url");
				userName = p.getProperty("userName");
				password = p.getProperty("password");
				dialect = p.getProperty("dialect");
				dataSourceType = p.getProperty("datasourceType");
				readOnlySource=p.getProperty("readOnlySource");
				String debug = p.getProperty("debug");
				readOnlyEnable = "true".equals(p.getProperty("readOnlyEnable"));
				if(p.getProperty("maxPoolSize")!=null)
					maxPoolSize =Integer.valueOf(p.getProperty("maxPoolSize"));
				if(p.getProperty("minPoolSize")!=null)
					minPoolSize =Integer.valueOf(p.getProperty("minPoolSize"));
				
				if (dataSourceType == null)
					throw new NullPointerException("datasourceType is undefined.");
				if (dialect == null) {
					autoSetDialect();
				}
				if(debug!=null && "true".equals(debug)){
					IsDebug=true;
				}
				if(readOnlySource!=null && readOnlyEnable){
					String[] sources = readOnlySource.split(",");
					readOnlyDataSourceMap = new HashMap<String, DataBaseConfig>();
					for(String source:sources){
						String key = source;
						String path = fn.replace("dbConfig", key);
						DataBaseConfig dbConfig = new DataBaseConfig(path);
						dbConfig.dataSourceName=key;
						readOnlyDataSourceMap.put(key, dbConfig);
					}
				}
				checkDialect(dialect);
				fi = null;
				p = null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	private void checkDialect(String dialect) {
		if (dialect == null)
			throw new IllegalArgumentException("the Dialect wasn't specify!");
		if (dialect.equalsIgnoreCase(MYSQL)) {
			this.dialect = MYSQL;
		} else if (dialect.equalsIgnoreCase(SQL_SERVER)) {
			this.dialect = SQL_SERVER;
		} else if (dialect.equalsIgnoreCase(ORACLE)) {
			this.dialect = ORACLE;
		} else {
			throw new IllegalArgumentException("the Dialect is not support!");
		}
	}

	private void autoSetDialect() {
		if (driver.equals("com.mysql.jdbc.Driver"))
			dialect = MYSQL;
		else if (driver.equals("com.microsoft.sqlserver.jdbc.SQLServerDriver"))
			dialect = SQL_SERVER;
		else if (driver.equals("oracle.jdbc.driver.OracleDriver"))
			dialect = ORACLE;
	}

	public String getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public Map<String, DataBaseConfig> getReadOnlyDataSourceMap() {
		return readOnlyDataSourceMap;
	}

	public Boolean getReadOnlyEnable() {
		return readOnlyEnable;
	}

	public void setReadOnlyEnable(Boolean readOnlyEnable) {
		this.readOnlyEnable = readOnlyEnable;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	
	
}
