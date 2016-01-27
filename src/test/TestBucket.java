package test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Map;

import com.zx.fileupload.FileUploadBucketProp;
import com.zx.fileupload.vo.FilePartObject;
import com.zx.fileupload.vo.FileUploadObject;

public class TestBucket implements FileUploadBucketProp{
	String path="D:\\tmp\\";
	@Override
	public boolean needMd5Checking() {
		return true;
	}
	/**
	 * example. 当收到一个文件上传分片时操作
	 */
	@Override
	public boolean onFilePartUpload(FilePartObject filePartObject) {
		String fileName=filePartObject.getFileId();
	//	System.out.println("upload "+ fileName +" partseq "+filePartObject.getSequence());
		File file=new File(path+fileName);
		try(RandomAccessFile randomaccess=new RandomAccessFile(file, "rw");) {
			randomaccess.seek(filePartObject.getStartIndex());
			InputStream in= filePartObject.getInputStream();
			int count=0;
			int i=0;
			byte[] b=new byte[1<<20];
			while ((i=in.read(b,0,b.length))!=-1) {
				count+=i;
				randomaccess.write(b,0,i);
			}
			return count==filePartObject.getLength();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void onFileUploadFinished(String fileId,
			FileUploadObject uploadObject) {
		
		File file=new File(path+fileId);
	
	}
	@Override
	public String getPropid(String fileName, long length,
			Map<String, String[]> parameterMap) {
		return fileName;
	}
	@Override
	public int getPartSize() {
		
		return 1<<20;
	}
	@Override
	public int getMaxTransportThreadNum() {
		return 3;
	}
	
	@Override
	public void onFileUploadCenceled(String fileId,
			FileUploadObject uploadObject) {
		System.out.println(fileId +"cencelled");
	}
	@Override
	public int geTransportPartTimeOut() {
		
		return 90;
	}

}
