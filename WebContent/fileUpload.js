/*
 * 文件上传控件,请与相应的java类库搭配使用
 * 使用方法
 * new fileupload({
 * 		url :上传连接
 * 		file:fileObject,
 * 		sending:function(sendingstatues){},
 * 		sendFinished:function(code,statue){}
 * });
 * 		field statue 系统状态 0,1,2,3 初始化,发送上传请求,准备上传,上传完成
 * 			  size 文件大小
 * 		function	cencel()  stop fource the file transport if file upload finished return true
 * 		function	startSend() start file opoad process
 */
function fileUpload(jsonObject) {
	this._url = jsonObject.url;
	this._file = jsonObject.file;
	this._sending = jsonObject.sending;
	this._sendFinished = jsonObject.sendFinished;
	this._statue;
	this._notCencelFlag=true;
	this._onStatueChanged;
	this._urlState=(this._url.match("=") == true);
	this.startSend = function() {
		var xhr = new XMLHttpRequest();
		xhr.fileUpload = this;
			xhr.open("GET", this._url + (this._urlStatue?"&":"?")+"type=0&fileName=" + this._file.name
					+ "&fileSize=" + this._file.size);
		xhr.send();
		xhr.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var response = eval('(' + this.response + ')');
				xhr.fileUpload._responseSuccess(response);
			}
		};
	}
	this.cencel=function(){
		_notCencelFlag=false;
	}
	this._responseSuccess=function(response)
	{
		if (response != null && response.code == 0) {
			this.statue = 2;
			
			var parts=response.needParts;
			for ( var i in parts) {
				var part=parts[i];
				if(this._notCencelFlag)
				this._transportData(part);
			}
			this._sending(response.received);
			if(this._file.size==response.received)
				this._sendFinished(200,"success");
				} else {
			this._sendFinished(response.code, response.value);
		}
	}
	this._transportData=function(response)
	{
		var xhr = new XMLHttpRequest();
		xhr.fileUpload=this;
		xhr.open("POST", this._url + (this._urlStatue?"&":"?")+"type=1&fileName=" + this._file.name
				+ "&fileSize=" + this._file.size+"&startIndex="+response.startIndex+"&endIndex="+response.endIndex);
		var reader=new FileReader();
		reader.readAsArrayBuffer(this._file.slice(response.startIndex,response.endIndex));
		reader.onload=function(){
			xhr.send(reader.result);
			xhr.onreadystatechange = function() {
				if (this.readyState == 4 && this.status == 200) {
					var response =eval( '('+this.response+')');
					this.fileUpload._responseSuccess(response);
				}
			};
		}
	}
	this.startSend();
}