package com.zx.fileupload.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.zx.fileupload.FileUpload;
import com.zx.fileupload.FileUploadBucketProp;
import com.zx.fileupload.FileUploadFactory;
import com.zx.fileupload.UploadConstent;
import com.zx.fileupload.exception.FileUploadRuntimeException;
import com.zx.fileupload.utils.StringUtil;
import com.zx.fileupload.utils.XmlPaser;
import com.zx.fileupload.vo.FileUploadListener;
import com.zx.fileupload.vo.ResourceClass;

public class DoUploadServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5117879179276874361L;
	
	
	FileUpload fileUpload;
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if(!fileUpload.isRunning){//when fileUpload not started;
			writeStringToResponse(resp, UploadConstent.ERROR_STOPED);
			return;
		}
		String requestType=req.getHeader(UploadConstent.TYPE);
		if(StringUtil.isNullString(requestType)){
			writeStringToResponse(resp, UploadConstent.ERROR_FORMAT);
			return;}
		switch (requestType) {
			case UploadConstent.REQUESTTYPE_NEW:
			{
				createNewRequest(req, resp);
				break;
			}
			case UploadConstent.REQUESTTYPE_TRANSMIT:
			{
				transimtData(req, resp);
				break;
			}
			case UploadConstent.REQUESTTYPE_CENCEL:
			{
				cencelRequest(req, resp);
				break;
			}
			case UploadConstent.REQUESTTYPE_SETMD5:
			getMD5Check(req, resp);
			default: writeStringToResponse(resp, UploadConstent.ERROR_FORMAT);
		}	
	}
	private void getMD5Check(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String id=req.getHeader(UploadConstent.FILE_ID);
		String Md5=req.getHeader(UploadConstent.MD5);
		writeStringToResponse(resp, fileUpload.setMD5(id,Md5));
	}
	private void cencelRequest(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String id=req.getHeader(UploadConstent.FILE_ID);
		writeStringToResponse(resp, fileUpload.cencelUpload(id));
	}
	private void transimtData(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String id=req.getParameter(UploadConstent.FILE_ID);
		int partSequence=req.getIntHeader(UploadConstent.PART_SEQUENCE);
		InputStream partInputStream=req.getInputStream();
		String responseString=fileUpload.transmitUploadPart(id,partSequence,partInputStream);
		writeStringToResponse(resp, responseString);
	}
	private void createNewRequest(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		String fileName=req.getParameter(UploadConstent.FILE_NAME);
		String bucketName=req.getHeader(UploadConstent.BUCKET_NAME);
		Long fileLength=Long.parseLong(req.getHeader(UploadConstent.LENGTH));
		Map<String,String[]> parameterMap=req.getParameterMap();
		writeStringToResponse(resp,fileUpload.addFileUploadRequest(fileName,bucketName,fileLength,parameterMap));
	}	private void writeStringToResponse(HttpServletResponse resp, String jsonString)
			throws IOException {
		resp.setContentType("application/json;charSet=UTF-8");
		Writer writer=resp.getWriter();
			writer.write(jsonString);
			writer.flush();
			writer.close();
	}
	@Override
	public void destroy() {
		fileUpload.stop();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		String cfgpath=config.getInitParameter(UploadConstent.GLOBAL_CFGPATH);
		if(StringUtil.isNullString(cfgpath))
			cfgpath=UploadConstent.DEFAULT_CFGPATH;
		if(cfgpath.contains("classPath:"))	
			cfgpath=cfgpath.replace("classPath:", config.getServletContext().getRealPath("WEB-INF"+File.separatorChar+"classes")+File.separatorChar);
		ResourceClass resource=new ResourceClass(new HashMap<String, FileUploadBucketProp>(),new ArrayList<FileUploadListener>(),3600,7200);
		parseCfgFile(resource,new File(cfgpath));
		fileUpload=FileUploadFactory.generateFileUpload(resource);
		fileUpload.start();
		super.init(config);
	}
	public ResourceClass parseCfgFile(ResourceClass resClass,File file) {
		if(file.exists()){
			Document document=XmlPaser.xmlDomParse(file);
			Element rootNode=(Element) document.getElementsByTagName(UploadConstent.XML_FILEUPLOAD).item(0);
			Element gloCfgNode= (Element) rootNode.getElementsByTagName(UploadConstent.XML_GLOBAL_CONFIG).item(0);
			loadGlobalConfig(resClass,gloCfgNode);
			loadUploadListener(resClass,rootNode.getElementsByTagName(UploadConstent.XML_GLOBAL_LISTENER));
			loadUploadBucket(resClass,rootNode.getElementsByTagName(UploadConstent.XML_GLOBAL_BUCKET));
		}
		return resClass;
		
	}
	//TODO Xml格式合法性判断
	private void loadUploadBucket(ResourceClass resClass,NodeList elements){
		for (int i = 0; i < elements.getLength(); i++) {
			Element element=(Element) elements.item(i);
			String bucketName=element.getElementsByTagName(UploadConstent.XML_BUCKET_NAME).item(0).getTextContent();
			String bucketClass=element.getElementsByTagName(UploadConstent.XML_BUCKET_CLASS).item(0).getTextContent();
			try {
				resClass.getFileUploadPropMap().put(bucketName, (FileUploadBucketProp) Class.forName(bucketClass).newInstance());
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				throw new FileUploadRuntimeException("can't load bucket"+bucketName,e);
			}
		}
	}
	//TODO Xml格式合法性判断
	private void loadUploadListener(ResourceClass resClass,NodeList elements) {
		for (int i = 0; i < elements.getLength(); i++) {
			Element element=(Element) elements.item(i);
			String listenerName=element.getElementsByTagName(UploadConstent.XML_LISTENER_NAME).item(0).getTextContent();
			String listenerClass=element.getElementsByTagName(UploadConstent.XML_LISTENER_CLASS).item(0).getTextContent();
			try {
				resClass.getProps().add( (FileUploadListener) Class.forName(listenerClass).newInstance());
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				throw new FileUploadRuntimeException("load fileUploadListener "+listenerName+" fail",e);
			}
		}

	}
	//TODO Xml格式合法性判断
	private void loadGlobalConfig(ResourceClass resClass,Element gloCfgNode) {
		String recycleCycleString=gloCfgNode.getElementsByTagName(UploadConstent.XML_GLOBAL_RC).item(0).getTextContent();
		String timeOutString=gloCfgNode.getElementsByTagName(UploadConstent.XML_GLOBAL_TO).item(0).getTextContent();
		int recucleCycle=UploadConstent.DEFAUL_TRECYCLECYCLE;
		int timeOut=UploadConstent.DEFAULT_TIMEOUT;
		if(!StringUtil.isNullString(recycleCycleString))
			recucleCycle=Integer.parseInt(recycleCycleString);
		if(!StringUtil.isNullString(timeOutString))
			timeOut=Integer.parseInt(timeOutString);
		
	}

}
