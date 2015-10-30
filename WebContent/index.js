function uploadAndSubmit(){
	var form = document.forms["demoForm"];
	this.fileUpload;
	if (form["file"].files.length > 0)
	{
		var file3 = form["file"].files[0];
		document.getElementById("bytesTotal").innerHTML=file3.size;
		this.fileUpload=new fileUpload({
			url:"/fileUpload/upload",
			file:file3,
			sending:function(sendingStatue){
				document.getElementById("bytesRead").innerHTML=sendingStatue;
			},
			sendFinished : function(code,statue){
				console.log("code :"+code+"  statue : "+statue);
				if(code==0)
					console.log("transport Success");
			}
		});
	}
	this.cencel=this.fileUpload.cencel();
}