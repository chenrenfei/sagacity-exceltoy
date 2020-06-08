/**
 * 
 */
package org.sagacity.tools.exceltoy.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @project sagacity-core
 * @description 基于freemarker的模版工具引擎，提供日常项目中模版和数据对象的结合处理
 * @author zhongxuchen <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:TemplateGenerator.java,Revision:v1.0,Date:2008-11-24 下午11:07:07
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TemplateGenerator {
	/**
	 * 定义全局日志
	 */
	private final static Logger logger = LogManager.getLogger(TemplateGenerator.class);
	private static Configuration cfg = null;

	public static TemplateGenerator me = new TemplateGenerator();

	public static TemplateGenerator getInstance() {
		return me;
	}

	/**
	 * 编码格式，默认utf-8
	 */
	private String encoding = "UTF-8";

	/**
	 * 设置编码格式
	 * 
	 * @param encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @todo <b>将模板和数据结合产生到目的文件中</b>
	 * @param keys
	 * @param templateData
	 * @param is
	 * @param distFile
	 */
	public void create(String[] keys, Object[] templateData, InputStream is, String distFile) {
		if (keys == null || templateData == null)
			return;
		FileOutputStream out = null;
		Writer writer = null;
		try {
			init();
			StringTemplateLoader templateLoader = new StringTemplateLoader();
			templateLoader.putTemplate("template", IOUtil.inputStream2String(is, this.encoding));
			cfg.setTemplateLoader(templateLoader);
			Template template = cfg.getTemplate("template");

			Map root = new HashMap();
			for (int i = 0; i < keys.length; i++) {
				root.put(keys[i], templateData[i]);
			}

			out = new FileOutputStream(distFile);

			if (StringUtil.isNotBlank(this.encoding)) {
				writer = new BufferedWriter(new OutputStreamWriter(out, this.encoding));
			} else {
				writer = new BufferedWriter(new OutputStreamWriter(out));
			}
			logger.info("generate file " + distFile);
			template.process(root, writer);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
			out = null;
			writer = null;
		}
	}

	/**
	 * 销毁实例
	 */
	public static void destory() {
		cfg = null;
	}

	public void init() {
		if (cfg == null) {
			cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			if (StringUtil.isNotBlank(this.encoding)) {
				cfg.setDefaultEncoding(this.encoding);
			}
		}
	}
}
