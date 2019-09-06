package org.sagacity.tools.exceltoy.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author zhongxuchen
 *
 */
public class BeanUtil {
	/**
	 * 定义日志
	 */
	private final static Logger logger = LogManager.getLogger(ClassLoaderUtil.class);

	/**
	 * @todo 获取指定名称的方法集,不区分大小写
	 * @param voClass
	 * @param properties
	 * @return
	 */
	public static Method[] matchGetMethods(Class voClass, String[] props) {
		Method[] methods = voClass.getMethods();
		List<Method> realMeth = new ArrayList<Method>();
		String name;
		// 过滤get 和is 开头的方法
		for (Method mt : methods) {
			if (!void.class.equals(mt.getReturnType()) && mt.getParameterTypes().length == 0) {
				name = mt.getName().toLowerCase();
				if (name.startsWith("get") || name.startsWith("is")) {
					realMeth.add(mt);
				}
			}
		}
		int indexSize = props.length;
		Method[] result = new Method[indexSize];
		if (realMeth.isEmpty()) {
			return result;
		}
		String prop;
		Method method;
		boolean matched = false;
		Class type;
		for (int i = 0; i < indexSize; i++) {
			prop = props[i].toLowerCase();
			matched = false;
			for (int j = 0; j < realMeth.size(); j++) {
				method = realMeth.get(j);
				name = method.getName().toLowerCase();
				// get完全匹配
				if (name.equals("get".concat(prop))) {
					matched = true;
				} else if (name.startsWith("is")) {
					// boolean型 is开头的方法
					type = method.getReturnType();
					if ((type.equals(Boolean.class) || type.equals(boolean.class))
							&& (name.equals(prop) || name.equals("is".concat(prop)))) {
						matched = true;
					}
				}
				if (matched) {
					result[i] = method;
					result[i].setAccessible(true);
					realMeth.remove(j);
					break;
				}
			}
			if (realMeth.isEmpty()) {
				break;
			}
		}
		return result;
	}

	/**
	 * @todo 利用java.lang.reflect并结合页面的property， 从对象中取出对应方法的值，组成一个List
	 * @param datas
	 * @param props
	 * @return
	 */
	public static List reflectBeansToList(List datas, String[] props) throws Exception {
		return reflectBeansToList(datas, props, false, 1);
	}

	/**
	 * @todo 利用java.lang.reflect并结合页面的property， 从对象中取出对应方法的值，组成一个List
	 * @param datas
	 * @param properties
	 * @param hasSequence
	 * @param startSequence
	 * @return
	 */
	public static List reflectBeansToList(List datas, String[] properties, boolean hasSequence, int startSequence)
			throws Exception {
		if (null == datas || datas.isEmpty() || null == properties || properties.length < 1)
			return null;
		// 数据的长度
		int maxLength = Integer.toString(datas.size()).length();
		List resultList = new ArrayList();
		try {
			int methodLength = properties.length;
			Method[] realMethods = null;
			boolean inited = false;
			Object rowObject = null;
			Object[] params = new Object[] {};
			for (int i = 0, n = datas.size(); i < n; i++) {
				rowObject = datas.get(i);
				if (null != rowObject) {
					// 第一行数据
					if (!inited) {
						realMethods = matchGetMethods(rowObject.getClass(), properties);
						inited = true;
					}
					List dataList = new ArrayList();
					if (hasSequence)
						dataList.add(StringUtil.addLeftZero2Len(Long.toString(startSequence + i), maxLength));
					for (int j = 0; j < methodLength; j++) {
						if (realMethods[j] != null)
							dataList.add(realMethods[j].invoke(rowObject, params));
						else
							dataList.add(null);
					}
					resultList.add(dataList);
				} else {
					if (logger.isDebugEnabled())
						logger.debug("BeanUtil.reflectBeansToList 方法,第:{}行数据为null!", i);
					resultList.add(null);
				}
			}
		} catch (Exception e) {
			logger.error("反射Java Bean获取数据组装List集合异常!{}", e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return resultList;
	}
}
