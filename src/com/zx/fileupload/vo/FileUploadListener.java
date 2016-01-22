package com.zx.fileupload.vo;

import com.zx.fileupload.FileUpload;

/**
 * file upload Object action interface
 * @author zhuyeqing
 *
 */
public interface FileUploadListener {
	
	/**
	 * Run this method before file upload service started
	 * @param fileUpload
	 */
	public void beforeStarted(FileUpload fileUpload);
	/**
	 * Run this method after file upload service stopped
	 * @param fileUpload
	 */
	public void afterStopped(FileUpload fileUpload);
}
