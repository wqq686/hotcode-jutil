package com.hotcode.common;

import java.io.File;
import java.io.InputStream;
import java.net.URL;


public class IOUtils {

	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static InputStream getInputStreamFromClassPath(String filename) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
	}
	
	
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public static String getCanonicalPath(File file) {
		try
		{
			return file == null ? null : file.getCanonicalPath() ;
		}catch(Exception e){throw CommonUtils.illegalStateException(e);}
	}
	
	
	/**
	 * 
	 * @param file
	 * @param paths
	 * @return
	 */
	public static String mergePaths(File file, String...paths) {
		String[] array = paths ; String root = null ;
		if(file!=null)
		{
			root = getCanonicalPath(file) ;
			if(paths!=null && paths.length>1)
			{
				array = new String[paths.length+1] ;
				System.arraycopy(paths, 0, array, 1, paths.length) ;
			}
			else
			{
				array = new String[1] ;
			}
			array[0] = root ;
		}
		return mergePaths(array) ;
	}
	
	
	/**
	 * 合并多个路径
	 * 
	 * @param paths
	 * @return
	 */
	public static String mergePaths(String...paths) {
		if(CommonUtils.isEmpty(paths)) return null ;
		StringBuilder builder = new StringBuilder(paths[0]) ;
		for(int i=1; i<paths.length; i++)
		{
			String path = CommonUtils.emptyIfNull(paths[i]) ;
			path = path.startsWith(File.separator) ? path.substring(1) : path ;
			path = path.endsWith(File.separator) ? path.substring(0, path.length()-1) : path ;
			builder.append(File.separator).append(path) ;
		}
		String path = builder.toString() ;
		return path ;
	}
	
	
	
	/**
	 * 合并路径返回URL
	 * 
	 * @param paths
	 * @return
	 */
	public static URL pathToURL(String...paths) {
		try
		{
			String path = mergePaths(paths) ;
			return new File(path).toURI().toURL() ;
		}catch(Exception e){throw CommonUtils.convertRuntimeException(e);}

	}
}
