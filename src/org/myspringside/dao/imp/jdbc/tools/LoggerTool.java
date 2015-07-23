package org.myspringside.dao.imp.jdbc.tools;

import org.apache.log4j.Logger;
import org.myspringside.dao.imp.jdbc.support.config.DataBaseConfig;


public class LoggerTool {
	public static Logger logger = Logger.getLogger ("");
	
	public static void info(String message){
		if(DataBaseConfig.IsDebug)
			System.out.println(message);
		logger.info(message);
	}
	public static void debug(String message){
		if(DataBaseConfig.IsDebug)
			System.out.println(message);
		logger.debug(message);
	}
	public static void warn(String message){
		if(DataBaseConfig.IsDebug)
			System.out.println(message);
		logger.warn(message);
	}
	
	public static void error(String message){
		if(DataBaseConfig.IsDebug)
			System.out.println(message);
		logger.error(message);
	}
	
	public static void error(Exception e){
		if(DataBaseConfig.IsDebug)
			e.printStackTrace();
		logger.error(e.getMessage(), e);
	}
	@SuppressWarnings("rawtypes")
	public static void error(Class c ,Exception e){
		if(DataBaseConfig.IsDebug)
			e.printStackTrace();
		logger.error(c.toString(), e);
	}
	
	@SuppressWarnings("rawtypes")
	public static void error(Class c ,Throwable e){
		if(DataBaseConfig.IsDebug)
			e.printStackTrace();
		logger.error(c.toString(), e);
	}
}
