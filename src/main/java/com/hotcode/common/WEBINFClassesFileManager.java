package com.hotcode.common;

import java.io.File;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 只监听WEB-INF/classes下的文件变更, 适用于app/WEB-INF/classes的项目结构
 * 1. 采用JDK7原生接口进行实现
 * 2. 实现FileNotifyer接口, 并调用register方法进行对文件的注册监听
 * 3. 内部以异步的方式(启动新线程(web-inf-classes-file-watcher))的方式对文件进行监听处理.
 * 4. 在context destroy的时候 最好 调用shutdown方法,销毁内部监听线程(尤其是在坑爹的tomcat中)
 * @author wuqq
 *
 */
public class WEBINFClassesFileManager {

	private final static String TAG = WEBINFClassesFileManager.class.getSimpleName();
	
	public static interface FileNotifyer {
		public void notifyChanged(File file, Kind<?> kind);
	}
	
	
	
	
	/**
	 * 
	 */
	private static boolean started = false;
	
	
	
	/**
	 * 
	 */
	private static ExecutorService listener = null ;
	
	
	
	/**
	 * 
	 */
	private static Map<String, FileNotifyer> notifyers = new HashMap<>() ;
	
	
	
	
	
	/**
	 * 监听WEB-INF/classes下的文件变更
	 * 
	 * @param notifier
	 * @param filenames 一组文件名
	 */
	public static synchronized void register(FileNotifyer notifier, String... filenames) {
		for (String name : filenames) 
		{
			if (!CommonUtils.isEmpty(name)) register(name, notifier) ;
		}
	}
	
	
	
	
	/**
	 * 监听单个文件
	 * @param filename 文件名
	 * @param notifier 通知者
	 */
	public static synchronized void register(String filename, FileNotifyer notifier) {
		start() ;
		notifyers.put(filename, notifier) ;
	}
	
	
	
	
	
	@SuppressWarnings("rawtypes")
	private static synchronized void start() {
		if(!started)
		{
			try
			{
				URL url = Thread.currentThread().getContextClassLoader().getResource(".") ;
				String directory = new File(url.toURI()).getParent() ;
				final WatchService watcher = FileSystems.getDefault().newWatchService();
				Path path = Paths.get(directory);
				path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
				listener = Executors.newSingleThreadExecutor(SimpleNamedThreadFactory.newSingleThreadFactory("web-inf-classes-file-watcher")) ;
				Runnable command = new Runnable() {
					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						for(;!Thread.currentThread().isInterrupted();)
						{
							String fileName = null;
							try
							{
								WatchKey key = watcher.take();   
								for (WatchEvent<?> event : key.pollEvents())
					            {   
									WatchEvent.Kind kind = event.kind();
									if (kind == StandardWatchEventKinds.OVERFLOW)
										continue;
					                WatchEvent<Path> e = (WatchEvent<Path>)event;  
					                
					                Path path = e.context();
									File file = path.toFile();
									fileName = file.getName();
									FileNotifyer notifyer = notifyers.get(fileName);
									if (notifyer == null) continue;
									notifyer.notifyChanged(file, kind);
					            }
								if (!key.reset()) break;
							}
							catch(Exception e)
							{
								System.err.println(TAG + " fileName:" + fileName);
								e.printStackTrace(); 
							}
						}
					}
				};
				
				listener.execute(command) ;
				started = true ;
			} catch(Exception e) { throw new IllegalStateException(e) ; }
		}
	}
	
	public static synchronized void shutdown() {
		if(started)
		{
			listener.shutdownNow() ;
			started = false ;
		}
	}
	
	

	
}
