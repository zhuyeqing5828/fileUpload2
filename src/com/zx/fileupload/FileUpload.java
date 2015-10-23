package com.zx.fileupload;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
	
	public void getUploadFile(HttpServletRequest req,HttpServletResponse resp) throws ServletException{
		resp.setContentType("application/json;charset=utf-8");
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

	public String doUploadFileRequest(HttpServletRequest req){
		return null;
		
	}
	public String doUploadFileTransmit(HttpServletRequest req){
		return null;
		
	}
}
