package com.zx.fileupload.exception;

public class RegexException extends RuntimeException {
	String regex;
	String message;
	
	public String getRegex() {
		return regex;
	}

	public String getMessage() {
		return message;
	}

	public RegexException(String regex, String message) {
		super(message+"  "+regex);
		this.regex = regex;
		this.message = message;
	}

}
