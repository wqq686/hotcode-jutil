package com.hotcode.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hotcode.common.CommonUtils;


/**
 * 抄自http://yuancihang.iteye.com/blog/592678, 稍微修改
 * 
 * @author wuqq
 *
 */
public class DomNode {
	
	/**
	 * 
	 */
	private static final String DEFAULT_ENCODING_CHARSET = "UTF-8" ;
	
	/**
	 * 
	 */
	private Element e;
    
	
	/**
	 * 
	 * @param element
	 */
    private DomNode(Element element){
        this.e = element;
    }
    
    
    /**
     * 
     * @param rootName
     * @return
     */
    public static DomNode newDomNode(String rootName) {
        try
        {
            DocumentBuilder dombuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dombuilder.newDocument();
            doc.setXmlStandalone(true);
            Element root = doc.createElement(rootName);
            doc.appendChild(root);
            return new DomNode(root);
        } catch (Exception e) { throw new XmlException(e.getMessage(), e); }

    }
    
    
    
    /**
     * 
     * @param is
     * @return
     */
    public static DomNode getRoot(InputStream is) {
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            docBuilder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    if (systemId.contains("http://java.sun.com/dtd/web-app_2_3.dtd")) {
                        return new InputSource(new StringReader(""));
                    } else {
                        return null;
                    }
                }
            });

            Document doc = docBuilder.parse(is);
            Element root = doc.getDocumentElement();
            return new DomNode(root);
        } catch (Exception e) {throw new XmlException(e.getMessage(), e);}
        finally{ try{if(is!=null)is.close(); } catch(Exception ignore){} }
    }
    
    
    /**
     * 
     * @param filename 从classpath中读取xml
     * @return
     */
    public static DomNode getRootFromClassPath(String filename) {
    	return getRoot(CommonUtils.getInputStreamFromClassPath(filename));
    	
    	
    }
    
    /**
     * 
     * @param xmlNamePath xml文件绝对路径
     * @return
     */
    public static DomNode getRoot(String xmlNamePath) {
    	try 
    	{
			return getRoot(new FileInputStream(xmlNamePath));
		} catch (FileNotFoundException e) { throw CommonUtils.illegalStateException(e); }
    }
    
    
    
    /**
     * 
     * @return
     */
    public Element getDomElement(){
        return e;
    }
    
    public String getTagName() {
    	return e.getTagName() ;
    }
    
    
    /**
     * 
     * @param attributeName
     * @return
     */
    public String attributeValue(String attributeName){
        return attributeValue(attributeName, null) ;
    }
    
    
    /**
     * 
     * @param attributeName
     * @param def
     * @return
     */
    public String attributeValue(String attributeName, String def){
    	String value = e.getAttribute(attributeName);
    	return value == null || value.trim().isEmpty() ? def : value.trim() ;
    }
    
    
    /**
     * 
     * @param attributeName
     * @return
     */
    public int attributeInt(String attributeName){
    	return attributeInt(attributeName, 0) ;
    }
    
    
    /**
     * 
     * @param attributeName
     * @param def
     * @return
     */
    public int attributeInt(String attributeName, int def){
    	String value = attributeValue(attributeName) ;
    	return CommonUtils.isEmpty(value) ? def : Integer.valueOf(value) ;
    }
    
    
    /**
     * 
     * @param elementName
     * @return
     */
    public boolean existElement(String elementName){
        NodeList nodeList = e.getElementsByTagName(elementName);
        if((nodeList == null) || (nodeList.getLength() < 1)){
            return false;
        }
        return true;
    }
    
    
    
    /**
     * 
     * @param elementName
     * @return
     */
    public String elementText(String elementName){
    	String text = null ;
        Element element = (Element)e.getElementsByTagName(elementName).item(0);
        if(element!=null && element.getFirstChild()!=null) {
        	text = element.getFirstChild().getNodeValue() ;
        }
        return text ==null ? null : text.trim();
    }
    
    
	public int elementInt(String elementName) {
		return elementInt(elementName, 0) ;
	}
	
	public int elementInt(String elementName, int def) {
		try{return Integer.valueOf(elementText(elementName));}catch(Exception ignore){}
		return def ;
	}
	
	public boolean elementBoolean(String elementName, boolean def) {
		String value = elementText(elementName) ;
		return CommonUtils.isEmpty(value) ? def : Boolean.valueOf(value.trim()) ;
	}
    /**
     * 
     * @param elementName
     * @return
     */
    public DomNode element(String elementName){
        NodeList nodeList = e.getElementsByTagName(elementName);
        if((nodeList == null) || (nodeList.getLength() < 1)){
            return null;
        }
        Element element = (Element)nodeList.item(0);
        return new DomNode(element);
    }
    
    
    
    /**
     * 
     * @return
     */
    public List<DomNode> getChildNodes(){
    	List<DomNode> list = new ArrayList<DomNode>();
    	NodeList nodeList = e.getChildNodes() ;
    	if(nodeList != null)
        {
        	for(int i=0;i<nodeList.getLength();i++){
                Node node = nodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element)node;
                    list.add(new DomNode(element));
                }
            }
        }
        return list;
    }
    
    
    /**
     * 
     * @param elementName
     * @return
     */
    public List<DomNode> elements(String elementName){
        List<DomNode> list = new ArrayList<DomNode>();
        NodeList nodeList = e.getElementsByTagName(elementName);
        if(nodeList != null)
        {
        	for(int i=0;i<nodeList.getLength();i++){
                Node node = nodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element)node;
                    list.add(new DomNode(element));
                }
            }
        }
        return list;
    }
    
    
    /**
     * 
     * @param name
     * @return
     */
    public DomNode addElement(String name){
        Document document = e.getOwnerDocument();
        Element element = document.createElement(name);
        e.appendChild(element);
        return new DomNode(element);
    }
    
    
    
    /**
     * 
     * @param name
     * @param value
     * @return
     */
    public DomNode addElement(String name, String value){
        Document document = e.getOwnerDocument();
        Element element = document.createElement(name);
        e.appendChild(element);
        Text text = document.createTextNode(value);
        element.appendChild(text);
        return new DomNode(element);
    }
    
    
    
    
    /**
     * 添加或修改属性
     * @param name
     * @param value
     * @return
     */
    public DomNode setAttribute(String name, String value){
        e.setAttribute(name, value);
        return this;
    }
    
    
    /**
     * 
     * @param subDom
     */
    public void remove(DomNode subDom){
        e.removeChild(subDom.getDomElement());
    }
    
    
    
    /**
     * 
     * @param name
     */
    public void removeElement(String name){
        NodeList nodeList = e.getElementsByTagName(name);
        if(nodeList != null)
        {
        	for(int i=0;i<nodeList.getLength();i++){
                e.removeChild(nodeList.item(i));
            }
        }
    }
    
    
    
    /**
     * 
     * @param name
     */
    public void removeAttribute(String name){
        e.removeAttribute(name);
    }
    
    
    
    /**
     * 
     * @param name
     * @param value
     * @return
     */
    public DomNode updateElementText(String name, String value){
        Element element = (Element)e.getElementsByTagName(name).item(0);
        Node textNode =  element.getFirstChild();
        textNode.setNodeValue(value);
        return new DomNode(element);
    }
    
    
    /**
     * 
     * @param value
     * @return
     */
    public DomNode updateElementText(String value){
        Node textNode =  e.getFirstChild();
        textNode.setNodeValue(value);
        return this;
    }
    
    
    /**
     * 
     * @return
     */
    public String getElementText(){
        Node textNode =  e.getFirstChild();
        return textNode.getNodeValue();
    }
    
    
    
    /**
     * 
     * @param os
     */
    public void write(OutputStream os){
        write(os, DEFAULT_ENCODING_CHARSET);
    }
    
    
    /**
     * 
     * @param xmlFile
     * @throws XmlException
     */
    public void write(String xmlFile)throws XmlException{
        write(xmlFile, DEFAULT_ENCODING_CHARSET);
    }
    
    public void write(String xmlFile, String encoding)throws XmlException{
        try {
            OutputStream os = new FileOutputStream(xmlFile);
            write(os, encoding);
            os.close();
        } catch (Exception e) {
            throw new XmlException(e.getMessage(), e);
        }
    }
    
    
    public void write(OutputStream os, String encoding){
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            tFactory.setAttribute("indent-number", 2);
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.transform(new DOMSource(e.getOwnerDocument()), new StreamResult(new OutputStreamWriter(os)));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * 
     */
    public void printNodes(){
        NodeList nodeList = e.getChildNodes();
        if(nodeList!=null)
        {
        	for(int i=0;i<nodeList.getLength();i++){
                Node node = nodeList.item(i);
                System.out.println("节点名: " + node.getNodeName() + ", 节点值: " + node.getNodeValue() + ", 节点类型: " + node.getNodeType());
            }
        }
    }


    
    /**
     * 
     * @param attributeName
     * @return
     */
    public boolean attributeBoolean(String attributeName) {
    	return attributeBoolean(attributeName, false) ;
    }
    
    
    /**
     * 
     * @param attributeName
     * @param def
     * @return
     */
	public boolean attributeBoolean(String attributeName, boolean def){
		String value = attributeValue(attributeName) ;
		return CommonUtils.isEmpty(value) ? def : Boolean.valueOf(value) ;
	}




 
    
   

}



class XmlException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public XmlException(String message){
        super(message);
    }
    
    public XmlException(String message, Throwable cause){
        super(message, cause);
    }
}