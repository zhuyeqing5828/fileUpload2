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
	TransportStatue transportStatue;
	long transportTime;
	public long getStartIndex() {
		return startIndex;
	}
		public int getLength() {
		return length;
	}
		
	
	public TransportStatue getTransportStatue() {
			return transportStatue;
		}
	public void setTransportStatue(TransportStatue transportStatue) {
			this.transportStatue = transportStatue;
		}
	public long getTransportTime() {
		return transportTime;
	}
	public void setTransportTime(long transportTime) {
		this.transportTime = transportTime;
	}
	public FilePartConfig(long startIndex, int length) {
		super();
		this.startIndex = startIndex;
		this.length = length;
		this.transportStatue=TransportStatue.INLINE;
	}
}
