package com.zx.fileupload;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zx.fileupload.vo.FilePartConfig;
import com.zx.fileupload.vo.FilePartObject;
import com.zx.fileupload.vo.FileUploadObject;
import com.zx.fileupload.vo.FileUploadListener;
import com.zx.fileupload.vo.ResourceClass;
import com.zx.fileupload.vo.TransportStatue;

/**
 * 文件上传工具 支持大文件上传,断点续传 版本 0.60
 * 
 * @author acer
 *
 */
public class FileUpload {
	final HashMap<String,FileUploadObject> fileUploadObjectMap;
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
		synchronized (fileUploadObjectMap) {
			FileUploadObject fileUploadObject=fileUploadObjectMap.get(propId);
			if(fileUploadObject==null){
				fileUploadObject=new FileUploadObject(bucketName,fileName,length,bucketProp);
				fileUploadObjectMap.put(propId, fileUploadObject);
			}
		}
		generateUploadPart(fileUploadObjectMap.get(propId));
		//return needParts
		return getNeedPartsString(propId, bucketProp.getMaxTransportThreadNum());
		}
	/**
	 * transmit upload Part and operate 
	 * @param ObjectId
	 * @param sequence
	 * @param in
	 * @return
	 */
	public String transmitUploadPart(String ObjectId,int sequence,InputStream in){
		FileUploadObject uploadObject=fileUploadObjectMap.get(ObjectId);
		if(uploadObject==null){
			System.out.println("ObjectId is "+ObjectId);
			return UploadConstent.ERROR_CLIENT_NOREQUEST;
			
		}
		Map<Integer, FilePartConfig> filePartMap=uploadObject.getParts();
		FilePartConfig partConfig=filePartMap.get(sequence);
		try{
			if(partConfig==null||partConfig.getTransportStatue()!=TransportStatue.WAITTING)
				return UploadConstent.ERRIR_CLIENT_NOPART;
		synchronized (partConfig) {
			partConfig.setTransportStatue(TransportStatue.TRANSPORTING);
			FilePartObject partObject=new FilePartObject(ObjectId, sequence, partConfig.getStartIndex(), partConfig.getLength(), in);
			if(uploadObject.getObjectProp().onFilePartUpload(partObject)){
				uploadObject.addReceivedSize(partConfig.getLength());	
				filePartMap.remove(sequence);
			}else{
				filePartMap.get(sequence).setTransportStatue(TransportStatue.INLINE);
			}
		}
		} catch (NullPointerException e){
			e.printStackTrace();
			System.out.println(sequence+" IS NOT USED");
			return UploadConstent.ERRIR_CLIENT_NOPART;
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
		if (fileConfig.getParts().isEmpty()&&fileConfig.getReceivedSize()==0) {
			int partSize=fileConfig.getObjectProp().getPartSize();
			long i = 0;
			int sequence=0;
			while(true){
				if ((i+partSize >fileConfig.getFileSize())) {
					FilePartConfig filePargConfig=new FilePartConfig(i, (int) (fileConfig.getFileSize()%partSize));
					fileConfig.getParts().put(sequence,filePargConfig);
					break;
				}
				fileConfig.getParts().put( sequence,new FilePartConfig(i,partSize));
				i=i+partSize;
				sequence++;
			}
			fileConfig.setSequence(sequence);
		}
	}

	private String getNeedPartsString(String objectId,int partNum) {
		FileUploadObject fileObject=fileUploadObjectMap.get(objectId);
		if(fileObject==null)
			return UploadConstent.WARN_CENCEL;
		fileObject.setLastUploaded(new Date().getTime());
		StringBuilder returnString = new StringBuilder(
				"{\"code\":0,\"value\":\"success\",\"id\":\"" + objectId + "\",\"needMd5\":"
						+ fileObject.getObjectProp().needMd5Checking()
						+ ",\"received\":" + fileObject.getReceivedSize());
		returnString.append(doGetPartsString(objectId,fileObject, partNum)+'}');	
		return returnString.toString();
	}

	private String doGetPartsString(String objectId,
			FileUploadObject fileConfig, int partNum) {
		StringBuilder returnString = new StringBuilder(",\"needParts\":[");
		synchronized (fileConfig.getParts()) {
			@SuppressWarnings("unchecked")
			Collection<Integer> partSeqs =((HashMap<Integer, FilePartConfig>)fileConfig.getParts().clone()).keySet();
			if (partSeqs.isEmpty()) {
				fileConfig.getObjectProp().onFileUploadFinished(objectId,fileConfig);
				returnString.append("{\"partSeq\":-1,\"startIndex\":0 ,\"length\":0}");
				} else {
				int i = 0;
				for (Integer partSeq : partSeqs) {
					FilePartConfig filePartConfig=fileConfig.getParts().get(partSeq);
					if(filePartConfig==null)
						continue;
					switch (filePartConfig.getTransportStatue()) {
						case WAITTING:
							if(new Date().getTime()-filePartConfig.getTransportTime()>fileConfig.getObjectProp().geTransportPartTimeOut()*1000){
								if (filePartConfig.getTransportStatue()==TransportStatue.WAITTING){
									fileConfig.getParts().remove(partSeq);
									int sequence=fileConfig.getSequence();
									fileConfig.getParts().put(++sequence, filePartConfig);
									fileConfig.setSequence(sequence);
									generateNeedPart(returnString, sequence, filePartConfig);
									i++;
								}
							}break;
						case INLINE:
							{
								generateNeedPart(returnString, partSeq, filePartConfig);
								i++;
							}break;
						default:
							break;
						}
					if (i >= partNum)
						break;
					}
					returnString.deleteCharAt(returnString.length() - 1);
				}
		}
		
		returnString.append(']');
		return returnString.toString();
	}

	private void generateNeedPart(StringBuilder returnString, int partSeq,
			FilePartConfig filePartConfig) {
		returnString.append("{\"startIndex\":"
				+ filePartConfig.getStartIndex() + ",\"length\":"
				+ filePartConfig.getLength() + ",\"partSeq\":"+partSeq+"},");
		filePartConfig.setTransportTime(new Date().getTime());
		filePartConfig.setTransportStatue(TransportStatue.WAITTING);
	}
	
	
	
	private void onStart() {
		for (FileUploadListener fileUploadProp2 : fileUploadProp.getProps()) {
			fileUploadProp2.beforeStarted(this);
		}
		
	}
	private void onStop(){
		for (FileUploadListener fileUploadProp2 : fileUploadProp.getProps()) {
			fileUploadProp2.afterStopped(this);
		}
	}

}
