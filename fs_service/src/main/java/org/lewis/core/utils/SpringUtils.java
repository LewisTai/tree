package org.lewis.core.utils;

import org.springframework.context.ApplicationContext;

public class SpringUtils {
	
	private static ApplicationContext applicationContext;
	
	public synchronized static void setApplicationContext(ApplicationContext act){
		applicationContext=act;
	}
	
	public static ApplicationContext getApplicationContext(){
		return applicationContext;
	}
	
	public static <T> T getBean(Class<T> requiredType){
		return applicationContext.getBean(requiredType);
	}


}
