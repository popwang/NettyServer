package com.my.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
/**
 * 用于netty服务配置的读取
 * @author Administrator
 */

public class Common {
	private static Properties p = new Properties();
	public static Properties readConfigFile(){
		String path = System.getProperty("user.dir") + "/conf/conf.properties";
		FileInputStream in;
		try {
			in = new FileInputStream(new File(path));
			p.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	public static int getPort(){
		return Integer.parseInt(p.getProperty("PORT"));
	}
	
	public static int getPoolSize(){
		return Integer.parseInt(p.getProperty("POOL_SIZE"));
	}
	
	static {
		readConfigFile();
	}
}
