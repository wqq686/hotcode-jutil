package com.hotcode.common;

import java.nio.ByteBuffer;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 * BASE64工具类
 * 
 * @author rxdai
 *
 */
public class BASE64Utils {
    
    /**
     * 按UTF-8编码encode该字符串
     * @param input
     * 
     * @return String
     */
    public static String encodeUTF8(String input) {
        return encode(input, CommonUtils.UTF8) ;
    }
    
    /**
     * 按charset编码encode该字符串
     * @param input
     * 
     * @return String
     */
    public static String encode(String input, String charset) {
        try
        {
        	return encode(input.getBytes(charset)) ;
        } catch(Exception e){throw CommonUtils.illegalStateException(e);}
    }
    
    /**
     * 对字节数组进行encode
     * @param bytes
     * @return String
     */
    public static String encode(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }
    
    
    /**
     * 对ByteBuffer进行encode
     * @param buf
     * @return String
     */
    public static String encode(ByteBuffer buf) {
        return new BASE64Encoder().encode(buf);
    }
    
    
    

    
    /**
     * 
     * @param input
     * @return
     */
    public static String decodeUTF8(String input) {
        try
        {
            return decode(input, CommonUtils.UTF8);
        } catch(Exception e){throw CommonUtils.illegalStateException(e);}
    }
    
    
    /**
     * 
     * @param input
     * @param charset
     * @return
     */
    public static String decode(String input, String charset) {
        try
        {
            return new String(decode(input), charset) ;
        } catch(Exception e){throw CommonUtils.illegalStateException(e);}
    }
    
    
    /**
     * 对BASE64的字符串进行decode
     * @param input
     * @return byte[] 失败，则返回null
     */
    public static byte[] decode(String input) {
        try
        {
            return new BASE64Decoder().decodeBuffer(input);
        } catch(Exception e){throw CommonUtils.illegalStateException(e);}
    }
    
    
    
    
    
    public static void main(String[] args) {
		String input = "融信财富" ;
		String output = encodeUTF8(input) ;
		System.out.println("encode input=" + output);
		System.out.println("decode output=" + decodeUTF8(output));
	}
    
}
