package com.hotcode.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.hotcode.common.IOUtils;
import com.hotcode.common.TimeUtils;


/**
 * 日志对象
 * 
 * @author wuqq
 *
 */
public class Logger {

	/**
	 * 日志级别类型
	 */
	public static enum Level {

		DEBUG(0), INFO(1), WARN(2), ERROR(3), FATAL(4);
		
		private int value;
		
		private Level(int value) {
			this.value = value;
		}
	}
	
	
	/**
	 * 日志名称
	 */
	private final String name;

	/**
	 * 本地日志路径
	 */
	private String logPath;

	/**
	 * 日志级别
	 */
	private Level level = null;

	/**
	 * 日志缓冲队列
	 */
	private final LinkedBlockingQueue<LogItem> logQueue = new LinkedBlockingQueue<LogItem>(50000);

	/**
	 * 初始化本地日志
	 * 
	 * @param name
	 * @param level
	 * @param logPath
	 */
	Logger(String name, Level level, String logPath) {
		this.name = name;
		this.level = level;
		this.logPath = logPath ;
	}
	
	
	/**
	 * 
	 * @throws IOException
	 */
	void doWriteLog() throws IOException {
		if (!logQueue.isEmpty()) 
		{
			File logFile = getLogFile() ;
			if(logFile!=null)
			{
				List<LogItem> list = new ArrayList<LogItem>(logQueue.size()) ;
				logQueue.drainTo(list) ;
				BufferedWriter writer = null ;
				for(LogItem item : list) 
				{
					if(writer==null || item.getTime()>nextNewFileTime) writer = getWriter(item.getTime()) ;
					writer.write(item.toString()) ;
				}
				writer.flush(); 
			}
		}
	}

	

	
	private BufferedWriter writer = null;
	
	
	private BufferedWriter getWriter(long time) throws IOException {
		if(time>nextNewFileTime)
		{
			File file = getLogFile() ;
			if(writer!=null)
			{
				writer.flush() ;
				try{writer.close() ;}catch(Exception ignore){}
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
		}
		return writer ;
	}



	/**
	 *  日志内容放入队列，如果发现有本地日志没有配置本地文件的，放入DEFAULT_LOG的队列
	 *  
	 * @param level
	 * @param message
	 * @param t
	 */
	private void log(Level level, String message, Throwable t) {
		if(level.value>=this.level.value)
		{
			if(message==null) message = "null" ;
			logQueue.offer(new LogItem(level, message, t));
		}
	}

	
	
	
	/**
	 * 打level为debug的log
	 * 
	 * @param message 要打的log
	 */
	public void debug(String message) {
		log(Level.DEBUG, message, null);
	}

	
	
	/**
	 * 打level为debug的log，并将异常的堆栈也打出来
	 * 
	 * @param message 要打的log
	 * @param t 异常的堆栈
	 */
	public void debug(String message, Throwable t) {
		log(Level.DEBUG, message, t);
	}

	
	
	/**
	 * 打level为info的log
	 * 
	 * @param message 要打的log
	 */
	public void info(String message) {
		log(Level.INFO, message, null);
	}

	
	
	/**
	 * 
	 * @param message
	 * @param parameters
	 */
	public void info(String message, Object... parameters) {
		String[] array = message.split("\\{\\}") ;
		StringBuilder builder = new StringBuilder() ;
		for(int index=0; index<array.length; index++)
		{
			builder.append(array[index]) ;
			if(parameters.length>index)
			{
				builder.append(parameters[index]) ;//TODO:暂时这样吧
			}
		}
		message = builder.toString();
		log(Level.INFO, message, null);
	}
	
	
	
	/**
	 * 
	 * @param message
	 * @param parameters
	 */
	
	/**
	public void info(String message, Object[] parameters) {
		String[] array = message.split("\\{\\}") ;
		StringBuilder builder = new StringBuilder() ;
		for(int index=0; index<array.length; index++)
		{
			builder.append(array[index]) ;
			if(parameters.length>index)
			{
				builder.append(parameters[index]) ;//TODO:暂时这样吧
			}
		}
		message = builder.toString();
		log(Level.INFO, message, null);
	}
	*/
	
	
	/**
	 * 打level为info的log，并将异常的堆栈也打出来
	 * 
	 * @param message 要打的log
	 * @param t 异常的堆栈
	 */
	public void info(String message, Throwable t) {
		log(Level.INFO, message, t);
	}

	
	
	/**
	 * 打level为warn的log
	 * 
	 * @param message 要打的log
	 */
	public void warn(String message) {
		log(Level.WARN, message, null);
	}

	
	
	/**
	 * 打level为warn的log，并将异常的堆栈也打出来
	 * 
	 * @param message 要打的log
	 * @param t 异常的堆栈
	 */
	public void warn(String message, Throwable t) {
		log(Level.WARN, message, t);
	}

	
	
	
	/**
	 * 打level为error的log
	 * 
	 * @param message 要打的log
	 */
	public void error(String message) {
		log(Level.ERROR, message, null);
	}

	
	
	
	/**
	 * 打level为error的堆栈
	 * 
	 * @param message 要打的log
	 * @param t 异常的堆栈
	 */
	public void error(String message, Throwable t) {
		log(Level.ERROR, message, t);
	}

	
	
	
	/**
	 * 打level为fatal的log
	 * 
	 * @param message 要打的log
	 */
	public void fatal(String message) {
		log(Level.FATAL, message, null);
	}

	
	
	
	/**
	 * 打level为fatal的log，并将异常的堆栈也打出来
	 * 
	 * @param message 要打的log
	 * @param t 异常的堆栈
	 */
	public void fatal(String message, Throwable t) {
		log(Level.FATAL, message, t);
	}

	
	
	
	public boolean isDebugEnabled() {
		return Level.DEBUG.value >= this.level.value;
	}

	
	
	public boolean isInfoEnabled() {
		return Level.INFO.value >= this.level.value;
	}

	
	
	/**
	 * 读取日志级别
	 * 
	 * @return 日志级别
	 */
	public Level getLevel() {
		return level;
	}

	
	
	
	/**
	 * 获取日志名称
	 * 
	 * @return 日志名称
	 */
	public String getName() {
		return name;
	}

	
	
	/**
	 * 获取日志输出路径
	 * 
	 * @return 日志输出路径
	 */
	public String getLogPath() {
		return logPath;
	}

	
	/**
	 * 
	 */
	private volatile long nextNewFileTime = 0 ;
	
	
	/**
	 * 
	 */
	private volatile File logFile = null ;
	
	
	
	
	
	private File getLogFile() throws IOException {
		if(name==null) return null ;
		
		long now = System.currentTimeMillis() ;
		
		if(now<nextNewFileTime) {
			return logFile ;
		}
		else if(now>=nextNewFileTime)
		{
			nextNewFileTime = 0 ;
		}
		
		if(nextNewFileTime==0)
		{
			//1. check log directory
			File logdir = new File(getLogPath()) ;
			if(!logdir.exists()) logdir.mkdirs() ;
			
			//2. 
			logFile = proccessCurrentFile(now) ;
			
			if(logFile==null)
			{
				String pathname = IOUtils.mergePaths(getLogPath(), name) ;
				File file = new File(pathname) ;
				file.createNewFile() ;
				logFile = file ;
			}
			nextNewFileTime = nextNewFileTime(now) ;
		}
		
		return logFile ;
	}
	
	
	private File proccessCurrentFile(long now) throws IOException {
		String pathname = IOUtils.mergePaths(getLogPath(), name) ;
		File current = new File(pathname) ;
		if(current.exists())
		{
			long last = current.lastModified() ;
			if(equalsDay(now, last))
			{
				return current ;
			}
			else
			{
				renameCurrentFile(current) ;
			}
		}
		return null ;
	}



	
	private void renameCurrentFile(File current) throws IOException {
		long last = current.lastModified() ;
		String lastName = formatLoggerFileName(name, last) ;
		String pathname = IOUtils.mergePaths(getLogPath(), lastName) ;
		File dest = new File(pathname) ;
		boolean ok = current.renameTo(dest) ;
		if(!ok)
		{
			FileChannel in = null, out = null ;
			try
			{
				in = new FileInputStream(current).getChannel();
				out = new FileOutputStream(dest).getChannel();
				in.transferTo(0, in.size(), out);
				out.force(true) ;				
			}
			finally { try{if(in!=null)in.close() ; if(out!=null)out.close() ;}catch(Exception ignore){} }
		}
	}



	private boolean equalsDay(long now, long last) {
		Date one = new Date(now), other = new Date(last) ;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd") ;
		return format.format(one).equals(format.format(other)) ;
	}
	
	/**
	 * 
	 * @param now
	 * @return
	 */
	private long nextNewFileTime(long now) {
		Calendar c = Calendar.getInstance() ;
		c.setTimeInMillis(now) ;
		TimeUtils.setCalendarToZero(c) ;
		c.set(Calendar.DAY_OF_MONTH, 1) ;
		return c.getTimeInMillis() ;
	}
	
	
	
	/**
	 * 
	 * @param loggerName
	 * @param time
	 * @return
	 */
	private String formatLoggerFileName(String loggerName, long time) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd") ;
		return loggerName + "." +formatter.format(new Date(time)) ;
	}
	
	
	
	
	@SuppressWarnings("unused")
	private File getLogFile0() throws IOException {
		if(name==null) return null ;
		
		long now = System.currentTimeMillis() ; String fileName = null ;
		
		if(now<nextNewFileTime) {
			return logFile ;
		}
		else if(now>=nextNewFileTime)
		{
			nextNewFileTime = 0 ;
		}
		
		if(nextNewFileTime==0)
		{
			File file = new File(getLogPath()) ;
			if(!file.exists()) file.mkdirs() ;
			
			fileName = formatLoggerFileName(name, now) ;
			String pathname = IOUtils.mergePaths(getLogPath(), fileName) ;
			file = new File(pathname) ;
			if(!file.exists()) file.createNewFile() ;
			logFile = file ;
			nextNewFileTime = nextNewFileTime(now) ;
		}
		
		return logFile ;
	}
}
