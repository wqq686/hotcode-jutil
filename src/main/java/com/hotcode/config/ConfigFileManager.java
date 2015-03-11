package com.hotcode.config;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hotcode.common.CommonUtils;
import com.hotcode.utils.Triple;

/**
 * 1. 如何实现忽略文件类型?
 * 2. 用JDK7来实现?
 * 
 * @author wuqq
 *
 */
public class ConfigFileManager {

	private static ConcurrentMap<String, Triple<String, FileEntry, List<FileListener>>> listeners = new ConcurrentHashMap<>() ;
	
	static {
		init();
	}
	
	private static void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(;!Thread.interrupted();) {
					try {
						Thread.sleep(1000L);
						for(String pathname : listeners.keySet()) {
							Triple<String, FileEntry, List<FileListener>> stamp = listeners.get(pathname) ;
							File current = new File(pathname) ;
							if(stamp.getSecond().refresh(current)) {
								System.out.println("file=["+current+"] onChanged...");
								List<FileListener> list = stamp.getThird() ;
								for(FileListener listener : list) {
									try{listener.onChanged(current);}catch(Exception e){e.printStackTrace();}
								}
							}
						}
					} catch(Throwable ignore){ignore.printStackTrace();}
				}
			}
		}, "touna-config-checker").start();
	}
	


	public static void main(String[] args) {
		register("F:/files", new FileListener() {
			@Override
			public void onChanged(File changed) {
				System.out.println(changed);
			}
		});
	}
	
	
	/**
	 * 监听文件
	 * @param listener
	 * @param pathnames 文件全路径数组
	 */
	public synchronized static void register(FileListener listener, String...pathnames) {
		for(String pathname : pathnames) {
			register(listener, new File(pathname));
		}
	}
	
	
	
	
	/**
	 * 监听文件
	 * @param listener
	 * @param files 文件数组
	 */
	public synchronized static void register(FileListener listener, File...files) {
		for(File file  : files) {
			if(file==null || listener==null) throw new IllegalArgumentException("the file=["+file+"] or listener=["+listener+"] is null.") ;
			else if(!file.isFile()) throw new IllegalArgumentException("the file=["+file+"] must be File.") ;
			
			try {
				String path = file.getCanonicalPath() ;
				Triple<String, FileEntry, List<FileListener>> stamp = listeners.get(path) ;
				if(stamp==null) {
					stamp = new Triple<String, FileEntry, List<FileListener>>(path, new FileEntry(file), new CopyOnWriteArrayList<FileListener>()) ;
					listeners.put(path, stamp) ;
				}
				
				stamp.getThird().add(listener) ;
				listener.onChanged(file);
			} catch(Exception e){throw new IllegalStateException(e);}
		}
	}
	
	
	
	
	
	/**
	 * 监听classpath上的文件
	 * @param listener
	 * @param filenames 文件名数组
	 */
	public synchronized static void registerClasspatch(FileListener listener, String... filenames) {
		for(String filename : filenames) {
			URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
		    String pathname = url.getFile();
		    register(listener, pathname);
		}
	}
	
	
	
	
	/**
	 * 
	 * @param path 目录路径
	 * @param listener 监听器
	 * @param @param filenames 文件名数组
	 */
	public synchronized static void register(String path, FileListener listener, String... filenames) {
		if(!new File(path).isDirectory()) throw new IllegalArgumentException("the path=["+path+"] must be Directory.") ;
		for(String name : filenames) {
			String pathname = CommonUtils.mergePath(path, name) ;
			register(listener, pathname);
		}
	}
	
	
	

	
	
	
	
}
