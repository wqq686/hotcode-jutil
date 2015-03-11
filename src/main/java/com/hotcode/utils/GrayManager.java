package com.hotcode.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.hotcode.common.CommonUtils;



/**
 * 灰度开关控制类.
 * 灰度规则:
 * 
 * 1. key_grayname=open|close
 * 
 * 2. grayname_mod=灰度的mod
 *  2.1. 1:全部通过
 *  2.2. 0:不进行求模判断, 直接进入步骤3判断
 *  2.3. mod<>1 and mod<>0的情况
 *    2.3.1 如果id=0, 直接返回false, id<>0, 进入步骤3判断
 *    
 * 3. grayname_id=1,2,3:根据id进行判断,含有该id, 则true, 否则false
 * 
 * @author wuqq
 *
 */
public class GrayManager {
	
	/**
	 * 
	 */
	private static final String GRAY_FILE_NAME = "gray.properties" ;
	
	/**
	 * 
	 */
	private static ConcurrentMap<String, Gray> grayContainer = new ConcurrentHashMap<String, Gray>() ;
	
	/**
	 * 
	 */
	private static boolean started = false ;
	
	
	/**
	 * 
	 */
	private static WatchService watcher ;
	
	
	

	
	
	
	
	/**
	 * 
	 */
	static{ start(); }
	
	
	/**
	 * 
	 */
	private static synchronized void start() {
		if(!started)
		{
			try
			{
				//1. load conf
				loadConf() ;				
				register() ;
//				register0() ;
				System.out.println(
					"GRAY CONFIG("
					+Thread.currentThread().getContextClassLoader().getResource(GRAY_FILE_NAME) + 
					")===>>>" + grayContainer ) ;
				started = true ;
			}
			catch(Throwable t)
			{
				t.printStackTrace(); 
			}
		}
	}
	
	
	
