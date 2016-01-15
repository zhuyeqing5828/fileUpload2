package com.zx.fileupload;

import java.util.List;
import java.util.Map;

public class ResourceClass {
	private Map<String,FileUploadBucketProp> fileUploadPropMap;
	private List<FileUploadProp> props;
	private int recycleCycle;
	private int fileUploadRequestTimeOut;
	public Map<String, FileUploadBucketProp> getFileUploadPropMap() {
		return fileUploadPropMap;
	}
	public void setFileUploadPropMap(
			Map<String, FileUploadBucketProp> fileUploadPropMap) {
		this.fileUploadPropMap = fileUploadPropMap;
	}
	public List<FileUploadProp> getProps() {
		return props;
	}
	public void setProps(List<FileUploadProp> props) {
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
			List<FileUploadProp> props, int recycleCycle,
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
