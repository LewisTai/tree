package org.lewis.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.lewis.fs.DB;
import org.lewis.fs.FSException;
import org.lewis.fs.FSFile;
import org.lewis.fs.FSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * 基于MongoDB的GridFS的文件系统实现
 * 
 * @author taiqichao
 * 
 */
@Service("fsService")
public class GridFServiceImpl implements FSService {

	@Autowired
	@Qualifier("documentDBMongoTemplate")
	private MongoTemplate documentDBMongoTemplate;
	
	@Autowired
	@Qualifier("imageDBMongoTemplate")
	private MongoTemplate imageDBMongoTemplate;


	private GridFS getFS(DB db) {
		if(DB.DOCUMENT.equals(db)){
			return new GridFS(documentDBMongoTemplate.getDb());
		}else if(DB.IMAGE.equals(db)){
			return new GridFS(imageDBMongoTemplate.getDb());
		}
		throw new RuntimeException("未知的文件数据库");
	}

	public FSFile saveFile(DB db,String fileName,InputStream in) throws FSException {
		// 保存至DB
		GridFSInputFile gfsf = getFS(db).createFile(in, fileName);
		gfsf.save();
		return getFile(db,fileName);
	}

	public void getFile(DB db,String fileName, OutputStream out) throws FSException {
		List<GridFSDBFile> fileList = getFS(db).find(fileName);
		if (null != fileList && fileList.size() > 0) {
			GridFSDBFile gridFSDBFile = fileList.get(0);
			try {
				gridFSDBFile.writeTo(out);
			} catch (IOException e) {
				throw new FSException(e);
			} finally {
				IOUtils.closeQuietly(out);
			}
		} else {
			throw new FSException();
		}

	}

	public FSFile getFile(DB db,String fileName) {
		List<GridFSDBFile> fileList = getFS(db).find(fileName);
		if (fileList != null && fileList.size() > 0) {
			GridFSDBFile gfsf = fileList.get(0);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				gfsf.writeTo(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			FSFile fsFile = new FSFile(gfsf.getFilename(),
					gfsf.getContentType(), gfsf.getLength(),
					gfsf.getUploadDate(), out.toByteArray());
			return fsFile;
		}
		return null;
	}

	public void removeFile(DB db,String fileName) throws FSException {
		getFS(db).remove(fileName);
	}

}
