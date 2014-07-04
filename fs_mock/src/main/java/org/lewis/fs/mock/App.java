package org.lewis.fs.mock;

import java.io.File;
import java.io.FileInputStream;

import org.lewis.fs.DB;
import org.lewis.fs.FSFile;
import org.lewis.fs.FSService;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        
    	String url = "http://localhost:8081/fs_service/remoting/fsService";
    	HessianProxyFactory factory = new HessianProxyFactory();
    	factory.setOverloadEnabled(true);
    	FSService fsService = (FSService) factory.create(FSService.class, url);
    }
}
