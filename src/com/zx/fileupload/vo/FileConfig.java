package com.zx.fileupload.vo;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * 文件上传服务器存储的配置文件
 * @author acer
 *
 */
public class FileConfig implements Serializable {

	/**
	 * init
	 */
	private static final long serialVersionUID = 10L;
	
	File file;
	File cfgFile;
	long fileSize;
	long receivedSize=0;
	Set<FilePartConfig> parts;
	long lastModified;
	
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	public long getReceivedSize() {
		return receivedSize;
	}
	public void setReceivedSize(long receivedSize) {
		this.receivedSize = receivedSize;
	}
	public File getCfgFile() {
		return cfgFile;
	}
	public void setCfgFile(File cfgFile) {
		this.cfgFile = cfgFile;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public Set<FilePartConfig> getParts() {
		return parts;
	}
	public void setParts(Set<FilePartConfig> parts) {
		this.parts = parts;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cfgFile == null) ? 0 : cfgFile.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileConfig other = (FileConfig) obj;
		if (cfgFile == null) {
			if (other.cfgFile != null)
				return false;
		} else if (!cfgFile.equals(other.cfgFile))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
		
}
