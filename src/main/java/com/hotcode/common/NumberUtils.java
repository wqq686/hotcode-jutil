package com.hotcode.common;

import java.text.DecimalFormat;


/**
 * 类似于TimeUtils, 这个是帮助类统一收敛数字相关
 * 
 * @author wuqq
 *
 */
public class NumberUtils {

	
	
	/**
	 * 格式化(##,###.00) 
	 * @param number
	 * @return ##,###.00
	 */
	public static String formatNumber(double number) {
		DecimalFormat format = new DecimalFormat("##,##0.00");
		return format.format(number) ;
	}
	
	
	
	
	/**
	 * 格式化金额(##,###.00元)
	 * @param moeny
	 * @return  ##,###.00元
	 */
	public static String formatYuan(double money) {
		return formatNumber(money) + "元";
	}
}
