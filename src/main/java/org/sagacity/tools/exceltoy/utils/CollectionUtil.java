/**
 * @Copyright 2008 版权归陈仁飞，不要肆意侵权抄袭，如引用请注明出处保留作者信息。
 */
package org.sagacity.tools.exceltoy.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagacity.tools.exceltoy.ExcelToyConstants;

/**
 * @project sagacity-core
 * @description 数组集合的公用方法
 * @author zhongxuchen <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:CollectionUtil.java,Revision:v1.0,Date:2008-10-22 上午10:57:00
 * @Modification Date:2011-8-11 {修复了pivotList设置旋转数据的初始值错误}
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CollectionUtil {
	/**
	 * 定义日志
	 */
	private final static Logger logger = LogManager.getLogger(CollectionUtil.class);

	/**
	 * @todo 转换数组类型数据为对象数组,解决原始类型无法强制转换的问题
	 * @param obj
	 * @return
	 */
	public static Object[] convertArray(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Object[])
			return (Object[]) obj;
		if (obj instanceof Collection)
			return ((Collection) obj).toArray();
		// 原始数组类型判断,原始类型直接(Object[])强制转换会发生错误
		if (obj instanceof int[]) {
			int[] tmp = (int[]) obj;
			Integer[] result = new Integer[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				result[i] = tmp[i];
			}
			return result;
		}
		if (obj instanceof short[]) {
			short[] tmp = (short[]) obj;
			Short[] result = new Short[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				result[i] = tmp[i];
			}
			return result;
		}
		if (obj instanceof long[]) {
			long[] tmp = (long[]) obj;
			Long[] result = new Long[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				result[i] = tmp[i];
			}
			return result;
		}
		if (obj instanceof float[]) {
			float[] tmp = (float[]) obj;
			Float[] result = new Float[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				result[i] = tmp[i];
			}
			return result;
		}
		if (obj instanceof double[]) {
			double[] tmp = (double[]) obj;
			Double[] result = new Double[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				result[i] = tmp[i];
			}
			return result;
		}
		if (obj instanceof boolean[]) {
			boolean[] tmp = (boolean[]) obj;
			Boolean[] result = new Boolean[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				result[i] = tmp[i];
			}
			return result;
		}
		if (obj instanceof char[]) {
			char[] tmp = (char[]) obj;
			String[] result = new String[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				result[i] = String.valueOf(tmp[i]);
			}
			return result;
		}
		if (obj instanceof byte[]) {
			byte[] tmp = (byte[]) obj;
			Byte[] result = new Byte[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				result[i] = tmp[i];
			}
			return result;
		}
		return new Object[] { obj };
	}

	/**
	 * @todo 数组转换为List集合,此转换只适用于一维和二维数组
	 * @param arySource Object
	 * @return List
	 */
	public static List arrayToDeepList(Object arySource) {
		if (null == arySource) {
			System.err.println("arrayToDeepList:the Ary Source is Null");
			return null;
		}
		List resultList = new ArrayList();
		if (arySource instanceof Object[][]) {
			Object[][] aryObject = (Object[][]) arySource;
			if (null != aryObject && 0 < aryObject.length) {
				int rowLength;
				for (int i = 0, n = aryObject.length; i < n; i++) {
					List tmpList = new ArrayList();
					rowLength = aryObject[i].length;
					for (int j = 0; j < rowLength; j++) {
						tmpList.add(aryObject[i][j]);
					}
					resultList.add(tmpList);
				}
			}
		} else {
			if (arySource.getClass().isArray()) {
				Object[] aryObject = convertArray(arySource);
				if (null != aryObject && 0 < aryObject.length) {
					for (int i = 0, n = aryObject.length; i < n; i++) {
						resultList.add(aryObject[i]);
					}
				}
			} else {
				logger.error("error define the Array! please sure the array is one or two dimension!");
			}
		}
		return resultList;
	}

	/**
	 * @todo 此转换只适用于一维数组(建议使用Arrays.asList())
	 * @param arySource Object
	 * @return List
	 */
	@Deprecated
	public static List arrayToList(Object arySource) {
		if (null == arySource) {
			System.err.println("arrayToList:the Ary Source is Null");
			return null;
		}
		if (arySource instanceof List)
			return (List) arySource;
		List resultList = new ArrayList();
		if (arySource.getClass().isArray()) {
			Object[] aryObject = convertArray(arySource);
			// return Arrays.asList(aryObject);
			if (null != aryObject && 0 < aryObject.length) {
				for (int i = 0, n = aryObject.length; i < n; i++) {
					resultList.add(aryObject[i]);
				}
			}
		} else {
			logger.warn("arySource is not Array! it type is :" + arySource.getClass());
			resultList.add(arySource);
		}
		return resultList;
	}

	/**
	 * @todo 剔除对象数组中的部分数据,简单采用List remove方式实现
	 * @param targetAry
	 * @param begin
	 * @param length
	 * @return
	 */
	public static Object[] subtractArray(Object[] sourceAry, int begin, int length) {
		if (sourceAry == null || sourceAry.length == 0)
			return null;
		if (begin + length > sourceAry.length || length == 0)
			return sourceAry;
		Object[] distinctAry = new Object[sourceAry.length - length];
		if (begin == 0) {
			System.arraycopy(sourceAry, length, distinctAry, 0, sourceAry.length - length);
		} else {
			System.arraycopy(sourceAry, 0, distinctAry, 0, begin);
			System.arraycopy(sourceAry, begin + length, distinctAry, begin, sourceAry.length - length - begin);
		}
		return distinctAry;
	}

	/**
	 * @todo 二维list转换为数组对象
	 * @param source
	 * @return
	 */
	public static Object[][] twoDimenlistToArray(Collection source) {
		if (source == null || source.isEmpty())
			return null;
		Object[][] result = new Object[source.size()][];
		int index = 0;
		Object obj;
		for (Iterator iter = source.iterator(); iter.hasNext();) {
			obj = iter.next();
			if (obj instanceof Collection) {
				result[index] = ((Collection) obj).toArray();
			} else if (obj.getClass().isArray()) {
				result[index] = convertArray(obj);
			} else if (obj instanceof Map) {
				result[index] = ((Map) obj).values().toArray();
			}
			index++;
		}
		return result;
	}

	/**
	 * @todo 判断list的维度
	 * @param obj
	 * @return
	 */
	public static int judgeObjectDimen(Object obj) {
		int result = 0;
		if (obj == null)
			return -1;

		if (obj instanceof Collection || obj.getClass().isArray() || obj instanceof Map) {
			result = 1;
			if (obj instanceof Collection) {
				Collection tmp = (Collection) obj;
				if (tmp.isEmpty())
					return result;
				if (((List) obj).get(0) != null && ((List) obj).get(0) instanceof List) {
					result = 2;
				}
			} else if (obj.getClass().isArray()) {
				Object[] tmp = convertArray(obj);
				if (tmp.length == 0)
					return result;
				if (tmp[0] != null && tmp[0].getClass().isArray()) {
					result = 2;
				}
			} else if (obj instanceof Map) {
				Map tmp = (Map) obj;
				if (tmp.isEmpty())
					return result;
				Object setItem = tmp.values().iterator().next();
				if (setItem.getClass().isArray() || setItem instanceof Collection || setItem instanceof Map) {
					result = 2;
				}
			}
		}
		return result;
	}

	/**
	 * @todo 从数组中获取值对照
	 * @param obj     key对像
	 * @param keyMaps 对照数组
	 * @return
	 */
	public static Object getKeyValue(Object obj, Object[][] keyMaps) {
		return getKeyValue(obj, keyMaps, null);
	}

	/**
	 * @todo 从数组中获取值对照
	 * @param obj          key对像
	 * @param keyMaps      对照数组
	 * @param defaultValue 默认值
	 * @return
	 */
	public static Object getKeyValue(Object obj, Object[][] keyMaps, Object defaultValue) {
		if (obj == null)
			return defaultValue;
		for (int i = 0; i < keyMaps.length; i++) {
			if (keyMaps[i][0] instanceof String) {
				if (obj.toString().equals(keyMaps[i][0].toString())) {
					return keyMaps[i][1];
				}
			} else if (obj.equals(keyMaps[i][0])) {
				return keyMaps[i][1];
			}
		}
		return null;
	}

	/**
	 * @todo 删除为null的数组对象
	 * @param obj
	 * @return
	 */
	public static Object removeNull(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Collection) {
			Collection tmp = (Collection) obj;
			Iterator iter = tmp.iterator();
			while (iter.hasNext()) {
				if (iter.next() == null) {
					iter.remove();
				}
			}
		} else if (obj.getClass().isArray()) {
			Object[] tmp = convertArray(obj);
			List tmpList = new ArrayList();
			for (int i = 0, n = tmp.length; i < n; i++) {
				if (tmp[i] != null) {
					tmpList.add(tmp[i]);
				}
			}
			return tmpList.toArray();
		} else if (obj instanceof Map) {
			Map tmp = (Map) obj;
			Iterator iter = tmp.entrySet().iterator();
			Map.Entry entry;
			while (iter.hasNext()) {
				entry = (Map.Entry) iter.next();
				if (entry.getValue() == null) {
					tmp.remove(entry.getKey());
				}
			}
		}
		return obj;
	}

	/**
	 * @todo 将集合数据转成hashMap
	 * @param data
	 * @param keyProp
	 * @param valueProp
	 * @param keyToStr  将key统一转成字符串
	 * @return
	 */
	public static HashMap hashList(Object data, Object keyProp, Object valueProp, boolean keyToStr) {
		return hashList(data, keyProp, valueProp, keyToStr, false);
	}

	/**
	 * @todo 将集合数据转成hashMap
	 * @param data
	 * @param keyProp
	 * @param valueProp
	 * @param keyToStr     将key统一转成字符串
	 * @param isLinkedHash 返回的是否为LinkedHashMap
	 * @return
	 */
	public static HashMap hashList(Object data, Object keyProp, Object valueProp, boolean keyToStr,
			boolean isLinkedHash) {
		int dimen = judgeObjectDimen(data);
		int keyIndex = -1;
		int valueIndex = -1;
		keyIndex = Integer.parseInt(keyProp.toString());
		valueIndex = (valueProp == null) ? -1 : Integer.parseInt(valueProp.toString());
		HashMap result = isLinkedHash ? new LinkedHashMap() : new HashMap();
		try {
			switch (dimen) {
			case -1:
				break;
			case 0:
				break;
			// 一维
			case 1: {
				if (data.getClass().isArray()) {
					Object[] hashObj = convertArray(data);
					List rowData;
					for (int i = 0, n = hashObj.length; i < n; i++) {
						rowData = (List) hashObj[i];
						result.put(keyToStr ? rowData.get(keyIndex).toString() : rowData.get(keyIndex),
								valueIndex == -1 ? hashObj[i] : rowData.get(valueIndex));
					}
				} else if (data instanceof List) {
					List hashObj = (List) data;
					Object[] rowData;
					for (int i = 0, n = hashObj.size(); i < n; i++) {
						rowData = convertArray(hashObj.get(i));
						result.put(keyToStr ? rowData[keyIndex].toString() : rowData[keyIndex],
								valueIndex == -1 ? hashObj.get(i) : rowData[valueIndex]);
					}
				}
				break;
			} // 2维
			case 2: {
				if (data.getClass().isArray()) {
					Object[] hashObj = convertArray(data);
					Object[] rowData;
					for (int i = 0, n = hashObj.length; i < n; i++) {
						rowData = convertArray(hashObj[i]);
						result.put(keyToStr ? rowData[keyIndex].toString() : rowData[keyIndex],
								valueIndex == -1 ? hashObj[i] : rowData[valueIndex]);
					}
				} else if (data instanceof List) {
					List hashObj = (List) data;
					List rowData;
					for (int i = 0, n = hashObj.size(); i < n; i++) {
						rowData = (List) hashObj.get(i);
						result.put(keyToStr ? rowData.get(keyIndex).toString() : rowData.get(keyIndex),
								valueIndex == -1 ? hashObj.get(i) : rowData.get(valueIndex));
					}
				}
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @todo 将内部的数组转换为list
	 * @param source
	 */
	public static void innerArrayToList(List source) {
		if (source == null || source.isEmpty())
			return;
		if (source.get(0).getClass().isArray()) {
			Object[] rowAry;
			for (int i = 0, n = source.size(); i < n; i++) {
				List rowList = new ArrayList();
				rowAry = convertArray(source.get(i));
				for (int j = 0, k = rowAry.length; j < k; j++) {
					rowList.add(rowAry[j]);
				}
				source.remove(i);
				source.add(i, rowList);
			}
		}
	}

	/**
	 * @todo 将内部list转换为数组
	 * @param source
	 * @return
	 */
	public static List innerListToArray(List source) {
		if (source == null || source.isEmpty())
			return source;
		List result = new ArrayList();
		Object sonList;
		for (int i = 0, n = source.size(); i < n; i++) {
			sonList = source.get(i);
			if (null == sonList) {
				result.add(null);
			} else if (sonList instanceof Collection) {
				result.add(((Collection) sonList).toArray());
			} else if (sonList.getClass().isArray()) {
				result.add(sonList);
			} else {
				System.err.println("数据类型必须为Collection");
				break;
			}
		}
		return result;
	}

	/**
	 * @todo 依据主键将内容合并
	 * @param excelData
	 * @param mainPK
	 * @param pkCols
	 */
	public static List merge(List<List> excelData, String mainPK, String[] pkCols) {
		if (excelData == null || excelData.isEmpty())
			return excelData;
		if (StringUtil.isBlank(mainPK) || pkCols == null || pkCols.length == 0)
			return excelData;
		LinkedHashMap<String, List> resultMap = new LinkedHashMap<String, List>();
		String pkValue;
		int size;
		for (List row : excelData) {
			pkValue = replaceHolder(mainPK, pkCols, row);
			// 已经存在,值进行merge处理
			if (resultMap.containsKey(pkValue)) {
				List preRow = resultMap.get(pkValue);
				size = preRow.size();
				if (row.size() <= size) {
					size = row.size();
					for (int i = 0; i < size; i++) {
						if (StringUtil.isNotBlank(row.get(i)) && StringUtil.isBlank(preRow.get(i))) {
							preRow.set(i, row.get(i));
						}
					}
				} else {
					// 颠倒一下,将数据长的作为主体
					for (int i = 0; i < size; i++) {
						if (StringUtil.isNotBlank(preRow.get(i)) && StringUtil.isBlank(row.get(i))) {
							row.set(i, preRow.get(i));
						}
					}
					resultMap.put(pkValue, row);
				}
			} else {
				resultMap.put(pkValue, row);
			}
		}
		List result = new ArrayList();
		Iterator iter = resultMap.values().iterator();
		while (iter.hasNext()) {
			result.add(iter.next());
		}
		return result;
	}

	/**
	 * @todo 以excel数据替换excel标题占位符号
	 * @param template
	 * @param pkCols
	 * @param rowData
	 * @return
	 */
	public static String replaceHolder(String template, String[] pkCols, List rowData) {
		if (pkCols == null || pkCols.length == 0)
			return template;
		String result = template;
		int index;
		String replace;
		for (int i = 0; i < pkCols.length; i++) {
			index = EQLUtil.getExcelFieldMapIndex(pkCols[i]);
			// 换用StringUtil替换解决result.replaceAll("\\$\\{\\}","$"),替换内容中存在"$"符合报错问题
			if (index != -1) {
				if (rowData.size() < index + 1 || rowData.get(index) == null) {
					replace = "";
				} else {
					replace = rowData.get(index).toString();
				}
				result = StringUtil.replaceAllStr(result, ExcelToyConstants.EXCEL_TITLE_BEGIN_MARK.concat(pkCols[i])
						.concat(ExcelToyConstants.EXCEL_TITLE_END_MARK), replace);
			}
		}
		return result;
	}
}
