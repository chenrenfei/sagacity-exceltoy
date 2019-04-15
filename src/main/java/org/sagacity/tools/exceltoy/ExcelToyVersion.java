/**
 * 
 */
package org.sagacity.tools.exceltoy;

/**
 * @project sagacity-tools
 * @description 获取exceltoy版本信息
 * @author chenrenfei <a href="mailto:zhongxuchen@gmail.com">联系作者</a>
 * @version id:ExcelToyVersion.java,Revision:v1.0,Date:2010-6-10 下午10:59:16
 */
public class ExcelToyVersion {
	/**
	 * @todo 获取版本信息
	 * @return
	 */
	public static String getVersion() {
		Package pkg = ExcelToyVersion.class.getPackage();
		return (pkg != null ? pkg.getImplementationVersion() : null);
	}
}
