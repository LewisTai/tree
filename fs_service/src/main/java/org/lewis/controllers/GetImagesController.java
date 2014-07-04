package org.lewis.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.lewis.fs.DB;
import org.lewis.fs.FSFile;
import org.lewis.fs.FSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.WebUtils;

@Controller
public class GetImagesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetImagesController.class);

	private static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";

	private static final String HEADER_LAST_MODIFIED = "Last-Modified";

	private static final String METHOD_GET = "GET";

	private static final String METHOD_HEAD = "HEAD";

	private static final String ETAG = "Etag";

	private String defaultImage = "static/images/default.jpg";
	
	@Autowired
	@Qualifier("fsService")
	private FSService fsService;
	
	@Value("${image.temp.dir}")
	private String tempDir;


	/**
	 * 校验是否修改
	 * 
	 * @param lastModifiedTimestamp
	 *            最后修改时间
	 * @param res
	 * @param req
	 * @return
	 */
	private boolean checkNotModified(long lastModifiedTimestamp,
			HttpServletRequest req, HttpServletResponse res) {
		boolean notModified = false;
		if (lastModifiedTimestamp >= 0
				&& !res.containsHeader(HEADER_LAST_MODIFIED)) {
			long ifModifiedSince = req.getDateHeader(HEADER_IF_MODIFIED_SINCE);
			notModified = (ifModifiedSince >= (lastModifiedTimestamp / 1000 * 1000));
			if (notModified && METHOD_GET.equals(req.getMethod())) {
				res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			} else {
				res.setDateHeader(HEADER_LAST_MODIFIED, lastModifiedTimestamp);
			}
		}
		return notModified;
	}

	private void setContentType(String fileName, HttpServletResponse res) {
		String contentType = "image/jpeg";
		if (StringUtils.endsWithIgnoreCase(fileName, ".gif")) {
			contentType = "image/gif";
		} else if (StringUtils.endsWithIgnoreCase(fileName, ".png")) {
			contentType = "image/png";
		}
		res.setContentType(contentType);
	}

	private void return404(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			File defaultImage = new File(WebUtils.getRealPath(request.getSession().getServletContext(), this.defaultImage));
			InputStream in = new FileInputStream(defaultImage);
			OutputStream out = response.getOutputStream();
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			out.flush();
			IOUtils.closeQuietly(out);
		} catch (Exception e) {
			try {
				response.sendError(404);
			} catch (IOException ex) {
			}
		}
	}

	private boolean isPicture(String picname) {
		picname = StringUtils.lowerCase(picname);
		return StringUtils.endsWith(picname, ".jpg")
				|| StringUtils.endsWith(picname, ".jpeg")
				|| StringUtils.endsWith(picname, ".gif")
				|| StringUtils.endsWith(picname, ".png");
	}

	private void getImage(final String name, int width, int height,
			final HttpServletRequest request, final HttpServletResponse response) {
		LOGGER.debug("Get image name:"+name);
		if (!isPicture(name)) {
			return404(request, response);
			return;
		}
		FSFile outPutFile = fsService.getFile(DB.IMAGE,name);
		if (null == outPutFile) {
			return404(request, response);
			return;
		}
		if (width * height > 0) {// 缩略
			final String thumbnailName = (new StringBuilder()).append("thumbnail-").append(String.valueOf(width)).append("-").append(String.valueOf(height)).append("-").append(name).toString();
			FSFile thumbnailFile = fsService.getFile(DB.IMAGE,thumbnailName);
			if(null!=thumbnailFile){
				outPutFile=thumbnailFile;
			}else{//生成缩略图
				try {
					//临时目录
					String tempPath =tempDir+"/" + FastDateFormat.getInstance("ss").format(new Date());
					File tempDir = new File(tempPath);
					if (!tempDir.exists()) {
						tempDir.mkdirs();
					}
					File thumbnailTemp = new File(tempPath + "/"+ UUID.randomUUID().toString() + "_"+ thumbnailName);
					InputStream orginInputStream = outPutFile.getInputStream(); // open
					OutputStream thumbnailTempOut = new FileOutputStream(thumbnailTemp); // open out
					Thumbnails.of(orginInputStream).size(width, height).toOutputStream(thumbnailTempOut);
					IOUtils.closeQuietly(orginInputStream);// close in
					IOUtils.closeQuietly(thumbnailTempOut);// close out
					outPutFile=fsService.saveFile(DB.IMAGE,thumbnailName,new FileInputStream(thumbnailTemp));
					FileUtils.forceDelete(thumbnailTemp);
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("生成缩略图失败:" + e.getMessage());
				}
			}
		}
		//输出
		outToClient(name, request, response, outPutFile);
	}

	private void outToClient(String name, HttpServletRequest request,
			HttpServletResponse response, FSFile orginFile) {
		long lastModifiedTimestamp = orginFile.getUploadDate().getTime();
		if (checkNotModified(lastModifiedTimestamp, request, response)) {
			LOGGER.debug("Resource not modified - returning 304");
			return;
		}
		if (METHOD_HEAD.equals(request.getMethod())) {
			LOGGER.debug("HEAD request - skipping content");
			return;
		}
		response.setContentLength((int) orginFile.getLength());
		response.setHeader(ETAG, "pic" + orginFile.getFileName().hashCode());
		setContentType(name, response);
		OutputStream out = null;
		InputStream in = orginFile.getInputStream();
		try {
			out = response.getOutputStream();
			IOUtils.copy(in, out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * 显示缩略图
	 */
	@RequestMapping(value = "/fs/images/thumbnail-{width}-{height}/{name:.*}", method = RequestMethod.GET)
	public void getThumbnailImage(@PathVariable int width,
			@PathVariable int height, @PathVariable String name,
			HttpServletRequest request, HttpServletResponse response) {
		this.getImage(name, width, height, request, response);
	}

	/**
	 * 显示原图 getOrginImage
	 */
	@RequestMapping(value = "/fs/images/{name:.*}", method = RequestMethod.GET)
	public void getOrginImage(@PathVariable String name,
			HttpServletRequest request, HttpServletResponse response) {
		this.getImage(name, 0, 0, request, response);
	}

}
