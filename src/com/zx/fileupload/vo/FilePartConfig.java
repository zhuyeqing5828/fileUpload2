package com.zx.fileupload.vo;

import java.io.Serializable;
/**
 * 文件各个分片信息配置类
 * @author acer
 *
 */
public class FilePartConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	long startIndex;
	long endIndex;
	boolean finished;
	public long getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(long startIndex) {
		this.startIndex = startIndex;
	}
	public long getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(long endIndex) {
		this.endIndex = endIndex;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (endIndex ^ (endIndex >>> 32));
		result = prime * result + (int) (startIndex ^ (startIndex >>> 32));
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
		FilePartConfig other = (FilePartConfig) obj;
		if (endIndex != other.endIndex)
			return false;
		if (startIndex != other.startIndex)
			return false;
		return true;
	}
	
}
