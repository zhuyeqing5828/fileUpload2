package com.zx.fileupload.utils;

import java.util.UUID;

import com.zx.fileupload.exception.RegexException;

public class StringUtil {
	private StringUtil(){}
	
	public static boolean isNullString(String s){
		return s==null||s.equals("");
	}
	
	public static String getRandomUUID(){
		return UUID.randomUUID().toString();
	}
	/**
	 * 将字符串中的动态参数转换成相应的值(共10个)
	 * 匹配规则
	 * $d	将其替换成参数数组的指定字符串
	 * $$	显示$字符
	 * $其他   显示其他字符
	 * @param srcString 源字符串
	 * @param strings	参数字符串
	 * @return	匹配好的字符串
	 */
	public static String replaceString(CharSequence srcString ,String...strings){
		return replaceString(srcString, strings, false);
	}
	/**
	 * 将字符串中的动态参数转换成相应的值(共10个)
	 * 匹配规则
	 * $d	将其替换成参数数组的指定字符串
	 * $$	显示$字符
	 * $其他   显示其他字符
	 * @param srcString 源字符串
	 * @param strings	参数字符串数组
	 * @param isRecursive 是否转换参数字符串中的参数
	 * @return	匹配好的字符串
	 */
	public static String replaceString(CharSequence srcString ,String[] strings,boolean isRecursive){
		StringBuilder sb= new StringBuilder(srcString);
		for (int i = 0; i < sb.length(); i++) {
			if(sb.charAt(i)=='$'){
				char c=sb.charAt(i+1);
				if(48<=c&&c>=57){
					if(strings.length<c-47||strings[c-48]==null)
						throw new RegexException("$"+c,"have not this parameter");
					String repString=strings[c-48];
					sb.replace(i, i+1,repString);
					i=isRecursive? i-1 : i+repString.length()-1;
				}else{
					sb.deleteCharAt(i);
				}
			}
		}
		return sb.toString();
	}
}
