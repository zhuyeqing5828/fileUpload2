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
	final Map<String,FileUploadObject> fileUploadObjectMap;
	private UploaderManageThread uploaderManageThread;
	public boolean  isRunning; 
	final ResourceClass fileUploadProp;

	public void setRecycleCycle(int recycleCycle) {
		if(!isRunning)
			this.fileUploadProp.setRecycleCycle(recycleCycle);
	}
	
	public void setFileUploadRequestTimeOut(int fileUploadRequestTimeOut) {
		if(isRunning)
			this.fileUploadProp.setFileUploadRequestTimeOut(fileUploadRequestTimeOut);
	}
	/**
	 * constructer Method .Generate a New FuleUploadManagerObject
	 * @param fileUploadRequestTimeOut		fileUploadobject timeout(second),when a part uploaded then reset time counter
	 * @param recycleCycle	the cycle of check fileUploadObject timeout (second)
	 */
	public FileUpload(ResourceClass resourclass) {
		this.fileUploadObjectMap=new HashMap<String,FileUploadObject>();
		this.isRunning=false;
		this.fileUploadProp=resourclass;
	
	}
	
	@Override
	protected void finalize() throws Throwable {
		stop();
		super.finalize();
	}
	/**
	 * 添加一组新的配置信息(bucket)
	 * @param bucketName	名称
	 * @param prop	信息对象
	 * @return		返回原有的配置信息对象,如果原有对象为null,则返回null
	 */
	public FileUploadBucketProp addUploadBucket(String bucketName,FileUploadBucketProp prop){
		return fileUploadProp.getFileUploadPropMap().put(bucketName, prop);
	}
	
	/**
	 * receive a new fileUploadObject
	 * @param fileUploadObject
	 * @return
	 */
	public String addFileUploadRequest(String fileName ,String bucketName,long length,Map<String,String[]> parameperMap) {
		
		FileUploadBucketProp bucketProp=fileUploadProp.getFileUploadPropMap().get(bucketName);
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
	/**
	 * stopFileUpload
	 */
	public void stop() {
		if(isRunning){
		uploaderManageThread.setContinueLoop(false);
		uploaderManageThread.interrupt();
		isRunning=false;
		onStop();
		}
	}
	/**
	 * start FileUpload
	 */
	public void start() {
		if(isRunning)
			return;
		onStart();
		this.uploaderManageThread=new UploaderManageThread(fileUploadObjectMap,fileUploadProp.getRecycleCycle(),fileUploadProp.getFileUploadRequestTimeOut());
		uploaderManageThread.start();
		isRunning=true;
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
			Collection<Integer> partSeqs = fileConfig.getParts().keySet();
			if (partSeqs.isEmpty()) {
				fileConfig.getObjectProp().onFileUploadFinished(objectId,
						fileConfig);
			} else {
				int i = 0;
				for (int partSeq : partSeqs) {
					FilePartConfig filePartConfig=fileConfig.getParts().get(partSeq);
					
					if (filePartConfig.isTransporting()) {
						continue;
					} else if (i >= partNum)
							break;
						else {
						returnString.append("{startIndex:"
								+ filePartConfig.getStartIndex() + ",length:"
								+ filePartConfig.getLength() + "partSeq:"+partSeq+"},");
						filePartConfig.setTransport(true);
						i++;
					}
				}
				returnString.deleteCharAt(returnString.length() - 1);
			}
		}
		returnString.append(']');
		return returnString.toString();
	}
	
	
	
	private void onStart() {
		for (FileUploadProp fileUploadProp2 : fileUploadProp.getProps()) {
			fileUploadProp2.beforeStarted(this);
		}
		
	}
	private void onStop(){
		for (FileUploadProp fileUploadProp2 : fileUploadProp.getProps()) {
			fileUploadProp2.afterStopped(this);
		}
	}

}
