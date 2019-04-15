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
	public static Method[] matchGetMethods(Class voClass, String[] properties) {
		int indexSize = properties.length;
		Method[] methods = voClass.getMethods();
		Method[] result = new Method[indexSize];
		String methodName;
		int methodCnt = methods.length;
		String property;
		Method method;
		for (int i = 0; i < indexSize; i++) {
			property = properties[i].toLowerCase();
			for (int j = 0; j < methodCnt; j++) {
				method = methods[j];
				methodName = method.getName().toLowerCase();
				// update 2012-10-25 from equals to ignoreCase
				if (!void.class.equals(method.getReturnType()) && method.getParameterTypes().length == 0
						&& (methodName.equals("get".concat(property)) || methodName.equals("is".concat(property))
								|| (methodName.startsWith("is") && methodName.equals(property)))) {
					result[i] = method;
					result[i].setAccessible(true);
					break;
				}
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
