package org.lewis.core.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lewis.core.web.ServletContextHolder;
import org.springframework.web.servlet.DispatcherServlet;


public class LewisDispatcherServlet extends DispatcherServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ServletContextHolder.setServletContext(request, response);
		super.doService(request, response);
		ServletContextHolder.remove();
	}
	
	
	

}
