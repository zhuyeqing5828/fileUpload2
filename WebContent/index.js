var fileId;
var fileUploadTest=new fileUpload("testBucket",{
	onLoaded:function(fileId,file){
		fileId=fileId;
		console.log(fileId +"loaded "+file.name);
	},

	onSending:function(fileId,process){
		document.getElementById("bytesRead").innerHTML=process;
	},
	
	onFinished:function(fileId){
		alert(fieldId+"finished");
		console.log(fileId +"finished ");
	},
	
	onCenceled:function(fieId){
		alert(fieldId+"cencelled");
		console.log(fileId +"cenceled ");
	}
	
	
});

function uploadAndSubmit(){
	var form = document.forms["demoForm"];
	this.fileUpload;
	if (form["file"].files.length > 0)
	{
		var file3 = form["file"].files[0];
		document.getElementById("bytesTotal").innerHTML=file3.size;
		fileUploadTest.addUploadMission(file3);
	}
}