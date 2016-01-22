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
		$.ajax(_uploadUrl+"?fileName="+file.name,{
			cache:false,
			headers:{type:"New",bucketName:this._bucketName,fileLength:file.size},
			data:parameterData,
			type:"POST",
			upload:this,
			success:function(data){
				if(!data.code){
					this.upload._onLoaded(data.fileId,file);
					this.upload._onSending(data.fileId,data.received);
					var needParts=data.needParts;
					for ( var needPartSeq in needParts) {
						var needPart=needParts[needPartSeq];
						this.upload._startTransmit(data.id,file,needPart.partSeq,needPart.startIndex,needPart.length);
					}
					
					//md5 Code
					if(data.needMd5){
						var md5Value=MD5Check(file,function(md5Value){
							$.ajax(_uploadUrl,{
								cache:false,
								headers:{type:"md5Code",fileId:fileId,MD5:md5Value},
							});
						});
						
					}
				}
			},
			error:function(e){
				var testsdf=eval(e.responseText);
			}
		});
	};
	
	this._startTransmit=function(id,file,sequence,startIndex,fileLength){
		if(sequence==-1){
			this._onFinished(id);
		}
			var reader=new FileReader();
			reader.readAsArrayBuffer(file.slice(startIndex,startIndex+fileLength));
			reader.upload=this;
			reader.onload=function(){
				var result=reader.result;
			$.ajax(_uploadUrl+"?id="+id,{
				cache:false,
				headers:{type:"Transmit",bucketName:this._bucketName,sequence:sequence},
				type:"POST",
				contentType:"multipart/form-data",
				processData:false,
				data:result,
				upload:this,
				success:function(data){
					if(!data.code){
						this.upload.upload._onSending(data.fileId,data.received);
						var needParts=data.needParts;
						for ( var needPartSeq in needParts) {
							var needPart=needParts[needPartSeq];
							this.upload.upload._startTransmit(data.id,file,needPart.partSeq,needPart.startIndex,needPart.length);
						}
//					if(!data.code){
//						this.upload.upload._onSending(data.fileId,data.received);
//					if(data.needParts.length){
//						var needPart=data.needParts[0];
//						this.upload.upload._startTransmit(data.fileId,file,data.senquence,needPart.startIndex,needPart.length);
//						}
					}else{
						console.log("Fileupload: Transport fail "+data.code+" "+data.value);
					}
				}
			
			});
			}
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