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
function fileUpload (jsonObject){
	this.url=jsonObject.url;
	this.statue=0;
	this._startIndex;
	this.autoStart=jsonObject.autoStart;
	this.file=jsonObject.file;
	this.size=this.file.size;
	this.sending=new function(sendingstatues){};
	this.sendFinished=new function(code,statue){};
	var reader=new FileReader();
	reader.onprocess=this.sending;
	this.cencel=function(){
		if(reader.readyState==2)
			return false;
		reader.abort();
		return true;
	};
	this.startSend = function() {
		statue = 1;
		var fileSize = this.file.size;
		var fileName = this.file.name;
		var file=this.file;
		var url=this.url;
		var urlStatue;
		urlStatue=(url.match("=")==true);
		var xhr = new XMLHttpRequest();
		var sendFinished=this.sendFinished;
		xhr.file=this.file;

		if(urlStatue)
			xhr.open("GET",url+"&type=0&fileName="+fileName+"&fileSize="+fileSize);
		else
			xhr.open("GET",url+"?type=0&fileName="+fileName+"&fileSize="+fileSize);
		xhr.send();
		xhr.onreadystatechange = function() {
			if (this.readyState==4 && this.status==200) {
				var response =eval( '('+this.response+')');
				if (response != null && response.code == 0) {
					statue = 2;
					startIndex = response.startIndex;
					var transportFile = this.file.slice(startIndex);
					transportFile.onprogress=function(readed){
						sending(readed+startIndex);
					}
					reader.readAsArrayBuffer(transportFile);
					reader.onload=function(){
						var xhr = new XMLHttpRequest();
						xhr.sendFinished=this.sendFinished;
						if(urlStatue)
							xhr.open("POST",url+"&type=1&fileName="+fileName);
						else
							xhr.open("POST",url+"?type=1&fileName="+fileName);
						xhr.send(reader.result);
						xhr.onreadystatechange = function() {
							if (this.readyState == 4 && this.status == 200) {
								var response =eval( '('+this.response+')');
								if(response.code==0)
									statue = 3;
								sendFinished(response.code, response.statue);
							}
						};
					}
					
				} else {
					sendFinished(response.code, response.statue);
				}
			}
		};
	}
	this.file=jsonObject.file;
	this.url=jsonObject.url;
	if(jsonObject.sendFinished)
		this.sendFinished=jsonObject.sendFinished;
	if(jsonObject.sending)
		this.sending=jsonObject.sending;
	if(!this.autoStart)
		this.startSend();
}
