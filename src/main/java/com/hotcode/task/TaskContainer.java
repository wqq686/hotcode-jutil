package com.hotcode.task;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.hotcode.common.CommonUtils;
import com.hotcode.logger.Logger;
import com.hotcode.logger.LoggerFactory;
import com.hotcode.logger.Logger.Level;

public class TaskContainer {

	private TaskContainer(){}
	

	private static boolean started = false ;
	
	/**
	 * 
	 */
	private static DelayQueue<SimpleDelayed<Runnable>> taskQueue = new DelayQueue<SimpleDelayed<Runnable>>() ;
	
	/**
	 * 
	 */
	private static ExecutorService taskExecutor ;
	
	/**
	 * 
	 */
	private static Thread taskTaker = null ;
	
	/**
	 * 
	 */
	private static String logName = "task.log" ;
	
	/**
	 * 
	 */
	private static String logPath ="/home/data/log";
	
	/**
	 * 
	 */
	private static Logger logger ;


	/**
	 * 配合logger使用
	 * @param logpath
	 */
	public static synchronized void start(String logpath) {
		if(!CommonUtils.isEmpty(logPath)) {
			logPath = logpath ;			
		}
		start(); 
	}
	
	
	
	/**
	 * 
	 */
	public static synchronized void start() {
		if(started) throw CommonUtils.illegalStateException("TaskContainer has been started.") ;
		taskExecutor = Executors.newCachedThreadPool() ;
		taskTaker = new Thread(new Runnable() {
			@Override
			public void run() {
				for(;!Thread.interrupted();)
				{
					try
					{
						SimpleDelayed<Runnable> d = taskQueue.take() ;
						if(d.getAttach()!=null) taskExecutor.execute(d.getAttach()) ;
					} catch(Exception e) {
						if(started && !(e instanceof InterruptedException))
						{
							String message = "taskQueue-taker take with:" + CommonUtils.formatThrowable(e) ;
							System.err.println(message);
							System.out.println(message) ;
						}
					}
				}
			}
		}, "taskQueue-taker");
		taskTaker.start() ;
		started = true ;
		System.out.println("TaskContainer started success.") ;
		getLogger().info("TaskContainer started success.") ;
	}
	
	
	
	/**
	 * 
	 */
	public static synchronized void stop() {
		check() ;
		taskTaker.interrupt() ;
		taskExecutor.shutdownNow() ;
		taskQueue.clear() ;
		System.out.println("TaskContainer stop.") ;
		getLogger().info("TaskContainer stop.") ;
	}
	
	
	
	/**
	 * 
	 * @param r
	 * @param time
	 * @param unit
	 * @return
	 */
	public static boolean addTask(Runnable r, long time, TimeUnit unit) {
		check() ;
		return taskQueue.offer(new SimpleDelayed<Runnable>(r, time, unit)) ;
	}
	
	/**
	 * 
	 * @param r
	 * @return
	 */
	public static boolean containsTask(Runnable r) {
		return taskQueue.contains(r) ;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public static String getLogPath() {
		return logPath;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static Logger getLogger() {
		if(logger==null) {
			System.out.println("[TASK-LOG]==>["+logName+"] path=" + logPath);
			synchronized (TaskContainer.class) {
				if(logger==null) logger = LoggerFactory.createLogger(logName, Level.INFO, logPath) ;
			}
		}
		return logger ;
	}

	
	

	/**
	 * 
	 */
	private static void check() {
		if(!started) throw CommonUtils.illegalStateException("TaskContainer hasn't started.") ;
	}
}
