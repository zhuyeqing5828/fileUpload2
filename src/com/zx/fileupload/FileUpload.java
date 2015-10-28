package com.zx.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zx.fileupload.exception.FileUploadRuntimeException;
import com.zx.fileupload.utils.StringUtil;
import com.zx.fileupload.vo.FileConfig;


/**
 * 文件上传工具
 * 支持大文件上传,断点续传
 * 版本 0.02
 * @author acer
 *
 */
public class FileUpload {
	static final String UPLOAD_REQUEST="0";
	static final String UPLOAD_TRANSMIT="1";
	
	final FileUploadProp uploadprop;
	final Set<FileConfig> fileConfigMap;
	public FileUpload(FileUploadProp uploadProp) {
		this.uploadprop=uploadProp;
		this.fileConfigMap=new HashSet<FileConfig>();
	}
	
	public void getUploadFile(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException{
		resp.setContentType("text/json;charset=utf-8");
		String respString="";
		String reqType=req.getParameter("type");
		if(reqType!=null){
			switch (reqType) {
			case  UPLOAD_REQUEST:
				respString=doUploadFileRequest(req);
				break;
			case UPLOAD_TRANSMIT:
				respString=doUploadFileTransmit(req);
				break;
			default:
				respString=doRequestError(req);
			}
		}else{
			respString=doRequestError(req);
		}
		try {
			resp.getWriter().write(respString);
			resp.getWriter().close();
		} catch (IOException e) {
			throw new FileUploadRuntimeException("response writter exception",e);
		}
		
	}
	
	private String doRequestError(HttpServletRequest req) {
		return null;
		
	}

	public String doUploadFileRequest(HttpServletRequest req) throws IOException{
		String reqFileName= req.getParameter("fileName");
		String reqFileSize= req.getParameter("fileSize");
		String localName=uploadprop.getLocalName(reqFileName);
		if(localName==null||localName.equals(""))
			return "{code:-1,value:'file has exist'}";
		File tempFile=new File(generateFullPath(localName)+".tmp");
		File cfgFile=new File(generateFullPath(localName+".cfg"));
		if(!tempFile.exists()){
			RandomAccessFile randomAccessFile=new RandomAccessFile(tempFile, "rw");
			randomAccessFile.setLength(Long.valueOf(reqFileSize));
			randomAccessFile.close();
		}
		if(!cfgFile.exists()){
			cfgFile.createNewFile();
			
		}
		Properties properties=new Properties();
		properties.load(new FileInputStream(cfgFile));
		//properties.get("partStart");
		//properties.get("partSet");
		//properties.get("partEnd");
		String totalSize=(String) properties.get("totalSize");
		if(StringUtil.isNullString(totalSize))
			properties.setProperty("totalSize", reqFileSize);
		properties.store(new FileOutputStream(cfgFile),null);
		return "{code:0,fileName:'"+localName+"',startIndex:"+tempFile.length()+"}";
	}

	private String generateFullPath(String localname) {
		return uploadprop.getSavePath()+"/"+localname;
	}
	public String doUploadFileTransmit(HttpServletRequest req) throws IOException{
		String fileName=req.getParameter("fileName");
		File tmpFile=new File(generateFullPath(fileName)+".tmp");
		File cfgFile=new File(generateFullPath(fileName)+".cfg");
		Properties properties=new Properties();
		properties.load(new FileInputStream(cfgFile));
		OutputStream out=new FileOutputStream(tmpFile,true);
		InputStream in=req.getInputStream();
		byte b[]=new byte[1<<20];
		int i;
		while((i=in.read(b))!=-1)
			out.write(b,0,i);
		out.close();
		in.close();
		if(tmpFile.length()==Long.valueOf((String)properties.get("totalSize"))){
			tmpFile.renameTo(new File(generateFullPath(fileName)));
			return "{code:0,value:'ok'}";
		}else{
			return "{code:200,value:'need more data'}";
		}
	}
}



class Uploader{
	FileConfig fileConfig;

	public FileConfig getFileConfig() {
		return fileConfig;
	}

	public void setFileConfig(FileConfig fileConfig) {
		this.fileConfig = fileConfig;
	}
	void init(){
		
	}
	/**
	 * 当对象被销毁时调用该方法
	 */
	void destroy(){
		
	}
	/**
	 * 获得传输数据
	 * @param req
	 * @return 下一个传输块的JSON字符串
	 */
	String doUploadFileTransport(HttpServletRequest req){
		return null;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileConfig == null) ? 0 : fileConfig.hashCode());
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
		Uploader other = (Uploader) obj;
		if (fileConfig == null) {
			if (other.fileConfig != null)
				return false;
		} else if (!fileConfig.equals(other.fileConfig))
			return false;
		return true;
	}
	
}
