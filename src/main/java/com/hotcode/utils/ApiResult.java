package com.hotcode.utils;

import com.hotcode.common.MessageException;


/**
 * 主要用于封装方法调用或者是RPC调用的结果.
 * @author wuqq
 *
 * @param <T>
 */
public class ApiResult<T> {

	/**
	 * 默认为200：调用成功
	 * 500：调用失败
	 */
	private int status = 200 ;
	
	
	/**
	 * 调用结果
	 */
	private T result ;
	
	
	/**
	 * 调用失败的原因.调用成功则为null
	 */
	private String message ;

	/**
	 * 某些情况下, 我们希望通过数字来约定调用状态时, 可以使用这个字段.但是必须在接口上写清楚
	 * 约定习俗：一般情况下,尽量不要使用这个字段
	 */
	private int code ;
	
	
	public T getResult() {
		return result;
	}


	public void setResult(T result) {
		this.result = result;
	}


	public String getMessage() {
		return message;
	}

	
	
	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}
	
	
	
	
	/**
	 * 只应该在调用失败的时候, 才使用这个方法设置出错信息
	 * 
	 * @param message
	 */
	public ApiResult<T> setMessage(String message) {
		this.status = 500 ;
		this.message = message;
		return this ;
	}
	
	
	/**
	 * 调用是否成功
	 * 
	 * @return
	 */
	public boolean isOK() {
		return status == 200 ;
	}

	/**
	 * 用于json序列化的方法
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 用于json序列化的方法
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}


	/**
	 * 
	 * @param e
	 * @return
	 */
	public boolean fillMessage(Exception e) {
		if(e!=null && e instanceof MessageException)
		{
			setMessage(e.getMessage()) ;
			return true ;
		}
		this.status = 500 ;
		return false ;
	}


	@Override
	public String toString() {
		return "ApiResult [status=" + status + ", result=" + result + ", message=" + message + ", code=" + code + "]";
	}
	
}
