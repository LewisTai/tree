package org.lewis.fs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * 文件
 * 
 * @author taiqichao
 * 
 */
public class FSFile implements Serializable{

	private static final long serialVersionUID = 6295834910298267157L;

	private String fileName;

	private String contentType;

	private long length;

	private Date uploadDate;

	private byte[] content;

	public FSFile() {
	}
	
	public FSFile(String fileName, 
			String contentType, 
			long length,
			Date uploadDate,
			InputStream in) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.length = length;
		this.uploadDate = uploadDate;
		ByteArrayOutputStream out =null;
		try {
			out=new ByteArrayOutputStream();
			IOUtils.copy(in, out);
			this.setContent(out.toByteArray());
		} catch (IOException ee) {
			ee.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
	}
	
	public FSFile(String fileName, String contentType, long length,
			Date uploadDate, byte[] content) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.length = length;
		this.uploadDate = uploadDate;
		this.content = content;
	}

	
	public InputStream getInputStream(){
		return new ByteArrayInputStream(this.content);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public String getFileExt(){
		return FilenameUtils.getExtension(this.fileName);
	}
	

}
