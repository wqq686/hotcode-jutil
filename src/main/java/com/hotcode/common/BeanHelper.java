package com.hotcode.common;

import java.lang.reflect.Field;
@Deprecated
public class BeanHelper {
	
	
	/**
	 * 生成toString字符串
	 * 
	 * @param klass
	 * @return
	 */
	public static String createBeanToString(Class<?> klass, String start, String end, String valuePrex, String valueAfter) {
		Field[] fields = klass.getDeclaredFields() ; boolean first = true ; StringBuilder builder = new StringBuilder() ;
		for(Field field : fields) {
			if(first) { 
				first = false ; 
				builder.append("\"") ;
				if(!CommonUtils.isEmpty(start)) builder.append(start) ;
				
			} 
			else {
				builder.append("+\"").append(valueAfter).append(", ") ;
			}
			
			builder.append(field.getName()).append(valuePrex).append("\"+this.").append(field.getName());
		}
		
		if(!CommonUtils.isEmpty(valueAfter) || !CommonUtils.isEmpty(end)){
			builder.append("+\"").append(valueAfter);
			
			if(!CommonUtils.isEmpty(end)) builder.append(end) ;
			
			builder.append("\"") ;
		}
		
		return builder.toString() ;
	}
	
	
	/**
	 * sysout输出toString()方法
	 * 
	 * @param klass
	 */
	public static void sysoutBeanToStringMethod(Class<?> klass, String start, String end, String prexTag, String afterTag) {
		System.out.println("	@Override");
		System.out.println("	public String toString() {");
		System.out.println("		return " + createBeanToString(klass, start, end, prexTag, afterTag) + " ;");
		System.out.println("	}");
	}
	
	
	/**
	 * 
	 * @param klass
	 */
	public static void sysoutBeanToStringMethod(Class<?> klass) {
		sysoutBeanToStringMethod(klass, "{", "}", "=", "") ;
	}
	
}
