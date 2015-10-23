package com.zx.fileupload;
/**
 * 文件上传工具抛出的运行时异常
 * @author acer
 *
 */
public class FileUploadRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileUploadRuntimeException() {
		super();
	}

	public FileUploadRuntimeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileUploadRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileUploadRuntimeException(String message) {
		super(message);
	}

	public FileUploadRuntimeException(Throwable cause) {
		super(cause);
	}

}
