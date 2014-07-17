package com.taobao.diamond.client;

import java.util.concurrent.Executor;

import com.taobao.diamond.client.impl.DiamondClientFactory;
import com.taobao.diamond.configinfo.ConfigureInfomation;
import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;

public class TestClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DiamondConfigure diamondConfigure=new DiamondConfigure();
		diamondConfigure.setConfigServerAddress("http://localhost/pet_fs");
		diamondConfigure.setConfigServerPort(8088);
		
		diamondConfigure.addDomainName("localhost");
		diamondConfigure.setPort(8080);
		
		DiamondSubscriber diamondSubscriber=DiamondClientFactory.getSingletonDiamondSubscriber();
		diamondSubscriber.setDiamondConfigure(diamondConfigure);
		diamondSubscriber.setSubscriberListener(new SubscriberListener() {
			public void receiveConfigInfo(ConfigureInfomation configureInfomation) {
				System.out.println("--->"+configureInfomation.getConfigureInfomation());
			}
			public Executor getExecutor() {
				return null;
			}
		});
		
		DiamondManager manager =new DefaultDiamondManager("ddd", "ddd", new ManagerListener() {
			
			public void receiveConfigInfo(String configInfo) {
				
			}
			
			public Executor getExecutor() {
				return null;
			}
		});
	}

}
