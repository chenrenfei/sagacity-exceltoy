/**
 * @Copyright 2008 版权归陈仁飞，不要肆意侵权抄袭，如引用请注明出处保留作者信息。
 */
package org.sagacity.tools.exceltoy.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @project sagacity-core
 * @description 输入输出IO工具类
 * @author chenrenfei $<a href="mailto:zhongxuchen@hotmail.com">联系作者</a>$
 * @version $id:IOUtil.java,Revision:v1.0,Date:2008-12-14 下午07:53:54 $
 */
@SuppressWarnings("rawtypes")
public class IOUtil {
	/**
	 * 定义日志
	 */
	private final static Logger logger = LogManager.getLogger(IOUtil.class);

	/**
	 * 转换InputStream为String
	 * 
	 * @param is
	 * @param encoding
	 * @return
	 */
	public static String inputStream2String(InputStream is, String encoding) {
		if (null == is)
			return null;
		StringBuilder buffer = new StringBuilder();
		BufferedReader in = null;
		try {
			if (StringUtil.isNotBlank(encoding)) {
				in = new BufferedReader(new InputStreamReader(is, encoding));
			} else {
				in = new BufferedReader(new InputStreamReader(is));
			}
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
				buffer.append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			closeQuietly(in);
		}
		return buffer.toString();
	}

	/**
	 * @todo 将inputStream转换成byte数组
	 * @param is
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static byte[] getBytes(InputStream is) throws Exception {
		if (is == null)
			return null;
		// 避免空流
		if (is.available() == 0)
			return new byte[] {};
		byte[] data = null;
		Collection chunks = new ArrayList();
		byte[] buffer = new byte[1024 * 1000];
		int read = -1;
		int size = 0;
		while ((read = is.read(buffer)) != -1) {
			if (read > 0) {
				byte[] chunk = new byte[read];
				System.arraycopy(buffer, 0, chunk, 0, read);
				chunks.add(chunk);
				size += chunk.length;
			}
		}
		if (size > 0) {
			ByteArrayOutputStream bos = null;
			try {
				bos = new ByteArrayOutputStream(size);
				for (Iterator itr = chunks.iterator(); itr.hasNext();) {
					byte[] chunk = (byte[]) itr.next();
					bos.write(chunk);
				}
				data = bos.toByteArray();
			} finally {
				closeQuietly(bos);
			}
		}
		return data;
	}

	/**
	 * 关闭一个或多个流对象
	 * 
	 * @param closeables 可关闭的流对象列表
	 * @throws IOException
	 */
	public static void close(Closeable... closeables) throws IOException {
		if (closeables != null) {
			for (Closeable closeable : closeables) {
				if (closeable != null) {
					closeable.close();
				}
			}
		}
	}

	/**
	 * 关闭一个或多个流对象
	 * 
	 * @param closeables 可关闭的流对象列表
	 */
	public static void closeQuietly(Closeable... closeables) {
		try {
			close(closeables);
		} catch (IOException e) {
			// do nothing
		}
	}
}
