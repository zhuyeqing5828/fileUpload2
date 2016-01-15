package com.zx.fileupload;
/**
 * file upload Object action interface
 * @author zhuyeqing
 *
 */
public class FileUploadProp {
	int recycleCycle = 7200;
	long requestTimeOut = 3600;
	/**
	 * Run this method before file upload service started
	 * @param fileUpload
	 */
	public void beforeStarted(FileUpload fileUpload){
		
	}
	/**
	 * Run this method after file upload service stopped
	 * @param fileUpload
	 */
	public void afterStopped(FileUpload fileUpload){
		
	}
}
