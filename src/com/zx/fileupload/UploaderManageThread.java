package com.zx.fileupload;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Map;
import com.zx.fileupload.vo.FileConfig;
/**
 */
public class UploaderManageThread extends Thread {
	final Map<String,FileConfig> fileConfigMap;
	final int cycle;
	final long timeout;
	private boolean continueLoop=true; 
	
	
	public void setContinueLoop(boolean continueLoop) {
		this.continueLoop = continueLoop;
	}


	public int getCycle() {
		return cycle;
	}


	public UploaderManageThread(Map<String,FileConfig> fileConfigMap) {
		this(fileConfigMap,1800,900);
	}
	/**
	 * 构造方法生成一个文件上传管理线程
	 * @param fileConfigMap  文件上传管理map
	 * @param cycle	循环周期
	 */
	public UploaderManageThread(Map<String,FileConfig> fileConfigMap, int cycle,long timeout) {
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
						.getLastModified() + timeout) {
					FileConfig config = fileConfigMap.get(fileConfigKey);
					if (config.getParts().isEmpty()) {
						config.getCfgFile().deleteOnExit();
					} else {
						try (ObjectOutputStream out = new ObjectOutputStream(
								new FileOutputStream(fileConfigMap.get(
										fileConfigKey).getCfgFile()));) {
							out.writeObject(fileConfigMap.get(fileConfigKey));
							out.flush();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					fileConfigMap.remove(fileConfigKey);
				}
			}
		}
	}

}
