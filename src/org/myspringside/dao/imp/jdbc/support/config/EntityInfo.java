package org.myspringside.dao.imp.jdbc.support.config;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.myspringside.dao.imp.jdbc.exception.ArgumentTypeIllegalException;
import org.myspringside.dao.imp.jdbc.tools.DataSource;
import org.myspringside.dao.imp.jdbc.tools.LoggerTool;
import org.myspringside.dao.imp.jdbc.tools.MultipleTable;
import org.myspringside.dao.imp.jdbc.tools.BeanUtils;
import org.myspringside.dao.imp.jdbc.tools.GenericsUtils;
import org.myspringside.dao.imp.jdbc.tools.MultipleTableField;

/**
 * @author Alex
 */
@SuppressWarnings("unchecked")
public class EntityInfo<T> {
	String table = null;
	Class<T> entityClass;
	Map<String, String> col_attr = new HashMap<String, String>();
	Map<String, String> attr_col = new HashMap<String, String>();
	@SuppressWarnings("unchecked")
	Map<String, Class> all_attr_types = new HashMap<String, Class>();
	Map<String, String> column_types = new HashMap<String, String>();
	List<String> idAttrs = new ArrayList<String>();
	List<String> idCols = new ArrayList<String>();
	String identityColumn;
	Map<Class, String> class_listAttr = new HashMap<Class, String>();
	
	public Boolean isMultiPleEntity=false;
	int multiplePageSize=0;
	String multipleField="";
	String dataSource;
	
	public final static String CLOB = "clob";
	
	
	
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public Map<String, String> getAttr_col() {
		return attr_col;
	}

	public void setAttr_col(Map<String, String> attr_col) {
		this.attr_col = attr_col;
	}

