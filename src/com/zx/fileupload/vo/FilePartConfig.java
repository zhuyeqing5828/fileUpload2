package com.zx.fileupload.vo;

import java.io.Serializable;
/**
 * 文件各个分块信息配置类
 * @author acer
 *
 */
public class FilePartConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	long startIndex;
	int length;
	boolean transport;
	
	public long getStartIndex() {
		return startIndex;
	}
		public int getLength() {
		return length;
	}
	public boolean isTransporting() {
		return transport;
	}
	public void setTransport(boolean transport) {
		this.transport = transport;
	}
	
	public FilePartConfig(long startIndex, int length) {
		super();
		this.startIndex = startIndex;
		this.length = length;
		this.transport=false;
	}
}
