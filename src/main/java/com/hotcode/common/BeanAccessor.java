package com.hotcode.common;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * 一个简易的访问JavaBean属性的工具类. 提供了获取、设置JavaBean属性的方法,类加载时自动判断是否可以使用sun.misc.Unsafe.
 * 
 * @author qingquanwu
 *
 */
public abstract class BeanAccessor {
	
	/**
	 * 
	 */
	private static final long INVALID_OFFSET = -1 ;
	
	/**
	 * 
	 */
	private static boolean canBeUseUnsafe = false ;
	
	/**
	 * 
	 */
	private static Unsafe unsafe = null ;
	
	
	
	static{ init(); }
	
	/**
	 * 
	 */
	private static void init() {
		try
		{
			canBeUseUnsafe = getUnsafe()!=null ;
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static boolean canBeUseUnsafe() {
		return canBeUseUnsafe ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public synchronized static Unsafe getUnsafe() {
		if(unsafe==null) {
			try
			{
				Class<?> clazz = Class.forName("sun.misc.Unsafe");
				Field field = clazz.getDeclaredField("theUnsafe");
				field.setAccessible(true);
				unsafe = (Unsafe) field.get(null);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException("error to get theUnsafe", e);
			}
		}
		return unsafe ;
	}
	
	
	
	/**
	 * 
	 * @param <T>
	 * @param bean
	 * @param field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object bean, Field field) {
		if(bean!=null && field!=null) {
			try
			{
				Object value = canBeUseUnsafe() ? getValueWithUnsafe(bean, field) : field.get(bean); ;
				if(value!=null) return (T)value ;
			}
			catch (Exception e) { throw new RuntimeException(e); }
		}
		return null ;
	}
	
	
	
	
	/**
	 * 
	 * @param bean
	 * @param field
	 * @param value
	 */
	public static void setValue(Object bean, Field field, Object value) 
	{
		if(bean!=null && field!=null) {
			try {
				if(canBeUseUnsafe())
				{
					setValueWithUnsafe(bean, field, value) ;
				}
				else
				{
					field.set(bean, value) ;
				}
			}
			catch (Exception e) { throw new RuntimeException(e); }
		}
	}
	
	
	
	
	/**
	 * 
	 * @param bean
	 * @param field
	 * @return
	 */
	private static Object getValueWithUnsafe(Object bean, Field field) {
		long offset = unsafe.objectFieldOffset(field) ;
		if(offset==INVALID_OFFSET) return null;
		
		Class<?> fieldClass = field.getType();
		if (fieldClass == boolean.class) {
			return unsafe.getBoolean(bean, offset);
		} else if (fieldClass == byte.class) {
			return unsafe.getByte(bean, offset);
		} else if (fieldClass == short.class) {
			return unsafe.getShort(bean, offset);
		} else if (fieldClass == char.class) {
			return unsafe.getChar(bean, offset);
		} else if (fieldClass == int.class) {
			return unsafe.getInt(bean, offset);
		} else if (fieldClass == long.class) {
			return unsafe.getLong(bean, offset);
		} else if (fieldClass == float.class) {
			return unsafe.getFloat(bean, offset);
		} else if (fieldClass == double.class) {
			return unsafe.getDouble(bean, offset);
		}
		return unsafe.getObject(bean, offset);
	}
	
	
	
	/**
	 * 
	 * @param bean
	 * @param field
	 * @param value
	 */
	private static void setValueWithUnsafe(Object bean, Field field, Object value) {
		long offset = unsafe.objectFieldOffset(field) ; 
		if(offset==INVALID_OFFSET) return ;
		
		Class<?> fieldClass = field.getType(); 
		if (fieldClass == boolean.class && value!=null){
			unsafe.putBoolean(bean,offset,((Boolean)value).booleanValue());
		} else if (fieldClass == byte.class  && value!=null){
			unsafe.putByte(bean,offset,((Byte)value).byteValue());
		} else if (fieldClass == short.class  && value!=null) {
			unsafe.putShort(bean,offset,((Short)value).shortValue());
		} else if (fieldClass == char.class  && value!=null) {
			unsafe.putChar(bean,offset,((Character)value).charValue());
		} else if (fieldClass == int.class  && value!=null) {
			unsafe.putInt(bean,offset,((Integer)value).intValue());
		} else if (fieldClass == long.class  && value!=null) {
			unsafe.putLong(bean,offset,((Long)value).longValue());
		} else if (fieldClass == float.class  && value!=null) {
			unsafe.putFloat(bean,offset,((Float)value).floatValue());
		} else if (fieldClass == double.class  && value!=null) {
			unsafe.putDouble(bean,offset,((Double)value).doubleValue());
		} else {
			unsafe.putObject(bean,offset,value);
		}
	}
	
}
