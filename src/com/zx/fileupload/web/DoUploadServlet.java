package com.zx.fileupload.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zx.fileupload.FileUpload;
import com.zx.fileupload.FileUploadFactory;
import com.zx.fileupload.UploadConstent;
import com.zx.fileupload.utils.StringUtil;

public class DoUploadServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5117879179276874361L;
	FileUpload fileUpload=FileUploadFactory.getFileUpload();
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String requestType=req.getHeader(UploadConstent.TYPE);
		if(StringUtil.isNullString(requestType)){
			writeStringToResponse(resp, UploadConstent.ERROR_FORMAT);
			return;}
		switch (requestType) {
			case UploadConstent.REQUESTTYPE_NEW:
			{
				String fileName=req.getHeader(UploadConstent.FILE_NAME);
				String bucketName=req.getHeader(UploadConstent.BUCKET_NAME);
				Long fileLength=Long.parseLong(req.getHeader(UploadConstent.LENGTH));
				Map<String,String[]> parameterMap=req.getParameterMap();
				writeStringToResponse(resp,fileUpload.addFileUploadRequest(fileName,bucketName,fileLength,parameterMap));
				break;
			}
			case UploadConstent.REQUESTTYPE_TRANSMIT:
			{
				String id=req.getHeader(UploadConstent.FILE_ID);
				int partSequence=req.getIntHeader(UploadConstent.PART_SEQUENCE);
				InputStream partInputStream=req.getInputStream();
				String responseString=fileUpload.transmitUploadPart(id,partSequence,partInputStream);
				writeStringToResponse(resp, responseString);
				break;
			}
			case UploadConstent.REQUESTTYPE_CENCEL:
			{
				String id=req.getHeader(UploadConstent.FILE_ID);
				writeStringToResponse(resp, fileUpload.cencelUpload(id));
				break;
			}
			case UploadConstent.REQUESTTYPE_SETMD5:
				String id=req.getHeader(UploadConstent.FILE_ID);
				String Md5=req.getHeader(UploadConstent.MD5);
				writeStringToResponse(resp, fileUpload.setMD5(id,Md5));
			default: writeStringToResponse(resp, UploadConstent.ERROR_FORMAT);
		}	
	}	private void writeStringToResponse(HttpServletResponse resp, String jsonString)
			throws IOException {
		resp.setContentType("text/json;charSet=UTF-8");
		Writer writer=resp.getWriter();
			writer.write(jsonString);
			writer.flush();
	}
	
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
	}
	
}
