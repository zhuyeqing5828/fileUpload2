package com.zx.fileupload;
/**
 * 文件上传工具回调函数
 * @author acer
 *
 */
public interface FileUploadProp {
	
	/**
	 * 通过上传的文件名获得服务器存储的文件名
	 * @param fileName 上传的文件名
	 * @return 服务器存储的文件名
	 */
	String getLocalName(String fileName);
	/**
	 * 当文件上传完毕后调用实现此接口的方法
	 * @param realFileName	上传完毕的服务器存储的文件名
	 * @return 文件状态: -1 文件上传失败,1文件部分上传成功,2文件上传完成
	 */
	int onSuccess(String realFileName);
	/**
	 * 通过此方法获得文件上传后的保存路径
	 * @return
	 */
	String getSavePath();
}
