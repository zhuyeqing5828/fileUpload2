package com.zx.fileupload;

import java.util.Map;

/**
 * 文件上传请求信息
 * @author zhuyeqing
 *
 */
public class FileUploadRequestProp {
	String filename;
	long fuleSize;
	FileUploadProp requestProp;
	Map<String,String> parameperMap;
	String Md5Code;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getFuleSize() {
		return fuleSize;
	}
	public void setFuleSize(long fuleSize) {
		this.fuleSize = fuleSize;
	}
	public FileUploadProp getRequestProp() {
		return requestProp;
	}
	public void setRequestProp(FileUploadProp requestProp) {
		this.requestProp = requestProp;
	}
	public Map<String, String> getParameperMap() {
		return parameperMap;
	}
	public void setParameperMap(Map<String, String> parameperMap) {
		this.parameperMap = parameperMap;
	}
	public String getMd5Code() {
		return Md5Code;
	}
	public void setMd5Code(String md5Code) {
		Md5Code = md5Code;
	}
	public FileUploadRequestProp(String filename, long fuleSize,
			FileUploadProp requestProp, Map<String, String> parameperMap,
			String md5Code) {
		super();
		this.filename = filename;
		this.fuleSize = fuleSize;
		this.requestProp = requestProp;
		this.parameperMap = parameperMap;
		Md5Code = md5Code;
	}
	@Override
	public String toString() {
		return "FileUploadRequestProp [filename=" + filename + ", fuleSize="
				+ fuleSize + ", requestProp=" + requestProp + ", parameperMap="
				+ parameperMap + ", Md5Code=" + Md5Code + "]";
	}
	
}
