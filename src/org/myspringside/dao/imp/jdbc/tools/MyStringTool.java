package org.myspringside.dao.imp.jdbc.tools;

import java.util.List;


public class MyStringTool {
	
	public static String stringFromList(List<Object> list){
		if(list!=null){
			StringBuilder builder =new StringBuilder();
			for(Object obj:list){
				builder.append(obj.toString()).append(",");
			}
			return builder.toString();
		}else
			return null;
	}
	public static String stringFromObjects(Object[] params){
		if(params!=null){
			StringBuilder builder =new StringBuilder();
			for(Object obj:params){
				builder.append(obj.toString()).append(",");
			}
			return builder.toString();
		}else
			return null;
	}
	
}
