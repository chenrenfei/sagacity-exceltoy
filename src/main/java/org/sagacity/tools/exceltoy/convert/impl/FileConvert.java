/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.FileUtil;

/**
 * @project sagacity-tools
 * @description 文件名称转换
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:FileConvert.java,Revision:v1.0,Date:2010-6-22
 */
public class FileConvert extends AbstractConvert {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5786835963794543815L;

	/**
	 * 文件路径
	 */
	private String filePath;

	/**
	 * 转换后存放路径
	 */
	private String distPath;

	/**
	 * 文件后缀
	 */
	private String extName = ".tmp";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java .lang
	 * .Object)
	 */
	@SuppressWarnings("static-access")
	public Object convert(Object param) throws Exception {
		if (param == null)
			return param;
		param = super.replaceParams(param);
		String randFileName = "";
		String fileName = ConvertUtil.jsonParamSet(this, param);
		String realFileName = FileUtil.linkPath(filePath, fileName);
		// 防止执行过快，获取的nanoTime一致
		Thread.currentThread().sleep(25);
		randFileName = "" + System.nanoTime() + (this.extName.indexOf(".") == 0 ? this.extName : "." + this.extName);
		if (FileUtil.copyFile(realFileName, FileUtil.linkPath(distPath, randFileName)))
			return randFileName;
		else
			return "";
	}

	/**
	 * @param filePath
	 *            the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @param distPath
	 *            the distPath to set
	 */
	public void setDistPath(String distPath) {
		this.distPath = distPath;
	}

	/**
	 * @param extName
	 *            the extName to set
	 */
	public void setExtName(String extName) {
		this.extName = extName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#reset()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public FileConvert clone() {
		try {
			// TODO Auto-generated method stub
			return (FileConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
