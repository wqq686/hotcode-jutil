package com.hotcode.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 过滤掉的sql关键字(未测试, 可能存在误杀情况, 慎用)
 * 扩展
 * @author wuqq
 *
 */
public class SQLInjectMatcher {

	private static Collection<String> sqlKeys = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>()) ;
	
	static { init(); }
	
	
	private static void init() {
		String keywords = "'|and|exec|execute|insert|create|drop|table|from|grant|use|group_concat|column_name|information_schema.columns|table_schema|union|where|select|delete|update|order|by|count|*|chr|mid|master|truncate|char|declare|or|;|-|--|+|,|like|//|/|%|#" ;
		String[] array = keywords.split("\\|") ;
		Set<String> keys = new HashSet<>() ;
		for(String key : array) {
			keys.add(key) ;
		}
		setSQLKeys(keys);
	}
	
	public static void main(String[] args) {
		
	}
	
	/**
	 * 设置sql关键字
	 * @param keys
	 */
	public static synchronized void setSQLKeys(Set<String> keys) {
		if(keys==null) keys = new HashSet<>() ;
		sqlKeys = Collections.unmodifiableCollection(keys) ;
	}
	
	
	
	/**
	 * 获得sql关键字
	 * @return
	 */
	public static Collection<String> getSQLKeys() {
		return sqlKeys ;
	}
	
	
	
	
	/**
	 * 是否含有sql关键字
	 * 
	 * @param line
	 * @return
	 */
	public static boolean contains(String line) {
		if(line==null || line.isEmpty()) return false ;
		
		final Collection<String> keys = getSQLKeys() ;
		for(String key : keys) {
			if(line.contains(key)) return true ;
		}
		return false ;
	}
	
	
	
	
	
	/**
	 * 过滤sql注入
	 * @param line
	 * @return
	 */
	public static String filter(String line) {
		if(line==null || line.isEmpty()) return line ;
		
		final Collection<String> keys = getSQLKeys() ;
		for(String key : keys) line = line.replaceAll(key, "") ;
		return line ;
	}
	
	
	

	
}
