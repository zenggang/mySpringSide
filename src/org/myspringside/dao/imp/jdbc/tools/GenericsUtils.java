package org.myspringside.dao.imp.jdbc.tools;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Generics的util类,
 * 
 * @author calvin
 */
@SuppressWarnings("unchecked")
public class GenericsUtils {

	private GenericsUtils() {
	}

	/**
	 * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
	 * GenricManager<Book>
	 * 
	 * @param clazz
	 *            The class to introspect
	 * @return the first generic declaration, or <code>Object.class</code> if
	 *         cannot be determined
	 */

	public static Class getSuperClassGenricType(Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
	 * GenricManager<Book>
	 * 
	 * @param clazz
	 *            clazz The class to introspect
	 * @param index
	 *            the Index of the generic ddeclaration,start from 0.
	 */
	public static Class getSuperClassGenricType(Class clazz, int index) throws IndexOutOfBoundsException {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			// log.warn(clazz.getSimpleName() +
			// "'s superclass not ParameterizedType");
			return null;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			// log.warn("Index: " + index + ", Size of " + clazz.getSimpleName()
			// + "'s Parameterized Type: " + params.length);
			return null;
		}
		if (!(params[index] instanceof Class)) {
			// log.warn(clazz.getSimpleName() +
			// " not set the actual class on superclass generic parameter");
			return null;
		}
		return (Class) params[index];
	}

	public static Class getClassFieldGenricType(Class clazz, String field) {
		try {
			Field f = clazz.getDeclaredField(field);
			ParameterizedType pt = (ParameterizedType) f.getGenericType();
			Class gc = (Class) pt.getActualTypeArguments()[0];
			return gc;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
