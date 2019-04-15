/**
 * 
 */
package org.sagacity.tools.exceltoy.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import freemarker.cache.FileTemplateLoader;
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
	 * 
	 * @todo 将字符串模版处理后以字符串输出
	 * @param keys
	 * @param templateData
	 * @param templateStr
	 * @return
	 */
	public String create(String[] keys, Object[] templateData, String templateStr) {
		if (keys == null || templateData == null)
			return null;
		String result = null;
		StringWriter writer = null;
		try {
			init();
			StringTemplateLoader templateLoader = new StringTemplateLoader();
			templateLoader.putTemplate("string_template", templateStr);
			cfg.setTemplateLoader(templateLoader);
			Template template = null;
			if (StringUtil.isNotBlank(this.encoding))
				template = cfg.getTemplate("string_template", this.encoding);
			else
				template = cfg.getTemplate("string_template");

			Map root = new HashMap();
			for (int i = 0; i < keys.length; i++) {
				root.put(keys[i], templateData[i]);
			}
			writer = new StringWriter();
			template.process(root, writer);
			writer.flush();
			result = writer.getBuffer().toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
			writer = null;
		}
		return result;
	}

	public String create(Map root, String templateStr) {
		if (root == null)
			return null;
		String result = null;
		StringWriter writer = null;
		try {
			init();
			StringTemplateLoader templateLoader = new StringTemplateLoader();
			templateLoader.putTemplate("string_template", templateStr);
			cfg.setTemplateLoader(templateLoader);
			Template template = null;
			if (StringUtil.isNotBlank(this.encoding))
				template = cfg.getTemplate("string_template", this.encoding);
			else
				template = cfg.getTemplate("string_template");
			writer = new StringWriter();
			template.process(root, writer);
			writer.flush();
			result = writer.getBuffer().toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
			writer = null;
		}
		return result;
	}

	/**
	 * @todo <b>将模板和数据结合产生到目的文件中</b>
	 * @param keys
	 * @param templateData
	 * @param templatePath
	 * @param templateFile
	 * @param distFile
	 */
	public void create(String[] keys, Object[] templateData, String templatePath, String templateFile,
			String distFile) {
		if (keys == null || templateData == null)
			return;
		FileOutputStream fout = null;
		Writer writer = null;
		try {
			init();
			FileTemplateLoader templateLoader = new FileTemplateLoader(new File(templatePath));
			cfg.setTemplateLoader(templateLoader);
			Template template = cfg.getTemplate(templateFile);
			if (StringUtil.isNotBlank(this.encoding))
				template.setEncoding(this.encoding);

			Map root = new HashMap();
			for (int i = 0; i < keys.length; i++) {
				root.put(keys[i], templateData[i]);
			}

			fout = new FileOutputStream(distFile);

			if (StringUtil.isNotBlank(this.encoding))
				writer = new BufferedWriter(new OutputStreamWriter(fout, this.encoding));
			else
				writer = new BufferedWriter(new OutputStreamWriter(fout));

			logger.info("generate file " + distFile);
			template.process(root, writer);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
			fout = null;
			writer = null;
		}
	}

	/**
	 * @todo <b>将模板和数据结合产生到目的文件中</b>
	 * @param keys
	 * @param templateData
	 * @param templateFile
	 * @param distFile
	 */
	public void create(String[] keys, Object[] templateData, File templateFile, String distFile) {
		create(keys, templateData, templateFile.getParent(), templateFile.getName(), distFile);
	}

	/**
	 * @todo <b>将模板和数据结合产生到目的文件中</b>
	 * @param keys
	 * @param templateData
	 * @param templatePath
	 * @param templateFile
	 * @param out
	 */
	public void create(String[] keys, Object[] templateData, String templatePath, String templateFile,
			OutputStream out) {
		if (keys == null || templateData == null)
			return;
		Writer writer = null;
		try {
			init();
			FileTemplateLoader templateLoader = new FileTemplateLoader(new File(templatePath));
			cfg.setTemplateLoader(templateLoader);
			Template template = cfg.getTemplate(templateFile);
			if (StringUtil.isNotBlank(this.encoding))
				template.setEncoding(this.encoding);

			Map root = new HashMap();
			for (int i = 0; i < keys.length; i++) {
				root.put(keys[i], templateData[i]);
			}

			if (StringUtil.isNotBlank(this.encoding))
				writer = new BufferedWriter(new OutputStreamWriter(out, this.encoding));
			else
				writer = new BufferedWriter(new OutputStreamWriter(out));

			template.process(root, writer);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
			writer = null;
		}
	}

	/**
	 * @todo <b>将模板和数据结合产生到目的文件中</b>
	 * @param keys
	 * @param templateData
	 * @param templateStr
	 * @param distFile
	 */
	public void create(String[] keys, Object[] templateData, String templateStr, String distFile) {
		if (keys == null || templateData == null)
			return;
		Writer writer = null;
		FileOutputStream out = null;
		try {
			init();
			StringTemplateLoader templateLoader = new StringTemplateLoader();
			templateLoader.putTemplate("template", templateStr);
			cfg.setTemplateLoader(templateLoader);
			Template template = cfg.getTemplate("template");
			if (StringUtil.isNotBlank(this.encoding))
				template.setEncoding(this.encoding);
			Map root = new HashMap();
			for (int i = 0; i < keys.length; i++) {
				root.put(keys[i], templateData[i]);
			}

			out = new FileOutputStream(distFile);

			if (StringUtil.isNotBlank(this.encoding))
				writer = new BufferedWriter(new OutputStreamWriter(out, this.encoding));
			else
				writer = new BufferedWriter(new OutputStreamWriter(out));
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

			if (StringUtil.isNotBlank(this.encoding))
				writer = new BufferedWriter(new OutputStreamWriter(out, this.encoding));
			else
				writer = new BufferedWriter(new OutputStreamWriter(out));
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
	 * @todo <b>将流模版结合数据并以字符串输出</b>
	 * @param keys
	 * @param templateData
	 * @param is
	 * @return
	 */
	public String create(String[] keys, Object[] templateData, InputStream is) {
		if (keys == null || templateData == null)
			return null;
		String result = null;
		StringWriter writer = null;
		try {
			init();
			StringTemplateLoader templateLoader = new StringTemplateLoader();
			templateLoader.putTemplate("template", IOUtil.inputStream2String(is, this.encoding));
			cfg.setTemplateLoader(templateLoader);
			Template template = cfg.getTemplate("template");
			if (StringUtil.isNotBlank(this.encoding))
				template.setEncoding(this.encoding);
			Map root = new HashMap();
			for (int i = 0; i < keys.length; i++) {
				root.put(keys[i], templateData[i]);
			}
			writer = new StringWriter();
			template.process(root, writer);
			writer.flush();
			result = writer.getBuffer().toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
			writer = null;
		}
		return result;
	}

	/**
	 * @todo <b>将模板和数据结合产生到目的文件中</b>
	 * @param keys
	 * @param templateData
	 * @param is
	 * @param out
	 */
	public void create(String[] keys, Object[] templateData, InputStream is, OutputStream out) {
		if (keys == null || templateData == null)
			return;
		Writer writer = null;
		try {
			init();
			StringTemplateLoader templateLoader = new StringTemplateLoader();
			templateLoader.putTemplate("template", IOUtil.inputStream2String(is, this.encoding));
			cfg.setTemplateLoader(templateLoader);
			Template template = cfg.getTemplate("template");
			if (StringUtil.isNotBlank(this.encoding))
				template.setEncoding(this.encoding);
			Map root = new HashMap();
			for (int i = 0; i < keys.length; i++) {
				root.put(keys[i], templateData[i]);
			}

			if (StringUtil.isNotBlank(this.encoding))
				writer = new BufferedWriter(new OutputStreamWriter(out, this.encoding));
			else
				writer = new BufferedWriter(new OutputStreamWriter(out));
			template.process(root, writer);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
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
			cfg = new Configuration();
			if (StringUtil.isNotBlank(this.encoding))
				cfg.setDefaultEncoding(this.encoding);
		}
	}

	// public static void main(String[] args) {
	// String template="你好${${name}}，你的收益率为0.7%";
	// Map root=new HashMap();
	// root.put("name", "陈仁飞");
	// root.put("陈仁飞", "陈仁飞是好人");
	// System.err.println(TemplateGenerator.getInstance().create(root, template));
	//
	//
	// /*
	// * SmsVO smsVO = new SmsVO(); smsVO.setContent("测试");
	// * smsVO.setCreateDate("20080827"); List MobileVOs = new ArrayList();
	// * MobileVO mobileVO = new MobileVO();
	// * mobileVO.setMobileNo("1397777777"); MobileVOs.add(mobileVO); MobileVO
	// * mobileVO1 = new MobileVO(); mobileVO1.setMobileNo("1397888888");
	// * MobileVOs.add(mobileVO1); smsVO.setMobileNos(MobileVOs);
	// * TemplateGenerator.create(new String[] { "smsVO" }, new Object[] {
	// * smsVO }, "D:/workspace/nantian/abchina/docs", "smsTemplate.ftl",
	// * "D:/workspace/nantian/abchina/docs/smsList.txt");
	// */
	// }
}
