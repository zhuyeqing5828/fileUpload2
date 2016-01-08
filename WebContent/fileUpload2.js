/**
 * 文件上传组件的前端js
 */
var _uploadUrl="upload";
var _upload_void_function=function(){void(0);};
/*
 * 创建一个fileUpload对象,每一个对象对应一个bucket
 * 参数
 * 	bucketName	对应bucket的名称.
 * 	jsonObject	json对象(将相关操作的方法放到这里)
 * 		onLoaded	当文件上传请求被接收后	fileID(返回的唯一上传ID) file(file对象)
 * 		onProcess	当文件上传进度被更新后	fileID(唯一上传ID) 	received(已上传的字节数)
 * 		onFinished	当文件上传完成后		fileID(唯一上传ID)
 * 		onCencelled	当文件上传被终止后		fileID(唯一上传ID)
 * 	
 * 
 */
function fileUpload(bucketName,jsonObject){
	
	this._bucketName=bucketName;
	
	this.cencelUploadMission=function(fileId){
		$.ajax(_uploadUrl,{
			cache:false,
			headers:{type:"Cencel",fileId:fileId},
			success:this._onCenceled
		}); 
	};
	
	this.addUploadMission=function(file,parameterData){
		$.ajax(_uploadUrl,{
			cache:false,
			headers:{type:"New",bucketName:this._bucketName,fileName:file.name,length:file.length},
			data:parameterData,
			type:"POST",
			success:function(data){
				if(this._onLoaded){
					if(!data.code){
						this._onLoaded(data.fileId,file);
						this._onProcess(data.fileId,data.received);
						var needParts=data.needParts;
						for ( var needPartSeq in needParts) {
							var needPart=needParts[needPartSeq];
							this._startTransmit(data.fileId,file,data.senquence,needPart.startIndex,needPart.length);
						}
						
						//md5 Code
						if(data.needMd5){
							var md5Value=md5Check(file);
							$.ajax(_uploadUrl,{
								cache:false,
								headers:{type:"md5Code",fileId:fileId,MD5:md5Value},
							});
						}
					}
				}
			}
		});
		
		
		return fileId;
	
	};
	
	this._startTransmit=function(id,file,sequence,startIndex,length){
		if(!data.code){
			var reader=new FileReader();
			reader.readAsArrayBuffer(this._file.slice(startIndex,startIndex+length));
			reader.onload=function(){
				var result=reader.result;
			}
		}
		$.ajax(_uploadUrl,{
			cache:false,
			headers:{type:"Transmit",bucketName:this._bucketName,id:id,sequence:sequence},
			type:"POST",
			processData:false,
			data:result,
			success:function(data){
				if(!data.code){
				this._onProcess(data.fileId,data.received);
				if(!data.needParts.length){
					var needPart=data.needParts[0];
					this._startTransmit(data.fileId,file,data.senquence,needPart.startIndex,needPart.length);
					}
				}else{
					console.log("Fileupload: Transport fail "+data.code+" "+data.value);
				}
			}
		
	});
		};
	
	
	this._onLoaded=function(){return true};
	if(jsonObject.onLoaded)
	this._onLoaded=jsonObject.onLoaded;
	this._onFinished=_upload_void_function;
	if(jsonObject.onFinished)
	this._onFinished=jsonObject.onFinished;
	this._onSending=_upload_void_function;	
	if(jsonObject.onSending)
	this._onSending=jsonObject.onSending;
	this._onCenceled=_upload_void_function;
	if(jsonObject.onCenceled)
	this._onCenceled=jsonObject.onCenceled;
	
	return this;
}