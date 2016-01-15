package com.zx.fileupload.vo;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.zx.fileupload.FileUploadBucketProp;
/**
 * 文件上传服务器存储的配置文件
 * @author acer
 *
 */
public class FileUploadObject implements Serializable {

	/**
	 * init
	 */
	private static final long serialVersionUID = 10L;
	String bucketName;
	String fileName;
	long fileSize;
	long receivedSize=0;
	long lastUploaded;
	String md5Checking;
	FileUploadBucketProp ObjectProp;
	Map<Integer,FilePartConfig> parts=new HashMap<Integer, FilePartConfig>();
	public String getBucketName() {
		return bucketName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public long getReceivedSize() {
		return receivedSize;
	}
	public void setReceivedSize(long receivedSize) {
		this.receivedSize = receivedSize;
	}
	public Map<Integer, FilePartConfig> getParts() {
		return parts;
	}
	
	public FileUploadBucketProp getObjectProp() {
		return ObjectProp;
	}
	
	public long getLastUploaded() {
		return lastUploaded;
	}

	public void setLastUploaded(long lastUploaded) {
		this.lastUploaded = lastUploaded;
	}
	
	public String getMd5Checking() {
		return md5Checking;
	}

	public void setMd5Checking(String md5Checking) {
		this.md5Checking = md5Checking;
	}

	public FileUploadObject(String bucketName, String fileName, long fileSize,
			FileUploadBucketProp objectProp) {
		super();
		this.bucketName = bucketName;
		this.fileName = fileName;
		this.fileSize = fileSize;
		ObjectProp = objectProp;
	}
	
	
}
