package com.zx.fileupload;

import java.io.InputStream;

public class FilePartObject {
	String fileId;
	int sequence;
	long startIndex;
	int length;
	InputStream in;
	public String getFileId() {
		return fileId;
	}
	public int getSequence() {
		return sequence;
	}
	public long getStartIndex() {
		return startIndex;
	}
	public int getLength() {
		return length;
	}
	public InputStream getIn() {
		return in;
	}
	public FilePartObject(String fileId, int sequence, long startIndex,
			int length, InputStream in) {
		super();
		this.fileId = fileId;
		this.sequence = sequence;
		this.startIndex = startIndex;
		this.length = length;
		this.in = in;
	}
	
}
