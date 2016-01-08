package com.zx.fileupload;

import java.util.Map;

import com.zx.fileupload.vo.FileUploadObject;

public abstract class CommonFileUploadProp implements FileUploadProp{

	@Override
	public abstract String getPropid(String fileName, long length,
			Map<String, String[]> parameterMap);

	@Override
	public abstract String onFileUploadRequest(FileUploadRequestProp prop) ;
/**
 * default  partSize 1M
 */
	@Override
	public int getPartSize() {
		return 1<<20;
	}
/**
 * default upload Thread num Per request
 */
	@Override
	public int getMaxTransportThreadNum() {
		return 3;
	}

	@Override
	public boolean onFilePartUpload(FilePartObject filePartObject) {
		return false;
	}

	@Override
	public void onFileUploadFinished(String fileId,
			FileUploadObject uploadObject) {
		
	}

	@Override
	public void onFileUploadCenceled(String fileId,
			FileUploadObject uploadObject) {
		
	}
	@Override
	public boolean needMd5Checking() {
		return false;
	}

}
