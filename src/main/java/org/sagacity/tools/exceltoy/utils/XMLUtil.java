/**
 * 
 */
package org.sagacity.tools.exceltoy.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.sagacity.tools.exceltoy.utils.callback.XMLCallbackHandler;
import org.w3c.dom.Document;

/**
 * @project sagacity-core
 * @description xml处理的工具类,提供xml对应schema validator等功能
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:XMLUtil.java,Revision:v1.0,Date:2009-4-27 上午11:57:58
 */
public class XMLUtil {
	/**
	 * 定义日志
	 */
	private final static Logger logger = LogManager.getLogger(XMLUtil.class);

	// xml 忽视验证的特性
	private final static String NO_VALIDATOR_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

	/**
	 * @todo 获取qName对应的内容
	 * @param xmlFile
	 * @param xmlQuery
	 * @param qName
	 * @return
	 * @throws Exception
	 */
	public static Object getXPathContent(File xmlFile, String xmlQuery, QName qName) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(xmlFile);
		XPathFactory pathFactory = XPathFactory.newInstance();
		XPath xpath = pathFactory.newXPath();
		XPathExpression pathExpression = xpath.compile(xmlQuery);
		return pathExpression.evaluate(doc, qName);
	}

	/**
	 * @todo 根据qName 获取节点对象
	 * @param xmlFile
	 * @param qName
	 * @return
	 * @throws Exception
	 */
	public static Object getXPathElement(File xmlFile, String qName) throws Exception {
		SAXReader saxReader = new SAXReader();
		InputStream is = new FileInputStream(xmlFile);
		org.dom4j.Document doc = saxReader.read(is);
		return doc.getRootElement().selectObject(qName);
	}

	/**
	 * @todo 获取节点集合
	 * @param xmlFile
	 * @param qName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static List getXPathElements(File xmlFile, String qName) throws Exception {
		SAXReader saxReader = new SAXReader();
		InputStream is = new FileInputStream(xmlFile);
		org.dom4j.Document doc = saxReader.read(is);
		return doc.getRootElement().elements(qName);
	}

	/**
	 * @todo 编辑xml文件
	 * @param xmlFile
	 * @param charset
	 * @param isValidator
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	public static boolean updateXML(File xmlFile, String charset, boolean isValidator, XMLCallbackHandler handler)
			throws Exception {
		if (handler == null || xmlFile == null || !xmlFile.exists())
			return false;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			SAXReader saxReader = new SAXReader();
			if (!isValidator)
				saxReader.setFeature(NO_VALIDATOR_FEATURE, false);
			if (charset != null)
				saxReader.setEncoding(charset);
			is = new FileInputStream(xmlFile);
			if (null != is) {
				org.dom4j.Document doc = saxReader.read(is);
				if (null != doc) {
					handler.process(doc, doc.getRootElement());
					OutputFormat format = OutputFormat.createPrettyPrint();
					if (charset != null)
						format.setEncoding(charset);
					fos = new FileOutputStream(xmlFile);
					XMLWriter output = new XMLWriter(fos, format);
					output.write(doc);
				}
				is.close();
			}
		} catch (Exception e) {
			logger.error("修改XML文件:{}错误:{}!", xmlFile, e.getMessage());
			throw e;
		} finally {
			if (fos != null)
				fos.close();
			if (is != null)
				is.close();
		}
		return true;
	}

	/**
	 * 读取xml文件
	 * 
	 * @param xmlFile
	 * @param charset
	 * @param isValidator
	 * @param handler
	 * @throws Exception
	 */
	public static Object readXML(Object xmlFile, String charset, boolean isValidator, XMLCallbackHandler handler)
			throws Exception {
		if (StringUtil.isBlank(xmlFile))
			return null;
		InputStream fileIS = null;
		InputStreamReader ir = null;
		try {
			SAXReader saxReader = new SAXReader();
			if (!isValidator)
				saxReader.setFeature(NO_VALIDATOR_FEATURE, false);
			if (StringUtil.isNotBlank(charset))
				saxReader.setEncoding(charset);
			if (charset != null)
				ir = new InputStreamReader(FileUtil.getFileInputStream(xmlFile), charset);
			else
				ir = new InputStreamReader(FileUtil.getFileInputStream(xmlFile));
			if (ir != null) {
				org.dom4j.Document doc = saxReader.read(ir);
				if (null != doc)
					return handler.process(doc, doc.getRootElement());
			}
		} catch (Exception e) {
			logger.error("解析文件:{}错误:{}!", xmlFile, e.getMessage());
			throw e;
		} finally {
			if (ir != null)
				ir.close();
			if (fileIS != null)
				fileIS.close();
		}
		return null;
	}
}
