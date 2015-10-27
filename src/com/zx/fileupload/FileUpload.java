package com.zx.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zx.fileupload.utils.StringUtil;


/**
 * 文件上传工具
 * 支持大文件上传,断点续传
 * 版本 0.01
 * @author acer
 *
 */
public class FileUpload {
	static final String UPLOAD_REQUEST="0";
	static final String UPLOAD_TRANSMIT="1";
	
	final FileUploadProp uploadprop;

	public FileUpload(FileUploadProp uploadProp) {
		this.uploadprop=uploadProp;
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
		if(!tempFile.exists())
			tempFile.createNewFile();
		if(!cfgFile.exists()){
			cfgFile.createNewFile();
			FileWriter fileWriter=new FileWriter(tempFile);
			fileWriter.write("");
			fileWriter.close();
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
