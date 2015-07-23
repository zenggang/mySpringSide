package org.myspringside.dao.imp.jdbc.support.processor;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import org.myspringside.dao.imp.jdbc.support.DataSourceFactory;
import org.myspringside.dao.imp.jdbc.support.config.DataBaseConfig;
import org.myspringside.dao.imp.jdbc.support.config.EntityInfo;
import org.myspringside.dao.imp.jdbc.tools.BeanUtils;
import org.myspringside.dao.imp.jdbc.tools.LoggerTool;


public class ResultSetProcessor {

	private String getDialect() {
		return DataSourceFactory.getDbConfig().getDialect();
	}
 
	

	
	@SuppressWarnings("unchecked")
	public <GT> List<GT> resultSetTovoList(ResultSet rs, EntityInfo<GT> ei) {
		try {
			List<GT> voList = new ArrayList<GT>();
			GT vo = null;
			
			Map<String, String> realColumnsMap= new HashMap<String, String>();
			for(String attr:ei.getAttr_col().keySet()){
				String colName=ei.getAttr_col().get(attr);
				if (!hasColumn(rs, colName))
					continue;
				else
					realColumnsMap.put(attr, colName);
			}
			while (rs.next()) {
				vo = (GT) ei.getEntityClass().newInstance();
				for(String attr:realColumnsMap.keySet()){
					String colName=realColumnsMap.get(attr);
					
					Type ft = ei.getAll_attr_types().get(attr);
					if (ft.equals(String.class)) {
						if (ei.getColumn_types().get(colName) == null) {
							String attrValue = rs.getString(colName);
							BeanUtils.forceSetProperty(vo, attr, attrValue);
						}
					} else if (ft.equals(Date.class)) {
						if (getDialect().equals(DataBaseConfig.ORACLE) || getDialect().equals(DataBaseConfig.MYSQL) ) {
							Timestamp ts = rs.getTimestamp(colName);
							if (ts != null) {
								Date attrValue = new Date(ts.getTime());
								BeanUtils.forceSetProperty(vo, attr, attrValue);
							}
						} else { 
							Date attrValue = rs.getDate(colName);
							if (attrValue != null) {
								attrValue = new Date(attrValue.getTime());
								BeanUtils.forceSetProperty(vo, attr, attrValue);
							} else
								BeanUtils.forceSetProperty(vo, attr, attrValue);
						}
					} else if (ft.equals(Integer.TYPE)) {
						int attrValue = rs.getInt(colName);
						BeanUtils.forceSetProperty(vo, attr, attrValue);
					} else if (ft.equals(Double.TYPE)) {
						double attrValue = rs.getDouble(colName);
						BeanUtils.forceSetProperty(vo, attr, attrValue);
					} else if (ft.equals(Long.TYPE)) {
						long attrValue = rs.getLong(colName);
						BeanUtils.forceSetProperty(vo, attr, attrValue);
					} else if (ft.equals(Boolean.TYPE)) {
						boolean attrValue = rs.getBoolean(colName);
						BeanUtils.forceSetProperty(vo, attr, attrValue);
					} else if (ft.equals(Float.TYPE)) {
						float attrValue = rs.getFloat(colName);
						BeanUtils.forceSetProperty(vo, attr, attrValue);
					} else if (ft.equals(Short.TYPE)) {
						short attrValue = rs.getShort(colName);
						BeanUtils.forceSetProperty(vo, attr, attrValue);
					} else if (ft.equals(Byte.TYPE)) {
						byte attrValue = rs.getByte(colName);
						BeanUtils.forceSetProperty(vo, attr, attrValue);
					}
				}
				voList.add(vo);
			}

			return voList;
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} catch (InstantiationException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} catch (IllegalAccessException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} catch (SecurityException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		} catch (NoSuchFieldException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		}catch (Exception e) {
			 LoggerTool.error(this.getClass(), e);
		}
		return null;
	}

	private boolean hasColumn(ResultSet rs, String columnName) {
		if(columnName!=null && !"".equals(columnName)){
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int length= rsmd.getColumnCount();
				for (int i = 1; i <= length; i++) {
					String cn = rsmd.getColumnName(i);
					if (columnName.equalsIgnoreCase(cn))
						return true;
				}
			} catch (SQLException e) {
				e.printStackTrace(); LoggerTool.error(this.getClass(), e);
			}
			return false;
		}
		return false;
	}
}
