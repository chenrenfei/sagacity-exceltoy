/**
 * 
 */
package org.sagacity.tools.exceltoy.utils.callback;

/**
 *@project sagacity-core 
 *@description 剔除树数据中没有叶子节点的接口
 *@author chenrenfei $<a href="mailto:zhongxuchen@hotmail.com">联系作者</a>$
 *@version $id:TreeRemoveCallbackHandler.java,Revision:v1.0,Date:2010-12-16 下午12:50:33 $
 */
public abstract class TreeRemoveCallbackHandler {
	/**
	 * 
	 *@todo 获取id/pid/isLeaf/delLeafFlag
	 *@param obj
	 *@return
	 */
	public abstract Object[] getIdPidLeafAndDelLeafCondition(Object obj);
}
