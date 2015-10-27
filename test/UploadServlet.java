

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zx.fileupload.FileUpload;
import com.zx.fileupload.FileUploadProp;

public class UploadServlet extends HttpServlet{
	public FileUpload fileupload;
	@Override
	public void init() throws ServletException {
		fileupload=new FileUpload(new FileUploadProp() {
			
			@Override
			public int onSuccess(String realFileName) {
				System.out.println(realFileName+" upload success");
				return 0;
			}
			
			@Override
			public String getSavePath() {
				
				return "D:/tmp";
			}
			
			@Override
			public String getLocalName(String fileName) {
				
				return fileName;
			}
		});
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		fileupload.getUploadFile(req, resp);
	}
	
}
