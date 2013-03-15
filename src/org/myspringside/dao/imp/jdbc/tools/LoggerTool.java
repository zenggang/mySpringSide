package org.myspringside.dao.imp.jdbc.tools;

import org.apache.log4j.Logger;


public class LoggerTool {
	public static Logger logger = Logger.getLogger ("");
	
	public static void error(Exception e){
		logger.error(e.getMessage(), e);
	}
	@SuppressWarnings("rawtypes")
	public static void error(Class c ,Exception e){
		logger.error(c.toString(), e);
	}
	
	@SuppressWarnings("rawtypes")
	public static void error(Class c ,Throwable e){
		logger.error(c.toString(), e);
	}
}
