package com.hotcode.helper;

import com.alibaba.fastjson.JSONArray;
import com.hotcode.common.TypesUtils;


/**
 * 依赖fast-json, FAST-JSON比较二逼, 序列化JavaBean时,需要getter、setter
 * 
 * @author wuqq
 *
 */
public class JSONHelper {

	/**
	 * 
	 */
	public static final String __JSON__RPC__REQUEST__KEY__ = "__JSON__RPC__REQUEST__KEY__" ;
	
	/**
	 * 
	 */
	public static final String JSON_RPC_ID = "id" ;
	
	/**
	 * 
	 */
	public static final String JSON_RPC_METHOD = "method" ;
	
	/**
	 * 
	 */
	public static final String JSON_RPC_PARAMS = "params" ;
	
	/**
	 * 
	 */
	public static final String JSON_RPC_RESULT = "result" ;
	
	/**
	 * 
	 */
	public static final String JSON_RPC_ERROR = "error" ;
	
	/**
	 * 
	 */
	public static final String JSON_RPC_ERROR_MESSAGE = "message" ;
	
	/**
	 * 
	 */
	public static final String JSON_RPC_ERROR_CODE = "code" ;
	/**
	 * 
	 */
	public static final String JSON_RPC_ERROR_CODE_500 = "500" ;
	
	/**
	 * 
	 */
	public static final int JSON_BUFFER_SIZE = 1024*4 ;
	
	
	
	/**
	 * 
	 * @param args
	 * @param array
	 * @return
	 */
	public static Object[] adjustArguments(Object[] args, JSONArray array) {
		Object[] parameters = null ;
		if(args!=null)
		{
			parameters = new Object[args.length] ;
			for(int index=0; index<args.length; index++)
			{
				Object data = array.get(index) ;
				parameters[index] = data==null ? args[0] : TypesUtils.cashTo(data, args[index]) ;
			}
		}
		return parameters ;
	}
	
	
	
	
	
}
