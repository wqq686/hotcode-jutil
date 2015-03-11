package com.hotcode.utils;

import com.alibaba.fastjson.JSON;


/**
 * JSON序列化, 依赖fastjson
 * 
 * @author wuqq
 *
 */
public class JSONSerializer implements ObjectSerializer {

	@Override
	public byte[] serialize(Object object) {
		return JSON.toJSONBytes(object) ;
	}

	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] bytes) {
		return (T) JSON.parse(bytes) ;
	}

}