	public String getTable() {
		
		return table;
	}
	public String getMultiPleTableName(int multipleFieldValue) {
		if(multiplePageSize==0)
			return table;
		else{
			return table+"_"+(multipleFieldValue/multiplePageSize+1);
		}
			
	}
	public String getMultipleField(){
		return multipleField;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Map<String, String> getCol_attr() {
		return col_attr;
	}

	public void setCol_attr(Map<String, String> col_attr) {
		this.col_attr = col_attr;
	}

	public boolean isNotIDAttr(String attr) {
		for (String idattr : getIdAttrs()) {
			if (idattr.equals(attr))
				return false;
		}
		return true;
	}

	public boolean isNotIDColumn(String col) {
		for (String idcol : getIdCols()) {
			if (idcol.equals(col))
				return false;
		}
		return true;
	}

	public List getIDAttrValues(T vo) {
		List identities = null;
		try {
			identities = new ArrayList();
			for (String idAttr : getIdAttrs()) {
				identities.add(BeanUtils.forceGetProperty(vo, idAttr));
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		}
		return identities;
	}

	public List<String> getAttrListWithoutIDColumnByColList(ArrayList<String> col_list) {
		for (int i = 0; i < col_list.size(); i++) {
			if (!isNotIDColumn(col_list.get(i))) {
				col_list.remove(i);
				i--;
			}

		}
		List<String> attr_list = new ArrayList<String>();
		for (String col : col_list) {
			attr_list.add(getCol_attr().get(col));
		}
		return attr_list;
	}
	public ArrayList<String> getColumListWithoutIDColumnByColList(ArrayList<String> col_list){
		for (int i = 0; i < col_list.size(); i++) {
			if (!isNotIDColumn(col_list.get(i))) {
				col_list.remove(i);
				i--;
			}
		}
		return col_list;
	}

	public List<String> getAttrListWithoutIdentityColumnByColList(ArrayList<String> col_list) {
		for (int i = 0; i < col_list.size(); i++) {
			if (col_list.get(i).equals(getIdentityColumn()))
				col_list.remove(i);
		}
		List<String> attr_list = new ArrayList<String>();
		for (String col : col_list) {
			attr_list.add(getCol_attr().get(col));
		}
		return attr_list;
	}

	@SuppressWarnings("unchecked")
	void initAnnotaionInfo(Class c) {
		this.entityClass = c;
		try {
			if (!c.isAnnotationPresent(Entity.class)) {
				throw new Exception("@Entity annotaion is not present!");
			}

			Field[] fs = c.getDeclaredFields();
			for (Field f : fs) {
				//获得实体属性的名称 
				String attrName = f.getName();
				Class ft = f.getType();
				if (!f.isAnnotationPresent(Transient.class)) {
					getAll_attr_types().put(attrName, ft);
					if (f.isAnnotationPresent(Column.class)) {
						Column col = (Column) f.getAnnotation(Column.class);
						String colName = col.name();

						if (col.columnDefinition() != null) {
							if (col.columnDefinition().equalsIgnoreCase(CLOB))
								column_types.put(colName, CLOB);
						}
						col_attr.put(colName, attrName);
						attr_col.put(attrName, colName);

						if (f.isAnnotationPresent(Id.class)) {
							idAttrs.add(attrName);
							idCols.add(colName);
							setupGeneratedValue(f, colName);
						}
					} else if (isPersistentable(f)) {
						col_attr.put(attrName, attrName);
						attr_col.put(attrName, attrName);
						//如果这一列是ID列但没有@Column注释则将属性名作为列名列名加入ID列集合
						if (f.isAnnotationPresent(Id.class)) {
							idAttrs.add(attrName);
							idCols.add(attrName);
							setupGeneratedValue(f, attrName);
						}
					} else if (f.getType().equals(List.class)) {
						Class fgt = GenericsUtils.getClassFieldGenricType(c, attrName);
						class_listAttr.put(fgt, attrName);
					}
				}
				//不论该属性是否透明，都保存它的名称和类型的映射信息，以防临时添加映射之用。
				getAll_attr_types().put(attrName, f.getType());
				
				if(f.isAnnotationPresent(MultipleTableField.class)){
					MultipleTableField multiField = f.getAnnotation(MultipleTableField.class);
					multiplePageSize = multiField.pageSize();
					if(multiplePageSize==0)
						throw new Exception("No  multiplePageSize is setting !");
					multipleField=attrName;
				}
				
			}
			if (idAttrs.size() == 0) {
				try {
					@SuppressWarnings("unused")
					Field idf = c.getField("id");
				} catch (NoSuchFieldException e) {
					e.printStackTrace(); LoggerTool.error(this.getClass(), e);
				}
				idAttrs.add("id");
			}

			if (c.isAnnotationPresent(Table.class)) {
				Table tc = (Table) c.getAnnotation(Table.class);
				table = tc.name();
				if(c.isAnnotationPresent(MultipleTable.class)){
					if(multiplePageSize==0)
						throw new Exception("No  MultipleTableField annotaion is  present!");
					isMultiPleEntity=true;
				}
			} else {
				throw new Exception("@Table annotaion is not present!");
			}
			if (c.isAnnotationPresent(DataSource.class)) {
				DataSource tc = (DataSource) c.getAnnotation(DataSource.class);
				dataSource=tc.name();
			}
			//如果主键超过一个则不能使用自动增长，否则抛出异常
			if (getIdAttrs().size() > 1 && getIdentityColumn() != null) {
				throw new Exception("mutiple id column can not use identity column");
			}
		} catch (Exception e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		}
	}

	private void setupGeneratedValue(Field f, String colName) throws ArgumentTypeIllegalException {
		if (f.isAnnotationPresent(GeneratedValue.class)) {
			Type ft = f.getType();
			GeneratedValue gv = (GeneratedValue) f.getAnnotation(GeneratedValue.class);
			GenerationType gt = gv.strategy();
			if (gt.equals(GenerationType.IDENTITY)) {
				if (!(ft.equals(Integer.TYPE) || ft.equals(Integer.class) || ft.equals(Long.TYPE) || ft
						.equals(Long.class)))
					throw new ArgumentTypeIllegalException("identity ID can't be type which is not Integer or Long.");
				//如果已经有一列被设置为自动增长则抛出异常
				if (identityColumn != null)
					throw new IllegalArgumentException("there can be only one identity column");
				identityColumn = colName;
			}

		}
	}

	private boolean isPersistentable(Field f) {
		if (BeanUtils.isBasicType(f))
			return true;
		else if (f.getType().equals(String.class) || f.getType().equals(Date.class))
			return true;
		else
			return false;
	}

	public Map<String, String> getColumn_types() {
		return column_types;
	}

	public void setColumn_types(Map<String, String> column_types) {
		this.column_types = column_types;
	}

	public Map<String, Class> getAll_attr_types() {
		return all_attr_types;
	}

	public void setAll_attr_types(Map<String, Class> all_attr_types) {
		this.all_attr_types = all_attr_types;
	}

	public List<String> getIdAttrs() {
		return idAttrs;
	}

	public void setIdAttrs(List<String> idAttrs) {
		this.idAttrs = idAttrs;
	}

	public List<String> getIdCols() {
		return idCols;
	}

	public void setIdCols(List<String> idCols) {
		this.idCols = idCols;
	}

	public String getIdentityColumn() {
		return identityColumn;
	}

	public void setIdentityColumn(String identityColumn) {
		this.identityColumn = identityColumn;
	}

	public Map<Class, String> getClass_listAttr() {
		return class_listAttr;
	}

	public void setClass_listAttr(Map<Class, String> class_listAttr) {
		this.class_listAttr = class_listAttr;
	}

	public Class getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
	}

}
