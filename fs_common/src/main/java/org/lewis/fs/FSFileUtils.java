package org.lewis.fs;

import java.rmi.server.UID;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;

public class FSFileUtils {

	/**
	 * 生成文档文件名
	 * 
	 * @param file
	 *            文件名
	 * @return fs文件名
	 */
	public static String generateFSFileNameForImage(String format) {
		StringBuffer str = new StringBuffer("s");
		str.append(new UID().toString().replaceAll(":", "_").replaceAll("-", "_"));
		str.append(".").append(format);
		return str.toString();
	}
	
	/**
	 * 生成图文件名
	 * 
	 * @param file
	 *            文件名
	 * @return fs文件名
	 */
	public static String generateFSFileNameForDocument(String name) {
		StringBuffer str = new StringBuffer("document-");
		str.append(UUID.randomUUID().toString()).append(".")
				.append(FilenameUtils.getExtension(name));
		return str.toString();
	}

	


}
