package test;

import com.zx.fileupload.FileUpload;
import com.zx.fileupload.vo.FileUploadListener;

public class TestListener implements FileUploadListener{

	@Override
	public void beforeStarted(FileUpload fileUpload) {
		System.out.println("before started");
	}

	@Override
	public void afterStopped(FileUpload fileUpload) {
		System.out.println("after stopped");
	}

}
