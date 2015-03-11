package com.hotcode.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @author wuqq
 *
 */
public class ReflectUtils {

	private static ConcurrentMap<Class<?>, Map<String, Field>> descContainer = new ConcurrentHashMap<Class<?>, Map<String, Field>>() ;
	
	/**
	 * 
	 * @param m
	 * @param instance
	 * @param args
	 * @return
	 */
	public static Object invokeMethod(Method m, Object instance, Object[] args) {
		try
		{
			return m.invoke(instance, args) ;
		} catch(Throwable t) {throw new IllegalStateException(t) ;}
	}
	
	
	public static void main(String[] args) {
		
	}
	
	
	
	
	/**
	 * 
	 * @param klass
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	public static Method foundMethod(Class<?> klass, String name, Class<?>...parameterTypes) {
		//1. direct found Method
		try { return klass.getDeclaredMethod(name, parameterTypes) ; }catch(Exception ignore){}
		
		//2. found Method by name
		Method[] methods = klass.getDeclaredMethods() ;
        String internedName = name.intern();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (m.getName() == internedName)
            {
            	return m ;
            }
        }
        
        //3. un found
        throw new IllegalStateException("UNFOUND Method=[" + name + "] @ class=[" + klass.getName() +"]") ;
	}
	
	
	
	/**
	 * 
	 * @param klass
	 * @param name
	 * @param value
	 * @param isReturnIfNotField
	 */
	public static boolean setStaticField(Class<?> klass, String name, Object value, boolean isReturnIfNotField) {
		try
		{
			Field field = directGetField(klass, name) ;
			if(field==null && isReturnIfNotField) return false;//TODO:暂时处理
			Object realValue = TypesUtils.cashFor(value, field.getType()) ;
			field.set(klass, realValue) ;
			return true ;
		} catch(Throwable t) {throw new IllegalStateException(t) ;}
	}
	
	
	
	/**
	 * 直接反射查找Field, 不会抛出NoSuchFieldException异常, 找不到返回null,更推荐使用foundField方法 
	 * @param klass
	 * @param name
	 * @return
	 */
	public static Field directGetField(Class<?> klass, String name) {
		Field field = null ;
		try { field = klass.getDeclaredField(name); }
		catch(Exception ignore)
		{
			String internedName = name.intern();
			for(; klass != null && klass!=Object.class ; klass = klass.getSuperclass()) {
				Field []fields = klass.getDeclaredFields();   
				for(Field e : fields) {
					if(e.getName()==internedName)
					{
						field = e ;
						break ;
					}
				}
			}
		}
		
		if(field!=null) field.setAccessible(true) ;
		return field ;
	}
	
	
	
	
	/**
	 * 
	 * @param klass
	 * @param name
	 * @return
	 */
	public static Field foundField(Class<?> klass, String name) {
		return getFields(klass).get(name) ;
	}
	
	
	
	/**
	 * 取得某个对象所有的域, 包括父类的. 有优化的空间：区分出是否获取父类的域
	 * 
	 * @param klass
	 * @return
	 */
	public static Map<String, Field> getFields(final Class<?> klass) {
		Map<String, Field> stamp = descContainer.get(klass);
		if (stamp == null) {
			stamp = new LinkedHashMap<String, Field>();
			Class<?> klazz = klass;
			for (; klazz != null && klazz != Object.class; klazz = klazz.getSuperclass()) {
				Field[] fields = klazz.getDeclaredFields();
				for (Field e : fields) {
					if (stamp.containsKey(e.getName())) continue;
					
					int mod = e.getModifiers() ;
					if(Modifier.isFinal(mod) || Modifier.isStatic(mod)) continue ;
					
					e.setAccessible(true);
					stamp.put(e.getName(), e);
				}
			}
			Map<String, Field> absent = descContainer.putIfAbsent(klass, stamp);
			if (absent != null)
				stamp = absent;
		}
		return stamp ;
	}

	

}
