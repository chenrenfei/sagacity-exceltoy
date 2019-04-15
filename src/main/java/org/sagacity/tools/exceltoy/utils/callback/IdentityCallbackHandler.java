/**
 * 
 */
package org.sagacity.tools.exceltoy.utils.callback;

/**
 *@project sagacity-core
 *@description 获取唯一性标识反调抽象类
 *@author chenrenfei $<a href="mailto:zhongxuchen@hotmail.com">联系作者</a>$
 *@version $id:IdentityCallbackHandler.java,Revision:v1.0,Date:2010-12-16
 *          下午11:03:08 $
 */
public abstract class IdentityCallbackHandler {
	public abstract Object getIdentity(Object row);
}
