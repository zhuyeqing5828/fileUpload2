package com.zx.fileupload;

public class UploadConstent {
	public static final String TYPE="type";
	public static final String FILE_NAME="fileName";
	public static final String BUCKET_NAME="bucketName";
	public static final String LENGTH="length";
	public static final String FILE_ID="id";
	public static final String MD5="md5Code";
	
	//part parameter
	public static final String PART_SEQUENCE = "sequence"; 
	//request types
	public static final String REQUESTTYPE_NEW="New";
	public static final String REQUESTTYPE_TRANSMIT="Transmit";
	public static final String REQUESTTYPE_CENCEL="Cencel";
	public static final String REQUESTTYPE_SETMD5 = "SetMD5";

public static final String WARN_CENED="{code:100,value:'uploadCenceled'}";
public static final String WARN_EXIST="{code:101,value:'file has exist'}";
public static final String WARN_NO_BUCKET="{code:102,value:'no bucket'}"; 
public static final String WARN_CENCEL="{code:110,value:'cenceled '}";
public static final String WARN_REFUSED_SEERVER="{code:112,value:'refused by server'}";
public static final String ERROR_FORMAT="{code:400,value:'request format error'}";
public static final String ERROE_SERVER="{code:500,value:'server inner fail'}";
public static final String UPLOAD_FINISHED = "{code:0,value:'success',needParts:[]}";
public static final String ERROR_CLIENT_NOREQUEST = "{code:404,value:'no upload request object mapped'}";
public static final String ERRIR_CLIENT_NOPART = "{code:403,value:'This part not be needed'";
public static final String REQUEST_SUCCESS = "{code:0,value:'success'}";


}
