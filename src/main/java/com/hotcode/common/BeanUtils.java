package com.hotcode.common;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * 
 * @author wuqq
 *
 */
public class BeanUtils {

	
	/**
	 * 
	 * @param bean
	 * @return
	 */
	public static Map<String, Object> bean2Map(Object bean) {
		if(bean==null) return null ;
		String[] names = ReflectUtils.getFields(bean.getClass()).keySet().toArray(new String[0]) ;
		return bean2Map(bean, names, names) ;
	}
	
	
	
	
	
	/**
	 * 
	 * @param bean
	 * @param fieldNames
	 * @return
	 */
	public static Map<String, Object> bean2Map(Object bean, String fieldNames) {
		return bean2Map(bean, fieldNames, fieldNames) ;
	}
	
	
	
	
	/**
	 * 
	 * @param bean
	 * @param fieldNames
	 * @param keyNames
	 * @return
	 */
	public static Map<String, Object> bean2Map(Object bean, String fieldNames, String keyNames) {
		return bean2Map(bean, null, fieldNames, keyNames) ;
	}
	
	
	
	/**
	 * 
	 * @param bean
	 * @param names
	 * @param keys
	 * @return
	 */
	public static Map<String, Object> bean2Map(Object bean, String[] names, String[] keys) {
		return bean2Map(bean, null, names, keys) ;
	}
	
	
	
	
	/**
	 * 
	 * @param bean
	 * @param dataMap
	 * @param fieldNames
	 * @param keyNames
	 * @return
	 */
	public static Map<String, Object> bean2Map(Object bean, Map<String, Object> dataMap, String fieldNames, String keyNames) {
		if(bean==null || fieldNames == null || keyNames == null ) return dataMap ;
		
		String[] names = fieldNames.split(",") ; String[] keys = keyNames.split(",") ;
		return bean2Map(bean, dataMap, names, keys) ;
	}
	
	
	
	
	/**
	 * 
	 * @param bean
	 * @param dataMap
	 * @param names
	 * @param keys
	 * @return
	 */
	public static Map<String, Object> bean2Map(Object bean, Map<String, Object> dataMap, String[] names, String[] keys) {
		if(bean==null) return dataMap ;
		
		if(dataMap==null) {
			dataMap = CommonUtils.stableMap(names.length) ;
		}
		
		Map<String, Field> fields = ReflectUtils.getFields(bean.getClass()) ;
		for(int index=0; index<names.length; index++)
		{
			String name = names[index], key = keys[index] ;
			if(!CommonUtils.isEmpty(name) && !CommonUtils.isEmpty(key))
			{
				name = name.trim() ; key = key.trim() ;
				Field field = fields.get(name) ;
				if(field!=null)
				{
					Object value = BeanAccessor.getValue(bean, field) ;
					CommonUtils.putIfNotNull(dataMap, key, value);
				}
			}
		}
		return dataMap ;
	}
	
	
	
	/**
	 * 
	 * @param from
	 * @param bean
	 */
	public static void map2bean(Map<String, Object> from, Object bean) {
		map2bean(from, bean, null);
	}
	
	
	
	/**
	 * 
	 * @param from
	 * @param bean
	 * @param mapping
	 */
	public static void map2bean(Map<String, Object> from, Object bean, Map<String, String> mapping) {
		if(!CommonUtils.isEmpty(from) && bean!=null) {
			Map<String, Field> toMap = ReflectUtils.getFields(bean.getClass()) ;
			boolean emptyMapping = CommonUtils.isEmpty(mapping) ;
			for(String key : from.keySet()) {
				Object value = from.get(key) ;
				if(value==null) continue ;
				String name = emptyMapping ? key : mapping.get(key) ;
				Field field = toMap.get(name) ;
				if(field!=null) {
					Object toValue = null ;
					if(field.getType()==value.getClass()) {
						toValue = value ;
					} else {
						try {
							toValue = TypesUtils.cashFor(value, field.getType()) ;
						}catch(Exception e){e.printStackTrace();} 
					}
					BeanAccessor.setValue(bean, field, toValue);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param src
	 * @param to
	 */
	public static void copy(Object src, Object to) {
		Map<String, Field> srcFields = ReflectUtils.getFields(src.getClass()) ;
		Map<String, Field> toFields = ReflectUtils.getFields(to.getClass()) ;
		boolean error = false ;
		for(String tfn : toFields.keySet())
		{
			Field toField = toFields.get(tfn), srcField = srcFields.get(tfn) ;
			if(srcField!=null)
			{
				Object value = null ; Class<?> toType = null ;
				try
				{
					value = BeanAccessor.getValue(src, srcField) ;
					toType = toField.getType() ;
					/**
					if(toType==Object.class)
					{
						System.out.println("field=" + tfn + "value=" + value);
					}*/
					value = TypesUtils.cashFor(value, toType) ;
					if(value!=null) BeanAccessor.setValue(to, toField, value);
				}
				catch(Exception e)
				{
					error = true ;
					System.err.println("field=" + tfn +", value=" + value + ",toType=" + toType + " with ex:" + CommonUtils.formatThrowable(e));
				}
			}
			
			if(error) System.err.println("srcFieldsKeys=" + srcFields.keySet() + ", toFieldsKeys=" + toFields.keySet());
		}
	}
	
	
	public static void copy(Object src, Object to, String fieldNames) {
		Map<String, Field> srcFields = ReflectUtils.getFields(src.getClass()) ;
		Map<String, Field> toFields = ReflectUtils.getFields(to.getClass()) ;
		String[] names = fieldNames.split(",") ;
		for(String name : names)
		{
			if(CommonUtils.isEmpty(name)) continue ;
			name = name.trim() ;
			Field srcField = srcFields.get(name) ;
			Field toField = toFields.get(name) ;
			if(srcField!=null && toField!=null)
			{
				Object value = BeanAccessor.getValue(src, srcField) ;
				if(value==null) continue ;
				
				Class<?> toType = toField.getType() ;
				value = TypesUtils.cashFor(value, toType) ;
				if(value!=null) BeanAccessor.setValue(to, toField, value);
			}
		}
	}
}
