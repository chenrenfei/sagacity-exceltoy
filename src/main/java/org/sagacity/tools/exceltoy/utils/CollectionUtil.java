/**
 * @Copyright 2008 版权归陈仁飞，不要肆意侵权抄袭，如引用请注明出处保留作者信息。
 */
package org.sagacity.tools.exceltoy.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.utils.callback.IdentityCallbackHandler;
import org.sagacity.tools.exceltoy.utils.callback.TreeRemoveCallbackHandler;
import org.sagacity.tools.exceltoy.utils.callback.TreeSetCallbackHandler;

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
			for (int i = 0; i < tmp.length; i++)
				result[i] = tmp[i];
			return result;
		} else if (obj instanceof short[]) {
			short[] tmp = (short[]) obj;
			Short[] result = new Short[tmp.length];
			for (int i = 0; i < tmp.length; i++)
				result[i] = tmp[i];
			return result;
		} else if (obj instanceof long[]) {
			long[] tmp = (long[]) obj;
			Long[] result = new Long[tmp.length];
			for (int i = 0; i < tmp.length; i++)
				result[i] = tmp[i];
			return result;
		} else if (obj instanceof float[]) {
			float[] tmp = (float[]) obj;
			Float[] result = new Float[tmp.length];
			for (int i = 0; i < tmp.length; i++)
				result[i] = tmp[i];
			return result;
		} else if (obj instanceof double[]) {
			double[] tmp = (double[]) obj;
			Double[] result = new Double[tmp.length];
			for (int i = 0; i < tmp.length; i++)
				result[i] = tmp[i];
			return result;
		} else if (obj instanceof boolean[]) {
			boolean[] tmp = (boolean[]) obj;
			Boolean[] result = new Boolean[tmp.length];
			for (int i = 0; i < tmp.length; i++)
				result[i] = tmp[i];
			return result;
		} else if (obj instanceof char[]) {
			char[] tmp = (char[]) obj;
			String[] result = new String[tmp.length];
			for (int i = 0; i < tmp.length; i++)
				result[i] = String.valueOf(tmp[i]);
			return result;
		} else if (obj instanceof byte[]) {
			byte[] tmp = (byte[]) obj;
			Byte[] result = new Byte[tmp.length];
			for (int i = 0; i < tmp.length; i++)
				result[i] = tmp[i];
			return result;
		}
		return new Object[] { obj };
	}

	/**
	 * @todo 数组转换为List集合,此转换只适用于一维和二维数组
	 * @param arySource
	 *            Object
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
					for (int i = 0, n = aryObject.length; i < n; i++)
						resultList.add(aryObject[i]);
				}
			} else {
				logger.error("error define the Array! please sure the array is one or two dimension!");
			}
		}
		return resultList;
	}

	/**
	 * @todo 此转换只适用于一维数组(建议使用Arrays.asList())
	 * @param arySource
	 *            Object
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
				for (int i = 0, n = aryObject.length; i < n; i++)
					resultList.add(aryObject[i]);
			}
		} else {
			logger.warn("arySource is not Array! it type is :" + arySource.getClass());
			resultList.add(arySource);
		}
		return resultList;
	}

	/**
	 * 此方法不建议使用，请用Collections中的排序
	 * 
	 * @todo 对简单对象进行排序
	 * @param aryData
	 * @param descend
	 */
	public static void sortArray(Object[] aryData, boolean descend) {
		if (aryData != null && aryData.length > 1) {
			int length = aryData.length;
			Object iData;
			Object jData;
			// 1:string,2:数字;3:日期
			Integer dataType = 1;
			if (aryData[0] instanceof java.util.Date)
				dataType = 3;
			else if (aryData[0] instanceof java.lang.Number)
				dataType = 2;
			boolean lessThen = false;
			for (int i = 0; i < length - 1; i++) {
				for (int j = i + 1; j < length; j++) {
					iData = aryData[i];
					jData = aryData[j];
					if (dataType == 2) {
						lessThen = ((Number) iData).doubleValue() < ((Number) jData).doubleValue();
					} else if (dataType == 3) {
						lessThen = ((Date) iData).before((Date) jData);
					} else
						lessThen = (iData.toString()).compareTo(jData.toString()) < 0;
					// 小于
					if ((descend && lessThen) || (!descend && !lessThen)) {
						aryData[i] = jData;
						aryData[j] = iData;
					}
				}
			}
		}
	}

	/**
	 * @deprecated 二维数组排序,写的比较早，建议使用Collections实现排序
	 * @param sourceList
	 * @param isDescend
	 * @param sortRow
	 */
	public static void sortTwoDimenList(List sourceList, String isDescend, int sortRow) {
		boolean descend = false;
		if (sourceList != null && sourceList.size() > 0 && (sourceList.get(0) instanceof ArrayList)
				&& ((List) sourceList.get(0)).size() > sortRow) {
			if (isDescend.equalsIgnoreCase("true"))
				descend = true;
			int size = sourceList.size();
			// 1:string,2:数字;3:日期
			Integer dataType = 1;
			Object sortColumnValue = ((List) sourceList.get(0)).get(sortRow);
			if (sortColumnValue instanceof java.util.Date)
				dataType = 3;
			else if (sortColumnValue instanceof java.lang.Number)
				dataType = 2;
			List iList;
			List jList;
			Object iData;
			Object jData;
			boolean lessThen = false;
			for (int i = 0; i < size - 1; i++) {
				for (int j = i + 1; j < size; j++) {
					iList = (List) sourceList.get(i);
					jList = (List) sourceList.get(j);
					iData = iList.get(sortRow);
					jData = jList.get(sortRow);
					if (dataType == 2) {
						lessThen = ((Number) iData).doubleValue() < ((Number) jData).doubleValue();
					} else if (dataType == 3) {
						lessThen = ((Date) iData).before((Date) jData);
					} else
						lessThen = (iData.toString()).compareTo(jData.toString()) < 0;
					// 小于
					if ((descend && lessThen) || (!descend && !lessThen)) {
						sourceList.set(i, jList);
						sourceList.set(j, iList);
					}
				}
			}
		} else {
			logger.error("对二维数组排序条件不符合");
		}
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
		if (begin == 0)
			System.arraycopy(sourceAry, length, distinctAry, 0, sourceAry.length - length);
		else {
			System.arraycopy(sourceAry, 0, distinctAry, 0, begin);
			System.arraycopy(sourceAry, begin + length, distinctAry, begin, sourceAry.length - length - begin);
		}
		return distinctAry;
	}

	public static Object[] copyArray(Object[] sourceAry, int begin, int length) {
		if (sourceAry == null || sourceAry.length == 0)
			return null;
		Object[] distinctAry = new Object[length];
		System.arraycopy(sourceAry, begin, distinctAry, 0, length);
		return distinctAry;
	}

	/**
	 * 字符串数组按照类型转换
	 * 
	 * @param values
	 * @param argType
	 * @return
	 */
	public static Object[] toArray(String[] values, String argType) {
		if (values == null)
			return null;
		String type = argType.toLowerCase();
		Object[] result = null;
		if (type.equals("string"))
			result = new String[values.length];
		else if (type.equals("int") || type.equals("integer"))
			result = new Integer[values.length];
		else if (type.equals("long"))
			result = new Long[values.length];
		else if (type.equals("date"))
			result = new Date[values.length];
		else if (type.equals("boolean"))
			result = new Boolean[values.length];
		else if (type.equals("double"))
			result = new Double[values.length];
		else if (type.equals("float"))
			result = new Float[values.length];
		else if (type.equals("short"))
			result = new Short[values.length];
		else if (type.equals("java.lang.class") || type.equals("class"))
			result = new Class[values.length];
		for (int i = 0; i < result.length; i++) {
			if (values[i] != null) {
				if (type.equals("string")) {
					result[i] = values[i];
				} else if (type.equals("int") || type.equals("integer")) {
					result[i] = Integer.parseInt(values[i]);
				} else if (type.equals("long")) {
					result[i] = new Long(values[i]);
				} else if (type.equals("date")) {
					result[i] = DateUtil.parseString(values[i]);
				} else if (type.equals("boolean")) {
					result[i] = Boolean.parseBoolean(values[i]);
				} else if (type.equals("double")) {
					result[i] = Double.parseDouble(values[i]);
				} else if (type.equals("float")) {
					result[i] = Float.parseFloat(values[i]);
				} else if (type.equals("short")) {
					result[i] = Short.parseShort(values[i]);
				} else if (type.equals("java.lang.class") || type.equals("class")) {
					try {
						result[i] = Class.forName(values[i]);
					} catch (ClassNotFoundException e) {
					}
				}
			}
		}
		return result;
	}

	/**
	 * @todo 获取对象在数组中的位置
	 * @param obj
	 *            Object
	 * @param source
	 *            Object[]
	 * @return int
	 */
	public static int indexOfArray(Object obj, Object[] source) {
		int index = -1;
		if (null != source && source.length > 0) {
			for (int i = 0, n = source.length; i < n; i++) {
				if (equals(obj, source[i])) {
					index = i;
					break;
				}
			}
		}
		return index;
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
			if (obj instanceof Collection)
				result[index] = ((Collection) obj).toArray();
			else if (obj.getClass().isArray())
				result[index] = convertArray(obj);
			else if (obj instanceof Map)
				result[index] = ((Map) obj).values().toArray();
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
				if (((List) obj).get(0) != null && ((List) obj).get(0) instanceof List)
					result = 2;
			} else if (obj.getClass().isArray()) {
				Object[] tmp = convertArray(obj);
				if (tmp.length == 0)
					return result;
				if (tmp[0] != null && tmp[0].getClass().isArray())
					result = 2;
			} else if (obj instanceof Map) {
				Map tmp = (Map) obj;
				if (tmp.isEmpty())
					return result;
				Object setItem = tmp.values().iterator().next();
				if (setItem.getClass().isArray() || setItem instanceof Collection || setItem instanceof Map)
					result = 2;
			}
		}
		return result;
	}

	/**
	 * @todo 从数组中获取值对照
	 * @param obj
	 *            key对像
	 * @param keyMaps
	 *            对照数组
	 * @return
	 */
	public static Object getKeyValue(Object obj, Object[][] keyMaps) {
		return getKeyValue(obj, keyMaps, null);
	}

	/**
	 * @todo 从数组中获取值对照
	 * @param obj
	 *            key对像
	 * @param keyMaps
	 *            对照数组
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	public static Object getKeyValue(Object obj, Object[][] keyMaps, Object defaultValue) {
		if (obj == null)
			return defaultValue;
		for (int i = 0; i < keyMaps.length; i++) {
			if (keyMaps[i][0] instanceof String) {
				if (obj.toString().equals(keyMaps[i][0].toString()))
					return keyMaps[i][1];
			} else if (obj.equals(keyMaps[i][0]))
				return keyMaps[i][1];
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
				if (iter.next() == null)
					iter.remove();
			}
		} else if (obj.getClass().isArray()) {
			Object[] tmp = convertArray(obj);
			List tmpList = new ArrayList();
			for (int i = 0, n = tmp.length; i < n; i++) {
				if (tmp[i] != null)
					tmpList.add(tmp[i]);
			}
			return tmpList.toArray();
		} else if (obj instanceof Map) {
			Map tmp = (Map) obj;
			Iterator iter = tmp.entrySet().iterator();
			Map.Entry entry;
			while (iter.hasNext()) {
				entry = (Map.Entry) iter.next();
				if (entry.getValue() == null)
					tmp.remove(entry.getKey());
			}
		}
		return obj;
	}

	/**
	 * @todo 排除集合中属性值等于给定值的子集合
	 * @param params
	 * @param propName
	 * @param propValue
	 * @return
	 */
	public static List copyExcludeSubList(List params, int paramIndex, Object propValue) {
		if (params == null || params.isEmpty())
			return params;
		List result = new ArrayList();
		Object rowData;
		Object value;
		for (int i = 0, n = params.size(); i < n; i++) {
			rowData = params.get(i);
			if (rowData != null) {
				value = rowData.getClass().isArray() ? convertArray(rowData)[paramIndex]
						: ((List) rowData).get(paramIndex);
				// 不相等
				if ((value == null && propValue != null) || (value != null && !value.equals(propValue))) {
					result.add(rowData);
				}
			}
		}
		return result;
	}

	/**
	 * @todo 处理树形数据，将子节点紧靠父节点排序
	 * @param treeList
	 * @param rootPidValue
	 * @param treeCallHandler
	 *            获取pid 值
	 * @return
	 */
	public static List sortTreeList(List treeList, Object rootPidValue, TreeSetCallbackHandler treeCallHandler) {
		if (treeList == null || treeList.isEmpty() || rootPidValue == null)
			return null;
		List result;
		// 支持多根节点
		if (rootPidValue instanceof List)
			result = (List) rootPidValue;
		else if (rootPidValue.getClass().isArray()) {
			result = arrayToList(rootPidValue);
		} else
			result = new ArrayList();

		int beginIndex = (result.isEmpty()) ? -1 : 0;
		int addCount = 1;
		Object compareId;
		int totalRecord = treeList.size();
		while (treeList.size() != 0) {
			addCount = 1;
			compareId = (beginIndex != -1) ? treeCallHandler.getIdPidSet(result.get(beginIndex))[0] : rootPidValue;
			for (int i = 0; i < treeList.size(); i++) {
				if (equalsIgnoreType(treeCallHandler.getIdPidSet(treeList.get(i))[1], compareId, false)) {
					result.add(beginIndex + addCount, treeList.get(i));
					treeList.remove(i);
					addCount++;
					i--;
				}
			}
			// 下一个
			beginIndex++;
			// 防止因数据不符合规则造成的死循环
			if (beginIndex + 1 > result.size())
				break;
		}
		if (result.size() != totalRecord)
			System.err.println("sortTree操作发现部分数据不符合树形结构规则,请检查!");
		return result;
	}

	/**
	 * @todo 树排序(内部数据位数组或List集合)
	 * @param treeList
	 * @param rootPidValue
	 * @param idColumn
	 * @param pidColumn
	 * @return
	 */
	public static List sortTreeList(List treeList, Object rootPidValue, int idColumn, int pidColumn) {
		if (treeList == null || treeList.isEmpty() || rootPidValue == null)
			return null;
		List result;
		// 支持多根节点
		if (rootPidValue instanceof List)
			result = (List) rootPidValue;
		else if (rootPidValue.getClass().isArray()) {
			result = arrayToList(rootPidValue);
		} else
			result = new ArrayList();
		int beginIndex = (result.isEmpty()) ? -1 : 0;
		int addCount = 1;
		int totalRecord = treeList.size();
		// 是否是数组
		boolean isArray = treeList.get(0).getClass().isArray();
		Object rowSet;
		Object idValue;
		Object pidValue;
		while (treeList.size() != 0) {
			addCount = 1;
			if (beginIndex != -1) {
				if (isArray)
					idValue = ((Object[]) result.get(beginIndex))[idColumn];
				else
					idValue = ((List) result.get(beginIndex)).get(idColumn);
			} else
				idValue = rootPidValue;
			for (int i = 0; i < treeList.size(); i++) {
				rowSet = treeList.get(i);
				if (isArray)
					pidValue = ((Object[]) rowSet)[pidColumn];
				else
					pidValue = ((List) rowSet).get(pidColumn);
				if (equalsIgnoreType(pidValue, idValue, false)) {
					result.add(beginIndex + addCount, rowSet);
					treeList.remove(i);
					addCount++;
					i--;
				}
			}
			// 下一个
			beginIndex++;
			// 防止因数据不符合规则造成的死循环
			if (beginIndex + 1 > result.size())
				break;
		}
		if (result.size() != totalRecord)
			System.err.println("sortTree操作发现部分数据不符合树形结构规则,请检查!");
		return result;
	}

	/**
	 * @todo 数据进行旋转
	 * @param data
	 * @param categorys
	 * @param categCol
	 * @param pkColumn
	 * @param categCompareCol
	 * @param startCol
	 * @param endCol
	 * @param defaultValue
	 * @return
	 * @throws Exception
	 */
	public static List pivotList(List data, List categorys, int categCol, int pkColumn, int categCompareCol,
			int startCol, int endCol, Object defaultValue) throws Exception {
		return pivotList(data, categorys, new Integer[] { categCol }, new Integer[] { pkColumn },
				new Integer[] { categCompareCol }, startCol, endCol, defaultValue);
	}

	/**
	 * @todo 集合进行数据旋转
	 * @Modification $Date:2011-8-11 修改了设置初始值的bug
	 * @param data
	 * @param categorys
	 * @param categCol
	 * @param pkColumns
	 * @param categCompareCol
	 * @param startCol
	 * @param endCol
	 * @param defaultValue
	 * @return
	 * @throws Exception
	 */
	public static List pivotList(List data, List categorys, Integer[] categoryCol, Integer[] pkColumns,
			Integer[] categCompareCol, int startCol, int endCol, Object defaultValue) throws Exception {
		if (data == null || data.isEmpty())
			return data;
		Integer[] categCol;
		if (categoryCol == null) {
			categCol = new Integer[categCompareCol.length];
			for (int i = 0; i < categCompareCol.length; i++)
				categCol[i] = i;
		} else
			categCol = categoryCol;
		boolean isTwoDimensionCategory = (categorys.get(0) instanceof Collection
				|| categorys.get(0).getClass().isArray());
		// 多维旋转参照数据行数跟参照列的数量要一致
		if (isTwoDimensionCategory
				&& (categCompareCol.length > categorys.size() || categCompareCol.length != categCol.length)) {
			throw new IllegalArgumentException("多维旋转参照数据行数跟参照列的数量要一致,categCol.length == categCompareCol.length!");
		}
		List result = new ArrayList();
		// 数据宽度
		int dataWidth = ((List) data.get(0)).size();
		int cateItemSize = isTwoDimensionCategory ? ((Collection) categorys.get(0)).size() : categorys.size();
		int rotateWith = endCol - startCol + 1;
		int lastRowWidth = dataWidth - categCompareCol.length + (cateItemSize - 1) * rotateWith;
		int rotateTotalCount = cateItemSize * rotateWith;
		int count = 0;
		boolean isRotaCol = false;
		Object[] rowData = null;
		int indexLength = pkColumns.length;
		boolean categoryColEqual = false;
		int categColSize = categCompareCol.length;
		// 主键列是否相等
		boolean pkColumnsEqual = false;
		List compareRow = null;
		List rowList;
		int rowSize = data.size();
		Object pkColValue;
		Object compareValue;
		for (int i = 0; i < rowSize; i++) {
			rowList = (List) data.get(i);
			pkColumnsEqual = true;
			if (i == 0)
				pkColumnsEqual = false;
			else {
				for (int k = 0; k < indexLength; k++) {
					pkColValue = rowList.get(pkColumns[k]);
					if (pkColValue == null)
						pkColValue = "null";
					compareValue = compareRow.get(pkColumns[k]);
					if (compareValue == null)
						compareValue = "null";
					pkColumnsEqual = pkColumnsEqual && equalsIgnoreType(pkColValue, compareValue, false);
					if (!pkColumnsEqual)
						break;
				}
			}

			// 不同指标，构建新的行数据
			if (!pkColumnsEqual) {
				compareRow = rowList;
				if (i != 0)
					result.add(rowData);
				rowData = new Object[lastRowWidth];
				// 设置旋转部分的数据的默认值
				if (defaultValue != null) {
					for (int j = 0; j < rotateTotalCount; j++)
						rowData[dataWidth - rotateWith - categCompareCol.length + j] = defaultValue;
				}
				count = 0;
				for (int k = 0; k < dataWidth; k++) {
					isRotaCol = false;
					for (int m = 0; m < categColSize; m++) {
						if (k == categCompareCol[m]) {
							isRotaCol = true;
							break;
						}
					}
					if (k >= startCol && k <= endCol)
						isRotaCol = true;
					if (!isRotaCol) {
						rowData[count] = rowList.get(k);
						count++;
					}
				}
			}
			for (int j = 0; j < cateItemSize; j++) {
				// 单个数据
				if (categColSize == 1) {
					pkColValue = rowList.get(categCompareCol[0]);
					if (pkColValue == null)
						pkColValue = "null";
					compareValue = isTwoDimensionCategory ? ((List) categorys.get(categCol[0])).get(j)
							: categorys.get(j);
					if (compareValue == null)
						compareValue = "null";
					if (equalsIgnoreType(pkColValue, compareValue, false)) {
						for (int t = 0; t < rotateWith; t++)
							rowData[count + j * rotateWith + t] = rowList.get(startCol + t);
					}
				} else {
					// 判断category 对应的几列值必须都一样
					categoryColEqual = true;
					for (int k = 0; k < categColSize; k++) {
						pkColValue = rowList.get(categCompareCol[k]);
						if (pkColValue == null)
							pkColValue = "null";
						compareValue = isTwoDimensionCategory ? ((List) categorys.get(categCol[k])).get(j)
								: categorys.get(j);
						if (compareValue == null)
							compareValue = "null";
						categoryColEqual = categoryColEqual && equalsIgnoreType(pkColValue, compareValue, false);
					}
					if (categoryColEqual) {
						for (int t = 0; t < rotateWith; t++)
							rowData[count + j * rotateWith + t] = rowList.get(startCol + t);
					}
				}
			}
			// 最后一行
			if (i == rowSize - 1)
				result.add(rowData);
		}
		innerArrayToList(result);
		return result;
	}

	/**
	 * @todo 连接集合
	 * @param source
	 * @param linkList
	 * @return
	 */
	public static List linkList(List source, List linkList) {
		List rowData;
		int index = 0;
		for (Iterator iter = source.iterator(); iter.hasNext();) {
			rowData = (List) iter.next();
			rowData.addAll((List) linkList.get(index));
			index++;
		}
		return source;
	}

	/**
	 * @todo 连接集合
	 * @param source
	 * @param linkList
	 * @param sourceIndexColumn
	 * @param linkIndexColumn
	 * @return
	 */
	public static List linkList(List source, List linkList, int sourceIndexColumn, int linkIndexColumn) {
		List result = new ArrayList();
		Object compareObj;
		List rowData;
		List linkRowData;
		for (Iterator iter = source.iterator(); iter.hasNext();) {
			rowData = (List) iter.next();
			compareObj = rowData.get(sourceIndexColumn);
			List rowList = new ArrayList();
			addList(rowList, rowData, -1);
			for (Iterator linkIterator = linkList.iterator(); linkIterator.hasNext();) {
				linkRowData = (List) linkIterator.next();
				if (equals(compareObj, linkRowData.get(linkIndexColumn))) {
					addList(rowList, linkRowData, linkIndexColumn);
					linkIterator.remove();
					break;
				}
			}
			result.add(rowList);
		}
		return result;
	}

	/**
	 * @todo 连接集合
	 * @param source
	 * @param linkList
	 * @param sourceIndexColumn
	 * @param linkIndexColumn
	 * @return
	 */
	public static List linkList(List source, List linkList, int[] sourceIndexColumn, int[] linkIndexColumn) {
		if (source == null || linkList == null)
			return null;
		List result = new ArrayList();
		int sourceSize = source.size();
		int linkSize = linkList.size();
		for (int i = 0; i < sourceSize; i++) {
			if (i == linkSize)
				break;
			List rowList = new ArrayList();
			addList(rowList, (List) source.get(i), sourceIndexColumn);
			addList(rowList, (List) linkList.get(i), linkIndexColumn);
			result.add(rowList);
		}
		return result;
	}

	// suport linkList
	private static void addList(List targetList, Object obj, int excludeCol) {
		if (obj instanceof Collection) {
			int meter = 0;
			for (Iterator iter = ((Collection) obj).iterator(); iter.hasNext();) {
				if (meter != excludeCol || excludeCol == -1)
					targetList.add(iter.next());
				meter++;
			}
		} else if (obj.getClass().isArray()) {
			Object[] addAry = convertArray(obj);
			for (int i = 0, n = addAry.length; i < n; i++) {
				if (i != excludeCol || excludeCol == -1)
					targetList.add(addAry[i]);
			}
		} else
			targetList.add(obj);
	}

	private static void addList(List targetList, List rowList, int[] includeCols) {
		for (int i = 0; i < includeCols.length; i++) {
			targetList.add(rowList.get(includeCols[i]));
		}
	}

	/**
	 * @todo 删除列
	 * @param source
	 * @param columns
	 */
	public static void deleteColumns(List source, Integer[] columns) {
		if (source == null || source.isEmpty())
			return;
		if (columns == null || columns.length == 0)
			return;
		boolean isArray = (source.get(0).getClass().isArray()) ? true : false;
		Object rowData;
		// 对数组进行排序，将需要删除的列按照升序排列，便于逆向删除
		sortArray(columns, false);
		HashMap columnMap = new HashMap();
		for (int i = 0, n = columns.length; i < n; i++)
			columnMap.put(columns[i], "");
		Object[] arrayData;
		List rowList;
		int meter = 0;
		for (int i = 0, n = source.size(); i < n; i++) {
			rowData = source.get(i);
			if (isArray) {
				arrayData = convertArray(rowData);
				Object[] newArray = new Object[arrayData.length - columns.length];
				meter = 0;
				for (int j = 0; j < arrayData.length; j++) {
					if (columnMap.get(j) == null) {
						newArray[meter] = arrayData[j];
						meter++;
					}
				}
				source.set(i, newArray);
			} else {
				// 逆向删除
				rowList = (List) rowData;
				for (int j = columns.length - 1; j >= 0; j--) {
					rowList.remove(columns[j].intValue());
				}
			}
		}
	}

	/**
	 * @todo 移动列，将特定列移到指定列的后面
	 * @param source
	 * @param columns
	 * @param afterIndex
	 */
	public static void moveColumns(List source, int[] columns, int afterIndex) {
		if (source == null || source.isEmpty())
			return;
		// 获取宽度并判断行数据的类型
		Object firstRowData = source.get(0);
		boolean isArray = (firstRowData.getClass().isArray()) ? true : false;
		int width = isArray ? ((Object[]) firstRowData).length : ((List) firstRowData).size();
		HashMap columnMap = new HashMap();
		for (int i = 0; i < columns.length; i++)
			columnMap.put(columns[i], "0");
		int[] newOrderColumns = new int[width + columns.length];
		// 将移动的列先放入新的位置
		for (int i = 0; i < width; i++) {
			if (i <= afterIndex)
				newOrderColumns[i] = i;
			else {
				if (i == afterIndex + 1) {
					for (int j = 0; j < columns.length; j++)
						newOrderColumns[i + j] = columns[j];
				}
				newOrderColumns[i + columns.length] = i;
			}
			if (i == width - 1 && i == afterIndex) {
				for (int j = 0; j < columns.length; j++)
					newOrderColumns[i + j] = columns[j];
			}
		}
		// 剔除移动前原位置的列
		int meter = 0;
		int[] realOrderColumns = new int[width];
		for (int i = 0; i < newOrderColumns.length; i++) {
			if (i <= afterIndex) {
				if (columnMap.get(newOrderColumns[i]) == null) {
					realOrderColumns[meter] = newOrderColumns[i];
					meter++;
				}
			} else {
				if (columnMap.get(newOrderColumns[i]) == null) {
					realOrderColumns[meter] = newOrderColumns[i];
					meter++;
				} else if (columnMap.get(newOrderColumns[i]).equals("0")) {
					realOrderColumns[meter] = newOrderColumns[i];
					meter++;
					columnMap.put(newOrderColumns[i], "1");
				}
			}
		}
		Object rowData;
		Object[] arrayData;
		List rowList;
		for (int i = 0, n = source.size(); i < n; i++) {
			rowData = source.get(i);
			if (isArray) {
				arrayData = (Object[]) rowData;
				Object[] newArray = new Object[width];
				for (int j = 0; j < realOrderColumns.length; j++)
					newArray[j] = arrayData[realOrderColumns[j]];
				rowData = newArray;
			} else {
				rowList = (List) rowData;
				List newList = new ArrayList();
				for (int j = 0; j < realOrderColumns.length; j++)
					newList.add(rowList.get(realOrderColumns[j]));
				rowList = newList;
			}
		}
	}

	/**
	 * @todo 切取集合的相应列块
	 * @param source
	 * @param columns
	 * @return
	 */
	public static List diceColumns(List source, int[] columns) {
		if (source == null || source.isEmpty())
			return null;
		boolean isArray = (source.get(0).getClass().isArray()) ? true : false;
		List result = new ArrayList();
		List tmpList;
		Object[] tmpAry;
		if (isArray) {
			for (int i = 0, n = source.size(); i < n; i++) {
				tmpAry = (Object[]) source.get(i);
				List rowList = new ArrayList();
				for (int j = 0; j < columns.length; j++) {
					rowList.add(tmpAry[columns[j]]);
				}
				result.add(rowList);
			}
		} else {
			for (int i = 0, n = source.size(); i < n; i++) {
				tmpList = (List) source.get(i);
				List rowList = new ArrayList();
				for (int j = 0; j < columns.length; j++) {
					rowList.add(tmpList.get(columns[j]));
				}
				result.add(rowList);
			}
		}
		return result;
	}

	/**
	 * @todo 切取集合的单一列（切片）
	 * @param source
	 * @param columnIndex
	 * @param distinct
	 *            是否去除重复
	 * @return
	 */
	public static List sliceColumn(List source, int columnIndex, boolean distinct) {
		if (source == null || source.isEmpty())
			return null;
		boolean isArray = (source.get(0).getClass().isArray()) ? true : false;
		List result = new ArrayList();
		Object cell;
		for (int i = 0, n = source.size(); i < n; i++) {
			cell = isArray ? convertArray(source.get(i))[columnIndex] : ((List) source.get(i)).get(columnIndex);
			if (distinct) {
				if (!result.contains(cell))
					result.add(cell);
			} else
				result.add(cell);
		}
		return result;
	}

	/**
	 * @todo 将集合数据转成hashMap
	 * @param data
	 * @param keyProp
	 * @param valueProp
	 * @param keyToStr
	 *            将key统一转成字符串
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
	 * @param keyToStr
	 *            将key统一转成字符串
	 * @param isLinkedHash
	 *            返回的是否为LinkedHashMap
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
				for (int j = 0, k = rowAry.length; j < k; j++)
					rowList.add(rowAry[j]);
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

	public static void groupSummary(List sumData, Object[][] groupIndexs, Integer[] sumColumns, int totalColumn,
			String totalLabel, boolean hasAverage, String averageLabel, String sumRecordSite) {
		groupSummary(sumData, groupIndexs, sumColumns, totalColumn, totalLabel, hasAverage, averageLabel, 2,
				sumRecordSite == null ? "bottom" : sumRecordSite, false);
	}

	public static void groupSummary(List sumData, Object[][] groupIndexs, Integer[] sumColumns, int totalColumn,
			String totalLabel, boolean hasAverage, String averageLabel, String sumRecordSite,
			boolean globalSumReverse) {
		groupSummary(sumData, groupIndexs, sumColumns, totalColumn, totalLabel, hasAverage, averageLabel, 2,
				sumRecordSite == null ? "bottom" : sumRecordSite, globalSumReverse);
	}

	public static void groupReverseSummary(List sumData, Object[][] groupIndexs, Integer[] sumColumns, int totalColumn,
			String totalLabel, boolean hasAverage, String averageLabel, String sumRecordSite) {
		groupReverseSummary(sumData, groupIndexs, sumColumns, totalColumn, totalLabel, hasAverage, averageLabel, 2,
				sumRecordSite == null ? "top" : sumRecordSite);
	}

	/**
	 * @todo 分组合计
	 * @param sumData
	 * @param groupIndexs
	 *            {汇总列，汇总标题，平均标题，汇总相对平均的位置(left/right/top/bottom)}
	 * @param sumColumns
	 * @param globalSumSite
	 *            存在全局汇总时，总计标题存放的列
	 * @param totalLabel
	 * @param hasAverage
	 * @param averageLabel
	 * @param averageFormat
	 * @param sumRecordSite
	 */
	public static void groupSummary(List sumData, Object[][] groupIndexs, Integer[] sumColumns, int globalSumSite,
			String totalLabel, boolean hasAverage, String averageLabel, int radixSize, String sumRecordSite,
			boolean totalSumReverse) {
		boolean hasTotalSum = false;
		if (globalSumSite >= 0 || hasAverage)
			hasTotalSum = true;
		List rowList;
		int groupTotal = (groupIndexs == null) ? 0 : groupIndexs.length;
		int columns = ((List) sumData.get(0)).size();
		int dataSize = sumData.size();
		// 总数
		Object[] totalSum = new Object[columns];
		// 记录各个分组的汇总小计
		HashMap groupSumMap = new HashMap();
		HashMap groupPreIndexMap = new HashMap();
		for (int i = 0; i < groupTotal; i++) {
			groupSumMap.put(groupIndexs[i][0], new Object[columns]);
			groupPreIndexMap.put(groupIndexs[i][0], 0);
		}
		groupSumMap.put(-1, totalSum);
		groupPreIndexMap.put(-1, 0);
		boolean isEqual = true;
		int preIndex = 0;
		int dymSize = 0;
		boolean isLast = false;
		// 分组对比值
		HashMap preGroupCompareIndexs = new HashMap();
		HashMap nowGroupCompareIndexs = new HashMap();
		String preGroupIndexs = "";
		String nowGroupIndexs = "";
		for (int i = 0; i < sumData.size(); i++) {
			nowGroupIndexs = "";
			dymSize = sumData.size();
			isLast = (i == dymSize - 1);
			isEqual = false;
			rowList = (List) sumData.get(i);
			// 构造同质比较条件
			// 第一个
			if (i == 0) {
				for (int k = 0; k < groupTotal; k++) {
					preGroupIndexs = preGroupIndexs + rowList.get((Integer) groupIndexs[k][0]).toString();
					preGroupCompareIndexs.put((Integer) groupIndexs[k][0], preGroupIndexs);
				}
			}

			for (int k = 0; k < groupTotal; k++) {
				nowGroupIndexs += rowList.get((Integer) groupIndexs[k][0]).toString();
				nowGroupCompareIndexs.put((Integer) groupIndexs[k][0], nowGroupIndexs);
			}

			for (int j = groupTotal - 1; j >= 0; j--) {
				// 不相等
				if (!equals(nowGroupCompareIndexs.get((Integer) groupIndexs[j][0]),
						preGroupCompareIndexs.get((Integer) groupIndexs[j][0]))) {
					// 最后一条
					if (j == groupTotal - 1)
						isEqual = true;
					sumData.addAll(i,
							createSummaryRow((Object[]) groupSumMap.get((Integer) groupIndexs[j][0]),
									(List) sumData.get(preIndex), (Integer) groupIndexs[j][0], groupIndexs[j],
									(Integer) groupPreIndexMap.get(groupIndexs[j][0]), radixSize));
					groupPreIndexMap.put(groupIndexs[j][0], 0);
					preGroupCompareIndexs.put((Integer) groupIndexs[j][0],
							nowGroupCompareIndexs.get((Integer) groupIndexs[j][0]));
					// 汇总之后重新置值
					groupSumMap.put((Integer) groupIndexs[j][0], new Object[columns]);
					// 同时存在汇总和平均
					if (groupIndexs[j][1] != null && groupIndexs[j][2] != null
							&& (groupIndexs[j][3].equals("top") || groupIndexs[j][3].equals("bottom")))
						i = i + 2;
					// (必须要有一个汇总或平均)
					else
						i++;
				} else
					break;
			}
			// 汇总计算（含求平均）
			calculateTotal(groupSumMap, rowList, sumColumns, radixSize);
			for (int m = 0; m < groupTotal; m++) {
				groupPreIndexMap.put(groupIndexs[m][0], (Integer) groupPreIndexMap.get(groupIndexs[m][0]) + 1);
			}
			// 相等
			if (isEqual) {
				preIndex = i;
			}
			// 最后一条记录
			if (isLast) {
				for (int j = groupTotal - 1; j >= 0; j--) {
					sumData.addAll(createSummaryRow((Object[]) groupSumMap.get((Integer) groupIndexs[j][0]),
							(List) sumData.get(preIndex), (Integer) groupIndexs[j][0], groupIndexs[j],
							(Integer) groupPreIndexMap.get(groupIndexs[j][0]), radixSize));
				}
				break;
			}
		}
		// 存在总的求和或平均
		if (hasTotalSum) {
			if (totalSumReverse)
				sumData.addAll(0, createSummaryRow((Object[]) groupSumMap.get(-1), (List) sumData.get(preIndex),
						globalSumSite,
						new Object[] { -1, totalLabel, averageLabel, sumRecordSite == null ? "bottom" : sumRecordSite },
						dataSize, radixSize));
			else
				sumData.addAll(createSummaryRow((Object[]) groupSumMap.get(-1), (List) sumData.get(preIndex),
						globalSumSite,
						new Object[] { -1, totalLabel, averageLabel, sumRecordSite == null ? "bottom" : sumRecordSite },
						dataSize, radixSize));
		}
	}

	/**
	 * @todo 逆向分组合计
	 * @param sumData
	 * @param groupIndexs
	 * @param sumColumns
	 * @param totalColumnIndex
	 * @param totalTitle
	 * @param hasAverage
	 * @param averageTitle
	 * @param radixSize
	 *            小数位长度
	 * @param firstSummary
	 */
	public static void groupReverseSummary(List sumData, Object[][] groupIndexs, Integer[] sumColumns,
			int globalSumSite, String totalLabel, boolean hasAverage, String averageLabel, int radixSize,
			String sumRecordSite) {
		boolean hasTotalSum = false;
		if (globalSumSite >= 0 || hasAverage)
			hasTotalSum = true;
		int groupTotal = (groupIndexs == null) ? 0 : groupIndexs.length;
		int columns = ((List) sumData.get(0)).size();
		if (sumRecordSite == null)
			sumRecordSite = "bottom";
		// 总数
		Object[] totalSum = new Object[columns];
		int dataSize = sumData.size();
		// 记录各个分组的汇总小计
		HashMap groupSumMap = new HashMap();
		HashMap groupPreIndexMap = new HashMap();
		for (int i = 0; i < groupTotal; i++) {
			groupSumMap.put(groupIndexs[i][0], new Object[columns]);
			groupPreIndexMap.put(groupIndexs[i][0], dataSize - 1);
		}
		groupSumMap.put(-1, totalSum);
		groupPreIndexMap.put(-1, dataSize - 1);
		String preGroupIndexs = "";
		String nowGroupIndexs = "";
		boolean isEqual = true;
		int preIndex = dataSize - 1;
		List rowList;
		// 分组对比值
		HashMap preGroupCompareIndexs = new HashMap();
		HashMap nowGroupCompareIndexs = new HashMap();
		for (int i = dataSize - 1; i >= 0; i--) {
			nowGroupIndexs = "";
			isEqual = false;
			rowList = (List) sumData.get(i);
			// 制造同质比较条件
			// 第一个
			if (i == dataSize - 1) {
				for (int k = 0; k < groupTotal; k++) {
					preGroupIndexs = preGroupIndexs + rowList.get((Integer) groupIndexs[k][0]).toString();
					preGroupCompareIndexs.put((Integer) groupIndexs[k][0], preGroupIndexs);
				}
			}
			for (int k = 0; k < groupTotal; k++) {
				nowGroupIndexs += rowList.get((Integer) groupIndexs[k][0]).toString();
				nowGroupCompareIndexs.put((Integer) groupIndexs[k][0], nowGroupIndexs);
			}

			for (int j = groupTotal - 1; j >= 0; j--) {
				// 不相等
				if (!equals(nowGroupCompareIndexs.get((Integer) groupIndexs[j][0]),
						preGroupCompareIndexs.get((Integer) groupIndexs[j][0]))) {
					if (j == groupTotal - 1)
						isEqual = true;
					sumData.addAll(i + 1,
							createSummaryRow((Object[]) groupSumMap.get((Integer) groupIndexs[j][0]),
									(List) sumData.get(preIndex), (Integer) groupIndexs[j][0], groupIndexs[j],
									(Integer) groupPreIndexMap.get(groupIndexs[j][0]) - i, radixSize));
					preGroupCompareIndexs.put((Integer) groupIndexs[j][0],
							nowGroupCompareIndexs.get((Integer) groupIndexs[j][0]));
					groupPreIndexMap.put(groupIndexs[j][0], i);
					// 汇总之后重新置值
					groupSumMap.put((Integer) groupIndexs[j][0], new Object[columns]);
				} else
					break;
			}
			calculateTotal(groupSumMap, rowList, sumColumns, radixSize);
			if (isEqual) {
				preIndex = i;
			}
			if (i == 0) {
				for (int j = groupTotal - 1; j >= 0; j--) {
					sumData.addAll(0,
							createSummaryRow((Object[]) groupSumMap.get((Integer) groupIndexs[j][0]),
									(List) sumData.get(preIndex), (Integer) groupIndexs[j][0], groupIndexs[j],
									(Integer) groupPreIndexMap.get(groupIndexs[j][0]) + 1, radixSize));
				}
			}
		}

		// 存在总的求和或平均
		if (hasTotalSum) {
			sumData.addAll(0, createSummaryRow((Object[]) groupSumMap.get(-1), (List) sumData.get(preIndex),
					globalSumSite, new Object[] { -1, totalLabel, averageLabel, sumRecordSite }, dataSize, radixSize));
		}
	}

	/**
	 * @todo 创建汇总行
	 * @param rowSummaryData
	 * @param rowList
	 * @param groupIndex
	 * @param title
	 * @param rowCount
	 * @param radixSize
	 *            小数位长度
	 * @return
	 */
	private static List createSummaryRow(Object[] rowSummaryData, List rowList, int groupIndex, Object[] title,
			int rowCount, int radixSize) {
		List result = new ArrayList();
		List summary = null;
		List average = null;
		int titleIndex = groupIndex;
		if (title.length == 5 && title[4] != null)
			titleIndex = (Integer) title[4];
		// 汇总
		if (title[1] != null || (title[3].equals("left") || title[3].equals("right"))) {
			summary = new ArrayList();
			// 汇总数据加入新的数据行中
			for (int i = 0, n = rowSummaryData.length; i < n; i++)
				summary.add(i, rowSummaryData[i]);
			// 设置分组列前面的数据
			for (int i = 0; i <= titleIndex; i++)
				summary.set(i, rowList.get(i));

			// 设置标题
			if (title[1] != null && !title[1].toString().trim().equals(""))
				summary.set(titleIndex, title[1]);
		}
		// 平均
		if (title[2] != null || (title[3].equals("left") || title[3].equals("right"))) {
			average = new ArrayList();
			// 平均数据加入新的数据行中
			Double averageValue;
			for (int i = 0, n = rowSummaryData.length; i < n; i++) {
				if (rowSummaryData[i] == null)
					average.add(i, null);
				else {
					averageValue = Double.valueOf(rowSummaryData[i].toString().replace(",", "")) / rowCount;
					if (radixSize >= 0)
						average.add(i, BigDecimal.valueOf(averageValue).setScale(radixSize, BigDecimal.ROUND_FLOOR)
								.doubleValue());
					else
						average.add(i, BigDecimal.valueOf(averageValue).doubleValue());
				}
			}
			// 设置分组列前面的数据
			for (int i = 0; i <= titleIndex; i++)
				average.set(i, rowList.get(i));

			// 设置标题
			if (title[2] != null && !title[2].toString().trim().equals(""))
				average.set(titleIndex, title[2]);
		}
		// 汇总或求平均
		if (summary == null || average == null) {
			if (summary != null)
				result.add(summary);
			if (average != null)
				result.add(average);
		} else {
			if (title[3].equals("top") || title[3].equals("bottom")) {
				result.add(summary);
				// 平均数据优先显示
				if (title[3].equals("bottom"))
					result.add(0, average);
			} else {
				// 汇总数据是否左边显示
				boolean isLeft = title[3].equals("left");
				String sumCellValue;
				String averageValue;
				String linkSign = " / ";
				if (title.length == 6 && title[5] != null)
					linkSign = title[5].toString();
				for (int i = 0, n = rowSummaryData.length; i < n; i++) {
					if (rowSummaryData[i] != null) {
						sumCellValue = (summary.get(i) == null) ? "" : summary.get(i).toString();
						averageValue = (average.get(i) == null) ? "" : average.get(i).toString();
						summary.set(i, isLeft ? (sumCellValue + linkSign + averageValue)
								: (averageValue + linkSign + sumCellValue));
					}
				}
				result.add(summary);
			}
		}
		return result;
	}

	/**
	 * @todo 汇总计算
	 * @param groupSumMap
	 * @param rowList
	 * @param summaryColumns
	 * @param radixSize
	 */
	private static void calculateTotal(HashMap groupSumMap, List rowList, Integer[] summaryColumns, int radixSize) {
		Object[] groupSums;
		int size = summaryColumns.length;
		Object cellValue;
		Object sumCellValue;
		int columnIndex;
		// new BigDecimal()
		for (Iterator iter = groupSumMap.values().iterator(); iter.hasNext();) {
			groupSums = (Object[]) iter.next();
			for (int i = 0; i < size; i++) {
				columnIndex = summaryColumns[i];
				sumCellValue = groupSums[columnIndex];
				cellValue = rowList.get(columnIndex);
				if (radixSize >= 0)
					groupSums[columnIndex] = new BigDecimal(
							StringUtil.isBlank(sumCellValue) ? "0" : sumCellValue.toString().replace(",", ""))
									.add(new BigDecimal(StringUtil.isBlank(cellValue) ? "0"
											: cellValue.toString().replace(",", "")))
									.setScale(radixSize, BigDecimal.ROUND_FLOOR);
				else
					groupSums[columnIndex] = new BigDecimal(
							StringUtil.isBlank(sumCellValue) ? "0" : sumCellValue.toString().replace(",", ""))
									.add(new BigDecimal(StringUtil.isBlank(cellValue) ? "0"
											: cellValue.toString().replace(",", "")));
			}
		}
	}

	/**
	 * @todo 移除树中没有叶子的节点
	 * @param treeList
	 * @param removeHandler
	 * @param judgeLeafValue
	 *            判断是否叶子的对比值
	 * @param judgeDelLeafValue
	 *            判断是否删除叶子的对比值
	 */
	public static void removeNoLeafTreeNode(List treeList, TreeRemoveCallbackHandler removeHandler,
			Object judgeLeafValue, Object judgeDelLeafValue) {
		// {id,pid,leafValue,delLeafValue}
		Object[] idPidLeafAndDelLeafCondition;
		boolean isDelete = true;
		Object parentId = null;
		int beginIndex = treeList.size() - 1;

		// 从最后面向前处理
		for (int i = beginIndex; i >= 0; i--) {
			idPidLeafAndDelLeafCondition = removeHandler.getIdPidLeafAndDelLeafCondition(treeList.get(i));
			// 叶子节点
			if (equals(idPidLeafAndDelLeafCondition[2], judgeLeafValue)) {
				if (equals(idPidLeafAndDelLeafCondition[3], judgeDelLeafValue)) {
					treeList.remove(i);
				}
				isDelete = false;
			} else {
				// 当前id与父id对比,判断是否不同层节点切换
				if (parentId != idPidLeafAndDelLeafCondition[0])
					isDelete = true;
				// 去除
				if (isDelete) {
					treeList.remove(i);
				}
			}
			// 设置父节点
			parentId = idPidLeafAndDelLeafCondition[1];
		}
	}

	/**
	 * @todo 删除重复的对象
	 * @param datas
	 * @param identityHandler
	 */
	public static void removeRepeat(List datas, IdentityCallbackHandler identityHandler) {
		if (null != datas && !datas.isEmpty()) {
			HashMap identityMap = new HashMap();
			Object identityValue;
			Object rowObj;
			for (Iterator iter = datas.iterator(); iter.hasNext();) {
				rowObj = iter.next();
				identityValue = identityHandler.getIdentity(rowObj);
				if (identityMap.get(identityValue) == null)
					identityMap.put(identityValue, 1);
				else {
					rowObj = null;
					iter.remove();
				}
			}
		}
	}

	/**
	 * @todo <b>列转行</b>
	 * @param data
	 * @param colIndex
	 *            保留哪些列进行旋转(其它的列数据忽略)
	 * @return
	 */
	public static List convertColToRow(List data, Integer[] colIndex) {
		if (data == null || data.isEmpty())
			return data;
		boolean innerAry = data.get(0).getClass().isArray();
		int newResultRowCnt = 0;
		if (colIndex == null) {
			newResultRowCnt = innerAry ? convertArray(data.get(0)).length : ((List) data.get(0)).size();
		} else
			newResultRowCnt = colIndex.length;

		/**
		 * 构造结果集
		 */
		Object[][] resultAry = new Object[newResultRowCnt][data.size()];
		Object[] rowAry = null;
		List rowList = null;
		for (int i = 0, n = data.size(); i < n; i++) {
			if (innerAry)
				rowAry = convertArray(data.get(i));
			else
				rowList = (List) data.get(i);
			if (colIndex != null) {
				for (int j = 0, k = colIndex.length; j < k; j++) {
					resultAry[j][i] = innerAry ? rowAry[colIndex[j]] : rowList.get(colIndex[j]);
				}
			} else {
				for (int j = 0; j < newResultRowCnt; j++) {
					resultAry[j][i] = innerAry ? rowAry[j] : rowList.get(j);
				}
			}
		}
		return arrayToDeepList(resultAry);
	}

	/**
	 * @todo 使用指定的字符串链接集合中的元素
	 * @param collection
	 * @param joinStr
	 * @return
	 */
	public static String join(Collection collection, String joinStr) {
		StringBuilder result = new StringBuilder();
		boolean secondIndex = false;
		for (Object obj : collection) {
			if (obj != null) {
				if (secondIndex)
					result.append(joinStr);
				else
					secondIndex = true;
				result.append(obj.toString());
			}
		}
		return result.toString();
	}

	public static class SummarySite {
		public static String top = "top";
		public static String bottom = "bottom";
		public static String left = "left";
		public static String right = "right";
	}

	/**
	 * @todo 判断字符串是否在给定的数组中
	 * @param compareStr
	 * @param compareAry
	 * @param ignoreCase
	 * @return
	 */
	public static boolean any(Object value, Object[] compareAry, boolean ignoreCase) {
		if (value == null || (compareAry == null || compareAry.length == 0))
			return false;
		for (Object s : compareAry) {
			if (s == null)
				return false;
			if (value.equals(s))
				return true;
			if (ignoreCase && value.toString().equalsIgnoreCase(s.toString()))
				return true;
		}
		return false;
	}

	/**
	 * @todo <b>对象比较</b>
	 * @param target
	 * @param compared
	 * @return
	 */
	private static boolean equals(Object target, Object compared) {
		if (null == target) {
			return target == compared;
		} else
			return target.equals(compared);
	}

	/**
	 * @todo 用于不同类型数据之间进行比较，判断是否相等,当类型不一致时统一用String类型比较
	 * @param target
	 * @param compared
	 * @param ignoreCase
	 * @return
	 */
	private static boolean equalsIgnoreType(Object target, Object compared, boolean ignoreCase) {
		if (target == null || compared == null)
			return target == compared;
		if (target.getClass().equals(compared.getClass()) && !(target instanceof CharSequence))
			return target.equals(compared);
		if (ignoreCase)
			return target.toString().equalsIgnoreCase(compared.toString());
		else
			return target.toString().equals(compared.toString());
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
					//颠倒一下,将数据长的作为主体
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
				if (rowData.size() < index + 1 || rowData.get(index) == null)
					replace = "";
				else
					replace = rowData.get(index).toString();
				result = StringUtil.replaceAllStr(result, ExcelToyConstants.EXCEL_TITLE_BEGIN_MARK.concat(pkCols[i])
						.concat(ExcelToyConstants.EXCEL_TITLE_END_MARK), replace);
			}
		}
		return result;
	}
}
