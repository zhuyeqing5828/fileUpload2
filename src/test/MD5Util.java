package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

protected static final char HEXDIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','A', 'B', 'C', 'D', 'E', 'F' };
protected MessageDigest messagedigest = null;
public MD5Util(String s) {
	 try{
		    messagedigest = MessageDigest.getInstance("s");
		   }catch(NoSuchAlgorithmException nsaex){
		    System.err.println(MD5Util.class.getName()+"初始化失败，MessageDigest不支持MD5Util。");
		    nsaex.printStackTrace();
		   }
}
public MD5Util(){
	this("Md5");
}

public String getFileMD5String(File file) throws IOException {
   FileInputStream in = new FileInputStream(file);
   FileChannel ch = in.getChannel();
   MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
   messagedigest.update(byteBuffer);
   return bufferToHex(messagedigest.digest());
}

public String getMD5String(String s) {
   return getMD5String(s.getBytes());
}

public String getMD5String(byte[] bytes) {
   messagedigest.update(bytes);
   return bufferToHex(messagedigest.digest());
}

private String bufferToHex(byte bytes[]) {
   return bufferToHex(bytes, 0, bytes.length);
}

private String bufferToHex(byte bytes[], int m, int n) {
   StringBuffer stringbuffer = new StringBuffer(2 * n);
   int k = m + n;
   for (int l = m; l < k; l++) {
    appendHexPair(bytes[l], stringbuffer);
   }
   return stringbuffer.toString();
}


private void appendHexPair(byte bt, StringBuffer stringbuffer) {
   char c0 = HEXDIGITS[(bt & 0xf0) >> 4];
   char c1 = HEXDIGITS[bt & 0xf];
   stringbuffer.append(c0);
   stringbuffer.append(c1);
}

public boolean checkPassword(String password, String md5PwdStr) {
   String s = getMD5String(password);
   return s.equals(md5PwdStr);
}

}
