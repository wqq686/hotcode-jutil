package com.hotcode.logger;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hotcode.logger.Logger.Level;


/**
 * 
 * @author wuqq
 *
 */
public class LoggerFactory {
	
	/**
	 * 所有的Logger对象
	 */
	private static final ConcurrentHashMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>(128);
	
	static 
	{
		new Thread(new LogFlushWorker(), "async-logg-flusher-thread").start();
	}

	
	
	/**
	 * 创建一个Logger对象，并保存在map中。
	 * 
	 * @param name
	 * @param level
	 * @param filePath
	 */
	public static Logger createLogger(String name, Level level, String filePath) {
		name = name.intern() ;
		Logger logger = new Logger(name, level, filePath);
		Logger absent = loggerMap.putIfAbsent(name, logger);
		return absent == null ? logger : absent ;
	}

	
	/**
	 * 
	 * @param name
	 * @param level
	 * @param filePath
	 * @return
	 */
	@Deprecated
	public static Logger DeprecatedCreateLogger(String name, Level level, String filePath) {
		name = name.intern() ;
		Logger logger = loggerMap.get(name) ;
		if(logger==null)
		{
			synchronized (name) 
			{
				logger = loggerMap.get(name) ;
				if(logger==null) createLogger(name, level, filePath) ;
			}
		}
		return logger ;
	}
	
	
	
	
	/**
	 * 根据log名字获取一个Logger对象
	 * 
	 * @param name
	 * @return
	 */
	public static Logger getLogger(String name) {
		return getLogger(name, false) ;
	}

	
	/**
	 * 根据log名字获取一个Logger对象
	 * @param name
	 * @param throwIfNull 如果不存在, 是否抛出NullPointerException
	 * @return
	 */
	public static Logger getLogger(String name, boolean throwIfNull) {
		Logger logger = loggerMap.get(name);
		if (logger == null && throwIfNull) throw new NullPointerException("Logger name=" + name + " UNFOUND.");
		return logger;
	}
	
	/**
	 * @return 返回包好所有日志对象的只读MAP
	 */
	public static Map<String, Logger> getLoggerMap() {
		return Collections.unmodifiableMap(loggerMap);
	}

}
