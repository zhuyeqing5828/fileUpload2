package com.zx.fileupload;

import java.util.Map;

import com.zx.fileupload.vo.FileUploadObject;

/**
 * 文件上传工具回调函数
 * @author acer
 *
 */
public interface FileUploadProp {
	/**
	 * 设置每个文件上传配置的唯一标识
	 * @return
	 */
	String getPropid(String fileName,long length,Map<String, String[]> parameterMap);
	/**
	 * 当收到一个文件上传请求的操作
	 * @param prop
	 * @return 该文件上传请求的id(唯一,在整个fileUpload对象) 如果为null 则拒绝该上传请求
	 */
	String onFileUploadRequest(FileUploadRequestProp prop);

	/**
	 * 设置分片大小
	 * @return
	 */
	int getPartSize();
	/**
	 * 设置最大同时上传的分片数量
	 * @return
	 */
	int getMaxTransportThreadNum();
	/**
	 * 当收到一个文件块后的操作
	 * @param filePartObject
	 * @return true为文件块生效,false为需要重传文件块
	 */
	boolean onFilePartUpload(FilePartObject filePartObject);
	/**
	 * 当文件上传完毕后操作
	 * @param prop
	 */
	void onFileUploadFinished(String fileId,FileUploadObject uploadObject);
	/**
	 * 当客户端文件上传终止后的操作
	 * @param uploadObject 
	 * @param prop
	 */
	void onFileUploadCenceled(String fileId, FileUploadObject uploadObject);
	/**
	 * 是否需要Md5校验
	 * @return
	 */
	boolean needMd5Checking();
}
