package com.zx.fileupload.utils;

import java.util.UUID;

public class StringUtil {
	private StringUtil(){}
	
	public static boolean isNullString(String s){
		return s==null||s.equals("");
	}
	
	public static String getRandomUUID(){
		return UUID.randomUUID().toString();
	}
}
