package com.hotcode.common;

/**
 * 用于传递消息的异常
 * 
 * @author wuqq
 *
 */
public class MessageException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -102324565904130269L;

	
	/**
	 * 
	 */
	private int code;

	/**
	 * 
	 * @param message
	 */
	public MessageException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public MessageException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

	
	/**
	 * 
	 * @param code
	 * @return
	 */
	public MessageException setCode(int code) {
		this.code = code;
		return this;
	}

	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public static MessageException newMessageException(String message) {
		return newMessageException(0, message);
	}

	
	/**
	 * 
	 * @param code
	 * @param message
	 * @return
	 */
	public static MessageException newMessageException(int code, String message) {
		return new MessageException(message).setCode(code);
	}

	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public static MessageException newMessageException(Exception e) {
		return new MessageException(e);
	}
	
}
