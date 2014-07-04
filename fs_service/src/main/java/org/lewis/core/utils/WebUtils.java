package org.lewis.core.utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.lewis.core.Constants;
import org.lewis.core.web.ServletContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class WebUtils {

	/**
	 * 获得session在cookie中的id
	 * 
	 * @return
	 */
	public static String getSessionId() {
		return WebUtils.getCookieByName(getRequest(),
				Constants.DEFAULT_SESSION_ID_KEY);
	}


	/**
	 * 获得当前请求
	 * 
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes();
		return attr.getRequest();
	}
	
	/**
	 * 获得当前响应
	 * @return
	 */
	public static HttpServletResponse getResponse() {
		return ServletContextHolder.getHttpServletResponse();

	}

	/**
	 * 获得IP地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr() {
		HttpServletRequest request = getRequest();
		String ip = request.getHeader("X-Real-IP");
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		} else {
			return ip;
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		} else {
			int index = ip.indexOf(',');
			if (index != -1) {
				ip = ip.substring(0, index);
			}
		}
		return ip;
	}

	/**
	 * 添加cookie
	 * 
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 * @param domain
	 * @param path
	 */
	public static void addCookie(HttpServletResponse response, String name,
			String value, int maxAge, String domain, String path) {
		Cookie cookie = new Cookie(name, value);
		if (StringUtils.isBlank(path)) {
			path = "/";
		}
		cookie.setPath(path);
		if (StringUtils.isNotBlank(domain)) {
			cookie.setDomain(domain);
		}
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	/**
	 * 获取cookie的值
	 * 
	 * @param request
	 * @param name
	 *            cookie的名称
	 * @return
	 */
	public static String getCookieByName(HttpServletRequest request, String name) {
		Map<String, Cookie> cookieMap = WebUtils.readCookieMap(request);
		if (cookieMap.containsKey(name)) {
			Cookie cookie = cookieMap.get(name);
			return cookie.getValue();
		} else {
			return null;
		}
	}

	/**
	 * 读取cookie
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (int i = 0; i < cookies.length; i++) {
				cookieMap.put(cookies[i].getName(), cookies[i]);
			}
		}
		return cookieMap;
	}

}
