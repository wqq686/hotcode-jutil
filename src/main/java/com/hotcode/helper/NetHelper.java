package com.hotcode.helper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.hotcode.common.CommonUtils;
import com.hotcode.common.StringHelper;


/**
 * 与网络相关的工具集, 依赖javax.servlet-api.jar
 * 
 * @author rxdai
 *
 */
public class NetHelper 
{
    /**
     * 获取页面输入的String类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return String型的输入参数
     */
    public static String getStringParameter(ServletRequest request, String name, String defaults)
    {
    	return getStringParameter(request, name, defaults, true);
    }
    
    /**
     * 获取页面输入的String类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @param decode 是否需要解码&#xxx;这种编码
     * @return String型的输入参数
     */
    public static String getStringParameter(ServletRequest request, String name, String defaults, boolean decode)
    {
    	String str = request.getParameter(name);
    	if(decode)
    	{
	    	//解码
	    	str = StringHelper.decodeNetUnicode(str);
    	}
        return StringHelper.convertString(str, defaults);
    }

    /**
     * 获取页面输入的int类型参数
     * @param request ServletRequest的实例 
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return int型的输入参数
     */
    public static int getIntParameter(ServletRequest request, String name, int defaults)
    {
        return StringHelper.convertInt(request.getParameter(name), defaults);
    }
    
    /**
     * 获取页面输入的int类型参数，若无该输入参数，则返回0
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @return int型的输入参数
     */
    public static int getIntParameter(ServletRequest request, String name)
    {
        return getIntParameter(request, name, 0);
    }
    
    /**
     * 获取页面输入的long类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return long型的输入参数
     */
    public static long getLongParameter(ServletRequest request, String name, long defaults)
    {
        return StringHelper.convertLong(request.getParameter(name), defaults);
    }
    
    /**
     * 获取页面输入的long类型参数，若无该输入参数，则返回0
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @return long型的输入参数
     */
    public static long getLongParameter(ServletRequest request, String name)
    {
        return getLongParameter(request, name, 0);
    }
    
    /**
     * 获取页面输入的double类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return double型的输入参数
     */
    public static double getDoubleParameter(ServletRequest request, String name, double defaults)
    {
        return StringHelper.convertDouble(request.getParameter(name), defaults);
    }
    
    /**
     * 获取页面输入的double类型参数，若无该参数，则返回0.0
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @return long型的输入参数
     */
    public static double getDoubleParameter(ServletRequest request, String name){
        return getDoubleParameter(request, name, 0.0);
    }
    
    /**
     * 获取页面输入的short类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return short型的输入参数
     */
    public static short getShortParameter(ServletRequest request, String name, short defaults)
    {
        return StringHelper.convertShort(request.getParameter(name), defaults);
    }
    
    /**
     * 获取页面输入的short类型参数，若无该参数，则返回0
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @return short型的输入参数
     */
    public static short getShortParameter(ServletRequest request, String name)
    {
        return getShortParameter(request, name, (short)0);
    }
    
    /**
     * 获取页面输入的float类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return float型的输入参数
     */
    public static float getFloatParameter(ServletRequest request, String name, float defaults)
    {
        return StringHelper.convertFloat(request.getParameter(name), defaults);
    }
    
    /**
     * 获取页面输入的float类型参数，若无该参数，则返回0.0
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @return long型的输入参数
     */
    public static float getFloatParameter(ServletRequest request, String name)
    {
        return getFloatParameter(request, name, (float)0.0);
    }
    
    /**
     * 获取boolean 类型的参数
     * @param request
     * @param name
     * @param defaults
     * @return boolean
     */
    public static boolean getBooleanParameter(ServletRequest request, String name, boolean defaults)
    {
        return StringHelper.convertBoolean(request.getParameter(name), defaults);
    }
    

    /**
     * 获取boolean 类型的参数,默认值为false
     * @param request
     * @param name
     * @return boolean
     */
    public static boolean getBooleanParameter(ServletRequest request, String name)
    {
        return getBooleanParameter(request, name, false);
    }
    
    
private static final String[] __http__ip__key__ = new String[]{"x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "http_client_ip", "HTTP_X_FORWARDED_FOR"} ;
	
	/**
	 * Get host IP address
	 * 
	 * @return IP Address
	 */
	public static InetAddress getAddress() {
		InetAddress inetAddress = null ;
		try
		{
			for(Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()){
					continue;
				}
				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
				inetAddress = addresses.hasMoreElements() ? addresses.nextElement() : InetAddress.getLocalHost() ;
			}
		} catch (Exception e) {throw CommonUtils.illegalStateException(e);}
		return inetAddress ;
	}

	
	
	
	/**
	 * 获取http请求ip地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestIP(HttpServletRequest request) {
		if(request==null) return null ;
		String ip = null ;
		for(String key : __http__ip__key__) {
			ip = getIPFromHeader(key, request) ;
			if(!CommonUtils.isEmpty(ip)) break ;
		}
		
		if(CommonUtils.isEmpty(ip)) {
			ip = request.getRemoteAddr();
		}
		
		if(!CommonUtils.isEmpty(ip) && ip.indexOf(",")>0) {
			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
		}
		
		if(CommonUtils.isEmpty(ip)) {
			ip = "unknown" ;
		}
		
		return ip ;
	}
	
	
	public static String getIPFromHeader(String key, HttpServletRequest request) {
		String ip = request.getHeader(key);
		return "unknown".equalsIgnoreCase(ip) ? null : ip ;
	}
}
