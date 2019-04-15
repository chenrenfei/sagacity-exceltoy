/**
 * 
 */
package org.sagacity.tools.exceltoy.utils.callback;

/**
 *@project sagacity-core
 *@description 树形结构数据处理的反调接口
 *@author chenrenfei $<a href="mailto:zhongxuchen@hotmail.com">联系作者</a>$
 *@version $id:TreeSetCallbackHandler.java,Revision:v1.0,Date:2010-9-13
 *          下午10:08:23 $
 */
public abstract class TreeSetCallbackHandler {

	/**
	 * 
	 *@todo 获取id和pid值
	 *@param obj
	 *@return
	 */
	public abstract Object[] getIdPidSet(Object obj);
}
