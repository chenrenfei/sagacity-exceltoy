/**
 * 
 */
package org.sagacity.tools.exceltoy.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.sagacity.tools.exceltoy.ExcelToyConstants;
import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.convert.ConvertDataSource;

/**
 * @project sagacity-tools
 * @description 转换器解析、调用工具
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ConvertUtil.java,Revision:v1.0,Date:2009-5-24
 * @Modification Date:2011-6-10 {改善了转换器匹配精度，排除了数据中存在@符号误当做转换器}
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConvertUtil {
	private final static Logger logger = LogManager.getLogger(ConvertUtil.class);

	// 存放转换器名称:@xxxConvert形式
	private static List allConvertName = new ArrayList();
	private static HashMap convertInstanceMap = new HashMap();
	private static HashMap convertClassMap = new HashMap();

	/**
	 * 存放转换器字符串包含的转换器链，即一个串中嵌套多个转换函数，用集合从里到外存放
	 */
	private static HashMap convertStackMap = new HashMap();

	/**
	 * 存放转换字符串中参数，如@dateFormat(${fieldA}),则存放${fieldA}
	 */
	private static HashMap convertParamMap = new HashMap();

	/**
	 * 转换器的格式
	 */
	private static Pattern convertPattern = Pattern.compile("@[a-z|A-Z]+[0-9]?[a-z|A-Z]*\\([\\w|\\W]*\\)");

	/**
	 * 转换器的格式
	 */
	private static Pattern convertParamPattern = Pattern.compile("@[a-z|A-Z]+[0-9]?[a-z|A-Z]*\\(");

	/**
	 * 
	 * @todo 将转换器放入HashMap堆栈中
	 * @param id
	 * @param processClass
	 * @param extend
	 * @param params
	 */
	private static void putConvertStack(String id, String processClass, String extend, List params) {
		try {
			String className = null;
			AbstractConvert extendConvert = null;
			// 判断是否继承已有的convert,
			if (StringUtil.isNotBlank(processClass)) {
				className = processClass;
			} else if (StringUtil.isNotBlank(extend)) {
				className = (String) convertClassMap.get(extend);
				extendConvert = (AbstractConvert) convertInstanceMap.get(extend);
			}

			if (StringUtil.isBlank(className)) {
				logger.info("id=" + id + "的 convert没有定义对应的继承或convert class!");
				logger.info("extend=" + extend);
				logger.info("class=" + processClass);
				return;
			}

			AbstractConvert convert = (AbstractConvert) Class.forName(className).newInstance();
			convertClassMap.put(id, className);

			// 将继承的convert中设置的参数设置到当前的convert中
			if (extendConvert != null)
				BeanUtils.copyProperties(convert, extendConvert);
			// 设置当前convert中参数
			if (params != null && !params.isEmpty()) {
				Element param;
				String paramKey;
				String value;
				for (int i = 0; i < params.size(); i++) {
					param = (Element) params.get(i);
					paramKey = param.attributeValue("name");
					if (param.attribute("value") != null)
						value = param.attributeValue("value");
					else
						value = param.getText();
					// 设置常量替换后的值
					BeanUtils.setProperty(convert, paramKey, ExcelToyConstants.getPropertyValue(value));
				}
			}
			convertInstanceMap.put(id, convert);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("解析convert转换器错误:" + e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @todo 加载配置文件中的convert
	 * @param convertElts
	 */
	public static void loadConverts(List convertElts) {
		if (convertElts == null || convertElts.isEmpty())
			return;
		Element convertElt;
		String id = null;
		String processClass = null;
		String extend = null;
		List params = null;
		for (int i = 0; i < convertElts.size(); i++) {
			convertElt = (Element) convertElts.get(i);
			// 默认用id来定义唯一转换器，同时提供用名称定义
			if (convertElt.attribute("id") != null)
				id = convertElt.attributeValue("id");
			else
				id = convertElt.attributeValue("name");
			allConvertName.add("@" + id + "(");
			if (convertElt.attribute("class") != null)
				processClass = convertElt.attributeValue("class");
			else
				processClass = null;
			if (convertElt.attribute("extend") != null)
				extend = convertElt.attributeValue("extend");
			else
				extend = null;
			params = convertElt.elements("param");

			// 定义class和extend对应的转换器已经定义的先加载
			if (StringUtil.isNotBlank(processClass)
					|| (StringUtil.isNotBlank(extend) && convertInstanceMap.containsKey(extend))) {
				putConvertStack(id, processClass, extend, params);
				convertElts.remove(i);
				// 从头继续加载,直到遍历完后都找不到对应的extend和processClass的转换器
				i = -1;
			}
		}
		if (convertElts.size() == 0)
			logger.info("所有定义的转换器全部加载成功!");
		else {
			for (int i = 0; i < convertElts.size(); i++) {
				convertElt = (Element) convertElts.get(i);
				logger.error("转换器:id=" + convertElt.attributeValue("id") + "未能加载!请正确检查其extend对象是否正确定义!");
			}
		}
	}

	/**
	 * 
	 * @todo 根据convert key获取对应的convert
	 * @param key
	 * @return
	 */
	public static AbstractConvert getConvert(String key) {
		String realKey = key.indexOf("@") == 0 ? key.substring(1) : key;
		Object result = convertInstanceMap.get(realKey);
		if (null != result)
			return (AbstractConvert) result;
		else
			return null;
	}

	/**
	 * 
	 * @todo 获取转换器堆栈
	 * @param convertStack
	 * @return
	 * @throws Exception
	 */
	public static List getConvertStack(String convertStack) throws Exception {
		if (convertStackMap.get(convertStack) == null) {
			int matchCount = StringUtil.nestMatchCnt(convertStack, convertPattern);
			List result = new ArrayList();
			int beginIndex = convertStack.indexOf("@");
			String convert;
			for (int i = 0; i < matchCount; i++) {
				convert = convertStack.substring(beginIndex + 1, convertStack.indexOf("(", beginIndex));
				if (convertInstanceMap.get(convert) != null) {
					// 逆序存放对应的转换器实例，将最外层放在最后
					result.add(0, convertInstanceMap.get(convert));
				} else {
					logger.error("转换器:" + convert + "没有定义!");
					throw new Exception("转换器:" + convert + "没有定义!");
				}
				beginIndex = convertStack.indexOf("@", beginIndex + 1);
			}
			convertStackMap.put(convertStack, result);
		}
		return (List) convertStackMap.get(convertStack);
	}

	/**
	 * 
	 * @todo 获取转换字符串中的参数
	 * @param convertStack
	 * @return
	 */
	public static String getConvertParam(String convertStack) {
		if (convertParamMap.get(convertStack) == null) {
			int matchCount = StringUtil.matchCnt(convertStack, convertParamPattern);
			int index = StringUtil.indexOrder(convertStack, "(", matchCount - 1);
			convertParamMap.put(convertStack,
					convertStack.substring(index + 1, StringUtil.getSymMarkIndex("(", ")", convertStack, index)));
		}
		return (String) convertParamMap.get(convertStack);
	}

	/**
	 * @todo 执行转换器字符串
	 * @param convertStack
	 * @param loopAs
	 * @param asValue
	 * @return
	 * @throws Exception
	 */
	public static Object executeConvert(String convertStack, String loopAs, String asValue) throws Exception {
		Object result = null;
		try {
			// 用excel数据替换相关参数占位符号
			String paramValue = convertStack;
			// 子表用分割符号分割excel数据某列，其每个值作为子表的某个列的数据源
			if (loopAs != null)
				paramValue = StringUtil.replaceAllStr(paramValue, "#{" + loopAs + "}", asValue);

			// 非链式，返回字符串，有别于链式，链式可以直接返回转换后的对象
			if (StringUtil.matches(paramValue, convertPattern) && isConvert(paramValue, false)) {
				result = replaceConvert(paramValue);
			} else
				result = paramValue;
			if (result instanceof String) {
				String str = result.toString();
				// 使用#[parentTable_field]参数，则用主表的数据替换相关主表字段占位符号，其占位符号跟常量一致
				if (ConvertDataSource.getMainTableRowData() != null
						&& ConvertDataSource.getMainTableRowData().length > 0) {
					for (int i = 0; i < ConvertDataSource.getMainTableRowData().length; i++) {
						str = StringUtil.replaceAllStr(str,
								"#{" + ConvertDataSource.getMainTableRowData()[i][0].toString() + "}",
								ConvertDataSource.getMainTableRowData()[i][1].toString());
					}
				}
				if (str.indexOf("${") != -1)
					str = EQLUtil.replaceHolder(str, EQLUtil.parseExcelFields(str));
				// 常量替换
				return ExcelToyConstants.replaceConstants(str);
			} else
				return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @todo 递归调用解析字符串中的转换器
	 * @param hasConvertStr
	 * @return
	 */
	private static Object replaceConvert(String hasConvertStr) throws Exception {
		String source = hasConvertStr;
		Matcher matcher = convertPattern.matcher(source);
		String matchedConvert = null;
		String tmpMatchedConvert = null;
		int count = 0;
		int subIndexCount = 0;
		int index = 0;
		while (matcher.find()) {
			index = matcher.start();
			tmpMatchedConvert = matcher.group();
			// 判断是否是转换器
			if (isConvert(tmpMatchedConvert, true)) {
				count++;
				matchedConvert = tmpMatchedConvert;
				// index后移1
				subIndexCount += index + 1;
			}
			source = source.substring(index + 1);
			matcher = convertPattern.matcher(source);
		}
		// 匹配不上，则表示字符串中的转换器已经全部执行被替换，返回结果终止递归
		if (count == 0)
			return hasConvertStr;
		int sysMarkIndex = StringUtil.getSymMarkIndex("(", ")", matchedConvert, 0);
		// 得到最后一个转换器中的参数
		String convertParam = matchedConvert.substring(matchedConvert.indexOf("(") + 1, sysMarkIndex);
		String convertName = matchedConvert.substring(1, matchedConvert.indexOf("("));
		String convertStr = matchedConvert.substring(0, sysMarkIndex + 1);
		// 调用转换器进行计算
		AbstractConvert convert = ConvertUtil.getConvert(convertName);
		Object obj = convert.convert(convertParam);
		// 最外层是转换器，则将转结果直接以对象方式返回
		if (hasConvertStr.trim().equals(convertStr.trim()))
			return obj;
		String convertResult = (obj == null) ? "" : obj.toString();
		hasConvertStr = StringUtil.replaceStr(hasConvertStr, convertStr, convertResult, subIndexCount - 1);
		return replaceConvert(hasConvertStr);
	}

	/**
	 * @todo 转换器中参数动态构造设置
	 * @param obj
	 * @param params
	 */
	public static String jsonParamSet(Object obj, Object param) throws Exception {
		String result = "";
		if (param != null) {
			String paramStr = param.toString();
			int argsEndIndex = paramStr.lastIndexOf("}");
			if (argsEndIndex == -1 || argsEndIndex != paramStr.trim().length() - 1)
				return paramStr;
			if (paramStr.indexOf("{") == -1)
				return paramStr;
			// 逆向取对称符号
			int argsBeginIndex = StringUtil.getSymMarkReverseIndex("{", "}", paramStr, argsEndIndex);
			result = paramStr.substring(0, argsBeginIndex);
			if (result.trim().lastIndexOf(",") != -1)
				result = result.substring(0, result.lastIndexOf(","));

			String[] params = paramStr.substring(argsBeginIndex + 1, argsEndIndex).split(",");
			String[] paramValue;
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					paramValue = params[i].split(":");
					if (paramValue.length > 1)
						BeanUtils.setProperty(obj, paramValue[0].trim(), paramValue[1].trim());
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @todo <b>判断匹配的字符串是否是转换器</b>
	 * @author zhongxuchen
	 * @date 2011-6-10 下午12:01:47
	 * @param matchedStr
	 * @param isStart
	 * @return
	 */
	private static boolean isConvert(String matchedStr, boolean isStart) {
		int index;
		for (int i = 0; i < allConvertName.size(); i++) {
			index = matchedStr.indexOf(allConvertName.get(i).toString());
			if (index == 0 || !isStart) {
				return true;
			}
		}
		return false;
	}

	public static HashMap parseParam(String param) {
		HashMap resultMap = new HashMap();
		if (param != null) {
			String result;
			String paramStr = param.toString();
			int argsEndIndex = paramStr.lastIndexOf("}");
			// 逆向取对称符号
			int argsBeginIndex = StringUtil.getSymMarkReverseIndex("{", "}", paramStr, argsEndIndex);
			result = paramStr.substring(0, argsBeginIndex);
			if (result.trim().lastIndexOf(",") != -1)
				result = result.substring(0, result.lastIndexOf(","));
			String[] params = paramStr.substring(argsBeginIndex + 1, argsEndIndex).split(",");
			String[] paramValue;
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					paramValue = params[i].split(":");
					if (paramValue.length > 1)
						resultMap.put(paramValue[0].trim(), paramValue[1].trim());
				}
			}
		}
		return resultMap;
	}

	/**
	 * 
	 * @todo <b>重置转换器</b>
	 * @author chenrenfei
	 * @date 2012-4-25 下午2:31:00
	 */
	public static void resetConvert() {
		if (convertInstanceMap != null && !convertInstanceMap.isEmpty()) {
			Map.Entry entry;
			for (Iterator iter = convertInstanceMap.entrySet().iterator(); iter.hasNext();) {
				entry = (Map.Entry) iter.next();
				((AbstractConvert) entry.getValue()).reset();
			}
		}
	}
}
