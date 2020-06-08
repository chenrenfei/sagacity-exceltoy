package org.sagacity.tools.exceltoy.utils;

import java.io.BufferedReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
	private final static Logger logger = LogManager.getLogger(BeanUtil.class);

	/**
	 * 保存set方法
	 */
	private static ConcurrentHashMap<String, Method> setMethods = new ConcurrentHashMap<String, Method>();

	/**
	 * 保存get方法
	 */
	private static ConcurrentHashMap<String, Method> getMethods = new ConcurrentHashMap<String, Method>();

	/**
	 * <p>
	 * update 2019-09-05 优化匹配方式，修复setIsXXX的错误 update 2020-04-09
	 * 支持setXXX()并返回对象本身,适配链式操作
	 * </p>
	 * 
	 * @todo 获取指定名称的方法集
	 * @param voClass
	 * @param props
	 * @return
	 */
	public static Method[] matchSetMethods(Class voClass, String[] props) {
		int indexSize = props.length;
		Method[] result = new Method[indexSize];
		Method[] methods = voClass.getMethods();
		// 先过滤出全是set且只有一个参数的方法
		List<Method> realMeth = new ArrayList<Method>();
		for (Method mt : methods) {
			// 剔除void 判断条件
			// if (mt.getParameterTypes().length == 1 &&
			// void.class.equals(mt.getReturnType())) {
			if (mt.getParameterTypes().length == 1) {
				if (mt.getName().startsWith("set")) {
					realMeth.add(mt);
				}
			}
		}
		if (realMeth.isEmpty()) {
			return result;
		}
		Method method;
		String prop;
		boolean matched = false;
		String name;
		Class type;
		for (int i = 0; i < indexSize; i++) {
			prop = "set".concat(props[i].toLowerCase());
			matched = false;
			for (int j = 0; j < realMeth.size(); j++) {
				method = realMeth.get(j);
				name = method.getName().toLowerCase();
				// setXXX完全匹配
				if (prop.equals(name)) {
					matched = true;
				} else {
					// boolean 类型参数
					type = method.getParameterTypes()[0];
					if ((type.equals(Boolean.class) || type.equals(boolean.class)) && prop.startsWith("setis")
							&& prop.replaceFirst("setis", "set").equals(name)) {
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
	 * @todo 获取指定名称的方法集,不区分大小写
	 * @param voClass
	 * @param props
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
						if (realMethods[j] != null) {
							dataList.add(realMethods[j].invoke(rowObject, params));
						} else {
							dataList.add(null);
						}
					}
					resultList.add(dataList);
				} else {
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

	/**
	 * @todo 类型转换
	 * @param paramValue
	 * @param typeName
	 * @return
	 */
	public static Object convertType(Object paramValue, String typeName) throws Exception {
		if (paramValue == null) {
			if (typeName.equals("int") || typeName.equals("long") || typeName.equals("double")
					|| typeName.equals("float") || typeName.equals("short"))
				return 0;
			if (typeName.equals("boolean") || typeName.equals("java.lang.boolean"))
				return false;
			return null;
		}
		// 转换为小写
		typeName = typeName.toLowerCase();
		String valueStr = paramValue.toString();
		// 字符串第一优先
		if (typeName.equals("string") || typeName.equals("java.lang.string")) {
			if (paramValue instanceof java.sql.Clob) {
				java.sql.Clob clob = (java.sql.Clob) paramValue;
				return clob.getSubString((long) 1, (int) clob.length());
			}
			if (paramValue instanceof java.util.Date) {
				return DateUtil.formatDate(paramValue, "yyyy-MM-dd HH:mm:ss");
			}
			return valueStr;
		}
		// 第二优先
		if (typeName.equals("java.math.bigdecimal") || typeName.equals("decimal")) {
			return new BigDecimal(convertBoolean(valueStr));
		}
		// 第三优先
		if (typeName.equals("java.time.localdatetime")) {
			if (paramValue instanceof LocalDateTime)
				return (LocalDateTime) paramValue;
			return DateUtil.asLocalDateTime(DateUtil.parseString(valueStr));
		}
		// 第四
		if (typeName.equals("java.time.localdate")) {
			if (paramValue instanceof LocalDate)
				return (LocalDate) paramValue;
			return DateUtil.asLocalDate(DateUtil.parseString(valueStr));
		}
		// 第五
		if (typeName.equals("java.lang.integer") || typeName.equals("integer")) {
			return Integer.valueOf(convertBoolean(valueStr));
		}
		// 第六
		if (typeName.equals("java.sql.timestamp") || typeName.equals("timestamp")) {
			if (paramValue instanceof java.sql.Timestamp) {
				return (java.sql.Timestamp) paramValue;
			}
			if (paramValue instanceof java.sql.Date) {
				return new Timestamp(((java.sql.Date) paramValue).getTime());
			}
			if (paramValue instanceof java.util.Date) {
				return new Timestamp(((java.util.Date) paramValue).getTime());
			}
			return new Timestamp(DateUtil.parseString(valueStr).getTime());
		}
		if (typeName.equals("java.lang.double")) {
			return Double.valueOf(valueStr);
		}
		if (typeName.equals("java.util.date") || typeName.equals("date")) {
			if (paramValue instanceof java.sql.Date) {
				return new java.util.Date(((java.sql.Date) paramValue).getTime());
			}
			if (paramValue instanceof java.util.Date) {
				return (java.util.Date) paramValue;
			}
			if (paramValue instanceof java.sql.Timestamp) {
				return new java.util.Date(((java.sql.Timestamp) paramValue).getTime());
			}
			if (paramValue instanceof Number) {
				return new java.util.Date(((Number) paramValue).longValue());
			}
			return DateUtil.parseString(valueStr);
		}
		if (typeName.equals("java.lang.long")) {
			return Long.valueOf(convertBoolean(valueStr));
		}
		if (typeName.equals("int")) {
			return Integer.valueOf(convertBoolean(valueStr)).intValue();
		}
		if (typeName.equals("java.sql.clob") || typeName.equals("clob")) {
			java.sql.Clob clob = (java.sql.Clob) paramValue;
			BufferedReader in = new BufferedReader(clob.getCharacterStream());
			StringWriter out = new StringWriter();
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			return out.toString();
		}
		if (typeName.equals("java.time.localtime")) {
			if (paramValue instanceof LocalTime)
				return (LocalTime) paramValue;
			return DateUtil.asLocalTime(DateUtil.parseString(valueStr));
		}
		// add 2020-4-9
		if (typeName.equals("java.math.biginteger") || typeName.equals("biginteger")) {
			return new BigInteger(convertBoolean(valueStr));
		}
		if (typeName.equals("long")) {
			return Long.valueOf(convertBoolean(valueStr)).longValue();
		}
		if (typeName.equals("double")) {
			return Double.valueOf(valueStr).doubleValue();
		}
		// update by 2020-4-13增加Byte类型的处理
		if (typeName.equals("java.lang.byte")) {
			return Byte.valueOf(valueStr);
		}
		if (typeName.equals("byte")) {
			return Byte.valueOf(valueStr).byteValue();
		}
		// byte数组
		if (typeName.equals("[b")) {
			if (paramValue instanceof byte[]) {
				return (byte[]) paramValue;
			}
			// blob类型处理
			if (paramValue instanceof java.sql.Blob) {
				java.sql.Blob blob = (java.sql.Blob) paramValue;
				return blob.getBytes(1, (int) blob.length());
			}
			return valueStr.getBytes();
		}

		if (typeName.equals("java.lang.boolean") || typeName.equals("boolean")) {
			if (valueStr.equalsIgnoreCase("true") || valueStr.equals("1"))
				return Boolean.TRUE;
			return Boolean.FALSE;
		}
		if (typeName.equals("java.lang.short")) {
			return Short.valueOf(convertBoolean(valueStr));
		}
		if (typeName.equals("short")) {
			return Short.valueOf(convertBoolean(valueStr)).shortValue();
		}
		if (typeName.equals("java.lang.float")) {
			return Float.valueOf(valueStr);
		}
		if (typeName.equals("float")) {
			return Float.valueOf(valueStr).floatValue();
		}
		if (typeName.equals("java.sql.date")) {
			if (paramValue instanceof java.sql.Date) {
				return (java.sql.Date) paramValue;
			}
			if (paramValue instanceof java.util.Date) {
				return new java.sql.Date(((java.util.Date) paramValue).getTime());
			}
			if (paramValue instanceof java.sql.Timestamp) {
				return new java.sql.Date(((java.sql.Timestamp) paramValue).getTime());
			}

			return new java.sql.Date(DateUtil.parseString(valueStr).getTime());
		}
		if (typeName.equals("char")) {
			return valueStr.charAt(0);
		}
		if (typeName.equals("java.sql.time") || typeName.equals("time")) {
			if (paramValue instanceof java.sql.Time) {
				return (java.sql.Time) paramValue;
			}
			if (paramValue instanceof java.util.Date) {
				return new java.sql.Time(((java.util.Date) paramValue).getTime());
			}
			if (paramValue instanceof java.sql.Timestamp) {
				return new java.sql.Time(((java.sql.Timestamp) paramValue).getTime());
			}

			return DateUtil.parseString(valueStr);
		}
		// 字符数组
		if (typeName.equals("[c")) {
			if (paramValue instanceof char[])
				return (char[]) paramValue;
			if (paramValue instanceof java.sql.Clob) {
				java.sql.Clob clob = (java.sql.Clob) paramValue;
				BufferedReader in = new BufferedReader(clob.getCharacterStream());
				StringWriter out = new StringWriter();
				int c;
				while ((c = in.read()) != -1) {
					out.write(c);
				}
				return out.toString();
			}
			return valueStr.toCharArray();
		}
		return paramValue;
	}

	private static String convertBoolean(String var) {
		if (var.equals("true"))
			return "1";
		if (var.equals("false"))
			return "0";
		return var;
	}

	/**
	 * @TODO 代替PropertyUtil 和BeanUtils的setProperty方法
	 * @param bean
	 * @param property
	 * @param value
	 * @throws Exception
	 */
	public static void setProperty(Object bean, String property, Object value) throws Exception {
		String key = bean.getClass().getName().concat(":set").concat(property);
		// 利用缓存提升方法匹配效率
		Method method = setMethods.get(key);
		if (method == null) {
			method = matchSetMethods(bean.getClass(), new String[] { property })[0];
			setMethods.put(key, method);
		}
		// 将数据类型进行转换再赋值
		String type = method.getParameterTypes()[0].getTypeName();
		method.invoke(bean, convertType(value, type));
	}

	/**
	 * @TODO 代替BeanUtils.getProperty 方法
	 * @param bean
	 * @param property
	 * @return
	 * @throws Exception
	 */
	public static Object getProperty(Object bean, String property) throws Exception {
		String key = bean.getClass().getName().concat(":get").concat(property);
		// 利用缓存提升方法匹配效率
		Method method = getMethods.get(key);
		if (method == null) {
			method = matchGetMethods(bean.getClass(), new String[] { property })[0];
			getMethods.put(key, method);
		}
		return method.invoke(bean);
	}
}
