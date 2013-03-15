package org.myspringside.dao.imp.jdbc.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.myspringside.dao.imp.jdbc.support.CommonQueryUtils;
import org.myspringside.dao.imp.jdbc.support.config.EntityInfo;
import org.myspringside.dao.imp.jdbc.support.processor.ResultSetProcessor;
import org.myspringside.dao.imp.jdbc.support.processor.StatementProcessor;
import org.myspringside.dao.imp.jdbc.support.processor.crud.CRUDProcessor;

@SuppressWarnings("unchecked")
public class Query<T> {
	List<Class> list_attr_classes = new ArrayList<Class>();
	List<String> temp_attrs = new ArrayList<String>();
	CRUDProcessor<T> qp;
	EntityInfo<T> ei;
	ResultSetProcessor rp;

	public Query(EntityInfo ei) {
		super();
		this.ei = ei;
		rp = new ResultSetProcessor();
		StatementProcessor sp = new StatementProcessor<T>(ei);
		CommonQueryUtils<T> cqu = new CommonQueryUtils<T>(sp);
		qp = new CRUDProcessor<T>(ei, cqu, sp, rp);
	}

	public void addTempEntityMapping(Class... vos) {
		for (Class vo : vos) {
			list_attr_classes.add(vo);
		}
	}

	public void addTempColumnMapping(String... params) {
		for (String param : params) {
			if (QueryConditions.pat.matcher(param).matches()) {
				String[] attr_col = param.split("=");
				temp_attrs.add(attr_col[0]);
				Set<String> all_attrs = ei.getAll_attr_types().keySet();
				if (!all_attrs.contains(attr_col[0])) {
					throw new IllegalArgumentException("the temp attribute name of entity don't exist.");
				}
				ei.getAttr_col().put(attr_col[0], attr_col[1]);
			} else {
				throw new IllegalArgumentException("parameter syntax must be like '...=...'");
			}
		}
	}

	public Page<T> pageQuery(int pageNo, int pageSize, String sql_params) {
		return qp.pageQuery(sql_params, pageNo, pageSize);
	}

	public List<T> findBySQLQuery(String sql, Object... params) {
		try {
			List<T> vol = qp.findBySQLQuery(true,sql, params);
			return vol;
		} finally {
			for (String attr : temp_attrs) {
				ei.getAttr_col().remove(attr);
			} 
		}
	}
	public T findUniqueBySQLQuery(String sql, Object... params) {
		List<T> vol = findBySQLQuery(sql, params);
		if (vol == null || vol.size() == 0)
			return null;
		else
			return vol.get(0);
	}

	
	public CRUDProcessor<T> getCRUDProcessor(){
		return qp;  
	}

	@SuppressWarnings("unused")
	private void removeDuplicateEntity(List<T> vol) {
		for (int i = 0; i < vol.size(); i++) {
			for (int j = 0; j < vol.size(); j++) {
				if (j != i) {
					if (EntityEqual(vol.get(i), vol.get(j), ei))
						vol.remove(j);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private List<Integer> getSameEntityInList(List<T> vol, int i) {
		List<Integer> result = new ArrayList<Integer>();
		for (int j = 0; j < vol.size(); j++) {
			if (j != i) {
				if (EntityEqual(vol.get(i), vol.get(j), ei))
					result.add(j);
			}
		}
		return result;
	}

	private boolean EntityEqual(T vo1, T vo2, EntityInfo<T> ei) {
		try {
			for (String idattr : ei.getIdAttrs()) {
				Object id1 = BeanUtils.forceGetProperty(vo1, idattr);
				Object id2 = BeanUtils.forceGetProperty(vo2, idattr);
				if (id1 == null || id2 == null)
					return false;
				if (!id1.equals(id2))
					return false;
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		}
		return true;
	}
}
