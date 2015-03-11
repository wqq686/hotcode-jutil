package com.hotcode.common;

import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
 * 
 * @author wuqq
 *
 */
public class CryptUtils {

	/**
	 * 
	 */
	private static final char hexChar[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8' , '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	

	/**
	 * 
	 * @param input 要进行加密的字符串
	 * @return  字符串的md5值
	 */
	public static String md5(String input) {
		return md5(input, null) ;
	}
	
	
	
	/**
	 * 
	 * @param input 要进行加密的字符串
	 * @return  字符串的md5值
	 */
	public static String md5(String input, String charset) {
		if(input==null) return null ;
		//md5加密算法的加密对象为字符数组，这里是为了得到加密的对象
		try
		{
			byte[] b = charset==null ? input.getBytes() : input.getBytes(charset);
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(b);
			byte[] bytes = md.digest();// 进行加密并返回字符数组
			char md5[] = new char[bytes.length << 1];
			int len = 0;
			//将字符数组转换成十六进制串，形成最终的密文
			for (int i = 0; i < bytes.length; i++) {
				byte val = bytes[i];
				md5[len++] = hexChar[(val >>> 4) & 0xf];
				md5[len++] = hexChar[val & 0xf];
			}
			return new String(md5);
		} catch (Exception e) {throw CommonUtils.illegalStateException(e);}
	}
	
	
	
	/**
	 * 按照UTF-8的方式先对字符串进行Base64加密, 再进行URLEncode
	 * 
	 * @param input
	 * @return
	 */
	public static String urlEncodeBase64(String input) {
		if(input==null) return null ;
		return CommonUtils.urlEncodeUTF8(BASE64Utils.encodeUTF8(input)) ;
	}
	
	
	/**
	 * 按照UTF-8的方式先对字符串进行URLDecode, 再进行Base64解密
	 * @param input
	 * @return
	 */
	public static String urlDecodeBase64(String input) {
		if(input==null) return null ;
		input = CommonUtils.urlDecodeUTF8(input) ;
		return BASE64Utils.decodeUTF8(input) ;
	}
	
	
	public static String desEncode(String key,String data, String desIvParameterSpec) throws Exception{
		if(CommonUtils.isEmpty(data)) return "";
		DESKeySpec dks = new DESKeySpec(key.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		Key secretKey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		IvParameterSpec iv=new IvParameterSpec(desIvParameterSpec.getBytes());//向量
		AlgorithmParameterSpec paramSpec = iv;
		cipher.init(Cipher.ENCRYPT_MODE, secretKey,paramSpec);
		byte[] bytes = cipher.doFinal(data.getBytes("GBK"));
		
		return BASE64Utils.encode(bytes);
	}
	
	
	public static String desDecode(String key,String data, String desIvParameterSpec) throws Exception{
		DESKeySpec dks = new DESKeySpec(key.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		Key secretKey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(desIvParameterSpec.getBytes());
		AlgorithmParameterSpec paramSpec = iv;
		cipher.init(Cipher.DECRYPT_MODE, secretKey,paramSpec);
		byte[] bytes=cipher.doFinal(BASE64Utils.decode(data));
		
		return new String(bytes, "GBK");
	}
    public static void main(String[] args) {
		String input = "融信财富" ;
		String output = urlEncodeBase64(input) ;
		System.out.println("encode input=" + output);
		System.out.println("decode output=" + urlDecodeBase64(output));
	}
    
}
