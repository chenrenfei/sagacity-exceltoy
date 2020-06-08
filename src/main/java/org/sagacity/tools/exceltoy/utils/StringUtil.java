/**
 * @Copyright 2007 版权归陈仁飞，不要肆意侵权抄袭，如引用请注明出处保留作者信息。
 */
package org.sagacity.tools.exceltoy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @project sagacity-core
 * @description 字符串处理常用功能
 * @author zhongxuchen <a href="mailto:zhongxuchen@gmail.com">联系作者</a>
 * @version id:StringUtil.java,Revision:v1.0,Date:Oct 19, 2007 10:09:42 AM
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StringUtil {
	/**
	 * 字符串中包含中文的表达式
	 */
	private static Pattern chinaPattern = Pattern.compile("[\u4e00-\u9fa5]");

	/**
	 * 单引号匹配正则表达式
	 */
	private static Pattern quotaPattern = Pattern.compile("(^')|([^\\\\]')");

	/**
	 * 双引号匹配正则表达式
	 */
	private static Pattern twoQuotaPattern = Pattern.compile("(^\")|([^\\\\]\")");

	/**
	 * private constructor,cann't be instantiated by other class 私有构造函数方法防止被实例化
	 */
	private StringUtil() {
	}

	public static boolean isNotBlank(Object str) {
		return !isBlank(str);
	}

	public static boolean isBlank(Object str) {
		if (null == str || str.toString().trim().equals(""))
			return true;
		return false;
	}

	public static String trim(String str) {
		if (str == null)
			return null;
		return str.trim();
	}

	/**
	 * @todo 将对象转为字符串排除null
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		if (null == obj)
			return "";
		return obj.toString();
	}

	/**
	 * @todo 替换换行、回车、tab符号;\r 换行、\t tab符合、\n 回车
	 * @param source
	 * @param target
	 * @return
	 */
	public static String clearMistyChars(String source, String target) {
		return source.replaceAll("\r", target).replaceAll("\t", target).replaceAll("\n", target);
	}

	/**
	 * @todo 将字符串转小写
	 * @param str
	 * @return
	 */
	public static String toLower(String str) {
		return (str != null) ? str.toLowerCase() : "";
	}

	/**
	 * @todo 返回第一个字符大写，其余保持不变的字符串
	 * @param sourceStr
	 * @return
	 */
	public static String firstToUpperCase(String sourceStr) {
		if (isBlank(sourceStr))
			return sourceStr;
		if (sourceStr.length() == 1)
			return sourceStr.toUpperCase();
		return sourceStr.substring(0, 1).toUpperCase().concat(sourceStr.substring(1));
	}

	/**
	 * @todo 返回第一个字符小写，其余保持不变的字符串
	 * @param sourceStr
	 * @return
	 */
	public static String firstToLowerCase(String sourceStr) {
		if (isBlank(sourceStr))
			return sourceStr;
		if (sourceStr.length() == 1)
			return sourceStr.toUpperCase();
		return sourceStr.substring(0, 1).toLowerCase().concat(sourceStr.substring(1));
	}

	/**
	 * @todo 返回第一个字符大写，其余保持不变的字符串
	 * @param sourceStr
	 * @return
	 */
	public static String firstToUpperOtherToLower(String sourceStr) {
		if (isBlank(sourceStr))
			return sourceStr;
		if (sourceStr.length() == 1)
			return sourceStr.toUpperCase();
		return sourceStr.substring(0, 1).toUpperCase().concat(sourceStr.substring(1).toLowerCase());
	}

	/**
	 * @todo 在不分大小写情况下字符所在位置
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static int indexOfIgnoreCase(String source, String pattern) {
		if (source == null || pattern == null)
			return -1;
		return source.toLowerCase().indexOf(pattern.toLowerCase());
	}

	/**
	 * @todo 不区分大小写指定字符出现最后位置
	 * @param source String
	 * @param target String
	 * @return int
	 */
	public static int lastIndexOfIgnoreCase(String source, String target) {
		if (source == null || target == null)
			return -1;
		return source.toLowerCase().lastIndexOf(target.toLowerCase());
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param index
	 * @return
	 */
	public static int indexOfIgnoreCase(String source, String target, int index) {
		if (source == null || target == null)
			return -1;
		return source.toLowerCase().indexOf(target.toLowerCase(), index);
	}

	/**
	 * @todo 字符串去掉空比较是否相等
	 * @param str1 String
	 * @param str2 String
	 * @return boolean
	 */
	public static boolean strTrimedEqual(String str1, String str2) {
		if (str1 != null && str2 != null)
			return str1.trim().equals(str2.trim());
		return str1.equals(str2);
	}

	/**
	 * @todo 左补零
	 * @param source
	 * @param length
	 * @return
	 */
	public static String addLeftZero2Len(String source, int length) {
		return addSign2Len(source, length, 0, 0);
	}

	/**
	 * @todo 右补零
	 * @param source
	 * @param length
	 * @return
	 */
	public static String addRightZero2Len(String source, int length) {
		return addSign2Len(source, length, 0, 1);
	}

	/**
	 * @todo 用空字符给字符串补足不足指定长度部分
	 * @param source
	 * @param length
	 * @return
	 */
	public static String addRightBlank2Len(String source, int length) {
		return addSign2Len(source, length, 1, 1);
	}

	/**
	 * @todo 用空字符给字符串补足不足指定长度部分
	 * @param source
	 * @param length
	 * @return
	 */
	public static String addLeftBlank2Len(String source, int length) {
		return addSign2Len(source, length, 1, 0);
	}

	/**
	 * @param source
	 * @param length
	 * @param flag
	 * @param leftOrRight
	 * @return
	 */
	private static String addSign2Len(String source, int length, int flag, int leftOrRight) {
		if (source == null || source.length() >= length)
			return source;
		int addSize = length - source.length();
		StringBuilder addStr = new StringBuilder();
		// 右边
		if (leftOrRight == 1)
			addStr.append(source);
		String sign = (flag == 1) ? " " : "0";
		for (int i = 0; i < addSize; i++)
			addStr.append(sign);
		// 左边
		if (leftOrRight == 0)
			addStr.append(source);
		return addStr.toString();
	}

	/**
	 * @todo <b>用特定符号循环拼接指定的字符串</b>
	 * @date 2012-7-12 下午10:17:30
	 * @param source
	 * @param sign
	 * @param loopSize
	 * @return
	 */
	public static String loopAppendWithSign(String source, String sign, int loopSize) {
		if (loopSize == 0)
			return "";
		if (loopSize == 1)
			return source;
		StringBuilder result = new StringBuilder(source);
		for (int i = 1; i < loopSize; i++)
			result.append(sign).append(source);
		return result.toString();
	}

	/**
	 * @todo 补字符(限单字符)
	 * @param source
	 * @param sign
	 * @param size
	 * @param isLeft
	 */
	public static String appendStr(String source, String sign, int size, boolean isLeft) {
		int length = 0;
		StringBuilder addStr = new StringBuilder("");
		String tmpStr = "";
		if (source != null) {
			length = source.length();
			tmpStr = source;
		}
		if (!isLeft)
			addStr.append(tmpStr);
		for (int i = 0; i < size - length; i++) {
			addStr.append(sign);
		}
		if (isLeft)
			addStr.append(tmpStr);
		return addStr.toString();
	}

	/**
	 * @todo treeTable中使用,在字符串中每隔固定长度插入一个给定的符号
	 * @param source
	 * @param size
	 * @param sign
	 * @return
	 */
	public static String appendStrPerSize(String source, int size, String sign) {
		StringBuilder result = new StringBuilder(source);
		int loop = (source.length() - size + 1) / size;
		int signLength = sign.length();
		for (int i = 0; i < loop; i++) {
			result.insert((i + 1) * size + i * signLength, sign);
		}
		return result.toString();
	}

	/**
	 * @todo 对字符进行乱序处理
	 * @param result
	 * @param chars
	 */
	public static void mixChars(StringBuffer result, char[] chars) {
		if (chars != null && chars.length > 0) {
			int pos = (int) Math.floor(Math.random() * chars.length);
			if (result == null)
				result = new StringBuffer();
			result.append(chars[pos]);
			if (chars.length > 1) {
				char[] tmp = new char[chars.length - 1];
				int index = 0;
				for (int i = 0; i < chars.length; i++) {
					if (i != pos) {
						tmp[index] = chars[i];
						index++;
					}
				}
				mixChars(result, tmp);
			}
		}
	}

	/**
	 * @todo 将流转成StringBuffer
	 * @param is
	 * @return
	 */
	public static StringBuffer inputStream2Buffer(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line = "";
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb;
	}

	/**
	 * @todo 针对jdk1.4 replace(char,char)提供jdk1.5中replace(String,String)的功能
	 * @param source
	 * @param template
	 * @param target
	 * @return
	 */
	public static String replaceStr(String source, String template, String target) {
		return replaceStr(source, template, target, 0);
	}

	public static String replaceStr(String source, String template, String target, int fromIndex) {
		if (source == null)
			return null;
		if (template == null)
			return source;
		if (fromIndex >= source.length() - 1)
			return source;
		int index = source.indexOf(template, fromIndex);
		if (index != -1) {
			source = source.substring(0, index).concat(target).concat(source.substring(index + template.length()));
		}
		return source;
	}

	public static String replaceStr(String source, String template, String target, int fromIndex, int endIndex) {
		if (source == null)
			return null;
		if (template == null)
			return source;
		if (endIndex >= source.length() - 1)
			return replaceStr(source, template, target, fromIndex);
		String beforeStr = (fromIndex == 0) ? "" : source.substring(0, fromIndex);
		String replaceBody = source.substring(fromIndex, endIndex + 1);
		String endStr = source.substring(endIndex + 1);
		int index = replaceBody.indexOf(template);
		if (index != -1) {
			replaceBody = replaceBody.substring(0, index).concat(target)
					.concat(replaceBody.substring(index + template.length()));
		}
		return beforeStr.concat(replaceBody).concat(endStr);
	}

	/**
	 * @todo 针对jdk1.4 replace(char,char)提供jdk1.5中replace(String,String)的功能
	 * @param source
	 * @param template
	 * @param target
	 * @return
	 */
	public static String replaceAllStr(String source, String template, String target) {
		return replaceAllStr(source, template, target, 0);
	}

	public static String replaceAllStr(String source, String template, String target, int fromIndex) {
		if (source == null || template.equals(target))
			return source;
		int index = source.indexOf(template, fromIndex);
		int subLength = target.length() - template.length();
		int begin = index - 1;
		while (index != -1 && index >= begin) {
			source = source.substring(0, index).concat(target).concat(source.substring(index + template.length()));
			begin = index + subLength + 1;
			index = source.indexOf(template, begin);
		}
		return source;
	}

	public static String replaceAllStr(String source, String template, String target, int fromIndex, int endIndex) {
		if (source == null || template.equals(target) || endIndex <= fromIndex)
			return source;
		if (endIndex >= source.length() - 1)
			return replaceAllStr(source, template, target, fromIndex);
		String beforeStr = (fromIndex == 0) ? "" : source.substring(0, fromIndex);
		String replaceBody = source.substring(fromIndex, endIndex + 1);
		String endStr = source.substring(endIndex + 1);
		int index = replaceBody.indexOf(template);
		int begin = index - 1;
		// 替换后的偏移量，避免在替换内容中再次替换形成死循环
		int subLength = target.length() - template.length();
		while (index != -1 && index >= begin) {
			replaceBody = replaceBody.substring(0, index).concat(target)
					.concat(replaceBody.substring(index + template.length()));
			begin = index + subLength + 1;
			index = replaceBody.indexOf(template, begin);
		}
		return beforeStr.concat(replaceBody).concat(endStr);
	}

	/**
	 * @todo 查询对称标记符号的位置，startIndex必须是<source.indexOf(beginMarkSign)
	 * @param beginMarkSign
	 * @param endMarkSign
	 * @param source
	 * @param startIndex
	 * @return
	 */
	public static int getSymMarkIndex(String beginMarkSign, String endMarkSign, String source, int startIndex) {
		Pattern pattern = null;
		// 单引号和双引号，排除\' 和 \"
		if (beginMarkSign.equals("'")) {
			pattern = quotaPattern;
		} else if (beginMarkSign.equals("\"")) {
			pattern = twoQuotaPattern;
		}
		// 判断对称符号是否相等
		boolean symMarkIsEqual = beginMarkSign.equals(endMarkSign) ? true : false;
		int beginSignIndex = -1;
		if (pattern == null) {
			beginSignIndex = source.indexOf(beginMarkSign, startIndex);
		} else {
			beginSignIndex = matchIndex(source, pattern, startIndex)[0];
			// 转义符号占一位,开始位后移一位
			if (beginSignIndex > startIndex) {
				beginSignIndex = beginSignIndex + 1;
			}
		}
		if (beginSignIndex == -1) {
			return source.indexOf(endMarkSign, startIndex);
		}
		int endIndex = -1;
		if (pattern == null) {
			endIndex = source.indexOf(endMarkSign, beginSignIndex + 1);
		} else {
			endIndex = matchIndex(source, pattern, beginSignIndex + 1)[0];
			// 转义符号占一位,开始位后移一位
			if (endIndex > beginSignIndex + 1) {
				endIndex = endIndex + 1;
			}
		}
		int preEndIndex = 0;
		while (endIndex > beginSignIndex) {
			// 寻找下一个开始符号
			if (pattern == null) {
				beginSignIndex = source.indexOf(beginMarkSign, (symMarkIsEqual ? endIndex : beginSignIndex) + 1);
			} else {
				beginSignIndex = matchIndex(source, pattern, endIndex + 1)[0];
				// 转义符号占一位,开始位后移一位
				if (beginSignIndex > endIndex + 1) {
					beginSignIndex = beginSignIndex + 1;
				}
			}

			// 找不到或则下一个开始符号位置大于截止符号则返回
			if (beginSignIndex == -1 || beginSignIndex > endIndex) {
				return endIndex;
			}
			// 记录上一个截止位置
			preEndIndex = endIndex;
			// 开始符号在截止符号前则寻找下一个截止符号
			if (pattern == null) {
				endIndex = source.indexOf(endMarkSign, (symMarkIsEqual ? beginSignIndex : endIndex) + 1);
			} else {
				endIndex = matchIndex(source, pattern, beginSignIndex + 1)[0];
				// 转义符号占一位,开始位后移一位
				if (endIndex > beginSignIndex + 1) {
					endIndex = endIndex + 1;
				}
			}
			// 找不到则返回上一个截止位置
			if (endIndex == -1) {
				return preEndIndex;
			}
		}
		return endIndex;
	}

	/**
	 * @todo 根据正则表达式找对策符号位置如：select select from from ，第一个select的对称from位置需要规避内部内容
	 * @param startRegex
	 * @param endRegex
	 * @param source
	 * @param startIndex
	 * @return
	 */
	public static int getSymMarkMatchIndex(String beginMarkSign, String endMarkSign, String source, int startIndex) {
		// 判断对称符号是否相等
		boolean symMarkIsEqual = beginMarkSign.equals(endMarkSign) ? true : false;
		Pattern startP = Pattern.compile(beginMarkSign);
		Pattern endP = Pattern.compile(endMarkSign);
		int[] beginSignIndex = matchIndex(source, startP, startIndex);
		if (beginSignIndex[0] == -1) {
			return matchIndex(source, endP, startIndex)[0];
		}
		int[] endIndex = matchIndex(source, endP, beginSignIndex[1] + 1);
		int[] tmpIndex = { 0, 0 };
		while (endIndex[0] > beginSignIndex[0]) {
			// 寻找下一个开始符号
			beginSignIndex = matchIndex(source, startP, (symMarkIsEqual ? endIndex[1] : beginSignIndex[1]) + 1);
			// 找不到或则下一个开始符号位置大于截止符号则返回
			if (beginSignIndex[0] == -1 || beginSignIndex[0] > endIndex[0]) {
				return endIndex[0];
			}
			tmpIndex = endIndex;
			// 开始符号在截止符号前则寻找下一个截止符号
			endIndex = matchIndex(source, endP, (symMarkIsEqual ? beginSignIndex[1] : endIndex[1]) + 1);
			// 找不到则返回
			if (endIndex[0] == -1) {
				return tmpIndex[0];
			}
		}
		return endIndex[0];
	}

	/**
	 * @todo 逆向查询对称标记符号的位置
	 * @param beginMarkSign
	 * @param endMarkSign
	 * @param source
	 * @param endIndex
	 * @return
	 */
	public static int getSymMarkReverseIndex(String beginMarkSign, String endMarkSign, String source, int endIndex) {
		int beginIndex = source.length() - endIndex - 1;
		int index = getSymMarkIndex(endMarkSign, beginMarkSign, new StringBuffer(source).reverse().toString(),
				beginIndex);
		return source.length() - index - 1;
	}

	/**
	 * @todo 查询对称标记符号的位置
	 * @param beginMarkSign
	 * @param endMarkSign
	 * @param source
	 * @param startIndex
	 * @return
	 */
	public static int getSymMarkIndexIgnoreCase(String beginMarkSign, String endMarkSign, String source,
			int startIndex) {
		return getSymMarkIndex(beginMarkSign.toLowerCase(), endMarkSign.toLowerCase(), source.toLowerCase(),
				startIndex);
	}

	/**
	 * @todo 通过正则表达式判断是否匹配
	 * @param source
	 * @param regex
	 * @return
	 */
	public static boolean matches(String source, String regex) {
		return matches(source, Pattern.compile(regex));
	}

	/**
	 * @todo 通过正则表达式判断是否匹配
	 * @param source
	 * @param p
	 * @return
	 */
	public static boolean matches(String source, Pattern p) {
		return p.matcher(source).find();
	}

	/**
	 * @todo 找到匹配的位置
	 * @param source
	 * @param regex
	 * @return
	 */
	public static int matchIndex(String source, String regex) {
		return matchIndex(source, Pattern.compile(regex));
	}

	public static int matchIndex(String source, Pattern p) {
		Matcher m = p.matcher(source);
		if (m.find())
			return m.start();
		return -1;
	}

	public static int[] matchIndex(String source, Pattern p, int start) {
		if (source.length() <= start)
			return new int[] { -1, -1 };
		Matcher m = p.matcher(source.substring(start));
		if (m.find()) {
			return new int[] { m.start() + start, m.end() + start };
		}
		return new int[] { -1, -1 };
	}

	public static int matchLastIndex(String source, String regex) {
		return matchLastIndex(source, Pattern.compile(regex));
	}

	public static int matchLastIndex(String source, Pattern p) {
		Matcher m = p.matcher(source);
		int matchIndex = -1;
		while (m.find()) {
			matchIndex = m.start();
		}
		return matchIndex;
	}

	/**
	 * @todo 获取匹配成功的个数
	 * @param source
	 * @param regex
	 * @return
	 */
	public static int matchCnt(String source, String regex) {
		return matchCnt(source, Pattern.compile(regex));
	}

	/**
	 * @todo 获取匹配成功的个数
	 * @param Pattern
	 * @param source
	 * @return
	 */
	public static int matchCnt(String source, Pattern p) {
		Matcher m = p.matcher(source);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return count;
	}

	/**
	 * @todo 获取匹配成功的个数
	 * @param source
	 * @param regex
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public static int matchCnt(String source, String regex, int beginIndex, int endIndex) {
		return matchCnt(source.substring(beginIndex, endIndex), Pattern.compile(regex));
	}

	/**
	 * @todo 获取包含嵌套匹配成功的个数
	 * @param source
	 * @param regex
	 * @return
	 */
	public static int nestMatchCnt(String source, String regex) {
		return nestMatchCnt(source, Pattern.compile(regex));
	}

	/**
	 * @todo 获取包含嵌套匹配成功的个数
	 * @param source
	 * @param p
	 * @return
	 */
	public static int nestMatchCnt(String source, Pattern p) {
		Matcher m = p.matcher(source);
		int count = 0;
		int index = 0;
		while (m.find()) {
			count++;
			index = m.start();
			source = source.substring(index + 1);
			m = p.matcher(source);
		}
		return count;
	}

	/**
	 * @todo <b>字符串编码转换</b>
	 * @param source
	 * @param fromChartSet
	 * @param toChartSet
	 * @return
	 */
	public static String encode(String source, String fromChartSet, String toChartSet) {
		try {
			if (isNotBlank(toChartSet)) {
				if (isNotBlank(fromChartSet)) {
					return new String(source.getBytes(fromChartSet), toChartSet);
				}
				return new String(source.getBytes(), toChartSet);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return source;
	}

	/**
	 * @todo 获取字符指定次数的位置
	 * @param source
	 * @param regex
	 * @param order
	 * @return
	 */
	public static int indexOrder(String source, String regex, int order) {
		int begin = 0;
		int count = 0;
		int index = source.indexOf(regex, begin);
		while (index != -1) {
			if (count == order)
				return index;
			begin = index + 1;
			index = source.indexOf(regex, begin);
			count++;
		}
		return -1;
	}

	/**
	 * @todo 不区分大小写返回第n次出现的位置，n为order的值
	 * @param source
	 * @param regex
	 * @param order
	 * @return
	 */
	public static int indexOrderIgnoreCase(String source, String regex, int order) {
		return indexOrder(source.toLowerCase(), regex.toLowerCase(), order);
	}

	/**
	 * @todo 字符串转ASCII
	 * @param str
	 * @return
	 */
	public static int[] str2ASCII(String str) {
		char[] chars = str.toCharArray(); // 把字符中转换为字符数组
		int[] result = new int[chars.length];
		for (int i = 0; i < chars.length; i++) {// 输出结果
			result[i] = (int) chars[i];
		}
		return result;
	}

	/**
	 * @todo 切割字符串，排除特殊字符对，如a,b,c,dd(a,c),dd(a,c)不能切割
	 * @param source
	 * @param splitSign 如逗号、分号、冒号或具体字符串,非正则表达式
	 * @param filterMap
	 * @return
	 */
	public static String[] splitExcludeSymMark(String source, String splitSign, HashMap filterMap) {
		if (source == null) {
			return null;
		}
		int splitIndex = source.indexOf(splitSign);
		if (splitIndex == -1) {
			return new String[] { source };
		}
		if (filterMap == null || filterMap.isEmpty()) {
			return source.split(splitSign);
		}
		List<String[]> filters = matchFilters(source, filterMap);
		if (filters.isEmpty()) {
			return source.split(splitSign);
		}
		int start = 0;
		int skipIndex = 0;
		int preSplitIndex = splitIndex;
		ArrayList splitResults = new ArrayList();
		int max = -1;
		int[] startEnd;
		while (splitIndex != -1) {
			max = -1;
			for (String[] filter : filters) {
				startEnd = getStartEndIndex(source, filter, skipIndex, splitIndex);
				// 分隔符号整合在对称符号的首尾中间,表示分隔符号属于内部字符串,在对称符号的终止位置后面重新获取分隔符号的位置
				if (startEnd[0] >= 0 && startEnd[0] <= splitIndex && startEnd[1] >= splitIndex) {
					if (startEnd[1] > max) {
						max = startEnd[1];
					}
				}
			}
			if (max > -1) {
				skipIndex = max + 1;
				splitIndex = source.indexOf(splitSign, skipIndex);
			}
			// 分隔符号位置没有变化，表示其不在对称符号中间
			if (preSplitIndex == splitIndex) {
				// 切割分隔符前部分
				splitResults.add(source.substring(start, preSplitIndex));
				// 重新记录下一次开始切割位置
				start = preSplitIndex + 1;
				skipIndex = start;
				splitIndex = source.indexOf(splitSign, skipIndex);
				preSplitIndex = splitIndex;
			} else {
				preSplitIndex = splitIndex;
			}
		}
		splitResults.add(source.substring(start));
		String[] resultStr = new String[splitResults.size()];
		for (int j = 0; j < splitResults.size(); j++) {
			resultStr[j] = (String) splitResults.get(j);
		}
		return resultStr;
	}

	/**
	 * @TODO 获取对称符号的开始和结束位置
	 * @param source
	 * @param filter
	 * @param skipIndex
	 * @param splitIndex
	 * @return
	 */
	private static int[] getStartEndIndex(String source, String[] filter, int skipIndex, int splitIndex) {
		int[] result = { -1, -1 };
		Pattern pattern = null;
		if (filter[0].equals("'")) {
			pattern = quotaPattern;
		} else if (filter[0].equals("\"")) {
			pattern = twoQuotaPattern;
		}
		String tmp;
		if (pattern == null) {
			result[0] = source.indexOf(filter[0], skipIndex);
			if (result[0] >= 0) {
				result[1] = getSymMarkIndex(filter[0], filter[1], source, skipIndex);
			}
		} else {
			result[0] = matchIndex(source, pattern, skipIndex)[0];
			if (result[0] >= 0) {
				tmp = source.substring(result[0], result[0] + 1);
				if (!tmp.equals("'") && !tmp.equals("\"")) {
					result[0] = result[0] + 1;
				}
				result[1] = getSymMarkIndex(filter[0], filter[1], source, result[0]);
			}
		}
		while (result[1] > 0 && result[1] < splitIndex) {
			if (pattern == null) {
				// 非正则表达式,往后移动一位
				result[0] = source.indexOf(filter[0], result[1] + 1);
				if (result[0] > 0) {
					result[1] = getSymMarkIndex(filter[0], filter[1], source, result[0]);
				} else {
					result[1] = -1;
				}
			} else {
				tmp = source.substring(result[1], result[1] + 1);
				if (!tmp.equals("'") && !tmp.equals("\"")) {
					result[0] = matchIndex(source, pattern, result[1] + 2)[0];
				} else {
					result[0] = matchIndex(source, pattern, result[1] + 1)[0];
				}
				// 正则表达式有一个转义符号占一位
				if (result[0] > 0) {
					tmp = source.substring(result[0], result[0] + 1);
					if (!tmp.equals("'") && !tmp.equals("\"")) {
						result[0] = result[0] + 1;
					}
					result[1] = getSymMarkIndex(filter[0], filter[1], source, result[0]);
				} else {
					result[1] = -1;
				}
			}
		}
		return result;
	}

	/**
	 * @TODO 匹配有效的过滤器
	 * @param source
	 * @param filterMap
	 * @return
	 */
	private static List<String[]> matchFilters(String source, HashMap filterMap) {
		List<String[]> result = new ArrayList<String[]>();
		Iterator iter = filterMap.entrySet().iterator();
		String beginSign;
		String endSign;
		int beginSignIndex;
		int endSignIndex;
		Map.Entry entry;
		Pattern pattern;
		// 排除不存在的过滤对称符号
		while (iter.hasNext()) {
			entry = (Map.Entry) iter.next();
			beginSign = (String) entry.getKey();
			endSign = (String) entry.getValue();
			pattern = null;
			if (beginSign.equals("'")) {
				pattern = quotaPattern;
			} else if (beginSign.equals("\"")) {
				pattern = twoQuotaPattern;
			}
			endSignIndex = -1;
			if (pattern == null) {
				beginSignIndex = source.indexOf(beginSign);
				if (beginSignIndex > -1) {
					endSignIndex = source.indexOf(endSign, beginSignIndex + 1);
				}
			} else {
				beginSignIndex = matchIndex(source, pattern);
				// 转义符号占一位,开始位后移一位
				if (beginSignIndex > -1) {
					beginSignIndex = beginSignIndex + 1;
					endSignIndex = matchIndex(source, pattern, beginSignIndex + 1)[0];
					// 转义符号占一位,开始位后移一位
					if (endSignIndex >= beginSignIndex + 1) {
						endSignIndex = endSignIndex + 1;
					}
				}
			}
			if (beginSignIndex != -1 && endSignIndex != -1) {
				result.add(new String[] { beginSign, endSign });
			}
		}
		return result;
	}

	/**
	 * @todo 替换字符串中的参数占位符号
	 * @param source
	 * @param startSign
	 * @param endSign
	 * @param paramNames
	 * @param paramValues
	 * @return
	 */
	public static String replaceParams(String source, String startSign, String endSign, String[] paramNames,
			String[] paramValues) {
		if (null == paramValues || null == source)
			return source;
		boolean useNum = false;
		if (null == paramNames || paramNames.length == 0) {
			useNum = true;
		}
		for (int i = 0; i < paramValues.length; i++) {
			source = source.replaceAll(startSign + (useNum ? i : paramNames[i]) + endSign, paramValues[i]);
		}
		return source;
	}

	/**
	 * @todo 判断首字母是否小写
	 * @param source
	 * @return
	 */
	public static boolean firstIsLowerCase(String source) {
		return matches(source, Pattern.compile("^[a-z0-9]\\w+"));
	}

	public static String toHumpFirstUpperCase(String source) {
		return toHumpStr(source, true);
	}

	public static String toHumpFirstLowerCase(String source) {
		return toHumpStr(source, false);
	}

	/**
	 * @todo 将字符串转换成驼峰形式
	 * @param source
	 * @param firstIsUpperCase
	 * @return
	 */
	public static String toHumpStr(String source, boolean firstIsUpperCase) {
		if (isBlank(source))
			return source;
		String[] humpAry = source.trim().replace("-", "_").split("\\_");
		String cell;
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < humpAry.length; i++) {
			cell = humpAry[i];
			// 全大写或全小写
			if (cell.toUpperCase().equals(cell)) {
				result.append(firstToUpperOtherToLower(cell));
			} else {
				result.append(firstToUpperCase(cell));
			}
		}
		// 首字母变大写
		if (firstIsUpperCase)
			return firstToUpperCase(result.toString());
		return firstToLowerCase(result.toString());
	}

	/**
	 * @todo 驼峰形式字符用分割符号链接,example:humpToSplitStr("organInfo","_") result:organ_Info
	 * @param source
	 * @param split
	 * @return
	 */
	public static String humpToSplitStr(String source, String split) {
		if (source == null)
			return null;
		char[] chars = source.trim().toCharArray();
		StringBuilder result = new StringBuilder();
		int charInt;
		int uperCaseCnt = 0;
		for (int i = 0; i < chars.length; i++) {
			charInt = chars[i];
			if (charInt >= 65 && charInt <= 90) {
				uperCaseCnt++;
			} else {
				uperCaseCnt = 0;
			}
			// 连续大写
			if (uperCaseCnt == 1 && i != 0)
				result.append(split);
			result.append(Character.toString(chars[i]));
		}
		return result.toString();
	}

	/**
	 * @todo <b>提供非正则方式的字符切割</b>
	 * @param source
	 * @param splitSign
	 * @return String[]
	 */
	public static String[] split(String source, String splitSign) {
		if (null == source)
			return null;
		if (source.indexOf(splitSign) == -1)
			return new String[] { source };
		int size = splitSign.length();
		int index = source.indexOf(splitSign);
		int beginIndex = 0;
		ArrayList<String> result = new ArrayList<String>();
		String tmpStr;
		while (index != -1) {
			result.add(source.substring(beginIndex, index));
			beginIndex = index + size;
			index = source.indexOf(splitSign, index + size);
			if (index == -1) {
				tmpStr = source.substring(beginIndex);
				if (!tmpStr.equals(""))
					result.add(tmpStr);
			}
		}
		String[] array = new String[result.size()];
		result.toArray(array);
		return array;
	}

	/**
	 * @todo 判断字符串中是否包含中文
	 * @param str
	 * @return
	 */
	public static boolean isContainChinese(String str) {
		if (chinaPattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

}
