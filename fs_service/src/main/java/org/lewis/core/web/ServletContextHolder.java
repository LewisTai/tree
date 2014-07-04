package org.lewis.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletContextHolder {

	private final static ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<HttpServletRequest>();
	private final static ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<HttpServletResponse>();

	public static void setServletContext(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		requestThreadLocal.set(httpServletRequest);
		responseThreadLocal.set(httpServletResponse);
	}

	public static void remove() {
		requestThreadLocal.remove();
		responseThreadLocal.remove();
	}

	public static HttpServletRequest getHttpServletRequest() {
		return requestThreadLocal.get();
	}

	public static HttpServletResponse getHttpServletResponse() {
		return responseThreadLocal.get();
	}

}
