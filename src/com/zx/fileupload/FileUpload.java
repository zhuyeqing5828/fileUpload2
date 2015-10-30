package com.zx.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zx.fileupload.exception.FileUploadRuntimeException;
import com.zx.fileupload.vo.FileConfig;
import com.zx.fileupload.vo.FilePartConfig;

/**
 * 文件上传工具 支持大文件上传,断点续传 版本 0.02
 * 
 * @author acer
 *
 */
public class FileUpload {
	static final String UPLOAD_REQUEST = "0";
	static final String UPLOAD_TRANSMIT = "1";

	final FileUploadProp uploadprop;
	final Map<String, FileConfig> fileConfigMap;
	final String savePath;
	final int partSize;
	final int transportPartsPerFile;

	/**
	 * 生成一个文件上传工具对象
	 * 
	 * @param uploadProp
	 *            回调函数对象
	 * @param savePath
	 *            文件存储目录
	 * @param partSize
	 *            分片上传时分片大小
	 */
	public FileUpload(FileUploadProp uploadProp, String savePath, int partSize,
			int maxPartTransportPerFile) {
		this.uploadprop = uploadProp;
		this.fileConfigMap = Collections
				.synchronizedMap(new HashMap<String, FileConfig>());
		this.savePath = savePath;
		this.partSize = partSize;
		this.transportPartsPerFile = maxPartTransportPerFile;
	}

	/**
	 * 生成一个分片大小为1M的文件上传工具对象
	 * 
	 * @param uploadProp
	 *            回调函数对象
	 * @param savePath
	 *            文件存储目录
	 */
	public FileUpload(FileUploadProp uploadprop,
			Map<String, FileConfig> fileConfigMap, String savePath) {
		this(uploadprop, savePath, 1 << 20, 3);
	}

	/**
	 * 处理文件上传请求的主方法
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public void getUploadFile(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/json;charset=utf-8");
		String respString = "";
		String reqType = req.getParameter("type");
		if (reqType != null) {
			switch (reqType) {
			case UPLOAD_REQUEST:
				respString = doUploadFileRequest(req);
				break;
			case UPLOAD_TRANSMIT:
				respString = doUploadFileTransmit(req);
				break;
			default:
				respString = doRequestError(req);
			}
		} else {
			respString = doRequestError(req);
		}
		try {
			resp.getWriter().write(respString);
			resp.getWriter().close();
		} catch (IOException e) {
			throw new FileUploadRuntimeException("response writter exception",
					e);
		}

	}

	private String doRequestError(HttpServletRequest req) {
		return null;

	}
	/**
	 * 处理上传请求的方法
	 * @param req
	 * @return
	 * @throws IOException
	 */
	private String doUploadFileRequest(HttpServletRequest req) throws IOException {
		String reqFileName = req.getParameter("fileName");
		String reqFileSize = req.getParameter("fileSize");
		String localName = uploadprop.getLocalName(reqFileName);
		FileConfig fileConfig;
		if (localName == null || localName.equals(""))
			return "{code:-1,value:'file has exist'}";
		fileConfig = fileConfigMap.get(localName);
		if (fileConfig == null) {
			fileConfig = new FileConfig();
			fileConfig.setFile(new File(generateFullPath(localName) + ".tmp"));
			fileConfig
					.setCfgFile(new File(generateFullPath(localName + ".cfg")));
			fileConfig.setFileSize(Long.valueOf(reqFileSize));
			fileConfig.setLastModified(new Date().getTime());
			fileConfig.setParts(Collections
					.synchronizedSet(new HashSet<FilePartConfig>()));
			fileConfigMap.put(localName, fileConfig);
		}
		File tmpFile = fileConfig.getFile();
		File cfgFile = fileConfig.getCfgFile();

		if (!tmpFile.exists()) {
			RandomAccessFile randomAccessFile = new RandomAccessFile(tmpFile,
					"rw");
			randomAccessFile.setLength(Long.valueOf(reqFileSize));
			randomAccessFile.close();
		}
		if (!cfgFile.exists()) {
			cfgFile.createNewFile();
		}
		if (fileConfig.getParts().isEmpty()) {
			int partsNum = (int) (tmpFile.length() / partSize);
			int i = 0;
			for (; i < partsNum; i = i + partSize + 1) {
				fileConfig.getParts().add(new FilePartConfig(i, i + partSize));
			}
			if (!(i > tmpFile.length())) {
				fileConfig.getParts().add(
						new FilePartConfig(i, tmpFile.length()));
			}
		}
		
		
		/*
		 * responseJSON { code:0, fileName: localFileName, received: received
		 * Data, needParts:[{startIndex:startIndex,endIndex:endIndex},....] }
		 */
		StringBuilder returnString = getNeedPartsString(localName, fileConfig,transportPartsPerFile);
		return returnString.toString();
	}

