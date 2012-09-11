package org.myspringside.dao.imp.jdbc.support.config;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class EntityInfoFactory {

	private static Map<Class, EntityInfo> entity_info_map = new HashMap<Class, EntityInfo>();

	static public EntityInfo initEntityInfoFromClassAnnotation(Class vo) {
		EntityInfo ei = entity_info_map.get(vo);
		if (ei == null) {
			ei = new EntityInfo();
			ei.initAnnotaionInfo(vo);
			entity_info_map.put(vo, ei);
		}
		return ei;
	}

	static public EntityInfo getEntityInfo(Class vo) {
		EntityInfo ei = entity_info_map.get(vo);
		if (ei == null) {
			return initEntityInfoFromClassAnnotation(vo);
		} else
			return ei;
	}
}
