package com.hotcode.helper;

import java.io.File;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 依赖dom4j、log4j
 * @author qingquanwu
 *
 */
public class Dom4JHelper {
	
	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(Dom4JHelper.class) ;
	
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static Element getXmlRoot(String path) 
	{
		File xmlfile = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());
		Element root = null;
		try 
		{
			SAXReader reader = new SAXReader();
			Document document = (Document) reader.read(xmlfile);
			root = document.getRootElement();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			logger.error("{} getXmlRoot element with Exception :{}", new Object[] { path, e });
			throw new RuntimeException(e) ;
		}
		return root;
	}
	
	
	
	
	public static Element getXmlRoot(InputStream is)  {
		Element root = null;
		try 
		{
			SAXReader reader = new SAXReader();
			Document document = (Document) reader.read(is);
			root = document.getRootElement();
		} catch (Exception e) { 
			logger.error("{} getXmlRoot element with Exception :{}", e );
			throw new RuntimeException(e) ;
		}
		return root;
	}
	
}
