/**
 * 
 */
package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;
import org.sagacity.tools.exceltoy.model.TreeTableModel;
import org.sagacity.tools.exceltoy.utils.ConvertUtil;
import org.sagacity.tools.exceltoy.utils.DBHelper;

/**
 * @project sagacity-tools
 * @description 设置树形表的nodeLevel和nodeRoute值
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:TreeRouteConvert.java,Revision:v1.0,Date:2010-10-7
 */
public class TreeRouteConvert extends AbstractConvert {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4249910790903521193L;

	/**
	 * table名称
	 */
	private String tableName;

	/**
	 * 父根id
	 */
	private Object rootId;

	/**
	 * id对应字段
	 */
	private String idField;

	/**
	 * 父id对应字段
	 */
	private String pidField;

	/**
	 * 节点路径字段
	 */
	private String nodeRouteField = "NODE_ROUTE";

	/**
	 * 节点层级字段
	 */
	private String nodeLevelField = "NODE_LEVEL";

	/**
	 * 记录id的数据类型，一般是字符和数字两种类型
	 */
	private boolean isChar = false;

	/**
	 * 叶子节点字段
	 */
	private String leafField = "IS_LEAF";

	/**
	 * 叶子值
	 */
	private String leafValue = "1";

	/**
	 * 树干的值
	 */
	private String trunkValue = "0";

	/**
	 * 节点id的数据长度(小于主键长度表示不需要补零或补空白)
	 */
	private int size = 1;

	/**
	 * 附加条件
	 */
	private String conditions;

	/**
	 * 节点路径分割符号
	 */
	private String splitSign = ",";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sagacity.tools.exceltoy.convert.AbstractConvert#convert(java .lang
	 * .Object)
	 */
	public Object convert(Object param) throws Exception {
		param = super.replaceParams(param);
		ConvertUtil.jsonParamSet(this, param);
		TreeTableModel treeTableModel = new TreeTableModel();
		treeTableModel.idField(this.idField);
		treeTableModel.rootId(this.rootId);
		treeTableModel.pidField(this.pidField);
		treeTableModel.table(this.tableName);
		treeTableModel.nodeLevelField(this.nodeLevelField);
		treeTableModel.nodeRouteField(this.nodeRouteField);
		treeTableModel.idTypeIsChar(this.isChar);
		treeTableModel.isLeafField(this.leafField);

		treeTableModel.idLength(this.size);
		treeTableModel.setSplitSign(this.splitSign);
		treeTableModel.setConditions(this.conditions);
		DBHelper.wrapTreeTableRoute(treeTableModel);
		return "";
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the rootId
	 */
	public Object getRootId() {
		return rootId;
	}

	/**
	 * @param rootId
	 *            the rootId to set
	 */
	public void setRootId(Object rootId) {
		this.rootId = rootId;
	}

	/**
	 * @return the idField
	 */
	public String getIdField() {
		return idField;
	}

	/**
	 * @param idField
	 *            the idField to set
	 */
	public void setIdField(String idField) {
		this.idField = idField;
	}

	/**
	 * @return the pidField
	 */
	public String getPidField() {
		return pidField;
	}

	/**
	 * @param pidField
	 *            the pidField to set
	 */
	public void setPidField(String pidField) {
		this.pidField = pidField;
	}

	/**
	 * @return the nodeRouteField
	 */
	public String getNodeRouteField() {
		return nodeRouteField;
	}

	/**
	 * @param nodeRouteField
	 *            the nodeRouteField to set
	 */
	public void setNodeRouteField(String nodeRouteField) {
		this.nodeRouteField = nodeRouteField;
	}

	/**
	 * @return the nodeLevelField
	 */
	public String getNodeLevelField() {
		return nodeLevelField;
	}

	/**
	 * @param nodeLevelField
	 *            the nodeLevelField to set
	 */
	public void setNodeLevelField(String nodeLevelField) {
		this.nodeLevelField = nodeLevelField;
	}

	/**
	 * @return the isChar
	 */
	public boolean getIsChar() {
		return isChar;
	}

	/**
	 * @param isChar
	 *            the isChar to set
	 */
	public void setIsChar(boolean isChar) {
		this.isChar = isChar;
	}

	/**
	 * @return the leafField
	 */
	public String getLeafField() {
		return leafField;
	}

	/**
	 * @param leafField
	 *            the leafField to set
	 */
	public void setLeafField(String leafField) {
		this.leafField = leafField;
	}

	/**
	 * @return the leafValue
	 */
	public String getLeafValue() {
		return leafValue;
	}

	/**
	 * @param leafValue
	 *            the leafValue to set
	 */
	public void setLeafValue(String leafValue) {
		this.leafValue = leafValue;
	}

	/**
	 * @return the trunkValue
	 */
	public String getTrunkValue() {
		return trunkValue;
	}

	/**
	 * @param trunkValue
	 *            the trunkValue to set
	 */
	public void setTrunkValue(String trunkValue) {
		this.trunkValue = trunkValue;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the conditions
	 */
	public String getConditions() {
		return conditions;
	}

	/**
	 * @param conditions
	 *            the conditions to set
	 */
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	/**
	 * @return the splitSign
	 */
	public String getSplitSign() {
		return splitSign;
	}

	/**
	 * @param splitSign
	 *            the splitSign to set
	 */
	public void setSplitSign(String splitSign) {
		this.splitSign = splitSign;
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
	public TreeRouteConvert clone() {
		try {
			// TODO Auto-generated method stub
			return (TreeRouteConvert) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
