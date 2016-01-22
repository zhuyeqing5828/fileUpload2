package com.zx.fileupload.vo;

import java.util.List;
import java.util.Map;

import com.zx.fileupload.FileUploadBucketProp;

public class ResourceClass {
	private Map<String,FileUploadBucketProp> fileUploadPropMap;
	private List<FileUploadListener> props;
	private int recycleCycle;
	private int fileUploadRequestTimeOut;
	
	public Map<String, FileUploadBucketProp> getFileUploadPropMap() {
		return fileUploadPropMap;
	}
	public void setFileUploadPropMap(
			Map<String, FileUploadBucketProp> fileUploadPropMap) {
		this.fileUploadPropMap = fileUploadPropMap;
	}
	public List<FileUploadListener> getProps() {
		return props;
	}
	public void setProps(List<FileUploadListener> props) {
		this.props = props;
	}
	public int getRecycleCycle() {
		return recycleCycle;
	}
	public void setRecycleCycle(int recycleCycle) {
		this.recycleCycle = recycleCycle;
	}
	public int getFileUploadRequestTimeOut() {
		return fileUploadRequestTimeOut;
	}
	public void setFileUploadRequestTimeOut(int fileUploadRequestTimeOut) {
		this.fileUploadRequestTimeOut = fileUploadRequestTimeOut;
	}
	public ResourceClass(Map<String, FileUploadBucketProp> fileUploadPropMap,
			List<FileUploadListener> props, int recycleCycle,
			int fileUploadRequestTimeOut) {
		super();
		this.fileUploadPropMap = fileUploadPropMap;
		this.props = props;
		this.recycleCycle = recycleCycle;
		this.fileUploadRequestTimeOut = fileUploadRequestTimeOut;
	}
	public ResourceClass() {
		super();
	}
	
}
