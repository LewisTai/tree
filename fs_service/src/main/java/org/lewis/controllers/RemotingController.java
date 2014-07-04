package org.lewis.controllers;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.lewis.core.utils.SpringUtils;
import org.lewis.core.utils.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;

@Controller
public class RemotingController {

	@RequestMapping(value = "/remoting/{serviceName}", method = RequestMethod.POST)
	public void getService(@PathVariable String serviceName,HttpServletRequest req,HttpServletResponse res)
			throws Exception {
		if (StringUtils.isBlank(serviceName)) {
			res.setStatus(404);
			return;
		}
		Object serviceObj = SpringUtils.getApplicationContext().getBean(serviceName);
		if (null == serviceObj) {
			res.setStatus(404);
			return;
		}
		Class<?>[] interfaces = serviceObj.getClass().getInterfaces();
		if (null == interfaces || interfaces.length == 0) {
			WebUtils.getResponse().setStatus(404);
			return;
		}
		HessianSkeleton hessianSkeleton = new HessianSkeleton(serviceObj,interfaces[0]);
		InputStream is = req.getInputStream();
		OutputStream os = res.getOutputStream();
		res.setContentType("application/x-hessian");
		hessianSkeleton.invoke(is, os,  new SerializerFactory());
	}

}
