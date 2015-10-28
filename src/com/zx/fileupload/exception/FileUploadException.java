package com.zx.fileupload.exception;

public class FileUploadException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileUploadException() {
		super();
	}

	public FileUploadException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileUploadException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileUploadException(String message) {
		super(message);
	}

	public FileUploadException(Throwable cause) {
		super(cause);
	}

}
