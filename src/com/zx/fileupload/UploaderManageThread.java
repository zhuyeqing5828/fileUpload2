package com.zx.fileupload;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zx.fileupload.vo.FileUploadObject;
/**
 * fileUpload Cache map recycle thread
 * @author zhuyeqing
 *
 */
public class UploaderManageThread extends Thread {
	final HashMap<String, FileUploadObject> fileConfigMap;
	final int cycle;
	final int timeout;
	private boolean continueLoop=true; 
	
	
	public void setContinueLoop(boolean continueLoop) {
		this.continueLoop = continueLoop;
	}


	public int getCycle() {
		return cycle;
	}
	

	public int getTimeout() {
		return timeout;
	}


	public UploaderManageThread(HashMap<String,FileUploadObject> fileConfigMap) {
		this(fileConfigMap,1800,900);
	}
	/**
	 * 构造方法生成一个文件上传管理线程
	 * @param fileConfigMap  文件上传管理map
	 * @param cycle	循环周期
	 */
	public UploaderManageThread(HashMap<String,FileUploadObject> fileConfigMap, int cycle,int timeout) {
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
		System.out.println("recycle unfinished file");
			}
		}
	}


	private void recycleFileConfig() {
		synchronized (fileConfigMap) {
			for (String fileConfigKey :((HashMap<String, FileUploadObject>) fileConfigMap.clone()).keySet()) {
				if (new Date().getTime() > fileConfigMap.get(fileConfigKey)
						.getLastUploaded() + timeout) {
					FileUploadObject config = fileConfigMap.get(fileConfigKey);
					fileConfigMap.remove(fileConfigKey);
					System.out.println( fileConfigKey+" has been removed");
				}
			}
		}
	}

}
