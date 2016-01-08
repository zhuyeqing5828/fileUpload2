package com.zx.fileupload;

public class FileUploadFactory {
	private static final FileUpload fileUpload=new FileUpload();
		public static FileUpload getFileUpload(){
			return fileUpload;
		}
}
