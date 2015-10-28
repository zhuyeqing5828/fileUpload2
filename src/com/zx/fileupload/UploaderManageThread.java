package com.zx.fileupload;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.zx.fileupload.vo.FileConfig;
/**
 */
public class UploaderManageThread extends Thread {
	final Set<FileConfig> fileConfigSet;
	final int cycle;
	final long timeout;
	private boolean continueLoop=true; 
	
	
	public int getCycle() {
		return cycle;
	}


	public UploaderManageThread(Set< FileConfig> fileConfigMap) {
		this(fileConfigMap,1800,900);
	}
	/**
	 * 构造方法生成一个文件上传管理线程
	 * @param fileConfigMap  文件上传管理map
	 * @param cycle	循环周期
	 */
	public UploaderManageThread(Set<FileConfig> fileConfigMap, int cycle,long timeout) {
		super();
		this.fileConfigSet = fileConfigMap;
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
		synchronized (fileConfigSet) {
			for (FileConfig fileConfig : fileConfigSet) {
				if(fileConfig.getParts().isEmpty()||new Date().getTime()>fileConfig.getLastModified()+timeout){
						try (
							ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(fileConfig.getCfgFile()));){
							out.writeObject(fileConfig);
							out.flush();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					fileConfigSet.remove(fileConfig);
						}
					}
				}

			}
		}
	}

}
