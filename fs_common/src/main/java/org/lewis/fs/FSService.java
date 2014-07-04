package org.lewis.fs;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 分布式文件系统接口
 * 
 * @author taiqichao
 * 
 */
public interface FSService {

	/**
	 * 存储文件
	 * 
	 * @param db
	 *            存储数据库
	
	 * @param fileName
	 *            文件名称
	 * @param in
	 *            文件输入流         
	 *            
	 * @return
	 * @throws FSException
	 */
	FSFile saveFile(DB db,String fileName,InputStream in) throws FSException;

	/**
	 * 获取文件
	 * 
	 * @param db
	 *            存储数据库
	 * @param fileName
	 *            文件名称
	 * @return
	 */
	FSFile getFile(DB db, String fileName);

	/**
	 * 获取文件以输出流输出
	 * 
	 * @param db
	 *            存储数据库
	 * 
	 * @param fileName
	 *            文件名称
	 * @param out
	 *            输出流
	 * @throws FSException
	 */
	void getFile(DB db, String fileName, OutputStream out) throws FSException;

	/**
	 * 删除文件
	 * 
	 * @param db
	 *            存储数据库
	 * @param fileName
	 *            文件名称
	 * @throws FSException
	 */
	void removeFile(DB db, String fileName) throws FSException;

}
