package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.zx.fileupload.CommonFileUploadProp;
import com.zx.fileupload.FilePartObject;
import com.zx.fileupload.FileUpload;
import com.zx.fileupload.FileUploadFactory;
import com.zx.fileupload.vo.FileUploadObject;

public class TestServlet extends HttpServlet{

	@Override
	public void init(ServletConfig config) throws ServletException {
		FileUpload fileUpload=FileUploadFactory.getFileUpload();
		fileUpload.addUploadBucket("test", new CommonFileUploadProp() {
			String path="D:\\tmp\\";
			@Override
			public String getPropid(String fileName, long length,
					Map<String, String[]> parameterMap) {
				return fileName;
			}
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
				File file=new File(path+fileName);
				try(RandomAccessFile randomaccess=new RandomAccessFile(file, "rw");) {
					randomaccess.seek(filePartObject.getStartIndex());
					InputStream in= filePartObject.getInputStream();
					int count=0;
					int i=0;
					byte[] b=new byte[1<<10];
					while ((i=in.read(b, i, 1<<10))!=-1) {
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
				String md5Code= uploadObject.getMd5Checking();
				
				File file=new File(path+fileId);
				MD5Util md5Util=new MD5Util();
				try {
					String code=md5Util.getFileMD5String(file);
					boolean result=code.equals(md5Code);
					System.out.println(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
		super.init(config);
	}

	
}
