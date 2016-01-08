package com.zx.fileupload;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zx.fileupload.vo.FilePartConfig;
import com.zx.fileupload.vo.FileUploadObject;

/**
 * 文件上传工具 支持大文件上传,断点续传 版本 0.60
 * 
 * @author acer
 *
 */
public class FileUpload {
	final Map<String,FileUploadProp> fileUploadPropMap;
	final Map<String,FileUploadObject> fileUploadObjectMap;
	final UploaderManageThread uploaderManageThread;
	
	/**
	 * constructer Method .Generate a New FuleUploadManagerObject
	 * @param fileUploadRequestTimeOut		fileUploadobject timeout(second),when a part uploaded then reset time counter
	 * @param recycleCycle	the cycle of check fileUploadObject timeout (second)
	 */
	public FileUpload(long fileUploadRequestTimeOut,int recycleCycle) {
		this.fileUploadPropMap=new HashMap<String,FileUploadProp>();
		this.fileUploadObjectMap=new HashMap<String,FileUploadObject>();
		this.uploaderManageThread=new UploaderManageThread(fileUploadObjectMap,recycleCycle,fileUploadRequestTimeOut);
		uploaderManageThread.start();
	}
	/**
	 * constructer Method .Generate a New FuleUploadManagerObject with Object time one hour and checking cycle two hours.
	 */
	public FileUpload(){
		this(3600,7200);
	}
	/**
	 * 销毁该对象前调用该方法
	 * warning DO NOT EXECUTE THIS METHOD EXPECT STOP THE SERVER 
	 */
	public void delete(){
		uploaderManageThread.setContinueLoop(false);
		uploaderManageThread.interrupt();
	}
	
	@Override
	protected void finalize() throws Throwable {
		delete();
		super.finalize();
	}

	/**
	 * GenerateTimeUploadPart
	 * @param fileConfig
	 */
	private void generateUploadPart(FileUploadObject fileConfig) {
		if (fileConfig.getParts().isEmpty()) {
			int partSize=fileConfig.getObjectProp().getPartSize();
			long i = 0;
			int sequence=0;
			while(true){
				if ((i+partSize >fileConfig.getFileSize())) {
					FilePartConfig filePargConfig=new FilePartConfig(i, partSize);
					fileConfig.getParts().put(sequence,filePargConfig);
					break;
				}
				fileConfig.getParts().put( sequence,new FilePartConfig(i,partSize));
				i=i+partSize;
				sequence++;
			}
		}else{
			//reset part's transport statue
			for (FilePartConfig part : fileConfig.getParts().values()) {
				part.setTransport(false);
			}
		}
	}

	private String getNeedPartsString(String objectId,int partNum) {
		FileUploadObject fileObject=fileUploadObjectMap.get(objectId);
		if(fileObject==null)
			return UploadConstent.WARN_CENCEL;
		fileObject.setLastUploaded(new Date().getTime());
		StringBuilder returnString = new StringBuilder(
				"{code:0,value:'success',id:'" + objectId + "',needMd5:"
						+ fileObject.getObjectProp().needMd5Checking()
						+ ",received:" + fileObject.getReceivedSize());
		returnString.append(doGetPartsString(objectId,fileObject, partNum)+'}');	
		return returnString.toString();
	}

	private String doGetPartsString(String objectId,
			FileUploadObject fileConfig, int partNum) {
		StringBuilder returnString = new StringBuilder(",needParts:[");
		synchronized (fileConfig.getParts()) {
			Collection<FilePartConfig> parts = fileConfig.getParts().values();
			if (parts.isEmpty()) {
				fileConfig.getObjectProp().onFileUploadFinished(objectId,
						fileConfig);
			} else {
				int i = 0;
				for (FilePartConfig filePartConfig : parts) {
					if (i < partNum)
						i++;
					else
						break;
					if (filePartConfig.isTransporting()) {
						continue;
					} else {

						returnString.append("{startIndex:"
								+ filePartConfig.getStartIndex() + ",length:"
								+ filePartConfig.getLength() + "},");
						filePartConfig.setTransport(true);
					}
				}
				returnString.deleteCharAt(returnString.length() - 1);
			}
		}
		returnString.append(']');
		return returnString.toString();
	}
	
	
	/**
	 * receive a new fileUploadObject
	 * @param fileUploadObject
	 * @return
	 */
	public String addFileUploadRequest(String fileName ,String bucketName,long length,Map<String,String[]> parameperMap) {
		
		FileUploadProp bucketProp=fileUploadPropMap.get(bucketName);
		String propId= bucketProp.getPropid(fileName,length,parameperMap);
		if(propId==null)
			return UploadConstent.WARN_REFUSED_SEERVER;
		if(propId.equals(""))
			return UploadConstent.UPLOAD_FINISHED;
		//get fileUploadObject
		FileUploadObject fileUploadObject=fileUploadObjectMap.get(propId);
		if(fileUploadObject==null){
			fileUploadObject=new FileUploadObject(bucketName,fileName,length,bucketProp);
			fileUploadObjectMap.put(propId, fileUploadObject);
			generateUploadPart(fileUploadObject);
		}
		//return needParts
		return getNeedPartsString(propId, bucketProp.getMaxTransportThreadNum());
		}
	/**
	 * transmit upload Part and operate 
	 * @param ObjectId
	 * @param sequence
	 * @param in
	 * @param MD5
	 * @return
	 */
	public String transmitUploadPart(String ObjectId,int sequence,InputStream in){
		FileUploadObject uploadObject=fileUploadObjectMap.get(ObjectId);
		if(uploadObject==null)
			return UploadConstent.ERROR_CLIENT_NOREQUEST;
		Map<Integer, FilePartConfig> filePartMap=uploadObject.getParts();
		FilePartConfig partConfig=filePartMap.get(sequence);
		if(partConfig==null)
			return UploadConstent.ERRIR_CLIENT_NOPART;
		FilePartObject partObject=new FilePartObject(ObjectId, sequence, partConfig.getStartIndex(), partConfig.getLength(), in);
		if(uploadObject.getObjectProp().onFilePartUpload(partObject)){
			synchronized (uploadObject) {
				uploadObject.setReceivedSize(uploadObject.getReceivedSize()+partConfig.getLength());	
			}
			
			filePartMap.remove(ObjectId);
		}else{
			filePartMap.get(ObjectId).setTransport(false);
		}
		
		return getNeedPartsString(ObjectId, 1);
	}
	
	public String cencelUpload(String objectId){
		 FileUploadObject uploadObject= fileUploadObjectMap.remove(objectId);
		 if(uploadObject!=null)
			 uploadObject.getObjectProp().onFileUploadCenceled(objectId,uploadObject);
		 else return UploadConstent.ERROR_CLIENT_NOREQUEST;
		 return UploadConstent.REQUEST_SUCCESS;
	}
	/**
	 * setMD5ToUploadFile
	 * @param id	upload request id	
	 * @param md5	file md5 check
	 * @return
	 */
	public String setMD5(String id, String md5) {
		 FileUploadObject uploadObject= fileUploadObjectMap.get(id);
		 if(uploadObject!=null)
			 uploadObject.setMd5Checking(md5);
		 else return UploadConstent.ERROR_CLIENT_NOREQUEST;
		 return UploadConstent.REQUEST_SUCCESS;
	}
}
