package com.hotcode.utils;

import java.util.Map;

import com.hotcode.common.CommonUtils;
import com.hotcode.common.PropertiesUtils;
import com.hotcode.common.TypesUtils;


/**
 * 用于描述进程信息.进程需要在classpath中存在process.properties文件
 * 
 * @author wuqq
 *
 */
public class ProcessManager {

	private static final String README_FILE_NAME = "process" ;
	/**
	 * 
	 */
	private String status = "CONFIG_NOT_FOUND" ;
	
	/**
	 * app名称(name)
	 */
	private String appName ;
	
	/**
	 * ip地址(ip)
	 */
	private String ip ;
	
	/**
	 * 端口号(port)
	 */
	private int port ;
	
	/**
	 * 是否是测试环境(is_test)
	 */
	private boolean isTestEnvironment ;
	
	/**
	 * 是否跑定时任务(is_task)
	 */
	private boolean engine ;
	
	/**
	 * 任务日志路径(task_log_path)
	 */
	private String taskLogPath ;

	
	private ProcessManager(){}
	
	private static ProcessManager instance = new ProcessManager() ;
	
	public static ProcessManager getInstance() {
		return instance ;
	}
	
	static { readme(); }
	
	
	private static void readme() {
		try
		{
			Map<String, String> config = PropertiesUtils.load(README_FILE_NAME) ;
			instance.status = "FOUNDED" ;
			
			//1. appname
			String value = config.get("name") ;
			instance.appName = value == null ? null : value.trim() ;
			
			//2. ip
			value = config.get("ip") ;
			instance.ip = value == null ? null : value.trim() ;
			
			//3. port
			value = config.get("port") ;
			instance.port = TypesUtils.cashFor(value, int.class) ;
			
			//4. is_test
			value = config.get("is_test") ;
			boolean isTest = TypesUtils.cashFor(value, boolean.class) ;
			instance.isTestEnvironment = isTest ;
			
			//5. task_server
			value = config.get("engine") ;
			boolean engine = TypesUtils.cashFor(value, boolean.class) ;
			instance.engine = engine ;
			
			//6. log_path
			value = config.get("task_log_path") ;
			instance.taskLogPath = CommonUtils.isEmpty(value) ? "home/data/log/" : value.trim() ;
			
			instance.status = "OK" ;
		} catch(Exception e) {e.printStackTrace();}
		
		
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getAppName() {
		return appName ;
	}

	
	/**
	 * 
	 * @return
	 */
	public boolean isTestEnvironment() {
		return isTestEnvironment;
	}

	
	/**
	 * 
	 * @return
	 */
	public boolean isEngine() {
		return engine;
	}

	
	/**
	 * 
	 * @return
	 */
	public String getTaskLogPath() {
		return taskLogPath;
	}

	/**
	 * 
	 * @return
	 */
	public String getIp() {
		return ip;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}


	@Override
	public String toString() {
		return "ProcessManager [status=" + status + ", appName=" + appName + ", ip=" + ip + ", port=" + port + ", isTestEnvironment=" + isTestEnvironment + ", engine=" + engine + ", taskLogPath=" + taskLogPath + "]";
	}

	
	
}
