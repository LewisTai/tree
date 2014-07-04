package org.lewis.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lewis.core.utils.SpringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


/**
 * 系统数据初始化监听器
 * 
 * @author taiqichao
 * @email taiqichao@gmail.com
 */
@Component
public class BootstrapListener implements ApplicationListener<ContextRefreshedEvent> {

	private Log log = LogFactory.getLog(BootstrapListener.class);


	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("Application started success :)");
		SpringUtils.setApplicationContext(event.getApplicationContext());
	}

}