	private StringBuilder getNeedPartsString(String localName,
			FileConfig fileConfig,int partNum) {
		fileConfig.setLastModified(new Date().getTime());
		StringBuilder returnString = new StringBuilder("{code:0,fileName:'" + localName + "',received:"
				+ fileConfig.getReceivedSize() + ",needParts:[");
			Set<FilePartConfig> parts = fileConfig.getParts();
			synchronized (parts) {
				int i=0;
				for (FilePartConfig filePartConfig : parts) {
					if (filePartConfig.isTransporting()) {
						continue;
					} else {
						returnString.append("{startIndex:"
								+ filePartConfig.getStartIndex() + ",endIndex:"
								+ filePartConfig.getEndIndex() + "},");
						filePartConfig.setTransport(true);
						if(i<partNum)
							i++;
						else break;
					}
			}
			returnString.append("{startIndex:" + fileConfig.getParts() + "}");
			returnString.append("]}");
		}
		return returnString;
	}

	private String generateFullPath(String localname) {
		return savePath + "/" + localname;
	}

	public String doUploadFileTransmit(HttpServletRequest req)
			throws IOException {
		String localName = req.getParameter("fileName");
		FileConfig fileConfig;
		synchronized (fileConfigMap) {
			fileConfig = fileConfigMap.get(localName);
			if (fileConfig == null) {
				File cfgFile=new File(generateFullPath(localName));
				if (!cfgFile.exists()) {
					return "{code:402,value:'illegal file Transmit request '}";
				}
				ObjectInputStream ois=new ObjectInputStream(new FileInputStream(cfgFile));
				try {
					fileConfig=(FileConfig)ois.readObject();
				} catch (ClassNotFoundException e) {
					throw new FileUploadRuntimeException(e);
				}
				fileConfig.setLastModified(new Date().getTime());
				fileConfigMap.put(localName, fileConfig);
			}
		}
		File tmpFile = fileConfig.getFile();
		long startIndex=Long.valueOf(req.getParameter("startIndex"));
		long endIndex=Long.valueOf(req.getParameter("endIndex"));
		FilePartConfig part=new FilePartConfig(startIndex, endIndex);
		if(fileConfig.getParts().contains(part)){
			RandomAccessFile raf=new RandomAccessFile(tmpFile, "rw");
			raf.seek(startIndex);
			InputStream in = req.getInputStream();
			byte b[] = new byte[partSize];
			int i;
			while ((i = in.read(b)) != -1){
				raf.write(b, 0, i);
			}
			raf.close();
			in.close();
			synchronized (fileConfig) {
				fileConfig.getParts().remove(part);
				fileConfig.setReceivedSize(fileConfig.getReceivedSize()+endIndex-startIndex);
				// on file finished
				if(fileConfig.getParts().isEmpty()){
					fileConfig.getFile().renameTo(new File(localName));
					uploadprop.onSuccess(localName);
					return "{code:200,value:'finished'}";
				}
				
			}
		}else{
			return "{code:404,value:'illegal part Transmit request '}";
		}
		
		StringBuilder returnString =getNeedPartsString(localName, fileConfig, 1);
		
		return returnString.toString();
		
	}
}
