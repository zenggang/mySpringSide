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
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
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
				e.printStackTrace();
			}
			return false;
		}
		return false;
	}
	/**
	 *  			else if (ei.getColumn_types().get(colName).equals(EntityInfo.CLOB)) {
							try {
								Clob clob = rs.getClob(colName);
								if (clob == null)
									continue;
								Reader reader = clob.getCharacterStream();
								BufferedReader br = new BufferedReader(reader);
								String attrValue = "";
								String line = "";
								while ((line = br.readLine()) != null) {
									attrValue += line;
								}
								attrValue=attrValue.replaceAll("\\s*$","");
								BeanUtils.forceSetProperty(vo, attr, attrValue);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
							@SuppressWarnings("unchecked")
	public <GT> List<GT> resultSetTovoList2(ResultSet rs, EntityInfo<GT> ei) {
		long time1 =new Date().getTime();

		try {
			List<GT> voList = new ArrayList<GT>();
			GT vo = null;
			Object[] gtSet =ei.getAttr_col().keySet().toArray();
			int length = gtSet.length;
			while (rs.next()) {
				vo = (GT) ei.getEntityClass().newInstance();				
				for (int i=0;i<length;i++) {
					String attr = gtSet[i].toString();
					String colName = ei.getAttr_col().get(attr);
					if (!hasColumn(rs, colName))
						continue;
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
			long time2 =new Date().getTime();
			System.out.println("旧方法耗时:"+(time2-time1));
			return voList; 
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}
	 */
}
