function uploadAndSubmit(){
	var form = document.forms["demoForm"];
	
	if (form["file"].files.length > 0)
	{
		var file3 = form["file"].files[0];
		document.getElementById("bytesTotal").innerHTML=file3.size;
		new fileUpload({
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
		
}