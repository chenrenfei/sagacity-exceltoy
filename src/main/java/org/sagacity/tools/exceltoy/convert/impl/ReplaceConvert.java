/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.StringUtil;

/**
 * @project sagacity-tools
 * @description 字符串替换
 * @author chenrf <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ReplaceConvert.java,Revision:v1.0,Date:2010-1-5
 */
public class ReplaceConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8382131975487934694L;

	/**
	 * 需要被替换的内容
	 */
	private String regex;

	/**
	 * 分割符号
	 */
	private String splitSign = null;

	/**
	 * 替换内容
	 */
	private String replacement;

	/**
	 * 是否全部替换
	 */
	private boolean all = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java .lang
	 * .Object)
	 */
	public Object convert(Object param) throws Exception {
		if (param == null) {
			return null;
		}
		param = super.replaceParams(param);
		String result = ConvertUtil.jsonParamSet(this, param);
		if (splitSign == null) {
			if (all) {
				result = StringUtil.replaceAllStr(result, regex, replacement);
			} else {
				result = StringUtil.replaceStr(result, regex, replacement);
			}
		} else {
			String[] replaceSource = regex.split(splitSign);
			String[] replaceAry = replacement.concat(" ").split(splitSign);
			String tmp = replaceAry[replaceAry.length - 1];
			replaceAry[replaceAry.length - 1] = tmp.substring(0, tmp.length() - 1);
			if (replaceSource != null && replaceSource.length > 0) {
				boolean singleRep = (replaceAry.length == replaceSource.length);
				for (int i = 0; i < replaceSource.length; i++) {
					if (all) {
						result = StringUtil.replaceAllStr(result, replaceSource[i],
								singleRep ? replaceAry[i] : replacement);
					} else {
						result = StringUtil.replaceStr(result, replaceSource[i],
								singleRep ? replaceAry[i] : replacement);
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param regex the regex to set
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}

	/**
	 * @param replacement the replacement to set
	 */
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	/**
	 * @param all the all to set
	 */
	public void setAll(boolean all) {
		this.all = all;
	}

	/**
	 * @param splitSign the splitSign to set
	 */
	public void setSplitSign(String splitSign) {
		this.splitSign = splitSign;
	}

	public void setSplit(String splitSign) {
		this.splitSign = splitSign;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#reset()
	 */
	@Override
	public void reset() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public ReplaceConvert clone() {
		try {
			return (ReplaceConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