	/**
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static void register() throws IOException, URISyntaxException {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(;!Thread.currentThread().isInterrupted();)
				{
					try
					{
						TimeUnit.SECONDS.sleep(10);
						loadConf(); 
					}
					catch(Exception e){
						e.printStackTrace();
						if(e instanceof InterruptedException) { break ; }
					}
				}
				
			}
		}, "gray-conf-listener") ;
		t.setDaemon(true);
		t.start();
	}
	
	
	
	/**
	 * JDK7的貌似有点坑爹
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("unused")
	private static void register0() throws IOException, URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(GRAY_FILE_NAME) ;
		String directory = new File(url.toURI()).getParent() ;
		final Path listenerDirectory = Paths.get(directory) ;
		watcher = FileSystems.getDefault().newWatchService();   
	    listenerDirectory.register(watcher, 
	    		StandardWatchEventKinds.ENTRY_CREATE, 
	    		StandardWatchEventKinds.ENTRY_DELETE, 
	    		StandardWatchEventKinds.ENTRY_MODIFY);
	    
		new Thread(new Runnable() {
			@SuppressWarnings({"unchecked", "rawtypes"})
			@Override
			public void run() {
				for(;!Thread.currentThread().isInterrupted();)
				{
					try
					{
						WatchKey key = watcher.take();   
			            for(WatchEvent<?> event : key.pollEvents()){   
							WatchEvent.Kind kind = event.kind();   
			                
							if(kind == StandardWatchEventKinds.OVERFLOW) continue;
			                
			                WatchEvent<Path> e = (WatchEvent<Path>)event;   
			                Path path = e.context();
			                String fileName = path.toFile().getName() ;
			                System.out.println(fileName + ", event="+e.kind());
			                
			                if (GRAY_FILE_NAME.equals(fileName)) 
			                {
								loadConf(); 
							}
			                
//			                listenerDirectory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			            }
			            if(!key.reset())
			            {  
			                break;  
			            }  
					}
					catch(Exception e)
					{
						e.printStackTrace(); 
					}
					started = false ;
				}
			}
		}, "gray-conf-listener").start(); 
	}
	
	
	
	private static void loadConf() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(GRAY_FILE_NAME) ;
		Properties prop = new Properties() ;
		prop.load(is);
		Map<String, String> container = new HashMap<String, String>() ;
		Set<String> keys = new HashSet<String>() ;
		for(Object k : prop.keySet())
		{
			Object v = prop.get(k) ;
			String name = String.valueOf(k), value = String.valueOf(v) ;
			container.put(name, value) ;
			if(name.startsWith("key_"))
			{
				keys.add(name) ;
			}
		}
		
		for(String key : keys)
		{
			try
			{
				Gray gray = parseGray(key, container) ;
				if(gray!=null) grayContainer.put(gray.key, gray) ;				
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("loadConf exception, key:" + key);
			}
		}
		is.close(); 
	}
	
	
	

	/**
	 * 
	 * @param key
	 * @param container
	 * @return
	 */
	private static Gray parseGray(String key, Map<String, String> container) {
		String name = key ;
		String value = container.get(name) ;
		boolean open = false ;
		if(!CommonUtils.isEmpty(value)) open = "open".equals(value.trim()) ;
		
		
		key = key.replaceFirst("key_", "").trim() ;
		
		name = key + "_mod" ;
		value = container.get(name) ;
		if(value==null)
		{
			name = key + "_rate" ;
			value = container.get(name) ;
		}
		int rate = Integer.valueOf(value) ;
		
		
		name = key + "_id" ;
		value = container.get(name) ;
		Set<Long> idSet = newSet(Long.class, value) ;
		
		
		name = key + "_name" ;
		value = container.get(name) ;
		Set<String> nameSet = newSet(String.class, value) ;
		
		
		Gray gray = new Gray(key, open, rate, idSet, nameSet) ;
		return gray ;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isGray(String key) {
		if(!CommonUtils.isEmpty(key))
		{
			Gray gray = grayContainer.get(key) ;
			return gray!=null && gray.open ;
		}
		return false ;
	}
	
	
	
	
	/**
	 * 是否进行灰度
	 * @param key
	 * @param value
	 * @return

	public static boolean isGray(String key, long value) {
		Gray gray = null ;
		
		if(key!=null) gray = grayContainer.get(key) ;
		
		//1. GrayNotFound Or NotOpen
		if(gray==null || !gray.open) return false ;
		
		//2. Rate == 1 ==> all
		if( gray.mod == 1 )
		{
			return true ;
		}
		
		//3. rate <> 1 ==> filter
		if( value == 0 )
		{
			return false ;
		}
		else if( gray.idSet.contains(Long.valueOf(value)) )
		{
			return true ;
		}
		
		//4. rate>0
		return gray.mod>0 && value % gray.mod == 0 ;
	}
	*/
	
	
	
	
	/**
	 * 根据id判断是否进行灰度
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean isGray(String key, long value) {
		if(key==null) return false ;
		Gray gray = grayContainer.get(key) ;
		if(isGrayFullOpen(gray))
		{
			return true ;
		}
		
		//3. rate <> 1 ==> filter
		if( value == 0 )
		{
			return false ;
		}
		else if( gray.idSet.contains(Long.valueOf(value)) )
		{
			return true ;
		}
		
		//4. rate>0
		return gray.mod>0 && value % gray.mod == 0 ;
	}
	
	
	/**
	 * 根据name判断是否进行灰度
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean isGray(String key, String value) {
		if(key==null) return false ;
		Gray gray = grayContainer.get(key) ;
		if(isGrayFullOpen(gray))
		{
			return true ;
		}
		if(gray.nameSet.contains(value)) return true ;
		if(value==null) return false ;
		value = value.intern() ;
		return gray.mod>0 && value.hashCode() % gray.mod == 0 ;
	}
	
	public static void main(String[] args) {
		String value = "linbao_t5" ;
		value = value.intern() ;
		System.out.println(value.hashCode());
		System.out.println(value.hashCode()%5);
	}
	
	/**
	 * 是否全量打开灰度
	 * 
	 * @param gray
	 * @return
	 */
	private static boolean isGrayFullOpen(Gray gray) {
		//1. GrayNotFound Or NotOpen
		if(gray==null || !gray.open) return false ;
		
		//2. Rate == 1 ==> all
		if( gray.mod == 1 )
		{
			return true ;
		}
		return false ;
	}
	
	
	
	
	static class Gray {
		String key; 
		boolean open = false; 
		int mod = 0 ; 
		Set<Long> idSet = new HashSet<>();
		Set<String> nameSet = new HashSet<>(); 
		
		Gray(String key, boolean open, int rate, Set<Long> idSet, Set<String> nameSet) {
			this.key = key ; this.open = open ; this.mod = rate ; this.idSet = idSet ; this.nameSet = nameSet ; 
		}

		
		@Override
		public String toString() {
			return "Gray [key=" + key + ", open=" + open + ", mod=" + mod+ ", nameSet=" + nameSet + ", idSet=" + idSet + "]";
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	private static <E> Set<E> newSet(Class<E> klass, String value) {
		Set<E> set = new HashSet<E>() ;
		if(!CommonUtils.isEmpty(value))
		{
			String[] array = value.trim().split(",") ;
			for(String a : array) 
			{
				if(!CommonUtils.isEmpty(a))
				{
					a = a.trim() ; E e = null ; 
					if(klass==String.class)
					{
						e = (E) a ;
					}
					else if(klass==Long.class)
					{
						e = (E) Long.valueOf(a) ;
					}
					else if(klass==Integer.class)
					{
						e = (E) Integer.valueOf(a) ;
					}
					set.add(e) ;
				}
			}
		}
		return set ;
	}
}
