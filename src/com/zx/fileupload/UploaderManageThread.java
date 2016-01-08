package com.zx.fileupload;

import java.util.Date;
import java.util.Map;

import com.zx.fileupload.vo.FileUploadObject;
/**
 * fileUpload Cache map recycle thread
 * @author zhuyeqing
 *
 */
public class UploaderManageThread extends Thread {
	final Map<String, FileUploadObject> fileConfigMap;
	final int cycle;
	final long timeout;
	private boolean continueLoop=true; 
	
	
	public void setContinueLoop(boolean continueLoop) {
		this.continueLoop = continueLoop;
	}


	public int getCycle() {
		return cycle;
	}


	public UploaderManageThread(Map<String,FileUploadObject> fileConfigMap) {
		this(fileConfigMap,1800,900);
	}
	/**
	 * 构造方法生成一个文件上传管理线程
	 * @param fileConfigMap  文件上传管理map
	 * @param cycle	循环周期
	 */
	public UploaderManageThread(Map<String,FileUploadObject> fileConfigMap, int cycle,long timeout) {
		super();
		this.fileConfigMap = fileConfigMap;
		this.cycle = cycle*1000;
		this.timeout=timeout*1000;
	}

	
	@Override
	public void run() {
		while(continueLoop){
		synchronized (new Object()) {
			try {
				Thread.sleep(cycle);
				
			} catch (InterruptedException e) {
				if(continueLoop)
					return;
			}
		recycleFileConfig();
			}
		}
	}


	private void recycleFileConfig() {
		synchronized (fileConfigMap) {
			for (String fileConfigKey : fileConfigMap.keySet()) {
				if (new Date().getTime() > fileConfigMap.get(fileConfigKey)
						.getLastUploaded() + timeout) {
					FileUploadObject config = fileConfigMap.get(fileConfigKey);
					fileConfigMap.remove(fileConfigKey);
				}
			}
		}
	}

}
