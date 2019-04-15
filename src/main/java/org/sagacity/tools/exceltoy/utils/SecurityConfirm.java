/**
 * 
 */
package org.sagacity.tools.exceltoy.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.sagacity.tools.exceltoy.model.DataSourceModel;
import org.sagacity.tools.exceltoy.model.TaskModel;

/**
 * @project sagacity-tools
 * @description ExcelToy 用来控制高风险任务执行，在执行高风险任务自动核实确认是否执行
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:SecurityConfirm.java,Revision:v1.0,Date:2012-4-20
 */
public class SecurityConfirm {
	/**
	 * 默认输入验证信息
	 */
	private final static String CONFIRM_MSG = "YES";

	/**
	 * 
	 * @todo <b>这里填写方法的说明</b>
	 * @author chenrenfei
	 * @date 2012-4-20 下午3:02:42
	 * @param taskId
	 * @param message
	 * @param checkMessage
	 * @return
	 */
	public static boolean confirm(TaskModel taskModel) {
		if (null == taskModel.getSecurityCode())
			return true;
		try {
			BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
			String confirmMsg = (taskModel.getSecurityCode().trim().equals("")) ? CONFIRM_MSG
					: taskModel.getSecurityCode();
			DataSourceModel dataSourceModel = DBHelper.getDataSource(taskModel.getDatasource());
			System.out.println("========*          高风险任务执行验证            * ===========");
			System.out.println("当前执行任务编号：" + taskModel.getId());
			System.out.println("任务执行安全提醒：" + taskModel.getSecurityTip());
			System.out.println("目标数据库：" + taskModel.getDatasource() + "["
					+ (dataSourceModel == null ? "" : dataSourceModel.getUrl()) + "]");
			System.out.println("禁止操作类型：" + (dataSourceModel == null ? "" : dataSourceModel.getForbid()));
			System.out.println("---------------------------------------------------------------------------");
			System.out.println("==请输入以下文字,并以回车键确认:【" + confirmMsg + "】====");
			String choose = buf.readLine();
			if (choose.equalsIgnoreCase(confirmMsg))
				return true;
			else {
				System.err.println("【################ 输入内容不相符,任务被禁止执行!  #########】");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
