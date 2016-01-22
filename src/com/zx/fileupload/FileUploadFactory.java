package com.zx.fileupload;

import com.zx.fileupload.vo.ResourceClass;

/**
 * file upload factory developer can override this method to implement more function
 * @author zhuyeqing
 *
 */
public class FileUploadFactory {
	protected static int requestTimeOut=3600;
	protected static int recycleCycle=7200;
	private static FileUpload fileUpload;
	public static final FileUpload getFileUpload(){
		if(fileUpload!=null&&fileUpload.isRunning)
			return fileUpload;
		return null;
	}
	public static final FileUpload generateFileUpload(ResourceClass resource){
		 if(fileUpload!=null&&fileUpload.isRunning)
			 fileUpload.stop();
		 fileUpload=new FileUpload(resource);
		 return fileUpload;
	}
	
}
